package org.carpet_org_addition.util.task.playerscheduletask;

import carpet.patches.EntityPlayerMPFake;
import carpet.patches.FakeClientConnection;
import carpet.utils.Messenger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.command.PlayerManagerCommand;
import org.carpet_org_addition.exception.TaskExecutionException;
import org.carpet_org_addition.mixin.rule.EntityAccessor;
import org.carpet_org_addition.mixin.rule.PlayerEntityAccessor;
import org.carpet_org_addition.util.GameUtils;
import org.carpet_org_addition.util.MessageUtils;

import java.util.Objects;

public class ReLoginTask extends PlayerScheduleTask {
    // 假玩家名
    private final String playerName;
    // 重新上线的时间间隔
    private int interval;
    // 距离下一次重新上线所需的时间
    private int remainingTick;
    private final MinecraftServer server;
    private final RegistryKey<World> dimensionId;
    private final CommandContext<ServerCommandSource> context;
    // 当前任务是否已经结束
    private boolean stop = false;
    // 假玩家重新上线的倒计时
    private int canSpawn = 2;

    public ReLoginTask(String playerName, int interval, MinecraftServer server, RegistryKey<World> dimensionId, CommandContext<ServerCommandSource> context) {
        this.playerName = playerName;
        this.interval = interval;
        this.remainingTick = this.interval;
        this.server = server;
        this.dimensionId = dimensionId;
        this.context = context;
    }

    @Override
    public void tick() {
        if (PlayerManagerCommand.fixShouldBeEnabled()) {
            Runnable function = () -> {
                MessageUtils.sendCommandErrorFeedback(context, "carpet.commands.playerManager.schedule.relogin.rule.disable");
                // 如果假玩家已经下线，重新生成假玩家
                ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.playerName);
                if (player == null) {
                    homePositionSpawn(this.playerName, this.server, this.dimensionId);
                }
            };
            throw new TaskExecutionException(function);
        }
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.playerName);
        if (player == null) {
            if (this.canSpawn == 0) {
                homePositionSpawn(this.playerName, this.server, this.dimensionId);
                this.canSpawn = 2;
            } else {
                this.canSpawn--;
            }
        } else if (this.remainingTick <= 0) {
            this.remainingTick = this.interval;
            if (player instanceof EntityPlayerMPFake fakePlayer) {
                // 如果假玩家坠入虚空，设置任务为停止
                if (fakePlayer.getY() < fakePlayer.getServerWorld().getBottomY() - 64) {
                    this.stop();
                }
                // 让假玩家退出游戏
                this.logoutPlayer(fakePlayer);
            }
        } else {
            this.remainingTick--;
        }
    }


    /**
     * 让假玩家退出游戏
     *
     * @see EntityPlayerMPFake#kill(Text)
     * @see EntityPlayerMPFake#shakeOff()
     */
    @SuppressWarnings("JavadocReference")
    private void logoutPlayer(EntityPlayerMPFake fakePlayer) {
        // TODO 日志输出： <玩家名> lost connection: Killed
        Text reason = Messenger.s("Killed");
        // 停止骑行
        if (fakePlayer.getVehicle() instanceof PlayerEntity) {
            fakePlayer.stopRiding();
        }
        for (Entity passenger : fakePlayer.getPassengersDeep()) {
            if (passenger instanceof PlayerEntity) {
                passenger.stopRiding();
            }
        }
        // 退出游戏
        TextContent var3 = reason.getContent();
        if (var3 instanceof TranslatableTextContent text) {
            if (text.getKey().equals("multiplayer.disconnect.duplicate_login")) {
                try {
                    CarpetOrgAddition.hiddenLoginMessages = true;
                    fakePlayer.networkHandler.onDisconnected(new DisconnectionInfo(reason));
                } finally {
                    CarpetOrgAddition.hiddenLoginMessages = false;
                }
                return;
            }
        }
        this.server.send(new ServerTask(this.server.getTicks(), () -> {
            try {
                CarpetOrgAddition.hiddenLoginMessages = true;
                fakePlayer.networkHandler.onDisconnected(new DisconnectionInfo(reason));
            } finally {
                CarpetOrgAddition.hiddenLoginMessages = false;
            }
        }));
    }

    @Override
    public boolean stopped() {
        return this.stop;
    }

    @Override
    public String getLogName() {
        return this.playerName + "周期性重新上线";
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void onCancel(CommandContext<ServerCommandSource> context) {
        MessageUtils.sendCommandFeedback(context, "carpet.commands.playerManager.schedule.relogin.cancel", this.playerName);
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.playerName);
        if (player == null) {
            homePositionSpawn(this.playerName, this.server, this.dimensionId);
        }
    }

    @Override
    public void sendEachMessage(ServerCommandSource source) {
        MessageUtils.sendCommandFeedback(source, "carpet.commands.playerManager.schedule.relogin", this.playerName, this.interval);
    }

    public void setInterval(int interval) {
        this.interval = interval;
        this.remainingTick = interval;
    }

    public void stop() {
        this.stop = true;
    }

    /**
     * 在假玩家上一次退出游戏的位置生成假玩家
     *
     * @param username    假玩家名
     * @param dimensionId 假玩家要生成的维度
     */
    private void homePositionSpawn(String username, MinecraftServer server, RegistryKey<World> dimensionId) {
        ServerWorld worldIn = server.getWorld(dimensionId);
        if (worldIn == null) {
            return;
        }
        UserCache.setUseRemote(false);
        GameProfile gameprofile;
        try {
            UserCache userCache = server.getUserCache();
            if (userCache == null) {
                return;
            }
            gameprofile = userCache.findByName(username).orElse(null);
        } finally {
            UserCache.setUseRemote(server.isDedicated() && server.isOnlineMode());
        }
        if (gameprofile == null) {
            gameprofile = new GameProfile(Uuids.getOfflinePlayerUuid(username), username);
        }
        EntityPlayerMPFake fakePlayer = EntityPlayerMPFake.respawnFake(server, worldIn, gameprofile, SyncedClientOptions.createDefault());
        fakePlayer.fixStartingPosition = GameUtils::pass;
        try {
            CarpetOrgAddition.hiddenLoginMessages = true;
            server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), fakePlayer, new ConnectedClientData(gameprofile, 0, fakePlayer.getClientOptions(), false));
        } catch (NullPointerException e) {
            CarpetOrgAddition.LOGGER.warn("{}在服务器关闭时尝试上线", this.playerName, e);
            this.stop();
            return;
        } finally {
            // 假玩家加入游戏后，这个变量必须重写设置为false，防止影响其它广播消息的方法
            CarpetOrgAddition.hiddenLoginMessages = false;
        }
        fakePlayer.setHealth(20.0F);
        ((EntityAccessor) fakePlayer).cancelRemoved();
        Objects.requireNonNull(fakePlayer.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT)).setBaseValue(0.6F);
        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(fakePlayer, (byte) ((int) (fakePlayer.headYaw * 256.0F / 360.0F))), dimensionId);
        server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(fakePlayer), dimensionId);
        fakePlayer.getDataTracker().set(PlayerEntityAccessor.getPlayerModelParts(), (byte) 127);
    }
}

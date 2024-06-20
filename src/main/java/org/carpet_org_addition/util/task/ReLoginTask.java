package org.carpet_org_addition.util.task;

import carpet.patches.EntityPlayerMPFake;
import carpet.patches.FakeClientConnection;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import org.carpet_org_addition.mixin.rule.EntityAccessor;
import org.carpet_org_addition.mixin.rule.PlayerEntityAccessor;
import org.carpet_org_addition.util.GameUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

public class ReLoginTask extends ServerTask {
    private final String name;
    private int interval;
    private int remainingTick;
    private final MinecraftServer server;
    private final RegistryKey<World> dimensionId;
    private boolean stop = false;

    public ReLoginTask(String name, int interval, MinecraftServer server, RegistryKey<World> dimensionId) {
        this.name = name;
        this.interval = interval;
        this.remainingTick = this.interval;
        this.server = server;
        this.dimensionId = dimensionId;
    }

    // 补全玩家名称
    public static @NotNull SuggestionProvider<ServerCommandSource> suggests() {
        return (context, builder) -> {
            MinecraftServer server = context.getSource().getServer();
            ServerTaskManagerInterface instance = ServerTaskManagerInterface.getInstance(server);
            List<String> taskList = instance.getTaskList().stream()
                    .filter(task -> task instanceof ReLoginTask)
                    .map(task -> ((ReLoginTask) task).getName()).toList();
            List<String> onlineList = server.getPlayerManager().getPlayerList().stream()
                    .map(player -> player.getName().getString()).toList();
            HashSet<String> players = new HashSet<>();
            players.addAll(taskList);
            players.addAll(onlineList);
            return CommandSource.suggestMatching(players.stream(), builder);
        };
    }

    @Override
    public void tick() {
        if (this.remainingTick <= 0) {
            this.remainingTick = this.interval;
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(this.name);
            if (player == null) {
                homePositionSpawn(this.name, this.server, this.dimensionId);
            } else if (player instanceof EntityPlayerMPFake fakePlayer) {
                // 如果假玩家坠入虚空，设置任务为停止
                if (fakePlayer.getY() < fakePlayer.getServerWorld().getBottomY() - 64) {
                    this.stop();
                }
                fakePlayer.kill();
            }
        } else {
            this.remainingTick--;
        }
    }

    @Override
    public boolean isEndOfExecution() {
        return this.stop;
    }

    public String getName() {
        return name;
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
        EntityPlayerMPFake fakePlayer = EntityPlayerMPFake.respawnFake(server, worldIn, gameprofile);
        fakePlayer.fixStartingPosition = GameUtils::pass;
        server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), fakePlayer);
        fakePlayer.setHealth(20.0F);
        ((EntityAccessor) fakePlayer).cancelRemoved();
        fakePlayer.setStepHeight(0.6F);
        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(fakePlayer, (byte) ((int) (fakePlayer.headYaw * 256.0F / 360.0F))), dimensionId);
        server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(fakePlayer), dimensionId);
        fakePlayer.getDataTracker().set(PlayerEntityAccessor.getPlayerModelParts(), (byte) 127);
    }
}

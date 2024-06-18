package org.carpet_org_addition.util;

import carpet.patches.EntityPlayerMPFake;
import carpet.patches.FakeClientConnection;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.carpet_org_addition.mixin.rule.EntityAccessor;
import org.carpet_org_addition.mixin.rule.PlayerEntityAccessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GameUtils {
    private GameUtils() {
    }

    /**
     * 获取一名玩家的字符串形式的玩家名
     *
     * @param player 要获取字符串形式玩家名的玩家
     * @return 玩家名的字符串形式
     */
    public static String getPlayerName(PlayerEntity player) {
        return player.getName().getString();
    }

    /**
     * 获取当前系统时间的字符串形式
     *
     * @return 当前系统时间的字符串形式
     */
    @Deprecated
    public static String getDateString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        return formatter.format(localDateTime);
    }

    @SuppressWarnings("DataFlowIssue")
    public static EntityPlayerMPFake createFakePlayer(String username, MinecraftServer server, Vec3d pos, double yaw, double pitch, RegistryKey<World> dimensionId, GameMode gamemode, boolean flying) {
        ServerWorld worldIn = server.getWorld(dimensionId);
        UserCache.setUseRemote(false);
        GameProfile gameprofile;
        try {
            gameprofile = server.getUserCache().findByName(username).orElse(null);
        } finally {
            UserCache.setUseRemote(server.isDedicated() && server.isOnlineMode());
        }
        if (gameprofile == null) {
            gameprofile = new GameProfile(Uuids.getOfflinePlayerUuid(username), username);
        }
        return trySpawn(server, pos, (float) yaw, (float) pitch, dimensionId, gamemode, flying, gameprofile, worldIn);
    }

    private static EntityPlayerMPFake trySpawn(MinecraftServer server, Vec3d pos, float yaw, float pitch, RegistryKey<World> dimensionId, GameMode gamemode, boolean flying, GameProfile gameprofile, ServerWorld worldIn) {
        // 生成假玩家的逻辑似乎不在主线程
        EntityPlayerMPFake instance = EntityPlayerMPFake.respawnFake(server, worldIn, gameprofile, SyncedClientOptions.createDefault());
        instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
        server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), instance, new ConnectedClientData(gameprofile, 0, instance.getClientOptions(), false));
        instance.teleport(worldIn, pos.x, pos.y, pos.z, yaw, pitch);
        instance.setHealth(20.0F);
        ((EntityAccessor) instance).cancelRemoved();
        Objects.requireNonNull(instance.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT)).setBaseValue(0.6F);
        instance.interactionManager.changeGameMode(gamemode);
        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(instance, (byte) ((int) (instance.headYaw * 256.0F / 360.0F))), dimensionId);
        server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(instance), dimensionId);
        instance.getDataTracker().set(PlayerEntityAccessor.getPlayerModelParts(), (byte) 127);
        instance.getAbilities().flying = flying;
        return instance;
    }

    /**
     * 一个占位符，什么也不做
     */
    public static void pass() {
    }
}

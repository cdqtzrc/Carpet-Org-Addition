package org.carpet_org_addition.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.carpet_org_addition.exception.NotFoundPlayerException;

public class GameUtils {
    private GameUtils() {
    }

    /**
     * 从服务器中寻找一个指定名称的玩家<br/>在多线程环境下，这个方法可能无法找到刚刚进入游戏的玩家，例如{@link carpet.patches.EntityPlayerMPFake#createFake(String, MinecraftServer, Vec3d, double, double, RegistryKey, GameMode, boolean)}假玩家类中的创造假玩家的方法，这个创造假玩家的方法中的添加玩家到玩家列表的语句和当前工具类中获取玩家对象的方法不一定在同一线程，如果生成假玩家后立即调用此方法查找指定玩家，则可能调用时刚刚生成的假玩家还没来得及添加到玩家列表，然后就找不到指定玩家，并抛出{@link NotFoundPlayerException}异常
     *
     * @param server     游戏当前的服务器对象
     * @param playerName 要查找的玩家名
     * @return 指定名称的玩家
     * @throws NotFoundPlayerException 找不到指定玩家
     */
    public static ServerPlayerEntity getPlayer(MinecraftServer server, String playerName) throws NotFoundPlayerException {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            throw new NotFoundPlayerException();
        }
        return player;
    }
}

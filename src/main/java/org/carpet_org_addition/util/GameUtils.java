package org.carpet_org_addition.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.exception.NotFoundPlayerException;

public class GameUtils {
    private GameUtils() {
    }

    /**
     * 从服务器中寻找一个指定名称的玩家
     *
     * @param server     游戏当前的服务器对象
     * @param playerName 要查找的玩家名
     * @return 指定名称的玩家
     * @throws NotFoundPlayerException 找不到指定玩家
     */
    public static PlayerEntity getPlayer(MinecraftServer server, String playerName) throws NotFoundPlayerException {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            throw new NotFoundPlayerException();
        }
        return player;
    }
}

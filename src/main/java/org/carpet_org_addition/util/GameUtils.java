package org.carpet_org_addition.util;

import net.minecraft.entity.player.PlayerEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}

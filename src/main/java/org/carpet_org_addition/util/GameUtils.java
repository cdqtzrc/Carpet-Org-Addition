package org.carpet_org_addition.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;

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

    /**
     * 将游戏刻时间转换为几分几秒的形式，如果时间非常接近整点，例如一小时零一秒，则会直接返回一小时，多出来的一秒会被忽略
     *
     * @param tick 游戏刻时间
     */
    public static MutableText tickToTime(long tick) {
        // 游戏刻
        if (tick < 20L) {
            return TextUtils.translate("carpet.command.time.tick", tick);
        }
        // 秒
        if (tick < 1200L) {
            return TextUtils.translate("carpet.command.time.second", tick / 20L);
        }
        // 整分
        if (tick < 72000L && (tick % 1200L == 0 || (tick / 20L) % 60L == 0)) {
            return TextUtils.translate("carpet.command.time.minute", tick / 1200L);
        }
        // 分和秒
        if (tick < 72000L) {
            return TextUtils.translate("carpet.command.time.minute_second", tick / 1200L, (tick / 20L) % 60L);
        }
        // 整小时
        if (tick % 72000L == 0 || (tick / 20L / 60L) % 60L == 0) {
            return TextUtils.translate("carpet.command.time.hour", tick / 72000L);
        }
        // 小时和分钟
        return TextUtils.translate("carpet.command.time.hour_minute", tick / 72000L, (tick / 20L / 60L) % 60L);
    }

    /**
     * 将当前系统时间偏移指定游戏刻数后返回时间的年月日时分秒形式
     *
     * @param offsetTick 时间偏移的游戏刻数
     */
    public static MutableText tickToRealTime(long offsetTick) {
        LocalDateTime time = LocalDateTime.now().plusSeconds(offsetTick / 20);
        return TextUtils.translate("carpet.command.time.format",
                time.getYear(), time.getMonth().ordinal() + 1, time.getDayOfMonth(),
                time.getHour(), time.getMinute(), time.getSecond());
    }

    /**
     * 一个占位符，什么也不做
     */
    public static void pass() {
    }
}

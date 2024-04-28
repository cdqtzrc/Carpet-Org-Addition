package org.carpet_org_addition.logger;

import carpet.logging.HUDLogger;
import carpet.logging.LoggerRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.TextUtils;

import java.lang.reflect.Field;

/**
 * 流浪商人生成记录器
 */
public class WanderingTraderSpawnLogger {
    public static final String LOGGER_NAME = "wanderingTrader";
    public static boolean wanderingTrader = false;
    private static SpawnCountdown spawnCountdown;

    // 注册流浪商人记录器
    public static void registerLoggers() {
        try {
            LoggerRegistry.registerLogger(LOGGER_NAME, createLogger());
        } catch (NoSuchFieldException e) {
            CarpetOrgAddition.LOGGER.error("记录器“" + LOGGER_NAME + "”未能成功注册", e);
        }
    }

    // 创建一个流浪商人记录器
    private static HUDLogger createLogger() throws NoSuchFieldException {
        return new HUDLogger(getField(), LOGGER_NAME, null, null, false);
    }

    // 反射获取wanderingTrader字段
    private static Field getField() throws NoSuchFieldException {
        return WanderingTraderSpawnLogger.class.getField(LOGGER_NAME);
    }

    // 更新HUD
    public static void updateHud(MinecraftServer server) {
        if (server.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            if (wanderingTrader && spawnCountdown != null) {
                // 计算流浪商人生成概率的百分比
                double chance = spawnCountdown.spawnChance / 10.0;
                Text time = spawnCountdown.countdown <= 60
                        ? TextUtils.getTranslate("carpet.logger.wanderingTrader.time.second", spawnCountdown.countdown)
                        : TextUtils.getTranslate("carpet.logger.wanderingTrader.time.minutes_and_seconds",
                        spawnCountdown.countdown / 60, spawnCountdown.countdown % 60);
                LoggerRegistry.getLogger(LOGGER_NAME).log((s, playerEntity) -> new Text[]{
                        TextUtils.getTranslate("carpet.logger.wanderingTrader.hud", time, (String.format("%.1f", chance) + "%"))
                });
            }
        } else {
            LoggerRegistry.getLogger(LOGGER_NAME).log((s, playerEntity)
                    -> new Text[]{TextUtils.getTranslate("carpet.logger.wanderingTrader.gamerule.not_enabled",
                    TextUtils.getTranslate(GameRules.DO_TRADER_SPAWNING.getTranslationKey()))});
        }
    }

    // 当前生成倒计时是否为null
    public static boolean spawnCountdownNonNull() {
        return spawnCountdown != null;
    }

    public static void setSpawnCountdown(SpawnCountdown spawnCountdown) {
        WanderingTraderSpawnLogger.spawnCountdown = spawnCountdown;
    }

    public static class SpawnCountdown {
        // 距离下一次流浪商人生成剩下的时间
        private final int countdown;
        // 流浪商人生成的概率
        private final int spawnChance;

        public SpawnCountdown(int countdown, int spawnChance) {
            this.countdown = countdown;
            this.spawnChance = spawnChance;
        }
    }
}

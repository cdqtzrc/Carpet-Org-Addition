package org.carpet_org_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.translate.Translate;
import org.carpet_org_addition.util.helpers.Waypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CarpetOrgAddition implements ModInitializer, CarpetExtension {
    static {
        //carpet org扩展
        CarpetServer.manageExtension(new CarpetOrgAddition());
    }

    //日志
    public static final Logger LOGGER = LoggerFactory.getLogger("CarpetOrgAddition");
    public static final String MOD_NAME_LOWER_CASE = "carpetorgaddition";

    /**
     * Runs the mod initializer.
     */
    //模组初始化
    @Override
    public void onInitialize() {
    }

    //在游戏开始时
    @Override
    public void onGameStarted() {
        // 解析Carpet设置
        CarpetServer.settingsManager.parseSettingsClass(CarpetOrgAdditionSettings.class);
    }

    // 当玩家登录时
    @Override
    public void onPlayerLoggedIn(ServerPlayerEntity player) {
        CarpetExtension.super.onPlayerLoggedIn(player);
        // 假玩家生成时不保留上一次的击退，着火时间，摔落高度
        if (CarpetOrgAdditionSettings.fakePlayerSpawnNoKnockback && player instanceof EntityPlayerMPFake fakePlayer) {
            // 清除速度
            fakePlayer.setVelocity(0, 0, 0);
            // 清除着火时间
            fakePlayer.setFireTicks(0);
            // 清除摔落高度
            fakePlayer.fallDistance = 0;
        }
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        CarpetExtension.super.onServerLoaded(server);
        Waypoint.replaceWaypoint(server);
    }

    //设置可以有翻译
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translate.getTranslate();
    }
}

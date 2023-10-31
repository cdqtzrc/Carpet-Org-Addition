package org.carpet_org_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.carpet.tools.text.Translate;
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
        CarpetServer.settingsManager.parseSettingsClass(CarpetOrgAdditionSettings.class);
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayerEntity player) {
        CarpetExtension.super.onPlayerLoggedIn(player);
        //假玩家生存时不保留上一次的击退
        if (CarpetOrgAdditionSettings.fakePlayerSpawnNotRetainKnockback && player instanceof EntityPlayerMPFake fakePlayer) {
            fakePlayer.setVelocity(0, 0, 0);
            fakePlayer.setFireTicks(0);
            fakePlayer.fallDistance = 0;
        }
    }

    //设置可以有翻译
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translate.getTranslate();
    }
}

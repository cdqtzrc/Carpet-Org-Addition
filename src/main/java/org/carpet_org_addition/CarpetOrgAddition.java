package org.carpet_org_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.command.PlayerManagerCommand;
import org.carpet_org_addition.command.RegisterCarpetCommands;
import org.carpet_org_addition.logger.WanderingTraderSpawnLogger;
import org.carpet_org_addition.translate.Translate;
import org.carpet_org_addition.util.express.ExpressManager;
import org.carpet_org_addition.util.express.ExpressManagerInterface;
import org.carpet_org_addition.util.wheel.Waypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CarpetOrgAddition implements ModInitializer, CarpetExtension {
    /**
     * 控制玩家登录登出的消息是否显示
     */
    public static boolean hiddenLoginMessages = false;
    // 日志
    public static final Logger LOGGER = LoggerFactory.getLogger("CarpetOrgAddition");
    public static final String MOD_NAME_LOWER_CASE = "carpetorgaddition";

    // TODO 更新日志类加载警告问题版本归属

    /**
     * 模组初始化
     */
    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new CarpetOrgAddition());
    }

    // 在游戏开始时
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
        if (CarpetOrgAdditionSettings.fakePlayerSpawnNoKnockback && player instanceof EntityPlayerMPFake) {
            // 清除速度
            player.setVelocity(Vec3d.ZERO);
            // 清除着火时间
            player.setFireTicks(0);
            // 清除摔落高度
            player.fallDistance = 0;
            // 清除负面效果
            player.getStatusEffects().removeIf(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL);
        }
        // 提示玩家接收快递
        ExpressManager expressManager = ExpressManagerInterface.getInstance(player.server);
        expressManager.promptToReceive(player);
        PlayerManagerCommand.loadSeafAfk(player);
    }

    // 服务器启动时调用
    @Override
    public void onServerLoaded(MinecraftServer server) {
        CarpetExtension.super.onServerLoaded(server);
        // 服务器启动时自动将旧的路径点替换成新的
        Waypoint.replaceWaypoint(server);
    }

    // 设置模组翻译
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translate.getTranslate();
    }

    // 注册记录器
    @Override
    public void registerLoggers() {
        CarpetExtension.super.registerLoggers();
        WanderingTraderSpawnLogger.registerLoggers();
    }

    // 注册命令
    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        RegisterCarpetCommands.registerCarpetCommands(dispatcher, commandBuildContext);
    }
}

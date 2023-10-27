package org.carpet_org_addition.command;

import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RegisterCarpetCommands {
    //注册Carpet命令
    public static void registerCarpetCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandBuildContext) {
        if (CarpetServer.settingsManager != null) {
            CarpetServer.settingsManager.registerCommand(dispatcher, commandBuildContext);
            CarpetServer.extensions.forEach((e) -> {
                carpet.api.settings.SettingsManager sm = e.extensionSettingsManager();
                if (sm != null) {
                    sm.registerCommand(dispatcher, commandBuildContext);
                }
            });
            //注册命令

            //物品分身命令
            ItemShadowingCommand.register(dispatcher);

            //保护假玩家命令
            ProtectCommand.register(dispatcher);

            //假玩家工具命令
            PlayerToolsCommand.register(dispatcher, commandBuildContext);

            //发送消息命令
            SendMessageCommand.register(dispatcher);

            //苦力怕音效命令
            //CreeperCommand.register(dispatcher);

            //经验转移命令
            XpTransferCommand.register(dispatcher);

            //生存旁观切换命令
            SpectatorCommand.register(dispatcher);

            //方块查找器命令
            BlockFinderCommand.register(dispatcher, commandBuildContext);

            //自杀命令
            KillMeCommand.register(dispatcher);

            //路径点管理器命令
            LocationsCommand.register(dispatcher);

            /*
              测试用命令
             */
            //CarpetOrgAdditionTestCommand.register(dispatcher);

            CarpetServer.extensions.forEach((e) -> e.registerCommands(dispatcher, commandBuildContext));
        }
    }
}

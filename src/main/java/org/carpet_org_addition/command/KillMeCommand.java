package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;

//自杀命令
public class KillMeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("killMe")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandKillMe))
                .executes(KillMeCommand::killMe));
    }

    //玩家自杀
    private static int killMe(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        //广播自杀消息
        MessageUtils.broadcastTextMessage(context.getSource(), TextUtils.translate("carpet.commands.killMe", player.getDisplayName()));
        player.kill();
        return 1;
    }
}

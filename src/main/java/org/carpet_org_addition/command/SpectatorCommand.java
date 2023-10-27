package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;

//在生存模式和旁观模式间切换
public class SpectatorCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spectator").requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandSpectator))
                .executes(SpectatorCommand::setGameMode));
    }

    //更改游戏模式
    private static int setGameMode(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        GameMode gameMode = player.isSpectator() ? GameMode.SURVIVAL : GameMode.SPECTATOR;
        player.changeGameMode(gameMode);
        //发送命令反馈
        MutableText text = Text.translatable("gameMode." + gameMode.getName());
        player.sendMessage(Text.translatable("commands.gamemode.success.self", text), true);
        return gameMode == GameMode.SURVIVAL ? 1 : 0;
    }
}

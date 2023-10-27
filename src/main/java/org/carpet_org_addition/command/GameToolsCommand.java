package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.carpet_org_addition.CarpetOrgAdditionSettings;

@Deprecated
public class GameToolsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("gametools").requires(source -> CommandHelper.canUseCommand(source, false)).then(CommandManager.literal("_kill").executes(context -> {
                    //杀死玩家
                    killPlayer(context);
                    return 1;
                })).then(CommandManager.literal("seed").executes(context -> {
                    //获取世界种子
                    seed(context);
                    return 1;
                }))
        );
    }

    //杀死玩家
    private static void killPlayer(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = getPlayer(context);
        if (player != null) {
            if (player.isCreative() && CarpetOrgAdditionSettings.creativeImmuneKill) {
                player.damage(player.getDamageSources().genericKill(), Float.MAX_VALUE);
                return;
            }
            player.kill();
        }
    }

    //获取世界种子，来自原版/seed命令
    private static void seed(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = getPlayer(context);
        if (player == null) {
            return;
        }
        long l = context.getSource().getWorld().getSeed();
        Text text = Texts.bracketedCopyable(String.valueOf(l));
        context.getSource().sendFeedback(() ->
                Text.translatable("commands.seed.success", text), false);
    }

    //获取玩家
    private static PlayerEntity getPlayer(CommandContext<ServerCommandSource> context) {
        return context.getSource().getPlayer();
    }
}

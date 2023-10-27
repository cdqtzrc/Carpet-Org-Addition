package org.carpet_org_addition.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 测试用，不会添加到游戏
 */
public class CarpetOrgAdditionTestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("carpetOrgAdditionTest").requires(source -> false)
                .then(CommandManager.literal("listEnchantBookFactory").executes(context -> {
                    listEnchantBookFactory(context.getSource());
                    return 1;
                }))
        );
    }

    //列出图书管理员所有可交易的附魔书
    private static void listEnchantBookFactory(ServerCommandSource source) {
        List<Enchantment> list = Registries.ENCHANTMENT.stream().filter(Enchantment::isAvailableForEnchantedBookOffer).toList();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            player.sendMessage(Text.of("图书管理员所有可交易的附魔书:"));
            for (Enchantment enchantment : list) {
                player.sendMessage(enchantment.getName(enchantment.getMaxLevel()));
            }
        }
    }
}

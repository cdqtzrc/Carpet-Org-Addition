package org.carpet_org_addition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.SendMessageUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * 测试用，不会添加到游戏
 */
public class CarpetOrgAdditionTestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("carpetOrgAdditionTest").requires(source -> false)
                .then(CommandManager.literal("listEnchantBookFactory").executes(context -> {
                    listEnchantBookFactory(context.getSource());
                    return 1;
                })).then(CommandManager.literal("getIndex")
                        .then(CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                .executes(CarpetOrgAdditionTestCommand::getItemIndex)))
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

    private static int getItemIndex(CommandContext<ServerCommandSource> context) {
        Predicate<ItemStack> item = ItemPredicateArgumentType.getItemStackPredicate(context, "item");
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return -1;
        }
        DefaultedList<ItemStack> main = player.getInventory().main;
        for (int index = 0; index < main.size(); index++) {
            if (item.test(main.get(index))) {
                SendMessageUtils.sendTextMessage(player, Text.literal(main.get(index).getName().getString() + "在索引" + index));
                return index;
            }
        }
        return -1;
    }
}

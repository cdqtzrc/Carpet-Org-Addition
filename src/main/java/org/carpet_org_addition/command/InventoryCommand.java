package org.carpet_org_addition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.InventoryUtils;
import org.carpet_org_addition.util.fakeplayer.PlayerEnderChestScreenHandler;
import org.carpet_org_addition.util.fakeplayer.PlayerInventoryScreenHandler;
import org.carpet_org_addition.util.wheel.ShulkerBoxInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class InventoryCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("inventory")
                .requires(source -> FabricLoader.getInstance().isDevelopmentEnvironment())
                .then(CommandManager.literal("open")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .executes(InventoryCommand::openInventory)
                                .then(CommandManager.literal("inventory")
                                        .executes(InventoryCommand::openInventory))
                                .then(CommandManager.literal("enderChest")
                                        .executes(InventoryCommand::openEnderChest))))
                .then(CommandManager.literal("sort")
                        .then(CommandManager.argument(CommandUtils.PLAYER, EntityArgumentType.player())
                                .then(CommandManager.argument("inventory", StringArgumentType.string())
                                        .suggests(suggests())
                                        .executes(context -> sortInventory(context, false))
                                        .then(CommandManager.argument("includeShulkerBox", BoolArgumentType.bool())
                                                .executes(context -> sortInventory(context, BoolArgumentType.getBool(context, "includeShulkerBox"))))))));
    }

    @NotNull
    private static SuggestionProvider<ServerCommandSource> suggests() {
        return (context, builder) -> CommandSource.suggestMatching(new String[]{"inventory", "enderChest"}, builder);
    }

    // 整理玩家物品栏
    private static int sortInventory(CommandContext<ServerCommandSource> context, boolean includeShulkerBox) throws CommandSyntaxException {
        InventoryType inventoryType = InventoryCommand.getInventoryType(context);
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        // 获取物品栏的集合
        Pair<List<ItemStack>, Runnable> pair = switch (inventoryType) {
            case INVENTORY -> {
                DefaultedList<ItemStack> main = targetPlayer.getInventory().main;
                List<ItemStack> list = main.subList(9, main.size());
                yield new Pair<>(list, () -> {
                });
            }
            case ENDER_CHEST -> {
                final List<ItemStack> list = InventoryUtils.toList(targetPlayer.getEnderChestInventory());
                yield new Pair<>(list, () -> {
                    for (int index = 0; index < list.size(); index++) {
                        targetPlayer.getEnderChestInventory().setStack(index, list.get(index));
                    }
                });
            }
        };
        // 整理潜影盒内部物品
        if (includeShulkerBox) {
            // 整理潜影盒中的物品
            ShulkerBoxInventory inventory = new ShulkerBoxInventory(pair.getLeft());
            inventory.sort();
            inventory.removeInventoryNbt();
            inventory.application();
        }
        // 整理物品栏
        InventoryUtils.sortInventory(pair.getLeft());
        pair.getRight().run();
        return 0;
    }

    // 打开玩家物品栏
    private static int openInventory(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sourcePlayer = CommandUtils.getSourcePlayer(context);
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player)
                        -> new PlayerInventoryScreenHandler(syncId, playerInventory, targetPlayer), targetPlayer.getName());
        sourcePlayer.openHandledScreen(screen);
        return 1;
    }

    // 打开玩家末影箱
    private static int openEnderChest(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sourcePlayer = CommandUtils.getSourcePlayer(context);
        ServerPlayerEntity targetPlayer = CommandUtils.getArgumentPlayer(context);
        SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player)
                        -> new PlayerEnderChestScreenHandler(syncId, playerInventory, targetPlayer), targetPlayer.getName());
        sourcePlayer.openHandledScreen(screen);
        return 0;
    }

    private static InventoryType getInventoryType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return InventoryType.of(StringArgumentType.getString(context, "inventory"));
    }

    private enum InventoryType {
        INVENTORY,
        ENDER_CHEST;

        private static InventoryType of(String inventory) throws CommandSyntaxException {
            return switch (inventory) {
                case "inventory" -> INVENTORY;
                case "enderChest" -> ENDER_CHEST;
                default -> throw CommandUtils.createException("command.unknown.argument");
            };
        }
    }
}

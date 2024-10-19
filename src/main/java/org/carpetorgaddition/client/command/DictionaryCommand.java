package org.carpetorgaddition.client.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.carpetorgaddition.client.command.argument.ClientObjectArgumentType;
import org.carpetorgaddition.client.util.ClientMessageUtils;
import org.carpetorgaddition.util.EnchantmentUtils;
import org.carpetorgaddition.util.TextUtils;

import java.util.List;
import java.util.Objects;

public class DictionaryCommand {
    // TODO 补全注释 处理多个对象匹配成功的情况
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("dictionary")
                        .then(ClientCommandManager.literal("item")
                                .then(ClientCommandManager.argument("item", ClientObjectArgumentType.item())
                                        .executes(DictionaryCommand::item)))
                        .then(ClientCommandManager.literal("block")
                                .then(ClientCommandManager.argument("block", ClientObjectArgumentType.block())
                                        .executes(DictionaryCommand::block)))
                        .then(ClientCommandManager.literal("entity")
                                .then(ClientCommandManager.argument("entity", ClientObjectArgumentType.entityType())
                                        .executes(DictionaryCommand::entityType)))
                        .then(ClientCommandManager.literal("enchantment")
                                .then(ClientCommandManager.argument("enchantment", ClientObjectArgumentType.enchantment())
                                        .executes(DictionaryCommand::enchantment)))
                        .then(ClientCommandManager.literal("statusEffect")
                                .then(ClientCommandManager.argument("statusEffect", ClientObjectArgumentType.statusEffect())
                                        .executes(DictionaryCommand::statusEffect)))));
    }

    private static int item(CommandContext<FabricClientCommandSource> context) {
        List<Item> list = ClientObjectArgumentType.getItem(context, "item");
        for (Item item : list) {
            String id = Registries.ITEM.getId(item).toString();
            sendFeedback(item.getName(), id);
        }
        return list.size();
    }

    private static int block(CommandContext<FabricClientCommandSource> context) {
        List<Block> list = ClientObjectArgumentType.getBlock(context, "block");
        for (Block block : list) {
            String id = Registries.BLOCK.getId(block).toString();
            sendFeedback(block.getName(), id);
        }
        return list.size();
    }

    private static int entityType(CommandContext<FabricClientCommandSource> context) {
        List<EntityType<?>> list = ClientObjectArgumentType.getEntityType(context, "entity");
        for (EntityType<?> entityType : list) {
            String id = Registries.ENTITY_TYPE.getId(entityType).toString();
            sendFeedback(entityType.getName(), id);
        }
        return list.size();
    }

    private static int enchantment(CommandContext<FabricClientCommandSource> context) {
        List<Enchantment> list = ClientObjectArgumentType.getEnchantment(context, "enchantment");
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return 0;
        }
        Registry<Enchantment> registry = player.networkHandler.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
        for (Enchantment enchantment : list) {
            String id = Objects.requireNonNull(registry.getId(enchantment)).toString();
            sendFeedback(EnchantmentUtils.getName(enchantment), id);
        }
        return list.size();
    }

    private static int statusEffect(CommandContext<FabricClientCommandSource> context) {
        List<StatusEffect> list = ClientObjectArgumentType.getStatusEffect(context, "statusEffect");
        for (StatusEffect statusEffect : list) {
            String id = Objects.requireNonNull(Registries.STATUS_EFFECT.getId(statusEffect)).toString();
            sendFeedback(statusEffect.getName(), id);
        }
        return list.size();
    }

    private static void sendFeedback(Text text, String id) {
        ClientMessageUtils.sendMessage("carpet.client.commands.dictionary.id", text, TextUtils.copy(id, id, TextUtils.translate("chat.copy.click"), Formatting.GREEN));
    }
}

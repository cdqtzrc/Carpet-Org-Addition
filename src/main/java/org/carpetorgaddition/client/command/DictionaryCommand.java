package org.carpetorgaddition.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.biome.Biome;
import org.carpetorgaddition.client.command.argument.ClientObjectArgumentType;
import org.carpetorgaddition.client.util.ClientMessageUtils;
import org.carpetorgaddition.util.EnchantmentUtils;
import org.carpetorgaddition.util.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DictionaryCommand {
    // TODO 补全注释
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
        {
            LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal("dictionary");
            dispatcher.register(builder
                    .then(ClientCommandManager.literal("item")
                            .then(ClientCommandManager.argument("item", ClientObjectArgumentType.item())
                                    .executes(context -> getId(context, DictionaryType.ITEM))))
                    .then(ClientCommandManager.literal("block")
                            .then(ClientCommandManager.argument("block", ClientObjectArgumentType.block())
                                    .executes(context -> getId(context, DictionaryType.BLOCK))))
                    .then(ClientCommandManager.literal("entity")
                            .then(ClientCommandManager.argument("entity", ClientObjectArgumentType.entityType())
                                    .executes(context -> getId(context, DictionaryType.ENTITY))))
                    .then(ClientCommandManager.literal("enchantment")
                            .then(ClientCommandManager.argument("enchantment", ClientObjectArgumentType.enchantment())
                                    .executes(context -> getId(context, DictionaryType.ENCHANTMENT))))
                    .then(ClientCommandManager.literal("statusEffect")
                            .then(ClientCommandManager.argument("statusEffect", ClientObjectArgumentType.statusEffect())
                                    .executes(context -> getId(context, DictionaryType.STATUS_EFFECT))))
                    .then(ClientCommandManager.literal("biome")
                            .then(ClientCommandManager.argument("biome", ClientObjectArgumentType.biome())
                                    .executes(context -> getId(context, DictionaryType.BIOME)))));
        });
    }

    private static <T> int getId(CommandContext<FabricClientCommandSource> context, DictionaryType type) {
        List<T> list = ClientObjectArgumentType.getType(context, type.name);
        if (list.size() == 1) {
            T t = list.getFirst();
            String id = type.id(t);
            sendFeedback(type.name(t), id);
        } else {
            sendFeedback(list.size());
            for (T t : list) {
                sendFeedback(type.id(t));
            }
        }
        return list.size();
    }

    private static void sendFeedback(Text text, String id) {
        ClientMessageUtils.sendMessage("carpet.client.commands.dictionary.id", text, canCopyId(id));
    }

    private static void sendFeedback(int count) {
        ClientMessageUtils.sendMessage("carpet.client.commands.dictionary.multiple.id", count);
    }

    private static void sendFeedback(String id) {
        ClientMessageUtils.sendMessage("carpet.client.commands.dictionary.multiple.each", canCopyId(id));
    }

    @NotNull
    private static MutableText canCopyId(String id) {
        return TextUtils.copy(id, id, TextUtils.translate("chat.copy.click"), Formatting.GREEN);
    }

    private enum DictionaryType {
        ITEM("item"),
        BLOCK("block"),
        ENTITY("entity"),
        ENCHANTMENT("enchantment"),
        STATUS_EFFECT("statusEffect"),
        BIOME("biome");
        private final String name;

        DictionaryType(String name) {
            this.name = name;
        }

        @SuppressWarnings("DataFlowIssue")
        private String id(Object obj) {
            DynamicRegistryManager.Immutable registry = MinecraftClient.getInstance().player.networkHandler.getRegistryManager();
            return switch (this) {
                case ITEM -> Registries.ITEM.getId((Item) obj).toString();
                case BLOCK -> Registries.BLOCK.getId((Block) obj).toString();
                case ENTITY -> Registries.ENTITY_TYPE.getId((EntityType<?>) obj).toString();
                case ENCHANTMENT -> registry.get(RegistryKeys.ENCHANTMENT).getId((Enchantment) obj).toString();
                case STATUS_EFFECT -> registry.get(RegistryKeys.STATUS_EFFECT).getId((StatusEffect) obj).toString();
                case BIOME -> registry.get(RegistryKeys.BIOME).getId((Biome) obj).toString();
            };
        }

        @SuppressWarnings("DataFlowIssue")
        private Text name(Object obj) {
            DynamicRegistryManager.Immutable registry = MinecraftClient.getInstance().player.networkHandler.getRegistryManager();
            return switch (this) {
                case ITEM -> ((Item) obj).getName();
                case BLOCK -> ((Block) obj).getName();
                case ENTITY -> ((EntityType<?>) obj).getName();
                case ENCHANTMENT -> EnchantmentUtils.getName((Enchantment) obj);
                case STATUS_EFFECT -> ((StatusEffect) obj).getName();
                case BIOME -> {
                    String key = registry.get(RegistryKeys.BIOME).getId((Biome) obj).toTranslationKey("biome");
                    yield TextUtils.translate(key);
                }
            };
        }
    }
}

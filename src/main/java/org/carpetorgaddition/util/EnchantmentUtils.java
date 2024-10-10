package org.carpetorgaddition.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.Optional;

public class EnchantmentUtils {
    /**
     * @return 指定物品上是否有指定附魔
     */
    public static boolean hasEnchantment(World world, RegistryKey<Enchantment> key, ItemStack itemStack) {
        Optional<Registry<Enchantment>> optional = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
        if (optional.isEmpty()) {
            return false;
        }
        Enchantment enchantment = optional.get().get(key);
        return getLevel(world, enchantment, itemStack) > 0;
    }

    /**
     * @return 获取指定物品上指定附魔的等级
     */
    public static int getLevel(World world, Enchantment enchantment, ItemStack itemStack) {
        Optional<Registry<Enchantment>> optional = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
        if (optional.isEmpty()) {
            return 0;
        }
        RegistryEntry<Enchantment> entry = optional.get().getEntry(enchantment);
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent component = itemStack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
            return component.getLevel(entry);
        }
        return EnchantmentHelper.getLevel(entry, itemStack);
    }

    /**
     * @return 获取一个附魔的名字，不带等级
     */
    public static MutableText getName(Enchantment enchantment) {
        MutableText mutableText = enchantment.description().copy();
        // 如果是诅咒附魔，设置为红色
        if (RegistryEntry.of(enchantment).isIn(EnchantmentTags.CURSE)) {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
        } else {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
        }
        return mutableText;
    }

    /**
     * @param level 附魔的等级
     * @return 获取一个附魔的名字，带有等级
     */
    public static MutableText getName(Enchantment enchantment, int level) {
        MutableText mutableText = getName(enchantment);
        if (level != 1 || enchantment.getMaxLevel() != 1) {
            mutableText = TextUtils.appendAll(mutableText, ScreenTexts.SPACE, TextUtils.translate("enchantment.level." + level));
        }
        return mutableText;
    }
}

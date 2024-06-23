package org.carpet_org_addition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.wheel.JsonSerial;
import org.carpet_org_addition.util.matcher.Matcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractActionData implements JsonSerial {
    public abstract ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer);

    // 获取物品的可变文本形式
    protected static MutableText getHoverText(Item item) {
        if (item == Items.AIR || item == null) {
            return TextUtils.hoverText(Text.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        // 获取物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(item.toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(Text.literal(capitalizeFirstLetter), item.getName(), null);
    }

    // 获取物品的可变文本形式
    protected static MutableText getHoverText(Matcher matcher) {
        if (matcher.isEmpty()) {
            return TextUtils.hoverText(Text.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        // 获取物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(matcher.toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(Text.literal(capitalizeFirstLetter), matcher.getName(), null);
    }

    // 获取物品堆栈的可变文本形式：物品名称x堆叠数量
    protected static MutableText getWithCountHoverText(@NotNull ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return TextUtils.hoverText(Text.literal("[A]"), TextUtils.appendAll(Items.AIR.getName()), Formatting.DARK_GRAY);
        }
        // 获取物品堆栈对应的物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(itemStack.getItem().toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(Text.literal(capitalizeFirstLetter),
                TextUtils.appendAll(itemStack.getItem().getName(), "x", String.valueOf(itemStack.getCount())), null);
    }

    // 获取配方的输出物品
    protected static Item getCraftOutPut(EntityPlayerMPFake fakePlayer, Matcher[] arr) {
        int widthHeight = arr.length == 9 ? 3 : 2;
        List<ItemStack> list = Arrays.stream(arr).map(Matcher::getDefaultStack).toList();
        CraftingRecipeInput craftingRecipeInput = CraftingRecipeInput.create(widthHeight, widthHeight, list);
        World world = fakePlayer.getWorld();
        // 获取配方的输出
        Optional<RecipeEntry<CraftingRecipe>> optional = fakePlayer.getCommandSource().getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingRecipeInput, world);
        return optional.map(craftingRecipe -> craftingRecipe.value().craft(craftingRecipeInput, world.getRegistryManager()).getItem()).orElse(Items.AIR);
    }
}

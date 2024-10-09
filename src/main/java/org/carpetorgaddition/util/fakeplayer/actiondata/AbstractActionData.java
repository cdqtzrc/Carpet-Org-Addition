package org.carpetorgaddition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.carpetorgaddition.util.TextUtils;
import org.carpetorgaddition.util.matcher.Matcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractActionData {
    public abstract ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer);

    // 获取物品的可变文本形式
    protected static MutableText getHoverText(Item item) {
        if (item == Items.AIR || item == null) {
            return TextUtils.hoverText(Text.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        // 获取物品ID的首字母，然后转为大写，再放进中括号里
        String capitalizeFirstLetter = "[" + String.valueOf(item.toString().charAt(0)).toUpperCase() + "]";
        return TextUtils.hoverText(Text.literal(capitalizeFirstLetter), item.getName());
    }

    // 获取物品的可变文本形式
    protected static MutableText getHoverText(Matcher matcher) {
        if (matcher.isEmpty()) {
            return TextUtils.hoverText(Text.literal("[A]"), Items.AIR.getName(), Formatting.DARK_GRAY);
        }
        return TextUtils.hoverText(Text.literal(getInitial(matcher)), matcher.getName());
    }

    // 获取物品ID的首字母，然后转为大写，再放进中括号里
    private static @NotNull String getInitial(Matcher matcher) {
        // 将物品名称的字符串切割为命名空间（如果有）和物品id
        String name = matcher.toString();
        if (name.startsWith("#")) {
            return "[#]";
        }
        String[] split = name.split(":");
        // 获取数组的索引，如果有命名空间，返回1索引，否则返回0索引，即舍弃命名空间
        int index = (split.length == 1) ? 0 : 1;
        // 获取物品id的首字母，然后大写
        return "[" + String.valueOf(split[index].charAt(0)).toUpperCase() + "]";
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
        // 合成格的宽高，如果数组长度为9，则表示在工作台合成，所以宽高为3，否则，物品在生存模式物品栏合成，所以宽高为2
        CraftingInventory craftingInventory = getCraftingInventory(arr);
        // 设置物品栏中每一个物品为配方中的物品
        for (int i = 0; i < arr.length; i++) {
            // 获取一个与对象匹配的物品堆栈对象
            ItemStack itemStack = arr[i].getDefaultStack();
            craftingInventory.setStack(i, itemStack);
        }
        World world = fakePlayer.getWorld();
        // 获取配方的输出
        Optional<CraftingRecipe> optional = fakePlayer.getCommandSource().getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        return optional.map(craftingRecipe -> craftingRecipe.craft(craftingInventory, world.getRegistryManager()).getItem()).orElse(Items.AIR);
    }

    @NotNull
    private static CraftingInventory getCraftingInventory(Matcher[] arr) {
        int widthHeight = arr.length == 9 ? 3 : 2;
        // 获取一个合成物品栏的对象，重写方法仅仅是为了代码不报错
        return new CraftingInventory(new ScreenHandler(null, -1) {
            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean canUse(PlayerEntity player) {
                return false;
            }
        }, widthHeight, widthHeight);
    }

    /**
     * 序列化假玩家动作数据
     */
    public abstract JsonObject toJson();
}

package org.carpet_org_addition.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.carpet_org_addition.exception.EmptyShulkerBoxException;

import java.util.Objects;

public class ShulkerBoxUtils {
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final String ITEMS = "Items";

    /**
     * 潜影盒工具类，私有化构造方法
     */
    private ShulkerBoxUtils() {
    }

    /**
     * 取出潜影盒内容物的第一个非空气物品
     *
     * @param shulkerBoxItemStack 当前要操作的潜影盒
     * @return 潜影盒内第一个非空气物品
     */
    public static ItemStack getShulkerBoxItem(ItemStack shulkerBoxItemStack) {
        if (!shulkerBoxItemStack.isOf(Items.SHULKER_BOX) || shulkerBoxItemStack.getCount() != 1) {
            return ItemStack.EMPTY;
        }
        NbtCompound nbt = shulkerBoxItemStack.getNbt();
        NbtList list;
        try {
            list = Objects.requireNonNull(nbt).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        } catch (NullPointerException e) {
            throw new EmptyShulkerBoxException();
        }
        for (int index = 0; index < list.size(); index++) {
            ItemStack itemStack = ItemStack.fromNbt(list.getCompound(index));
            if (itemStack.isEmpty()) {
                continue;
            }
            //获取后删除潜影盒内的物品
            removeFirstStack(list, shulkerBoxItemStack);
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * 删除潜影盒中第一个物品
     *
     * @param list                要删除物品的列表，这是潜影盒NBT中的一个Items列表
     * @param shulkerBoxItemStack 要删除物品的潜影盒
     */
    private static void removeFirstStack(NbtList list, ItemStack shulkerBoxItemStack) {
        for (int index = 0; index < list.size(); index++) {
            if (ItemStack.fromNbt(list.getCompound(index)).isEmpty()) {
                continue;
            }
            list.remove(index);
            return;
        }
        if (isEmptyShulkerBox(shulkerBoxItemStack)) {
            shulkerBoxItemStack.removeSubNbt(BLOCK_ENTITY_TAG);
        }
    }

    /**
     * 判断当前潜影盒是否是空潜影盒
     *
     * @param shulkerBoxItemStack 当前要检查是否为空的潜影盒物品
     * @return 潜影盒是否为空
     */
    public static boolean isEmptyShulkerBox(ItemStack shulkerBoxItemStack) {
        if (shulkerBoxItemStack.getCount() != 1) {
            return true;
        }
        NbtCompound nbt = shulkerBoxItemStack.getNbt();
        NbtList list;
        try {
            list = Objects.requireNonNull(nbt).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        } catch (NullPointerException e) {
            return true;
        }
        for (int index = 0; index < list.size(); index++) {
            ItemStack itemStack = ItemStack.fromNbt(list.getCompound(index));
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

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
     * 取出潜影盒内容物的第一个非空气物品，堆叠的潜影盒会被视为空潜影盒。<br/>
     * <br/>
     * 因为正常情况下有物品的潜影盒无法堆叠（原版的潜影盒不可堆叠，但是空潜影盒可以通过carpet或Tweakeroo的功能堆叠），即便是有物品，也不能使用
     * 本方法取出物品，如果需要取出，应该现将物品堆分开，否则如果直接取出，则堆叠的所有潜影盒都会受影响。假设有一个堆叠数为10的潜影盒，内含一组物品，
     * 如果将物品分成10份后再取出，则每个潜影盒都可以取出1组物品，总共可以取出10组物品。但是如果直接使用本方法取出物品，则只能取出一组物品，然后获得
     * 一个10堆叠的空潜影盒，这会损失一些物品。因为本方法操作的整组物品堆栈，操作时并不会考虑物品堆叠数量，所以需要事先将堆叠潜影盒分开。
     *
     * @param shulkerBoxItemStack 当前要操作的潜影盒
     * @return 潜影盒内第一个非空气物品，如果潜影盒内没有物品，返回ItemStack.EMPTY
     */
    public static ItemStack getShulkerBoxItem(ItemStack shulkerBoxItemStack) {
        // 正常情况下有物品的潜影盒不可堆叠，所以可堆叠的潜影盒内部没有物品
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
        // 依次遍历潜影盒内部每一个槽位
        for (int index = 0; index < list.size(); index++) {
            // 依次取出潜影盒内部的每一个物品，此时潜影盒内部的物品没有被删除，所以相当于把物品复制了一份
            ItemStack itemStack = ItemStack.fromNbt(list.getCompound(index));
            // 如果物品为空，结束本轮循环，不再向下执行
            if (itemStack.isEmpty()) {
                continue;
            }
            // 如果有物品，才将潜影盒内的物品删除，再将该物品的对象作为方法返回值返回
            removeFirstStack(list, shulkerBoxItemStack);
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * 删除潜影盒中第一个物品，需要确保潜影盒堆叠数为1
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
     * @return 潜影盒内没有物品返回true，有物品返回false
     */
    public static boolean isEmptyShulkerBox(ItemStack shulkerBoxItemStack) {
        // 正常情况下有物品的潜影盒无法堆叠
        if (shulkerBoxItemStack.getCount() != 1) {
            return true;
        }
        NbtCompound nbt = shulkerBoxItemStack.getNbt();
        NbtList list;
        try {
            list = Objects.requireNonNull(nbt).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        } catch (NullPointerException e) {
            // 潜影盒物品没有NBT，说明该潜影盒物品为空
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

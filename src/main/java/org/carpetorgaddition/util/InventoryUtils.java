package org.carpetorgaddition.util;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.carpetorgaddition.util.matcher.SimpleMatcher;
import org.carpetorgaddition.util.inventory.ImmutableInventory;

import java.util.Objects;
import java.util.function.Consumer;

public class InventoryUtils {
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String ITEMS = "Items";
    public static final String INVENTORY = "Inventory";

    /**
     * 物品栏工具类，私有化构造方法
     */
    private InventoryUtils() {
    }

    /**
     * 从物品形式的潜影盒中获取第一个指定的物品，并将该物品从潜影盒的NBT中删除，使用时，为避免不必要的物品浪费，取出来的物品必须使用或丢出
     *
     * @param shulkerBox 潜影盒物品
     * @param matcher    一个物品匹配器对象，用来指定要从潜影盒中拿取的物品
     * @return 潜影盒中获取的指定物品
     */
    public static ItemStack pickItemFromShulkerBox(ItemStack shulkerBox, SimpleMatcher matcher) {
        // 判断潜影盒是否为空，空潜影盒直接返回空物品
        if (isEmptyShulkerBox(shulkerBox)) {
            // 因为这个判断，可以保证下方的shulkerBox.getNbt()不会返回null
            return ItemStack.EMPTY;
        }
        @SuppressWarnings("DataFlowIssue")
        NbtList list = shulkerBox.getNbt().getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        for (int index = 0; index < list.size(); index++) {
            ItemStack itemStack = ItemStack.fromNbt(list.getCompound(index));
            // 依次检查潜影盒内每个物品是否为指定物品，如果是，从NBT中删除该物品，并将该物品的副本返回
            if (matcher.test(itemStack)) {
                list.remove(index);
                // 如果潜影盒最后一个物品被取出，就删除潜影盒的“BlockEntityTag”标签以保证潜影盒堆叠的正常运行
                ifItIsEmptyRemoveIt(shulkerBox);
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 获取潜影盒中指定物品，并让这个物品执行一个函数，然后将执行函数前的物品返回
     *
     * @param matcher  匹配物品的谓词
     * @param consumer 要执行的函数
     * @return 执行函数前的物品
     */
    public static ItemStack shulkerBoxConsumer(ItemStack shulkerBox, SimpleMatcher matcher, Consumer<ItemStack> consumer) {
        if (isEmptyShulkerBox(shulkerBox)) {
            // 因为这个判断，可以保证下方的shulkerBox.getNbt()不会返回null
            return ItemStack.EMPTY;
        }
        @SuppressWarnings("DataFlowIssue")
        NbtList list = shulkerBox.getNbt().getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        for (int index = 0; index < list.size(); index++) {
            ItemStack itemStack = ItemStack.fromNbt(list.getCompound(index));
            ItemStack copyStack = itemStack.copy();
            if (matcher.test(itemStack)) {
                consumer.accept(itemStack);
                // 当前槽位的物品被清除，删除对应NBT
                if (itemStack.isEmpty()) {
                    list.remove(index);
                    ifItIsEmptyRemoveIt(shulkerBox);
                } else {
                    // 将修改过的物品写回NBT
                    list.setElement(index, itemStack.writeNbt(new NbtCompound()));
                }
                return copyStack;
            }
        }
        return ItemStack.EMPTY;
    }

    // 如果潜影盒为空，删除对应的NBT
    private static void ifItIsEmptyRemoveIt(ItemStack shulkerBox) {
        if (isEmptyShulkerBox(shulkerBox)) {
            shulkerBox.removeSubNbt(BLOCK_ENTITY_TAG);
        }
    }

    /**
     * 判断当前潜影盒是否是空潜影盒
     *
     * @param shulkerBox 当前要检查是否为空的潜影盒物品
     * @return 潜影盒内没有物品返回true，有物品返回false
     * @apiNote 此方法可以保证返回值为false时，shulkerBox.getNbt()永远不会返回null
     */
    public static boolean isEmptyShulkerBox(ItemStack shulkerBox) {
        // 正常情况下有物品的潜影盒无法堆叠
        if (shulkerBox.getCount() != 1) {
            return true;
        }
        NbtCompound nbt = shulkerBox.getNbt();
        // 潜影盒没有NBT，所以一定是空潜影盒
        if (nbt == null) {
            return true;
        }
        NbtList list = Objects.requireNonNull(nbt).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        for (int index = 0; index < list.size(); index++) {
            ItemStack itemStack = ItemStack.fromNbt(list.getCompound(index));
            if (itemStack.isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 获取潜影盒物品的物品栏
     *
     * @param shulkerBox 要获取物品栏的潜影盒
     * @return 潜影盒内的物品栏
     */
    public static ImmutableInventory getInventory(ItemStack shulkerBox) {
        if (isEmptyShulkerBox(shulkerBox)) {
            return ImmutableInventory.EMPTY;
        }
        // 获取潜影盒NBT
        // 因为有空潜影盒的判断，shulkerBox.getNbt()不会返回null
        // noinspection DataFlowIssue
        NbtCompound nbt = shulkerBox.getNbt().getCompound(BLOCK_ENTITY_TAG);
        if (nbt != null && nbt.contains(ITEMS, NbtElement.LIST_TYPE)) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
            // 读取潜影盒NBT
            Inventories.readNbt(nbt, defaultedList);
            return new ImmutableInventory(defaultedList);
        }
        return ImmutableInventory.EMPTY;
    }

    /**
     * 从NBT中获取一个物品栏对象
     *
     * @param nbt 从这个NBT中获取物品栏
     */
    @SuppressWarnings("unused")
    public static ImmutableInventory getInventoryFromNbt(NbtCompound nbt) {
        NbtList inventory = nbt.getList(INVENTORY, NbtElement.COMPOUND_TYPE);
        int size = inventory.size();
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(size, ItemStack.EMPTY);
        for (int index = 0; index < size; index++) {
            defaultedList.set(index, ItemStack.fromNbt(inventory.getCompound(index)));
        }
        return new ImmutableInventory(defaultedList);
    }

    /**
     * 判断指定物品是否为潜影盒
     *
     * @param shulkerBox 要判断是否为潜影盒的物品
     * @return 指定物品是否是潜影盒
     */
    public static boolean isShulkerBoxItem(ItemStack shulkerBox) {
        if (shulkerBox.isOf(Items.SHULKER_BOX)) {
            return true;
        }
        if (shulkerBox.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }
}

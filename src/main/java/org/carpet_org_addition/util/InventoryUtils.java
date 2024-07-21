package org.carpet_org_addition.util;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.matcher.SimpleMatcher;
import org.carpet_org_addition.util.wheel.ImmutableInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class InventoryUtils {
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String ITEMS = "Items";
    public static final String INVENTORY = "Inventory";

    /**
     * 潜影盒工具类，私有化构造方法
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
                if (isEmptyShulkerBox(shulkerBox)) {
                    shulkerBox.removeSubNbt(BLOCK_ENTITY_TAG);
                }
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
                return copyStack;
            }
        }
        return ItemStack.EMPTY;
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
        //noinspection DataFlowIssue shulkerBox.getNbt()不会返回null
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
        return shulkerBox.isOf(Items.SHULKER_BOX)
                || shulkerBox.isOf(Items.WHITE_SHULKER_BOX)
                || shulkerBox.isOf(Items.ORANGE_SHULKER_BOX)
                || shulkerBox.isOf(Items.MAGENTA_SHULKER_BOX)
                || shulkerBox.isOf(Items.LIGHT_BLUE_SHULKER_BOX)
                || shulkerBox.isOf(Items.YELLOW_SHULKER_BOX)
                || shulkerBox.isOf(Items.LIME_SHULKER_BOX)
                || shulkerBox.isOf(Items.PINK_SHULKER_BOX)
                || shulkerBox.isOf(Items.GRAY_SHULKER_BOX)
                || shulkerBox.isOf(Items.LIGHT_GRAY_SHULKER_BOX)
                || shulkerBox.isOf(Items.CYAN_SHULKER_BOX)
                || shulkerBox.isOf(Items.PURPLE_SHULKER_BOX)
                || shulkerBox.isOf(Items.BLUE_SHULKER_BOX)
                || shulkerBox.isOf(Items.BROWN_SHULKER_BOX)
                || shulkerBox.isOf(Items.GREEN_SHULKER_BOX)
                || shulkerBox.isOf(Items.RED_SHULKER_BOX)
                || shulkerBox.isOf(Items.BLACK_SHULKER_BOX);
    }

    /**
     * 整理物品栏，合并未堆叠满的物品，然后物品按照ID排序，空气物品放在最后
     *
     * @param list 包含物品的集合
     */
    public static void sortInventory(List<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack itemStack = list.get(i);
            // 物品到最大堆叠还需要多少物品
            int count = itemStack.getMaxCount() - itemStack.getCount();
            // 该物品堆叠已满，跳过该物品的合并
            if (count <= 0) {
                continue;
            }
            // 合并物品
            for (int j = i + 1; j < list.size(); j++) {
                ItemStack otherStack = list.get(j);
                if (otherStack.isEmpty()) {
                    continue;
                }
                // 物品是否可以合并
                if (ItemStack.canCombine(itemStack, otherStack)) {
                    if (count - otherStack.getCount() > 0) {
                        // 合并后堆叠数量仍然不满
                        itemStack.increment(otherStack.getCount());
                        list.set(j, ItemStack.EMPTY);
                        count = itemStack.getMaxCount() - itemStack.getCount();
                    } else {
                        // 合并后堆叠数量已满
                        // 一共需要移动多少个物品
                        int moveCount = itemStack.getMaxCount() - itemStack.getCount();
                        itemStack.increment(moveCount);
                        otherStack.decrement(moveCount);
                        break;
                    }
                }
            }
        }
        // 物品排序
        list.sort((o1, o2) -> {
            // 空物品放在最后
            if (!o1.isEmpty() && o2.isEmpty()) {
                return -1;
            }
            if (o1.isEmpty() && !o2.isEmpty()) {
                return 1;
            }
            // 按物品ID排序
            return Registries.ITEM.getId(o1.getItem()).toString().compareTo(Registries.ITEM.getId(o2.getItem()).toString());
        });
    }

    public static List<ItemStack> toList(Inventory inventory) {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int index = 0; index < inventory.size(); index++) {
            list.add(inventory.getStack(index));
        }
        return list;
    }
}

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
import org.carpet_org_addition.util.matcher.Matcher;
import org.carpet_org_addition.util.wheel.ImmutableInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * 取出并删除潜影盒内容物的第一个非空气物品，堆叠的潜影盒会被视为空潜影盒。<br/>
     * <br/>
     * 因为正常情况下有物品的潜影盒无法堆叠（原版的潜影盒不可堆叠，但是空潜影盒可以通过Carpet或Tweakeroo的功能堆叠），即便是有物品，也不能使用
     * 本方法取出物品，如果需要取出，应该现将物品堆分开，否则如果直接取出，则堆叠的所有潜影盒都会受影响。假设有一个堆叠数为10的潜影盒，内含一组物品，
     * 如果将物品分成10份后再取出，则每个潜影盒都可以取出1组物品，总共可以取出10组物品。但是如果直接使用本方法取出物品，则只能取出一组物品，然后获得
     * 一个10堆叠的空潜影盒，这会损失一些物品。因为本方法操作的整组物品堆栈，操作时并不会考虑物品堆叠数量，所以需要事先将堆叠潜影盒分开。
     *
     * @param shulkerBox 当前要操作的潜影盒
     * @return 潜影盒内第一个非空气物品，如果潜影盒内没有物品，返回ItemStack.EMPTY
     */
    public static ItemStack getShulkerBoxItem(ItemStack shulkerBox) {
        if (!InventoryUtils.isShulkerBoxItem(shulkerBox)) {
            // 物品不是潜影盒，自然不会有潜影盒的NBT
            return ItemStack.EMPTY;
        }
        // 正常情况下有物品的潜影盒不可堆叠，所以可堆叠的潜影盒内部没有物品
        if (shulkerBox.getCount() != 1) {
            return ItemStack.EMPTY;
        }
        NbtCompound nbt = shulkerBox.getNbt();
        NbtList list;
        try {
            list = Objects.requireNonNull(nbt).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        } catch (NullPointerException e) {
            return ItemStack.EMPTY;
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
            list.remove(index);
            if (isEmptyShulkerBox(shulkerBox)) {
                shulkerBox.removeSubNbt(BLOCK_ENTITY_TAG);
            }
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * 从物品形式的潜影盒中获取第一个指定的物品，并将该物品从潜影盒的NBT中删除，使用时，为避免不必要的物品浪费，取出来的物品必须使用或丢出
     *
     * @param shulkerBox 潜影盒物品
     * @param matcher    一个物品匹配器对象，用来指定要从潜影盒中拿取的物品
     * @return 潜影盒中获取的指定物品
     */
    public static ItemStack pickItemFromShulkerBox(ItemStack shulkerBox, Matcher matcher) {
        // 判断潜影盒是否为空，空潜影盒直接返回空物品
        if (isEmptyShulkerBox(shulkerBox)) {
            return ItemStack.EMPTY;
        }
        NbtCompound nbt = shulkerBox.getNbt();
        NbtList list;
        try {
            list = Objects.requireNonNull(nbt).getCompound(BLOCK_ENTITY_TAG).getList(ITEMS, NbtElement.COMPOUND_TYPE);
        } catch (NullPointerException e) {
            return ItemStack.EMPTY;
        }
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
     * 判断当前潜影盒是否是空潜影盒
     *
     * @param shulkerBox 当前要检查是否为空的潜影盒物品
     * @return 潜影盒内没有物品返回true，有物品返回false
     */
    public static boolean isEmptyShulkerBox(ItemStack shulkerBox) {
        // 正常情况下有物品的潜影盒无法堆叠
        if (shulkerBox.getCount() != 1) {
            return true;
        }
        NbtCompound nbt = shulkerBox.getNbt();
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

    /**
     * 获取潜影盒物品的物品栏
     *
     * @param shulkerBox 要获取物品栏的潜影盒
     * @return 潜影盒内的物品栏
     */
    // TODO 没有NBT时返回一个空物品栏
    public static ImmutableInventory getInventory(ItemStack shulkerBox) {
        try {
            // 获取潜影盒NBT
            NbtCompound nbt = Objects.requireNonNull(shulkerBox.getNbt()).getCompound(BLOCK_ENTITY_TAG);
            if (nbt != null && nbt.contains(ITEMS, NbtElement.LIST_TYPE)) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
                // 读取潜影盒NBT
                Inventories.readNbt(nbt, defaultedList);
                return new ImmutableInventory(defaultedList);
            }
            return ImmutableInventory.EMPTY;
        } catch (NullPointerException e) {
            // 潜影盒物品没有NBT，说明该潜影盒物品为空
            return ImmutableInventory.EMPTY;
        }
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

package org.carpet_org_addition.util.wheel;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.carpet_org_addition.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

public class ShulkerBoxInventory implements Inventory {

    private final DefaultedList<ItemStack> stacks;
    private final List<ItemStack> shulkerBoxList;

    public ShulkerBoxInventory(List<ItemStack> shulkerBoxList) {
        this.shulkerBoxList = new ArrayList<>();
        ArrayList<ItemStack> list = new ArrayList<>();
        for (ItemStack itemStack : shulkerBoxList) {
            if (InventoryUtils.isShulkerBoxItem(itemStack)) {
                if (InventoryUtils.isEmptyShulkerBox(itemStack)) {
                    continue;
                }
                this.shulkerBoxList.add(itemStack);
                // 获取潜影盒物品栏组件
                ContainerComponent component = itemStack.get(DataComponentTypes.CONTAINER);
                if (component != null) {
                    list.addAll(component.stream().toList());
                }
            }
        }
        this.stacks = DefaultedList.copyOf(ItemStack.EMPTY, list.toArray(value -> new ItemStack[0]));
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.stacks) {
            if (itemStack.isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.stacks, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.stacks, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    /**
     * 为潜影盒内的物品整理并排序，当前物品栏为多个潜影盒时，物品可以跨潜影盒整理排序
     */
    public void sort() {
        InventoryUtils.sortInventory(this.stacks);
    }

    /**
     * 清除空潜影盒的物品栏组件
     */
    public void removeInventoryNbt() {
        for (ItemStack itemStack : this.shulkerBoxList) {
            if (InventoryUtils.isEmptyShulkerBox(itemStack)) {
                itemStack.remove(DataComponentTypes.CONTAINER);
            }
        }
    }

    /**
     * 应用对潜影盒的更改：将物品集合写入物品组件
     */
    public void application() {
        int number = 0;
        for (ItemStack itemStack : this.shulkerBoxList) {
            DefaultedList<ItemStack> defaultedList;
            if (number < this.size()) {
                List<ItemStack> list = this.stacks.subList(number, (number + 27) > this.size() ? this.size() : (number + 27));
                number += 27;
                ItemStack[] arr = list.toArray(new ItemStack[0]);
                defaultedList = DefaultedList.copyOf(ItemStack.EMPTY, arr);
            } else {
                defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
            }
            itemStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(defaultedList));
        }
    }
}

package org.carpet_org_addition.util.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public abstract class AbstractCustomSizeInventory implements Inventory {
    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(this.getSize() - this.getActualSize(), ItemStack.EMPTY);

    protected abstract int getSize();

    protected abstract int getActualSize();

    protected abstract Inventory getInventory();

    @Override
    public final int size() {
        return this.getSize();
    }

    @Override
    public final boolean isEmpty() {
        if (this.getInventory().isEmpty()) {
            for (ItemStack itemStack : stacks) {
                if (itemStack.isEmpty()) {
                    continue;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < this.getActualSize()) {
            return this.getInventory().getStack(slot);
        }
        return stacks.get(slot - this.getActualSize());
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < this.getActualSize()) {
            return this.getInventory().removeStack(slot, amount);
        }
        ItemStack itemStack = Inventories.splitStack(this.stacks, getAmendSlotIndex(slot), amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < this.getActualSize()) {
            return this.getInventory().removeStack(slot);
        }
        ItemStack itemStack = this.stacks.get(getAmendSlotIndex(slot));
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.stacks.set(getAmendSlotIndex(slot), ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < this.getActualSize()) {
            this.getInventory().setStack(slot, stack);
            return;
        }
        this.stacks.set(getAmendSlotIndex(slot), stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void clear() {
        this.getInventory().clear();
        this.stacks.clear();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot < this.getActualSize();
    }

    public void dropExcess(PlayerEntity player) {
        for (ItemStack itemStack : stacks) {
            player.dropItem(itemStack, false, false);
        }
    }

    protected final int getAmendSlotIndex(int slotIndex) {
        return slotIndex - this.getActualSize();
    }
}

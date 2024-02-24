package org.carpet_org_addition.util.matcher;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ItemStackMatcher implements Matcher {
    public final ItemStack itemStack;

    public ItemStackMatcher(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ItemStack.canCombine(this.itemStack, itemStack);
    }

    @Override
    public boolean isEmpty() {
        return this.itemStack.isEmpty();
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public Item getItem() {
        return this.itemStack.getItem();
    }

    @Override
    public Text getName() {
        return this.getItem().getName();
    }

    @Override
    public ItemStack getDefaultStack() {
        return this.itemStack;
    }

    @Override
    public MutableText toText() {
        return this.itemStack.toHoverableText().copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ItemStackMatcher itemStackMatcher) {
            return ItemStack.canCombine(this.itemStack, itemStackMatcher.itemStack);
        }
        return false;
    }

    @Override
    public int hashCode() {
        NbtCompound nbt = this.itemStack.getNbt();
        return this.itemStack.getItem().hashCode() + (nbt == null || nbt.isEmpty() ? 0 : nbt.hashCode());
    }

    @Override
    public String toString() {
        return this.itemStack.getItem() + "*" + this.itemStack.getCount();
    }
}

package org.carpet_org_addition.util.helpers;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

/**
 * 不可变的物品栏，一旦创建，里面的内容都是不可以改变的，只能进行查询操作，否则抛出{@link UnsupportedOperationException}
 */
public class ImmutableInventory extends SimpleInventory implements Inventory {
    /**
     * 当前物品栏是否已锁定，锁定后，物品栏不能改变
     */
    private boolean lock = false;

    public ImmutableInventory(DefaultedList<ItemStack> list) {
        super(list.size());
        for (int i = 0; i < list.size(); i++) {
            // 不能用super.setStack(i, list.get(i))，编译器会自动把super设置为this
            this.setStack(i, list.get(i));
        }
        this.lock = true;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack removeStack(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (this.lock) {
            throw new UnsupportedOperationException();
        } else {
            super.setStack(slot, stack);
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ItemStack> clearToList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack removeItem(Item item, int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack addStack(ItemStack stack) {
        throw new UnsupportedOperationException();
    }
}

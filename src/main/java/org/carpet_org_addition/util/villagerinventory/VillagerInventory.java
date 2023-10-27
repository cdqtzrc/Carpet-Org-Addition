package org.carpet_org_addition.util.villagerinventory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class VillagerInventory extends AbstractVillagerInventory {
    //物品栏实际大小
    private final int ACTUAL_SIZE = 8;
    private final SimpleInventory inventory;
    private final VillagerEntity villager;
    //removeStack(int slot, int amount)方法中会用到这个列表
    private final DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public VillagerInventory(VillagerEntity villager) {
        this.villager = villager;
        this.inventory = villager.getInventory();
    }

    //判断物品栏是否为空
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    //获取物品堆栈对象
    @Override
    public ItemStack getStack(int slot) {
        if (slot < ACTUAL_SIZE) {
            return inventory.getStack(slot);
        } else {
            return defaultedList.get(0);
        }
    }

    //删除物品，带数量
    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < ACTUAL_SIZE) {
            return inventory.removeStack(slot, amount);
        }
        return Inventories.splitStack(defaultedList, 0, amount);
    }

    //删除物品
    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack;
        if (slot < ACTUAL_SIZE) {
            itemStack = inventory.getStack(slot);
            inventory.setStack(slot, ItemStack.EMPTY);
        } else {
            itemStack = defaultedList.get(0);
            defaultedList.set(0, ItemStack.EMPTY);
        }
        return itemStack;
    }

    //设置物品堆栈
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < ACTUAL_SIZE) {
            inventory.setStack(slot, stack);
        } else {
            defaultedList.set(0, stack);
        }
    }

    //标记
    @Override
    public void markDirty() {
    }

    //控制物品栏自动关闭
    //村民死亡,村民被删除,村民与玩家距离大于8时自动关闭
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (villager.isDead()) {
            return false;
        }
        if (villager.isRemoved()) {
            return false;
        }
        return player.distanceTo(villager) < 8;
    }

    //清空物品栏
    @Override
    public void clear() {
        inventory.clear();
    }

    //获取容器中最后一个槽位的物品堆栈，用于在关闭村民物品栏GUI时丢出物品
    public ItemStack getEndSlot() {
        return defaultedList.get(0);
    }
}

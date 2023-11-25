package org.carpet_org_addition.util.villagerinventory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

@Deprecated
public class VillagerInventory implements Inventory {
    //物品栏实际大小
    private final int ACTUAL_SIZE = 8;
    private final SimpleInventory inventory;
    private final VillagerEntity villager;
    //removeStack(int slot, int amount)方法中会用到这个列表
    // 村民的物品栏中最后一个物品
    private final SimpleInventory finalItem = new SimpleInventory(1);

    public VillagerInventory(VillagerEntity villager) {
        this.villager = villager;
        this.inventory = villager.getInventory();
    }

    @Override
    public int size() {
        return 9;
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
            return finalItem.getStack(slot - ACTUAL_SIZE);
        }
    }

    //删除物品，带数量
    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < ACTUAL_SIZE) {
            return inventory.removeStack(slot, amount);
        }
        return finalItem.removeStack(slot - ACTUAL_SIZE, amount);
    }

    //删除物品
    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack;
        if (slot < ACTUAL_SIZE) {
            itemStack = inventory.getStack(slot);
            inventory.setStack(slot, ItemStack.EMPTY);
        } else {
            itemStack = finalItem.removeStack(slot - ACTUAL_SIZE);
            finalItem.setStack(slot - ACTUAL_SIZE, ItemStack.EMPTY);
        }
        return itemStack;
    }

    //设置物品堆栈
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < ACTUAL_SIZE) {
            inventory.setStack(slot, stack);
        } else {
            finalItem.setStack(slot - ACTUAL_SIZE, stack);
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

    // 获取容器中装着最后一个物品的物品栏，用于在关闭GUI时让物品回到玩家物品栏中
    @SuppressWarnings("unused")
    public SimpleInventory getFinalItemInventory() {
        return finalItem;
    }
}

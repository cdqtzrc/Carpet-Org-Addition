package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Deprecated
public class FakePlayerInventory extends PlayerInventory implements Inventory {
    //物品栏实际大小
    private final int ACTUAL_SIZE = 41;
    private final EntityPlayerMPFake playerMPFake;
    private final DefaultedList<ItemStack> playerMainInventory;
    private final DefaultedList<ItemStack> armorList;
    private final DefaultedList<ItemStack> offHead;
    private final DefaultedList<ItemStack> placeholderList = DefaultedList.ofSize(13, ItemStack.EMPTY);

    public FakePlayerInventory(EntityPlayerMPFake playerMPFake) {
        super(playerMPFake);
        PlayerInventory inventory = playerMPFake.getInventory();
        this.playerMPFake = playerMPFake;
        this.playerMainInventory = inventory.main;
        this.armorList = inventory.armor;
        this.offHead = inventory.offHand;
    }

    //物品栏的大小
    @Override
    public int size() {
        //6x9的大小
        return 54;
    }

    //判断物品栏是否为空
    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : playerMainInventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        for (ItemStack itemStack : armorList) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        for (ItemStack itemStack : offHead) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    //获取物品栏内物品
    @Override
    public ItemStack getStack(int slot) {
        if (slot < 36) {
            return playerMainInventory.get(slot);
        }
        if (slot < 40) {
            return armorList.get(3 - (slot - 36));
        }
        if (slot == 40) {
            return offHead.get(0);
        }
        return placeholderList.get(slot - ACTUAL_SIZE);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 36) {
            return Inventories.splitStack(playerMainInventory, slot, amount);
        }
        if (slot < 40) {
            return Inventories.splitStack(armorList, 3 - (slot - 36), amount);
        }
        if (slot == 40) {
            return Inventories.splitStack(offHead, 0, amount);
        }
        return Inventories.splitStack(playerMainInventory, slot - ACTUAL_SIZE, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 36) {
            return Inventories.removeStack(playerMainInventory, slot);
        }
        if (slot < 40) {
            return Inventories.removeStack(armorList, 3 - (slot - 36));
        }
        if (slot == 40) {
            return Inventories.removeStack(offHead, 0);
        }
        return Inventories.removeStack(placeholderList, slot - ACTUAL_SIZE);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 36) {
            playerMainInventory.set(slot, stack);
        } else if (slot < 40) {
            armorList.set(3 - (slot - 36), stack);
        } else if (slot == 40) {
            offHead.set(0, stack);
        } else {
            placeholderList.set(slot - ACTUAL_SIZE, stack);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return !playerMPFake.isRemoved();
    }

    @Override
    public void clear() {
        playerMainInventory.clear();
        armorList.clear();
        offHead.clear();
    }

    public DefaultedList<ItemStack> getPlaceholderList() {
        return this.placeholderList;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot < 41;
    }
}

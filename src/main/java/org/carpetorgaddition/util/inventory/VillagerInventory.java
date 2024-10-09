package org.carpetorgaddition.util.inventory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

public class VillagerInventory extends AbstractCustomSizeInventory {
    private final VillagerEntity villager;
    private final SimpleInventory inventory;

    public VillagerInventory(VillagerEntity villager) {
        this.villager = villager;
        this.inventory = villager.getInventory();
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    protected int getActualSize() {
        return 8;
    }

    @Override
    protected Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (villager.isDead() || villager.isRemoved()) {
            return false;
        }
        return player.distanceTo(villager) < 8;
    }
}

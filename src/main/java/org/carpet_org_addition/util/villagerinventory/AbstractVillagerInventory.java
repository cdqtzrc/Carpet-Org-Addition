package org.carpet_org_addition.util.villagerinventory;

import net.minecraft.inventory.Inventory;

public abstract class AbstractVillagerInventory implements Inventory {
    //物品栏大小
    @Override
    public int size() {
        return 9;
    }
}

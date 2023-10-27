package org.carpet_org_addition.util.villagerinventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;

public class VillagerContainerScreenHandler extends Generic3x3ContainerScreenHandler {
    private final VillagerInventory villagerInventory;

    public VillagerContainerScreenHandler(int syncId, PlayerInventory playerInventory, VillagerInventory villagerInventory) {
        super(syncId, playerInventory, villagerInventory);
        this.villagerInventory = villagerInventory;
    }

    //在关闭村民背包界面时丢出最后一格的物品
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        //丢出容器最后一个物品
        player.dropItem(villagerInventory.getEndSlot(), false, false);
    }
}

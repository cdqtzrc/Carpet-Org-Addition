package org.carpet_org_addition.util.wheel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

/**
 * 不能向其中拿取和放置物品的槽位
 */
public class DisabledSlot extends Slot {
    public DisabledSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    // 设置不能向槽位中放入物品
    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    // 设置不能向槽位中拿取物品
    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return false;
    }

    @Override
    public boolean canTakePartial(PlayerEntity player) {
        return false;
    }
}

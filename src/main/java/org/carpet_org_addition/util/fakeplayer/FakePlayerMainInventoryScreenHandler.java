package org.carpet_org_addition.util.fakeplayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class FakePlayerMainInventoryScreenHandler extends GenericContainerScreenHandler {
    private final FakePlayerInventory fakePlayerInventory;

    private FakePlayerMainInventoryScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, FakePlayerInventory fakePlayerInventory, int rows) {
        super(type, syncId, playerInventory, fakePlayerInventory, rows);
        this.fakePlayerInventory = fakePlayerInventory;
    }

    public static FakePlayerMainInventoryScreenHandler getFakePlayerMainInventoryScreenHandler(int syncId, PlayerInventory playerInventory, FakePlayerInventory fakePlayerInventory) {
        return new FakePlayerMainInventoryScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, fakePlayerInventory, 6);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        DefaultedList<ItemStack> placeholderList = fakePlayerInventory.getPlaceholderList();
        for (ItemStack itemStack : placeholderList) {
            player.dropItem(itemStack, false);
        }
    }

    @Override
    public boolean isValid(int slot) {
        return slot < 41;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.getIndex() < 41;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.getIndex() < 41;
    }
}

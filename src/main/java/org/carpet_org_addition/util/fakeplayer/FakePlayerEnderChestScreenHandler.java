package org.carpet_org_addition.util.fakeplayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

//假玩家末影箱GUI
public class FakePlayerEnderChestScreenHandler extends GenericContainerScreenHandler {
    PlayerEntity playerEntity;

    private FakePlayerEnderChestScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, PlayerEntity playerEntity) {
        super(type, syncId, playerInventory, inventory, rows);
        this.playerEntity = playerEntity;
    }

    //获取假玩家末影箱GUI对象
    public static FakePlayerEnderChestScreenHandler getFakePlayerEnderChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PlayerEntity playerEntity) {
        return new FakePlayerEnderChestScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inventory, 3, playerEntity);
    }

    //假玩家死亡时，自动关闭GUI
    @Override
    public boolean canUse(PlayerEntity player) {
        if (playerEntity == null) {
            return false;
        }
        return !playerEntity.isRemoved();
    }
}

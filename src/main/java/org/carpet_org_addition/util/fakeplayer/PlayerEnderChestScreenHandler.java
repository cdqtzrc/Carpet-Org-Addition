package org.carpet_org_addition.util.fakeplayer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

// 假玩家末影箱GUI
public class PlayerEnderChestScreenHandler extends GenericContainerScreenHandler {
    /**
     * 不一定是假玩家，也有可能是/playerTools命令的执行者自己
     */
    private final ServerPlayerEntity playerEntity;

    public PlayerEnderChestScreenHandler(int syncId, PlayerInventory playerInventory, ServerPlayerEntity playerEntity) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, playerEntity.getEnderChestInventory(), 3);
        this.playerEntity = playerEntity;
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

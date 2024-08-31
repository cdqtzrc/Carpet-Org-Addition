package org.carpet_org_addition.util.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPlayerInventory extends AbstractCustomSizeInventory {
    private final ServerPlayerEntity fakePlayer;
    private final PlayerInventory inventory;

    public ServerPlayerInventory(ServerPlayerEntity fakePlayer) {
        this.fakePlayer = fakePlayer;
        this.inventory = fakePlayer.getInventory();
    }

    @Override
    protected int getSize() {
        return 54;
    }

    @Override
    protected int getActualSize() {
        return 41;
    }

    @Override
    protected Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        // 玩家活着，并且玩家没有被删除
        return !this.fakePlayer.isDead() && !this.fakePlayer.isRemoved();
    }
}

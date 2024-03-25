package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import org.carpet_org_addition.util.helpers.AbstractCustomSizeInventory;

public class FakePlayerInventory extends AbstractCustomSizeInventory {
    private final EntityPlayerMPFake fakePlayer;
    private final PlayerInventory inventory;

    public FakePlayerInventory(EntityPlayerMPFake fakePlayer) {
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
        // 假玩家活着，并且假玩家没有被删除
        return !this.fakePlayer.isDead() && !this.fakePlayer.isRemoved();
    }
}

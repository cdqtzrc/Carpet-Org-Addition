package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;

public class FakePlayerGuiCraftScreenHandler extends Generic3x3ContainerScreenHandler {
    private final EntityPlayerMPFake fakePlayer;
    private final ScreenHandlerContext context;
    private final SimpleInventory fakePlayerCraftInventory;

    public FakePlayerGuiCraftScreenHandler(int syncId,
                                           PlayerInventory playerInventory,
                                           EntityPlayerMPFake fakePlayer,
                                           ScreenHandlerContext context,
                                           SimpleInventory fakePlayerCraftInventory) {
        super(syncId, playerInventory, fakePlayerCraftInventory);
        this.fakePlayer = fakePlayer;
        this.context = context;
        this.fakePlayerCraftInventory = fakePlayerCraftInventory;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        //如果没有给假玩家指定合成配方，结束方法
        if (fakePlayerCraftInventory.isEmpty()) {
            return;
        }
        //修改假玩家的3x3合成配方
        Item[] items = new Item[9];
        for (int i = 0; i < fakePlayerCraftInventory.size(); i++) {
            items[i] = fakePlayerCraftInventory.getStack(i).getItem();
        }
        //将假玩家动作设置为3x3合成
        FakePlayerActionInterface fakePlayerActionInterface = (FakePlayerActionInterface) fakePlayer;
        fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_3X3);
        fakePlayerActionInterface.setCraft(items);
        //关闭GUI后，物品回到玩家背包
        this.context.run((world, pos) -> this.dropInventory(player, fakePlayerCraftInventory));
    }
}

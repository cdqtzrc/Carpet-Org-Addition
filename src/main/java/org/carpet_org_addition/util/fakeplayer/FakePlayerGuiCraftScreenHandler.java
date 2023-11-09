package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.command.ServerCommandSource;

public class FakePlayerGuiCraftScreenHandler extends Generic3x3ContainerScreenHandler {
    private final EntityPlayerMPFake fakePlayer;
    private final ScreenHandlerContext screenHandlerContext;
    private final SimpleInventory fakePlayerCraftInventory;
    private final CommandContext<ServerCommandSource> context;

    public FakePlayerGuiCraftScreenHandler(int syncId,
                                           PlayerInventory playerInventory,
                                           EntityPlayerMPFake fakePlayer,
                                           ScreenHandlerContext screenHandlerContext,
                                           SimpleInventory fakePlayerCraftInventory,
                                           CommandContext<ServerCommandSource> context) {
        super(syncId, playerInventory, fakePlayerCraftInventory);
        this.fakePlayer = fakePlayer;
        this.screenHandlerContext = screenHandlerContext;
        this.fakePlayerCraftInventory = fakePlayerCraftInventory;
        this.context = context;
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
        FakePlayerActionInterface fakePlayerActionInterface = (FakePlayerActionInterface) fakePlayer;
        //context不能为null，否则假玩家不能合成
        fakePlayerActionInterface.setContext(context);
        // 设置假玩家合成动作
        setCraftAction(items, fakePlayerActionInterface);
        //关闭GUI后，物品回到玩家背包
        this.screenHandlerContext.run((world, pos) -> this.dropInventory(player, fakePlayerCraftInventory));
    }

    // 设置假玩家合成动作
    private void setCraftAction(Item[] items, FakePlayerActionInterface fakePlayerActionInterface) {
        // 如果能在2x2合成格中合成，优先使用2x2
        if (items[0] == Items.AIR && items[1] == Items.AIR && items[2] == Items.AIR && items[5] == Items.AIR && items[8] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new Item[]{items[3], items[4], items[6], items[7]});
        } else if (items[0] == Items.AIR && items[3] == Items.AIR && items[6] == Items.AIR && items[7] == Items.AIR && items[8] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new Item[]{items[1], items[2], items[4], items[5]});
        } else if (items[2] == Items.AIR && items[5] == Items.AIR && items[6] == Items.AIR && items[7] == Items.AIR && items[8] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new Item[]{items[0], items[1], items[3], items[4]});
        } else if (items[0] == Items.AIR && items[1] == Items.AIR && items[2] == Items.AIR && items[3] == Items.AIR && items[6] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new Item[]{items[4], items[5], items[7], items[8]});
        } else {
            //将假玩家动作设置为3x3合成
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_3X3);
            fakePlayerActionInterface.set3x3Craft(items);
        }
    }
}

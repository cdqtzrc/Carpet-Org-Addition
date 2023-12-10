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
import org.carpet_org_addition.command.PlayerToolsCommand;
import org.carpet_org_addition.util.helpers.ItemMatcher;

public class FakePlayerGuiCraftScreenHandler extends Generic3x3ContainerScreenHandler {
    /**
     * 一个假玩家对象，类中所有操作都是围绕这个假玩家进行的
     */
    private final EntityPlayerMPFake fakePlayer;
    /**
     * 假玩家当前打开的GUI的屏幕处理程序上下文对象，用来在关闭GUI时，将GUI内的物品放回玩家物品栏
     */
    private final ScreenHandlerContext screenHandlerContext;
    /**
     * 控制假玩家合成物品的物品栏，不是假玩家背包的物品栏
     */
    private final SimpleInventory fakePlayerCraftInventory;
    /**
     * 执行/playerTools命令后的命令执行上下文对象，修改假玩家动作类型时会用到这个属性
     */
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

    // 关闭GUI时，设置假玩家的合成动作和配方
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
        // context不能为null，否则假玩家不能合成
        fakePlayerActionInterface.setContext(context);
        // 设置假玩家合成动作
        setCraftAction(items, fakePlayerActionInterface);
        // 关闭GUI后，物品回到玩家背包
        this.screenHandlerContext.run((world, pos) -> this.dropInventory(player, fakePlayerCraftInventory));
        // 提示启用Ctrl+Q合成修复
        PlayerToolsCommand.promptToEnableCtrlQCraftingFix(context.getSource());
    }

    // 设置假玩家合成动作
    private void setCraftAction(Item[] items, FakePlayerActionInterface fakePlayerActionInterface) {
        // 如果能在2x2合成格中合成，优先使用2x2
        if (items[0] == Items.AIR && items[1] == Items.AIR && items[2] == Items.AIR
                && items[5] == Items.AIR && items[8] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new ItemMatcher[]{
                    new ItemMatcher(items[3]), new ItemMatcher(items[4]), new ItemMatcher(items[6]), new ItemMatcher(items[7])});
        } else if (items[0] == Items.AIR && items[3] == Items.AIR && items[6] == Items.AIR
                && items[7] == Items.AIR && items[8] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new ItemMatcher[]{
                    new ItemMatcher(items[1]), new ItemMatcher(items[2]), new ItemMatcher(items[4]), new ItemMatcher(items[5])});
        } else if (items[2] == Items.AIR && items[5] == Items.AIR && items[6] == Items.AIR
                && items[7] == Items.AIR && items[8] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new ItemMatcher[]{new ItemMatcher(items[0]),
                    new ItemMatcher(items[1]), new ItemMatcher(items[3]), new ItemMatcher(items[4])});
        } else if (items[0] == Items.AIR && items[1] == Items.AIR && items[2] == Items.AIR
                && items[3] == Items.AIR && items[6] == Items.AIR) {
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_2X2);
            fakePlayerActionInterface.set2x2Craft(new ItemMatcher[]{
                    new ItemMatcher(items[4]), new ItemMatcher(items[5]), new ItemMatcher(items[7]), new ItemMatcher(items[8])});
        } else {
            //将假玩家动作设置为3x3合成
            fakePlayerActionInterface.setAction(FakePlayerActionType.CRAFT_3X3);
            ItemMatcher[] itemMatcherArr = new ItemMatcher[9];
            for (int i = 0; i < itemMatcherArr.length; i++) {
                itemMatcherArr[i] = new ItemMatcher(items[i]);
            }
            fakePlayerActionInterface.set3x3Craft(itemMatcherArr);
        }
    }
}

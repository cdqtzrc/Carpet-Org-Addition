package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.command.PlayerActionCommand;
import org.carpet_org_addition.util.fakeplayer.actiondata.CraftingTableCraftData;
import org.carpet_org_addition.util.fakeplayer.actiondata.InventoryCraftData;
import org.carpet_org_addition.util.matcher.ItemMatcher;

public class CraftingSetRecipeScreenHandler extends CraftingScreenHandler {
    /**
     * 一个假玩家对象，类中所有操作都是围绕这个假玩家进行的
     */
    private final EntityPlayerMPFake fakePlayer;
    /**
     * 控制假玩家合成物品的物品栏，与父类中的input是同一个对象
     */
    private final RecipeInputInventory inputInventory;
    /**
     * 执行/playerAction命令后的命令执行上下文对象，修改假玩家动作类型时会用到这个属性
     */
    private final CommandContext<ServerCommandSource> context;

    public CraftingSetRecipeScreenHandler(int syncId, PlayerInventory playerInventory, EntityPlayerMPFake fakePlayer,
                                          ScreenHandlerContext screenHandlerContext,
                                          CommandContext<ServerCommandSource> context) {
        super(syncId, playerInventory, screenHandlerContext);
        this.inputInventory = ((FakePlayerCraftRecipeInterface) this).getInput();
        this.fakePlayer = fakePlayer;
        this.context = context;
    }

    // 阻止玩家取出输出槽位的物品
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // 比较当前槽位的索引和工作台输出槽位的索引
        if (slotIndex == this.getCraftingResultSlotIndex()) {
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    // 关闭GUI时，设置假玩家的合成动作和配方
    @Override
    public void onClosed(PlayerEntity player) {
        //如果没有给假玩家指定合成配方，结束方法
        if (inputInventory.isEmpty()) {
            return;
        }
        //修改假玩家的3x3合成配方
        Item[] items = new Item[9];
        for (int i = 0; i < inputInventory.size(); i++) {
            items[i] = inputInventory.getStack(i).getItem();
        }
        // 设置假玩家合成动作
        setCraftAction(items, FakePlayerActionInterface.getManager(fakePlayer));
        // 关闭GUI后，使用父类的方法让物品回到玩家背包
        super.onClosed(player);
        // 提示启用Ctrl+Q合成修复
        PlayerActionCommand.promptToEnableCtrlQCraftingFix(context.getSource());
    }

    // 设置假玩家合成动作
    private void setCraftAction(Item[] items, FakePlayerActionManager actionManager) {
        // TODO 使用循环优化
        // 如果能在2x2合成格中合成，优先使用2x2
        if (items[0] == Items.AIR && items[1] == Items.AIR && items[2] == Items.AIR
                && items[5] == Items.AIR && items[8] == Items.AIR) {
            actionManager.setAction(FakePlayerAction.INVENTORY_CRAFT, new InventoryCraftData(new ItemMatcher[]{
                    new ItemMatcher(items[3]), new ItemMatcher(items[4]), new ItemMatcher(items[6]), new ItemMatcher(items[7])}));
        } else if (items[0] == Items.AIR && items[3] == Items.AIR && items[6] == Items.AIR
                && items[7] == Items.AIR && items[8] == Items.AIR) {
            actionManager.setAction(FakePlayerAction.INVENTORY_CRAFT, new InventoryCraftData(new ItemMatcher[]{
                    new ItemMatcher(items[1]), new ItemMatcher(items[2]), new ItemMatcher(items[4]), new ItemMatcher(items[5])}));
        } else if (items[2] == Items.AIR && items[5] == Items.AIR && items[6] == Items.AIR
                && items[7] == Items.AIR && items[8] == Items.AIR) {
            actionManager.setAction(FakePlayerAction.INVENTORY_CRAFT, new InventoryCraftData(new ItemMatcher[]{
                    new ItemMatcher(items[0]), new ItemMatcher(items[1]), new ItemMatcher(items[3]), new ItemMatcher(items[4])}));
        } else if (items[0] == Items.AIR && items[1] == Items.AIR && items[2] == Items.AIR
                && items[3] == Items.AIR && items[6] == Items.AIR) {
            actionManager.setAction(FakePlayerAction.INVENTORY_CRAFT, new InventoryCraftData(new ItemMatcher[]{
                    new ItemMatcher(items[4]), new ItemMatcher(items[5]), new ItemMatcher(items[7]), new ItemMatcher(items[8])}));
        } else {
            //将假玩家动作设置为3x3合成
            ItemMatcher[] itemMatchersArr = new ItemMatcher[9];
            for (int i = 0; i < itemMatchersArr.length; i++) {
                itemMatchersArr[i] = new ItemMatcher(items[i]);
            }
            actionManager.setAction(FakePlayerAction.CRAFTING_TABLE_CRAFT, new CraftingTableCraftData(itemMatchersArr));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}

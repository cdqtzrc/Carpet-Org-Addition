package org.carpetorgaddition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.carpetorgaddition.exception.InfiniteLoopException;
import org.carpetorgaddition.util.fakeplayer.actiondata.StonecuttingData;

public class FakePlayerStonecutting {
    private FakePlayerStonecutting() {
    }

    public static void stonecutting(StonecuttingData stonecuttingData, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof StonecutterScreenHandler stonecutterScreenHandler) {
            // 获取要切割的物品和按钮的索引
            Item item = stonecuttingData.getItem();
            int buttonIndex = stonecuttingData.getButton();
            // 定义变量记录成功完成合成的次数
            int craftCount = 0;
            // 用于循环次数过多时抛出异常结束循环
            int loopCount = 0;
            while (true) {
                loopCount++;
                if (loopCount > 1000) {
                    throw new InfiniteLoopException();
                }
                // 定义变量记录是否需要遍历物品栏
                boolean needToTraverseInventory = true;
                // 获取切石机输入槽对象
                Slot inputSlot = stonecutterScreenHandler.getSlot(0);
                // 判断切石机输入槽是否有物品
                if (inputSlot.hasStack()) {
                    // 如果有物品，并且是指定物品，设置不需要遍历物品栏
                    ItemStack itemStack = inputSlot.getStack();
                    if (itemStack.isOf(item)) {
                        needToTraverseInventory = false;
                    } else {
                        // 如果不是指定物品，丢出该物品
                        FakePlayerUtils.throwItem(stonecutterScreenHandler, 0, fakePlayer);
                    }
                }
                // 如果需要遍历物品栏
                if (needToTraverseInventory) {
                    // 尝试从物品栏中找到需要的物品
                    for (int index = 2; index < stonecutterScreenHandler.slots.size(); index++) {
                        // 如果找到，移动到切石机输入槽，然后结束循环
                        if (stonecutterScreenHandler.getSlot(index).getStack().isOf(item)) {
                            FakePlayerUtils.quickMove(stonecutterScreenHandler, index, fakePlayer);
                            break;
                        }
                        // 如果遍历完物品栏还没有找到指定物品，认为物品栏中没有该物品，结束方法
                        if (index == stonecutterScreenHandler.slots.size() - 1) {
                            return;
                        }
                    }
                }
                // 模拟单击切石机按钮
                stonecutterScreenHandler.onButtonClick(fakePlayer, buttonIndex);
                // 获取切石机输出槽对象
                Slot outputSlot = stonecutterScreenHandler.getSlot(1);
                // 如果输出槽有物品
                if (outputSlot.hasStack()) {
                    // 丢出该物品
                    FakePlayerUtils.loopThrowItem(stonecutterScreenHandler, 1, fakePlayer);
                    craftCount++;
                    // 限制每个游戏刻合成次数
                    int ruleValue = CarpetOrgAdditionSettings.fakePlayerMaxCraftCount;
                    if (ruleValue > 0 && craftCount >= CarpetOrgAdditionSettings.fakePlayerMaxCraftCount) {
                        return;
                    }
                } else {
                    // 否则，认为前面的操作有误，停止合成，结束方法
                    FakePlayerUtils.stopAction(fakePlayer.getCommandSource(), fakePlayer,
                            "carpet.commands.playerAction.stone_cutting");
                    return;
                }
            }
        }
    }
}

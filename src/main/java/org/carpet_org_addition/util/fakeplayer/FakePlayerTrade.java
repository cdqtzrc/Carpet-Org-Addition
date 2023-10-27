package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.InfiniteLoopException;

public class FakePlayerTrade {
    //假玩家交易
    public static void trade(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        //获取按钮的索引，减去1
        int index = IntegerArgumentType.getInteger(context, "index") - 1;
        //判断当前打开的GUI是否为交易界面
        if (fakePlayer.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
            //判断按钮索引是否越界
            if (merchantScreenHandler.getRecipes().size() <= index) {
                FakePlayerUtils.stopAction(context.getSource(), fakePlayer, "carpet.commands.playerTools.action.trade");
                return;
            }
            int loopCount = 0;
            //如果村民无限交易未启用，则只循环一次
            do {
                loopCount++;
                if (loopCount > 1000) {
                    //无限循环异常
                    throw new InfiniteLoopException();
                }
                //如果当前交易以锁定，直接结束方法
                if (merchantScreenHandler.getRecipes().get(index).isDisabled()) {
                    return;
                }
                //模拟单击左侧交易选项的按钮
                merchantScreenHandler.setRecipeIndex(index);
                merchantScreenHandler.switchTo(index);
                //判断输出槽是否有物品，如果有，丢出物品，否则结束方法
                if (merchantScreenHandler.getSlot(2).hasStack()) {
                    FakePlayerUtils.loopThrowItem(merchantScreenHandler, 2, fakePlayer);
                } else {
                    // 如果假玩家背包已满，且村民交易槽位上还有物品，并且交易槽上的物品不足以交易物品
                    // 这时由于原版漏洞，假玩家单击交易选项按钮时，物品不会自动填充到交易槽位
                    // 这虽然不至于导致游戏进入死循环，但是假玩家交易会卡住，所以但无法获取输出物品时，在结束方法前要丢出交易槽位的物品
                    if (inventoryIsFull(fakePlayer)) {
                        Slot slot0 = merchantScreenHandler.getSlot(0);
                        if (slot0.hasStack()) {
                            FakePlayerUtils.quickMove(merchantScreenHandler, 0, fakePlayer);
                            if (slot0.hasStack()) {
                                FakePlayerUtils.throwItem(merchantScreenHandler, 0, fakePlayer);
                            }
                        }
                        Slot slot1 = merchantScreenHandler.getSlot(1);
                        if (slot1.hasStack()) {
                            FakePlayerUtils.throwItem(merchantScreenHandler, 1, fakePlayer);
                            if (slot1.hasStack()) {
                                FakePlayerUtils.throwItem(merchantScreenHandler, 1, fakePlayer);
                            }
                        }
                    } else {
                        return;
                    }
                }
            } while (CarpetOrgAdditionSettings.villagerInfiniteTrade);
        }
    }

    // 判断玩家物品栏是否已满
    private static boolean inventoryIsFull(EntityPlayerMPFake fakePlayer) {
        for (ItemStack itemStack : fakePlayer.getInventory().main) {
            if (itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

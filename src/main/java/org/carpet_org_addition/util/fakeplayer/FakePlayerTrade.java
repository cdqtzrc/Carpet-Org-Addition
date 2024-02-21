package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.Merchant;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.InfiniteLoopException;
import org.carpet_org_addition.mixin.rule.MerchantScreenHandlerAccessor;
import org.carpet_org_addition.util.StringUtils;
import org.carpet_org_addition.util.helpers.Counter;

public class FakePlayerTrade {
    //假玩家交易
    public static void trade(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer, boolean voidTrade) {
        //获取按钮的索引，减去1
        int index = IntegerArgumentType.getInteger(context, "index") - 1;
        //判断当前打开的GUI是否为交易界面
        if (fakePlayer.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
            FakePlayerActionInterface fakePlayerActionInterface = FakePlayerActionInterface.getInstance(fakePlayer);
            // 获取计数器，记录村民距离上次被加载的时间是否超过了5游戏刻（区块卸载后村民似乎不会立即卸载）
            Counter<FakePlayerActionType> tickCounter = fakePlayerActionInterface.getTickCounter();
            if (voidTrade) {
                // 获取正在接受交易的村民
                MerchantScreenHandlerAccessor accessor = (MerchantScreenHandlerAccessor) merchantScreenHandler;
                Merchant merchant = accessor.getMerchant();
                if (merchant instanceof MerchantEntity merchantEntity) {
                    ChunkPos chunkPos = merchantEntity.getChunkPos();
                    // 如果村民位于已加载区块内，直接结束方法
                    if (merchantEntity.getWorld().isChunkLoaded(chunkPos.x, chunkPos.z)) {
                        // 村民位于加载区块内，重置计数器
                        tickCounter.set(FakePlayerActionType.VOID_TRADE, 5);
                        return;
                    }
                }
                // 检查计数器是否归零
                if (tickCounter.hasElement(FakePlayerActionType.VOID_TRADE)) {
                    // 如果没有归零，计数器递减，然后结束方法
                    tickCounter.decrement(FakePlayerActionType.VOID_TRADE);
                    return;
                } else {
                    // 如果归零，重置计数器
                    tickCounter.set(FakePlayerActionType.VOID_TRADE, 5);
                }
            }
            //判断按钮索引是否越界
            if (merchantScreenHandler.getRecipes().size() <= index) {
                FakePlayerUtils.stopAction(context.getSource(), fakePlayer,
                        "carpet.commands.playerAction.trade");
                return;
            }
            int loopCount = 0;
            try {
                //如果村民无限交易未启用，则只循环一次
                do {
                    loopCount++;
                    if (loopCount > 1000) {
                        //无限循环异常
                        throw new InfiniteLoopException(StringUtils.getPlayerName(fakePlayer)
                                + "在与村民交易时循环了" + loopCount + "次("
                                + StringUtils.getDimensionId(fakePlayer.getWorld()) + ":["
                                + StringUtils.getBlockPosString(fakePlayer.getBlockPos()) + "])");
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
                        // 这虽然不至于导致游戏进入死循环，但是假玩家交易会卡住，所以当无法获取输出物品时，要先尝试丢出交易槽位的物品，如果仍然无法获取输出物品才结束方法
                        if (inventoryIsFull(fakePlayer)) {
                            // 第一个交易槽位
                            Slot slot0 = merchantScreenHandler.getSlot(0);
                            if (slot0.hasStack()) {
                                FakePlayerUtils.quickMove(merchantScreenHandler, 0, fakePlayer);
                                if (slot0.hasStack()) {
                                    FakePlayerUtils.throwItem(merchantScreenHandler, 0, fakePlayer);
                                }
                            }
                            // 第二个交易槽位
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
            } finally {
                if (voidTrade) {
                    // 如果是虚空交易，交易完毕后关闭交易GUI
                    fakePlayer.closeHandledScreen();
                }
            }
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

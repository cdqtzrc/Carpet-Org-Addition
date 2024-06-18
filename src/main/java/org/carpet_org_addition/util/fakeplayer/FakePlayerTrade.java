package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.InfiniteLoopException;
import org.carpet_org_addition.mixin.rule.MerchantScreenHandlerAccessor;
import org.carpet_org_addition.util.fakeplayer.actiondata.TradeData;
import org.carpet_org_addition.util.helpers.SingleThingCounter;

import java.util.UUID;

public class FakePlayerTrade {
    //假玩家交易
    public static void trade(TradeData tradeData, EntityPlayerMPFake fakePlayer) {
        //获取按钮的索引
        int index = tradeData.getIndex();
        //判断当前打开的GUI是否为交易界面
        if (fakePlayer.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
            boolean voidTrade = tradeData.isVoidTrade();
            // 获取计数器，记录村民距离上次被加载的时间是否超过了5游戏刻（区块卸载后村民似乎不会立即卸载）
            SingleThingCounter timer = tradeData.getTimer();
            if (voidTrade) {
                // 获取正在接受交易的村民
                MerchantScreenHandlerAccessor accessor = (MerchantScreenHandlerAccessor) merchantScreenHandler;
                Merchant merchant = accessor.getMerchant();
                if (merchant instanceof MerchantEntity merchantEntity) {
                    // 是否应该等待区块卸载
                    if (shouldWait(merchantEntity)) {
                        timer.set(5);
                        return;
                    }
                }
                // 检查计数器是否归零
                if (timer.nonZero()) {
                    // 如果没有归零，计数器递减，然后结束方法
                    timer.decrement();
                    return;
                } else {
                    // 如果归零，重置计数器，然后开始交易
                    timer.set(5);
                }
            }
            ServerCommandSource source = fakePlayer.getCommandSource();
            // 判断按钮索引是否越界
            if (merchantScreenHandler.getRecipes().size() <= index) {
                FakePlayerUtils.stopAction(source, fakePlayer,
                        "carpet.commands.playerAction.trade");
                return;
            }
            // 尝试交易物品
            tryTrade(source, fakePlayer, merchantScreenHandler, index, voidTrade);
            if (voidTrade) {
                // 如果是虚空交易，交易完毕后关闭交易GUI
                fakePlayer.closeHandledScreen();
            }
        }
    }

    // 尝试交易物品
    private static void tryTrade(ServerCommandSource source, EntityPlayerMPFake fakePlayer,
                                 MerchantScreenHandler merchantScreenHandler, int index, boolean voidTrade) {
        InfiniteLoopException exception = new InfiniteLoopException();
        //如果村民无限交易未启用，则只循环一次
        do {
            exception.checkLoopCount();
            //如果当前交易以锁定，直接结束方法
            TradeOffer tradeOffer = merchantScreenHandler.getRecipes().get(index);
            if (tradeOffer.isDisabled()) {
                return;
            }
            // 选择要交易物品的索引
            merchantScreenHandler.setRecipeIndex(index);
            // 填充交易槽位
            if (switchItem(fakePlayer, merchantScreenHandler, tradeOffer)) {
                // 判断输出槽是否有物品，如果有，丢出物品，否则停止交易，结束方法
                if (merchantScreenHandler.getSlot(2).hasStack()) {
                    FakePlayerUtils.loopThrowItem(merchantScreenHandler, 2, fakePlayer);
                } else {
                    FakePlayerUtils.stopAction(source, fakePlayer, "carpet.commands.playerAction.trade");
                    return;
                }
            } else {
                // 除非假玩家物品栏内已经没有足够的物品用来交易，否则填充交易槽位不会失败
                return;
            }
            // 如果启用了村民无限交易或当时为虚空交易，则尽可能完成所有交易
        } while (voidTrade || CarpetOrgAdditionSettings.villagerInfiniteTrade);
    }

    // 选择物品
    private static boolean switchItem(EntityPlayerMPFake fakePlayer, MerchantScreenHandler merchantScreenHandler, TradeOffer tradeOffer) {
        // 获取第一个交易物品
        ItemStack firstBuyItem = tradeOffer.getDisplayedFirstBuyItem();// 0索引
        // 获取第二个交易物品
        ItemStack secondBuyItem = tradeOffer.getDisplayedSecondBuyItem();// 1索引
        DefaultedList<Slot> list = merchantScreenHandler.slots;
        return fillTradeSlot(fakePlayer, merchantScreenHandler, firstBuyItem, 0, list)
                && fillTradeSlot(fakePlayer, merchantScreenHandler, secondBuyItem, 1, list);
    }

    /**
     * 填充交易槽位
     *
     * @param fakePlayer            要交易物品的假玩家
     * @param merchantScreenHandler 假玩家当前打开的交易GUI
     * @param buyItem               村民的交易物品
     * @param slotIndex             第几个交易物品
     * @param list                  当前交易界面的物品栏
     * @return 槽位上的物品是否已经足够参与交易
     */
    private static boolean fillTradeSlot(EntityPlayerMPFake fakePlayer, MerchantScreenHandler merchantScreenHandler,
                                         ItemStack buyItem, int slotIndex, DefaultedList<Slot> list) {
        // 获取交易槽上的物品
        ItemStack slotItem = merchantScreenHandler.getSlot(slotIndex).getStack();
        // 如果交易槽上的物品不是需要的物品，就丢弃槽位中的物品
        if (!slotItem.isOf(buyItem.getItem())) {
            FakePlayerUtils.throwItem(merchantScreenHandler, slotIndex, fakePlayer);
        }
        // 如果交易所需的物品为空，或者槽位的物品已经是所需的物品，直接跳过该物品
        if (buyItem.isEmpty() || slotItemCanTrade(slotItem, buyItem)) {
            return true;
        }
        // 将物品移动到交易槽位
        for (int index = 3; index < list.size(); index++) {
            // 获取当前槽位上的物品
            ItemStack itemStack = list.get(index).getStack();
            // 如果交易槽位上有物品，就将当前物品与交易槽上的物品比较，同时比较物品NBT
            // 否则，将当前物品直接与村民需要的交易物品进行比较，不比较NBT
            if (slotItem.isEmpty() ? buyItem.isOf(itemStack.getItem()) : ItemStack.areItemsAndComponentsEqual(slotItem, itemStack)) {
                // 如果匹配，将当前物品移动到交易槽位
                if (FakePlayerUtils.withKeepPickupAndMoveItemStack(merchantScreenHandler, index, slotIndex, fakePlayer)) {
                    // 如果假玩家填充交易槽后光标上有剩余的物品，将剩余的物品放回原槽位
                    if (!merchantScreenHandler.getCursorStack().isEmpty()) {
                        FakePlayerUtils.pickupCursorStack(merchantScreenHandler, index, fakePlayer);
                    }
                    slotItem = merchantScreenHandler.getSlot(slotIndex).getStack();
                    // 交易槽位物品的地址值可能发生变化，不能直接使用slotItem对象，需要重新获取
                    if (slotItemCanTrade(slotItem, buyItem)) {
                        return true;
                    }
                }
            }
        }
        // 假玩家身上没有足够的物品用来交易，返回false
        return false;
    }

    // 是否应该等待区块卸载
    private static boolean shouldWait(MerchantEntity merchant) {
        // 如果村民所在区块没有被加载，可以交易
        ChunkPos chunkPos = merchant.getChunkPos();
        if (merchant.getWorld().isChunkLoaded(chunkPos.x, chunkPos.z)) {
            // 检查村民是否存在于任何一个维度，如果不存在，可以交易
            UUID uuid = merchant.getUuid();
            MinecraftServer server = merchant.getServer();
            if (server == null) {
                return true;
            }
            for (ServerWorld world : server.getWorlds()) {
                if (world.getEntity(uuid) == null) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    // 检查槽位上的物品是否可以交易
    private static boolean slotItemCanTrade(ItemStack slotItem, ItemStack tradeItem) {
        return (slotItem.getCount() >= tradeItem.getCount()) || slotItem.getCount() >= slotItem.getMaxCount();
    }
}

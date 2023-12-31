package org.carpet_org_addition.util.fakeplayer;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.InfiniteLoopException;
import org.carpet_org_addition.util.StringUtils;
import org.carpet_org_addition.util.helpers.ItemMatcher;

public class FakePlayerCraft {
    //最大循环次数
    private static final int MAX_LOOP_COUNT = 1000;

    private FakePlayerCraft() {
    }

    //假玩家自动合成物品（单个材料）  例：一个铁块合成九个铁锭
    public static void craftOne(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer, ItemMatcher[] items) {
        craft2x2(context, fakePlayer, items);
    }

    //自动合成物品（4个相同材料）
    public static void craftFour(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer, ItemMatcher[] items) {
        craft2x2(context, fakePlayer, items);
    }

    //假玩家自动合成物品（九个相同的材料）  例：九个铁锭合成一个铁块
    public static void craftNine(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer, ItemMatcher[] items) {
        craft3x3(context, fakePlayer, items);
    }


    //合成自定义物品，3x3
    public static void craft3x3(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer, ItemMatcher[] items) {
        if (fakePlayer.currentScreenHandler instanceof CraftingScreenHandler craftingScreenHandler) {
            int loopCount = 0;
            do {
                loopCount++;
                //避免游戏进入死循环
                if (loopCount > MAX_LOOP_COUNT) {
                    throw new InfiniteLoopException(StringUtils.getPlayerName(fakePlayer) + "在合成物品时循环了"
                            + loopCount + "次(" + StringUtils.getDimensionId(fakePlayer.getWorld()) + ":["
                            + StringUtils.getBlockPosString(fakePlayer.getBlockPos()) + "])");
                }
                //定义变量记录找到正确合成材料的次数
                int successCount = 0;
                //依次获取每一个合成材料和遍历合成格
                for (int index = 1; index <= 9; index++) {
                    //依次获取每一个合成材料
                    ItemMatcher itemMatcher = items[index - 1];
                    Slot slot = craftingScreenHandler.getSlot(index);
                    //如果合成格的指定槽位不是所需要合成材料，则丢出该物品
                    if (slot.hasStack()) {
                        ItemStack itemStack = slot.getStack();
                        if (itemMatcher.test(itemStack)) {
                            //合成表格上已经有正确的合成材料，找到正确的合成材料次数自增
                            successCount++;
                        } else {
                            FakePlayerUtils.throwItem(craftingScreenHandler, index, fakePlayer);
                        }
                    } else {
                        //如果指定合成材料是空气，则不需要遍历物品栏，直接跳过该物品，并增加找到正确合成材料的次数
                        if (itemMatcher.isEmpty()) {
                            successCount++;
                            continue;
                        }
                        //遍历物品栏找到需要的合成材料
                        int size = craftingScreenHandler.slots.size();
                        for (int inventoryIndex = 10; inventoryIndex < size; inventoryIndex++) {
                            ItemStack itemStack = craftingScreenHandler.getSlot(inventoryIndex).getStack();
                            if (itemMatcher.test(itemStack)) {
                                // 如果假玩家合成保留物品启用，并且该物品的数量为1，并且该物品的最大堆叠数大于1
                                // 认为这个物品需要保留，结束本轮循环
                                if (CarpetOrgAdditionSettings.fakePlayerCraftKeepItem && itemStack.getCount() == 1
                                        && itemStack.getMaxCount() > 1) {
                                    continue;
                                }
                                // 光标拾取和移动物品
                                FakePlayerUtils.pickupAndMoveItemStack(craftingScreenHandler,
                                        inventoryIndex, index, fakePlayer);
                                // 找到正确合成材料的次数自增
                                successCount++;
                                break;
                            }
                            //合成格没有遍历完毕，继续查找下一个合成材料
                            //合成格遍历完毕，并且物品栏找不到需要的合成材料，结束方法
                            if (index == 9 && inventoryIndex == size - 1) {
                                return;
                            }
                        }
                    }
                }
                //正确材料找到的次数等于9说明全部找到，可以合成
                if (successCount == 9) {
                    if (craftingScreenHandler.getSlot(0).hasStack()) {
                        //合成步骤正确，输出物品
                        FakePlayerUtils.throwItem(craftingScreenHandler, 0, fakePlayer);
                    } else {
                        //如果没有输出物品，说明之前的合成步骤有误，停止合成
                        stopCraftAction(context.getSource(), fakePlayer);
                        return;
                    }
                } else {
                    if (successCount > 9) {
                        // 找到正确合成材料的次数不应该大于合成槽位数量，如果超过了说明前面的操作出了问题，抛出异常结束方法
                        throw new IllegalStateException(fakePlayer.getName().getString() + "找到正确合成材料的次数为"
                                + successCount + "，正常不应该超过9");
                    }
                    //遍历完物品栏后，如果找到正确合成材料小于9，认为玩家身上没有足够的合成材料了，直接结束方法
                    return;
                }
            } while (CarpetSettings.ctrlQCraftingFix);
        }
    }

    //合成自定义物品，2x2
    public static void craft2x2(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer, ItemMatcher[] items) {
        PlayerScreenHandler playerScreenHandler = fakePlayer.playerScreenHandler;
        //定义变量记录循环次数
        int loopCount = 0;
        do {
            loopCount++;
            //如果循环次数过多，认为游戏进入了死循环，直接抛出异常强行结束方法
            if (loopCount > MAX_LOOP_COUNT) {
                throw new InfiniteLoopException(StringUtils.getPlayerName(fakePlayer) + "在合成物品时循环了"
                        + loopCount + "次(" + StringUtils.getDimensionId(fakePlayer.getWorld()) + ":["
                        + StringUtils.getBlockPosString(fakePlayer.getBlockPos()) + "])");
            }
            // 定义变量记录找到正确合成材料的次数
            int successCount = 0;
            //遍历4x4合成格
            for (int craftIndex = 1; craftIndex <= 4; craftIndex++) {
                //获取每一个合成材料
                ItemMatcher itemMatcher = items[craftIndex - 1];
                Slot slot = playerScreenHandler.getSlot(craftIndex);
                //检查合成格上是否已经有物品
                if (slot.hasStack()) {
                    //如果有并且物品是正确的合成材料，直接结束本轮循环，即跳过该物品
                    if (itemMatcher.test(slot.getStack())) {
                        successCount++;
                        continue;
                    } else {
                        //如果不是，丢出该物品
                        FakePlayerUtils.throwItem(playerScreenHandler, craftIndex, fakePlayer);
                    }
                } else if (itemMatcher.isEmpty()) {
                    successCount++;
                    continue;
                }
                int size = playerScreenHandler.slots.size();
                // 遍历物品栏，包括盔甲槽和副手槽
                for (int inventoryIndex = 5; inventoryIndex < size; inventoryIndex++) {
                    ItemStack itemStack = playerScreenHandler.getSlot(inventoryIndex).getStack();
                    //如果该槽位是正确的合成材料，将该物品移动到合成格，然后增加找到正确合成材料的次数
                    if (itemMatcher.test(itemStack)) {
                        // 如果假玩家合成保留物品启用，并且该物品的数量为1，并且该物品的最大堆叠数大于1
                        // 认为这个物品需要保留，结束本轮循环
                        if (CarpetOrgAdditionSettings.fakePlayerCraftKeepItem && itemStack.getCount() == 1
                                && itemStack.getMaxCount() > 1) {
                            continue;
                        }
                        FakePlayerUtils.pickupAndMoveItemStack(playerScreenHandler,
                                inventoryIndex, craftIndex, fakePlayer);
                        successCount++;
                        break;
                    }
                    //如果遍历完物品栏还没有找到指定物品，认为玩家身上已经没有该物品，结束方法
                    if (craftIndex == 4 && inventoryIndex == size - 1) {
                        return;
                    }
                }
            }
            //如果找到正确合成材料的次数为4，认为找到了所有的合成材料，尝试输出物品
            if (successCount == 4) {
                //如果输出槽有物品，则丢出该物品
                if (playerScreenHandler.getSlot(0).hasStack()) {
                    FakePlayerUtils.throwItem(playerScreenHandler, 0, fakePlayer);
                } else {
                    //如果输出槽没有物品，认为前面的合成操作有误，停止合成
                    stopCraftAction(context.getSource(), fakePlayer);
                    return;
                }
            } else {
                if (successCount > 4) {
                    // 找到正确合成材料的次数不应该大于合成槽位数量，如果超过了说明前面的操作出了问题，抛出异常结束方法
                    throw new IllegalStateException(fakePlayer.getName().getString() + "找到正确合成材料的次数为"
                            + successCount + "，正常不应该超过4");
                }
                //遍历完物品栏后，如果没有找到足够多的合成材料，认为玩家身上没有足够的合成材料了，直接结束方法
                return;
            }
        } while (CarpetSettings.ctrlQCraftingFix);
    }

    /**
     * 假玩家停止物品合成操作，并广播停止合成的消息
     *
     * @param source       发送消息的消息源
     * @param playerMPFake 需要停止操作的假玩家
     */
    private static void stopCraftAction(ServerCommandSource source, EntityPlayerMPFake playerMPFake) {
        FakePlayerUtils.stopAction(source, playerMPFake, "carpet.commands.playerTools.action.craft");
    }
}

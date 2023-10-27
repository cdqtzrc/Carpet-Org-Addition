package org.carpet_org_addition.util.fakeplayer;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.exception.InfiniteLoopException;

public class FakePlayerCraft {
    //最大循环次数
    private static final int MAX_LOOP_COUNT = 1000;

    private FakePlayerCraft() {
    }

    //假玩家自动合成物品（单个材料）  例：一个铁块合成九个铁锭
    public static void craftOne(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof CraftingScreenHandler craftingScreenHandler) {
            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
            //如果合成材料是空气，停止合成，然后直接结束方法
            if (item == Items.AIR) {
                stopCraftAction(context.getSource(), fakePlayer);
                return;
            }
            int loopCount = 0;
            do {
                loopCount++;
                //循环次数过多，认为游戏进入了死循环，通过抛出异常结束循环
                if (loopCount > MAX_LOOP_COUNT) {
                    throw new InfiniteLoopException();
                }
                //是否需要遍历物品栏
                boolean flag = false;
                //遍历工作台合成格
                for (int index = 1; index <= 9; index++) {
                    ItemStack itemStack = craftingScreenHandler.getSlot(index).getStack();
                    if (index == 1) {
                        //检测1索引上是否是指定的物品
                        if (!itemStack.isOf(item)) {
                            //如果不是，把物品丢出去
                            FakePlayerUtils.throwItem(craftingScreenHandler, 1, fakePlayer);
                        } else {
                            //否则，将标记设置为true，表示不需要遍历物品栏
                            flag = true;
                        }
                        continue;
                    }
                    //检测第一格以外的其他格子上是否有物品，如果有就丢出去
                    if (craftingScreenHandler.slots.get(index).hasStack()) {
                        FakePlayerUtils.throwItem(craftingScreenHandler, index, fakePlayer);
                    }
                }
                int size = craftingScreenHandler.slots.size();
                if (!flag) {
                    //遍历玩家物品栏
                    for (int index = 10; index < size; index++) {
                        ItemStack itemStack = craftingScreenHandler.getSlot(index).getStack();
                        if (itemStack.isOf(item)) {
                            //找到指定的物品，移动到合成格
                            craftingScreenHandler.quickMove(fakePlayer, index);
                            break;
                        }
                        if (index == size - 1) {
                            //如果遍历完物品栏，还没有找到需要的物品，就认为物品栏已经没有该物品，然后结束方法
                            return;
                        }
                    }
                }
                if (craftingScreenHandler.getSlot(0).hasStack()) {
                    FakePlayerUtils.throwItem(craftingScreenHandler, 0, fakePlayer);
                } else {
                    stopCraftAction(context.getSource(), fakePlayer);
                    //如果输出槽没有物品，及时结束方法，防止进入无法跳出的死循环
                    return;
                }
            } while (CarpetSettings.ctrlQCraftingFix);
        }
    }

    //假玩家自动合成物品（九个相同的材料）  例：九个铁锭合成一个铁块
    public static void craftNine(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof CraftingScreenHandler craftingScreenHandler) {
            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
            //如果合成材料是空气，停止合成，然后直接结束方法
            if (item == Items.AIR) {
                stopCraftAction(context.getSource(), fakePlayer);
                return;
            }
            int loopCount = 0;
            do {
                loopCount++;
                if (loopCount > MAX_LOOP_COUNT) {
                    //循环次数过多，认为游戏进入了死循环，通过抛出异常结束循环
                    throw new InfiniteLoopException();
                }
                //是否可以取出合成物品
                boolean canCraft = false;
                //是否需要遍历物品栏
                boolean flag = false;
                //遍历工作台合成格
                for (int index = 1; index <= 9; index++) {
                    ItemStack itemStack = craftingScreenHandler.getSlot(index).getStack();
                    //检测每一个合成格上是否是指定的物品
                    if (!craftingScreenHandler.getSlot(index).hasStack()) {
                        //如果该合成格上没有物品，设置需要遍历玩家物品栏，结束本轮循环
                        flag = true;
                        //因为该格子没有物品，所以这里一定不是所需要的合成材料，没有必要继续进行判断（如果合成材料是空气，代码也不会执行到这里，在进入循环前会对合成材料进行检查
                        continue;
                    }
                    //判断该格子是否为指定的合成材料
                    if (!itemStack.isOf(item)) {
                        //如果不是，把物品丢出去，再设置需要遍历玩家物品栏
                        FakePlayerUtils.throwItem(craftingScreenHandler, index, fakePlayer);
                        flag = true;
                        //该格子不是所需要的物品，不能设置可以合成
                        continue;
                    }
                    //遍历完毕，设置可以合成物品，如果仍然有些材料位置不正确，在遍历物品栏时会重新设置为不可合成
                    //直到不再需要遍历物品栏，才真正表示所有材料位置正确
                    canCraft = true;
                }
                /*
                  现在需要用铁锭合成铁块
                  假设前五次遍历时都正确，这时已经执行到设置可以合成物品
                  第六次往后都为空，设置需要遍历玩家物品栏
                  此时工作台合成格状态如下：
                  -----------------------------
                  铁锭  铁锭  铁锭
                  铁锭  铁锭  空气   输出：空
                  空气  空气  空气
                  -----------------------------
                  这时候已经设置为可以合成物品，并且需要遍历玩家物品栏
                  然后需要遍历玩家物品栏
                  假设玩家物品栏有一个合成材料物品
                  将这个物品移到合成格
                  此时工作台合成格状态如下：
                  -----------------------------
                  铁锭  铁锭  铁锭
                  铁锭  铁锭  铁锭   输出：铁栅栏
                  空气  空气  空气
                  -----------------------------
                  现在输出物品是铁栅栏，但是期望合成物品是铁块
                  如果遍历物品栏时没有设置不可合成
                  那么合成时输出物品就是铁栅栏
                  但是如果设置了不可合成
                  就不会合成物品，而是进入下一次循环，直到不再需要遍历物品栏
                 */
                if (flag) {
                    //需要遍历物品栏，表示合成格物品一定不正确，设置不能合成
                    canCraft = false;
                    int size = craftingScreenHandler.slots.size();
                    //遍历玩家物品栏
                    for (int index = 10; index < size; index++) {
                        ItemStack itemStack = craftingScreenHandler.getSlot(index).getStack();
                        if (itemStack.isOf(item)) {
                            //找到指定的物品，移动到合成格
                            craftingScreenHandler.quickMove(fakePlayer, index);
                            break;
                        }
                        if (index == size - 1) {
                            //如果遍历完物品栏，还没有找到需要的物品，就认为玩家物品栏已经没有该物品，然后结束方法
                            return;
                        }
                    }
                }
                if (canCraft) {
                    //如果输出槽有物品，就丢出该物品
                    if (craftingScreenHandler.getSlot(0).hasStack()) {
                        FakePlayerUtils.throwItem(craftingScreenHandler, 0, fakePlayer);
                    } else {
                        stopCraftAction(context.getSource(), fakePlayer);
                        //如果输出槽没有物品，说明该配方不能合成任何物品，这时候要及时结束方法，避免游戏进入无法跳出的死循环
                        return;
                    }
                }
            } while (CarpetSettings.ctrlQCraftingFix);
        }
    }

    //自动合成物品（4个相同材料）
    public static void craftFour(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        //如果合成材料是空气，停止合成，然后直接结束方法
        if (item == Items.AIR) {
            stopCraftAction(context.getSource(), fakePlayer);
            return;
        }
        int loopCount = 0;
        do {
            loopCount++;
            if (loopCount > MAX_LOOP_COUNT) {
                //循环次数过多，认为游戏进入了死循环，通过抛出异常结束循环
                throw new InfiniteLoopException();
            }
            //获取玩家的物品栏GUI对象
            PlayerScreenHandler playerScreenHandler = fakePlayer.playerScreenHandler;
            //获取要合成的物品
            //定义遍历记录合成槽的第几个槽位需要交换物品
            int swap = 0;
            //是否可以合成物品
            boolean canCraft = false;
            //是否需要遍历物品栏
            boolean requireInventory = false;
            //是否需要遍历快捷栏以外的物品栏
            boolean requireMainInventory = false;
            //遍历合成槽
            for (int index = 1; index <= 4; index++) {
                ItemStack itemStack = playerScreenHandler.getSlot(index).getStack();
                //如果一个槽位上不是指定物品，就丢出该物品，设置该索引的槽位需要交互物品，设置需要遍历快捷栏，结束本轮循环
                if (!itemStack.isOf(item)) {
                    FakePlayerUtils.throwItem(playerScreenHandler, index, fakePlayer);
                    swap = index;
                    requireInventory = true;
                    continue;
                }
                //设置可以合成物品，如果该槽位上不是指定物品，代码不会执行到这里
                canCraft = true;
            }
            //减去副手的大小
            int size = playerScreenHandler.slots.size() - 1;
            //如果需要遍历快捷栏
            if (requireInventory) {
                canCraft = false;
                //遍历快捷栏，尝试找到与指定物品相符的物品
                for (int shortcutIndex = 9 + 27; shortcutIndex < size; shortcutIndex++) {
                    //获取快捷栏上的每一个物品对象
                    ItemStack itemStack = playerScreenHandler.getSlot(shortcutIndex).getStack();
                    //如果该槽位是指定物品，就把这个物品和合成槽中需要交互位置的物品交互位置，然后结束循环
                    if (itemStack.isOf(item)) {
                        FakePlayerUtils.swapItem(playerScreenHandler, swap, shortcutIndex - 36, fakePlayer);
                        break;
                    } else {
                        //否则，如果遍历完快捷栏还没有找到指定物品，设置需要遍历主物品栏
                        if (shortcutIndex == size - 1) {
                            requireMainInventory = true;
                        }
                        //如过该槽位没有物品，结束本轮循环
                        if (!playerScreenHandler.getSlot(shortcutIndex).hasStack()) {
                            continue;
                        }
                        //如果有物品，将物品移动到主物品栏，如果不能移动，就丢出该物品
                        playerScreenHandler.quickMove(fakePlayer, shortcutIndex);
                        if (playerScreenHandler.getSlot(shortcutIndex).hasStack()) {
                            FakePlayerUtils.throwItem(playerScreenHandler, shortcutIndex, fakePlayer);
                        }
                    }
                }
                //如果需要遍历快捷栏以外的物品栏
                if (requireMainInventory) {
                    int inventoryIndex;
                    //遍历物品栏，尝试找到与指定物品相符的物品
                    for (inventoryIndex = 9; inventoryIndex < size - 9; inventoryIndex++) {
                        //如果该槽位是指定物品，将该物品移动到快捷栏
                        if (playerScreenHandler.getSlot(inventoryIndex).getStack().isOf(item)) {
                            playerScreenHandler.quickMove(fakePlayer, inventoryIndex);
                        } else {
                            //如果遍历完物品栏还没有找到指定物品，认为玩家身上已经没有该物品，结束方法
                            if (inventoryIndex == size - 9 - 1) {
                                return;
                            }
                        }
                    }
                }
            }
            //如果可以合成物品
            if (canCraft) {
                //如果输出槽有物品，就丢出该物品
                if (playerScreenHandler.getSlot(0).hasStack()) {
                    FakePlayerUtils.throwItem(playerScreenHandler, 0, fakePlayer);
                } else {
                    //否则，认为前面的执行有误，停止合成，结束方法
                    stopCraftAction(context.getSource(), fakePlayer);
                    return;
                }
            }
        } while (CarpetSettings.ctrlQCraftingFix);
    }

    //合成自定义物品，3x3
    public static void craft3x3(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof CraftingScreenHandler craftingScreenHandler) {
            int loopCount = 0;
            do {
                loopCount++;
                //避免游戏进入死循环
                if (loopCount > MAX_LOOP_COUNT) {
                    throw new InfiniteLoopException();
                }
                //定义变量记录找到正确合成材料的次数
                int successCount = 0;
                //依次获取每一个合成材料和遍历合成格
                for (int index = 1; index <= 9; index++) {
                    //依次获取每一个合成材料
                    Item item = ItemStackArgumentType.getItemStackArgument(context, "item" + index).getItem();
                    Slot slot = craftingScreenHandler.getSlot(index);
                    //如果合成格的指定槽位不是所需要合成材料，则丢出该物品
                    if (slot.hasStack()) {
                        ItemStack itemStack = slot.getStack();
                        if (itemStack.isOf(item)) {
                            //合成表格上已经有正确的合成材料，找到正确的合成材料次数自增
                            successCount++;
                        } else {
                            FakePlayerUtils.throwItem(craftingScreenHandler, index, fakePlayer);
                        }
                    } else {
                        //如果指定合成材料是空气，则不需要遍历物品栏，直接跳过该物品，并增加找到正确合成材料的次数
                        if (item == Items.AIR) {
                            successCount++;
                            continue;
                        }
                        //遍历物品栏找到需要的合成材料
                        int size = craftingScreenHandler.slots.size();
                        for (int inventoryIndex = 10; inventoryIndex < size; inventoryIndex++) {
                            if (craftingScreenHandler.getSlot(inventoryIndex).getStack().isOf(item)) {
                                //找到正确合成材料的次数自增
                                successCount++;
                                //光标拾取和移动物品
                                FakePlayerUtils.pickupAndMoveItemStack(craftingScreenHandler, inventoryIndex, index, fakePlayer);
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
                    //遍历完合成格后，如果找到正确合成材料小于9，认为玩家身上没有足够的合成材料了，直接结束方法
                    return;
                }
            } while (CarpetSettings.ctrlQCraftingFix);
        }
    }

    //合成自定义物品，2x2
    public static void craft2x2(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        PlayerScreenHandler playerScreenHandler = fakePlayer.playerScreenHandler;
        //定义变量记录循环次数
        int loopCount = 0;
        do {
            loopCount++;
            //如果循环次数过多，认为游戏进入了死循环，直接抛出异常强行结束方法
            if (loopCount > MAX_LOOP_COUNT) {
                throw new InfiniteLoopException();
            }
            //定义变量记录找到正确合成材料的次数
            int successCount = 0;
            //遍历4x4合成格
            for (int craftIndex = 1; craftIndex <= 4; craftIndex++) {
                //获取每一个合成材料
                Item item = ItemStackArgumentType.getItemStackArgument(context, "item" + craftIndex).getItem();
                Slot slot = playerScreenHandler.getSlot(craftIndex);
                //检查合成格上是否已经有物品
                if (slot.hasStack()) {
                    //如果有并且物品是正确的合成材料，直接结束本轮循环，即跳过该物品
                    if (slot.getStack().isOf(item)) {
                        successCount++;
                    } else {
                        //如果不是，丢出该物品
                        FakePlayerUtils.throwItem(playerScreenHandler, craftIndex, fakePlayer);
                    }
                } else {
                    if (item == Items.AIR) {
                        successCount++;
                        continue;
                    }
                    int size = playerScreenHandler.slots.size();
                    //遍历物品栏，包括盔甲槽和副手槽
                    for (int inventoryIndex = 5; inventoryIndex < size; inventoryIndex++) {
                        //如果该槽位是正确的合成材料，将该物品移动到合成格，然后增加找到正确合成材料的次数
                        if (playerScreenHandler.getSlot(inventoryIndex).getStack().isOf(item)) {
                            FakePlayerUtils.pickupAndMoveItemStack(playerScreenHandler, inventoryIndex, craftIndex, fakePlayer);
                            successCount++;
                            break;
                        }
                        //如果遍历完物品栏还没有找到指定物品，认为玩家身上已经没有该物品，结束方法
                        if (craftIndex == 4 && inventoryIndex == size - 1) {
                            return;
                        }
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
                //遍历完合成格后，如果没有找到足够多的合成材料，认为玩家身上没有足够的合成材料了，直接结束方法
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

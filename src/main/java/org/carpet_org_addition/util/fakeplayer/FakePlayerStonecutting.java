package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.exception.InfiniteLoopException;
import org.carpet_org_addition.util.StringUtils;

public class FakePlayerStonecutting {
    private FakePlayerStonecutting() {
    }

    public static void stonecutting(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof StonecutterScreenHandler stonecutterScreenHandler) {
            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
            //获取要切割的物品和按钮的索引
            int buttonIndex = IntegerArgumentType.getInteger(context, "button") - 1;
            //定义变量记录循环次数，用于循环次数过多时抛出异常结束循环
            int loopCount = 0;
            while (true) {
                loopCount++;
                if (loopCount > 1000) {
                    //无限循环异常
                    throw new InfiniteLoopException(StringUtils.getPlayerName(fakePlayer)
                            + "在使用切石机时循环了" + loopCount + "次("
                            + StringUtils.getDimensionId(fakePlayer.getWorld()) + ":["
                            + StringUtils.getBlockPosString(fakePlayer.getBlockPos()) + "])");
                }
                //定义变量记录是否需要遍历物品栏
                boolean flag = true;
                //获取切石机输入槽对象
                Slot inputSlot = stonecutterScreenHandler.getSlot(0);
                //判断切石机输入槽是否有物品
                if (inputSlot.hasStack()) {
                    //如果有物品，并且是指定物品，设置不需要遍历物品栏
                    ItemStack itemStack = inputSlot.getStack();
                    if (itemStack.isOf(item)) {
                        flag = false;
                    } else {
                        //如果不是指定物品，丢出该物品
                        FakePlayerUtils.throwItem(stonecutterScreenHandler, 0, fakePlayer);
                    }
                }
                //如果需要遍历物品栏
                if (flag) {
                    //尝试从物品栏中找到需要的物品
                    for (int index = 2; index < stonecutterScreenHandler.slots.size(); index++) {
                        //如果找到，移动到切石机输入槽，然后结束循环
                        if (stonecutterScreenHandler.getSlot(index).getStack().isOf(item)) {
                            FakePlayerUtils.quickMove(stonecutterScreenHandler, index, fakePlayer);
                            break;
                        }
                        //如果遍历完物品栏还没有找到指定物品，认为物品栏中没有该物品，结束方法
                        if (index == stonecutterScreenHandler.slots.size() - 1) {
                            return;
                        }
                    }
                }
                //模拟单击切石机按钮
                stonecutterScreenHandler.onButtonClick(fakePlayer, buttonIndex);
                //获取切石机输出槽对象
                Slot outputSlot = stonecutterScreenHandler.getSlot(1);
                //如果输出槽有物品
                if (outputSlot.hasStack()) {
                    //丢出该物品
                    FakePlayerUtils.loopThrowItem(stonecutterScreenHandler, 1, fakePlayer);
                } else {
                    //否则，认为前面的操作有误，停止合成，结束方法
                    FakePlayerUtils.stopAction(context.getSource(), fakePlayer,
                            "carpet.commands.playerTools.action.stone_cutting");
                    return;
                }
            }
        }
    }
}

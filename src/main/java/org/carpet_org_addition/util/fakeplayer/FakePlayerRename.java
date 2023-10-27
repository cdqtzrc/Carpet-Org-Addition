package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;

public class FakePlayerRename {
    private FakePlayerRename() {
    }

    public static void rename(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        if (fakePlayer.currentScreenHandler instanceof AnvilScreenHandler anvilScreenHandler) {
            //获取当前要操作的物品和要重命名的字符串
            Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
            String newName = StringArgumentType.getString(context, "name");
            Slot oneSlot = anvilScreenHandler.getSlot(0);
            //第一个槽位的物品是否正确：是指定物品，没有被正确重命名，已经最大堆叠
            boolean oneSlotCorrect = false;
            //判断第一个槽位是否有物品
            if (oneSlot.hasStack()) {
                ItemStack itemStack = oneSlot.getStack();
                //判断该槽位的物品是否已经正确重命名
                if (itemStack.getName().getString().equals(newName)) {
                    //如果已经重命名，丢弃该槽位的物品
                    //因为该槽位的物品被丢弃，所以该槽位的物品一定不是预期物品，没有必要继续判断，直接结束本轮循环
                    FakePlayerUtils.pickupAndThrow(anvilScreenHandler, 0, fakePlayer);
                }
                // TODO 此处是否需要一个else？
                //判断当前物品堆栈对象是否为指定物品
                if (itemStack.isOf(item)) {
                    //让物品最大堆叠后才能重命名，节省经验
                    if (itemStack.getCount() == itemStack.getMaxCount()) {
                        oneSlotCorrect = true;
                    }
                } else {
                    //当前槽位不是指定物品，丢出该槽位的物品
                    FakePlayerUtils.pickupAndThrow(anvilScreenHandler, 0, fakePlayer);
                }
            }
            //遍历玩家物品栏，找到指定需要重命名的物品
            //第一个槽位的物品必须是正确的
            for (int index = 3; !oneSlotCorrect && index < anvilScreenHandler.slots.size(); index++) {
                if (anvilScreenHandler.getSlot(index).hasStack() && anvilScreenHandler.getSlot(index).getStack().isOf(item)) {
                    //找到指定物品后，模拟按住Shift键将物品移动到铁砧输入槽，然后跳出for循环
                    FakePlayerUtils.quickMove(anvilScreenHandler, index, fakePlayer);
                    break;
                }
                //如果遍历完物品栏还是没有找到指定物品，认为玩家物品栏中已经没有指定物品，结束方法
                if (index == anvilScreenHandler.slots.size() - 1) {
                    return;
                }
            }
            //获取铁砧第二个输入槽
            Slot twoSlot = anvilScreenHandler.getSlot(1);
            //判断该槽位是否有物品
            if (twoSlot.hasStack()) {
                //如果有，移动到物品栏，如果不能移动，直接丢出
                FakePlayerUtils.quickMove(anvilScreenHandler, 1, fakePlayer);
                if (twoSlot.hasStack()) {
                    FakePlayerUtils.pickupAndThrow(anvilScreenHandler, 1, fakePlayer);
                }
            }
            //判断第一个输入槽是否正确，第二个格子是否没有物品
            if (oneSlotCorrect && !twoSlot.hasStack()) {
                //设置物品名称
                anvilScreenHandler.setNewItemName(newName);
                //判断是否可以取出输出槽的物品
                if (anvilScreenHandler.getSlot(2).hasStack() && canTakeOutput(fakePlayer, anvilScreenHandler)) {
                    //丢出输出槽的物品
                    FakePlayerUtils.pickupAndThrow(anvilScreenHandler, 2, fakePlayer);
                } else {
                    //如果不能取出，可能玩家已经没有经验，停止重命名
                    FakePlayerUtils.stopAction(context.getSource(), fakePlayer, "carpet.commands.playerTools.action.rename");
                }
            }
        }
    }

    //判断是否可以输出物品
    private static boolean canTakeOutput(PlayerEntity player, AnvilScreenHandler anvilScreenHandler) {
        return (player.getAbilities().creativeMode || player.experienceLevel >= anvilScreenHandler.getLevelCost()) && anvilScreenHandler.getLevelCost() > 0;
    }
}
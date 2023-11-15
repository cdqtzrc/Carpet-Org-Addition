package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.server.command.ServerCommandSource;

public class FakePlayerMoveItem {
    private FakePlayerMoveItem() {
    }

    public static void moveItem(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        if (!(fakePlayer.currentScreenHandler instanceof ShulkerBoxScreenHandler shulkerBoxScreenHandler)) {
            return;
        }
        //获取要装在潜影盒的物品
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        //只遍历玩家物品栏，不遍历潜影盒容器
        //前27个格子是潜影盒的槽位
        for (int index = 63 - 36; index < 63; index++) {// 63-36=27
            ItemStack itemStack = shulkerBoxScreenHandler.slots.get(index).getStack();
            if (itemStack.isEmpty()) {
                continue;
            }
            if (itemStack.isOf(item)) {
                //相当于按住Shift键移动物品
                FakePlayerUtils.quickMove(shulkerBoxScreenHandler, index, fakePlayer);
            } else {
                //丢弃玩家物品栏中与指定物品不符的物品
                FakePlayerUtils.throwItem(shulkerBoxScreenHandler, index, fakePlayer);
            }
        }
    }
}

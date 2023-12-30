package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.exception.NoNbtException;
import org.carpet_org_addition.util.InventoryUtils;

public class FakePlayerSorting {
    private FakePlayerSorting() {
    }

    public static void sorting(CommandContext<ServerCommandSource> context, EntityPlayerMPFake fakePlayer) {
        //获取要分拣的物品对象
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        //获取分拣物品要丢出的方向
        Vec3d thisVec = Vec3ArgumentType.getVec3(context, "this");
        //获取非分拣物品要丢出的方向
        Vec3d otherVec = Vec3ArgumentType.getVec3(context, "other");
        //获取玩家物品栏对象
        PlayerInventory inventory = fakePlayer.getInventory();
        //遍历玩家物品栏，找到要丢出的物品
        for (int index = 0; index < inventory.size(); index++) {
            //定义变量记录当前槽位的物品堆栈对象
            ItemStack itemStack = inventory.getStack(index);
            if (itemStack.isEmpty()) {
                continue;
            }
            //如果是要分拣的物品，就转向一边，否则转身向另一边
            if (itemStack.getItem() == item) {
                fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, thisVec);
            } else {
                //丢弃潜影盒内的物品
                //判断当前物品是不是潜影盒
                if (InventoryUtils.isShulkerBoxItem(itemStack)) {
                    while (true) {
                        //一轮循环结束后，再重新将当前物品设置为物品栏中的潜影盒
                        itemStack = inventory.getStack(index);
                        //判断潜影盒是否为空
                        if (!InventoryUtils.isEmptyShulkerBox(itemStack)) {
                            //获取潜影盒内第一个非空气物品，获取后，该物品会在潜影盒内删除
                            //设置当前物品为潜影盒内容物的第一个非空物品
                            try {
                                itemStack = InventoryUtils.getShulkerBoxItem(itemStack);
                            } catch (NoNbtException e) {
                                //空潜影盒异常，潜影盒可能没有NBT，如直接从创造模式物品栏中拿取出的潜影盒
                                //丢出这个异常的潜影盒
                                itemStack = inventory.getStack(index);
                                //设置当前朝向为丢出非指定物品朝向
                                fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, otherVec);
                                break;
                            }
                            //根据当前物品设置朝向
                            fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES,
                                    itemStack.getItem() == item ? thisVec : otherVec);
                        }
                        //如果为空，将朝向设置为丢出非指定物品的方向，然后结束循环
                        else {
                            //设置当前朝向为丢出非指定物品朝向
                            fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, otherVec);
                            break;
                        }
                        //丢弃潜影盒内物品堆栈
                        FakePlayerUtils.dropItem(fakePlayer, itemStack);
                    }
                } else {
                    //设置当前朝向为丢出非指定物品朝向
                    fakePlayer.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, otherVec);
                }
            }
            //丢弃该物品堆栈
            FakePlayerUtils.dropItem(fakePlayer, itemStack);
        }
    }
}

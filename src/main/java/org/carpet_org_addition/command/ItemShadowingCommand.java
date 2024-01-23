package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.exception.NoNbtException;
import org.carpet_org_addition.util.*;
import org.carpet_org_addition.util.helpers.ImmutableInventory;

public class ItemShadowingCommand {
    public ItemShadowingCommand() {
    }

    //注册用于制作物品分身的/itemshadowing命令
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((CommandManager.literal("itemshadowing")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandItemShadowing))
                .executes(ItemShadowingCommand::itemShadowing)));
    }

    //制作物品分身
    private static int itemShadowing(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        // 获取主副手上的物品
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if (main.isEmpty()) {
            // 主手不能为空
            throw CommandUtils.getException("carpet.commands.itemshadowing.main_hand_is_empty");
        } else if (off.isEmpty()) {
            player.setStackInHand(Hand.OFF_HAND, main);
            // 广播制作物品分身的消息
            MessageUtils.broadcastTextMessage(context.getSource(),
                    TextUtils.getTranslate("carpet.commands.itemshadowing.broadcast",
                            player.getDisplayName(), main.toHoverableText()));
            // 将玩家制作物品分身的消息写入日志
            try {
                ImmutableInventory inventory = InventoryUtils.getInventory(main);
                CarpetOrgAddition.LOGGER.info(player.getName().getString() + "制作了一个"
                        + main.getItem().getName().getString() + "的物品分身，包含" + inventory.itemCount()
                        + "个物品，分别是：" + inventory + "，在"
                        + StringUtils.getDimensionId(player.getWorld()) + "，坐标:["
                        + StringUtils.getBlockPosString(player.getBlockPos()) + "]");
            } catch (NoNbtException e) {
                CarpetOrgAddition.LOGGER.info(player.getName().getString() + "制作了一个["
                        + main.getItem().getName().getString() + "]的物品分身，在"
                        + StringUtils.getDimensionId(player.getWorld()) + "，坐标:["
                        + StringUtils.getBlockPosString(player.getBlockPos()) + "]");
            }
            return 1;
        } else {
            // 副手必须为空
            throw CommandUtils.getException("carpet.commands.itemshadowing.off_hand_not_empty");
        }
    }
}

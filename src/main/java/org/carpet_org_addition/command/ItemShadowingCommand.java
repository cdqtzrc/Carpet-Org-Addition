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
    //注册用于制作物品分身的/itemshadowing命令
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("itemshadowing")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandItemShadowing))
                .executes(ItemShadowingCommand::itemShadowing));
    }

    //制作物品分身
    private static int itemShadowing(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取主副手上的物品
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if (main.isEmpty()) {
            // 主手不能为空
            throw CommandUtils.createException("carpet.commands.itemshadowing.main_hand_is_empty");
        } else if (off.isEmpty()) {
            player.setStackInHand(Hand.OFF_HAND, main);
            // 广播制作物品分身的消息
            MessageUtils.broadcastTextMessage(context.getSource(),
                    TextUtils.getTranslate("carpet.commands.itemshadowing.broadcast",
                            player.getDisplayName(), main.toHoverableText()));
            // 将玩家制作物品分身的消息写入日志
            if (InventoryUtils.isShulkerBoxItem(main)) {
                try {
                    ImmutableInventory inventory = InventoryUtils.getInventory(main);
                    CarpetOrgAddition.LOGGER.info("{}制作了一个{}的物品分身，包含{}个物品，分别是：{}，在{}，坐标:[{}]",
                            GameUtils.getPlayerName(player), main.getItem().getName().getString(),
                            inventory.slotCount(), inventory, WorldUtils.getDimensionId(player.getWorld()),
                            WorldUtils.toPosString(player.getBlockPos()));
                } catch (NoNbtException e) {
                    CarpetOrgAddition.LOGGER.info("{}制作了一个空[{}]的物品分身，在{}，坐标:[{}]",
                            GameUtils.getPlayerName(player), main.getItem().getName().getString(),
                            WorldUtils.getDimensionId(player.getWorld()), WorldUtils.toPosString(player.getBlockPos()));
                }
            } else {
                CarpetOrgAddition.LOGGER.info("{}制作了一个[{}]的物品分身，在{}，坐标:[{}]",
                        GameUtils.getPlayerName(player), main.getItem().getName().getString(),
                        WorldUtils.getDimensionId(player.getWorld()), WorldUtils.toPosString(player.getBlockPos()));
            }
            return 1;
        } else {
            // 副手必须为空
            throw CommandUtils.createException("carpet.commands.itemshadowing.off_hand_not_empty");
        }
    }
}

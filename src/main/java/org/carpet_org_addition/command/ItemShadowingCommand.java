package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;

public class ItemShadowingCommand {
    public ItemShadowingCommand() {
    }

    //注册用于制作物品分身的/itemshadowing命令
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((CommandManager.literal("itemshadowing").requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandItemShadowing)).executes(context -> {
            ServerPlayerEntity player = CommandUtils.getPlayer(context);
            return itemShadowing(player);
        })));
    }

    //制作物品分身
    private static int itemShadowing(PlayerEntity player) throws CommandSyntaxException {
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if (main.isEmpty()) {
            throw CommandUtils.getException("carpet.commands.itemshadowing.main_hand_is_empty");
        } else if (off.isEmpty()) {
            player.setStackInHand(Hand.OFF_HAND, player.getMainHandStack());
            return 1;
        } else {
            throw CommandUtils.getException("carpet.commands.itemshadowing.off_hand_not_empty");
        }
    }
}

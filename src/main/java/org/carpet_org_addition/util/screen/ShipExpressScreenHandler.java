package org.carpet_org_addition.util.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.express.Express;
import org.carpet_org_addition.util.express.ExpressManager;
import org.carpet_org_addition.util.express.ExpressManagerInterface;

import java.io.IOException;

public class ShipExpressScreenHandler extends GenericContainerScreenHandler {
    private final Inventory inventory;
    private final ExpressManager expressManager;
    private final MinecraftServer server;
    private final ServerPlayerEntity sourcePlayer;
    private final ServerPlayerEntity targetPlayer;

    public ShipExpressScreenHandler(int syncId, PlayerInventory playerInventory, ServerPlayerEntity sourcePlayer, ServerPlayerEntity targetPlayer, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inventory, 3);
        this.inventory = inventory;
        this.server = targetPlayer.server;
        this.expressManager = ((ExpressManagerInterface) targetPlayer.server).getExpressManager();
        this.sourcePlayer = sourcePlayer;
        this.targetPlayer = targetPlayer;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (this.inventory.isEmpty()) {
            return;
        }
        // 快递接收者可能在发送者发送快递时退出游戏
        if (this.targetPlayer.isRemoved()) {
            MessageUtils.sendCommandErrorFeedback(this.sourcePlayer.getCommandSource(), "carpet.commands.multiple.no_player");
            // 将GUI中的物品放回玩家物品栏
            this.dropInventory(this.sourcePlayer, this.inventory);
            return;
        }
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.getStack(i).copyAndEmpty();
            // 不要发送空气物品
            if (stack.isEmpty()) {
                continue;
            }
            Express express = new Express(this.server, this.sourcePlayer, this.targetPlayer, stack, this.expressManager.generateNumber());
            try {
                expressManager.put(express);
            } catch (IOException e) {
                CarpetOrgAddition.LOGGER.error("批量发送物品时遇到意外错误", e);
                MessageUtils.sendCommandErrorFeedback(this.sourcePlayer.getCommandSource(), e, "carpet.commands.multiple.error");
                return;
            }
        }
    }
}

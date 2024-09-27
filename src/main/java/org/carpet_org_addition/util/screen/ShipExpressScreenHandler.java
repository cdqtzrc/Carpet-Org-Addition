package org.carpet_org_addition.util.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.constant.TextConstants;
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
        this.expressManager = ExpressManagerInterface.getInstance(targetPlayer.server);
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
        SimpleInventory simpleInventory = new SimpleInventory(this.inventory.size());
        // 合并可堆叠的物品
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack itemStack = this.inventory.getStack(i);
            // 不要发送空气物品
            if (itemStack.isEmpty()) {
                continue;
            }
            simpleInventory.addStack(itemStack);
        }
        // 发送物品
        for (int i = 0; i < simpleInventory.size(); i++) {
            ItemStack stack = simpleInventory.getStack(i);
            // 前面已经对物品进行了整理，所以遇到空物品时，说明物品已发送完毕
            if (stack.isEmpty()) {
                break;
            }
            Express express = new Express(this.server, this.sourcePlayer, this.targetPlayer, stack, this.expressManager.generateNumber());
            try {
                expressManager.putNoMessage(express);
            } catch (IOException e) {
                CarpetOrgAddition.LOGGER.error("批量发送物品时遇到意外错误", e);
                MessageUtils.sendCommandErrorFeedback(this.sourcePlayer.getCommandSource(), e, "carpet.commands.multiple.error");
                return;
            }
        }
        sendFeedback(simpleInventory);
    }

    // 发送命令反馈
    public void sendFeedback(SimpleInventory simpleInventory) {
        int count = 0;
        ItemStack firstStack = simpleInventory.getStack(0);
        // 定义变量记录查找状态
        // 如果为0，表示物品栏里只有一种物品，并且NBT也相同
        // 如果为1，表示物品栏里只有一种物品，但是NBT不相同
        // 如果为2，表示物品栏里有多种物品，不考虑NBT
        int onlyOneKind = 0;
        for (int i = 0; i < simpleInventory.size(); i++) {
            ItemStack stack = simpleInventory.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }
            count += stack.getCount();
            // 比较物品和物品NBT
            if (onlyOneKind != 0 && ItemStack.areEqual(firstStack, stack)) {
                continue;
            }
            onlyOneKind = 1;
            // 只比较物品
            if (firstStack.isOf(stack.getItem())) {
                continue;
            }
            onlyOneKind = 2;
        }
        // TODO "向%s发送了%s个%s，点击%s全部撤回"第一个参数不正确
        Text playerName = this.sourcePlayer.getDisplayName();
        MutableText command = TextConstants.clickRun("/mail cancel");
        Object[] args = switch (onlyOneKind) {
            case 0 -> {
                MutableText itemCount = TextConstants.itemCount(count, firstStack.getMaxCount());
                yield new Object[]{playerName, itemCount, firstStack.toHoverableText(), command};
            }
            case 1 -> {
                // 物品名称
                Text hoverableText = firstStack.getItem().getDefaultStack().toHoverableText();
                // 物品堆叠数
                MutableText itemCount = TextConstants.itemCount(count, firstStack.getMaxCount());
                yield new Object[]{playerName, itemCount, hoverableText, command};
            }
            case 2 -> {
                // 不显示物品堆叠组数，但鼠标悬停可以显示物品栏
                MutableText itemText = TextUtils.translate("carpet.command.item.item");
                yield new Object[]{playerName, count, TextConstants.inventory(itemText, simpleInventory), command};
            }
            default -> throw new IllegalStateException();
        };
        // 向物品发送者发送消息
        MessageUtils.sendCommandFeedback(this.sourcePlayer.getCommandSource(), "carpet.commands.mail.sending.multiple", args);
        // 向物品接收者发送消息
        MessageUtils.sendCommandFeedback(this.targetPlayer.getCommandSource(), "carpet.commands.mail.receive.multiple",
                this.sourcePlayer.getDisplayName(), args[1], args[2], TextConstants.clickRun("/mail receive"));
        Express.playXpOrbPickupSound(this.targetPlayer);
        Express.checkRecipientPermission(this.sourcePlayer, this.targetPlayer);
    }
}

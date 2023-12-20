package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.SendMessageUtils;
import org.carpet_org_addition.util.findtask.result.ItemFindResult;

import java.util.ArrayList;

public class ItemFindFeedback extends AbstractFindFeedback<ItemFindResult> {
    /**
     * 要查找的物品
     */
    private final ItemStack itemStack;
    /**
     * 是否包含在容器中的潜影盒中找到的物品
     */
    private boolean inTheShulkerBox = false;
    /**
     * 找到物品的总数
     */
    private int count = 0;

    public ItemFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<ItemFindResult> list, ItemStack itemStack) {
        super(context, list);
        this.itemStack = itemStack;
        this.setName("ItemFindFeedbackThread");
    }

    @Override
    public void run() {
        // 计算总共找到的物品数量
        for (ItemFindResult result : list) {
            this.count += result.getCount();
            // 是否包含从容器中的潜影盒中找到的物品，用来决定是否让数字带斜体
            if (result.inTheShulkerBox()) {
                this.inTheShulkerBox = true;
            }
        }
        sendFeedback();
    }

    @Override
    protected void sendFeedback() {
        // 为数量添加鼠标悬停效果
        MutableText text = FinderCommand.showCount(itemStack, count, inTheShulkerBox);
        // 发送命令反馈
        if (list.size() <= 10) {
            // 数量较少，不排序，直接输出
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find",
                    list.size(), text, itemStack.toHoverableText());
            for (ItemFindResult result : list) {
                SendMessageUtils.sendTextMessage(context.getSource(), result.toText());
            }
        } else {
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find.not_more_than_ten",
                    list.size(), text, itemStack.toHoverableText());
            // 按照容器内指定物品的数量从多到少排序
            list.sort((o1, o2) -> o2.getCount() - o1.getCount());
            // 容器数量过多，只反馈前十个
            for (int i = 0; i < 10; i++) {
                SendMessageUtils.sendTextMessage(context.getSource(), list.get(i).toText());
            }
        }
    }
}

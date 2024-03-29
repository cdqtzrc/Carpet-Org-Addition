package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.ItemFindResult;
import org.carpet_org_addition.util.matcher.Matcher;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class ItemFindFeedback extends AbstractFindFeedback<ItemFindResult> {
    /**
     * 要查找的物品
     */
    private final Matcher matcher;
    /**
     * 是否包含在容器中的潜影盒中找到的物品
     */
    private boolean inTheShulkerBox = false;
    /**
     * 找到物品的总数
     */
    private int count = 0;

    public ItemFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<ItemFindResult> list, Matcher matcher, int maxCount) {
        super(context, list, maxCount);
        this.matcher = matcher;
        this.setName("ItemFindFeedbackThread");
    }

    @Override
    public void run() {
        // 计算总共找到的物品数量
        for (ItemFindResult result : list) {
            this.count += result.getCount();
            // 是否包含从容器中的潜影盒中找到的物品，用来决定是否让数字带斜体
            if (result.inTheBox()) {
                this.inTheShulkerBox = true;
            }
        }
        // 按照容器内指定物品的数量从多到少排序
        list.sort((o1, o2) -> o2.getCount() - o1.getCount());
        try {
            sendFeedback();
        } catch (TimeoutException e) {
            MessageUtils.sendCommandFeedback(context.getSource(), AbstractFindFeedback.TIME_OUT);
        }
    }

    @Override
    protected void sendFeedback() throws TimeoutException {
        MutableText text;
        boolean isItem = matcher.isItem();
        if (isItem) {
            // 为数量添加鼠标悬停效果
            text = FinderCommand.showCount(matcher.getItem().getDefaultStack(), count, inTheShulkerBox);
        } else {
            text = TextUtils.regularStyle(String.valueOf(count), null, false, inTheShulkerBox,
                    false, false);
        }
        // 发送命令反馈
        if (list.size() <= this.maxCount) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find",
                    list.size(), text, matcher.toText());
            for (ItemFindResult result : list) {
                checkTimeOut();
                MessageUtils.sendTextMessage(context.getSource(),
                        isItem
                                ? result.toText()
                                : TextUtils.appendAll(result.toText(), result.getMatcher().toText()));
            }
        } else {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find.limit",
                    list.size(), text, matcher.toText(), this.maxCount);
            // 容器数量过多，只反馈前十个
            for (int i = 0; i < this.maxCount; i++) {
                checkTimeOut();
                MessageUtils.sendTextMessage(context.getSource(),
                        isItem
                                ? list.get(i).toText()
                                : TextUtils.appendAll(list.get(i).toText(), list.get(i).getMatcher().toText()));
            }
        }
    }
}

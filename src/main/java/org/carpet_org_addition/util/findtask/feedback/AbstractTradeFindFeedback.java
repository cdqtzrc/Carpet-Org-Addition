package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.AbstractTradeFindResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

public abstract class AbstractTradeFindFeedback<T extends AbstractTradeFindResult> extends AbstractFindFeedback<T> {
    /**
     * 村民的游戏内名称
     */
    public static final MutableText VILLAGER = TextUtils.getTranslate("entity.minecraft.villager");
    /**
     * 流浪商人的游戏内名称
     */
    public static final MutableText WANDERING_TRADER = TextUtils.getTranslate("entity.minecraft.wandering_trader");
    protected final BlockPos sourceBlockPos;

    protected AbstractTradeFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<T> list, BlockPos sourceBlockPos, int maxCount) {
        super(context, list, maxCount);
        this.sourceBlockPos = sourceBlockPos;
    }

    @Override
    public void run() {
        // 发送反馈
        try {
            sendFeedback();
        } catch (TimeoutException e) {
            MessageUtils.sendCommandFeedback(context.getSource(), AbstractFindFeedback.TIME_OUT);
        }
    }

    @Override
    protected void sendFeedback() throws TimeoutException {
        // 获取村民的数量，村民的数量不等于交易选项的数量，因为一个村民可能包含多个符合预期的交易
        HashSet<T> hashSet = new HashSet<>(list);
        int merchantCount = hashSet.size();
        // 打印输出
        if (list.size() <= this.maxCount) {
            // 数量小于等于10，直接输出
            MessageUtils.sendCommandFeedback(context.getSource(),
                    "carpet.commands.finder.trade.result", merchantCount,
                    getFindItemText(),
                    VILLAGER,// 村民和流浪商人的翻译键
                    WANDERING_TRADER);
            for (T result : list) {
                checkTimeOut();
                MessageUtils.sendTextMessage(context.getSource(), result.toText());
            }
        } else {
            // 数量大于10，只输出距离最近的前十个
            MessageUtils.sendCommandFeedback(context.getSource(),
                    getTranslateKey(), merchantCount,
                    getFindItemText(),
                    VILLAGER,
                    WANDERING_TRADER,
                    this.maxCount);
            for (int i = 0; i < this.maxCount; i++) {
                checkTimeOut();
                MessageUtils.sendTextMessage(context.getSource(), list.get(i).toText());
            }
        }
    }

    protected abstract String getTranslateKey();

    protected abstract Text getFindItemText();
}

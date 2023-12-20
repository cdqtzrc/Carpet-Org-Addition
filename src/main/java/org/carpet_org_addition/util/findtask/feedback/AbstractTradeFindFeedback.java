package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.SendMessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.AbstractTradeFindResult;

import java.util.ArrayList;

public abstract class AbstractTradeFindFeedback<T extends AbstractTradeFindResult> extends AbstractFindFeedback<T> {
    /**
     * 村民的游戏内名称
     */
    protected static final MutableText VILLAGER = TextUtils.getTranslate("entity.minecraft.villager");
    /**
     * 流浪商人的游戏内名称
     */
    protected static final MutableText WANDERING_TRADER = TextUtils.getTranslate("entity.minecraft.wandering_trader");
    protected final BlockPos sourceBlockPos;

    protected AbstractTradeFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<T> list, BlockPos sourceBlockPos) {
        super(context, list);
        this.sourceBlockPos = sourceBlockPos;
    }

    @Override
    public void run() {
        // 按照从近到远的顺序排序
        list.sort((o1, o2) -> MathUtils.compareBlockPos(sourceBlockPos, o1.getMerchant().getBlockPos(), o2.getMerchant().getBlockPos()));
        // 发送反馈
        sendFeedback();
    }

    @Override
    protected void sendFeedback() {
        // 打印输出
        if (list.size() <= 10) {
            // 数量小于等于10，直接输出
            SendMessageUtils.sendCommandFeedback(context.getSource(),
                    "carpet.commands.finder.trade.result", list.size(),
                    getFindItemText(),
                    VILLAGER,// 村民和流浪商人的翻译键
                    WANDERING_TRADER);
            for (T result : list) {
                SendMessageUtils.sendTextMessage(context.getSource(), result.toText());
            }
        } else {
            // 数量大于10，只输出距离最近的前十个
            SendMessageUtils.sendCommandFeedback(context.getSource(),
                    "carpet.commands.finder.trade.result.not_more_than_ten", list.size(),
                    getFindItemText(),
                    VILLAGER,
                    WANDERING_TRADER);
            for (int i = 0; i < 10; i++) {
                SendMessageUtils.sendTextMessage(context.getSource(), list.get(i).toText());
            }
        }
    }

    protected abstract Text getFindItemText();
}

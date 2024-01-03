package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;

import java.util.ArrayList;

public class TradeItemFindFeedback extends AbstractTradeFindFeedback<TradeItemFindResult> {
    protected final ItemStack itemStack;

    public TradeItemFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<TradeItemFindResult> list, BlockPos sourceBlockPos, ItemStack itemStack, int maxCount) {
        super(context, list, sourceBlockPos, maxCount);
        this.itemStack = itemStack;
        this.setName("TradeItemFindFeedbackThread");
    }

    @Override
    public void run() {
        // 按照从近到远的顺序排序
        list.sort((o1, o2) -> MathUtils.compareBlockPos(sourceBlockPos, o1.getMerchant().getBlockPos(), o2.getMerchant().getBlockPos()));
        super.run();
    }

    @Override
    protected String getTranslateKey() {
        return "carpet.commands.finder.trade.result.limit";
    }

    @Override
    protected Text getFindItemText() {
        return itemStack.toHoverableText();
    }
}

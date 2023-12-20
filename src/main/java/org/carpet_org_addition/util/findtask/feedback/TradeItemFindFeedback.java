package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;

import java.util.ArrayList;

public class TradeItemFindFeedback extends AbstractTradeFindFeedback<TradeItemFindResult> {
    protected final ItemStack itemStack;
    public TradeItemFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<TradeItemFindResult> list, BlockPos sourceBlockPos, ItemStack itemStack) {
        super(context, list, sourceBlockPos);
        this.setName("TradeItemFindFeedbackThread");
        this.itemStack = itemStack;
    }

    @Override
    protected Text getFindItemText() {
        return itemStack.toHoverableText();
    }
}

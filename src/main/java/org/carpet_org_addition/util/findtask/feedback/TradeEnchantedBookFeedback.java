package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.TradeEnchantedBookResult;

import java.util.ArrayList;

public class TradeEnchantedBookFeedback extends AbstractTradeFindFeedback<TradeEnchantedBookResult> {
    private final Enchantment enchantment;

    public TradeEnchantedBookFeedback(CommandContext<ServerCommandSource> context, ArrayList<TradeEnchantedBookResult> list, BlockPos sourceBlockPos, Enchantment enchantment) {
        super(context, list, sourceBlockPos);
        this.enchantment = enchantment;
        this.setName("TradeEnchantedBookFindFeedbackThread");
    }

    @Override
    public void run() {
        // 按照附魔书等级从高到低进行排序，如果等级相同，按照玩家离村民的距离从近到远的顺序排序
        list.sort((o1, o2) -> {
            int compare = Integer.compare(o1.getLevel(), o2.getLevel());
            if (compare == 0) {
                return MathUtils.compareBlockPos(sourceBlockPos, o1.getMerchant().getBlockPos(), o2.getMerchant().getBlockPos());
            }
            return -compare;
        });
        super.run();
    }

    @Override
    protected String getTranslateKey() {
        return "carpet.commands.finder.trade.enchanted_book.result.not_more_than_ten";
    }

    @Override
    protected MutableText getFindItemText() {
        MutableText mutableText = Text.translatable(enchantment.getTranslationKey());
        // 如果是诅咒附魔，设置为红色
        if (enchantment.isCursed()) {
            mutableText.formatted(Formatting.RED);
        } else {
            mutableText.formatted(Formatting.GRAY);
        }
        return TextUtils.appendAll(mutableText, Items.ENCHANTED_BOOK.getName());
    }
}

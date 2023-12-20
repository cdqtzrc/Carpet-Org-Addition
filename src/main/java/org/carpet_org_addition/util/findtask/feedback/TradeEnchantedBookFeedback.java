package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
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

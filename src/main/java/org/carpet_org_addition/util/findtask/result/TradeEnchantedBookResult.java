package org.carpet_org_addition.util.findtask.result;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.util.TextUtils;

public class TradeEnchantedBookResult extends AbstractTradeFindResult {
    /**
     * 获取到的附魔
     */
    private final Enchantment enchantment;
    /**
     * 获取到附魔的等级
     */
    private final int level;

    public TradeEnchantedBookResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex, Enchantment enchantment, int level) {
        super(merchant, tradeOffer, tradeIndex);
        this.enchantment = enchantment;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public MutableText toText() {
        return TextUtils.getTranslate("carpet.commands.finder.trade.enchanted_book.each",
                TextUtils.blockPos(merchant.getBlockPos(), Formatting.GREEN), merchantName, tradeIndex, enchantment.getName(level));
    }
}

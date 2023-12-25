package org.carpet_org_addition.util.findtask.result;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.util.TextUtils;

public class TradeItemFindResult extends AbstractTradeFindResult {
    public TradeItemFindResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex) {
        super(merchant, tradeOffer, tradeIndex);
    }

    @Override
    public MutableText toText() {
        return TextUtils.getTranslate("carpet.commands.finder.trade.item.each",
                TextUtils.blockPos(merchant.getBlockPos(), Formatting.GREEN), merchantName, tradeIndex);
    }
}

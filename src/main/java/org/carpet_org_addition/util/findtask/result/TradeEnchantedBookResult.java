package org.carpet_org_addition.util.findtask.result;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.village.TradeOffer;

public class TradeEnchantedBookResult extends AbstractTradeFindResult {
    public TradeEnchantedBookResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex) {
        super(merchant, tradeOffer, tradeIndex);
    }
}

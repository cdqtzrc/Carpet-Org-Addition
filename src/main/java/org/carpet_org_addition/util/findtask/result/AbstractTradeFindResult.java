package org.carpet_org_addition.util.findtask.result;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.text.MutableText;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.util.TextUtils;

public abstract class AbstractTradeFindResult extends AbstractFindResult {
    protected final TradeOffer tradeOffer;
    protected final int tradeIndex;
    protected final MerchantEntity merchant;
    protected final MutableText merchantName;

    protected AbstractTradeFindResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex) {
        this.tradeOffer = tradeOffer;
        this.tradeIndex = tradeIndex;
        this.merchant = merchant;
        String command = "/particleLine ~ ~1 ~ " + merchant.getX() + " " + (merchant.getY() + 1) + " " + merchant.getZ();
        merchantName = TextUtils.command(merchant.getName().copy(), command, null, null, true);
    }

    public MerchantEntity getMerchant() {
        return this.merchant;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractTradeFindResult tradeFindResult) {
            return this.merchant.getUuid().equals(tradeFindResult.merchant.getUuid());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return merchant.getUuid().hashCode();
    }
}

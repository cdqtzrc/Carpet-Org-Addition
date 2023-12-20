package org.carpet_org_addition.util.findtask.result;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.util.TextUtils;

public abstract class AbstractTradeFindResult extends AbstractFindResult {
    protected final TradeOffer tradeOffer;
    protected final int tradeIndex;
    protected final MerchantEntity merchant;

    protected AbstractTradeFindResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex) {
        this.tradeOffer = tradeOffer;
        this.tradeIndex = tradeIndex;
        this.merchant = merchant;
    }

    public MerchantEntity getMerchant() {
        return this.merchant;
    }

    @Override
    public MutableText toText() {
        String command = "/particleLine ~ ~1 ~ " + merchant.getX() + " " + (merchant.getY() + 1) + " " + merchant.getZ();
        MutableText name = TextUtils.command(merchant.getName().copy(), command, null, null, true);
        return TextUtils.getTranslate("carpet.commands.finder.trade.item.each",
                TextUtils.blockPos(merchant.getBlockPos(), Formatting.GREEN), name, tradeIndex);
    }
}

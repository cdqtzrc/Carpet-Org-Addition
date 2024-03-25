package org.carpet_org_addition.mixin.rule;

import net.minecraft.village.TradeOffer;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeOffer.class)
public class TradeOfferMixin {

    //村民无限交易
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.villagerInfiniteTrade) {
            ci.cancel();
        }
    }
}

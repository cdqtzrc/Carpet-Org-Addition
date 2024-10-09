package org.carpetorgaddition.mixin.rule;

import net.minecraft.enchantment.SwiftSneakEnchantment;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwiftSneakEnchantment.class)
public class SwiftSneakEnchantmentMixin {

    // 可再生迅捷潜行
    @Inject(method = "isAvailableForEnchantedBookOffer", at = @At("HEAD"), cancellable = true)
    private void canOffer(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.renewableSwiftSneak) {
            cir.setReturnValue(true);
        }
    }
}

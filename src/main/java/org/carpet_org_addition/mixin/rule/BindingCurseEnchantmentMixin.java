package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.EnchantmentHelper;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//绑定诅咒无效化
@Mixin(EnchantmentHelper.class)
public class BindingCurseEnchantmentMixin {
    @Inject(method = "hasBindingCurse", at = @At("HEAD"), cancellable = true)
    private static void isCursed(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.bindingCurseInvalidation) {
            cir.setReturnValue(false);
        }
    }
}

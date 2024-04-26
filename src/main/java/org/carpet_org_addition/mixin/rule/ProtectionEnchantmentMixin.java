package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 保护类魔咒兼容
@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin extends Enchantment {
    public ProtectionEnchantmentMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.protectionEnchantmentCompatible) {
            cir.setReturnValue(super.canAccept(enchantment));
        }
    }
}

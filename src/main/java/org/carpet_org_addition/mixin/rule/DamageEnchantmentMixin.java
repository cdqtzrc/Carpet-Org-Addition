package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 伤害类附魔兼容
@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin extends Enchantment {
    public DamageEnchantmentMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.damageEnchantmentCompatible) {
            cir.setReturnValue(super.canAccept(enchantment));
        }
    }
}

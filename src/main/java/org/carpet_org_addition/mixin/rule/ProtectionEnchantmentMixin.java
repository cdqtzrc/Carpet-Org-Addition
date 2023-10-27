package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//保护类魔咒兼容
@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin extends Enchantment {
    protected ProtectionEnchantmentMixin(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Inject(method = "canAccept", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.protectionEnchantmentCompatible) {
            cir.setReturnValue(super.canAccept(enchantment));
        }
    }
}

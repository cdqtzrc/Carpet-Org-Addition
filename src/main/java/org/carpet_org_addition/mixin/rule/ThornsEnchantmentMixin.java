package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//荆棘不额外损耗耐久
@Mixin(ThornsEnchantment.class)
public class ThornsEnchantmentMixin {
    @Inject(method = "onUserDamaged", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), cancellable = true)
    private void damage(LivingEntity user, Entity attacker, int level, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.thornsDamageDurability) {
            ci.cancel();
        }
    }
}

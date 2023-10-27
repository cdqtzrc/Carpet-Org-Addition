package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {
    //生命恢复带有饱和效果
    @Inject(method = "applyUpdateEffect", at = @At("HEAD"))
    private void applyUpdateEffect(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.regenerationSaturation) {
            if ((Object) this == StatusEffects.REGENERATION) {
                if (entity instanceof PlayerEntity player && !player.getWorld().isClient) {
                    player.getHungerManager().add(1, 1.0f);
                }
            }
        }
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin {
    //和平的苦力怕
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void setTarget(LivingEntity target, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.peacefulCreeper && target instanceof PlayerEntity) {
            ci.cancel();
        }
    }

    // 闪电苦力怕同时炸死多个生物时每个都掉落头颅
    @Inject(method = "shouldDropHead", at = @At("HEAD"), cancellable = true)
    private void shouldDropHead(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.superChargedCreeper) {
            cir.setReturnValue(true);
        }
    }
}
package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//猪灵快速交易
@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {
    @Inject(method = "setAdmiringItem", at = @At("HEAD"), cancellable = true)
    private static void setAdmiringItem(LivingEntity entity, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.customPiglinBarteringTime != -1) {
            entity.getBrain().remember(MemoryModuleType.ADMIRING_ITEM, true, CarpetOrgAdditionSettings.customPiglinBarteringTime);
            ci.cancel();
        }
    }
}

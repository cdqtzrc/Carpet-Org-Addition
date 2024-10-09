package org.carpetorgaddition.mixin.rule;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.BlockPos;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//禁用钓鱼开放水域检测
@Mixin(FishingBobberEntity.class)
public class FishingMixin {
    @Inject(method = "isOpenOrWaterAround", at = @At("HEAD"), cancellable = true)
    private void isOpenOrWaterAround(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disableOpenOrWaterDetection) {
            cir.setReturnValue(true);
        }
    }
}

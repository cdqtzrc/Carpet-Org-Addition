package org.carpetorgaddition.mixin.rule;

import net.minecraft.entity.passive.IronGolemEntity;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//铁傀儡可以骑
@Mixin(IronGolemEntity.class)
public class IronGolemEntityMixin {
    //禁止铁傀儡攻击玩家
    @Inject(method = "isPlayerCreated", at = @At("HEAD"), cancellable = true)
    private void isPlayerCreated(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disableIronGolemAttackPlayer) {
            cir.setReturnValue(true);
        }
    }
}

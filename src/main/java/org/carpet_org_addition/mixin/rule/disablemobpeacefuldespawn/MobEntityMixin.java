package org.carpet_org_addition.mixin.rule.disablemobpeacefuldespawn;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.mob.MobEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    // 禁止特定生物在和平模式下被清除
    @WrapOperation(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;isDisallowedInPeaceful()Z"))
    private boolean isDisallowedInPeaceful(MobEntity mob, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.disableMobPeacefulDespawn && (mob.isPersistent() || mob.cannotDespawn())) {
            return false;
        }
        return original.call(mob);
    }
}

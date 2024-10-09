package org.carpetorgaddition.mixin.rule.disablemobpeacefuldespawn;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.boss.WitherEntity;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherEntity.class)
public class WitherEntityMixin {
    // 禁止特定生物在和平模式下被清除（凋灵）
    @WrapOperation(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;isDisallowedInPeaceful()Z"))
    private boolean isDisallowedInPeaceful(WitherEntity wither, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.disableMobPeacefulDespawn && (wither.isPersistent() || wither.cannotDespawn())) {
            return false;
        }
        return original.call(wither);
    }
}

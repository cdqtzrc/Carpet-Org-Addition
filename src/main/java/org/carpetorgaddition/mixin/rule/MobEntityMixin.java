package org.carpetorgaddition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Unique
    private final MobEntity thisMob = (MobEntity) (Object) this;

    // 生物是否可以捡起物品
    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;canPickUpLoot()Z"))
    private boolean canPickUpLoot(MobEntity instance, Operation<Boolean> original) {
        return switch (CarpetOrgAdditionSettings.mobWhetherOrNotCanPickItem) {
            case YES -> true;
            case VANILLA -> original.call(instance);
            case NO -> false;
            case YES_ONLY_HOSTILE -> thisMob instanceof HostileEntity || original.call(instance);
            case NO_ONLY_HOSTILE -> !(thisMob instanceof HostileEntity) && original.call(instance);
        };
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private final Entity thisEntity = (Entity) (Object) this;

    // 登山船
    @Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true)
    private void getStepHeight(CallbackInfoReturnable<Float> cir) {
        if (CarpetOrgAdditionSettings.climbingBoat
                && thisEntity instanceof BoatEntity boatEntity
                && boatEntity.getControllingPassenger() instanceof PlayerEntity) {
            cir.setReturnValue(1.0F);
        }
    }
}

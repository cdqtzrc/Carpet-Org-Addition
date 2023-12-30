package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderPearlEntity.class)
public class EnderPearlEntityMixin {
    //无伤末影珍珠
    @Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean damage(Entity entity, DamageSource damageSource, float amount) {
        if (CarpetOrgAdditionSettings.notDamageEnderPearl) {
            return false;
        }
        return entity.damage(damageSource, 5F);
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public class BowItemMixin {
    //高精度弓
    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"))
    private void setVelocity(PersistentProjectileEntity instance, Entity entity, float pitch, float yam, float roll, float speed, float divergence) {
        instance.setVelocity(entity, pitch, yam, roll, speed, CarpetOrgAdditionSettings.highPrecisionBow ? 0.0F : divergence);
    }
}
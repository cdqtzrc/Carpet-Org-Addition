package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.mob.AbstractPiglinEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//禁止猪灵僵尸化
@Mixin(AbstractPiglinEntity.class)
public class AbstractPiglinEntityMixin {
    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/AbstractPiglinEntity;shouldZombify()Z"))
    private boolean mobTick(AbstractPiglinEntity piglinEntity) {
        if (CarpetOrgAdditionSettings.disablePiglinZombify) {
            return false;
        }
        return piglinEntity.shouldZombify();
    }
}

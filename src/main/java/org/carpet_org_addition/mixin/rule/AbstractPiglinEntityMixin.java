package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.mob.AbstractPiglinEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//禁止猪灵僵尸化
@Mixin(AbstractPiglinEntity.class)
public class AbstractPiglinEntityMixin {
    @Inject(method = "shouldZombify", at = @At("HEAD"), cancellable = true)
    private void shouldZombify(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disablePiglinZombify) {
            cir.setReturnValue(false);
        }
    }
}

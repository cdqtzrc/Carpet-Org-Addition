package org.carpetorgaddition.mixin.rule;

import net.minecraft.entity.mob.AbstractPiglinEntity;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractPiglinEntity.class)
public class AbstractPiglinEntityMixin {

    //禁止猪灵僵尸化
    @Inject(method = "shouldZombify", at = @At("HEAD"), cancellable = true)
    private void shouldZombify(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disablePiglinZombify) {
            cir.setReturnValue(false);
        }
    }
}

package org.carpetorgaddition.mixin.rule.canminespawner;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobSpawnerBlockEntity.class)
public class MobSpawnerBlockEntityMixin {
    @Inject(method = "copyItemDataRequiresOperator", at = @At("HEAD"), cancellable = true)
    private void copyItemDataRequiresOperator(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.canMineSpawner) {
            cir.setReturnValue(false);
        }
    }
}

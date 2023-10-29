package org.carpet_org_addition.mixin.rule;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class BiomeMixin {
    // 禁止水结冰
    @Inject(method = "canSetIce(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void canSetIce(WorldView world, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.disableWaterFreezes) {
            cir.setReturnValue(false);
        }
    }
}

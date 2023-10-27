package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.TurtleEggBlock;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//海龟蛋快速孵化
@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {
    @Inject(method = "shouldHatchProgress", at = @At("HEAD"), cancellable = true)
    private void progress(World world, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.turtleEggFastHatch) {
            cir.setReturnValue(true);
        }
    }
}

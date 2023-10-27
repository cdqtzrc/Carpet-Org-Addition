package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.KelpBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AbstractPlantStemBlock.class)
public class AbstractPlantStemBlockMixin {
    //阻止海带生长
    @Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
    private void growth(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.disableKelpGrow && state.getBlock() instanceof KelpBlock) {
            ci.cancel();
        }
    }
}

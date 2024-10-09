package org.carpetorgaddition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepeaterBlock.class)
public class RepeaterBlockMixin {
    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void preventDrop(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        // 防止中继器掉落
        if (CarpetOrgAdditionSettings.simpleUpdateSkipper && direction == Direction.DOWN) {
            cir.setReturnValue(state);
        }
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    /**
     * 防止修改硬度的基岩被活塞推动
     * {@link AbstractBlockMixin}
     */
    @Redirect(method = "isMovable", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private static float canMove(BlockState blockState, BlockView blockView, BlockPos pos) {
        if (blockState.getBlock() == Blocks.BEDROCK) {
            return -1;
        }
        return blockState.getHardness(blockView, pos);
    }
}

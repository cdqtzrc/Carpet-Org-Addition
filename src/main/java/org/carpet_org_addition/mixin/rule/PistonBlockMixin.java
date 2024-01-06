package org.carpet_org_addition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    /**
     * 防止修改硬度的基岩被活塞推动
     * {@link AbstractBlockMixin}
     */
    @WrapOperation(method = "isMovable", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
    private static float canMove(BlockState instance, BlockView blockView, BlockPos blockPos, Operation<Float> original) {
        if (instance.getBlock() == Blocks.BEDROCK) {
            return -1;
        }
        return original.call(instance, blockView, blockPos);
    }
}

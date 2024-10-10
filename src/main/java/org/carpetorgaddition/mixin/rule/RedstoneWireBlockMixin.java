package org.carpetorgaddition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 红石线不会连接到打开的活板门上的红石线
@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    // 红石线不会连接到打开的活板门上的红石线
    @WrapOperation(method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
    private Block getBlock(BlockState instance, Operation<Block> original) {
        if (CarpetOrgAdditionSettings.simpleUpdateSkipper) {
            return null;
        }
        return original.call(instance);
    }

    // 防止红石线掉落
    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void preventDrop(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir) {
        if (CarpetOrgAdditionSettings.simpleUpdateSkipper && direction == Direction.DOWN) {
            cir.setReturnValue(state);
        }
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(IceBlock.class)
public class IceBlockMixin {
    //冰被破坏时不需要下方为可阻止移动的方块就可以变成水
    @SuppressWarnings("deprecation")
    @Inject(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;blocksMovement()Z"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        BlockState blockState = world.getBlockState(pos.down());
        if ((CarpetOrgAdditionSettings.iceBreakPlaceWater || blockState.blocksMovement()) || blockState.isLiquid()) {
            world.setBlockState(pos, IceBlock.getMeltedState());
        }
        ci.cancel();
    }
}

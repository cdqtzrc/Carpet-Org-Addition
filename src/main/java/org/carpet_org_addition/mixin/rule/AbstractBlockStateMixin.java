package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.helpers.BlockHardnessModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Shadow
    public abstract Block getBlock();

    /**
     * 修改硬度的基岩不会被推动
     * {@link PistonBlockMixin}
     */
    @Inject(method = "getHardness", at = @At("HEAD"), cancellable = true)
    public void getBlockHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        Optional<Float> optional = BlockHardnessModifiers.getHardness(this.getBlock());
        optional.ifPresent(cir::setReturnValue);
    }

    //蓝冰上不能刷怪
    @Inject(method = "allowsSpawning", at = @At(value = "HEAD"), cancellable = true)
    private void canSpawn(BlockView world, BlockPos pos, EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.blueIceCanSpawn && this.getBlock() == Blocks.BLUE_ICE) {
            cir.setReturnValue(false);
        }
    }
}

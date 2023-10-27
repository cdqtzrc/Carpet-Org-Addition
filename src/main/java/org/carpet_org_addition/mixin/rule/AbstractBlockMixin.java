package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockMixin {
    @Shadow
    public abstract Block getBlock();

    /**
     * 修改硬度的基岩不会被推动
     * {@link PistonBlockMixin}
     */
    //用于修改基岩硬度
    //抄的Carpet AMS
    @Inject(method = "getHardness", at = @At("TAIL"), cancellable = true)
    public void getBlockHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float bedrockHardness = CarpetOrgAdditionSettings.setBedrockHardness;
        if (bedrockHardness < 0) {
            bedrockHardness = -1;
        }
        if (this.getBlock() == Blocks.BEDROCK) {
            cir.setReturnValue(bedrockHardness);
        }
    }

    //蓝冰上不能刷怪
    @Inject(method = "allowsSpawning", at = @At(value = "HEAD"), cancellable = true)
    private void canSpawn(BlockView world, BlockPos pos, EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.blueIceCanSpawn && getBlock() == Blocks.BLUE_ICE) {
            cir.setReturnValue(false);
        }
    }
}


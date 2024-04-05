package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 让玩家手动放置的幽匿尖啸体可以生成监守者
@Mixin(SculkShriekerBlock.class)
public class SculkShriekerBlockMixin {
    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    public void getPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        if (CarpetOrgAdditionSettings.sculkShriekerCanSummon) {
            cir.setReturnValue(getBlockState(ctx));
        }
    }

    @Unique
    private BlockState getBlockState(ItemPlacementContext ctx) {
        SculkShriekerBlock sculkShriekerBlock = (SculkShriekerBlock) (Object) this;
        return sculkShriekerBlock.getDefaultState().with(SculkShriekerBlock.WATERLOGGED,
                        ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER)
                .with(SculkShriekerBlock.CAN_SUMMON, true);
    }
}

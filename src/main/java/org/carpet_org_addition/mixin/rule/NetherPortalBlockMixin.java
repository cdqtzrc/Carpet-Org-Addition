package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//下界传送门方块不更新
@Mixin(NetherPortalBlock.class)
@SuppressWarnings("deprecation")
public abstract class NetherPortalBlockMixin extends Block {
    public NetherPortalBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public abstract BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                         WorldAccess world, BlockPos pos, BlockPos neighborPos);

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    private void update(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos,
                        BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (CarpetOrgAdditionSettings.disablePortalUpdate) {
            cir.setReturnValue(super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos));
        }
    }
}

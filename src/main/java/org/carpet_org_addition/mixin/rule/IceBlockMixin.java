package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(IceBlock.class)
public class IceBlockMixin {
    @SuppressWarnings({"deprecation"})
    //冰被破坏时不需要下方为可阻止移动的方块就可以变成水
    @Redirect(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;blocksMovement()Z"))
    private boolean blocksMovement(BlockState blockState) {
        if (CarpetOrgAdditionSettings.iceBreakPlaceWater) {
            return true;
        }
        return blockState.blocksMovement();
    }
}

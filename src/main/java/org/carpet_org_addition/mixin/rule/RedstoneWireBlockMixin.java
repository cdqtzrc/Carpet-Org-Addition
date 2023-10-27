package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//红石线不会连接到打开的活板门上的红石线
@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    @Redirect(method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
    private Block getBlock(BlockState instance) {
        if (CarpetOrgAdditionSettings.simpleUpdateSkipper) {
            return null;
        }
        return instance.getBlock();
    }
}

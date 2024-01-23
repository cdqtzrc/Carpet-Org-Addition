package org.carpet_org_addition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.IceBlock;
import net.minecraft.block.Material;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(IceBlock.class)
public class IceBlockMixin {
    //冰被破坏时不需要下方为可阻止移动的方块就可以变成水
    @WrapOperation(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Material;blocksMovement()Z"))
    private boolean afterBreak(Material instance, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.iceBreakPlaceWater) {
            return true;
        }
        return original.call(instance);
    }
}

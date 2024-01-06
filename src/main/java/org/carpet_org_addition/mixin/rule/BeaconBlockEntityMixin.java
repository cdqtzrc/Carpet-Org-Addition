package org.carpet_org_addition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.Box;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconBlockEntity.class)
//大范围信标
public class BeaconBlockEntityMixin {
    @WrapOperation(method = "applyPlayerEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"))
    private static Box box(Box box, double x, double y, double z, Operation<Box> original) {
        Box range = box.stretch(x, y, z);
        if (CarpetOrgAdditionSettings.wideRangeBeacon) {
            return range.expand(50).stretch(0, -384, 0);
        }
        return range;
    }
}

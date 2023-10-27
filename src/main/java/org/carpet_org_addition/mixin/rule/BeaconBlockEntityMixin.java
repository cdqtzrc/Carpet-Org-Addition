package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.Box;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconBlockEntity.class)
//大范围信标
public class BeaconBlockEntityMixin {
    @Redirect(method = "applyPlayerEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;stretch(DDD)Lnet/minecraft/util/math/Box;"))
    private static Box box(Box box, double x, double y, double z) {
        if (CarpetOrgAdditionSettings.wideRangeBeacon) {
            return box.stretch(x, y, z).expand(50).stretch(0, -384, 0);
        }
        return box.stretch(x, y, z);
    }
}

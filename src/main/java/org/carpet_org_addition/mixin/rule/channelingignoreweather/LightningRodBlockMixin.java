package org.carpet_org_addition.mixin.rule.channelingignoreweather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningRodBlock.class)
public class LightningRodBlockMixin {
    @WrapOperation(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"))
    //避雷针 击中避雷针时产生雷电
    private boolean isThundering(World world, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.channelingIgnoreWeather) {
            return true;
        }
        return original.call(world);
    }
}

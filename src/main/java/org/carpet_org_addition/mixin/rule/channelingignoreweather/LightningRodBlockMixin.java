package org.carpet_org_addition.mixin.rule.channelingignoreweather;

import net.minecraft.block.LightningRodBlock;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningRodBlock.class)
public class LightningRodBlockMixin {
    @Redirect(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"))
    //避雷针 击中避雷针时产生雷电
    private boolean isThundering(World world) {
        if (CarpetOrgAdditionSettings.channelingIgnoreWeather) {
            return true;
        }
        return world.isThundering();
    }
}

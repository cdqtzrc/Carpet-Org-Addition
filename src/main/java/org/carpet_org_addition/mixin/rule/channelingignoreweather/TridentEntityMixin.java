package org.carpet_org_addition.mixin.rule.channelingignoreweather;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//强化引雷
@Mixin(TridentEntity.class)
public class TridentEntityMixin {
    @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isThundering()Z"))
    //击中实体时产生闪电
    private boolean isThundering(World world) {
        if (CarpetOrgAdditionSettings.channelingIgnoreWeather) {
            return true;
        }
        return world.isThundering();
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//激流忽略天气
@Mixin(TridentItem.class)
public class TridentItemMixin {
    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean isTouchingWaterOrRain(PlayerEntity playerEntity) {
        if (CarpetOrgAdditionSettings.riptideIgnoreWeather) {
            return true;
        }
        return playerEntity.isTouchingWaterOrRain();
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean useIsTouchingWaterOrRain(PlayerEntity playerEntity) {
        if (CarpetOrgAdditionSettings.riptideIgnoreWeather) {
            return true;
        }
        return playerEntity.isTouchingWaterOrRain();
    }
}

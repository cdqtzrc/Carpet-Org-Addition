package org.carpetorgaddition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//激流忽略天气
@Mixin(TridentItem.class)
public class TridentItemMixin {
    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean isTouchingWaterOrRain(PlayerEntity instance, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.riptideIgnoreWeather) {
            return true;
        }
        return original.call(instance);
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    private boolean useIsTouchingWaterOrRain(PlayerEntity instance, Operation<Boolean> original) {
        if (CarpetOrgAdditionSettings.riptideIgnoreWeather) {
            return true;
        }
        return original.call(instance);
    }
}

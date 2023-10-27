package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    //创造玩家免疫/kill
    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void kill(CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.creativeImmuneKill) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;
            if (livingEntity instanceof PlayerEntity player) {
                if (player.isCreative()) {
                    ci.cancel();
                }
            }
        }
    }

    //禁用伤害免疫
    @Redirect(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I", opcode = Opcodes.GETFIELD))
    private int setTimeUntilRegen(LivingEntity instance) {
        if (CarpetOrgAdditionSettings.disableDamageImmunity) {
            return 0;
        }
        return instance.timeUntilRegen;
    }
}
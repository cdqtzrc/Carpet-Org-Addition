package org.carpetorgaddition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ItemEntity;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//掉落物不消失
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Shadow
    private int itemAge;

    @WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ItemEntity;itemAge:I", opcode = Opcodes.GETFIELD))
    private int itemTick(ItemEntity instance, Operation<Integer> original) {
        if (CarpetOrgAdditionSettings.itemNeverDespawn) {
            if (itemAge > 5995) {
                itemAge--;
            }
        }
        return original.call(instance);
    }
}

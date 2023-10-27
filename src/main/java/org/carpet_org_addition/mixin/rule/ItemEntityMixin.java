package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.ItemEntity;
import org.objectweb.asm.Opcodes;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//掉落物不消失
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Shadow
    private int itemAge;

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ItemEntity;itemAge:I", opcode = Opcodes.GETFIELD))
    private int itemTick(ItemEntity instance) {
        if (CarpetOrgAdditionSettings.itemNeverDespawn) {
            if (itemAge > 5995) {
                itemAge--;
            }
        }
        return itemAge;
    }
}

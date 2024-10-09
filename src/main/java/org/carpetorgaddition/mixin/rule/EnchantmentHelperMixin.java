package org.carpet_org_addition.mixin.rule;

import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    // 绑定诅咒无效化
    @Inject(method = "hasAnyEnchantmentsWith", at = @At("HEAD"), cancellable = true)
    private static void hasAnyEnchantmentsWith(ItemStack stack, ComponentType<?> componentType, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.bindingCurseInvalidation && EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE.equals(componentType)) {
            cir.setReturnValue(false);
        }
    }
}

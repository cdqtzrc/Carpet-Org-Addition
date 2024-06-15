package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 击退棒
@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Unique
    private final Enchantment thisEnchantment = (Enchantment) (Object) this;

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.knockbackStick
                && thisEnchantment.equals(Enchantments.KNOCKBACK)
                && stack.getItem() == Items.STICK) {
            cir.setReturnValue(true);
        }
    }
}
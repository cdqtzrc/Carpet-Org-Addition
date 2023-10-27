package org.carpet_org_addition.mixin.rule;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.KnockbackEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;

//击退棒
@Mixin(KnockbackEnchantment.class)
public class KnockbackEnchantmentMixin extends Enchantment {

    protected KnockbackEnchantmentMixin(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (CarpetOrgAdditionSettings.knockbackStick) {
            return stack.getItem() == Items.STICK || super.isAcceptableItem(stack);
        }
        return super.isAcceptableItem(stack);
    }
}

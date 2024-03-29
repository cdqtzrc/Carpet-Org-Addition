package org.carpet_org_addition.mixin.rule;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.SmithingScreenHandler;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin {
    @Shadow
    protected abstract List<ItemStack> getInputStacks();

    // 可重复使用的锻造模板
    @Inject(method = "decrementStack", at = @At("HEAD"), cancellable = true)
    private void decrement(int slot, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.reusableSmithingTemplate && slot == 0) {
            ItemStack itemStack = this.getInputStacks().get(slot);
            if (itemStack.isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) || itemStack.isIn(ItemTags.TRIM_TEMPLATES)) {
                ci.cancel();
            }
        }
    }
}
package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MiningToolItem.class)
public abstract class MiningToolItemMixin {

    @Shadow
    @Final
    protected float miningSpeed;

    //将镐作为基岩的有效采集工具
    @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    private void mining(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (CarpetOrgAdditionSettings.pickaxeMinedBedrock && state.getBlock() == Blocks.BEDROCK) {
            MiningToolItem toolItem = (MiningToolItem) (Object) this;
            if (toolItem instanceof PickaxeItem) {
                cir.setReturnValue(this.miningSpeed);
            }
        }
    }
}

package org.carpetorgaddition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    // 将镐作为基岩的有效采集工具
    @Inject(method = "getMiningSpeed", at = @At("HEAD"), cancellable = true)
    private void miningSpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (CarpetOrgAdditionSettings.pickaxeMinedBedrock && state.getBlock() == Blocks.BEDROCK) {
            ToolComponent tool = stack.get(DataComponentTypes.TOOL);
            if (tool == null) {
                return;
            }
            cir.setReturnValue(tool.getSpeed(Blocks.STONE.getDefaultState()));
        }
    }
}

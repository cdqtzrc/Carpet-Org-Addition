package org.carpet_org_addition.mixin.rule;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.helpers.BlockHardnessModifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Shadow
    public abstract Block getBlock();

    /**
     * 修改硬度的基岩不会被推动
     * {@link PistonBlockMixin}
     */
    // 抄的Carpet AMS
    @Inject(method = "getHardness", at = @At("TAIL"), cancellable = true)
    public void getBlockHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        // 设置基岩硬度
        Block block = this.getBlock();
        if (block == Blocks.BEDROCK) {
            float bedrockHardness = CarpetOrgAdditionSettings.setBedrockHardness;
            if (bedrockHardness < 0) {
                bedrockHardness = -1;
            }
            cir.setReturnValue(bedrockHardness);
        }
    }

    // 修改深板岩系列方块硬度
    @ModifyReturnValue(method = "getHardness", at = @At("RETURN"))
    private float setDeepSlateHardness(float original) {
        if (CarpetOrgAdditionSettings.softDeepslate) {
            Block block = this.getBlock();
            return BlockHardnessModifiers.deepslateModifier(
                block,
                original,
                Blocks.DEEPSLATE,
                Blocks.CHISELED_DEEPSLATE,
                Blocks.POLISHED_DEEPSLATE,
                Blocks.DEEPSLATE_BRICK_SLAB,
                Blocks.DEEPSLATE_BRICK_STAIRS,
                Blocks.DEEPSLATE_BRICK_WALL,
                Blocks.POLISHED_DEEPSLATE_SLAB,
                Blocks.POLISHED_DEEPSLATE_STAIRS,
                Blocks.POLISHED_DEEPSLATE_WALL
            );
        } else {
            return original;
        }
    }

    // 修改普通石头矿石方块硬度
    @ModifyReturnValue(method = "getHardness", at = @At("RETURN"))
    private float setSimpleOreHardness(float original) {
        if (CarpetOrgAdditionSettings.softOres) {
            Block block = this.getBlock();
            return BlockHardnessModifiers.simpleOreModifier(
                block,
                original,
                Blocks.COAL_ORE,
                Blocks.IRON_ORE,
                Blocks.COPPER_ORE,
                Blocks.LAPIS_ORE,
                Blocks.GOLD_ORE,
                Blocks.REDSTONE_ORE,
                Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE
            );
        } else {
            return original;
        }
    }

    // 修改深板岩矿石方块硬度
    @ModifyReturnValue(method = "getHardness", at = @At("RETURN"))
    private float setDeepslateOreHardness(float original) {
        if (CarpetOrgAdditionSettings.softOres) {
            Block block = this.getBlock();
            return BlockHardnessModifiers.deepslateOreModifier(
                block,
                original,
                Blocks.DEEPSLATE_COAL_ORE,
                Blocks.DEEPSLATE_IRON_ORE,
                Blocks.DEEPSLATE_COPPER_ORE,
                Blocks.DEEPSLATE_LAPIS_ORE,
                Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.DEEPSLATE_REDSTONE_ORE,
                Blocks.DEEPSLATE_DIAMOND_ORE,
                Blocks.DEEPSLATE_EMERALD_ORE
            );
        } else {
            return original;
        }
    }

    // 修改深下界矿石方块硬度
    @ModifyReturnValue(method = "getHardness", at = @At("RETURN"))
    private float setNetherOreHardness(float original) {
        if (CarpetOrgAdditionSettings.softOres) {
            Block block = this.getBlock();
            return BlockHardnessModifiers.netherOreModifier(
                block,
                original,
                Blocks.NETHER_QUARTZ_ORE,
                Blocks.NETHER_GOLD_ORE
            );
        } else {
            return original;
        }
    }

    // 修改黑曜石方块硬度
    @ModifyReturnValue(method = "getHardness", at = @At("RETURN"))
    private float setObsidianHardness(float original) {
        if (CarpetOrgAdditionSettings.softObsidian && this.getBlock().equals(Blocks.OBSIDIAN)) {
            return Blocks.END_STONE.getHardness();
        } else {
            return original;
        }
    }

    //蓝冰上不能刷怪
    @Inject(method = "allowsSpawning", at = @At(value = "HEAD"), cancellable = true)
    private void canSpawn(BlockView world, BlockPos pos, EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOrgAdditionSettings.blueIceCanSpawn && this.getBlock() == Blocks.BLUE_ICE) {
            cir.setReturnValue(false);
        }
    }
}

package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
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
    //抄的Carpet AMS
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
        } else if (CarpetOrgAdditionSettings.softDeepslate) {
            // 易碎深板岩
            if (block == Blocks.DEEPSLATE || block == Blocks.CHISELED_DEEPSLATE
                    || block == Blocks.POLISHED_DEEPSLATE || block == Blocks.DEEPSLATE_BRICK_SLAB
                    || block == Blocks.DEEPSLATE_BRICK_STAIRS || block == Blocks.DEEPSLATE_BRICK_WALL
                    || block == Blocks.POLISHED_DEEPSLATE_SLAB || block == Blocks.POLISHED_DEEPSLATE_STAIRS
                    || block == Blocks.POLISHED_DEEPSLATE_WALL) {
                cir.setReturnValue(Blocks.STONE.getHardness());
            } else if (block == Blocks.COBBLED_DEEPSLATE || block == Blocks.COBBLED_DEEPSLATE_SLAB
                    || block == Blocks.COBBLED_DEEPSLATE_STAIRS || block == Blocks.COBBLED_DEEPSLATE_WALL
                    || block == Blocks.DEEPSLATE_BRICKS || block == Blocks.DEEPSLATE_TILES
                    || block == Blocks.DEEPSLATE_TILE_SLAB || block == Blocks.DEEPSLATE_TILE_STAIRS
                    || block == Blocks.DEEPSLATE_TILE_WALL || block == Blocks.CRACKED_DEEPSLATE_BRICKS
                    || block == Blocks.CRACKED_DEEPSLATE_TILES) {
                // 深板岩圆石
                cir.setReturnValue(Blocks.COBBLESTONE.getHardness());
            }
        } else if (block == Blocks.OBSIDIAN && CarpetOrgAdditionSettings.softObsidian) {
            // 易碎黑曜石
            cir.setReturnValue(Blocks.END_STONE.getHardness());
        } else {
            // 易碎矿石
            if (CarpetOrgAdditionSettings.softOres) {
                // 普通的石头矿石
                if (block == Blocks.COAL_ORE || block == Blocks.IRON_ORE
                        || block == Blocks.COPPER_ORE || block == Blocks.LAPIS_ORE
                        || block == Blocks.GOLD_ORE || block == Blocks.REDSTONE_ORE
                        || block == Blocks.DIAMOND_ORE || block == Blocks.EMERALD_ORE) {
                    cir.setReturnValue(Blocks.STONE.getHardness());
                    return;
                }
                // 深板岩矿石
                if (block == Blocks.DEEPSLATE_COAL_ORE || block == Blocks.DEEPSLATE_IRON_ORE
                        || block == Blocks.DEEPSLATE_COPPER_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE
                        || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE
                        || block == Blocks.DEEPSLATE_DIAMOND_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
                    cir.setReturnValue(Blocks.DEEPSLATE.getHardness());
                    return;
                }
                // 下界矿石
                if (block == Blocks.NETHER_QUARTZ_ORE || block == Blocks.NETHER_GOLD_ORE) {
                    cir.setReturnValue(Blocks.NETHERRACK.getHardness());
                }
            }
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

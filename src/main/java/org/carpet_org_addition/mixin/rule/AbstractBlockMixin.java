package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    private final AbstractBlock thisAbstractBlock = (AbstractBlock) (Object) this;

    // 所有矿石，但不含红石矿石
    private static final Block[] STONE_ORES = {Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE, Blocks.LAPIS_ORE,
            Blocks.GOLD_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE};
    // 所以深板岩矿石，不包含深层红石矿石
    private static final Block[] DEEPSLATE_STONE_ORES = {Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE};

    /**
     * 修改硬度的基岩不会被推动
     * {@link PistonBlockMixin}
     */
    //抄的Carpet AMS
    @Inject(method = "getHardness", at = @At("TAIL"), cancellable = true)
    public void getBlockHardness(CallbackInfoReturnable<Float> cir) {
        // 设置基岩硬度
        if (thisAbstractBlock == Blocks.BEDROCK) {
            float bedrockHardness = CarpetOrgAdditionSettings.setBedrockHardness;
            if (bedrockHardness < 0) {
                bedrockHardness = -1;
            }
            cir.setReturnValue(bedrockHardness);
        } else if (thisAbstractBlock == Blocks.DEEPSLATE && CarpetOrgAdditionSettings.softDeepslate) {
            // 易碎深板岩
            cir.setReturnValue(Blocks.STONE.getHardness());
        } else if (thisAbstractBlock == Blocks.OBSIDIAN && CarpetOrgAdditionSettings.softObsidian) {
            // 易碎黑曜石
            cir.setReturnValue(Blocks.END_STONE.getHardness());
        } else {
            // 易碎矿石
            if (CarpetOrgAdditionSettings.softOres) {
                if (thisAbstractBlock instanceof ExperienceDroppingBlock) {
                    // 普通的石头矿石
                    for (Block ore : STONE_ORES) {
                        if (thisAbstractBlock == ore) {
                            cir.setReturnValue(Blocks.STONE.getHardness());
                            return;
                        }
                    }
                    // 深板岩矿石
                    for (Block ore : DEEPSLATE_STONE_ORES) {
                        if (thisAbstractBlock == ore) {
                            cir.setReturnValue(Blocks.DEEPSLATE.getHardness());
                            return;
                        }
                    }
                    // 下界石英矿石和下界金矿石
                    if (thisAbstractBlock == Blocks.NETHER_QUARTZ_ORE || thisAbstractBlock == Blocks.NETHER_GOLD_ORE) {
                        cir.setReturnValue(Blocks.NETHERRACK.getHardness());
                    }
                } else if (thisAbstractBlock == Blocks.REDSTONE_ORE) {
                    // 红石矿石
                    cir.setReturnValue(Blocks.STONE.getHardness());
                } else if (thisAbstractBlock == Blocks.DEEPSLATE_REDSTONE_ORE) {
                    // 深层红石矿石
                    cir.setReturnValue(Blocks.DEEPSLATE.getHardness());
                }
            }
        }
    }
}

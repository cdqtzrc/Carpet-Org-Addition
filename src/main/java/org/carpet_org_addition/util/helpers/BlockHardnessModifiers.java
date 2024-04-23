package org.carpet_org_addition.util.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class BlockHardnessModifiers {
    public static float deepslateModifier(Block getBlock, float original, Block... blocks) {
        for (Block block : blocks) {
            if (getBlock == block) {
                return Blocks.COBBLESTONE.getHardness();
            }
        }
        return original;
    }

    public static float simpleOreModifier(Block getBlock, float original, Block... blocks) {
        for (Block block : blocks) {
            if (getBlock == block) {
                return Blocks.STONE.getHardness();
            }
        }
        return original;
    }

    public static float deepslateOreModifier(Block getBlock, float original, Block... blocks) {
        for (Block block : blocks) {
            if (getBlock == block) {
                return Blocks.DEEPSLATE.getHardness();
            }
        }
        return original;
    }

    public static float netherOreModifier(Block getBlock, float original, Block... blocks) {
        for (Block block : blocks) {
            if (getBlock == block) {
                return Blocks.NETHERRACK.getHardness();
            }
        }
        return original;
    }
}

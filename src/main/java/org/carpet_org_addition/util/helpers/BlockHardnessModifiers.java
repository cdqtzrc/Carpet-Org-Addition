package org.carpet_org_addition.util.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.carpet_org_addition.CarpetOrgAdditionSettings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("SpellCheckingInspection")
public class BlockHardnessModifiers {
    /**
     * 深板岩和与深板岩硬度相同的变种
     */
    private static final List<Block> DEEPSLATE = Stream.of(Blocks.DEEPSLATE, Blocks.CHISELED_DEEPSLATE,
            Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICK_STAIRS,
            Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE_STAIRS,
            Blocks.POLISHED_DEEPSLATE_WALL).toList();

    /**
     * 深板岩圆石和与深板岩圆石硬度相同的变种
     */
    private static final List<Block> COBBLED_DEEPSLATE = Stream.of(Blocks.COBBLED_DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE_WALL,
            Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILE_STAIRS,
            Blocks.DEEPSLATE_TILE_WALL, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_TILES).toList();

    /**
     * 普通矿石
     */
    private static final List<Block> ORE = Stream.of(Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE,
            Blocks.LAPIS_ORE, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE).toList();

    /**
     * 深层矿石
     */
    private static final List<Block> DEEPSLATE_ORE = Stream.of(Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_EMERALD_ORE).toList();

    /**
     * 下界矿石
     */
    private static final List<Block> NETHER_ORE = Stream.of(Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE).toList();

    // 获取方块硬度
    public static Optional<Float> getHardness(Block block) {
        // 设置基岩硬度
        if (block == Blocks.BEDROCK) {
            float bedrockHardness = CarpetOrgAdditionSettings.setBedrockHardness;
            return Optional.of(bedrockHardness < 0 ? -1 : bedrockHardness);
        }
        // 易碎深板岩
        if (CarpetOrgAdditionSettings.softDeepslate) {
            // 深板岩
            if (DEEPSLATE.contains(block)) {
                return Optional.of(Blocks.STONE.getHardness());
            }
            // 深板岩圆石
            if (COBBLED_DEEPSLATE.contains(block)) {
                return Optional.of(Blocks.COBBLESTONE.getHardness());
            }
        }
        // 易碎黑曜石
        if (CarpetOrgAdditionSettings.softObsidian && block == Blocks.OBSIDIAN) {
            return Optional.of(Blocks.END_STONE.getHardness());
        }
        // 易碎矿石
        if (CarpetOrgAdditionSettings.softOres) {
            // 普通矿石
            if (ORE.contains(block)) {
                return Optional.of(Blocks.STONE.getHardness());
            }
            // 深层矿石
            if (DEEPSLATE_ORE.contains(block)) {
                return Optional.of(Blocks.DEEPSLATE.getHardness());
            }
            // 下界矿石
            if (NETHER_ORE.contains(block)) {
                return Optional.of(Blocks.NETHERRACK.getHardness());
            }
        }
        return Optional.empty();
    }
}

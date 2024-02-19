package org.carpet_org_addition.util;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class WorldUtils {
    public static final String OVERWORLD = "minecraft:overworld";
    public static final String THE_NETHER = "minecraft:the_nether";
    public static final String THE_END = "minecraft:the_end";

    /**
     * 获取区域内所有方块坐标的集合
     *
     * @param box 用来指定的区域盒子对象
     * @return 盒子内所有的方块坐标
     */
    public static ArrayList<BlockPos> allBlockPos(Box box) {
        int endX = (int) box.maxX;
        int endY = (int) box.maxY;
        int endZ = (int) box.maxZ;
        ArrayList<BlockPos> list = new ArrayList<>();
        for (int startX = (int) box.minX; startX < endX; startX++) {
            for (int startY = (int) box.minY; startY < endY; startY++) {
                for (int startZ = (int) box.minZ; startZ < endZ; startZ++) {
                    list.add(new BlockPos(startX, startY, startZ));
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unused")
    public static Formatting getColor(String dimension) {
        if (dimension == null) {
            return Formatting.GREEN;
        }
        return switch (dimension) {
            case OVERWORLD -> Formatting.GREEN;
            case THE_NETHER -> Formatting.RED;
            case THE_END -> Formatting.DARK_PURPLE;
            default -> Formatting.WHITE;
        };
    }
}

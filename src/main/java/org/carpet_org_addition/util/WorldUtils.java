package org.carpet_org_addition.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;

public class WorldUtils {
    private WorldUtils() {
    }

    public static final String OVERWORLD = "minecraft:overworld";
    public static final String THE_NETHER = "minecraft:the_nether";
    public static final String THE_END = "minecraft:the_end";

    /**
     * 获取区域内所有方块坐标的集合
     *
     * @param box 用来指定的区域盒子对象
     * @return 盒子内所有的方块坐标
     */
    @SuppressWarnings("unused")
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

    /**
     * 获取方块坐标的字符串形式
     *
     * @param blockPos 要转换为字符串形式的方块位置对象
     * @return 方块坐标的字符串形式
     */
    public static String toPosString(BlockPos blockPos) {
        return blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
    }

    /**
     * 获取当前维度的ID
     *
     * @param world 当前世界的对象
     * @return 当前维度的ID
     */
    public static String getDimensionId(World world) {
        return world.getRegistryKey().getValue().toString();
    }

    /**
     * 根据维度获取世界对象
     *
     * @param server    游戏当前的服务器
     * @param dimension 一个维度的id
     */
    public static ServerWorld getWorld(MinecraftServer server, String dimension) {
        String[] split = dimension.split(":");
        Identifier identifier;
        if (split.length == 1) {
            identifier = new Identifier("minecraft", dimension);
        } else if (split.length == 2) {
            identifier = new Identifier(split[0], split[1]);
        } else {
            throw new IllegalArgumentException();
        }
        return server.getWorld(RegistryKey.of(RegistryKeys.WORLD, identifier));
    }

    /**
     * 将字符串解析为世界ID
     *
     * @param worldId 世界的ID，如果指定了命名空间，则使用指定的，否则使用minecraft
     * @return 世界类型的注册表项
     */
    public static RegistryKey<World> getWorld(String worldId) {
        if (worldId.contains(":")) {
            String[] split = worldId.split(":");
            if (split.length != 2) {
                throw new IllegalArgumentException();
            }
            return RegistryKey.of(RegistryKeys.WORLD, new Identifier(split[0], split[1]));
        }
        return RegistryKey.of(RegistryKeys.WORLD, new Identifier(worldId));
    }
}

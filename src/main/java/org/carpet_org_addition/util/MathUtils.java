package org.carpet_org_addition.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.carpet_org_addition.CarpetOrgAdditionSettings;

import static net.minecraft.server.network.ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE;

public class MathUtils {
    /**
     * 数学工具类，私有化构造方法
     */
    private MathUtils() {
    }

    /**
     * 根据经验等级和经验值计算总经验值<br/>
     *
     * @param level 经验等级
     * @param xp    经验值
     * @return 总经验值
     * @author ChatGPT
     */
    public static int getTotalExperience(int level, int xp) {
        int totalExp;
        // 0-16级
        if (level <= 16) {
            totalExp = level * level + 6 * level;
        }
        // 17-31级
        else if (level <= 31) {
            totalExp = (int) (2.5 * level * level - 40.5 * level + 360);
        }
        // 32级以上
        else {
            totalExp = (int) (4.5 * level * level - 162.5 * level + 2220);
        }
        //防止数值溢出
        int sum = totalExp + xp;
        return sum < 0 ? totalExp : sum;
    }

    /**
     * 获取两个方块坐标的距离的平方
     *
     * @param fromBlockPos 第一个方块坐标
     * @param toBlockPos   第二个方块坐标
     * @return 两个方块坐标距离的平方
     */
    public static double getBlockSquareDistance(BlockPos fromBlockPos, BlockPos toBlockPos) {
        int x = fromBlockPos.getX() - toBlockPos.getX();
        int y = fromBlockPos.getY() - toBlockPos.getY();
        int z = fromBlockPos.getZ() - toBlockPos.getZ();
        return x * x + y * y + z * z;
    }

    /**
     * 获取两个方块坐标的距离
     *
     * @param fromBlockPos 第一个方块的坐标
     * @param toBlockPos   第二个方块的坐标
     * @return 两个方块的距离
     */
    public static double getBlockDistance(BlockPos fromBlockPos, BlockPos toBlockPos) {
        return Math.sqrt(getBlockSquareDistance(fromBlockPos, toBlockPos));
    }

    /**
     * 获取两个方块坐标的整数距离(四舍五入)
     *
     * @param fromBlockPos 第一个方块的坐标
     * @param toBlockPos   第二个方块的坐标
     * @return 两个方块之间四舍五入的整数距离
     */
    public static int getBlockIntegerDistance(BlockPos fromBlockPos, BlockPos toBlockPos) {
        return (int) Math.round(Math.sqrt(getBlockSquareDistance(fromBlockPos, toBlockPos)));
    }

    /**
     * 在集合中从近到远排序<br/>
     * 计算两个方块坐标与源坐标的距离，用于在{@link java.util.TreeSet<BlockPos>}集合和{@link java.util.TreeMap<BlockPos>}集合中为定义的排序规则计算结果，分别计算两个方块坐标与原坐标的距离，根据结果返回整数，如果大于等于0，返回1，如果小于0，返回-1，这个方法比较的结果永远不会返回0，因为返回0，集合会认为这两个键是相同对象的而不存储，但是实际情况中经常会遇到两个方块坐标距离源方块坐标的距离相同的情况，这不代表两个方块坐标是同一个坐标。同样的，本方法不应使用类型转换返回结果，假设如果两个方块坐标距离源方块坐标距离的差为0.1，如果使用类型转换，即(int)0.1，那么返回值为0，与预期结果不符，此处使用三元运算符
     *
     * @param blockPos   源方块坐标
     * @param o1BlockPos 要在集合中添加的方块坐标
     * @param o2BlockPos 集合中已有的方块坐标
     * @return 根据距离返回1或-1
     */
    public static int compareBlockPos(final BlockPos blockPos, BlockPos o1BlockPos, BlockPos o2BlockPos) {
        double distance = getBlockSquareDistance(blockPos, o1BlockPos) - getBlockSquareDistance(blockPos, o2BlockPos);
        return distance >= 0 ? 1 : -1;
    }

    /**
     * 获取Carpet Org Addition设置的玩家最大交互距离并进行判断，小于0的值会被视为6.0，超过128的值会被视为128.0
     *
     * @return 当前设置的最大交互距离，最大不超过128
     */
    public static double getPlayerMaxInteractionDistance() {
        double distance = CarpetOrgAdditionSettings.maxBlockInteractionDistance;
        if (distance < 0) {
            return 6.0;
        }
        return Math.min(distance, 128.0);
    }

    /**
     * 获取玩家最大交互距离的平方
     *
     * @return 当前设置的最大交互距离，然后取平方
     */
    public static double getMaxBreakSquaredDistance() {
        return MathHelper.square(getPlayerMaxInteractionDistance());
    }

    /**
     * 获取游戏默认的交互距离的平方
     *
     * @return 默认的交互距离，取平方距离
     */
    public static double getDefaultInteractionDistance() {
        return MAX_BREAK_SQUARED_DISTANCE;
    }

    /**
     * 根据在主世界的指定位置获取下界的方块位置坐标
     */
    public static BlockPos getTheNetherPos(double xPos, double yPos, double zPos) {
        return new BlockPos((int) Math.round(xPos / 8), (int) Math.round(yPos), (int) Math.round(zPos / 8));
    }

    /**
     * 根据在主世界实体的位置获取对应下界的方块位置坐标
     *
     * @param entity 一个实体，根据这个实体的位置获取对应下界的方块位置
     */
    public static BlockPos getTheNetherPos(Entity entity) {
        return getTheNetherPos(entity.getX(), entity.getY(), entity.getZ());
    }

    /**
     * 根据指定下界位置坐标获取在主世界对应的坐标
     */
    public static BlockPos getOverworldPos(double xPos, double yPos, double zPos) {
        return new BlockPos((int) Math.round(xPos * 8), (int) Math.round(yPos), (int) Math.round(zPos * 8));
    }

    /**
     * 根据实体在下界的位置坐标获取对应主世界的方块位置坐标
     *
     * @param entity 一个实体，根据这个实体的位置获取在主世界对应的坐标
     */
    public static BlockPos getOverworldPos(Entity entity) {
        return getOverworldPos(entity.getX(), entity.getY(), entity.getZ());
    }
}

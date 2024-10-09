package org.carpetorgaddition.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.carpetorgaddition.rulevalidator.MaxBlockPlaceDistanceValidator;

import java.util.Random;
import java.util.StringJoiner;

public class MathUtils {
    /**
     * 数学工具类，私有化构造方法
     */
    private MathUtils() {
    }

    private static final Random RANDOM = new Random();

    /**
     * 获取两个方块坐标的距离的平方
     *
     * @param fromBlockPos 第一个方块坐标
     * @param toBlockPos   第二个方块坐标
     * @return 两个方块坐标距离的平方
     */
    public static double getBlockSquareDistance(BlockPos fromBlockPos, BlockPos toBlockPos) {
        // 虽然是两个整数相减，但此处的结果不能使用int接收
        // 因为整数和整数之间的运算结果只能是整数，而整数的取值范围相对有限，如果数值较大，有可能发生数值溢出
        double x = fromBlockPos.getX() - toBlockPos.getX();
        double y = fromBlockPos.getY() - toBlockPos.getY();
        double z = fromBlockPos.getZ() - toBlockPos.getZ();
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
     *
     * @param blockPos   源方块坐标
     * @param o1BlockPos 要在集合中添加的方块坐标
     * @param o2BlockPos 集合中已有的方块坐标
     * @return 根据距离返回1或-1
     */
    public static int compareBlockPos(final BlockPos blockPos, BlockPos o1BlockPos, BlockPos o2BlockPos) {
        // 坐标1到原坐标的距离
        double distance1 = getBlockSquareDistance(blockPos, o1BlockPos);
        // 坐标2到原坐标的距离
        double distance2 = getBlockSquareDistance(blockPos, o2BlockPos);
        // 比较两个距离的大小
        return Double.compare(distance1, distance2);
    }

    public static boolean isDefaultDistance() {
        return CarpetOrgAdditionSettings.maxBlockPlaceDistance == -1;
    }

    /**
     * 获取Carpet Org Addition设置的玩家最大交互距离并进行判断，小于0的值会被视为6.0，超过256的值会被视为256.0
     *
     * @return 当前设置的最大交互距离，最大不超过128
     */
    public static double getPlayerMaxInteractionDistance() {
        double distance = CarpetOrgAdditionSettings.maxBlockPlaceDistance;
        if (distance < 0) {
            return 6.0;
        }
        return Math.min(distance, MaxBlockPlaceDistanceValidator.MAX_VALUE);
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

    /**
     * 生成一次指定范围内随机整数，包含最大值和最小值，范围也可以包含负数，最大最小值参数也可以反过来传递
     *
     * @param min 随机数的最小值
     * @param max 随机数的最大值
     * @return 指定范围内的随机数
     */
    public static int randomInt(int min, int max) {
        if (max == min) {
            // 如果最大最小值相等，直接返回
            return max;
        }
        if (min > max) {
            // 如果最小值大于最大值，交换最大最小值
            int temp = max;
            max = min;
            min = temp;
        }
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * 判断一个整数是否介于两个整数之间，包括最大最小值，最大最小值反过来传递不影响结果
     *
     * @param max    范围的最大值
     * @param min    范围的最小值
     * @param number 要检查是否介于这两个数之间的数
     */
    public static boolean betweenTwoNumbers(int max, int min, int number) {
        if (min > max) {
            // 如果最小值大于最大值，交换最大最小值
            int temp = max;
            max = min;
            min = temp;
        }
        return max >= number && min <= number;
    }

    /**
     * 求一个小数数组里所有数的平均值
     *
     * @param args 小数数组
     * @return 数组内所有数的平均值
     */
    public static double average(double... args) {
        double sum = 0;
        for (double arg : args) {
            sum += arg;
        }
        return sum / args.length;
    }

    /**
     * 将一个浮点数数组中的每一个元素格式化为保留两位小数的字符串，然后拼接成一个大字符串，每个元素用空格隔开
     *
     * @param args 要格式化的浮点数数组
     * @return 每一个元素拼接后的大字符串
     */
    public static String keepTwoDecimalPlaces(double... args) {
        StringJoiner sj = new StringJoiner(" ");
        for (double arg : args) {
            sj.add(String.format("%.2f", arg));
        }
        return sj.toString();
    }
}
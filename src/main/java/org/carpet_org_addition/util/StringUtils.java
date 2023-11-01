package org.carpet_org_addition.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {
    /**
     * 字符串工具类，私有化构造方法
     */
    private StringUtils() {
    }

    /**
     * 获取方块坐标的字符串形式
     *
     * @param blockPos 要转换为字符串形式的方块位置对象
     * @return 方块坐标的字符串形式
     */
    public static String getBlockPosString(BlockPos blockPos) {
        return blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
    }

    /**
     * 获取一个带括号的方块坐标的字符串
     *
     * @param blockPos 要转换为字符串形式的方块位置对象
     * @return 方块坐标的带括号字符串形式
     */
    public static String getBracketedBlockPos(BlockPos blockPos) {
        return Texts.bracketed(Text.translatable("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ())).getString();
    }

    /**
     * 获取当前系统时间的字符串形式
     *
     * @return 当前系统时间的字符串形式
     */
    public static String getDateString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        return formatter.format(localDateTime);
    }

    /**
     * 获取物品的名称的字符串形式
     *
     * @param item 要获取名称的物品
     * @return 物品的字符串形式，即物品的名称
     */
    @Deprecated
    public static String getItemName(Item item) {
        return item.getName().getString();
    }

    /**
     * 获取方块名称的字符串形式
     *
     * @param block 要获取名称的方块
     * @return 方块的名称
     */
    @Deprecated
    public static String getBlockName(Block block) {
        return block.getName().getString();
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
     * 获取一名玩家的字符串形式的玩家名
     *
     * @param player 要获取字符串形式玩家名的玩家
     * @return 玩家名的字符串形式
     */
    public static String getPlayerName(PlayerEntity player) {
        return player.getName().getString();
    }
}

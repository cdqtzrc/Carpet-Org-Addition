package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.*;

import java.util.TreeSet;

public class BlockFinderCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("blockFinder").requires(source ->
                        CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandBlockFinder))
                .then(CommandManager.argument("block", BlockStateArgumentType.blockState(commandBuildContext))
                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 128))
                                .executes(context ->
                                        finder(context, BlockStateArgumentType.getBlockState(context, "block").getBlockState()
                                                .getBlock(), IntegerArgumentType.getInteger(context, "radius"))
                                )))
        );
    }

    //方块查找
    private static int finder(CommandContext<ServerCommandSource> context, Block block, int radius) throws CommandSyntaxException {
        //获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        //获取命令执行时的方块坐标
        final BlockPos sourceBlockPos = player.getBlockPos();
        TreeSet<BlockPos> treeSet = findBlock(player.getWorld(), sourceBlockPos, block, radius);
        int count = treeSet.size();
        sendFeedback(context, block, treeSet, player.getBlockPos());
        return count;
    }

    /**
     * 获取指定范围内所有指定方块的坐标
     *
     * @param world    要查找方块的世界对象
     * @param blockPos 查找范围的中心坐标
     * @param block    要查找的方块
     * @param radius   查找的范围，是一个边长为两倍radius，高度为整个世界高度的长方体
     * @return 包含所有查找到的方块坐标和该方块距离源方块坐标的距离的集合，并且已经从近到远排序
     */
    public static TreeSet<BlockPos> findBlock(World world, BlockPos blockPos, Block block, int radius) throws CommandSyntaxException {
        //创建TreeSet集合，用来记录找到的方块坐标
        TreeSet<BlockPos> treeSet = new TreeSet<>((o1, o2) -> MathUtils.compareBlockPos(blockPos, o1, o2));
        //获取三个坐标的最大值
        int maxX = blockPos.getX() + radius;
        int maxZ = blockPos.getZ() + radius;
        int maxHeight = world.getHeight();
        long currentTimeMillis = System.currentTimeMillis();
        //创建可变坐标对象
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
        //遍历整个三维空间，找到与目标方块匹配的方块
        for (int minX = blockPos.getX() - radius; minX <= maxX; minX++) {
            for (int minZ = blockPos.getZ() - radius; minZ <= maxZ; minZ++) {
                for (int bottomY = world.getBottomY(); bottomY <= maxHeight; bottomY++) {
                    if (System.currentTimeMillis() - currentTimeMillis > 3000) {
                        //3秒内未完成方块查找，通过抛出异常结束方法
                        throw CommandUtils.getException("carpet.commands.blockFinder.timeout");
                    }
                    //修改可变坐标的值
                    mutableBlockPos.set(minX, bottomY, minZ);
                    if (world.getBlockState(mutableBlockPos).getBlock() == block) {
                        //将找到的方块坐标添加到集合
                        treeSet.add(new BlockPos(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ()));
                    }
                }
            }
        }
        return treeSet;
    }

    //发送命令反馈
    public static void sendFeedback(CommandContext<ServerCommandSource> context, Block block, TreeSet<BlockPos> treeSet, BlockPos sourceBlockPos) {
        int size = treeSet.size();
        //判断集合中是否有元素
        if (size == 0) {
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.blockFinder.not_found_block"
                    , TextUtils.getBlockName(block));
        } else {
            //在聊天栏输出方块坐标消息
            int count = 0;
            if (size > 10) {
                SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.blockFinder.find", size, TextUtils.getBlockName(block));
                for (BlockPos blockPos : treeSet) {
                    count++;
                    if (count > 10) {
                        break;
                    }
                    sendFeedback(context, sourceBlockPos, count, blockPos);
                }
            } else {
                SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.blockFinder.find.not_more_than_ten", size, TextUtils.getBlockName(block));
                for (BlockPos blockPos : treeSet) {
                    count++;
                    sendFeedback(context, sourceBlockPos, count, blockPos);
                }
            }
        }
    }

    //发送反馈
    private static void sendFeedback(CommandContext<ServerCommandSource> context, BlockPos sourceBlockPos, int count, BlockPos blockPos) {
        if (CarpetOrgAdditionSettings.canParseWayPoint) {
            //如果规则可解析路径点启用，发送不带有特殊样式的文本
            SendMessageUtils.sendStringMessage(context.getSource(),
                    StringUtils.getTranslatableString("carpet.commands.blockFinder.feedback", count,
                            (int) MathUtils.getBlockDistance(sourceBlockPos, blockPos),
                            StringUtils.getBracketedBlockPos(blockPos)));
        } else {
            SendMessageUtils.sendCommandFeedback(context.getSource(),
                    "carpet.commands.blockFinder.feedback", count,
                    (int) MathUtils.getBlockDistance(sourceBlockPos, blockPos),
                    TextUtils.blockPos(blockPos, Formatting.GREEN));
        }
    }
}
package org.carpetorgaddition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.carpetorgaddition.util.CommandUtils;
import org.carpetorgaddition.util.TextUtils;
import org.carpetorgaddition.util.constant.TextConstants;
import org.carpetorgaddition.util.matcher.ItemMatcher;
import org.carpetorgaddition.util.matcher.ItemStackMatcher;
import org.carpetorgaddition.util.matcher.Matcher;
import org.carpetorgaddition.util.task.ServerTaskManagerInterface;
import org.carpetorgaddition.util.task.findtask.BlockFindTask;
import org.carpetorgaddition.util.task.findtask.ItemFindTask;
import org.carpetorgaddition.util.task.findtask.TradeEnchantedBookFindTask;
import org.carpetorgaddition.util.task.findtask.TradeItemFindTask;
import org.carpetorgaddition.util.wheel.SelectionArea;

public class FinderCommand {
    /**
     * 最大统计数量
     */
    public static final int MAXIMUM_STATISTICAL_COUNT = 30000;
    /**
     * 最大反馈消息数量
     */
    public static final int MAX_FEEDBACK_COUNT = 10;
    /**
     * 每个游戏刻最大查找时间
     */
    public static final long MAX_FIND_TIME = 200;
    /**
     * 任务执行的最大游戏刻数
     */
    public static final int MAX_TICK_COUNT = 50;
    /**
     * 村民的游戏内名称
     */
    public static final MutableText VILLAGER = TextUtils.translate("entity.minecraft.villager");
    /**
     * 查找超时时抛出异常的反馈消息
     */
    public static final String TIME_OUT = "carpet.commands.finder.timeout";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("finder")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandFinder))
                .then(CommandManager.literal("block")
                        .then(CommandManager.argument("blockState", BlockStateArgumentType.blockState(commandBuildContext))
                                .executes(context -> blockFinder(context, 64))
                                .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                        .executes(context -> blockFinder(context, IntegerArgumentType.getInteger(context, "range"))))
                                .then(CommandManager.literal("from")
                                        .then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
                                                .then(CommandManager.literal("to")
                                                        .then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
                                                                .executes(FinderCommand::areaBlockFinder)))))))
                .then(CommandManager.literal("item")
                        .then(CommandManager.argument("itemStack", ItemStackArgumentType.itemStack(commandBuildContext))
                                .executes(context -> findItem(context, 64))
                                .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                        .executes(context -> findItem(context, IntegerArgumentType.getInteger(context, "range"))))
                                .then(CommandManager.literal("from")
                                        .then(CommandManager.argument("from", BlockPosArgumentType.blockPos())
                                                .then(CommandManager.literal("to")
                                                        .then(CommandManager.argument("to", BlockPosArgumentType.blockPos())
                                                                .executes(FinderCommand::areaItemFinder)))))))
                .then(CommandManager.literal("trade")
                        .then(CommandManager.literal("item")
                                .then(CommandManager.argument("itemStack", ItemStackArgumentType.itemStack(commandBuildContext))
                                        .executes(context -> findTradeItem(context, 64))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                                .executes(context -> findTradeItem(context, IntegerArgumentType.getInteger(context, "range"))))))
                        .then(CommandManager.literal("enchanted_book")
                                .then(CommandManager.argument("enchantment", RegistryEntryReferenceArgumentType.registryEntry(commandBuildContext, RegistryKeys.ENCHANTMENT))
                                        .executes(context -> findEnchantedBookTrade(context, 64))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                                .executes(context -> findEnchantedBookTrade(context, IntegerArgumentType.getInteger(context, "range"))))))));
    }

    // 物品查找
    private static int findItem(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要查找的物品堆栈
        ItemStack itemStack = ItemStackArgumentType.getItemStackArgument(context, "itemStack").createStack(1, false);
        // 获取玩家所在的位置，这是命令开始执行的坐标
        BlockPos sourceBlockPos = player.getBlockPos();
        // 查找周围容器中的物品
        Matcher matcher = new ItemStackMatcher(itemStack);
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        World world = player.getWorld();
        taskManager.addTask(new ItemFindTask(world, matcher, new SelectionArea(world, sourceBlockPos, range), context));
        return 1;
    }

    // 区域查找物品
    private static int areaItemFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        BlockPos from = BlockPosArgumentType.getBlockPos(context, "from");
        BlockPos to = BlockPosArgumentType.getBlockPos(context, "to");
        // 获取要查找的物品
        ItemStack itemStack = ItemStackArgumentType.getItemStackArgument(context, "itemStack").createStack(1, false);
        Matcher matcher = new ItemStackMatcher(itemStack);
        // 计算要查找的区域
        SelectionArea selectionArea = new SelectionArea(from, to);
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(player.getServer());
        taskManager.addTask(new ItemFindTask(player.getWorld(), matcher, selectionArea, context));
        return 1;
    }

    // 方块查找
    private static int blockFinder(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要匹配的方块状态
        BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "blockState");
        // 获取命令执行时的方块坐标
        final BlockPos sourceBlockPos = player.getBlockPos();
        ServerTaskManagerInterface tackManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        ServerWorld world = player.getServerWorld();
        SelectionArea selectionArea = new SelectionArea(world, sourceBlockPos, range);
        tackManager.addTask(new BlockFindTask(world, sourceBlockPos, selectionArea, context, blockStateArgument));
        return 1;
    }

    // 区域方块查找
    private static int areaBlockFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        BlockPos from = BlockPosArgumentType.getBlockPos(context, "from");
        BlockPos to = BlockPosArgumentType.getBlockPos(context, "to");
        // 获取要匹配的方块状态
        BlockStateArgument argument = BlockStateArgumentType.getBlockState(context, "blockState");
        // 计算要查找的区域
        SelectionArea selectionArea = new SelectionArea(from, to);
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(player.getServer());
        // 添加查找任务
        taskManager.addTask(new BlockFindTask(player.getServerWorld(), player.getBlockPos(), selectionArea, context, argument));
        return 1;
    }

    // 准备根据物品查找交易项
    private static int findTradeItem(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家对象
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要匹配的物品
        ItemMatcher matcher = new ItemMatcher(ItemStackArgumentType.getItemStackArgument(context, "itemStack").getItem());
        // 获取玩家所在的坐标
        BlockPos sourcePos = player.getBlockPos();
        World world = player.getWorld();
        // 查找范围
        SelectionArea area = new SelectionArea(world, sourcePos, range);
        TradeItemFindTask task = new TradeItemFindTask(world, area, sourcePos, context, matcher);
        // 向任务管理器添加任务
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        taskManager.addTask(task);
        return 1;
    }

    // 准备查找出售指定附魔书的村民
    private static int findEnchantedBookTrade(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取需要查找的附魔
        Enchantment enchantment = RegistryEntryReferenceArgumentType.getEnchantment(context, "enchantment").value();
        // 获取玩家所在的位置
        BlockPos sourcePos = player.getBlockPos();
        World world = player.getWorld();
        // 查找范围
        SelectionArea area = new SelectionArea(world, sourcePos, range);
        TradeEnchantedBookFindTask task = new TradeEnchantedBookFindTask(world, area, sourcePos, context, enchantment);
        // 向任务管理器添加任务
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        taskManager.addTask(task);
        return 1;
    }

    // 将物品数量转换为“多少组多少个”的形式
    public static MutableText showCount(ItemStack itemStack, int count, boolean inTheShulkerBox) {
        MutableText text = TextConstants.itemCount(count, itemStack.getMaxCount());
        // 如果包含在潜影盒内找到的物品，在数量上添加斜体效果
        return inTheShulkerBox ? TextUtils.toItalic(text) : text;
    }
}

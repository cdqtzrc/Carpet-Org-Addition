package org.carpet_org_addition.command;

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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.matcher.ItemPredicateMatcher;
import org.carpet_org_addition.util.matcher.ItemStackMatcher;
import org.carpet_org_addition.util.matcher.Matcher;
import org.carpet_org_addition.util.task.ServerTaskManagerInterface;
import org.carpet_org_addition.util.task.findtask.BlockFindTask;
import org.carpet_org_addition.util.task.findtask.ItemFindTask;
import org.carpet_org_addition.util.task.findtask.TradeFindTask;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.function.Predicate;

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
    public static final MutableText VILLAGER = TextUtils.getTranslate("entity.minecraft.villager");
    /**
     * 查找超时时抛出异常的反馈消息
     */
    public static final String TIME_OUT = "carpet.commands.finder.timeout";

    // TODO 查找256格范围方块抛出异常
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("finder")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandFinder))
                .then(CommandManager.literal("block")
                        .then(CommandManager.argument("blockState", BlockStateArgumentType.blockState(commandBuildContext))
                                .executes(context -> blockFinder(context, 32))
                                .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                        .executes(context -> blockFinder(context, -1)))))
                .then(CommandManager.literal("item")
                        .then(CommandManager.argument("itemStack", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                .executes(context -> findItem(context, 32))
                                .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                        .executes(context -> findItem(context, -1)))))
                .then(CommandManager.literal("trade")
                        .then(CommandManager.literal("item")
                                .then(CommandManager.argument("itemStack", ItemStackArgumentType.itemStack(commandBuildContext))
                                        .executes(context -> findTradeItem(context, 32))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                                .executes(context -> findTradeItem(context, -1)))))
                        .then(CommandManager.literal("enchanted_book")
                                .then(CommandManager.argument("enchantment", RegistryEntryArgumentType.registryEntry(commandBuildContext, RegistryKeys.ENCHANTMENT))
                                        .executes(context -> findEnchantedBookTrade(context, 32))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 256))
                                                .executes(context -> findEnchantedBookTrade(context, -1)))))));
    }

    // 物品查找
    private static int findItem(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要查找的物品堆栈
        Predicate<ItemStack> predicate = ItemPredicateArgumentType.getItemStackPredicate(context, "itemStack");
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        // 获取玩家所在的位置，这是命令开始执行的坐标
        BlockPos sourceBlockPos = player.getBlockPos();
        // 查找周围容器中的物品
        Matcher matcher = new ItemPredicateMatcher(predicate);
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        World world = player.getWorld();
        taskManager.addTask(new ItemFindTask(world, matcher, new SelectionArea(world, sourceBlockPos, range), context));
        return 1;
    }

    // 方块查找
    private static int blockFinder(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要匹配的方块状态
        BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "blockState");
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        // 获取命令执行时的方块坐标
        final BlockPos sourceBlockPos = player.getBlockPos();
        ServerTaskManagerInterface tackManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        ServerWorld world = player.getServerWorld();
        SelectionArea selectionArea = new SelectionArea(world, sourceBlockPos, range);
        tackManager.addTask(new BlockFindTask(world, sourceBlockPos, selectionArea, context, blockStateArgument));
        return 1;
    }

    // TODO 结果分组：位于%s的村民的第1,2,3,4项交易出售该物品
    // TODO 鼠标悬停显示价格
    // 准备根据物品查找交易项
    private static int findTradeItem(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家对象
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        // 获取要匹配的物品
        ItemStackArgument itemStackArgument = ItemStackArgumentType.getItemStackArgument(context, "itemStack");
        // 获取玩家所在的坐标
        BlockPos sourcePos = player.getBlockPos();
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        World world = player.getWorld();
        ItemStackMatcher matcher = new ItemStackMatcher(itemStackArgument.createStack(1, false));
        TradeFindTask.TradePredicate predicate = new TradeFindTask.TradePredicate(matcher);
        taskManager.addTask(new TradeFindTask(world, new SelectionArea(world, sourcePos, range), sourcePos, context, predicate));
        return 1;
    }

    // 准备查找出售指定附魔书的村民
    private static int findEnchantedBookTrade(CommandContext<ServerCommandSource> context, int range) throws CommandSyntaxException {
        // 获取执行命令的玩家
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        // 获取需要查找的附魔
        Enchantment enchantment = RegistryEntryArgumentType.getEnchantment(context, "enchantment").value();
        // 获取玩家所在的位置
        BlockPos sourcePos = player.getBlockPos();
        World world = player.getWorld();
        ServerTaskManagerInterface taskManager = ServerTaskManagerInterface.getInstance(context.getSource().getServer());
        TradeFindTask.TradePredicate predicate = new TradeFindTask.TradePredicate(enchantment);
        SelectionArea selectionArea = new SelectionArea(world, sourcePos, range);
        taskManager.addTask(new TradeFindTask(world, selectionArea, sourcePos, context, predicate));
        return 1;
    }

    // 将物品数量转换为“多少组多少个”的形式
    public static MutableText showCount(ItemStack itemStack, int count, boolean inTheShulkerBox) {
        // 获取物品的最大堆叠数
        int maxCount = itemStack.getMaxCount();
        // 计算物品有多少组
        int group = count / maxCount;
        // 计算物品余几个
        int remainder = count % maxCount;
        String value = String.valueOf(count);
        MutableText text = Text.literal(value);
        if (inTheShulkerBox) {
            // 如果包含在潜影盒内找到的物品，在数量上添加斜体效果
            text = TextUtils.regularStyle(value, Formatting.WHITE, false, true, false, false);
        }
        if (group == 0) {
            return TextUtils.hoverText(text, TextUtils.getTranslate("carpet.commands.finder.item.remainder", remainder), null);
        } else if (remainder == 0) {
            return TextUtils.hoverText(text, TextUtils.getTranslate("carpet.commands.finder.item.group", group), null);
        } else {
            return TextUtils.hoverText(text, TextUtils.getTranslate("carpet.commands.finder.item.count", group, remainder), null);
        }
    }
}

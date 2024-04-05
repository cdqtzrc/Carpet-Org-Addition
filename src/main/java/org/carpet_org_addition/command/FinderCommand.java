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
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.feedback.*;
import org.carpet_org_addition.util.findtask.finder.BlockFinder;
import org.carpet_org_addition.util.findtask.finder.EnchantedBookTradeFinder;
import org.carpet_org_addition.util.findtask.finder.ItemFinder;
import org.carpet_org_addition.util.findtask.finder.TradeItemFinder;
import org.carpet_org_addition.util.findtask.result.BlockFindResult;
import org.carpet_org_addition.util.findtask.result.ItemFindResult;
import org.carpet_org_addition.util.findtask.result.TradeEnchantedBookResult;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;
import org.carpet_org_addition.util.matcher.ItemPredicateMatcher;
import org.carpet_org_addition.util.matcher.Matcher;

import java.util.ArrayList;
import java.util.function.Predicate;

public class FinderCommand {
    /**
     * 最大统计数量
     */
    public static final int MAXIMUM_STATISTICS = 300000;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("finder")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandFinder))
                .then(CommandManager.literal("block")
                        .then(CommandManager.argument("blockState", BlockStateArgumentType.blockState(commandBuildContext))
                                .executes(context -> blockFinder(context, 32, 10))
                                .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 128))
                                        .executes(context -> blockFinder(context, -1, 10))
                                        .then(CommandManager.argument("maxCount", IntegerArgumentType.integer(1))
                                                .executes(context -> blockFinder(context, -1, -1))))))
                .then(CommandManager.literal("item")
                        .then(CommandManager.argument("itemStack", ItemPredicateArgumentType.itemPredicate(commandBuildContext))
                                .executes(context -> findItem(context, 32, 10))
                                .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 128))
                                        .executes(context -> findItem(context, -1, 10))
                                        .then(CommandManager.argument("maxCount", IntegerArgumentType.integer(1))
                                                .executes(context -> findItem(context, -1, -1))))))
                .then(CommandManager.literal("trade")
                        .then(CommandManager.literal("item")
                                .then(CommandManager.argument("itemStack", ItemStackArgumentType.itemStack(commandBuildContext))
                                        .executes(context -> findTradeItem(context, 32, 10))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 128))
                                                .executes(context -> findTradeItem(context, -1, 10))
                                                .then(CommandManager.argument("maxCount", IntegerArgumentType.integer(1))
                                                        .executes(context -> findTradeItem(context, -1, -1))))))
                        .then(CommandManager.literal("enchantedBook")
                                .then(CommandManager.argument("enchantment", RegistryEntryArgumentType.registryEntry(commandBuildContext, RegistryKeys.ENCHANTMENT))
                                        .executes(context -> findEnchantedBookTrade(context, 32, 10))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 128))
                                                .executes(context -> findEnchantedBookTrade(context, -1, 10))
                                                .then(CommandManager.argument("maxCount", IntegerArgumentType.integer(1))
                                                        .executes(context -> findEnchantedBookTrade(context, -1, -1))))))));
    }

    // 物品查找
    private static int findItem(CommandContext<ServerCommandSource> context, int range, int maxCount) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要查找的物品堆栈
        Predicate<ItemStack> predicate = ItemPredicateArgumentType.getItemStackPredicate(context, "itemStack");
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        if (maxCount == -1) {
            // 设置最多显示几条消息
            maxCount = IntegerArgumentType.getInteger(context, "maxCount");
        }
        // 获取玩家所在的位置，这是命令开始执行的坐标
        BlockPos sourceBlockPos = player.getBlockPos();
        // 查找周围容器中的物品
        Matcher matcher = new ItemPredicateMatcher(predicate);
        // 创建一个物品查找器对象
        ItemFinder itemFinder = new ItemFinder(player.getWorld(), sourceBlockPos, range, matcher);
        // 进行物品查找
        ArrayList<ItemFindResult> list = itemFinder.startSearch();
        if (list.isEmpty()) {
            // 在周围的容器中找不到指定物品
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find.not_item",
                    matcher.toText());
            return 0;
        } else if (list.size() > MAXIMUM_STATISTICS) {
            // 容器太多，无法统计
            throw CommandUtils.createException("carpet.commands.finder.item.too_much_container",
                    matcher.toText(), list.size());
        }
        // 在一个单独的线程中处理数据
        new ItemFindFeedback(context, list, matcher, maxCount).start();
        return list.size();
    }

    // 方块查找
    private static int blockFinder(CommandContext<ServerCommandSource> context, int range, int maxCount) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        // 获取要匹配的方块状态
        BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "blockState");
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        if (maxCount == -1) {
            // 设置最多显示几条消息
            maxCount = IntegerArgumentType.getInteger(context, "maxCount");
        }
        // 获取命令执行时的方块坐标
        final BlockPos sourceBlockPos = player.getBlockPos();
        BlockFinder blockFinder = new BlockFinder(player.getWorld(), sourceBlockPos, range, blockStateArgument);
        // 开始查找方块，然后返回查询结果
        ArrayList<BlockFindResult> list = blockFinder.startSearch();
        int count = list.size();
        //判断集合中是否有元素，如果没有，直接在聊天栏发送反馈并结束方法
        if (list.isEmpty()) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.not_found_block",
                    TextUtils.getBlockName(blockStateArgument.getBlockState().getBlock()));
            return 0;
        }
        // 在一个单独的线程中对查找到的方块进行排序和发送反馈
        new BlockFindFeedback(context, list, sourceBlockPos, blockStateArgument.getBlockState().getBlock(), maxCount).start();
        return count;
    }

    // 准备根据物品查找交易项
    private static int findTradeItem(CommandContext<ServerCommandSource> context, int range, int maxCount) throws CommandSyntaxException {
        // 获取执行命令的玩家对象
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        if (maxCount == -1) {
            // 设置最多显示几条消息
            maxCount = IntegerArgumentType.getInteger(context, "maxCount");
        }
        // 获取要匹配的物品
        ItemStackArgument itemStackArgument = ItemStackArgumentType.getItemStackArgument(context, "itemStack");
        // 获取玩家所在的坐标
        BlockPos sourcePos = player.getBlockPos();
        TradeItemFinder tradeItemFinder = new TradeItemFinder(player.getWorld(), sourcePos, range, itemStackArgument);
        // 开始查找物品
        ArrayList<TradeItemFindResult> list = tradeItemFinder.startSearch();
        ItemStack itemStack = itemStackArgument.createStack(1, true);
        // 找不到出售指定物品的村民，直接结束方法
        if (list.isEmpty()) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                    itemStack.toHoverableText(),
                    AbstractTradeFindFeedback.VILLAGER,
                    AbstractTradeFindFeedback.WANDERING_TRADER);
            return 0;
        }
        // 在单独的线程中处理查找结果
        new TradeItemFindFeedback(context, list, sourcePos, itemStack, maxCount).start();
        return list.size();
    }

    // 准备查找出售指定附魔书的村民
    private static int findEnchantedBookTrade(CommandContext<ServerCommandSource> context, int range, int maxCount) throws CommandSyntaxException {
        // 获取执行命令的玩家
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        if (range == -1) {
            // 获取要查找方块的范围
            range = IntegerArgumentType.getInteger(context, "range");
        }
        if (maxCount == -1) {
            // 设置最多显示几条消息
            maxCount = IntegerArgumentType.getInteger(context, "maxCount");
        }
        // 获取需要查找的附魔
        Enchantment enchantment = RegistryEntryArgumentType.getEnchantment(context, "enchantment").value();
        // 获取玩家所在的位置
        BlockPos sourcePos = player.getBlockPos();
        EnchantedBookTradeFinder enchantedBookTradeFinder = new EnchantedBookTradeFinder(player.getWorld(), sourcePos, range, enchantment);
        // 开始查找周围附近出售指定附魔书的村民
        ArrayList<TradeEnchantedBookResult> list = enchantedBookTradeFinder.startSearch();
        // 找不到出售指定物品的村民，直接结束方法
        if (list.isEmpty()) {
            MutableText mutableText = Text.translatable(enchantment.getTranslationKey());
            // 如果是诅咒附魔，设置为红色
            if (enchantment.isCursed()) {
                mutableText.formatted(Formatting.RED);
            } else {
                mutableText.formatted(Formatting.GRAY);
            }
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                    TextUtils.appendAll(mutableText, Items.ENCHANTED_BOOK.getName()),
                    TextUtils.getTranslate("entity.minecraft.villager"),
                    TextUtils.getTranslate("entity.minecraft.wandering_trader"));
            return 0;
        }
        new TradeEnchantedBookFeedback(context, list, sourcePos, enchantment, maxCount).start();
        return list.size();
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

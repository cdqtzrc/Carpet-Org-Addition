package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FinderCommand {
    // TODO 查找附近的村民实体，并找出有指定交易的村民
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("finder").requires(source ->
                        CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandFinder))
                .then(CommandManager.literal("block")
                        .then(CommandManager.argument("blockState", BlockStateArgumentType.blockState(commandBuildContext))
                                .then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 128))
                                        .executes(FinderCommand::blockFinder))))
                .then(CommandManager.literal("item")
                        .then(CommandManager.argument("itemStack", ItemStackArgumentType.itemStack(commandBuildContext))
                                .then(CommandManager.argument("radius", IntegerArgumentType.integer(0, 128))
                                        .executes(FinderCommand::itemFinder))))
        );
    }

    // TODO 物品查找支持掉落物和物品展示框
    // 物品查找
    private static int itemFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        // 获取要查找的物品堆栈
        ItemStackArgument itemStackArgument = ItemStackArgumentType.getItemStackArgument(context, "itemStack");
        // 获取要查找方块的范围
        int radius = IntegerArgumentType.getInteger(context, "radius");
        // 获取玩家所在的位置，这是命令开始执行的坐标
        BlockPos sourceBlockPos = player.getBlockPos();
        // 查找周围容器中的物品
        ArrayList<ItemStackFindResult> list = findItem(player.getServerWorld(), sourceBlockPos, itemStackArgument, radius);
        ItemStack itemStack = itemStackArgument.createStack(1, true);
        if (list.isEmpty()) {
            // 在周围的容器中找不到指定物品
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find.not_item",
                    itemStack.toHoverableText());
            return 0;
        } else if (list.size() > 300000) {
            // 容器太多，无法统计
            throw CommandUtils.getException("carpet.commands.finder.item.too_much_container",
                    itemStack.toHoverableText(), list.size());
        }
        // 在一个单独的线程中处理数据
        ItemFinderDataCollationThread sort = new ItemFinderDataCollationThread(context, itemStack, list);
        // 设置新线程的名字
        sort.setName("Item Finder Thread");
        // 启动线程
        sort.start();
        return list.size();
    }

    //方块查找
    private static int blockFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取执行命令的玩家并非空判断
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        // 获取要匹配的方块状态
        BlockStateArgument blockStateArgument = BlockStateArgumentType.getBlockState(context, "blockState");
        // 获取要查找方块的范围
        int radius = IntegerArgumentType.getInteger(context, "radius");
        // 获取命令执行时的方块坐标
        final BlockPos sourceBlockPos = player.getBlockPos();
        // 开始查找方块，然后返回查询结果
        ArrayList<BlockPos> list = findBlock(player.getServerWorld(), sourceBlockPos, blockStateArgument, radius);
        int count = list.size();
        // 如果找到的方块数量过多，直接抛出异常结束方法，不再进行排序
        if (count > 300000) {
            throw CommandUtils.getException("carpet.commands.finder.block.too_much_blocks",
                    TextUtils.getBlockName(blockStateArgument.getBlockState().getBlock()), count);
        }
        // 在一个单独的线程中对查找到的方块进行排序
        BlockFinderDataCollationThread sort = new BlockFinderDataCollationThread(context, player, blockStateArgument, list);
        // 设置多线程的名称
        sort.setName("Block Finder Thread");
        // 启动新的线程
        sort.start();
        return count;
    }

    /**
     * 获取指定范围内所有指定方块的坐标
     *
     * @param world              要查找方块的世界对象
     * @param blockPos           查找范围的中心坐标
     * @param blockStateArgument 要查找的方块，支持方块状态
     * @param radius             查找的范围，是一个边长为两倍radius，高度为整个世界高度的长方体
     * @return 包含所有查找到的方块坐标和该方块距离源方块坐标的距离的集合，并且已经从近到远排序
     */
    public static ArrayList<BlockPos> findBlock(ServerWorld world, BlockPos blockPos, BlockStateArgument blockStateArgument,
                                                int radius) throws CommandSyntaxException {
        //创建ArrayList集合，用来记录找到的方块坐标
        ArrayList<BlockPos> list = new ArrayList<>();
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
                        throw CommandUtils.getException("carpet.commands.finder.timeout");
                    }
                    //修改可变坐标的值
                    mutableBlockPos.set(minX, bottomY, minZ);
                    if (blockStateArgument.test(world, mutableBlockPos)) {
                        //将找到的方块坐标添加到集合
                        list.add(new BlockPos(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ()));
                    }
                }
            }
        }
        return list;
    }

    private static ArrayList<ItemStackFindResult> findItem(ServerWorld world, BlockPos blockPos, ItemStackArgument itemStackArgument, int radius) throws CommandSyntaxException {
        //创建ArrayList集合，用来记录找到的物品坐标
        ArrayList<ItemStackFindResult> list = new ArrayList<>();
        //获取三个坐标的最大值
        int maxX = blockPos.getX() + radius;
        int maxZ = blockPos.getZ() + radius;
        int maxHeight = world.getHeight();
        // 获取当前系统时间的毫秒值
        long currentTimeMillis = System.currentTimeMillis();
        //遍历整个三维空间，找到与目标物品匹配的物品
        for (int minX = blockPos.getX() - radius; minX <= maxX; minX++) {
            for (int minZ = blockPos.getZ() - radius; minZ <= maxZ; minZ++) {
                for (int bottomY = world.getBottomY(); bottomY <= maxHeight; bottomY++) {
                    // 检查时间是否超时
                    if (System.currentTimeMillis() - currentTimeMillis > 3000) {
                        //3秒内未完成方块查找，通过抛出异常结束方法
                        throw CommandUtils.getException("carpet.commands.finder.timeout");
                    }
                    // 定义变量记录找到物品的数量
                    int count = 0;
                    // 定义变量记录是否有物品是在潜影盒内找到的
                    boolean inTheShulkerBox = false;
                    // 当前的方块坐标
                    BlockPos currentPos = new BlockPos(minX, bottomY, minZ);
                    // 判断当前的方块实体是不是容器
                    if (world.getBlockEntity(currentPos) instanceof Inventory inventory) {
                        for (int index = 0; index < inventory.size(); index++) {
                            // 获取当前准备比较的物品堆栈对象
                            ItemStack itemStack = inventory.getStack(index);
                            // 如果物品栏中的物品与指定物品匹配，找到物品的数量增加
                            if (itemStackArgument.test(itemStack)) {
                                count += itemStack.getCount();
                                // 不再判断本物品是否为潜影盒
                                continue;
                            }
                            // 判断当前物品是否为潜影盒
                            if (ShulkerBoxUtils.isShulkerBoxItem(itemStack)) {
                                // 获取潜影盒内的物品栏
                                Inventory shulkerBoxInventory = ShulkerBoxUtils.getInventory(itemStack);
                                if (shulkerBoxInventory == null) {
                                    // 潜影盒没有NBT，直接结束本轮循环，不进人潜影盒内查找物品
                                    continue;
                                }
                                // 在潜影盒内寻找物品
                                for (int i = 0; i < shulkerBoxInventory.size(); i++) {
                                    ItemStack shulkerBoxItemStack = shulkerBoxInventory.getStack(i);
                                    if (itemStackArgument.test(shulkerBoxItemStack)) {
                                        // 在潜影盒内找到物品的数量增加
                                        inTheShulkerBox = true;
                                        // 找到物品的数量增加
                                        count += shulkerBoxItemStack.getCount();
                                    }
                                }
                            }
                        }
                        // 如果容器中有指定物品，就将查找结果添加进集合
                        if (count > 0) {
                            list.add(new ItemStackFindResult(currentPos, count, inTheShulkerBox,
                                    world.getBlockState(currentPos).getBlock().getTranslationKey(),
                                    itemStackArgument.createStack(1, true)));
                        }
                    }
                }
            }
        }
        return list;
    }

    public static int tradeFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        int range = IntegerArgumentType.getInteger(context, "range");
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        BlockPos sourcePos = player.getBlockPos();
        World world = player.getWorld();
        ArrayList<TradeFindResult> findResults = findTrade(sourcePos, range, world, item);
        return findResults.size();
    }

    @NotNull
    private static ArrayList<TradeFindResult> findTrade(BlockPos sourcePos, int range, World world, Item item) {
        Box box = new Box(sourcePos.getX() - range, world.getBottomY(), sourcePos.getZ() - range,
                sourcePos.getX() + range, world.getTopY(), sourcePos.getZ() + range);
        ArrayList<TradeFindResult> findResults = new ArrayList<>();
        List<MerchantEntity> list = world.getNonSpectatingEntities(MerchantEntity.class, box);
        for (MerchantEntity merchant : list) {
            TradeOfferList offerList = merchant.getOffers();
            for (int index = 0; index < offerList.size(); index++) {
                if (offerList.get(index).getSellItem().isOf(item)) {
                    findResults.add(new TradeFindResult(merchant, offerList.get(index), index + 1));
                }
            }
        }
        return findResults;
    }


    //发送命令反馈
    public static void sendFeedback(CommandContext<ServerCommandSource> context, Block block, ArrayList<BlockPos> list,
                                    BlockPos sourceBlockPos) {
        int size = list.size();
        //判断集合中是否有元素
        if (size == 0) {
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.not_found_block",
                    TextUtils.getBlockName(block));
        } else {
            //在聊天栏输出方块坐标消息
            int count = 0;
            if (size > 10) {
                SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.find", size,
                        TextUtils.getBlockName(block));
                for (BlockPos blockPos : list) {
                    count++;
                    if (count > 10) {
                        break;
                    }
                    sendFeedback(context, sourceBlockPos, count, blockPos);
                }
            } else {
                SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.find.not_more_than_ten",
                        size, TextUtils.getBlockName(block));
                for (BlockPos blockPos : list) {
                    count++;
                    sendFeedback(context, sourceBlockPos, count, blockPos);
                }
            }
        }
    }

    //发送反馈
    private static void sendFeedback(CommandContext<ServerCommandSource> context, BlockPos sourceBlockPos, int count, BlockPos blockPos) {
        SendMessageUtils.sendCommandFeedback(context.getSource(),
                "carpet.commands.finder.block.feedback", count,
                (int) MathUtils.getBlockDistance(sourceBlockPos, blockPos),
                //如果规则可解析路径点启用，发送不带有特殊样式的文本
                CarpetOrgAdditionSettings.canParseWayPoint
                        ? StringUtils.getBracketedBlockPos(blockPos)
                        : TextUtils.blockPos(blockPos, Formatting.GREEN));
    }

    static class BlockFinderDataCollationThread extends Thread {
        private final CommandContext<ServerCommandSource> context;
        private final BlockStateArgument blockStateArgument;
        private final BlockPos sourceBlockPos;
        private final ArrayList<BlockPos> list;

        BlockFinderDataCollationThread(CommandContext<ServerCommandSource> context, ServerPlayerEntity player,
                                       BlockStateArgument blockStateArgument, ArrayList<BlockPos> list) {
            this.context = context;
            this.blockStateArgument = blockStateArgument;
            this.sourceBlockPos = player.getBlockPos();
            this.list = list;
        }

        @Override
        public void run() {
            // 将集合中的元素排序
            list.sort((o1, o2) -> MathUtils.compareBlockPos(sourceBlockPos, o1, o2));
            // 发送命令反馈
            sendFeedback(context, blockStateArgument.getBlockState().getBlock(), list, sourceBlockPos);
        }
    }

    static class ItemFinderDataCollationThread extends Thread {
        private final CommandContext<ServerCommandSource> context;
        private final ItemStack itemStack;
        private final ArrayList<ItemStackFindResult> list;

        ItemFinderDataCollationThread(CommandContext<ServerCommandSource> context, ItemStack itemStack, ArrayList<ItemStackFindResult> list) {
            this.context = context;
            this.itemStack = itemStack;
            this.list = list;
        }

        @Override
        public void run() {
            boolean inTheShulkerBox = false;
            // 计算总共找到的物品数量
            int count = 0;
            for (ItemStackFindResult result : list) {
                count += result.count;
                if (result.inTheShulkerBox) {
                    inTheShulkerBox = true;
                }
            }
            // 为数量添加鼠标悬停效果
            MutableText text = showCount(itemStack, count, inTheShulkerBox);
            // 发送命令反馈
            if (list.size() <= 10) {
                // 数量较少，不排序
                SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find",
                        list.size(), text, itemStack.toHoverableText());
                for (ItemStackFindResult result : list) {
                    SendMessageUtils.sendTextMessage(context.getSource(), result.toText());
                }
            } else {
                SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find.not_more_than_ten",
                        list.size(), text, itemStack.toHoverableText());
                // 按照容器内指定物品的数量从多到少排序
                list.sort((o1, o2) -> o2.count - o1.count);
                // 容器数量过多，只反馈前十个
                for (int i = 0; i < 10; i++) {
                    SendMessageUtils.sendTextMessage(context.getSource(), list.get(i).toText());
                }
            }
        }
    }

    /**
     * @param blockPos        物品所在容器的坐标
     * @param count           物品的数量
     * @param inTheShulkerBox 包含在潜影盒内找到的物品
     * @param blockName       物品所在容器方块名称的翻译键
     */
    private record ItemStackFindResult(BlockPos blockPos, int count, boolean inTheShulkerBox,
                                       String blockName, ItemStack itemStack) {
        private MutableText toText() {
            String command = "/particleLine ~ ~1 ~ " + ((double) blockPos.getX() + 0.5) + " "
                    + ((double) blockPos.getY() + 0.5) + " " + ((double) blockPos.getZ() + 0.5);
            return TextUtils.getTranslate("carpet.commands.finder.item.each", TextUtils.blockPos(blockPos, Formatting.GREEN),
                    TextUtils.command(TextUtils.getTranslate(blockName), command, null, null, true),
                    showCount(itemStack, count, inTheShulkerBox));
        }
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

    private record TradeFindResult(MerchantEntity merchant, TradeOffer tradeOffer, int tradeIndex) {
    }
}

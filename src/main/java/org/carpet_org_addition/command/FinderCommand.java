package org.carpet_org_addition.command;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.SendMessageUtils;
import org.carpet_org_addition.util.ShulkerBoxUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.feedback.BlockFindFeedback;
import org.carpet_org_addition.util.findtask.feedback.ItemFindFeedback;
import org.carpet_org_addition.util.findtask.feedback.TradeEnchantedBookFeedback;
import org.carpet_org_addition.util.findtask.feedback.TradeItemFindFeedback;
import org.carpet_org_addition.util.findtask.result.BlockFindResult;
import org.carpet_org_addition.util.findtask.result.ItemFindResult;
import org.carpet_org_addition.util.findtask.result.TradeEnchantedBookResult;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FinderCommand {
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
                .then(CommandManager.literal("trade")
                        .then(CommandManager.literal("item")
                                .then(CommandManager.argument("itemStack", ItemStackArgumentType.itemStack(commandBuildContext))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 128))
                                                .executes(FinderCommand::tradeItemFinder))))
                        .then(CommandManager.literal("enchantedBook")
                                .then(CommandManager.argument("enchantment", RegistryEntryArgumentType.registryEntry(commandBuildContext, RegistryKeys.ENCHANTMENT))
                                        .then(CommandManager.argument("range", IntegerArgumentType.integer(0, 128))
                                                .executes(FinderCommand::enchantedBookTradeFinder)
                                                .then(CommandManager.argument("allLevels", BoolArgumentType.bool())
                                                        .executes(FinderCommand::enchantedBookTradeFinder))))))
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
        ArrayList<ItemFindResult> list = findItem(player.getServerWorld(), sourceBlockPos, itemStackArgument, radius);
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
        new ItemFindFeedback(context, list, itemStack).start();
        return list.size();
    }

    // 开始查找物品
    private static ArrayList<ItemFindResult> findItem(ServerWorld world, BlockPos blockPos, ItemStackArgument itemStackArgument,
                                                      int radius) throws CommandSyntaxException {
        //创建ArrayList集合，用来记录找到的物品坐标
        ArrayList<ItemFindResult> list = new ArrayList<>();
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
                    checkTimeOut(currentTimeMillis);
                    // 定义变量记录找到物品的数量
                    int count = 0;
                    // 定义变量记录是否有物品是在潜影盒内找到的
                    boolean inTheShulkerBox = false;
                    // 当前正在查找物品的所在容器的方块坐标
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
                            list.add(new ItemFindResult(currentPos, count, inTheShulkerBox,
                                    world.getBlockState(currentPos).getBlock().getTranslationKey(),
                                    itemStackArgument.createStack(1, true)));
                        }
                    }
                }
            }
        }
        return list;
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
        ArrayList<BlockFindResult> list = findBlock(player.getServerWorld(), sourceBlockPos, blockStateArgument, radius);
        int count = list.size();
        // 如果找到的方块数量过多，直接抛出异常结束方法，不再进行排序
        if (count > 300000) {
            throw CommandUtils.getException("carpet.commands.finder.block.too_much_blocks",
                    TextUtils.getBlockName(blockStateArgument.getBlockState().getBlock()), count);
        }
        //判断集合中是否有元素，如果没有，直接在聊天栏发送反馈并结束方法
        if (list.isEmpty()) {
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.not_found_block",
                    TextUtils.getBlockName(blockStateArgument.getBlockState().getBlock()));
            return 0;
        }
        // 在一个单独的线程中对查找到的方块进行排序和发送反馈
        new BlockFindFeedback(context, list, sourceBlockPos, blockStateArgument.getBlockState().getBlock()).start();
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
    private static ArrayList<BlockFindResult> findBlock(ServerWorld world, BlockPos blockPos, BlockStateArgument blockStateArgument,
                                                        int radius) throws CommandSyntaxException {
        //创建ArrayList集合，用来记录找到的方块坐标
        ArrayList<BlockFindResult> list = new ArrayList<>();
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
                    checkTimeOut(currentTimeMillis);
                    //修改可变坐标的值
                    mutableBlockPos.set(minX, bottomY, minZ);
                    if (blockStateArgument.test(world, mutableBlockPos)) {
                        //将找到的方块坐标添加到集合
                        list.add(new BlockFindResult(new BlockPos(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ()), blockPos));
                    }
                }
            }
        }
        return list;
    }

    // 检查查找是否超时
    private static void checkTimeOut(long currentTimeMillis) throws CommandSyntaxException {
        if (System.currentTimeMillis() - currentTimeMillis > 3000) {
            //3秒内未完成方块查找，通过抛出异常结束方法
            throw CommandUtils.getException("carpet.commands.finder.timeout");
        }
    }


    // 准备根据物品查找交易项
    private static int tradeItemFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取执行命令的玩家对象
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        // 获取查找的范围
        int range = IntegerArgumentType.getInteger(context, "range");
        // 获取要匹配的物品
        ItemStackArgument itemStackArgument = ItemStackArgumentType.getItemStackArgument(context, "itemStack");
        // 获取玩家所在的坐标
        BlockPos sourcePos = player.getBlockPos();
        // 开始查找物品
        ArrayList<TradeItemFindResult> list = findTradeItem(sourcePos, range, player.getWorld(), itemStackArgument);
        ItemStack itemStack = itemStackArgument.createStack(1, true);
        // 找不到出售指定物品的村民，直接结束方法
        if (list.isEmpty()) {
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                    itemStack.toHoverableText(),
                    TextUtils.getTranslate("entity.minecraft.villager"),
                    TextUtils.getTranslate("entity.minecraft.wandering_trader"));
            return 0;
        }
        // 在单独的线程中处理查找结果
        new TradeItemFindFeedback(context, list, sourcePos, itemStack).start();
        return list.size();
    }

    // 查找出售指定物品的交易选项
    @NotNull
    private static ArrayList<TradeItemFindResult> findTradeItem(BlockPos sourcePos, int range, World world, ItemStackArgument itemStackArgument) {
        // 创建一个盒子对象，以玩家所在的位置为中心，宽度为指定范围的两倍，高度为整个区块高度
        Box box = new Box(sourcePos.getX() - range, world.getBottomY(), sourcePos.getZ() - range,
                sourcePos.getX() + range, world.getTopY(), sourcePos.getZ() + range);
        // 创建一个集合存储查找到的交易的接货
        ArrayList<TradeItemFindResult> findResults = new ArrayList<>();
        // 根据之前的盒子对象获取所有在这个区域内商人实体对象（村民和流浪商人）
        List<MerchantEntity> list = world.getNonSpectatingEntities(MerchantEntity.class, box);
        for (MerchantEntity merchant : list) {
            // 获取集合中的每一个实体，并获取每一个实体的交易选项
            TradeOfferList offerList = merchant.getOffers();
            for (int index = 0; index < offerList.size(); index++) {
                if (itemStackArgument.test(offerList.get(index).getSellItem())) {
                    // 如果交易的输出物品与指定的物品匹配，则将该选项添加到集合
                    findResults.add(new TradeItemFindResult(merchant, offerList.get(index), index + 1));
                }
            }
        }
        return findResults;
    }

    // 准备查找出售指定附魔书的村民
    private static int enchantedBookTradeFinder(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // 获取执行命令的玩家
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        // 获取要查找的范围
        int range = IntegerArgumentType.getInteger(context, "range");
        // 获取是否需要包含非满级附魔书
        boolean allLevels;
        try {
            allLevels = BoolArgumentType.getBool(context, "allLevels");
        } catch (IllegalArgumentException e) {
            // 如果未指定，默认为false，即只统计满级附魔书
            allLevels = false;
        }
        // 获取需要查找的附魔
        Enchantment enchantment = RegistryEntryArgumentType.getEnchantment(context, "enchantment").value();
        // 获取玩家所在的位置
        BlockPos sourcePos = player.getBlockPos();
        // 开始查找周围附近出售指定附魔书的村民
        ArrayList<TradeEnchantedBookResult> list = tradeFinderEnchantedBook(sourcePos, range, player.getWorld(), enchantment, allLevels);
        // 找不到出售指定物品的村民，直接结束方法
        if (list.isEmpty()) {
            MutableText mutableText = Text.translatable(enchantment.getTranslationKey());
            // 如果是诅咒附魔，设置为红色
            if (enchantment.isCursed()) {
                mutableText.formatted(Formatting.RED);
            } else {
                mutableText.formatted(Formatting.GRAY);
            }
            SendMessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                    TextUtils.appendAll(mutableText, Items.ENCHANTED_BOOK.getName()),
                    TextUtils.getTranslate("entity.minecraft.villager"),
                    TextUtils.getTranslate("entity.minecraft.wandering_trader"));
            return 0;
        }
        new TradeEnchantedBookFeedback(context, list, sourcePos, enchantment).start();
        return list.size();
    }

    // 开始查找周围附近出售指定附魔书的村民
    private static ArrayList<TradeEnchantedBookResult> tradeFinderEnchantedBook(BlockPos sourcePos, int range, World world, Enchantment enchantment, boolean allLevels) {
        // 创建一个盒子对象，以玩家所在的位置为中心，宽度为指定范围的两倍，高度为整个区块高度
        Box box = new Box(sourcePos.getX() - range, world.getBottomY(), sourcePos.getZ() - range,
                sourcePos.getX() + range, world.getTopY(), sourcePos.getZ() + range);
        // 创建一个集合存储查找到的交易的接货
        ArrayList<TradeEnchantedBookResult> list = new ArrayList<>();
        // 根据之前的盒子对象获取所有在这个区域内商人实体对象（村民和流浪商人）
        List<MerchantEntity> entities = world.getNonSpectatingEntities(MerchantEntity.class, box);
        for (MerchantEntity merchant : entities) {
            // 获取每一个商人实体出售的物品
            TradeOfferList offers = merchant.getOffers();
            for (int i = 0; i < offers.size(); i++) {
                // 判断出售的物品是不是附魔书
                ItemStack itemStack = offers.get(i).getSellItem();
                if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
                    int level = 0;
                    // 获取附魔的注册id
                    Identifier registryId = EnchantmentHelper.getEnchantmentId(enchantment);
                    // 获取附魔书所有的附魔
                    NbtList nbtList = EnchantedBookItem.getEnchantmentNbt(itemStack);
                    for (int j = 0; j < nbtList.size(); j++) {
                        // 获取每一个附魔的复合NBT标签
                        NbtCompound nbtCompound = nbtList.getCompound(j);
                        // 获取这本附魔书上附魔的id
                        Identifier bookEnchantmentId = EnchantmentHelper.getIdFromNbt(nbtCompound);
                        // 如果附魔书上附魔的id与指定id相同，跳出循环，获取等级
                        if (bookEnchantmentId == null || !bookEnchantmentId.equals(registryId)) {
                            continue;
                        }
                        // 获取附魔等级
                        level = EnchantmentHelper.getLevelFromNbt(nbtCompound);
                        break;
                    }
                    // 将符合条件的附魔书添加到集合
                    if (level > 0 && (allLevels || level == enchantment.getMaxLevel())) {
                        list.add(new TradeEnchantedBookResult(merchant, offers.get(i), (i + 1)));
                    }
                }
            }
        }
        return list;
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

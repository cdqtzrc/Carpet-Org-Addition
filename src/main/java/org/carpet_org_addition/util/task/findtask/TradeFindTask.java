package org.carpet_org_addition.util.task.findtask;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.exception.TaskExecutionException;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.matcher.ItemStackMatcher;
import org.carpet_org_addition.util.task.ServerTask;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TradeFindTask extends ServerTask {
    private final World world;
    private final SelectionArea selectionArea;
    private final BlockPos sourcePos;
    private final CommandContext<ServerCommandSource> context;
    private final TradePredicate predicate;
    private Iterator<MerchantEntity> iterator;
    private FindState findState;
    private int villagerCount;
    /**
     * tick方法开始执行时的时间
     */
    private long startTime;
    /**
     * 任务被执行的总游戏刻数
     */
    private int tickCount;
    private final ArrayList<Result> results = new ArrayList<>();

    public TradeFindTask(World world, SelectionArea selectionArea, BlockPos sourcePos,
                         CommandContext<ServerCommandSource> context, TradePredicate predicate) {
        this.world = world;
        this.selectionArea = selectionArea;
        this.sourcePos = sourcePos;
        this.context = context;
        this.predicate = predicate;
        this.findState = FindState.SEARCH;
    }

    @Override
    public void tick() {
        this.startTime = System.currentTimeMillis();
        this.tickCount++;
        if (tickCount > FinderCommand.MAX_TICK_COUNT) {
            // 任务超时
            MessageUtils.sendCommandErrorFeedback(context, FinderCommand.TIME_OUT);
            this.findState = FindState.END;
            return;
        }
        while (true) {
            if (timeout()) {
                return;
            }
            try {
                switch (this.findState) {
                    case SEARCH -> searchVillager();
                    case SORT -> sort();
                    case FEEDBACK -> feedback();
                    default -> {
                        return;
                    }
                }
            } catch (TaskExecutionException e) {
                e.disposal();
                this.findState = FindState.END;
                return;
            }
        }
    }

    // 查找周围的村民
    private void searchVillager() {
        if (this.iterator == null) {
            this.iterator = this.world.getNonSpectatingEntities(MerchantEntity.class, this.selectionArea.toBox()).iterator();
        }
        while (this.iterator.hasNext()) {
            if (timeout()) {
                return;
            }
            MerchantEntity merchant = this.iterator.next();
            // 获取集合中的每一个实体，并获取每一个实体的交易选项
            TradeOfferList offerList = merchant.getOffers();
            // 当前村民是否销售指定物品
            boolean onSale = false;
            for (int index = 0; index < offerList.size(); index++) {
                if (this.predicate.test(offerList.get(index).getSellItem())) {
                    // 村民所在坐标
                    BlockPos blockPos = merchant.getBlockPos();
                    // 村民或流浪商人的名称
                    MutableText villagerName = TextUtils.command(merchant.getName().copy(),
                            "/particleLine ~ ~1 ~ " + merchant.getUuid(), null, null, true);
                    // 如果交易的输出物品与指定的物品匹配，则将该选项添加到集合
                    Result result = switch (this.predicate.taskType) {
                        case ITEM -> new ItemFindResult(this.sourcePos, blockPos, villagerName, index + 1);
                        case ENCHANTED_BOOK ->
                                new EnchantedBookResult(this.sourcePos, blockPos, villagerName, index + 1,
                                        this.predicate.getLevel(), this.predicate.getEnchantmentName());
                    };
                    this.results.add(result);
                    onSale = true;
                }
            }
            if (onSale) {
                // 增加销售指定物品村民的数量
                this.villagerCount++;
            }
        }
        this.findState = FindState.SORT;
    }

    // 对结果进行排序
    private void sort() {
        if (this.results.isEmpty()) {
            switch (this.predicate.taskType) {
                case ITEM ->
                        MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                                this.predicate.getTradeName(), FinderCommand.VILLAGER);
                case ENCHANTED_BOOK -> {
                    // 重置supplier的内容
                    this.predicate.test(ItemStack.EMPTY);
                    MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                            TextUtils.appendAll(this.predicate.getTradeName(), Items.ENCHANTED_BOOK.getName()),
                            FinderCommand.VILLAGER);
                }
            }
            this.findState = FindState.END;
            return;
        }
        this.results.sort((o1, o2) -> o1.compare(o1, o2));
        this.findState = FindState.FEEDBACK;
    }

    private void feedback() {
        // limit表示是否结果过多只显示前10条
        boolean limit = this.results.size() > FinderCommand.MAX_FEEDBACK_COUNT;
        ArrayList<Object> list = new ArrayList<>();
        list.add(this.villagerCount);
        list.add(this.predicate.getTradeName());
        list.add(FinderCommand.VILLAGER);
        list.add(this.results.size());
        // 消息的翻译键
        String key;
        if (limit) {
            key = switch (this.predicate.taskType) {
                case ITEM -> "carpet.commands.finder.trade.result.limit";
                case ENCHANTED_BOOK -> "carpet.commands.finder.trade.enchanted_book.result.limit";
            };
            // 需要限制数量的消息中多一个占位符
            list.add(FinderCommand.MAX_FEEDBACK_COUNT);
        } else {
            key = "carpet.commands.finder.trade.result";
        }
        // 发送消息：在周围找到了<交易选项数量>个出售<出售的物品名称>的<村民>或<流浪商人>
        MessageUtils.sendCommandFeedback(context.getSource(), key, list.toArray(Object[]::new));
        // 发送每一条（或前10条）结果
        for (int i = 0; i < this.results.size() && i < FinderCommand.MAX_FEEDBACK_COUNT; i++) {
            MessageUtils.sendTextMessage(this.context.getSource(), this.results.get(i).toText());
        }
        this.findState = FindState.END;
    }

    @Override
    public boolean stopped() {
        return this.findState == FindState.END;
    }

    // 当前任务是否超时
    private boolean timeout() {
        return (System.currentTimeMillis() - this.startTime) > FinderCommand.MAX_FIND_TIME;
    }

    private interface Result extends Comparator<Result> {
        MutableText toText();
    }

    private record ItemFindResult(BlockPos sourcePos, BlockPos villagerPos,
                                  MutableText villagerName, int tradeIndex) implements Result {
        @Override
        public MutableText toText() {
            return TextUtils.getTranslate("carpet.commands.finder.trade.item.each",
                    TextUtils.blockPos(villagerPos, Formatting.GREEN), villagerName, tradeIndex);
        }

        @Override
        public int compare(Result o1, Result o2) {
            return MathUtils.compareBlockPos(sourcePos, ((ItemFindResult) o1).villagerPos(), ((ItemFindResult) o2).villagerPos());
        }
    }

    private record EnchantedBookResult(BlockPos sourcePos, BlockPos villagerPos, MutableText villagerName,
                                       int tradeIndex, int level, MutableText enchantmentName) implements Result {
        @Override
        public MutableText toText() {
            return TextUtils.getTranslate("carpet.commands.finder.trade.enchanted_book.each",
                    TextUtils.blockPos(villagerPos, Formatting.GREEN), villagerName, tradeIndex, enchantmentName);
        }

        @Override
        public int compare(Result o1, Result o2) {
            EnchantedBookResult result1 = (EnchantedBookResult) o1;
            EnchantedBookResult result2 = (EnchantedBookResult) o2;
            int compare = Integer.compare(result1.level(), result2.level());
            if (compare == 0) {
                return MathUtils.compareBlockPos(sourcePos, result1.villagerPos, result2.villagerPos);
            }
            return -compare;
        }
    }

    public static class TradePredicate implements Predicate<ItemStack> {
        private final TaskType taskType;
        private final Predicate<ItemStack> predicate;
        private final MutableText tradeName;
        // 获取附魔的对组
        // 请注意，每次调用test()方法都会改变supplier的值，确保在下一次调用test()方法之前接收supplier的值
        private Supplier<Pair<Enchantment, Integer>> supplier;

        public TradePredicate(ItemStackMatcher itemStackMatcher) {
            this.taskType = TaskType.ITEM;
            this.tradeName = itemStackMatcher.getName().copy();
            this.predicate = itemStackMatcher;
            this.supplier = () -> {
                throw new UnsupportedOperationException();
            };
        }

        public TradePredicate(Enchantment enchantment) {
            this.taskType = TaskType.ENCHANTED_BOOK;
            // TODO 注释有误导性
            // 当前物品不是附魔书或当前附魔书没有指定附魔时，不应该获取它的名称
            MutableText text = TextUtils.getTranslate(enchantment.getTranslationKey());
            // 设置附魔名称的颜色
            TextUtils.setColor(text, enchantment.isCursed() ? Formatting.RED : Formatting.GRAY);
            this.tradeName = TextUtils.appendAll(text, Items.ENCHANTED_BOOK.getName());
            this.predicate = enchantedBook -> {
                if (enchantedBook.isOf(Items.ENCHANTED_BOOK)) {
                    // 获取附魔的注册id
                    Identifier registryId = EnchantmentHelper.getEnchantmentId(enchantment);
                    // 获取附魔书所有的附魔
                    NbtList nbtList = EnchantedBookItem.getEnchantmentNbt(enchantedBook);
                    for (int i = 0; i < nbtList.size(); i++) {
                        // 获取每一个附魔的复合NBT标签
                        NbtCompound nbt = nbtList.getCompound(i);
                        // 获取这本附魔书上附魔的id
                        Identifier enchantmentId = EnchantmentHelper.getIdFromNbt(nbt);
                        if (enchantmentId != null && enchantmentId.equals(registryId)) {
                            // 如果附魔书上附魔的id与指定id相同，获取等级
                            final int level = EnchantmentHelper.getLevelFromNbt(nbt);
                            if (level > 0) {
                                this.supplier = () -> new Pair<>(enchantment, level);
                                return true;
                            }
                        }
                    }
                }
                // 当前物品不是附魔书或者附魔书上没有指定附魔
                this.supplier = () -> {
                    throw new IllegalStateException();
                };
                return false;
            };
        }

        private MutableText getTradeName() {
            return this.tradeName;
        }

        private int getLevel() {
            return this.supplier.get().getRight();
        }

        private MutableText getEnchantmentName() {
            Pair<Enchantment, Integer> pair = this.supplier.get();
            return pair.getLeft().getName(pair.getRight()).copy();
        }


        @Override
        public boolean test(ItemStack itemStack) {
            return this.predicate.test(itemStack);
        }
    }

    private enum FindState {
        SEARCH, SORT, FEEDBACK, END
    }

    private enum TaskType {
        ITEM, ENCHANTED_BOOK
    }

    @Override
    public String toString() {
        return "交易查找";
    }
}

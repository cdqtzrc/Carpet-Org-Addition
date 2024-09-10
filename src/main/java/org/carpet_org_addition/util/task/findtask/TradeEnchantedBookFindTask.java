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
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradeEnchantedBookFindTask extends AbstractTradeFindTask {
    private final MutableText treadName;
    private final Enchantment enchantment;

    public TradeEnchantedBookFindTask(World world, SelectionArea selectionArea, BlockPos sourcePos, CommandContext<ServerCommandSource> context, Enchantment enchantment) {
        super(world, selectionArea, sourcePos, context);
        // 获取附魔名称，不带等级
        MutableText text = TextUtils.getTranslate(enchantment.getTranslationKey());
        // 设置附魔名称的颜色
        TextUtils.setColor(text, enchantment.isCursed() ? Formatting.RED : Formatting.GRAY);
        this.treadName = TextUtils.appendAll(text, Items.ENCHANTED_BOOK.getName());
        this.enchantment = enchantment;
    }

    @Override
    protected void searchVillager(MerchantEntity merchant) {
        TradeOfferList offers = merchant.getOffers();
        // 键：附魔书的等级，值：同一只村民出售的相同附魔书的索引集合
        HashMap<Integer, ArrayList<Integer>> hashMap = new HashMap<>();
        for (int index = 0; index < offers.size(); index++) {
            ItemStack enchantedBook = offers.get(index).getSellItem();
            int level = getBookEnchantment(enchantedBook);
            if (level == -1) {
                continue;
            }
            // 将同一只村民出售的相同附魔相同等级的附魔书分到一组
            ArrayList<Integer> result = hashMap.get(level);
            if (result == null) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(index + 1);
                hashMap.put(level, list);
            } else {
                // 同一只村民出售了多本相同附魔书，将结果组装起来
                result.add(index + 1);
            }
        }
        if (hashMap.isEmpty()) {
            return;
        }
        // 添加结果
        for (Map.Entry<Integer, ArrayList<Integer>> entry : hashMap.entrySet()) {
            this.results.add(new EnchantedBookFindResult(merchant, entry.getValue(), entry.getKey()));
        }
    }

    private int getBookEnchantment(ItemStack enchantedBook) {
        if (enchantedBook.isOf(Items.ENCHANTED_BOOK)) {
            // 获取附魔的注册id
            Identifier registryId = EnchantmentHelper.getEnchantmentId(this.enchantment);
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
                        return level;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    protected void notFound() {
        MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.trade.find.not_trade",
                TextUtils.appendAll(this.getTradeName(), Items.ENCHANTED_BOOK.getName()), FinderCommand.VILLAGER);
    }

    @Override
    protected MutableText getTradeName() {
        return this.treadName;
    }

    private class EnchantedBookFindResult implements Result {
        private final MerchantEntity merchant;
        private final ArrayList<Integer> list;
        private final int level;

        private EnchantedBookFindResult(MerchantEntity merchant, ArrayList<Integer> list, int level) {
            this.merchant = merchant;
            this.list = list;
            this.level = level;
        }

        @Override
        public MutableText toText() {
            // 村民或流浪商人的名称
            MutableText villagerName = TextUtils.command(merchant.getName().copy(),
                    "/particleLine ~ ~1 ~ " + merchant.getUuid(), null, null, true);
            // 获取交易名称
            MutableText enchantmentName = TradeEnchantedBookFindTask.this.enchantment.getName(level).copy();
            return TextUtils.getTranslate("carpet.commands.finder.trade.enchanted_book.each",
                    TextUtils.blockPos(this.villagerPos(), Formatting.GREEN), villagerName, getIndexArray(this.list), enchantmentName);
        }

        @Override
        public BlockPos villagerPos() {
            return this.merchant.getBlockPos();
        }

        @Override
        public int compare(Result o1, Result o2) {
            EnchantedBookFindResult result1 = (EnchantedBookFindResult) o1;
            EnchantedBookFindResult result2 = (EnchantedBookFindResult) o2;
            int compare = Integer.compare(result1.level, result2.level);
            if (compare == 0) {
                return MathUtils.compareBlockPos(sourcePos, result1.villagerPos(), result2.villagerPos());
            }
            return -compare;
        }
    }

    @Override
    protected String getResultLimitKey() {
        return "carpet.commands.finder.trade.enchanted_book.result.limit";
    }
}

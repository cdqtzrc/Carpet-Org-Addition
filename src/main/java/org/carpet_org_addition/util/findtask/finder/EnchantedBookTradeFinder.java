package org.carpet_org_addition.util.findtask.finder;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.util.EnchantmentUtils;
import org.carpet_org_addition.util.findtask.result.TradeEnchantedBookResult;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.List;

public class EnchantedBookTradeFinder extends AbstractFinder {
    private final Enchantment enchantment;
    private final ArrayList<TradeEnchantedBookResult> list = new ArrayList<>();

    public EnchantedBookTradeFinder(World world, BlockPos sourcePos, int range, Enchantment enchantment) {
        super(world, sourcePos, range);
        this.enchantment = enchantment;
    }

    @Override
    public ArrayList<TradeEnchantedBookResult> startSearch() {
        Box box = new SelectionArea(this.world, this.sourcePos, this.range).toBox();
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
                    // 获取附魔书所有的附魔
                    if (EnchantmentUtils.getLevel(this.world, enchantment, itemStack) > 0) {
                        // 将符合条件的附魔书添加到集合
                        list.add(new TradeEnchantedBookResult(merchant, offers.get(i), (i + 1), enchantment, level));
                    }
                }
            }
        }
        return this.list;
    }
}
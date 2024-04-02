package org.carpet_org_addition.util.findtask.finder;

import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.carpet_org_addition.util.findtask.result.TradeItemFindResult;
import org.carpet_org_addition.util.helpers.SelectionArea;

import java.util.ArrayList;
import java.util.List;

public class TradeItemFinder extends AbstractFinder {
    private final ItemStackArgument argument;
    private final ArrayList<TradeItemFindResult> list = new ArrayList<>();

    public TradeItemFinder(World world, BlockPos sourcePos, int range, ItemStackArgument itemStackArgument) {
        super(world, sourcePos, range);
        this.argument = itemStackArgument;
    }

    @Override
    public ArrayList<TradeItemFindResult> startSearch() {
        SelectionArea selectionArea = new SelectionArea(this.world, this.sourcePos, this.range);
        Box box = selectionArea.toBox();
        // 根据之前的盒子对象获取所有在这个区域内商人实体对象（村民和流浪商人）
        List<MerchantEntity> entities = world.getNonSpectatingEntities(MerchantEntity.class, box);
        for (MerchantEntity merchant : entities) {
            // 获取集合中的每一个实体，并获取每一个实体的交易选项
            TradeOfferList offerList = merchant.getOffers();
            for (int index = 0; index < offerList.size(); index++) {
                if (this.argument.test(offerList.get(index).getSellItem())) {
                    // 如果交易的输出物品与指定的物品匹配，则将该选项添加到集合
                    this.list.add(new TradeItemFindResult(merchant, offerList.get(index), index + 1));
                }
            }
        }
        return list;
    }
}

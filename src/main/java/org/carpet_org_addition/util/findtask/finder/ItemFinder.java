package org.carpet_org_addition.util.findtask.finder;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.carpet_org_addition.util.InventoryUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.ItemFindResult;
import org.carpet_org_addition.util.matcher.ItemMatcher;
import org.carpet_org_addition.util.matcher.Matcher;
import org.carpet_org_addition.util.wheel.Counter;
import org.carpet_org_addition.util.wheel.ImmutableInventory;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.List;

public class ItemFinder extends AbstractFinder {
    private final Matcher matcher;
    private final ArrayList<ItemFindResult> list = new ArrayList<>();

    public ItemFinder(World world, BlockPos sourcePos, int range, Matcher matcher) {
        super(world, sourcePos, range);
        this.matcher = matcher;
    }

    @Override
    public ArrayList<ItemFindResult> startSearch() throws CommandSyntaxException {
        long startTimeMillis = System.currentTimeMillis();
        SelectionArea selectionArea = new SelectionArea(world, sourcePos, range);
        // 从容器中查找物品
        findFromContainer(selectionArea, startTimeMillis);
        // 从实体身上查找物品
        findFromEntity(selectionArea.toBox(), startTimeMillis);
        return this.list;
    }

    // 从容器中查找物品
    private void findFromContainer(SelectionArea selectionArea, long startTimeMillis) throws CommandSyntaxException {
        for (BlockPos blockPos : selectionArea) {
            // 判断是否超时
            checkTimeOut(startTimeMillis);
            // 检查当前位置方块实体有没有物品栏
            if (this.world.getBlockEntity(blockPos) instanceof Inventory inventory) {
                Pair<Counter<ItemMatcher>, Boolean> pair = this.count(inventory);
                addResultToList(pair, blockPos, world.getBlockState(blockPos).getBlock().getName());
            }
        }
    }

    // 从实体查找物品
    private void findFromEntity(Box box, long startTimeMillis) throws CommandSyntaxException {
        List<Entity> entities = world.getNonSpectatingEntities(Entity.class, box);
        for (Entity entity : entities) {
            // 检查超时
            checkTimeOut(startTimeMillis);
            if (entity instanceof ItemEntity itemEntity) {
                // 查找掉落物物品
                Pair<Counter<ItemMatcher>, Boolean> pair = count(new SimpleInventory(itemEntity.getStack()));
                addResultToList(pair, entity.getBlockPos(), TextUtils.getTranslate("carpet.commands.finder.item.drops"));
            } else if (entity instanceof EntityPlayerMPFake fakePlayer) {
                // 从假玩家身上查找物品
                Pair<Counter<ItemMatcher>, Boolean> pair = count(fakePlayer.getInventory());
                addResultToList(pair, entity.getBlockPos(), fakePlayer.getName());
            }
        }
    }

    // 统计物品栏内物品数量
    private Pair<Counter<ItemMatcher>, Boolean> count(Inventory inventory) {
        Counter<ItemMatcher> counter = new Counter<>();
        boolean inTheBox = false;
        for (int slotIndex = 0; slotIndex < inventory.size(); slotIndex++) {
            ItemStack itemStack = inventory.getStack(slotIndex);
            if (itemStack.isEmpty()) {
                continue;
            }
            // 从物品栏内找到与匹配器对应的物品并添到计数器
            if (this.matcher.test(itemStack)) {
                counter.add(new ItemMatcher(itemStack), itemStack.getCount());
            } else if (InventoryUtils.isShulkerBoxItem(itemStack)) {
                // 检查潜影盒内的物品
                ImmutableInventory immutableInventory = InventoryUtils.getInventory(itemStack);
                // 从潜影盒内查找物品
                if (immutableInventory.isEmpty()) {
                    // 潜影盒中没有物品，结束本轮循环，检查下一个槽位
                    continue;
                }
                // 遍历潜影盒内的物品栏，找到与匹配器对应的物品
                for (ItemStack stack : immutableInventory) {
                    if (matcher.test(stack)) {
                        // 如果从潜影盒内找到了指定物品，将inTheBox标记为true，表示找到的物品包含从潜影盒内找到的
                        inTheBox = true;
                        // 成员位置的匹配器记录可能是物品标签而不是具体的物品
                        // 如果匹配器记录的是物品标签，则命令完显示反馈时，反馈中的内容可能不正确
                        // 如果是原木（#minecraft:logs），可能对应多种物品：橡木原木白桦原木等
                        // 为了命令执行反馈的正确显示，比如计算堆叠数，匹配器匹配成功的每一种物品都创建一个新的物品匹配器，只对应一种物品
                        // 计数器统计的是新物品匹配器的数量，每一种物品单独计数
                        counter.add(new ItemMatcher(stack), stack.getCount());
                    }
                }
            }
        }
        // 返回值：物品栏内与匹配器匹配的物品计数器，是否包含在潜影盒内找到的
        return new Pair<>(counter, inTheBox);
    }

    // 将查找结果添加到集合
    private void addResultToList(Pair<Counter<ItemMatcher>, Boolean> pair, BlockPos blockPos, Text text) {
        Counter<ItemMatcher> counter = pair.getLeft();
        for (ItemMatcher itemMatcher : counter) {
            // 物品查找结果构造方法参数中的物品查找器被用来在命令反馈中显示物品名称，数量，堆叠组数的内容
            // 但当前成员位置的物品匹配器对象中保存的可能是物品标签，而不是具体的物品，所以不能正确计算上述内容
            // 所以要使用局部位置的物品匹配器对象，它是从count方法中重新创建的只对应一种物品的物品匹配器
            list.add(new ItemFindResult(blockPos, counter.getCount(itemMatcher), pair.getRight(), text, itemMatcher));
        }
    }
}

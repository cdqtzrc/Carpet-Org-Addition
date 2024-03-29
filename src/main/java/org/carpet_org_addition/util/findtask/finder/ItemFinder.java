package org.carpet_org_addition.util.findtask.finder;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.carpet_org_addition.exception.NoNbtException;
import org.carpet_org_addition.util.InventoryUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.ItemFindResult;
import org.carpet_org_addition.util.helpers.Counter;
import org.carpet_org_addition.util.helpers.ImmutableInventory;
import org.carpet_org_addition.util.helpers.SelectionArea;
import org.carpet_org_addition.util.matcher.ItemMatcher;
import org.carpet_org_addition.util.matcher.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ItemFinder extends AbstractFinder {
    private final Matcher matcher;
    private final ArrayList<ItemFindResult> list = new ArrayList<>();

    public ItemFinder(World world, BlockPos sourcePos, int range, CommandContext<ServerCommandSource> context) {
        super(world, sourcePos, range, context);
        Predicate<ItemStack> predicate = ItemPredicateArgumentType.getItemStackPredicate(context, "itemStack");
        matcher = Matcher.of(predicate);
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
            if (this.world.getBlockState(blockPos) instanceof Inventory inventory) {
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
                counter.add(new ItemMatcher(itemStack));
            } else if (InventoryUtils.isShulkerBoxItem(itemStack)) {
                // 检查潜影盒内的物品
                ImmutableInventory immutableInventory;
                try {
                    immutableInventory = InventoryUtils.getInventory(itemStack);
                } catch (NoNbtException e) {
                    continue;
                }
                if (!immutableInventory.isEmpty()) {
                    // 从潜影盒内查找物品
                    inTheBox = true;
                    // 遍历潜影盒内的物品栏，找到与匹配器对应的物品
                    for (ItemStack stack : immutableInventory) {
                        if (matcher.test(stack)) {
                            counter.add(new ItemMatcher(stack));
                        }
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
            // 将计数器内的物品和对应数量添加到集合
            list.add(new ItemFindResult(blockPos, counter.getCount(itemMatcher), pair.getRight(), text, this.matcher));
        }
    }
}

package org.carpet_org_addition.util.task.findtask;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.InventoryUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.matcher.Matcher;
import org.carpet_org_addition.util.task.ServerTask;
import org.carpet_org_addition.util.wheel.Counter;
import org.carpet_org_addition.util.wheel.ImmutableInventory;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.Iterator;

public class ItemFinderTask extends ServerTask {
    private final World world;
    private final SelectionArea selectionArea;
    private final CommandContext<ServerCommandSource> context;
    private Iterator<Entity> iterator;
    private FindState findState;
    private int count;
    private boolean shulkerBox;
    /**
     * tick方法开始执行时的时间
     */
    private long startTime;
    /**
     * 任务被执行的总游戏刻数
     */
    private int tickCount;
    private final Matcher matcher;
    private static final long MAX_FIND_TIME = 200;
    private final ArrayList<Result> results = new ArrayList<>();

    public ItemFinderTask(World world, Matcher matcher, SelectionArea selectionArea, CommandContext<ServerCommandSource> context) {
        this.world = world;
        this.selectionArea = selectionArea;
        this.findState = FindState.BLOCK;
        this.tickCount = 0;
        this.matcher = matcher;
        this.context = context;
    }

    @Override
    public void tick() {
        this.startTime = System.currentTimeMillis();
        this.tickCount++;
        while (true) {
            if (timeout()) {
                return;
            }
            switch (this.findState) {
                case BLOCK -> searchFromContainer();
                case ENTITY -> searchFromEntity();
                case SORT -> sort();
                case FEEDBACK -> feedback();
                default -> {
                    return;
                }
            }
        }
    }

    // 从容器查找
    private void searchFromContainer() {
        for (BlockPos blockPos : selectionArea) {
            if (this.timeout()) {
                return;
            }
            BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
            if (blockEntity instanceof Inventory inventory) {
                // 获取容器名称
                MutableText containerName
                        = inventory instanceof LockableContainerBlockEntity lockableContainer
                        ? lockableContainer.getName().copy()
                        : this.world.getBlockState(blockPos).getBlock().getName();
                this.count(inventory, blockPos, containerName);
            }
        }
        this.findState = FindState.ENTITY;
    }

    // 从实体查找
    private void searchFromEntity() {
        Box box = selectionArea.toBox();
        if (this.iterator == null) {
            this.iterator = this.world.getNonSpectatingEntities(Entity.class, box).iterator();
        }
        while (iterator.hasNext()) {
            if (timeout()) {
                return;
            }
            Entity entity = iterator.next();
            // 掉落物
            if (entity instanceof ItemEntity itemEntity) {
                this.count(new SimpleInventory(itemEntity.getStack()), itemEntity.getBlockPos(), itemEntity.getName().copy());
                continue;
            }
            // 假玩家
            if (entity instanceof EntityPlayerMPFake fakePlayer) {
                this.count(fakePlayer.getInventory(), fakePlayer.getBlockPos(), fakePlayer.getName().copy());
                continue;
            }
            // 容器实体
            if (entity instanceof VehicleInventory inventory) {
                this.count(inventory, entity.getBlockPos(), entity.getName().copy());
            }
        }
        this.findState = FindState.SORT;
    }

    // 统计符合条件的物品
    private void count(Inventory inventory, BlockPos blockPos, MutableText containerName) {
        // 是否有物品是在潜影盒中找到的
        boolean shulkerBox = false;
        Counter<Item> counter = new Counter<>();
        for (int index = 0; index < inventory.size(); index++) {
            ItemStack itemStack = inventory.getStack(index);
            if (this.matcher.test(itemStack)) {
                // 统计符合条件的物品数量
                counter.add(itemStack.getItem(), itemStack.getCount());
            } else if (InventoryUtils.isShulkerBoxItem(itemStack)) {
                ImmutableInventory immutableInventory = InventoryUtils.getInventory(itemStack);
                for (ItemStack stack : immutableInventory) {
                    if (this.matcher.test(stack)) {
                        counter.add(stack.getItem(), stack.getCount());
                        shulkerBox = true;
                    }
                }
            }
        }
        // 如果为物品标签，那么同一个容器中可能出现多中匹配的物品
        for (Item item : counter) {
            // TODO 更新日志：反馈结果容器名称可以为自定义的容器名称
            int count = counter.getCount(item);
            this.count += count;
            if (shulkerBox) {
                this.shulkerBox = true;
            }
            this.results.add(new Result(item, blockPos, containerName, count, shulkerBox));
        }
    }

    // 对结果进行排序
    private void sort() {
        this.results.sort((o1, o2) -> o2.count() - o1.count());
    }

    // 发送命令反馈
    private void feedback() {
        MutableText itemOrTagName;
        boolean isItem = matcher.isItem();
        if (isItem) {
            // 为数量添加鼠标悬停效果
            itemOrTagName = FinderCommand.showCount(matcher.getItem().getDefaultStack(), this.count, this.shulkerBox);
        } else {
            itemOrTagName = TextUtils.regularStyle(String.valueOf(count), null, false, this.shulkerBox, false, false);
        }
        if (this.results.size() <= 10) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find",
                    this.results.size(), itemOrTagName, matcher.toText());
        } else {
            // TODO 取消自定义最大反馈数量
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.item.find.limit",
                    this.results.size(), itemOrTagName, matcher.toText(), 10);
        }
        for (int i = 0; i < this.results.size() && i <= 10; i++) {
            MutableText message = this.results.get(i).toText();
            if (isItem) {
                MessageUtils.sendTextMessage(this.context.getSource(), message);
            } else {
                MessageUtils.sendTextMessage(this.context.getSource(), TextUtils.appendAll(message, this.results.get(i).item().getName().copy()));
            }
        }
        this.findState = FindState.END;
    }

    // 当前任务是否超时
    private boolean timeout() {
        return (System.currentTimeMillis() - this.startTime) > MAX_FIND_TIME;
    }

    @Override
    public boolean isEndOfExecution() {
        return this.findState == FindState.END;
    }

    private record Result(Item item, BlockPos blockPos, MutableText containerName,
                          int count, boolean shulkerBox) {
        private MutableText toText() {
            // 获取要执行的命令，使用%f是因为数值较大时小数可能变成科学计数法
            String command = "/particleLine ~ ~1 ~ %.1f %.1f %.1f"
                    .formatted(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getX() + 0.5);
            return TextUtils.getTranslate("carpet.commands.finder.item.each", TextUtils.blockPos(blockPos, Formatting.GREEN),
                    TextUtils.command(containerName, command, null, null, true),
                    FinderCommand.showCount(item.getDefaultStack(), count, shulkerBox));
        }
    }

    private enum FindState {
        BLOCK, ENTITY, SORT, FEEDBACK, END
    }
}

package org.carpet_org_addition.util.task.findtask;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.exception.TaskExecutionException;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.task.ServerTask;
import org.carpet_org_addition.util.wheel.SelectionArea;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockFindTask extends ServerTask {
    private final ServerWorld world;
    private final SelectionArea selectionArea;
    private final CommandContext<ServerCommandSource> context;
    private final BlockPos sourctePos;
    private Iterator<BlockPos> iterator;
    private FindState findState;
    /**
     * tick方法开始执行时的时间
     */
    private long startTime;
    /**
     * 任务被执行的总游戏刻数
     */
    private int tickCount;
    private final BlockStateArgument argument;
    private final ArrayList<Result> results = new ArrayList<>();

    public BlockFindTask(ServerWorld world, BlockPos sourctePos, SelectionArea selectionArea, CommandContext<ServerCommandSource> context, BlockStateArgument blockStateArgument) {
        this.world = world;
        this.sourctePos = sourctePos;
        this.selectionArea = selectionArea;
        this.context = context;
        this.argument = blockStateArgument;
        this.findState = FindState.SEARCH;
        this.tickCount = 0;
    }

    @Override
    public void tick() {
        this.startTime = System.currentTimeMillis();
        this.tickCount++;
        if (this.tickCount > FinderCommand.MAX_TICK_COUNT) {
            // 任务超时
            MessageUtils.sendCommandErrorFeedback(context, FinderCommand.TIME_OUT);
            this.findState = FindState.END;
            return;
        }
        while (true) {
            if (this.timeout()) {
                return;
            }
            try {
                switch (this.findState) {
                    case SEARCH -> this.searchBlock();
                    case SORT -> this.sort();
                    case FEEDBACK -> this.sendFeedback();
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

    // 查找方块
    private void searchBlock() {
        if (this.iterator == null) {
            this.iterator = this.selectionArea.iterator();
        }
        while (this.iterator.hasNext()) {
            if (this.timeout()) {
                return;
            }
            BlockPos blockPos = this.iterator.next();
            // 获取区块XZ坐标
            int chunkX = ChunkSectionPos.getSectionCoord(blockPos.getX());
            int chunkZ = ChunkSectionPos.getSectionCoord(blockPos.getZ());
            // 判断区块是否已加载
            if (this.world.isChunkLoaded(chunkX, chunkZ)) {
                if (this.argument.test(world, blockPos)) {
                    this.results.add(new Result(this.sourctePos, blockPos));
                }
                if (this.results.size() > FinderCommand.MAXIMUM_STATISTICAL_COUNT) {
                    // 方块过多，无法统计
                    Runnable function = () -> MessageUtils.sendCommandErrorFeedback(this.context,
                            "carpet.commands.finder.block.too_much_blocks",
                            TextUtils.getBlockName(this.argument.getBlockState().getBlock()));
                    throw new TaskExecutionException(function);
                }
            }
        }
        this.findState = FindState.SORT;
    }

    // 对结果排序
    private void sort() {
        if (this.results.isEmpty()) {
            // 从周围没有找到指定方块
            MutableText blockName = TextUtils.getBlockName(this.argument.getBlockState().getBlock());
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.not_found_block", blockName);
            this.findState = FindState.END;
            return;
        }
        this.results.sort((o1, o2) -> MathUtils.compareBlockPos(this.sourctePos, o1.blockPos(), o2.blockPos()));
        this.findState = FindState.FEEDBACK;
    }

    // 发送反馈
    private void sendFeedback() {
        int count = this.results.size();
        Block block = this.argument.getBlockState().getBlock();
        if (count <= FinderCommand.MAX_FEEDBACK_COUNT) {
            MessageUtils.sendCommandFeedback(context.getSource(),
                    "carpet.commands.finder.block.find", count,
                    TextUtils.getBlockName(block));
        } else {
            // 数量过多，只输出距离最近的前十个
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.find.limit",
                    count, TextUtils.getBlockName(block), FinderCommand.MAX_FEEDBACK_COUNT);
        }
        for (int i = 0; i < this.results.size() && i < FinderCommand.MAX_FEEDBACK_COUNT; i++) {
            MessageUtils.sendTextMessage(context.getSource(), this.results.get(i).toText());
        }
        this.findState = FindState.END;
    }

    // 当前任务是否超时
    private boolean timeout() {
        return (System.currentTimeMillis() - this.startTime) > FinderCommand.MAX_FIND_TIME;
    }

    @Override
    public boolean stopped() {
        return this.findState == FindState.END;
    }

    private record Result(BlockPos sourcteBlockPos, BlockPos blockPos) {
        public MutableText toText() {
            return TextUtils.getTranslate("carpet.commands.finder.block.feedback",
                    MathUtils.getBlockIntegerDistance(sourcteBlockPos, blockPos),
                    TextUtils.blockPos(blockPos, Formatting.GREEN));
        }
    }

    private enum FindState {
        SEARCH, SORT, FEEDBACK, END
    }

    @Override
    public String getLogName() {
        return "方块查找";
    }
}

package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.Block;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;
import org.carpet_org_addition.util.findtask.result.BlockFindResult;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class BlockFindFeedback extends AbstractFindFeedback<BlockFindResult> {
    /**
     * 执行命令前玩家所在的位置
     */
    private final BlockPos sourcePos;
    /**
     * 要查找的方块
     */
    private final Block block;

    public BlockFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<BlockFindResult> list, BlockPos sourcePos, Block block, int maxCount) {
        super(context, list, maxCount);
        this.sourcePos = sourcePos;
        this.block = block;
        this.setName("BlockFindFeedbackThread");
    }

    @Override
    public void run() {
        // 将集合中的元素排序
        list.sort((o1, o2) -> MathUtils.compareBlockPos(sourcePos, o1.getBlockPos(), o2.getBlockPos()));
        // 发送命令反馈
        try {
            sendFeedback();
        } catch (TimeoutException e) {
            MessageUtils.sendCommandFeedback(context.getSource(), AbstractFindFeedback.TIME_OUT);
        }
    }

    @Override
    public void sendFeedback() throws TimeoutException {
        int size = list.size();
        //在聊天栏输出方块坐标消息
        if (size <= this.maxCount) {
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.find", size,
                    TextUtils.getBlockName(block));
            for (AbstractFindResult result : list) {
                checkTimeOut();
                MessageUtils.sendTextMessage(context.getSource(), result.toText());
            }
        } else {
            // 数量过多，只输出距离最近的前十个
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.finder.block.find.limit",
                    size, TextUtils.getBlockName(block), this.maxCount);
            for (int i = 0; i < this.maxCount; i++) {
                checkTimeOut();
                MessageUtils.sendTextMessage(context.getSource(), list.get(i).toText());
            }
        }
    }
}

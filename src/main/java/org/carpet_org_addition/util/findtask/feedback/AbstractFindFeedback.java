package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFindFeedback<T extends AbstractFindResult> extends Thread {
    public static final String TIME_OUT = "carpet.commands.finder.timeout";
    /**
     * 用来获取发送消息的服务器命令源
     */
    protected final CommandContext<ServerCommandSource> context;
    /**
     * 保存查找结果的集合
     */
    protected final ArrayList<T> list;
    /**
     * 最多显示多少条消息
     */
    protected final int maxCount;
    protected final long startTime = System.currentTimeMillis();

    protected AbstractFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<T> list, int maxCount) {
        this.context = context;
        this.list = list;
        this.maxCount = maxCount;
    }

    // 检查查找是否超时
    protected final void checkTimeOut() throws TimeoutException {
        if (System.currentTimeMillis() - startTime > 1000) {
            //一秒内没有输出完所有消息，直接中断当前线程执行
            throw new TimeoutException();
        }
    }

    /**
     * 发送命令反馈
     */
    protected abstract void sendFeedback() throws TimeoutException;
}

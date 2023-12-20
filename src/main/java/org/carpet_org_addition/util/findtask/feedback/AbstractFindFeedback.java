package org.carpet_org_addition.util.findtask.feedback;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.findtask.result.AbstractFindResult;

import java.util.ArrayList;

public abstract class AbstractFindFeedback<T extends AbstractFindResult> extends Thread {
    /**
     * 用来获取发送消息的服务器命令源
     */
    protected final CommandContext<ServerCommandSource> context;
    /**
     * 保存查找结果的集合
     */
    protected final ArrayList<T> list;

    protected AbstractFindFeedback(CommandContext<ServerCommandSource> context, ArrayList<T> list) {
        this.context = context;
        this.list = list;
    }

    /**
     * 发送命令反馈
     */
    protected abstract void sendFeedback();
}

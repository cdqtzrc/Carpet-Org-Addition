package org.carpet_org_addition.util.task;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public abstract class PlayerScheduleTask extends ServerTask {
    /**
     * @return 玩家名称
     */
    public abstract String getPlayerName();

    /**
     * 任务取消被取消时调用，可以用来发送消息
     */
    public abstract void onCancel(CommandContext<ServerCommandSource> context);

    public abstract void sendEachMessage(ServerCommandSource source);
}

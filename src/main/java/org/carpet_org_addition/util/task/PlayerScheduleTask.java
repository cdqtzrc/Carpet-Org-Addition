package org.carpet_org_addition.util.task;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

public abstract class PlayerScheduleTask extends ServerTask {
    /**
     * @return 玩家名称
     */
    public abstract String getPlayerName();

    /**
     * @return 任务取消时发送的消息
     */
    public abstract MutableText getCancelMessage();

    public abstract void sendEachMessage(ServerCommandSource source);
}

package org.carpet_org_addition.util.task;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

public abstract class PlayerScheduleTask extends ServerTask {
    public abstract String getPlayerName();

    public abstract MutableText getCancelMessage();

    public abstract void sendEachMessage(ServerCommandSource source);
}

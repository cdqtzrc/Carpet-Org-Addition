package org.carpet_org_addition.util.task;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface ServerTaskManagerInterface {
    void addTask(ServerTask task);

    /**
     * @return 获取任务列表
     */
    ArrayList<ServerTask> getTaskList();

    @NotNull
    static ServerTaskManagerInterface getInstance(MinecraftServer server) {
        return (ServerTaskManagerInterface) server;
    }
}

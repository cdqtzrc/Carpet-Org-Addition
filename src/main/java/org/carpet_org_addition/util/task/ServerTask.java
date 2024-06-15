package org.carpet_org_addition.util.task;

public abstract class ServerTask {
    /**
     * 每个游戏刻都调用此方法
     */
    public abstract void tick();

    /**
     * @return 当前任务是否已经执行完毕
     */
    public abstract boolean isEndOfExecution();
}

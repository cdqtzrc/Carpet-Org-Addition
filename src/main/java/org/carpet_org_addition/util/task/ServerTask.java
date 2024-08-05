package org.carpet_org_addition.util.task;

import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.exception.TaskExecutionException;

public abstract class ServerTask {
    /**
     * 每个游戏刻都调用此方法
     */
    protected abstract void tick();

    /**
     * @return 当前任务是否已经执行完毕
     */
    protected abstract boolean stopped();

    /**
     * 执行任务
     *
     * @return 当前任务是否已经执行结束
     */
    public final boolean taskTick() {
        try {
            this.tick();
            return this.stopped();
        } catch (TaskExecutionException e) {
            e.disposal();
        } catch (RuntimeException e) {
            CarpetOrgAddition.LOGGER.error("{}任务执行时遇到意外错误", this, e);
        }
        return true;
    }

    /**
     * @return 当前任务的名称
     */
    public abstract String toString();
}

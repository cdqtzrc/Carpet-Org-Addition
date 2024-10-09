package org.carpetorgaddition.exception;

public class TaskExecutionException extends RuntimeException {
    /**
     * 异常的应对措施
     */
    private final Runnable countermeasures;

    public TaskExecutionException(Runnable countermeasures) {
        this.countermeasures = countermeasures;
    }

    /**
     * 处理异常
     */
    public void disposal() {
        this.countermeasures.run();
    }
}

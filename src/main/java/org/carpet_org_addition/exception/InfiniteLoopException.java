package org.carpet_org_addition.exception;

/**
 * 无限循环异常
 */
public class InfiniteLoopException extends RuntimeException {
    /**
     * 当前循环的次数
     */
    private int loopCount = 0;
    /**
     * 最大的循环次数，当前循环次数超过这个值时抛出自身的异常
     */
    private final int maxLoopCount;

    public InfiniteLoopException() {
        this.maxLoopCount = 1000;
    }

    public InfiniteLoopException(int maxLoopCount) {
        this.maxLoopCount = maxLoopCount;
    }

    /**
     * 将当前循环次数+1，然后检查循环次数，如果循环次数过多抛出异常
     */
    public void checkLoopCount() {
        loopCount++;
        if (loopCount >= maxLoopCount) {
            throw this;
        }
    }
}

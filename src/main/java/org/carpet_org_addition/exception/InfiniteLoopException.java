package org.carpet_org_addition.exception;

/**
 * 无限循环异常<br/>
 * 有些地方可能需要使用while (true)循环，如果不确定程序能才有限次数内结束，可以尝试抛出本异常结束循环
 */
public class InfiniteLoopException extends RuntimeException {
    public InfiniteLoopException() {
    }
}

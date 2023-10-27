package org.carpet_org_addition.exception;

/**
 * 操作超时异常<br/>
 * 执行一些需要消耗大量性能的代码时，如果程序长时间未执行完成而一直继续执行，可以尝试通过抛出本异常结束方法<br/>
 * 与{@link java.util.concurrent.TimeoutException}超时异常不同，这是一个运行时异常
 */
public class OperationTimeoutException extends RuntimeException {
    public OperationTimeoutException() {
    }
}

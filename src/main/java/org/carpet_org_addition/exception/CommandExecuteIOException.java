package org.carpet_org_addition.exception;

import org.carpet_org_addition.CarpetOrgAddition;

public class CommandExecuteIOException extends RuntimeException {
    private CommandExecuteIOException(Throwable throwable) {
        super(throwable);
    }

    // 创建异常，并将异常信息写入日志
    public static CommandExecuteIOException of(Throwable throwable) {
        CommandExecuteIOException exception = new CommandExecuteIOException(throwable);
        CarpetOrgAddition.LOGGER.error("IO异常", throwable);
        return exception;
    }
}

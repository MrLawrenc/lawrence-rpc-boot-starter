package com.github.lawrence.exception;

/**
 * @author : Lawrence
 * date  2021/7/15 17:44
 */
public class RpcServerException extends RpcException {
    public RpcServerException() {
    }

    public RpcServerException(String message) {
        super(message);
    }

    public RpcServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcServerException(Throwable cause) {
        super(cause);
    }

    public RpcServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
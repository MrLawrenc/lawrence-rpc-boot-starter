package com.github.lawrence.exception;

/**
 * @author : Lawrence
 * date  2021/7/15 17:44
 */
public class RpcClientException extends RpcException {
    public RpcClientException() {
    }

    public RpcClientException(String message) {
        super(message);
    }

    public RpcClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcClientException(Throwable cause) {
        super(cause);
    }

    public RpcClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
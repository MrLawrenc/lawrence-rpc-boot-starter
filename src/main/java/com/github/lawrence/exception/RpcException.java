package com.github.lawrence.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author : Lawrence
 * date  2021/7/14 10:52
 */
public class RpcException extends RuntimeException {
    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static String a(Throwable t) throws Exception{
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        sw.close();
        pw.close();
        return sw.toString();
    }
}
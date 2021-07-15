package com.github.lawrence.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author : Lawrence
 * date  2021/7/14 10:52
 */
@Slf4j
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

    public static String trans(Throwable t) {
        return trans(t, true);
    }

    public static String trans(Throwable t, boolean needLog) {
        if (needLog) {
            log.error("", t);
        }
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            sw.close();
            pw.close();
            return sw.toString();
        } catch (Exception e) {
            if (needLog) {
                log.error("exception trans to string error", e);
            }
            return "main:" + t.getMessage() + "  fit:" + e.getMessage();
        }
    }
}
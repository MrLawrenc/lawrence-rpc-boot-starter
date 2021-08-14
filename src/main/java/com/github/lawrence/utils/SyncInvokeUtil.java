package com.github.lawrence.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.exception.RpcClientException;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * @author : Lawrence
 * date  2021/7/13 20:44
 */
public final class SyncInvokeUtil {

    private static final Map<Channel, Thread> THREAD_MAP = new ConcurrentHashMap<>(8);

    private final static ThreadLocal<RespMsg> rThreadLocal = new ThreadLocal<>();
    private final static RespMsg initObj = new RespMsg();

    //s
    static int wait_seconds = 60;

    //发送同步请求
    public static <T> T syncRequest(Channel channel, RpcMsg rpcMsg, Class<T> returnType) {
        try {
            channel.writeAndFlush(rpcMsg);
            THREAD_MAP.put(channel, Thread.currentThread());
            //only init
            rThreadLocal.set(initObj);
            long start = System.currentTimeMillis();

            //防止虚假唤醒
            /*boolean timeout = false;
            while (!notified&&!timeout) {
                LockSupport.parkNanos(wait_nanos);
                if ((System.currentTimeMillis() - start) * 1000 >= wait_nanos) {
                    timeout = true;
                }
            }*/

            LockSupport.parkUntil(start + wait_seconds * 1000);
            //@see java.util.concurrent.locks.LockSupport.parkNanos(long) javadoc
            if (Thread.interrupted()) {
                throw new RpcClientException("The client was interrupted abnormally");
            }
            if (System.currentTimeMillis() >= start + wait_seconds * 1000) {
                throw new RpcClientException("Server response timed out");
            }
            RespMsg rspMsg = rThreadLocal.get();
            if (Objects.isNull(rspMsg) || Objects.isNull(rspMsg.result)) {
                throw new RpcClientException("The server does not respond to data or the client is falsely awakened(Because the call spuriously (that is, for no reason) returns.)");
            }
            if (rspMsg.exception) {
                throw new RpcClientException(rspMsg.result);
            }
            return new ObjectMapper().readValue(rspMsg.result, returnType);
        } catch (IOException e) {
            throw new RpcClientException(e);
        } finally {
            rThreadLocal.remove();
        }
    }

    //响应同步请求
    public static void respSync(Channel channel, String resultJson, boolean exception) throws Exception {
        Thread thread = THREAD_MAP.remove(channel);
        try {
            if (Objects.isNull(thread)) {
                throw new RpcClientException("The line is disconnected");
            }
            setValue4Thread(thread, new RespMsg(exception, resultJson));
        } finally {
            LockSupport.unpark(thread);
        }
    }

    public static void rmInvalidChannel(Channel channel) {
        THREAD_MAP.remove(channel);
    }


    static void setValue4Thread(Thread thread, RespMsg respMsg) throws Exception {
        Field field = Thread.class.getDeclaredField("threadLocals");
        field.setAccessible(true);
        Object localMap = field.get(thread);
        Field entryArray = localMap.getClass().getDeclaredField("table");
        entryArray.setAccessible(true);
        Object[] entryArrayObj = (Object[]) entryArray.get(localMap);
        for (Object entry : entryArrayObj) {
            if (entry == null) {
                continue;
            }
            Field currentTlField = entry.getClass().getSuperclass().getSuperclass().getDeclaredField("referent");
            currentTlField.setAccessible(true);
            ThreadLocal<?> currentTl = (ThreadLocal<?>) currentTlField.get(entry);
            if (currentTl == rThreadLocal) {
                Field v = entry.getClass().getDeclaredField("value");
                v.setAccessible(true);
                v.set(entry, respMsg);
                break;
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RespMsg {
        boolean exception = false;
        String result;
    }

}
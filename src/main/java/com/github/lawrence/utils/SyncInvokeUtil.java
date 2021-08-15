package com.github.lawrence.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.exception.RpcClientException;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public final class SyncInvokeUtil {

    private static final Map<Channel, InnerThread> THREAD_MAP = new ConcurrentHashMap<>(8);

    private final static RespMsg INIT_OBJ = new RespMsg();
    private final static ThreadLocal<RespMsg> RESP_MSG_THREAD_LOCAL = ThreadLocal.withInitial(() -> INIT_OBJ);

    /**
     * 单位s
     */
    final static long WAIT_SECONDS = 60;
    private final static long WAIT_NANOS = WAIT_SECONDS * 1000;

    /**
     * 发送同步请求
     */
    public static <T> T syncRequest(Channel channel, RpcMsg rpcMsg, Class<T> returnType) {
        THREAD_MAP.put(channel, InnerThread.req(Thread.currentThread()));
        long start = System.currentTimeMillis();
        try {
            channel.writeAndFlush(rpcMsg);

            //防止虚假唤醒
            /*boolean timeout = false;
            while (!notified&&!timeout) {
                LockSupport.parkNanos(wait_nanos);
                if ((System.currentTimeMillis() - start) * 1000 >= wait_nanos) {
                    timeout = true;
                }
            }*/

            LockSupport.parkUntil(start + WAIT_NANOS);
            //@see java.util.concurrent.locks.LockSupport.parkNanos(long) javadoc
            if (Thread.interrupted()) {
                throw new RpcClientException("The client was interrupted abnormally");
            }
            if (System.currentTimeMillis() >= start + WAIT_NANOS) {
                throw new RpcClientException("Server response timed out");
            }
            InnerThread innerThread = THREAD_MAP.remove(channel);


            if (Objects.isNull(innerThread.resultJson)) {
                throw new RpcClientException("The server does not respond to data or the client is falsely awakened(Because the call spuriously (that is, for no reason) returns.)");
            }
            if (innerThread.exception) {
                throw new RpcClientException(innerThread.resultJson);
            }
            return new ObjectMapper().readValue(innerThread.resultJson, returnType);
        } catch (IOException e) {
            throw new RpcClientException(e);
        } finally {
            log.debug("request cost time --> {}ms", System.currentTimeMillis() - start);
            RESP_MSG_THREAD_LOCAL.remove();
        }
    }

    /**
     * 响应同步请求
     */
    public static void respSync(Channel channel, String resultJson, boolean exception) throws Exception {
        InnerThread innerThread = THREAD_MAP.get(channel);
        try {
            if (Objects.isNull(innerThread)) {
                throw new RpcClientException("The line is disconnected");
            }
            innerThread.exception = exception;
            innerThread.resultJson = resultJson;
        } finally {
            LockSupport.unpark(innerThread.thread);
        }
    }

    private static class InnerThread {
        Thread thread;
        String resultJson;
        boolean exception = false;

        static InnerThread req(Thread thread) {
            InnerThread innerThread = new InnerThread();
            innerThread.thread = thread;
            return innerThread;
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
            if (currentTl == RESP_MSG_THREAD_LOCAL) {
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
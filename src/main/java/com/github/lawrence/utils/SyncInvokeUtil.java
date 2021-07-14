package com.github.lawrence.utils;

import com.alibaba.fastjson.JSON;
import com.github.lawrence.codes.RpcMsg;
import io.netty.channel.Channel;

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

    private final static ThreadLocal<String> rThreadLocal = new ThreadLocal<>();

    //发送同步请求
    public static Object syncRequest(Channel channel, RpcMsg rpcMsg, Class<?> returnType) {
        try {
            channel.write(rpcMsg);
            THREAD_MAP.put(channel, Thread.currentThread());
            //only init
            rThreadLocal.set("");
            LockSupport.park();
            String r = rThreadLocal.get();
            return JSON.parseObject(r, returnType);
        } finally {
            rThreadLocal.remove();
        }
    }

    //响应同步请求
    public static void respSync(Channel channel, String resultJson) throws Exception {
        Thread thread = THREAD_MAP.remove(channel);
        if (Objects.isNull(thread)) {
            //
        }
        setValue4Thread(thread, resultJson);
        LockSupport.unpark(thread);
    }

    public static void rmInvalidChannel(Channel channel) throws Exception {
        THREAD_MAP.remove(channel);
    }


    static void setValue4Thread(Thread thread, String respJson) throws Exception {
        Field field = Thread.class.getDeclaredField("threadLocals");
        field.setAccessible(true);
        Object localMap = field.get(thread);
        Field entryArray = localMap.getClass().getDeclaredField("table");
        entryArray.setAccessible(true);
        Object[] entryArrayObj = (Object[]) entryArray.get(localMap);
        for (Object entry : entryArrayObj) {
            Field currentTlField = entry.getClass().getSuperclass().getSuperclass().getDeclaredField("referent");
            currentTlField.setAccessible(true);
            ThreadLocal<?> currentTl = (ThreadLocal<?>) currentTlField.get(entry);
            if (currentTl == rThreadLocal) {
                Field v = entry.getClass().getDeclaredField("value");
                v.setAccessible(true);
                v.set(entry, respJson);
                break;
            }
        }
    }

}
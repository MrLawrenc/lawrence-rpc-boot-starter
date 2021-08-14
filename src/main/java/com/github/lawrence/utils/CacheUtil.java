package com.github.lawrence.utils;

import com.RandomLB;
import com.github.lawrence.client.InstanceChannel;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author : Lawrence
 * date  2021/7/11 17:12
 */
public final class CacheUtil {
    /**
     * key 为serviceName value为该服务对应的bean
     */
    private static final Map<String, Object> SERVICE_MAP = new HashMap<>();

    /**
     * key 为serviceName value为该服务对应的连接通道channel
     */
    private static final Map<String, List<InstanceChannel>> CHANNEL_MAP = new ConcurrentHashMap<>(8);

    public static void addServiceInfo(String serviceName, Object service) {
        SERVICE_MAP.put(serviceName, service);
    }

    public static Object getBeanByServiceName(String serviceName) {
        return SERVICE_MAP.get(serviceName);
    }

    public static Set<String> serviceNames() {
        return SERVICE_MAP.keySet();
    }

    public static Channel getChannelIfPresent(String serviceName, Supplier<List<InstanceChannel>> instanceChannelSupplier) {
        List<InstanceChannel> instanceChannelList = CHANNEL_MAP.get(serviceName);
        if (Objects.isNull(instanceChannelList)) {
            instanceChannelList = instanceChannelSupplier.get();
            CHANNEL_MAP.put(serviceName, instanceChannelList);
        }
        InstanceChannel instanceChannel = new RandomLB().select(instanceChannelList);
        return instanceChannel.getChannel();
    }

    public static void rmChannel(Channel channel) {
        for (Map.Entry<String, List<InstanceChannel>> entry : CHANNEL_MAP.entrySet()) {
            if (entry.getValue().removeIf(instanceChannel -> instanceChannel.getChannel() == channel)) {
                break;
            }
        }
    }

}
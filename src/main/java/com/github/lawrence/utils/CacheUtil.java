package com.github.lawrence.utils;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author : Lawrence
 * date  2021/7/11 17:12
 */
public final class CacheUtil {
    private static final Map<String, Object> SERVICE_MAP = new HashMap<>();

    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>(8);

    public static void addServiceInfo(String serviceName, Object service) {
        SERVICE_MAP.put(serviceName, service);
    }

    public static Set<String> serviceNames() {
        return SERVICE_MAP.keySet();
    }

    public static Channel getChannelIfPresent(String serviceName, Supplier<Channel> channel) {
        Channel cacheChannel = CHANNEL_MAP.get(serviceName);
        if (Objects.isNull(cacheChannel)) {
            cacheChannel = channel.get();
            CHANNEL_MAP.put(serviceName, cacheChannel);
        }
        return cacheChannel;
    }


}
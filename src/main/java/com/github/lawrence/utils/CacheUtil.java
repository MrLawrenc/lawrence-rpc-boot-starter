package com.github.lawrence.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : Lawrence
 * date  2021/7/11 17:12
 */
public final class CacheUtil {
    private static final Map<String, Object> SERVICE_MAP = new HashMap<>();

    public static void addServiceInfo(String serviceName, Object service) {
        SERVICE_MAP.put(serviceName, service);
    }

    public static Set<String> serviceNames() {
        return SERVICE_MAP.keySet();
    }

}
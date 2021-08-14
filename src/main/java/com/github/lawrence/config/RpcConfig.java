package com.github.lawrence.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : Lawrence
 * date  2021/7/11 16:56
 */
@ConfigurationProperties(prefix = "lawrence")
@Data
public class RpcConfig {
    /**
     * 注册中心信息
     */
    private String registryIp = "192.168.0.104";
    private String registryPort = "8848";

    /**
     * 默认服务地址信息
     */
    private String serviceIp = "127.0.0.1";
    private int servicePort = 9010;
}
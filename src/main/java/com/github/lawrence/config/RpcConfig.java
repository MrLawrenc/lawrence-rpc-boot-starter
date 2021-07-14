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
    private String registryIp = "172.27.35.10";
    private String registryPort = "8850";

    /**
     * 默认服务地址信息
     */
    private String serviceIp = "127.0.0.1";
    private int servicePort = 9527;
}
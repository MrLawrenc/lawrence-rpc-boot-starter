package com.github.lawrence.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Lawrence
 * date  2021/7/11 21:37
 */
@Configuration
@EnableConfigurationProperties(ProviderConfig.class)
public class RpcConfig {

    @Bean
    public RpcProcessor rpcProcessor() {
        return new RpcProcessor();
    }

    @Bean
    public RpcStartRegistry rpcStartRegistry(ProviderConfig providerConfig) {
        return new RpcStartRegistry(providerConfig);
    }

}
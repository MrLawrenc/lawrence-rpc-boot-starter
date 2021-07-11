package com.github.lawrence.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Lawrence
 * date  2021/7/11 21:37
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcAutoConfig {

    @Bean
    public RpcProcessor rpcProcessor() {
        return new RpcProcessor();
    }

    @Bean
    public StartRegistryRpcService rpcStartRegistry(RpcConfig rpcConfig) {
        return new StartRegistryRpcService(rpcConfig);
    }

    @Bean
    public Beans beans() {
        return new Beans();
    }

}
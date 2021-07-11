package com.github.lawrence.config;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.github.lawrence.utils.CacheUtil;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Objects;

/**
 * 注册所有rpc服务
 *
 * @author : Lawrence
 * date  2021/7/11 17:02
 */
public class StartRegistryRpcService implements ApplicationListener<ContextRefreshedEvent> {
    private final RpcConfig rpcConfig;

    public StartRegistryRpcService(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //避免因多个重启重复执行 只处理顶层容器
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            NamingService naming = NamingFactory.createNamingService(rpcConfig.getRegistryIp() + ":" + rpcConfig.getRegistryPort());
            for (String serviceName : CacheUtil.serviceNames()) {
                naming.registerInstance(serviceName, rpcConfig.getServiceIp(), rpcConfig.getServicePort(), "TEST1");
            }
        }
    }
}
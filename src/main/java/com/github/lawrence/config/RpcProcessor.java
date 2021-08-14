package com.github.lawrence.config;

import com.github.lawrence.anno.RpcConsumer;
import com.github.lawrence.anno.RpcProvider;
import com.github.lawrence.client.RpcClient;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.utils.CacheUtil;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toMap;

/**
 * @author : Lawrence
 * date  2021/7/11 16:36
 */
public class RpcProcessor implements BeanPostProcessor {

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcProvider rpcProvider = bean.getClass().getAnnotation(RpcProvider.class);
        if (Objects.nonNull(rpcProvider)) {
            provider(rpcProvider, bean);
        }
        tryConsumer(bean);
        return bean;
    }

    public void provider(RpcProvider rpcProvider, Object bean) {
        String serviceName = rpcProvider.name();
        CacheUtil.addServiceInfo(serviceName, bean);
    }

    private static final AtomicInteger COUNT = new AtomicInteger();

    public void tryConsumer(Object bean) throws IllegalAccessException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            RpcConsumer consumer = field.getType().getAnnotation(RpcConsumer.class);
            if (Objects.nonNull(consumer)) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(field.getType());
                enhancer.setNamingPolicy((s, s1, o, predicate) -> "Proxy$" + bean.getClass().getSimpleName() + COUNT.getAndIncrement());
                Method[] methods = field.getType().getMethods();
                Map<String, Method> methodMap = Arrays.stream(methods).collect(toMap(Method::getName, m -> m));
                enhancer.setCallback((MethodInterceptor) (proxyObj, method, params, methodProxy) -> {
                    if (methodMap.containsKey(method.getName())) {
                        RpcMsg rpcMsg = new RpcMsg(RpcMsg.Data.createReq(consumer.service(), method.getName(), params));
                        return RpcClient.sendRpc(consumer.service(), rpcMsg,methodMap.get(method.getName()).getReturnType());
                    }
                    throw new RuntimeException("Call a non-existent method(" + method.getName() + ")");
                });
                Object proxyObj = enhancer.create();
                field.set(bean, proxyObj);
            }
        }
    }


}
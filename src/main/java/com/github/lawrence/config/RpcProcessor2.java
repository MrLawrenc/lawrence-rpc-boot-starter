package com.github.lawrence.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author : MrLawrenc
 * date  2021/8/8 16:40
 * <p>
 * 通过手动想容器注入BeanDefinition
 */
@Slf4j
@Component
public class RpcProcessor2 implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        /**
         * 获取AutoImpl注解的接口，这些接口就需要通过动态代理提供默认实现
         */
        Set<Class<?>> classes = getAutoImplClasses();
        for (Class<?> clazz : classes) {
            /**
             * 获取继承自HandlerRouter的接口的泛型的类型typeName，传入到DynamicProxyBeanFactory
             * 以便传入到DynamicProxyBeanFactory扫描typeName的实现类，然后按照feign和url两种实现
             * 方式分类
             */

            Type[] types = clazz.getGenericInterfaces();
            ParameterizedType type = (ParameterizedType) types[0];
            String typeName = type.getActualTypeArguments()[0].getTypeName();

            /**
             * 通过FactoryBean注入到spring容器，HandlerInterfaceFactoryBean实现以下功能：
             * 1.调用动态代理DynamicProxyBeanFactory提供HandlerRouter子接口的默认实现
             * 2.将第一步的默认实现，注入到spring容器
             */

            //获取类上注解
            //HandlerRouterAutoImpl handlerRouterAutoImpl = clazz.getAnnotation(HandlerRouterAutoImpl.class);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getPropertyValues().add("interfaceClass", clazz);
            definition.getPropertyValues().add("typeName", typeName);
            definition.getPropertyValues().add("context", applicationContext);
            definition.setBeanClass(HandlerInterfaceFactoryBean.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            //beanDefinitionRegistry.registerBeanDefinition(handlerRouterAutoImpl.name(), definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        log.info("------------------------>postProcessBeanFactory");
    }

    /**
     * 通过反射扫描出所有使用HandlerRouterAutoImpl的类
     *
     * @return
     */
    private Set<Class<?>> getAutoImplClasses() {
     /*   Reflections reflections = new Reflections(
                "io.ubt.iot.devicemanager.impl.handler.*",
                new TypeAnnotationsScanner(),
                new SubTypesScanner()
        );
        return reflections.getTypesAnnotatedWith(HandlerRouterAutoImpl.class);*/
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        log.info("------------------->setApplicationContext");
    }


    private String getYmlProperty(String propery) {
        return applicationContext.getEnvironment().getProperty(propery);
    }

    @Data
    public static class HandlerInterfaceFactoryBean<T> implements FactoryBean<T> {
        private Class<T> interfaceClass;
        private String typeName;
        private ApplicationContext context;

        @Override
        public T getObject() throws Exception {
            //生成代理对象
            //Object object = DynamicProxyBeanFactory.newMapperProxy(typeName, context, interfaceClass);
            return (T) null;
        }

        @Override
        public Class<?> getObjectType() {
            return interfaceClass;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }
}
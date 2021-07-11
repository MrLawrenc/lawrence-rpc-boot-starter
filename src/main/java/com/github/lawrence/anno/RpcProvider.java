package com.github.lawrence.anno;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 服务生产者
 *
 * @author : Lawrence
 * date  2021/7/11 16:31
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface RpcProvider {
    String name();
}
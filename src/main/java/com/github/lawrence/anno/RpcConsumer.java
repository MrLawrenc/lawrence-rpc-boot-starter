package com.github.lawrence.anno;

import java.lang.annotation.*;

/**
 * 服务消费者
 *
 * @author : Lawrence
 * date  2021/7/11 16:31
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE,ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcConsumer {
    String service();
}

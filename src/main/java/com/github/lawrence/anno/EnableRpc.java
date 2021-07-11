package com.github.lawrence.anno;

import com.github.lawrence.config.RpcAutoConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Lawrence
 * date  2021/7/11 21:39
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan("com.github.lawrence")
@Import({RpcAutoConfig.class})
public @interface EnableRpc {
}
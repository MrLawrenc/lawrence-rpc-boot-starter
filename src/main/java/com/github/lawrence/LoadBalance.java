package com.github.lawrence;

import com.github.lawrence.client.InstanceChannel;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author Lawrence
 * date  2021/7/11 21:49
 */
public interface LoadBalance {
    /**
     * 算法名
     *
     * @return 算法名
     */
    String name();

    /**
     * 负载均衡算法
     *
     * @param instances 所有实例列表
     * @return 选择到的实例
     */
    InstanceChannel select(List<InstanceChannel> instances) throws NoSuchAlgorithmException;
}

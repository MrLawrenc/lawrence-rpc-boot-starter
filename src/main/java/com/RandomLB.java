package com;

import com.github.lawrence.LoadBalance;
import com.github.lawrence.client.InstanceChannel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;

/**
 * sda
 *
 * @author : Lawrence
 * date  2021/7/14 20:56
 */
public class RandomLB implements LoadBalance {
    @Override
    public String name() {
        return "Random algorithm";
    }

    @Override
    public InstanceChannel select(List<InstanceChannel> instances) {
        if (Objects.isNull(instances)) {
            throw new RuntimeException("No service list available");
        }
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        random.setSeed(System.currentTimeMillis());
        return instances.get(random.nextInt(instances.size()));
    }
}
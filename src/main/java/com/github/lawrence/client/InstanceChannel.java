package com.github.lawrence.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * channel and instance
 *
 * @author : Lawrence
 * date  2021/8/14 10:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceChannel {
    private Instance instance;
    private Channel channel;
}
package com.github.lawrence.client;

import com.NacosUtil;
import com.RandomLB;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.lawrence.codes.MessageDecoder;
import com.github.lawrence.codes.MessageEncoder;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.utils.CacheUtil;
import com.github.lawrence.utils.SyncInvokeUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : Lawrence
 * date  2021/7/11 21:18
 */
@Slf4j
public class RpcClient {

    public static Channel connect(String serviceName) {
        //lb

        Instance instance = new RandomLB().select(NacosUtil.services(serviceName));
        String host = instance.getIp();
        int port = instance.getPort();

        Bootstrap b = new Bootstrap();
        NioEventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 1,
                new DefaultThreadFactory("main-client-work"));
        b.group(work)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ClientHandler clientHandler = new ClientHandler();
                        ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(),
                                new IdleStateHandler(60, 20, 0));
                        ch.pipeline().addLast(clientHandler);
                    }
                });

        ChannelFuture future;
        try {
            future = b.connect(host, port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        future.addListener((ChannelFutureListener) future1 -> {
            boolean success = future1.isSuccess();
            if (success) {
                log.info("connect {} : {} success", host, port);
            } else {
                log.error("connect {} : {} fail", host, port);
            }
        });
        future.channel().closeFuture().addListener(f -> work.shutdownGracefully());
        return future.channel();
    }


    public static Object sendRpc(String serviceName, RpcMsg rpcMsg, Class<?> returnType) throws InterruptedException {
        Channel channel = CacheUtil.getChannelIfPresent(serviceName, () -> RpcClient.connect(serviceName));
        //阻塞等待消息返回
        return SyncInvokeUtil.syncRequest(channel, rpcMsg, returnType);
    }
}
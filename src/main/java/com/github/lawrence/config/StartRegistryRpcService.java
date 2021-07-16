package com.github.lawrence.config;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.github.lawrence.codes.MessageDecoder;
import com.github.lawrence.codes.MessageEncoder;
import com.github.lawrence.server.TcpServerHandler;
import com.github.lawrence.utils.CacheUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 注册所有rpc服务
 *
 * @author : Lawrence
 * date  2021/7/11 17:02
 */
@Slf4j
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
            startRpcListener();
        }
    }

    public void startRpcListener() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup proxyBossGroup = new NioEventLoopGroup(2,
                new DefaultThreadFactory("server-boss"));
        EventLoopGroup proxyWorkerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << 1
                , new DefaultThreadFactory("server-work"));
        bootstrap.group(proxyBossGroup, proxyWorkerGroup)
                .channel(NioServerSocketChannel.class)
                //.handler(new LoggingHandler(LogLevel.TRACE))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                         pipeline.addLast(new IdleStateHandler(60, 20, 0, TimeUnit.SECONDS));

                        pipeline.addLast("decode", new MessageDecoder())
                                .addLast("encode", new MessageEncoder());

                        pipeline.addLast("businessHandler", new TcpServerHandler());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future;
        try {
            future = bootstrap.bind(rpcConfig.getServiceIp(), rpcConfig.getServicePort()).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("Rpc service failed to start", e);
        }
        future.addListener(fu -> {
            if (fu.isSuccess()) {
                log.info("server started on port {}!", rpcConfig.getServicePort());
            } else {
                log.error("server start fail! will close current service!");
                System.exit(0);
            }
        });
        Channel channel = future.channel();
        channel.closeFuture().addListener((ChannelFutureListener) f -> {
            proxyBossGroup.shutdownGracefully();
            proxyWorkerGroup.shutdownGracefully();
        });
    }
}
package com.github.lawrence.server;

import com.github.lawrence.codes.RpcMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author   Lawrence
 * date  2021/7/12 22:07
 */
@Slf4j
@ChannelHandler.Sharable
public class TcpServerHandler extends SimpleChannelInboundHandler<RpcMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) throws Exception {
    }
}

package com.github.lawrence.client;

import com.github.lawrence.codes.RpcMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author : Lawrence
 * date  2021/7/11 21:20
 */
@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws java.lang.Exception {
        if (!(msg instanceof RpcMsg)) {
            throw new Exception("unknown message type: " + msg.getClass().getName());
        }
        RpcMsg rpcMsg = (RpcMsg) msg;

    }
}
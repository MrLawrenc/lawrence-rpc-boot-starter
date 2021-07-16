package com.github.lawrence.client;

import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.utils.CacheUtil;
import com.github.lawrence.utils.SyncInvokeUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : Lawrence
 * date  2021/7/11 21:20
 */
@ChannelHandler.Sharable
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("connected to " + ctx.channel().remoteAddress());
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) throws Exception {
        String resultJson = msg.respResult();
        if (msg.success() || msg.exception()) {
            SyncInvokeUtil.respSync(ctx.channel(), resultJson, msg.exception());
        } else {
            SyncInvokeUtil.respSync(ctx.channel(), "The server responded with an unknown message", true);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SyncInvokeUtil.rmInvalidChannel(ctx.channel());
        CacheUtil.rmChannel(ctx.channel());
        ctx.close();
    }
}
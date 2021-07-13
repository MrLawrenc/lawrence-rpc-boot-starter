package com.github.lawrence.client;

import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.utils.SyncInvokeUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author : Lawrence
 * date  2021/7/11 21:20
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) throws Exception {

        String resultJson = msg.respResult();
        if (msg.success()) {
            SyncInvokeUtil.respSync(ctx.channel(),resultJson);
        } else if (msg.exception()) {

        } else {

        }
    }
}
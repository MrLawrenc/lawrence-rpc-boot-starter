package com.github.lawrence.server;

import com.alibaba.fastjson.JSON;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.exception.RpcServerException;
import com.github.lawrence.utils.CacheUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author Lawrence
 * date  2021/7/12 22:07
 */
@Slf4j
@ChannelHandler.Sharable
public class TcpServerHandler extends SimpleChannelInboundHandler<RpcMsg> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + " is connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) throws Exception {
        //validate

        RpcMsg.Data data = msg.getData();
        log.debug("server received msg:{}", JSON.toJSONString(data));

        Object bean = CacheUtil.getBeanByServiceName(data.findServiceOrMethod(false));
        int paramsLen = data.getArgsType().size();
        Class<?>[] paramTypes = new Class<?>[paramsLen];
        Object[] args = new Object[paramsLen];

        for (int i = 0; i < paramsLen; i++) {
            Class<?> paramType = Class.forName(data.getArgsType().get(i));
            paramTypes[i] = paramType;
            Object paramObj = JSON.parseObject(data.getArgsJson().get(i), paramType);
            args[i] = paramObj;
        }

        Method method = bean.getClass().getMethod(data.findServiceOrMethod(true), paramTypes);
        try {
            Object result = method.invoke(bean, args);
            String r = JSON.toJSONString(result);
            ctx.writeAndFlush(new RpcMsg(RpcMsg.Data.createSuccessResp(r)));
        } catch (Exception e) {
            ctx.writeAndFlush(new RpcMsg(RpcMsg.Data.createExceptionResp(RpcServerException.trans(e))));
        }
    }
}

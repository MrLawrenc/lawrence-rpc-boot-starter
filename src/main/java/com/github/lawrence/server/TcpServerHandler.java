package com.github.lawrence.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.exception.RpcServerException;
import com.github.lawrence.utils.CacheUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;

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
    public void channelRead0(ChannelHandlerContext ctx, RpcMsg msg) throws Exception {
        //validate

        RpcMsg.Data data = msg.getData();
        log.debug("server received msg:{}", new ObjectMapper().writeValueAsString(data));

        Object bean = CacheUtil.getBeanByServiceName(data.findServiceOrMethod(false));
        int paramsLen = data.getArgsType().size();
        Class<?>[] paramTypes = new Class<?>[paramsLen];
        Object[] args = new Object[paramsLen];

        for (int i = 0; i < paramsLen; i++) {
            Class<?> paramType = Class.forName(data.getArgsType().get(i));
            paramTypes[i] = paramType;
            Object paramObj = new ObjectMapper().readValue(data.getArgsJson().get(i), paramType);
            args[i] = paramObj;
        }

        Method method = null;
        try {
            method = bean.getClass().getMethod(data.findServiceOrMethod(true), paramTypes);
            Object result = method.invoke(bean, args);
            String r = new ObjectMapper().writeValueAsString(result);
            ctx.writeAndFlush(new RpcMsg(RpcMsg.Data.createSuccessResp(r)));
        } catch (Throwable e) {
            log.error("invoke {}#{} error!", bean.getClass().getName(), method == null ? "unknown method" : method.getName(), e);
            Throwable cause = e.getCause();
            ctx.writeAndFlush(new RpcMsg(RpcMsg.Data.createExceptionResp(Objects.nonNull(cause) ? cause.getMessage() : RpcServerException.trans(e, false))));
        }
    }
}

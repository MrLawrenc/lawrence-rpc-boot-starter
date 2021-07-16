package com.github.lawrence.codes;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Lawrence
 * date  2021/7/11 19:10
 */
@Slf4j
public class MessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        int len = byteBuf.readInt();
        if (byteBuf.readableBytes() < len) {
            log.debug("The data length is not enough, continue to wait......");
            return;
        }
        String data = byteBuf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        RpcMsg message = JSON.parseObject(data, RpcMsg.class);
        out.add(message);
    }
}

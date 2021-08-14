package com.github.lawrence.codes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author Lawrence
 * date  2021/7/11 19:10
 */
@Slf4j
public class MessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws IOException {
        int len = byteBuf.readInt();
        if (byteBuf.readableBytes() < len) {
            log.debug("The data length is not enough, continue to wait......");
            return;
        }

        //String data = byteBuf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        byte[] data = new byte[len];
        byteBuf.readBytes(data);
        RpcMsg message = new ObjectMapper().readValue(data, RpcMsg.class);
        out.add(message);
    }
}

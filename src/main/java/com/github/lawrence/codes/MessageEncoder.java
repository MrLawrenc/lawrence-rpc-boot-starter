package com.github.lawrence.codes;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Lawrence
 * date  2021/7/11 19:05
 */
public class MessageEncoder extends MessageToByteEncoder<RpcMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg msg, ByteBuf byteBuf) throws Exception {
        byte[] data = JSON.toJSONBytes(msg);
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}

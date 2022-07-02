package com.foundation.io.simple.contact.room.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/6/21 13:57
 */
public class CarryLengthFrameEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.capacity();
        ByteBuf buf = Unpooled.buffer(2 + length);
        // 先将消息长度写入，也就是消息头
        buf.writeShort(length);
        // 消息体中包含我们要发送的数据
        buf.writeBytes(msg);
        out.add(buf);
    }
}

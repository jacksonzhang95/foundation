package com.foundation.io.simple.contact.room.serialize.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author : jacksonz
 * @date : 2021/6/20 22:44
 */
public class ProtostuffEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] datas = ProtostuffUtil.serializer(msg);
        // 读取消息的长度
        int length = datas.length;
        //  初始化ByteBuf
        ByteBuf buf = Unpooled.buffer(2 + length);
        // 先将消息长度写入，也就是消息头
        buf.writeShort(length);
        // 消息体中包含我们要发送的数据
        buf.writeBytes(datas);
        out.writeBytes(buf);
    }
}

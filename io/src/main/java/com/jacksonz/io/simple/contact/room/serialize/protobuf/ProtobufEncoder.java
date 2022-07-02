package com.foundation.io.simple.contact.room.serialize.protobuf;

import com.foundation.io.simple.contact.room.serialize.protobuf.domain.ProtobufMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author : jacksonz
 * @date : 2021/6/20 21:05
 */
public class ProtobufEncoder extends MessageToByteEncoder<ProtobufMessage.protobufMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtobufMessage.protobufMessage msg, ByteBuf out)
            throws Exception {

        // 将对象转换为byte
        byte[] bytes = msg.toByteArray();
        // 读取消息的长度
        int length = bytes.length;
        //  初始化ByteBuf
        ByteBuf buf = Unpooled.buffer(2 + length);
        // 先将消息长度写入，也就是消息头
        buf.writeShort(length);
        // 消息体中包含我们要发送的数据
        buf.writeBytes(bytes);
        out.writeBytes(buf);
    }
}

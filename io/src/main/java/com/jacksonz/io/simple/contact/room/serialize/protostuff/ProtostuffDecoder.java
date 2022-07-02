package com.foundation.io.simple.contact.room.serialize.protostuff;

import com.foundation.io.simple.contact.room.MessageDto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/6/20 22:44
 */
public class ProtostuffDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            // 字节转成对象
            MessageDto messageDto = ProtostuffUtil.deserializer(msg.array(), MessageDto.class);
            if (messageDto != null) {
                out.add(messageDto);
            }
        } catch (Exception e) {
            System.out.println("解析异常");
        }
    }
}

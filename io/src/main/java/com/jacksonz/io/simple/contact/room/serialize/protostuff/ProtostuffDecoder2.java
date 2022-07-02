package com.foundation.io.simple.contact.room.serialize.protostuff;

import com.foundation.io.simple.contact.room.MessageDto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/6/21 8:38
 */
public class ProtostuffDecoder2 extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            MessageDto msgReal = ProtostuffUtil.deserializer(msg.array(), MessageDto.class);
            if (msgReal != null) {
                out.add(msgReal);
            }
        } catch (Exception e) {
            System.out.println("解析异常");
        }
    }

}
package com.foundation.io.simple.contact.room.serialize.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/6/20 22:44
 */
public class ProtostuffEncoder2 extends MessageToMessageEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        byte[] datas = ProtostuffUtil.serializer(msg);
        ByteBuf buf = Unpooled.buffer(datas.length);
        buf.writeBytes(datas);
        out.add(buf);
    }
}

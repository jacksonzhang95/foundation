package com.foundation.io.simple.contact.room.serialize.protobuf;

import com.foundation.io.simple.contact.room.serialize.protobuf.domain.ProtobufMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/6/20 21:05
 */
public class ProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            ProtobufMessage.protobufMessage protobufMsg = ProtobufMessage.protobufMessage.parseFrom(msg.array());
            if (protobufMsg != null) {
                out.add(protobufMsg);
            }
        } catch (Exception e) {
        }
    }
}

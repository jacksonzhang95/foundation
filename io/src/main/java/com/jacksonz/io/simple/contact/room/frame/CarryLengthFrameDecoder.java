package com.foundation.io.simple.contact.room.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/6/21 10:58
 */
public class CarryLengthFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 标记一下当前的readIndex的位置
        in.markReaderIndex();
        // 由于使用2字节记录数据长度, 可读字节数不够2字节证明没有数据可读
        if (in.readableBytes() < 2) {
            return;
        }
        // 读取传送过来的消息的长度
        int length = in.readUnsignedShort();
        // 长度如果小于0(非法数据，关闭连接)
        if (length < 0) {
            ctx.close();
        }
        // 读到的消息体长度如果小于传送过来的消息长度(拆包)
        if (length > in.readableBytes()) {
            // 重置读取位置
            in.resetReaderIndex();
            return;
        }
        ByteBuf frame = Unpooled.buffer(length);
        in.readBytes(frame);
        out.add(frame);
    }
}

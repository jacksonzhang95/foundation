package com.foundation.io.simple.contact.room;

import com.foundation.io.simple.contact.room.frame.CarryLengthFrameEncoder;
import com.foundation.io.simple.contact.room.serialize.protostuff.ProtostuffEncoder2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author : jacksonz
 * @date : 2021/5/29 11:00
 */
public class ContactRoomClient {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup workEventLoopGroup = new NioEventLoopGroup(1);

        Bootstrap bootstrap = new Bootstrap()
                .group(workEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
//                                .addLast(new ObjectDecoder(10240, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
//                                .addLast(new ProtobufEncoder())
                                .addLast(new CarryLengthFrameEncoder())
                                .addLast(new ProtostuffEncoder2())

//                                .addLast(new ObjectEncoder())
//                                 .addLast(new ProtobufDecoder())
//                                .addLast(new CarryLengthFrameDecoder())
//                                .addLast(new ProtostuffDecoder2())
//                                .addLast(new ContactRoomClientNettyHandler())
                        ;
                    }
                })
                ;

        try {
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9001);
            channelFuture.sync();

            Channel channel = channelFuture.channel();

            /**
             * 测试 StringEncoder/StringEncoder 发送字符串对象
             */
//            Scanner sc = new Scanner(System.in);
//            while (true) {
//                channel.writeAndFlush(sc.next());
//            }

            /**
             * 测试 使用ObjectDecoder/ObjectEncoder 发送对象
             */
            for (int i = 0; i < 1000; i++) {
                MessageDto messageDto = new MessageDto();
                messageDto.setId("123");
                messageDto.setName("321");
                channel.write(messageDto);
            }
            channel.flush();
            /*
            ProtobufMessage.protobufMessage protoMsg = ProtobufMessage.protobufMessage.newBuilder().setContent("test").setIp("123321").build();
            channel.writeAndFlush(protoMsg);
             */

            Thread.sleep(100000L);

        } finally {
            workEventLoopGroup.shutdownGracefully();
        }
    }
}

class ContactRoomClientNettyHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
    }
}

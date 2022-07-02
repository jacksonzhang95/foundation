package com.foundation.io.simple.contact.room;

import com.foundation.io.simple.contact.room.frame.CarryLengthFrameDecoder;
import com.foundation.io.simple.contact.room.serialize.protostuff.ProtostuffDecoder2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2021/5/27 22:08
 */
public class ContactRoomServer {

    public static void main(String[] args) throws InterruptedException {
        // 服务端创建两个线程组, 一个是Boss线程组，一个是work线程组
        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(1);

        try {
            // 构建Netty模型
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()

                                    /**
                                     * 添加ChannelInboundHandlerAdapter(处理数据流入)
                                     * 按添加顺序遍历执行
                                     */

                                    // 粘包拆包处理器 - 回车换行
//                                    .addLast(new LineBasedFrameDecoder(1000))
                                    // 粘包拆包处理器 - 特殊分隔符
//                                    .addLast(new c(1000, Unpooled.copiedBuffer("_".getBytes())))
                                    // 粘包拆包处理器 - 固定长度
//                                    .addLast(new FixedLengthFrameDecoder(1000))
                                    // 粘包拆包处理器 - 头部带数据长度处理
                                    .addLast(new CarryLengthFrameDecoder())

                                    // 字符串解码器
//                                    .addLast(new StringDecoder())
                                    // 对象解码器（JDK序列化）
//                                    .addLast(new ObjectDecoder(10240, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                                    // protobuf解码器
//                                    .addLast(new ProtobufDecoder())
                                    // protostuff解码器
                                    .addLast(new ProtostuffDecoder2())

                                    // 添加业务处理器
                                    .addLast(new ContactRoomServerNettyHandler())

                                    // 心跳处理器(IdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime,TimeUnit unit) )
//                                    .addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS))


                                    /**
                                     * 添加ChannelOutboundHandlerAdapter(处理数据流出)
                                     * 按添加顺序的倒序遍历执行
                                     */

                                    // 粘包拆包处理器 - 头部带数据长度处理
//                                    .addLast(new CarryLengthFrameEncoder())

                                    // 字符串编码器
//                                    .addLast(new StringEncoder())
                                    // 对象编码器（JDK序列化）
//                                    .addLast(new ObjectEncoder())
                                    // protobuf编码器
//                                    .addLast(new ProtobufEncoder())
                                    // protostuff编码器
//                                    .addLast(new ProtostuffEncoder2());
                            ;
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(9001).sync();

            channelFuture.channel().closeFuture().sync();

        } finally {
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
        }

    }
}

class ContactRoomServerNettyHandler extends ChannelInboundHandlerAdapter {

    private static volatile Map<String, Channel> channelMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = String.valueOf(channel.remoteAddress());
        channelMap.put(remoteAddress, channel);
        String messageFormat = "客户端[%s]: 上线了";
        System.out.println(String.format(messageFormat, remoteAddress));
        for (String key : channelMap.keySet()) {
            if (Objects.equals(key, remoteAddress)) {
                continue;
            }
            Channel otherChannel = channelMap.get(key);
            otherChannel.writeAndFlush(String.format(messageFormat, remoteAddress));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = String.valueOf(channel.remoteAddress());
        channelMap.remove(remoteAddress);
        String messageFormat = "客户端[%s]下线了";
        System.out.println(String.format(messageFormat, remoteAddress));
        for (String key : channelMap.keySet()) {
            if (Objects.equals(key, remoteAddress)) {
                continue;
            }
            Channel otherChannel = channelMap.get(key);
            otherChannel.writeAndFlush(String.format(messageFormat, remoteAddress));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = String.valueOf(channel.remoteAddress());

        String messageFormat = "客户端[%s]: %s";
        System.out.println(String.format(messageFormat, remoteAddress, msg));
        for (String key : channelMap.keySet()) {
            if (Objects.equals(key, remoteAddress)) {
                continue;
            }
            Channel otherChannel = channelMap.get(key);
            otherChannel.writeAndFlush(String.format(messageFormat, remoteAddress, msg));
        }
    }
}

package com.foundation.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * @author : jacksonz
 * @date : 2021/5/27 20:54
 */
public class NettyClientDemo {

    public static void main(String[] args) throws Exception {
        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建客户端启动对象
            //注意客户端使用的不是 ServerBootstrap 而是 Bootstrap
            Bootstrap bootstrap = new Bootstrap();

            //设置相关参数
            //设置线程组
            bootstrap.group(group)
                    // 使用 NioSocketChannel 作为客户端的通道实现
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            //加入处理器
                            channel.pipeline()
                                    .addLast(new NettyClientHandlerDemo());
                        }
                    });

            System.out.println("netty client start");

            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

            Channel channel = channelFuture.channel();

            //对关闭通道进行监听
            channel.closeFuture();

            Scanner sc = new Scanner(System.in);
            while (true) {
                ByteBuf buf = Unpooled.copiedBuffer(sc.nextLine(), CharsetUtil.UTF_8);
                channel.writeAndFlush(buf);
            }

        } finally {
            group.shutdownGracefully();
        }
    }
}

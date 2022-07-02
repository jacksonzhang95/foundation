package com.foundation.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @author : jacksonz
 * @date : 2021/5/27 20:17
 */
public class NettyServiceDemo {

    public static void main(String[] args) throws Exception {

        /**
         * 创建两个线程组bossGroup和workerGroup
         * 子线程个数，默认为cpu核数的两倍
         *
         * workerGroup负责读写事件 和 业务处理
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            //创建服务器端的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来配置参数
            //设置两个线程组
            bootstrap.group(bossGroup, workerGroup)
                    //使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)

                    /**
                     * 配置socket参数
                     *
                     * ChannelOption.SO_BACKLOG:
                     *      初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的,所以同一时间只能处理一个客户端连接.
                     *      多个客户端同时来的时候,服务端将不能处理的客户端连接请求放在队列中等待处理
                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)

                    /**
                     * 配置workerGroup的ChannelHandler
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new NettyServerHandlerDemo());
                        }
                    });

            ChannelFuture cf = bootstrap
                    /**
                     * 绑定一个端口, 生成了一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
                     * 启动服务器(并绑定端口)，bind是异步操作
                     */
                    .bind(9000)
                    // sync方法是等待异步操作执行完毕
                    .sync();

            //给cf注册监听器，监听我们关心的事件
            /*
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("监听端口9000成功");
                    } else {
                        System.out.println("监听端口9000失败");
                    }
                }
            });
            */

            cf.channel()
                    // 对通道关闭进行监听，closeFuture是异步操作，监听通道关闭
                    .closeFuture()
                    // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
                    .sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
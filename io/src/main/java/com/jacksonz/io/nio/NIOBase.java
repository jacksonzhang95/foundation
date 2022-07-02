package com.foundation.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/5/27 19:21
 */
public class NIOBase {

    /**
     * 保存客户端连接
     */
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void startServer() throws IOException {
        // 创建NIO ServerSocketChannel,与BIO的serverSocket类似
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));

        // 设置ServerSocketChannel为非阻塞
        serverSocket.configureBlocking(false);

        while (true) {
            /**
             * 非阻塞模式accept方法不会阻塞，否则会阻塞
             * NIO的非阻塞是由操作系统内部实现的，底层调用了linux内核的accept函数
             */
            SocketChannel socketChannel = serverSocket.accept();
            // 如果有客户端进行连接
            if (socketChannel != null) {
                System.out.println("连接成功");
                // 设置SocketChannel为非阻塞
                socketChannel.configureBlocking(false);
                // 保存客户端连接在List中
                channelList.add(socketChannel);
            }

            // 遍历连接进行数据读取
            Iterator<SocketChannel> iterator = channelList.iterator();
            while (iterator.hasNext()) {
                SocketChannel sc = iterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                // 非阻塞模式read方法不会阻塞，否则会阻塞
                int len = sc.read(byteBuffer);
                // 如果有数据，把数据打印出来
                if (len > 0) {
                    System.out.println("接收到消息：" + new String(byteBuffer.array(), Charset.forName("UTF-8")));
                }
                // 如果客户端断开，把socket从集合中去掉
                else if (len == -1) {
                    iterator.remove();
                    System.out.println("客户端断开连接");
                }
            }
        }
    }

    public static void startClient() throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9000));
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put("Hello".getBytes(Charset.forName("UTF-8")));
        // 需要切换模式
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        Thread.sleep(100000L);
    }

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                startServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(2000L);

        new Thread(() -> {
            try {
                startClient();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

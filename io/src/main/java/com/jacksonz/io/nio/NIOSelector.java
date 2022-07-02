package com.foundation.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author : jacksonz
 * @date : 2021/5/27 19:24
 */
public class NIOSelector {

    public static void startServer() throws IOException {
        // 创建NIO ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));

        // 设置ServerSocketChannel为非阻塞
        serverSocket.configureBlocking(false);

        // 打开Selector处理Channel，即创建epoll
        Selector selector = Selector.open();

        /**
         * ServerSocketChannel只能注册OP_ACCEPT
         * 把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
         */
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 阻塞等待需要处理的事件发生
//            selector.select();
            System.out.println(selector.selectNow());

            // 获取selector中注册的全部事件的 SelectionKey 实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            // 遍历SelectionKey对事件进行处理
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                }
                // 如果是OP_READ事件，则进行读取和打印
                else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = socketChannel.read(byteBuffer);
                    // 如果有数据，把数据打印出来
                    if (len > 0) {
                        System.out.println("接收到消息：" + new String(byteBuffer.array()));
                    }
                    // 如果客户端断开连接，关闭Socket
                    else if (len == -1) {
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }
                //从事件集合里删除本次处理的key，防止下次select重复处理
                iterator.remove();
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void startClient() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        // 异步，所以是非阻塞
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress("localhost", 9000));
        System.out.println("发送成功");
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put("Hello".getBytes(Charset.forName("UTF-8")));
        // 需要切换模式
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    public static void main(String[] args) throws Exception {
        /*
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
            }
        }).start();
         */

        int OP_READ = 1 << 0;
        int OP_WRITE = 1 << 2;
        int OP_CONNECT = 1 << 3;
        int OP_ACCEPT = 1 << 4;
//
        System.out.println(OP_READ);
        System.out.println(OP_WRITE);
        System.out.println(OP_CONNECT);
        System.out.println(OP_ACCEPT);

        int readyOps = OP_ACCEPT;

        System.out.println(readyOps & SelectionKey.OP_CONNECT);

        System.out.println(readyOps & SelectionKey.OP_WRITE);

        System.out.println((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)));

    }
}

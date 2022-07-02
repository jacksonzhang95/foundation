package com.foundation.io.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author : jacksonz
 * @date : 2021/5/25 20:54
 */
public class BIOServer {

    private static int port = 9000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            //阻塞等待连接
            Socket clientSocket = serverSocket.accept();
            System.out.println("有客户端连接了。。");
            handler(clientSocket);

            new Thread(() -> {
                try {
                    handler(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("准备read。。");
        //接收客户端的数据，阻塞方法，没有数据可读时就阻塞
        int read = clientSocket.getInputStream().read(bytes);
        System.out.println("read完毕。。");
        if (read != -1) {
            System.out.println("接收到客户端的数据：" + new String(bytes, 0, read));
        }
        clientSocket.getOutputStream().write("HelloClient".getBytes());
        clientSocket.getOutputStream().flush();
    }
}
package com.foundation.io.bio;

import java.io.IOException;
import java.net.Socket;

/**
 * @author : jacksonz
 * @date : 2021/5/25 21:08
 */
public class BIOClient {

    private static int port = 9000;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", port);
        //向服务端发送数据
        socket.getOutputStream().write("test".getBytes());
        socket.getOutputStream().flush();

        //接收服务端回传的数据
        byte[] bytes = new byte[1024];
        socket.getInputStream().read(bytes);
        System.out.println("接收到服务端的数据：" + new String(bytes));

        socket.close();
    }
}

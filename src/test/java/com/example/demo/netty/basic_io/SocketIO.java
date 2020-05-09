package com.example.demo.netty.basic_io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *
 * BIO
 *
 * 这种实现方式最大的弊端时阻塞blocking
 * 第一个客户端连上来之后，下一个客户端无法连接
 *
 * 测试：xhell连接到linxu
 * 命令：nc 192.168.158.1 9090,然后发送数据
 *
 */
public class SocketIO {

    public static void main(String[] args) {

        try {
            /**
             * 干了三件事情：
             * 1: new ServerSocket()=6fd
             * 2: bind(6fd,9090)
             * 3: listen(6fd)
             */
            ServerSocket server = new ServerSocket(9090);
            System.out.println("step1: new ServerSocket(9090)");
            /**
             * 阻塞1
             * accetp(6fd) 监听得到客户端的文件描述符
             * 如果没有客户端连上来则一直阻塞
             * 如果有客户端连上来之后则会得到一个读写客户端的文件描述符  ---> 7fd
             *
             */
            Socket client = server.accept();
            System.out.println("step2: client\t" + client.getPort());

            /**
             * 为了支持并发可一在此处抛出线程
             * 但是一个链接对应一个线程会极大的消耗性能
             */
            InputStream in = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //阻塞2
            System.out.println(reader.readLine());

            while (true){

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

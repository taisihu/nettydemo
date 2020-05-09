package com.example.demo.netty.basic_io;

import ch.qos.logback.core.util.TimeUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 *
 * 这种方式的server端和读取都不会阻带
 * 弊端是资源浪费比较大，需要遍历每一个客户端，事实上客户端未必发送了数据
 *
 *
 */
public class SocketNIO {

    public static void main(String[] args) {

        LinkedList<SocketChannel> clients = new LinkedList<SocketChannel>();
        try {
            ServerSocketChannel ss = ServerSocketChannel.open();

            ss.bind(new InetSocketAddress(9090));
            ss.configureBlocking(false);

            while (true){

                TimeUnit.SECONDS.sleep(1);

                SocketChannel client = ss.accept();
                if(null == client){
                    System.out.println("null...");
                }else {
                    client.configureBlocking(false);
                    int prot = client.socket().getPort();
                    System.out.println("client..prot:"+prot);
                    clients.add(client);
                }

                //创建一个缓冲过去，可以在堆内也可以在堆外
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                for(SocketChannel c : clients){
                    int num = c.read(buffer);
                    if(num>0){
                        //将buffer中的指针移动过到开始位置，然后将标志结尾的limit
                        buffer.flip();
                        byte[] byteArr = new byte[buffer.limit()];
                        //将缓冲区中内容填充到数组中去
                        buffer.get(byteArr);
                        String str = new String(byteArr);
                        System.out.println(c.socket().getPort()+" : " + str);
                        buffer.clear();
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}

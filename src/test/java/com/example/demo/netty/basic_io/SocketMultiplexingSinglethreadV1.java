package com.example.demo.netty.basic_io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用模型一：boss和workder在同一个线程中
 *
 * 此实现方式是循环询问内核事件到达
 * 1 selector.register:监听事件
 * 2 selector.select获取监听事件
 * 3 selectionKeys:遍历事件集合,循环串行处理
 * 4 获取数据，可以将数据保存到一个队列或其他缓存中，设置一个事件触发处理数据
 *
 */
public class SocketMultiplexingSinglethreadV1 {

    private ServerSocketChannel server = null;
    private Selector selector = null;
    int port = 9090;

    public void initServer(){
        try {
            /**
             * 下面三行代表的三个步骤是不变的，分别是
             * 1 创建socket
             * 2 设置非阻塞
             * 3 绑定端口
             */
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            //得到一个多路复用器
            selector = Selector.open();
            /**
             * 将server注册到多路复用器，监听accept事件
             * 返回值serverKey包含了Server的channel
             */
            SelectionKey serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        initServer();
        System.out.println("服务器启动了。。。");
        try {
            while (true){
                //直接select，询问内核是否有事件到达，因为已经将监听事件注册到selector中，selector只会返回被监听的事件
                while (selector.select(1000)>0){

                    //返回有事件到达的集合
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        //判断事件的状态
                        if(key.isAcceptable()){
                            //是否是可连接状态，如果是注册这个连接的可读事件到多路复用器
                            System.out.println("接收事件。。。");
                            acceptHandler(key);
                        }else if(key.isReadable()){
                            //如果可读，则读取这个事件中的数据
                            System.out.println("读取事件。。。");
                            readHandler(key);
                        }
                    }
                }
            }
        }catch (IOException e){

        }


    }

    /**
     * @param key
     *
     * key包含了Server(ServerSocketChannel)，selector
     * 所以直接可以从key中获取server
     *
     */
    public void acceptHandler(SelectionKey key){
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            //代表了一个新的客户端到达
            SocketChannel client = ssc.accept();
            //将新客户端设置为非阻塞
            client.configureBlocking(false);
            //为新客户端创建一个单独的读写缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            /**
             * 监听客户端的读事件，为每个客户端单独设置一个读写缓冲区，防止多个事件操作同一个缓冲区
             * 注册事件会返回一个selectKey，包含了客户端的channel，buffer,所以可以将返回设置到其他线程中去处理
             * 此时selector已经监听了server的accept事件和客户端的可读取事件
             * 可以将事件无限注册到多路复用器中
             */
            SelectionKey cliKey = client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("----------------------------");
            System.out.println("新客户端： " + client.getRemoteAddress());
            System.out.println("----------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readHandler(SelectionKey key){
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        //先清空缓冲区，防止有脏数据
        buffer.clear();
        int read = 0;
        try {
            while (true){
                //将数据从客户端读取，填充到buffer
                read = client.read(buffer);
                if(read>0){
                    buffer.flip();
                    while (buffer.hasRemaining()){
                        client.write(buffer);
                    }
                }else if(read == 0){
                    break;
                }else{
                    //会出现-1，表示客户端关闭，出现了close_wait,会出现死循环
                    //优雅的处理方式是触发一个事件窗口，判断-1出现次数，过多则关闭客户端
                    client.close();
                    break;
                }
            }
        }catch (IOException e){

        }
    }

    public static void main(String[] args) {

        SocketMultiplexingSinglethreadV1 socketMultiplexingSinglethreadV1 = new SocketMultiplexingSinglethreadV1();
        socketMultiplexingSinglethreadV1.start();

    }

}

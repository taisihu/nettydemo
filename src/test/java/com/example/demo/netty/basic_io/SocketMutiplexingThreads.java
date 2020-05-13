package com.example.demo.netty.basic_io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 此模型为一个server，两个worker的多线程模型
 * server只监听连接事件
 * worker监听读写事件
 *
 * 这种模型需要循环遍历，每次都要与内核有一个数据传输的过程
 *
 * server将监听到的连接事件的读取事件注册到worker线程上
 * 由worker来进行数据读取处理
 *
 *
 */
public class SocketMutiplexingThreads {

    private ServerSocketChannel server = null;
    private Selector selector1 = null;
    private Selector selector2 = null;
    private Selector selector3 = null;
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
            //得到Server多路复用器
            selector1 = Selector.open();
            //得到workder多路复用器
            selector2 = Selector.open();
            selector3 = Selector.open();
            /**
             * 将server注册到多路复用器，监听accept事件
             * 返回值serverKey包含了Server的channel
             */
            SelectionKey serverKey = server.register(selector1, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SocketMutiplexingThreads service = new SocketMutiplexingThreads();
        service.initServer();
        NIOThread t1 = new NIOThread(service.selector1,2);
        NIOThread t2 = new NIOThread(service.selector2);
        NIOThread t3 = new NIOThread(service.selector3);

        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t2.start();
        t3.start();

        System.out.println("服务器启动了");

    }

}

class NIOThread extends Thread{

    Selector selector = null;
    static int SELECTOR_NUM = 0;
    int id = 0;
    static BlockingQueue<SocketChannel>[] queue;
    static AtomicInteger idx = new AtomicInteger();
    boolean boss = false;

    //boss用的构造器
    NIOThread(Selector selector,int n){
        this.selector = selector;
        this.SELECTOR_NUM = n;

        queue = new LinkedBlockingDeque[SELECTOR_NUM];
        for (int i = 0; i < SELECTOR_NUM; i++) {
            queue[i] = new LinkedBlockingDeque<SocketChannel>();
        }
        boss = true;
        System.out.println("boss 启动了。。。");

    }

    //worker的构造器
    NIOThread(Selector selector){
        this.selector = selector;
        id = idx.getAndIncrement() % SELECTOR_NUM;
        System.out.println("worker-"+id+" 启动了。。。");
    }

    @Override
    public void run() {

        try {
            while (true){
                //直接select，询问内核是否有事件到达，因为已经将监听事件注册到selector中，selector只会返回被监听的事件
                //selector.select()：这个方法调用的是操作系统中的epoll_wait()
                while (selector.select(1000)>0){
                    //返回有事件到达的集合
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        //判断事件的状态
                        if(key.isAcceptable()){
                            //只有boss可以执行到这个操作，因为只有boss注册了accept事件
                            //是否是可连接状态，如果是注册这个连接的可读事件到多路复用器
                            acceptHandler(key);
                        }else if(key.isReadable()){
                            //只有worker才会走到这个步骤，因为wokder监听了可读事件
                            //如果可读，则读取这个事件中的数据
                            System.out.println("读取事件。。。");
                            readHandler(key);
                        }
                    }
                }

                //将客户端读写注册到当前worker线程，boss不参与
                if(!boss && !queue[id].isEmpty()){
                    ByteBuffer buffer = ByteBuffer.allocate(8192);
                    SocketChannel client = queue[id].take();
                    //因为worker线程也监听了9090端口，所以可以监听到连接到9090端口的读写事件
                    client.register(selector,SelectionKey.OP_READ,buffer);
                    System.out.println("---------------------------");
                    System.out.println("新客户端：" + client.socket().getPort()+"分配到worker:"+(id));
                    System.out.println("---------------------------");
                }

            }
        }catch (IOException e){

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param key
     *
     * key包含了Server(ServerSocketChannel)，selector
     * 所以直接可以从key中获取server
     *
     * boss将客户端需要监听的事件注册到worker
     *
     */
    public void acceptHandler(SelectionKey key){
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            //代表了一个新的客户端到达
            SocketChannel client = ssc.accept();
            //将新客户端设置为非阻塞
            client.configureBlocking(false);
            //均匀将监听客户端到达事件分配到不同的woker上去
            int num = idx.getAndDecrement() % SELECTOR_NUM;
            queue[num].add(client);
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
}

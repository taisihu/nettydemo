package com.example.demo.netty.thread.basic_thread;

public class WatiNotifyThreadTest {

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            Task task = new Task();
            task.start();
        }

    }

}

class Task extends Thread{

    public static Object lock = new Object();

    @Override
    public void run() {
        synchronized (lock){
            try {
                System.out.println("currentThread--->"+Thread.currentThread().getName());
                Thread.currentThread().wait();
//                lock.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

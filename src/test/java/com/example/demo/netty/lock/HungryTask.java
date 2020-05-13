package com.example.demo.netty.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程饥饿
 */
public class HungryTask {

    //非公平锁
//    static Lock lock = new ReentrantLock();
    //公平锁
    static Lock lock = new ReentrantLock(true);

    static class Task extends Thread{

        int count = 0;

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()){
                lock.lock(); //没拿到锁，会等待拿到锁的线程释放锁，然后再去抢锁
                count++;
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }finally {
                    lock.unlock();
                }

            }

        }
    }

    public static void main(String[] args) {

        Task[] threads = new Task[5];
        for (int i = 0; i < 5; i++) {
            Task thread =  new Task();
            thread.start();
            threads[i] = thread;
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Task task : threads){
            System.out.println(task.getName() +">>>"+task.count);
        }

        System.exit(0);

    }

}

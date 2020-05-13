package com.example.demo.netty.lock.mylock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class MyLockTest {

    static MyLock myLock = new MyLock(true);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(()->{
                System.out.println(Thread.currentThread()+"准备抢锁");
                myLock.lock();
                try{
                    System.out.println(Thread.currentThread()+"抢到锁");
                    TimeUnit.SECONDS.sleep(3);//模拟业务处理场景
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    myLock.unlock();
                }
            });
            thread.start();
        }
        TimeUnit.SECONDS.sleep(6);
    }

}

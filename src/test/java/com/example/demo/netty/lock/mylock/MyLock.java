package com.example.demo.netty.lock.mylock;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * 手动实现一个锁
 * 公平和非公平的区别:
 * 公平锁：抢锁前判断等待队列中是否有其他线程在等待
 * 非公平锁：不判断等待队列中是否有其他线程在等待
 *
 */
public class MyLock implements Lock {

    boolean isFairLock = false;

    //锁状态，0-未占用，1-已占用
    AtomicInteger state = new AtomicInteger(0);

    LinkedBlockingDeque<Thread> threadQueue =  new LinkedBlockingDeque<Thread>();

    Thread ownThread = null;

    public MyLock(boolean isFairLock) {
        this.isFairLock = isFairLock;
    }

    //尝试获取锁，没有获取就等待
    @Override
    public void lock() {
        if(isFairLock){
            fairLock();
        }else {
            unFairLock();
        }
    }

    /**
     * 公平锁实现
     */
    public void fairLock(){
        if(threadQueue.isEmpty() && !tryLock()){ //队列为空才可以去抢锁
            //如果不成功则将当前线程添加到一个等待队列中
            threadQueue.add(Thread.currentThread());
            while (true){
                if((Thread.currentThread() == threadQueue.peek()) && tryLock()){ //判断自己在队列头部的时候才有资格去抢锁
                    threadQueue.poll();
                    return;
                }else {
                    LockSupport.park(); //让当前运行的线程停止运行--阻塞
                }
            }
        }
    }

    /**
     * 非公平锁实现
     */
    public void unFairLock(){
        if(!tryLock()){ //如果被唤醒，并且有另一个线程到来，则会出现锁竞争，是非公平锁
            //如果不成功则将当前线程添加到一个等待队列中
            threadQueue.add(Thread.currentThread());
            while (true){
                if(tryLock()){
                    threadQueue.poll();
                    return;
                }else {
                    LockSupport.park(); //让当前运行的线程停止运行--阻塞
                }
            }
        }
    }

    //尝试获取锁，如果没有抢到，则直接返回，不等待
    @Override
    public boolean tryLock() {

        if(state.get()==0){
            //加锁操作，通过修改变量状态从0到1表示加锁成功
            state.compareAndSet(0,1);//CAS native方法，硬件底层原语，保证原子操作
            ownThread = Thread.currentThread();
            return true;
        }else if(Thread.currentThread() == ownThread){ //可重入实现
            state.incrementAndGet();
            return true;
        }

        return false;
    }

    //释放锁
    @Override
    public void unlock() {

        //如果释放的不是当前的线程加的锁，则抛异常
        if(Thread.currentThread() != ownThread){
            throw new RuntimeException("不是你的锁不要动!!!");
        }
        //先将其他占用锁的状态清空,通知其他线程去抢锁
        if(state.decrementAndGet() == 0){ //unlock将state减1，直到state为0时，释放资源
            ownThread = null;
            Thread nextThread = threadQueue.peek();//获取队列第一个线程
            LockSupport.unpark(nextThread);//唤醒下一个线程，去获取锁
        }


    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public Condition newCondition() {
        return null;
    }
}

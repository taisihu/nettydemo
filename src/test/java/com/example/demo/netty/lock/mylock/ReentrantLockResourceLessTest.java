package com.example.demo.netty.lock.mylock;

public class MylockReenterTest {

    static MyLock myLock = new MyLock(false);

    public static void main(String[] args) {

        System.out.println("===================lock=========================");
        System.out.println("ready to lock...,state="+myLock.state.get());
        myLock.lock();
        System.out.println("first lock...,state="+myLock.state.get());
        myLock.lock();
        System.out.println("second lock...,state="+myLock.state.get());


        System.out.println("===================unlock=========================");
        System.out.println("first unlock...,state="+myLock.state.get());
        myLock.unlock();
        System.out.println("second unlock...，state="+myLock.state.get());
        myLock.unlock();
        System.out.println("finish unlock...,state="+myLock.state.get());
    }

}

package com.example.demo.netty.thread;

import java.util.concurrent.TimeUnit;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-11-21 16:03
 **/
public class ThreadPoolTest {


    public static void main(String[] args) {

        int coreCount = Runtime.getRuntime().availableProcessors();

        System.out.println("cpu核数："+coreCount);

        for (int i = 0; i < 8; i++) {
            PermanentThread permanentThread = new PermanentThread("永久线程"+i);
            ExecuteUtil.execute(permanentThread);
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 20; i++) {
            TempThread tempThread = new TempThread("临时线程"+i);
            ExecuteUtil.execute(tempThread);
        }

    }


}

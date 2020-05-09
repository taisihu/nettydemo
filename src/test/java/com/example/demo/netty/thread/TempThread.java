package com.example.demo.netty.thread;

import com.example.demo.netty.utils.DateTimeUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-11-21 16:12
 **/
public class TempThread implements Runnable {

    private String threadName;

    public TempThread(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void run() {

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String dataStr = DateTimeUtil.timeStamp2Date(System.currentTimeMillis(), DateTimeUtil.DATA_FORMAT_YYYYMMDDHHMMSS);

        System.out.println("==========="+threadName+",当前时间"+dataStr);

    }

}

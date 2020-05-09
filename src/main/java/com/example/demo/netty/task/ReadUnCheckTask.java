package com.example.demo.netty.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-11-21 18:40
 **/
@Component
public class ReadUnCheckTask {

    public void read(){

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("读取队列数据...");

    }

}

package com.example.demo.netty.starter;

import com.example.demo.netty.task.ReadUnCheckTask;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-11-21 18:42
 **/
public class ReaderStarter implements InitializingBean {

    @Autowired
    ReadUnCheckTask readUnCheckTask;

    @Override
    public void afterPropertiesSet() throws Exception {

        readUnCheckTask.read();

    }
}

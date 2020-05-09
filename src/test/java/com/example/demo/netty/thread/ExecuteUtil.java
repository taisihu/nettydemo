package com.example.demo.netty.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-25 16:31
 **/
public class ExecuteUtil {

    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(CORE_COUNT * 2, CORE_COUNT * 2, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(CORE_COUNT * 20));

    /**
     * 校验清理工作对象延迟队列
     */
    public static void execute(Runnable command) {
        EXECUTOR.execute(command);
    }

}

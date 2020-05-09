package com.example.demo.netty.redis;

import com.example.demo.netty.utils.RedisHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-16 19:46
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    RedisHelper redisHelper;

    @Test
    public void test(){
        redisHelper.set("key20191017","value20191017");
        System.out.println("===========");
        System.out.println(redisHelper.get("key20191017"));
    }

}

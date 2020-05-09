package com.example.demo.netty.redis;

import com.example.demo.netty.core.NameTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-16 19:55
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class NameTemplateTest {

    @Autowired
    NameTemplate nameTemplate;

    @Test
    public void test(){

        System.out.println(nameTemplate.getName());

    }

}

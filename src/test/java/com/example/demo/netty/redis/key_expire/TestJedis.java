package com.example.demo.netty.redis.key_expire;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-30 17:30
 **/
public class TestJedis {

    public static void main(String[] args) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(jedisPoolConfig, "192.168.100.166",6379,60,"root");
        Jedis jedis = pool.getResource();
        jedis.set("567", "你还在吗");
        jedis.expire("567", 10);

    }
}
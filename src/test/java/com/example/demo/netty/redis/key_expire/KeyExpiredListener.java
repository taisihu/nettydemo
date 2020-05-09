package com.example.demo.netty.redis.key_expire;

import redis.clients.jedis.JedisPubSub;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-30 17:28
 **/
public class KeyExpiredListener extends JedisPubSub {

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("onPSubscribe "
                + pattern + " " + subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

        System.out.println("onPMessage pattern "
                + pattern + " " + channel + " " + message);
    }



}
package com.example.demo.netty.config;

import com.example.demo.netty.NettyApplication;
import com.example.demo.netty.utils.RedisMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-30 18:42
 **/
@Configuration
@Import( value = NettyApplication.class )
public class PubsubConfiguration {

    @Autowired
    private RedisMessageListener redisMessageListener;

    @Bean
    public ChannelTopic expiredTopic() {
        return new ChannelTopic("__keyevent@0__:expired");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Autowired RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(redisMessageListener, expiredTopic());
        return redisMessageListenerContainer;
    }


}

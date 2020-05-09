package com.example.demo.netty.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-10-17 10:31
 **/
public class MyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("================");
        System.out.println(msg.toString());

    }
}

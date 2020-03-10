package com.yinxi.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class MyHartBeatHadler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){

            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            String idea=null;
            switch (idleStateEvent.state()){
                case ALL_IDLE:
                    idea="读写空闲";
                    break;
                case READER_IDLE:
                    idea="读空闲";
                    break;
                case WRITER_IDLE:
                    idea="写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress()+"--服务器空闲--"+idea);
        }
    }
}

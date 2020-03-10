package com.yinxi.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyHeartBeatServer {

    public static void main(String[] args) {

        new MyHeartBeatServer().initHeartBeat();
    }

    public  void initHeartBeat() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();


        try {
            ServerBootstrap bootstrapServer = new ServerBootstrap();
            bootstrapServer.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            channelPipeline.addLast(new MyHartBeatHadler());
                        }
                    });
            try {
                ChannelFuture channelFuture = bootstrapServer.bind(5688).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}

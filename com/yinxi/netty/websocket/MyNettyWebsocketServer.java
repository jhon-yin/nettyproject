package com.yinxi.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class MyNettyWebsocketServer {
    private  final int PORT;

    public MyNettyWebsocketServer(int port) {
        this.PORT = port;
    }

    public static void main(String[] args) {

        new MyNettyWebsocketServer(5689).initNettyWebServer();
    }

    public void initNettyWebServer(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            // 基于http 协议,使用http的编码和解码器
                            channelPipeline.addLast(new HttpServerCodec());
                            // 以块方式写
                            channelPipeline.addLast(new ChunkedWriteHandler());
                            channelPipeline.addLast(new HttpObjectAggregator(8196));
                            channelPipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            channelPipeline.addLast(new MyTextWebsocketFramHandler());

                        }
                    });
            System.out.println("server is ok...");
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

}

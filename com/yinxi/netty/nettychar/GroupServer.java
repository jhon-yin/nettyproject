package com.yinxi.netty.nettychar;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class GroupServer {

    private int PORT = 8858;

    public static void main(String[] args) {
        GroupServer groupServer = new GroupServer(5688);
        groupServer.groupServer();
    }

    public GroupServer(int port){
        this.PORT=port;
    }

    public void groupServer(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline channelPipeline = ch.pipeline();
                        // 像pipline 中加入解码器
                        channelPipeline.addLast("decoder", new StringDecoder());
                        // 像pipline 中加入编码器
                        channelPipeline.addLast("encoder", new StringEncoder());
                        // 加入自己的业务 Hanlder
                        channelPipeline.addLast(new GroupServerChartHander());// 像通道中添加一个注册器
                    }
                });

            System.out.println("neety server is ok...");
        // 绑定端口

            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}

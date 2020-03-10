package com.yinxi.netty.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

public class NettyServer {
    public static void main(String[] args) {

        /**
         * 创建 bossGroup 和 workGroup
         * 说明：
         * 1.创建两个现成组 bossGroup 和 workGroup
         * 2.bossGroup 只是处理连接请求，真正的和客户端业务处理都交给workGroup完成
         * 3.两个都是无限循环
         * 4.bossGroup 和 workGroup 含有的子线程（EventLoopGroup） 的个数默认
         *   实际CPU 核数 * 2
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 创建服务器启动对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workGroup)// 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128)// BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {// 创建一个通道

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast("decoder",new ProtobufDecoder(null));
                            channelPipeline.addLast(new NettyServerHandller());
                        }
                    });
            System.out.println("服务器 is ready...");
            // 绑定一个端口并且同步，生成一个 ChannelFuture 对象
            // 启动服务器并（绑定对象）
            try {
                ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
                // 对关闭通道进行监听
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally {
            // 优雅的关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}

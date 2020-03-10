package com.yinxi.netty.nettychar;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;


public class GroupClient {

    private final int PORT;
    private final String HOST;
    public static void main(String[] args) {
        GroupClient groupClient = new GroupClient(5688,"127.0.0.1");
        groupClient.initClient();
    }

    public GroupClient(int port,String host) {
        this.HOST=host;
        this.PORT=port;
    }

    public void initClient(){

        EventLoopGroup bossGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast("encoder",new StringEncoder());
                            channelPipeline.addLast("decoder",new StringDecoder());
                            channelPipeline.addLast(new ClientChartHandler());
                        }
                    });

            try {
                ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
                Channel channel = channelFuture.channel();
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()){
                    String msg = scanner.nextLine();
                    channel.writeAndFlush(msg+"\r\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {
            bossGroup.shutdownGracefully();
        }

    }

}

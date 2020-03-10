package com.yinxi.netty.nettychar;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class GroupServerChartHander extends SimpleChannelInboundHandler<String> {

     // GlobalEventExecutor.INSTANCE 是全局的事件执行器
     private static ChannelGroup channelGroup= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * handlerAdded 表示连接建立， 一旦连接建立第一个被执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"加入聊天");
        channelGroup.add(channel);// 将channel 加入当前组中
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"离开聊天室");
    }


    // 当 channel 读的时候触发
    // 读取客户端发送过来的数据并转发。。。
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();
        channelGroup.forEach(c -> {
            if(c != channel){

                c.writeAndFlush("[客户]"+c.remoteAddress()+"发送了"+msg+"消息\n");
            }else{

                c.writeAndFlush("[自己]发送了"+msg+"消息\n");
            }
        });
    }


    // 当channel 处于活动状态时 触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"上线了。。。");
    }

    // 当 channel 处于不活动的时候 触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"离线了。。。");
    }

    // 当 channel 读完毕的时候 触发
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ctx.channel().writeAndFlush("消息已收到");
        System.out.println(channel.remoteAddress()+"内容读取完成。。。");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


}

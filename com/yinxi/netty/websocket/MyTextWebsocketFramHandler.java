package com.yinxi.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

public class MyTextWebsocketFramHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("[服务器]收到消息"+msg.text());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("[服务器时间]"+ LocalDateTime.now()+":"+msg.text()));
    }


    // 当websocket 连接后触发
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        // id 表示唯一值 asLongText 是唯一的 asShortText不是
        System.out.println("websocket 连接 handlerAdded 被调用"+ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("websocket 关闭 handlerRemoved 被调用"+ctx.channel().remoteAddress());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

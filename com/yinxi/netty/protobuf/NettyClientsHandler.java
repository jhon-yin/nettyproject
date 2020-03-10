package com.yinxi.netty.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientsHandler extends ChannelInboundHandlerAdapter {
    // 当通道就绪后就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道已就绪："+ctx.channel().isActive());
        ctx.writeAndFlush(Unpooled.copiedBuffer("通道已就绪。。Hello Server ", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("【服务器】回复消息："+byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
        try {
            super.exceptionCaught(ctx, cause);
        } catch (Exception e) {
            ctx.close();
            e.printStackTrace();
        }
    }
}

package com.yinxi.nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientNio {

    @Test
    public void client(){
        try {
            // 创建socketChannel
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
            if(!socketChannel.connect(inetSocketAddress)){

                while (!socketChannel.finishConnect()){

                    System.out.println("等待客户端的连接。。。");
                }
            }

            String str="我和我的祖国。。";
            ByteBuffer wrap = ByteBuffer.wrap(str.getBytes());// wrap 包裹
            int write = socketChannel.write(wrap);
            if(write != -1){
                System.out.println("成功发送{"+write+"}条数据");
            }
        System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

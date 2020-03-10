package com.yinxi.nio;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ServerNio {

    @Test
    public void nioServer(){

        try {
            // 创建serverSocketChannel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 获取Selector
            Selector selector = Selector.open();
            // 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(6666));
            // 设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 把serverSocketChannel 注册到 Selector 中
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);// OP_ACCEPT : 用于套接字接受操作的 位集
            while (true){
                if(selector.select(1000) == 0){
                    System.out.println("等待1秒后，还没有客户端连接，继续等待。。。");
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                while (selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    // 获取到集合后判断是否连接
                    if(selectionKey.isAcceptable()){// 如果还没有连接
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ,ByteBuffer.allocate(512));
                        System.out.println("客户端连接成功生成一个socketChannel:"+socketChannel.hashCode());
                    }
                    if(selectionKey.isReadable()){// 如果是读操作
                        // 首先要从当前的selectKey 中获取一个channel
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
                        int read = channel.read(byteBuffer);
                        if(read != -1){
                            System.out.println(new String(byteBuffer.array()));
                        }
                    }
                    selectionKeyIterator.remove();

                    // Object key = selectionKey.attachment();// 获取当前附加的对象
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

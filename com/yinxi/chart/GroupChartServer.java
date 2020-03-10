package com.yinxi.chart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChartServer {

    private ServerSocketChannel listingSocketChannel;
    private Selector selector;
    private static final int PORT = 6667;


    public static void main(String[] args) {
        GroupChartServer groupChartServer = new GroupChartServer();
        groupChartServer.listen();

    }

    public GroupChartServer(){

        try {
            // 创建ServerSocketChannel
            listingSocketChannel = ServerSocketChannel.open();
            // 获取Selector
             selector = Selector.open();
             // 绑定端口
            listingSocketChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置为非阻塞模式
            listingSocketChannel.configureBlocking(false);
            // 将ServerSocketChannel 注册到 Selector 中
            listingSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen(){
        while (true){
            try {
                int selectCount = selector.select(1000);
                if(selectCount > 0){// 说明已连接
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        if(key.isAcceptable()){// key 的通道是否已准备好接受新的套接字连接 是 true 否 false
                            SocketChannel socketChannel = listingSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            ByteBuffer attachmentByteBuffer = (ByteBuffer) key.attachment();
                            socketChannel.register(selector,SelectionKey.OP_READ,attachmentByteBuffer);
                            System.out.println(socketChannel.getRemoteAddress()+":上线了。。。");
                        }
                        if(key.isReadable()){// 如果是读，将数据读到缓冲区，输出，并转发给其余通道
                            
                            readData(key);
                        }
                        iterator.remove();
                    }
                }else{// 未连接正在等待。。。
                   // System.out.println("超时1秒客户端未连接。。。");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeData(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
    }

    private void readData(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        try {
            int read = channel.read(byteBuffer);
            if(read != -1){// 如果不等于 -1 说明读到数据
                String msg= new String(byteBuffer.array());
                System.out.println("消息 form 客户端-->："+msg);
                // 读到数据后打印 然后转发 。。。
                transpond(msg,channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                System.out.println(channel.getRemoteAddress()+":离线了。。。");
                key.cancel();
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void transpond(String msg, SocketChannel self) {
        Iterator<SelectionKey> iterator = selector.keys().iterator();
        while (iterator.hasNext()){
            SelectionKey next = iterator.next();
            Channel taggetChannel = next.channel();
            if(taggetChannel instanceof SocketChannel && taggetChannel != self){

                SocketChannel channel = (SocketChannel) taggetChannel;
                ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
                try {
                    channel.write(wrap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

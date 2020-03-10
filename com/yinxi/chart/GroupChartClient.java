package com.yinxi.chart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class GroupChartClient {

    private final String HOST = "127.0.0.1";
    private static final int PORT = 6667;

    private SocketChannel socketChannel;
    private Selector selector;
    private String username;

    public static void main(String[] args) {

        final GroupChartClient groupChartClient = new GroupChartClient();
      //  groupChartClient.listenFlag();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                groupChartClient.readServerInfo();
            }
        },0,1000);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            groupChartClient.sendToServer(s);
        }
    }

    public GroupChartClient(){
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);
            socketChannel =SocketChannel.open(inetSocketAddress);
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            username = socketChannel.getLocalAddress().toString().substring(1);

            System.out.println(username +" is ok ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器发送消息
     * @param msg
     */
    private void sendToServer(String msg){
        try {
            msg = username +" 说: "+ msg;
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取服务端返回的消息
     *
     */
    private void readServerInfo(){
        try {
            int i = selector.select(1000);
            if(i > 0){

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){

                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int read = channel.read(byteBuffer);
                        if(read > -1){
                            System.out.println(new String(byteBuffer.array()));
                        }

                    }
                    iterator.remove();
                }
            }else{

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

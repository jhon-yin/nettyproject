package com.yinxi;


import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class file {

    @Test
   public void scateringAndGatteringTest(){
        ServerSocketChannel serverSocketChannel= null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
            serverSocketChannel.socket().bind(inetSocketAddress);
            ByteBuffer[] byteBuffer = new ByteBuffer[2];
            byteBuffer[0] = ByteBuffer.allocate(5);
            byteBuffer[1] = ByteBuffer.allocate(3);
            // 等待连接客户端
            SocketChannel accept = serverSocketChannel.accept();
            int messageLength =  8;
            while (true){
                int byteRead = 0;
                    while (byteRead < messageLength){
                        long read = accept.read(byteBuffer);
                        if(read != -1){
                            byteRead+=read;
                            System.out.println("byteRead:"+byteRead);
                            Arrays.asList(byteBuffer).stream().map((byteBuffers) -> "position:"+byteBuffers.position()
                            +"limit:"+byteBuffers.limit()
                            ).forEach(System.out::println);
                            Arrays.asList(byteBuffer).stream().map(b->new String(b.array())).forEach(System.out::println);
                        }
                    }

                System.out.println("----------------------");
                Arrays.asList(byteBuffer).stream().forEach((byteBuffer1)->byteBuffer1.flip());


                long byteWrite = 0;
                while (byteWrite < messageLength){
                    long l = accept.write(byteBuffer);
                    byteWrite+=l;
                }

                Arrays.asList(byteBuffer).stream().forEach((byteBuffer1)->byteBuffer1.clear());
                System.out.println("byteRead:"+byteRead+"byteWrite:"+byteWrite+"messageLength:"+messageLength);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Test
    public void transFerForm(){
        // 定义输入流管道
        // 定于输出流管道

        try {
            FileInputStream fileInputStream = new FileInputStream("d:\\a.JPG");
            FileOutputStream fileOutputStream = new FileOutputStream("d:\\a1.JPG");

            // 获得输入流channel
            // 获得输出流 channel
            FileChannel fileInputStreamChannel = fileInputStream.getChannel();
            FileChannel fileOutputStreamChannel = fileOutputStream.getChannel();
            // 将文件放入到 transFerForm 中
           long rlong= fileOutputStreamChannel.transferFrom(fileInputStreamChannel,0,fileInputStreamChannel.size());
           if (rlong > 0){
               System.out.println("数据拷贝完成，+size:"+ rlong);
           }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Test
    public  void textCopyTextByBufferTest(){
        FileInputStream fileInputStream =null;
        FileOutputStream fileOutputStream = null;
        try {
             fileInputStream = new FileInputStream("e:\\sql.txt");
            FileChannel fileInputStreamChannel = fileInputStream.getChannel();
             fileOutputStream = new FileOutputStream("e:\\sql1.txt");
            FileChannel fileOutputStreamChannel = fileOutputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(5);

                while (true){
                    byteBuffer.clear();
                    int read = fileInputStreamChannel.read(byteBuffer);
                    if(read == -1){
                         break;
                    }
                    byteBuffer.flip();
                    fileOutputStreamChannel.write(byteBuffer);
                }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

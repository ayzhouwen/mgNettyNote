package com.test;

import cn.hutool.core.date.DateUtil;
import com.my_proto.client.ClientHandler;
import com.my_proto.client.MyProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


//测试客户端tcp连接池,参考<<netty进阶之路>>
public class ClientTcpPool {
    static final String HOST = "192.168.204.129";
    static final int PORT = 9999;
    static final int SIZE = 256;
    static final int PoolSize=10000;

    public static void main(String[] args) throws Exception {
        CreatePool2();

    }

        // Configure the client.

    //错误的创建tcp连接池,原因会创建很多的线程,PoolSize个线程
    public static void CreatePool1(){
            for (int i=0;i<PoolSize;i++){
                EventLoopGroup group=new NioEventLoopGroup();
                Bootstrap b=new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new MyProtocolEncoder());
                                ch.pipeline().addLast(new ClientHandler());
                            }
                        });

                ChannelFuture future = null;
                try {
                    future = b.connect(HOST, PORT).sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
                future.channel().closeFuture().addListener((r)->group.shutdownGracefully());
            }
    }


    //正确的创建tcp连接池的方法,这样可以避免创建非常多的线程,目前只创建一个线程池线程,本机默认的是8个线程
    public static void CreatePool2(){
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap b=new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MyProtocolEncoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

        long time=System.currentTimeMillis();
        for (int i=0;i<PoolSize;i++){
            ChannelFuture future = null;
            int portarr[]={9999,9999,9999}; //改变数组值实现连接多个服务端
            try {
                System.out.println(i%3);
                future = b.connect(HOST, portarr[i%3]).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
            future.channel().closeFuture().addListener((r)->group.shutdownGracefully());
        }

        System.out.println("连接执行时间:"+DateUtil.spendMs(time));
    }
}

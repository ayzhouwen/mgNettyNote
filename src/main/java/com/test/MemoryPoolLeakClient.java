package com.test;

import cn.hutool.core.date.DateUtil;
import com.my_proto.client.ClientHandler;
import com.my_proto.client.MyProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//测试客户端内存池泄露,参考<<netty进阶之路>>
public class MemoryPoolLeakClient  {
    static final String HOST = "127.0.0.1";
    static final int PORT = 9999;
    static final int SIZE = 256;
    static final int PoolSize=10000;
    static final byte[] sendbytearr=new byte[1024*1024];//1m

    static {

        for (int i=0;i<sendbytearr.length;i++){
            sendbytearr[i]=Byte.valueOf(i%10+"");
        }
    }
    public static void main(String[] args) {
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap b=new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                for (int i=0;i<1024;i++){
                                    ctx.writeAndFlush(sendbytearr);
                                }
                            }
                        });
                    }
                });

        long time=System.currentTimeMillis();
        ChannelFuture future = null;
        try {
            future = b.connect(HOST, PORT).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
        future.channel().closeFuture().addListener((r)->group.shutdownGracefully());

        System.out.println("连接执行时间:"+DateUtil.spendMs(time));
    }
}

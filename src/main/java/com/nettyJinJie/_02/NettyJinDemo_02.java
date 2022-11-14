package com.nettyJinJie._02;

import com.goExplore_05_1.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;


//2.1.4 客户端
//案例1通过阻塞方式绑定监听端口,启动服务端之后，没发生任何异常,程序退出，代码示例如下:
// connect连接1000次,不会出现1000个线程,真实的结果是把线程池给占满(默认cpu合数*2),
@Slf4j
public class NettyJinDemo_02 {
    public static void main(String[] args) {
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap b=new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                        log.info(msg);
                    }
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        log.info("连上服务器了,id:{}",ctx.channel().id());
                    }
                });

            }


        });

        //如果i等于1000,此处不会出现1000个线程,真实的结果是把线程池给占满(默认cpu合数*2),
        for (int i = 0; i < 1000; i++) {
            try {
                int finalI = i;
                b.connect("localhost",18080).channel().closeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {

                        log.info(future.channel().toString()+" 连接关闭!!!序号:{},ID:{}", finalI,future.channel().id());
                    }
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }





    }
}

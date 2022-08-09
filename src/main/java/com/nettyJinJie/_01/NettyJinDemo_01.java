package com.nettyJinJie._01;

import com.nettyJinJie.common.ByteBufRecvHanldlerA;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

//1.1 Netty服务端意外退出问题
//案例1通过阻塞方式绑定监听端口,启动服务端之后，没发生任何异常,程序退出，代码示例如下:
@Slf4j
public class NettyJinDemo_01 {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {

            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p=ch.pipeline();
                            p.addLast(new ByteBufRecvHanldlerA());
                            //增加A再增加B,但是B收不到消息,SimpleChannelInboundHandler
//                            p.addLast(new RecvHanldlerB());
//                            p.addLast(new TestHandlerA());
//                            p.addLast(new TestHandlerB());
                        }
                    });

         ChannelFuture f=   b.bind(18080).sync();//用同步方式绑定服务监听端口
            f.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    //业务逻辑代码 ....
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    log.info(future.channel().toString()+" 链路关闭");
                }
            });
        } finally {
            //不能在此加入关闭功能代码,否则程序会退出
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
        }


    }
}

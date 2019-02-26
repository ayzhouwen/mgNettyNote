package com.test;

import com.my_proto.client.ClientHandler;
import com.my_proto.client.MyProtocolEncoder;
import com.my_proto.server.MyProtocolDecoder;
import com.my_proto.server.Server;
import com.my_proto.server.ServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

//测试服务,来执行<<netty进阶之路的片段代码>>
public class TestServer {
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;  //最大长度
    private static final int LENGTH_FIELD_LENGTH = 4;  //长度字段所占的字节数
    private static final int LENGTH_FIELD_OFFSET = 2;  //长度偏移
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;
    private int port;

    public TestServer(int port){
        this.port=port;
    }


    public void start() throws InterruptedException {
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap sbs=new ServerBootstrap().group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MemoryPoolLeakHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 绑定端口，开始接收进来的连接
        ChannelFuture future = sbs.bind(port).sync();

        System.out.println("Server start listen at " + port );
        //future.channel().closeFuture().sync(); //main线程同步阻塞等待



        //main线程不阻塞继续执行模式
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                System.out.println(future.channel().toString()+"链路关闭");
            }
        });

        System.out.println("main线程执行完毕");
    }

    public static void main(String[] args) throws InterruptedException {
        Server server=new Server(9999);
        server.start();

    }
}

package com.my_proto.server;

import cn.hutool.core.thread.ThreadUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Server {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;  //最大长度
    private static final int LENGTH_FIELD_LENGTH = 4;  //长度字段所占的字节数
    private static final int LENGTH_FIELD_OFFSET = 2;  //长度偏移
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;
    private int port;

    public Server(int port){
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
                        ch.pipeline().addLast(new MyProtocolDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
                        ch.pipeline().addLast(new ServerHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        // 绑定端口，开始接收进来的连接
        ChannelFuture future = sbs.bind(port).sync();

        System.out.println("Server start listen at " + port );
        future.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        Server server=new Server(9999);
        server.start();

    }
}

package com.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;

/**
 * 多网卡测试,这是chatgpt 写的代码，由于没有设备暂时没测试
 */
public class MultipleNetworkCards {
    public static void main(String[] args) throws Exception {
        // 创建主线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建工作线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 获取指定网卡的NetworkInterface对象
            NetworkInterface networkInterface = NetworkInterface.getByName("eth0");

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8080))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 添加处理逻辑
                        }
                    })
                    // 设置SO_BINDTODEVICE选项，绑定指定的网卡
                    .childOption(ChannelOption.IP_MULTICAST_IF, networkInterface)
                    .bind()
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}

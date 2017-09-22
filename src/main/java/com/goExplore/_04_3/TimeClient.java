package com.goExplore._04_3;

import java.awt.Event;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
//客户端也是用编码器,来解决粘包问题
public class TimeClient {
		public void connect(int port,String host) throws Exception {
			//配置客户端NIO线程组
			EventLoopGroup group=new NioEventLoopGroup();
			try {
				Bootstrap b=new Bootstrap();
				b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new TimeClientHandler());
					}
					
				});
				
				//发起异步连接操作
				ChannelFuture f=b.connect(host, port).sync();
				//等待客户端链路关闭
				f.channel().closeFuture().sync();
			} catch (Exception e) {
				// TODO: handle exception
			}finally {
				//优雅退出,释放nio线程组
				group.shutdownGracefully();
			}
		}
		
		public static void main(String[] args) throws Exception {
			int port=1111;
			new TimeClient().connect(port, "127.0.0.1");
			//new TimeClient().connect(port, "192.168.0.130");
		}
}

package com.goExplore._04_2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {
		public void bind(int port) throws Exception{
			//配置服务端的nio线程组
			EventLoopGroup bossGroup=new NioEventLoopGroup();
			EventLoopGroup workerGroup=new NioEventLoopGroup();
			try {
				ServerBootstrap b=new ServerBootstrap();
				b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new  ChildChannelHandler()  );
				//绑定端口
				ChannelFuture f=b.bind(port).sync();
				//等待服务端监听端口关闭
				f.channel().closeFuture().sync();
				
			} finally {
				//优雅退出,释放线程池资源
				bossGroup.shutdownGracefully();
				workerGroup.shutdownGracefully();
				
			}
		}
		
		public static void main(String[] args) {
			int port=1111;
			try {
				new TimeServer().bind(port);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}



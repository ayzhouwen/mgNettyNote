package com.goExplore_05_1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
//DelimiterBasedFrameDecoder编码器解决粘包问题
public class EchoClient {
	public void connect(int port,String host) throws Exception{
		//配置客户端nio线程组
		EventLoopGroup group =new NioEventLoopGroup();
		try {
			Bootstrap b=new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ByteBuf delimiter=Unpooled.copiedBuffer("$_".getBytes());
					ch.pipeline().addLast(new  DelimiterBasedFrameDecoder(1024, delimiter));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new EchoClientHandler() );
					
				}
			});
			//发起异步连接操作
			ChannelFuture f=b.connect(host,port).sync();
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
		} catch (Exception e) {
				//优雅退出,释放nio线程组
			group.shutdownGracefully();
		}
	
	}
	
	public static void main(String[] args) throws Exception {
		int port=10880;
		new EchoClient().connect(port, "127.0.0.1");
	}
}

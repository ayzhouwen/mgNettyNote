package com.goExplore_07_2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

//DelimiterBasedFrameDecoder编码器解决粘包问题
public class EachoServer {
	//配置服务端nio线程组
	public void bind(int port) throws Exception{
		EventLoopGroup bossGroup=new NioEventLoopGroup();
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try{
			ServerBootstrap b=new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
						//ByteBuf delimiter=Unpooled.copiedBuffer("$_".getBytes());
						//ch.pipeline().addLast(new  DelimiterBasedFrameDecoder(1024, delimiter));
						//ch.pipeline().addLast(new StringDecoder());

			       ChannelPipeline pipeline = ch.pipeline();  
			       pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));
				   pipeline.addLast("msgpack decoder",new MsgpackDecoder());   
				   pipeline.addLast("frameEncoder",new LengthFieldPrepender(2));
                   pipeline.addLast("msgpack encoder",new MsgpackEncoder());  
					ch.pipeline().addLast(new EchoServerHandler() );
				}
			});
			//绑定端口
			ChannelFuture f=b.bind(port).sync();
			//等待服务端口监听端口关闭
			f.channel().closeFuture().sync();
			;
		}finally{
			//优雅退出,释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		int port=3312;
		try {
			new EachoServer().bind(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

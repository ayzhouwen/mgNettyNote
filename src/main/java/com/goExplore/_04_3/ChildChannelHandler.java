package com.goExplore._04_3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
//利用LineBasedFrameDecoder 解决TCP粘包问题
public class ChildChannelHandler  extends ChannelInitializer<SocketChannel>  {
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
		arg0.pipeline().addLast(new StringDecoder());
		arg0.pipeline().addLast(new TimeServerHandler());
		
	}
}

package com.goExplore._04_2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ChildChannelHandler  extends ChannelInitializer<SocketChannel>  {
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		arg0.pipeline().addLast(new TimeServerHandler());
		
	}
}

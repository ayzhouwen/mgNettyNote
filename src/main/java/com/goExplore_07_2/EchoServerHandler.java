package com.goExplore_07_2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoServerHandler  extends SimpleChannelInboundHandler {
	private int counter=0;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("服务器:"+msg);
		ctx.writeAndFlush(msg);
		
	}
	

	
	@Override
	public void  exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
	

}

package com.goExplore_05_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoClientHandler  extends SimpleChannelInboundHandler {
	private int counter;
	private static String ECHO_REQ="Hi,Lilinfeng.Welcome to netty.$_";
	public  EchoClientHandler() {
	
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

		System.out.println("This is"+ ++counter+"来自服务器的响应: ["+msg+"]");
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
			for(int i=0;i<100;i++){
				ctx.writeAndFlush(Unpooled.copiedBuffer(  ECHO_REQ.getBytes()) );
			}
	}
	



}

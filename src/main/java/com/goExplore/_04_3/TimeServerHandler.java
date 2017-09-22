package com.goExplore._04_3;


import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
//使用编码器后接收数据可以直接使用String
public class TimeServerHandler   extends  SimpleChannelInboundHandler {
	private int counter;
	@Override
	public void  channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
		String body=(String)msg;
		System.out.println("the time server receive order :"+body+" ; the counter is :"+ ++counter);
		String currentTime ="QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
		currentTime+=System.getProperty("line.separator");
		ByteBuf resp=Unpooled.copiedBuffer(currentTime.getBytes());
		//ctx.write(resp);
		ctx.writeAndFlush(resp);
		System.out.println("TimeServerHandler_channelRead_线程名:"+Thread.currentThread().getName());
		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		
	} ; 
	@Override
	public  void  channelReadComplete( ChannelHandlerContext ctx)throws Exception{
	ctx.flush();
	}
	
	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		ctx.close();
	}
}

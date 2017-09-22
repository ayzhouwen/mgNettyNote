package com.goExplore._04_2;


import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
//对timeserver改造使其出现tcp粘包问题
public class TimeServerHandler   extends  SimpleChannelInboundHandler {
	private int counter;
	@Override
	public void  channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
		ByteBuf buf=(ByteBuf)msg;
		byte[] req=new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body=new String(req,"UTF-8").substring(0, req.length-System.getProperty("line.separator").length());
		System.out.println("the time server receive order :"+body+" ; the counter is :"+ ++counter);
		String currentTime ="QUERY TIME ORDER".equalsIgnoreCase(body)?new Date(System.currentTimeMillis()).toString():"BAD ORDER";
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

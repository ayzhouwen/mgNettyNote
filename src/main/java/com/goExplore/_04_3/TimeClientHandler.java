package com.goExplore._04_3;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TimeClientHandler extends SimpleChannelInboundHandler {
		private int counter;
		private byte[] req;
		private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());
		//private final ByteBuf firstMessage;
		public TimeClientHandler(){
			 req=("QUERY TIME ORDER"+System.getProperty("line.separator")).getBytes();
			//firstMessage=Unpooled.buffer(req.length);
			 //	firstMessage.writeBytes(req);
			//System.out.println("TimeServerHandler线程名:"+System.currentTimeMillis());
				System.out.println("TimeClientHandler构造函数线程名:"+Thread.currentThread().getName());
		}
	
		@Override
		public void channelActive(ChannelHandlerContext ctx){
			ByteBuf message=null;
			for(int i=0;i<100;i++){
				message=Unpooled.buffer(req.length);
				message.writeBytes(req);
				ctx.writeAndFlush(message);
			}
			
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				String body=(String)msg;
				System.out.println("Now is:"+body+"; the counter is :"+ ++counter);
				System.out.println("TimeClientHandler_channelRead_线程名:"+Thread.currentThread().getName());
		}
		
		@Override
		
		public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
			//释放资源
			logger.warning("Unexpect exception from downstream:"+cause.getMessage());
			ctx.close();
		}
		
		
		
		
}

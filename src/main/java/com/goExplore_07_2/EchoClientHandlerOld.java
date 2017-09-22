package com.goExplore_07_2;

import com.goExplore_06_1_2.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.msgpack.type.ArrayValue;

public class EchoClientHandlerOld  extends SimpleChannelInboundHandler {
	private int counter;
	private int sendNumber=30;
	private static String ECHO_REQ="Hi,Lilinfeng.Welcome to netty.$_";

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

		ArrayValue av=(ArrayValue) msg;
		System.out.println("This is"+ ++counter+"来自服务器的响应: ["+msg+"]"+"name:"+	av.get(0)+",id:"+	av.get(1));
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		
	UserInfo[] infos=  UserInfo();
	for(UserInfo infoe:infos){
		ctx.write(infoe);
		}
	ctx.flush();

	}
	
	private UserInfo [] UserInfo(){
		UserInfo [] userInfos=new UserInfo[sendNumber];
		UserInfo userInfo=null;
		for(int i=0;i<sendNumber;i++){
			userInfo=new UserInfo();
			userInfo.setUserID(i);
			userInfo.setUserName("ABCDEFG--->"+i);	
			userInfos[i]=userInfo;
		}
		return userInfos;
	}
	

	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete");
		//ctx.flush();
		super.channelReadComplete(ctx);
	}
	
	

}

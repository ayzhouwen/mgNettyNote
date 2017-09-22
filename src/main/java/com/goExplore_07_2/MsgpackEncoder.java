package com.goExplore_07_2;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
	try {
		MessagePack messagePack =new MessagePack();
		//System.out.println("encode15");
		byte [] raw=messagePack.write(msg);
		//System.out.println("encode17");
		out.writeBytes(raw);
	//	System.out.println("encode19");
		
	} catch (Exception e) {
			e.printStackTrace();
	}
	
	}

}

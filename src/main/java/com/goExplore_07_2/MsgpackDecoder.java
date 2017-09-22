package com.goExplore_07_2;

import java.nio.ByteBuffer;
import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackDecoder   extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
	//	System.out.println("dncode16");
		try {
			
			final byte[] array;
			final int length=msg.readableBytes();
			array=new byte[length];
			msg.getBytes(msg.readerIndex(), array,0,length);
			MessagePack messagePack=new MessagePack();
			out.add(messagePack.read(array));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

package com.nettyJinJie.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class ByteBufRecvHanldlerA extends SimpleChannelInboundHandler<ByteBuf>{


    public String convertByteBufToString(ByteBuf buf) {
        String str;
        if(buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("RecvHanldlerA-channelRead0");
        log.info("ctx"+ctx);
        String msgStr=convertByteBufToString(msg);
        log.info("msg:"+msgStr);
        //  ctx.writeAndFlush(msgStr);
        ByteBuf byteBuf=PooledByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes("哈哈".getBytes());
        ctx.writeAndFlush(byteBuf);
        //不能直接发送接收到的buf,否则会有IllegalReferenceCountException异常
       // ctx.writeAndFlush(msg);
        //不能直接发送字符串,否则会报 unsupported message type: String (expected: ByteBuf, FileRegion)
       //ctx.writeAndFlush("哈哈");
        //不能直接发送字符串字节数组,否则会报 unsupported message type: String (expected: ByteBuf, FileRegion)
        //ctx.writeAndFlush("哈哈".getBytes());



    }
}

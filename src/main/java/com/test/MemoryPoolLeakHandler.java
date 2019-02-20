package com.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//测试客户端tcp连接池,参考<<netty进阶之路>>
public class MemoryPoolLeakHandler extends ChannelInboundHandlerAdapter {
    static ExecutorService executorService=Executors.newSingleThreadExecutor();
    PooledByteBufAllocator allocator=new PooledByteBufAllocator(false);
    public    void channelRead(ChannelHandlerContext ctx,Object msg){
        ByteBuf reqMsg=(ByteBuf)msg;
        byte [] body=new byte[reqMsg.readByte()];
        executorService.execute(()->{
            //解析请求消息,做路由转发,代码省略
            //转发成功,返回响应给客户端
            ByteBuf respMsg=allocator.heapBuffer(body.length);
            respMsg.writeBytes(body);  //作为示例,简化处理,将请求返回
            ctx.writeAndFlush(respMsg);
        });
    }

}

package com.test.jm;

import cn.hutool.core.date.DateUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * todo 连接断了无法发送，实际生产环境需要做重连
 */
@Slf4j
public class IotJmClientTest {
    static final String HOST = "192.168.1.39";
    /**
     * 发消息测试
     */
    public static void  sendMsg(){

        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap b=new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IotJmTcpDecoder());
                        ch.pipeline().addLast(new IotJmTcpEncoder());
                    }
                });

        long time=System.currentTimeMillis();
        for (int i=0;i<1;i++){
            ChannelFuture future = null;
            int portarr[]={28080,28080,28080}; //改变数组值实现连接多个服务端
            try {
                future = b.connect(HOST, portarr[i%3]).sync();
            } catch (Exception e) {
                log.error("连接异常:",e);
                group.shutdownGracefully();
            }

            future.channel().closeFuture().addListener((r)->group.shutdownGracefully());


            SendMsgRunable sendMsgRunable=new  SendMsgRunable(future.channel());
            for (int j = 0; j <1 ; j++) {
                new Thread(sendMsgRunable).start();
            }

        }



        log.info("连接执行时间:"+ DateUtil.spendMs(time));

    }

    public static void main(String[] args) {
        sendMsg();
    }


}

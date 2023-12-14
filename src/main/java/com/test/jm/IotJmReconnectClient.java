package com.test.jm;

import com.common.thread.MyThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 可以重连的客户端
 */
@Slf4j
public class IotJmReconnectClient {
    private String host;
    private int port;
    private static Bootstrap bootstrap = new Bootstrap();
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static ThreadPoolExecutor sendMsgPoolExecutor= new ThreadPoolExecutor(Math.min(Runtime.getRuntime().availableProcessors()*2,32), 128, 0,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(5000), new MyThreadFactory("sendMsgPool"));

    /**
     * 重连线程池
     */
    private static ScheduledExecutorService scheduledReConnect =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, new MyThreadFactory("scheduledReConnectPool"));

    // 定义一个 AttributeKey，用于唯一标识你要绑定的属性
    public static final AttributeKey<IotJmReconnectClient> iotClient = AttributeKey.valueOf("iotClient");

    public IotJmReconnectClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    static  {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //加入处理器
                        ch.pipeline().addLast(new IotJmTcpDecoder());
                        ch.pipeline().addLast(new IotJmTcpEncoder());
                    }
                });
    }

    public void connect() throws Exception {
        //启动客户端去连接服务器端
        IotJmReconnectClient my=this;
        ChannelFuture cf = bootstrap.connect(host, port);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("开始重连服务器：{}:{}",host,port);
                    //重连交给后端线程执行
                    scheduledReConnect.schedule(() -> {

                        try {
                            connect();
                        } catch (Exception e) {
                            log.error("连接服务器：{}:{}异常",host,port,e);
                        }
                    }, 3000, TimeUnit.MILLISECONDS);
                } else {
                    log.info("连接服务器：{}:{}成功",host,port);
                    cf.channel().attr(iotClient).set(my);
                    SendMsgRunable sendMsgRunable=new  SendMsgRunable(future.channel(),IotJmReconnectClient.this);
                    sendMsgPoolExecutor.execute(sendMsgRunable);
                }
            }
        });
    }

    public static void closeNetty(){
        group.shutdownGracefully();
    }
    public static void main(String[] args) throws Exception {
        for (int i = 0; i <10000 ; i++) {
            IotJmReconnectClient IotJmReconnectClient = new IotJmReconnectClient("192.168.1.39", 28080);
            IotJmReconnectClient.connect();
        }

    }
}

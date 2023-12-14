package com.test.jm;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
class SendMsgRunable implements Runnable{
    private Channel channel;
    private IotJmReconnectClient client;
    /**
     * 所有线程总发送数量
     */
    private AtomicLong sendNum=new AtomicLong(1);
    /**
     * 最大允许发送数量
     */
    private static  final Long maxSendNum=100000L;
    SendMsgRunable(Channel channel,IotJmReconnectClient client){
        this.channel=channel;
        this.client=client;
    }
    @Override
    public void run() {

        while (true){

            IotJmTcpPacket packet=new IotJmTcpPacket();
            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("token", IdUtil.fastSimpleUUID());
            jsonObject.put("token", "424332366e474f5f84d55b83a627b0f1");
            jsonObject.put("msg", "测试");
            jsonObject.put("name", Thread.currentThread().getName());
            Long msgid=sendNum.getAndIncrement();
            if (msgid>maxSendNum){
                client.closeNetty();
                return;
            }
            jsonObject.put("msgid", msgid);
            packet.setMsgData(jsonObject.toJSONString());
            packet.setFrameType(IotJmFrameTypeEnum.pointDataReport);
            if (channel.isActive()){
                channel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info("消息发送成功："+ msgid);
                        } else {
                            log.error("消息发送失败："+ msgid);
                        }

                    }

                });
            }else {
                log.error("连接失败，停止发送："+ msgid);
                return;
            }

            try {
                Thread.sleep(RandomUtil.randomInt(1,10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
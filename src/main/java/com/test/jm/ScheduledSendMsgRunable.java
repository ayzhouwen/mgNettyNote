package com.test.jm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 定时发送消息
 */
@Slf4j
class ScheduledSendMsgRunable implements Runnable {
    private Channel channel;
    private IotJmReconnectClient client;
    /**
     * 所有线程总发送数量
     */
    private AtomicLong sendNum = new AtomicLong(1);
    /**
     * 最大允许发送数量
     */
    private static final Long maxSendNum = 100000L;

    ScheduledSendMsgRunable(Channel channel, IotJmReconnectClient client) {
        this.channel = channel;
        this.client = client;

    }

    @Override
    public void run() {

        while (true) {

            IotJmTcpPacket packet = new IotJmTcpPacket();

            Long msgid = sendNum.getAndIncrement();
            if (msgid > maxSendNum) {
                client.closeNetty();
                return;
            }
            packet.setMsgData(getMockPointDataReport());
            packet.setFrameType(IotJmFrameTypeEnum.pointDataReport);
            if (channel.isActive()) {
                channel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            log.info("消息发送成功：" + msgid);
                        } else {
                            log.error("消息发送失败：" + msgid);
                        }

                    }

                });
            } else {
                log.error("连接失败，停止发送：" + msgid);
                return;
            }

            try {
                Thread.sleep(RandomUtil.randomInt(1000, 5000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取模拟监测点数据上报
     *
     * @return
     */
    private String getMockPointDataReport() {
        //language=JSON
        String time = DateUtil.now();
        String json = "{\n" +
                "  \"token\": \"424332366e474f5f84d55b83a627b0f1\",\n" +
                "  \"iotMsgNo\": 10001,\n" +
                "  \"iotMsgTime\": \"" + time + "\",\n" +
                "  \"iotMsgBody\": [\n" +
                "    {\n" +
                "      \"devCode\": \"110116000000-0204-000002\",\n" +
                "      \"networkState\": 0,\n" +
                "      \"points\": [{\n" +
                "        \"pointCode\": \"110116000000-0204-000002-02040001-0000\",\n" +
                "        \"pointValue\": 0,\n" +
                "        \"pointState\": 0,\n" +
                "        \"receviceTime\": \"" + time + "\"\n" +
                "      },{\n" +
                "        \"pointCode\": \"110116000000-0204-000002-02040001-0001\",\n" +
                "        \"pointValue\": 1,\n" +
                "        \"pointState\": 0,\n" +
                "        \"receviceTime\": \"" + time + "\"\n" +
                "      }]\n" +
                "    },    {\n" +
                "      \"devCode\": \"110116000000-0102-000003\",\n" +
                "      \"networkState\": 0,\n" +
                "      \"points\": [{\n" +
                "        \"pointCode\": \"110116000000-0102-000003-01020001-0000\",\n" +
                "        \"pointValue\": 0,\n" +
                "        \"pointState\": 0,\n" +
                "        \"receviceTime\": \"" + time + "\"\n" +
                "      },{\n" +
                "        \"pointCode\": \"110116000000-0102-000003-01020001-0001\",\n" +
                "        \"pointValue\": 0,\n" +
                "        \"pointState\": 0,\n" +
                "        \"receviceTime\": \"" + time + "\"\n" +
                "      }]\n" +
                "    }]}";

        return json;

    }

    ;
}
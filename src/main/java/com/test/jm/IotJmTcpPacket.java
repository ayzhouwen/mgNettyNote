package com.test.jm;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

/**
 * jm监控主机TCP协议数据包
 *
 */
@Data
public class IotJmTcpPacket {
    /**
     * 帧头标识 0xAABB,用完手动释放
     */
    public static ByteBuf getHeadFlag(){
        return      Unpooled.copiedBuffer(new byte[]{(byte) 0xAA,(byte) 0xBB});
    }
    /**
     * 单包最大 50M
     */
    public static final int packetSizeLimit=1024*1024*50;
    /**
     * 帧类型
     */
    public  IotJmFrameTypeEnum frameType;
    /**
     * 帧数据(json字符串)
     */
    public String msgData;

}

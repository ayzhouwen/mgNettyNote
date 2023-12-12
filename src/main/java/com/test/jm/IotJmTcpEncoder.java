package com.test.jm;

import com.common.utils.CRCUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * jm监控主机Tcp数据报文编码器
 * 一个数据报文由帧头字节+帧数据字节+帧尾字节组成
 * 帧头字节:0xAABB(固定)+帧类型(1Byte)+数据长度(4Byte)+帧尾crc校验字节(2字节)
 * CRC校验码：使用 CRC-16/XMODEM 标准进行校验，校验范围：帧类型、数据长度，用于验证内容的完整性。
 */
public class IotJmTcpEncoder extends MessageToByteEncoder<IotJmTcpPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IotJmTcpPacket msg, ByteBuf out) throws Exception {
            ByteBuf dataByteBuf = Unpooled.copiedBuffer(msg.msgData, StandardCharsets.UTF_8);
            out.writeBytes(IotJmTcpPacket.getHeadFlag());
            out.writeByte(msg.frameType.getCode());
            out.writeInt(dataByteBuf.readableBytes());
            out.writeBytes(dataByteBuf);
            //计算crc
            short crc= CRCUtils.getCRC(ByteBufUtil.getBytes(out));
            out.writeShort(crc);

    }
}

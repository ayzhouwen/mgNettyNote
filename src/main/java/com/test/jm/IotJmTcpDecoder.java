package com.test.jm;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.thread.TaskExecutePool;
import com.common.utils.CRCUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * jm监控主机Tcp数据报文解码器
 * 一个数据报文由帧头字节+帧数据字节+帧尾字节组成
 * 帧头字节:0xAABB(固定)+帧类型(1Byte)+数据长度(4Byte)+帧尾crc校验字节(2字节)
 * CRC校验码：使用 CRC-16/XMODEM 标准进行校验，校验范围：帧类型、数据长度，用于验证内容的完整性。
 */
@Slf4j
public class IotJmTcpDecoder extends ByteToMessageDecoder {
    //帧头索引
    private int headerIndex = -1;
    //帧头读取错误次数
    private int headerError=0;
    //帧数据长度
    private int frameDataLen = -1;
    //帧类型
    Byte frameType = null;
    private final TaskExecutePool MsgExecute = new TaskExecutePool(32, "jmTcpDataMsgHandle", 5000);
    @Override
    public void channelInactive(ChannelHandlerContext ctx)  {
        log.info("jm监控主机:{}连接断开",ctx.channel().remoteAddress());
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        log.info("发现jm监控主机:{}新连接",ctx.channel().remoteAddress());
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("接收到十六进制数据：{}", ByteBufUtil.hexDump(in));
        // 获取帧头位置
        if (headerIndex == -1) {
            int index = ByteBufUtil.indexOf(IotJmTcpPacket.getHeadFlag(), in);
            if (index > -1 && index < in.capacity()) {
                in.readerIndex(index);
                in.readShort();
                // 将帧头标识位置保存
                headerIndex = index;
                log.info("读取到帧头标识位置:"+headerIndex);
            }else {
                //异常数据
                headerError++;
                if (headerError>=2){
                    log.error("jm监控主机:{},无法获取帧头标识",ctx.channel().remoteAddress());
                    in.skipBytes(in.readableBytes());
                    ctx.channel().close();
                }
            }
        }
        if (headerIndex > -1) {
            //帧头可读
            if (in.isReadable(5)&&frameType==null&&frameDataLen==-1) {
                frameType = in.readByte();
                if (!IotJmFrameTypeEnum.containsCode(frameType)){
                    log.error("jm监控主机:{},无效帧类型",ctx.channel().remoteAddress());
                    in.skipBytes(in.readableBytes());
                    ctx.channel().close();
                    return;
                }
                log.info("帧类型：{}", frameType + "");
                frameDataLen = in.readInt();
                log.info("数据帧长度：{}", frameDataLen);
                if (frameDataLen<=0||frameDataLen>IotJmTcpPacket.packetSizeLimit){
                    log.error("jm监控主机:{},帧数据长度:{}超出最大限制:{}",ctx.channel().remoteAddress(),frameDataLen,IotJmTcpPacket.packetSizeLimit);
                    in.skipBytes(in.readableBytes());
                    ctx.channel().close();
                    return;
                }
            }
            if (frameDataLen > 0) {
                // 数据帧
                ByteBuf frameData = null;
                //帧数据和帧尾可读,计算crc
                if (in.isReadable(frameDataLen + 2)) {
                    frameData = in.readBytes(frameDataLen);
                    log.info("帧数据：{}",ByteBufUtil.hexDump(frameData));
                    short srcCrc = in.readShort();
                    log.info("crc校验：{}",srcCrc);
                    ByteBuf crcByteBuf = Unpooled.buffer();
                    crcByteBuf.writeBytes(IotJmTcpPacket.getHeadFlag());
                    crcByteBuf.writeByte(frameType);
                    crcByteBuf.writeInt(frameDataLen);
                    crcByteBuf.writeBytes(Unpooled.copiedBuffer(frameData));
                    try {
                        log.info("crcByteBuf:{}",ByteBufUtil.hexDump(crcByteBuf));
                        boolean crcB = CRCUtils.checkCRC(ByteBufUtil.getBytes(crcByteBuf), srcCrc);
                        if (crcB) {
                            log.info("数据校验成功");
                            String body = frameData.toString(StandardCharsets.UTF_8);
                            log.info("接收到的数据body："+body);
                            msgHandle(frameType,body,ctx);

                        } else {
                            log.error("数据crc16校验不成功");
                            return;
                        }
                    } finally {
                        headerIndex = -1;
                        frameDataLen = -1;
                        frameType = null;
                        headerError=0;
                        frameData.release();
                    }
                }
            }
        }


    }


    /**
     * 消息处理
     * @param msgData
     * @param ctx
     */
    private void  msgHandle(Byte frameType,String msgData,ChannelHandlerContext ctx){


    };
}

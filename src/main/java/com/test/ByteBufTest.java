package com.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufTest {
    /**
     * 重复读测试
     */
    public static void repeatable(){
        ByteBuf byteBuf= Unpooled.buffer();
        byteBuf.writeInt(1900);
        byteBuf.writeInt(1901);
        log.info(ByteBufUtil.hexDump(byteBuf));
        log.info(byteBuf.toString());
        //开始读
        int n,rindex;
        n=byteBuf.readInt();
        log.info("读取到的值:{},当前可读字节：{}",n,byteBuf.readableBytes());
        byteBuf.markReaderIndex();
        n=byteBuf.readInt();
        log.info("读取到的值:{},当前可读字节：{}",n,byteBuf.readableBytes());
        log.info(byteBuf.toString());
        //如果再次调用byteBuf.readInt(),运行报错，java.lang.IndexOutOfBoundsException
//        n=byteBuf.readInt();

        //方法一：当字节读完时，如果喜爱那个重复读可以使用get，但是必须清楚字节位置，否则读出来的数据是错误的
        n=byteBuf.getInt(3);
        log.info("读取到的值:{},当前可读字节：{}",n,byteBuf.readableBytes());
        log.info(byteBuf.toString());
        //方法二: 循环恢复读指针(推荐，不用死记内存结构)
        for (int i = 0; i <10 ; i++) {
            byteBuf.resetReaderIndex();
            log.info(byteBuf.toString());
            n=byteBuf.readInt();
            log.info("读取到的值:{},当前可读字节：{}",n,byteBuf.readableBytes());
            log.info(byteBuf.toString());
        }



    }

    public static void main(String[] args) {
        repeatable();
    }
}

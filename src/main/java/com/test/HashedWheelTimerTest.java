package com.test;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 *  HashedWheelTimer 定时任务测试
 */
@Slf4j
public class HashedWheelTimerTest {
    public static void main(String[] args) {
        HashedWheelTimer timer=new HashedWheelTimer();
        for (int i = 0; i < 10000; i++) {
            timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    try {
                        log.info(timeout.toString());
                    } catch (Exception e) {
                        log.error("定时器执行异常:",e);
                    }
                }
            },10*i, TimeUnit.MILLISECONDS);
        }

        try {
            //如果不执行 timer.stop()那么 main线程60秒后执行完毕后,HashedWheelTimer里如果还有任务还在运行
            Thread.sleep(1000*10);
//            timer.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

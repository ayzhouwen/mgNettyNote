package com.test.jm;

import lombok.Getter;

import java.util.Arrays;

/**
 * jm 监控主机Tcp通信帧类型
 */

@Getter
public enum IotJmFrameTypeEnum {
    pointDataReport((byte)0x01,"监测点数据上报"),
    pointAlarmReport((byte)0x02,"监测点告警数据上报"),
    pointCmdReq((byte)0x03,"监测点控制指令请求"),
    pointCmdRep((byte)0x04,"监测点控制指令响应");
    private Byte code;
    private String title;

    IotJmFrameTypeEnum(Byte code, String title) {
        this.code = code;
        this.title = title;
    }

    /**
     * 根据code返回对象
     * @param code
     * @return
     */
    public static IotJmFrameTypeEnum getEnumBycode(Byte code) {
        IotJmFrameTypeEnum[] values = IotJmFrameTypeEnum.values();
        for (IotJmFrameTypeEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
    /**
     *  是否包含code
     * @param code
     * @return
     */
    public static boolean containsCode(Byte code) {
        IotJmFrameTypeEnum[] values = IotJmFrameTypeEnum.values();
        for (IotJmFrameTypeEnum value : values) {
            if (value.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static IotJmFrameTypeEnum getEnumByCode(Byte code) {
        return Arrays.stream(IotJmFrameTypeEnum.values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);
    }

}

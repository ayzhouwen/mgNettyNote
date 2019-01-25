package com.my_proto.server;

public class MyProtocolBean {
    //类型 系统编号 0XA 表示a系统,0XB 表示b系统
    private byte type;
    //信息标志 0XA表示心跳包 0XB 表示超时包, 0XC表示业务信息包
    private byte flag;
    //内容长度
    private int length;
    //内容
    private String content;

    public MyProtocolBean(byte flag, byte type, int length, String content){
        this.flag = flag;
        this.type = type;
        this.length = length;
        this.content = content;
    };
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyProtocolBean{" +
                "type=" + type +
                ", flag=" + flag +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }


}

package com.goExplore_06_1_2;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.nio.ByteBuffer;

//java序列化后码流太大,比纯二进制码流大的多,时间长
//此处的注解一定要加,否则服务端会接收不到消息
@Message 
public class UserInfo implements Serializable  {
	private static final long serialVersionUID = -1173155423576631307L;
	//默认的序列号
	private String userName;
	private int userID;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public UserInfo buildUserName(String userName){
		this.userName=userName;
		return this;
	}
	
	public UserInfo buildUserID(int userID){
		this.userID=userID;
		return this;
	}
	
	//使用自定义的编码解码比序列化占用更小内存,更短解码时间
	public byte[] codeC(){
			ByteBuffer buffer=ByteBuffer.allocate(1024);
			byte[] value=this.userName.getBytes();
			buffer.putInt(value.length);
			buffer.put(value);
			buffer.putInt(this.userID);
			buffer.flip();
			value=null;
			byte [] result=new byte[buffer.remaining()];
			return result;
	}
	
	@Override
	public String toString() {
	return "UserInfo [userName=" + userName + ", userID=" + userID + "]";
	}
	
}

package com.goExplore_06_1_2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestUserInfo {
	public static void main(String[] args) throws IOException {
		UserInfo info=new UserInfo();
		info.buildUserID(100).buildUserName("欢迎来到netty的世界");
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ObjectOutputStream os=new ObjectOutputStream(bos);
		os.writeObject(info);
		os.flush();
		os.close();
		byte [] b=bos.toByteArray();
		System.out.println("the jdk serializable length is:"+b.length);
		bos.close();
		System.out.println("----------------------------------------------");
		System.out.println("the byte array serializable length  is :"+info.codeC().length);
	}
}

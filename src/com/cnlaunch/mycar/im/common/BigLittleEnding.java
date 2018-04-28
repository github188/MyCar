package com.cnlaunch.mycar.im.common;

public class BigLittleEnding {
	public static byte[] intToLittleEnding(int n){
		return new byte[]{
				(byte)(0xff & n),
				(byte)((0xff00 & n) >> 8),
				(byte)((0xff0000 & n) >> 16),
				(byte)((0xff000000 & n) >> 24)			
		};
	}
	
	public static int intFromLittleEnding(byte[] bytes){
		if(bytes.length != 4){
			throw new IllegalArgumentException("参数必须为长度为4的byte数组");
		}
		return (bytes[0]&0xff) 
				| (bytes[1]&0xff) << 8
				| (bytes[2]&0xff) << 16
				| (bytes[3]&0xff) << 24;
	}
}

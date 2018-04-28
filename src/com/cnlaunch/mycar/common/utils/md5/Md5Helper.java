package com.cnlaunch.mycar.common.utils.md5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * <b>Title:</b> Md5Helper.java<br>
 * <b>Description:</b> MD5加密<br>
 * Copyright (c) 2010-2011 Launch Tech Co., Ltd. All rights reserved.
 * 
 * @author chenfj Feb 2, 2010
 * @version V1.0
 */
public class Md5Helper {
	/**
	 * Logger for this class
	 */


	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组丄1�7进制字串，即数字字母混合的形弄1�7
	 * 
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 返回加密结果的1�7进制数字字串，即全数字形弄1�7
	 * 
	 * @param b 字节数组
	 * @return 10进制数字字串
	 */
	private static String byteArrayToNumberString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToNumString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToNumString(byte b) {
		int _b = b;
		if (_b < 0) {
			_b = 256 + _b;
		}

		return String.valueOf(_b);
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 得到某字符串的MD5，使用JDK内置的实玄1�7
	 * 
	 * @param s 要生成MD5的字符串
	 * @return MD5结果的1�7进制数字字串，即全数字形弄1�7
	 */
	public static String jdkMd5AsNum(String s) {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("MD5");
			s = byteArrayToNumberString(md.digest(s.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
	
		}

		return s;
	}

	/**
	 * 得到某字符串的MD5，使用JDK内置的实玄1�7
	 * 
	 * @param s 要生成MD5的字符串
	 * @return MD5结果的1�7进制字串，即数字字母混合的形弄1�7
	 */
	public static String jdkMd5AsHex(String s) {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("MD5");
			s = byteArrayToHexString(md.digest(s.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
		
		}

		return s;
	}

	/**
	 * 得到某字符串的MD5，使用Fast MD5 Implementation in
	 * Java(http://www.twmacinta.com/myjava/fast_md5.php)
	 * 
	 * @param s 要生成MD5的字符串
	 * @return MD5结果的1�7进制字串，即数字字母混合的形弄1�7
	 */
	public static String fastMd5AsHex(String s) {
		MD5 md5 = new MD5();
		try {
			md5.Update(s, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		
		}
		return md5.asHex();
	}

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		System.out.println(fastMd5AsHex("launch031").equals("bb27c928d929977576337b02afa81d90"));
//		String s = "admin";
//		String result = null;
//		int cycle = 1000;
//
//		// Fast MD5 Implementation
//		// MD5.initNativeLibrary(true);
//
//		long time3 = System.currentTimeMillis();
//		for (int i = 0; i < cycle; i++) {
//			result = Md5Helper.fastMd5AsHex(s);
//		}
//		time3 = System.currentTimeMillis() - time3;
//		System.out.println("MD5.asHex(" + s + ")=" + result + "\tTime:" + time3);
//
//		// JDK MD5实现
//		long time = System.currentTimeMillis();
//		for (int i = 0; i < cycle; i++) {
//			result = Md5Helper.jdkMd5AsHex(s);
//		}
//		time = System.currentTimeMillis() - time;
//		System.out.println("MD5Helper.md5AsHex(" + s + ")=" + result
//				+ "\tTime:" + time);
//
//		long time2 = System.currentTimeMillis();
//		for (int i = 0; i < cycle; i++) {
//			result = Md5Helper.jdkMd5AsNum(s);
//		}
//		time2 = System.currentTimeMillis() - time2;
//		System.out.println("MD5Helper.md5AsNum(" + s + ")=" + result
//				+ "\tTime:" + time2);
		
		

	}

}

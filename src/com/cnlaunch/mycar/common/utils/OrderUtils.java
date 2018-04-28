package com.cnlaunch.mycar.common.utils;

public class OrderUtils {

	// 0x55 0xAA 0xF0 0xF8
	// : <起始标志>+<目标地址>+<源地址>+<包长度>+<计数器>+<命令字>+<参数设置区>+<包校验>
	/*
	 * <起始标志>:0x55 0xAA 
	 * <目标地址>:0xF0 
	 * <源地址>: 0xF8 
	 * <包长度>: ="<计数器>+<命令字>+<参数设置区>" :
	 * <计数器>: 
	 * <命令字>: 
	 * <参数设置区>: 
	 * <包校验>:对“<目标地址>+<源地址>+<包长度>+<计数器>+<命令字>+<参数设置区>" :
	 */
	public static byte[] getSendOrder(byte[] orderBody) {
		int length = orderBody.length;
		byte[] order = new byte[length + 5];
		order[0] = 0x55;
		order[1] = (byte) 0xaa;
		order[2] = (byte) 0xf0;// 目标地址为诊断头
		order[3] = (byte) 0xf8;// 源地址为手机端
		order[4] = (byte) orderBody.length;// 包长度
		for (int i = 0; i < length; i++) {
			order[i + 4] = orderBody[i];
		}
		order[length + 4] = getOrderCheckCode(order);

		return order;
	}

	// 得到指令校验码
	public static byte getOrderCheckCode(byte[] order) {
		byte checkCode = 0;
		for (int i = 0; i < order.length; i++) {
			checkCode ^= order[i];
		}
		return checkCode;
	}
}

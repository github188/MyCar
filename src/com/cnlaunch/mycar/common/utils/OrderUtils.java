package com.cnlaunch.mycar.common.utils;

public class OrderUtils {

	// 0x55 0xAA 0xF0 0xF8
	// : <��ʼ��־>+<Ŀ���ַ>+<Դ��ַ>+<������>+<������>+<������>+<����������>+<��У��>
	/*
	 * <��ʼ��־>:0x55 0xAA 
	 * <Ŀ���ַ>:0xF0 
	 * <Դ��ַ>: 0xF8 
	 * <������>: ="<������>+<������>+<����������>" :
	 * <������>: 
	 * <������>: 
	 * <����������>: 
	 * <��У��>:�ԡ�<Ŀ���ַ>+<Դ��ַ>+<������>+<������>+<������>+<����������>" :
	 */
	public static byte[] getSendOrder(byte[] orderBody) {
		int length = orderBody.length;
		byte[] order = new byte[length + 5];
		order[0] = 0x55;
		order[1] = (byte) 0xaa;
		order[2] = (byte) 0xf0;// Ŀ���ַΪ���ͷ
		order[3] = (byte) 0xf8;// Դ��ַΪ�ֻ���
		order[4] = (byte) orderBody.length;// ������
		for (int i = 0; i < length; i++) {
			order[i + 4] = orderBody[i];
		}
		order[length + 4] = getOrderCheckCode(order);

		return order;
	}

	// �õ�ָ��У����
	public static byte getOrderCheckCode(byte[] order) {
		byte checkCode = 0;
		for (int i = 0; i < order.length; i++) {
			checkCode ^= order[i];
		}
		return checkCode;
	}
}

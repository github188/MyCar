package com.cnlaunch.mycar.obd2.util;

/**
 * <���ܼ���>�����߳̿����� <������ϸ����>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-12
 * @since DBS V100
 */
public class TheadSwitch {
	/*
	 * �����Ǳ�������ˢ��
	 */
	public static boolean flag = true;
	/*
	 * ��������������ˢ��
	 */
	public static boolean flagAllData = true;

	public static boolean isFlagAllData() {
		return flagAllData;
	}

	public static void setFlagAllData(boolean flagAllData) {
		TheadSwitch.flagAllData = flagAllData;
	}

	public static boolean isFlag() {
		return flag;
	}

	public static void setFlag(boolean flag) {
		TheadSwitch.flag = flag;
	}

}
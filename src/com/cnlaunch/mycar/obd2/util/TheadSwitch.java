package com.cnlaunch.mycar.obd2.util;

/**
 * <功能简述>控制线程开关类 <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-12
 * @since DBS V100
 */
public class TheadSwitch {
	/*
	 * 控制仪表数据流刷新
	 */
	public static boolean flag = true;
	/*
	 * 控制所有数据流刷新
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
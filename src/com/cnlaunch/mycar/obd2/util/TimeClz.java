package com.cnlaunch.mycar.obd2.util;

/**
 * 
 * <���ܼ���>ʱ������� <������ϸ����>������ϱ����ȡʱ���ʱ���¼
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class TimeClz {
	private static String day;
	private static String hour;
	private static String minute;
	private static String second;

	public TimeClz() {
	}

	public static String getDay() {
		return day;
	}

	public static void setDay(String day) {
		TimeClz.day = day;
	}

	public static String getHour() {
		return hour;
	}

	public static void setHour(String hour) {
		TimeClz.hour = hour;
	}

	public static String getMinute() {
		return minute;
	}

	public static void setMinute(String minute) {
		TimeClz.minute = minute;
	}

	public static String getSecond() {
		return second;
	}

	public static void setSecond(String second) {
		TimeClz.second = second;
	}

	@Override
	public String toString() {
		return day + " " + hour + minute + second;
	}

}

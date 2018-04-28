package com.cnlaunch.mycar.obd2.util;

/**
 * 
 * <功能简述> <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-6-28
 * @since DBS V100
 */
public class AccelerationRecordsConstants {
	private static long carSpeedTimes1 = 0l;
	private static long carSpeedTimes2 = 0l;

	private static boolean isRecordTheSpeedOfTime1 = true;
	private static boolean isRecordTheSpeedOfTime2 = false;

	private static long carSpeed1 = 0l;
	private static long carSpeed2 = 0l;

	public static long getCarSpeedTimes1() {
		return carSpeedTimes1;
	}

	public static void setCarSpeedTimes1(long carSpeedTimes1) {
		AccelerationRecordsConstants.carSpeedTimes1 = carSpeedTimes1;
	}

	public static long getCarSpeedTimes2() {
		return carSpeedTimes2;
	}

	public static void setCarSpeedTimes2(long carSpeedTimes2) {
		AccelerationRecordsConstants.carSpeedTimes2 = carSpeedTimes2;
	}

	public static boolean isRecordTheSpeedOfTime1() {
		return isRecordTheSpeedOfTime1;
	}

	public static void setRecordTheSpeedOfTime1(boolean isRecordTheSpeedOfTime1) {
		AccelerationRecordsConstants.isRecordTheSpeedOfTime1 = isRecordTheSpeedOfTime1;
	}

	public static boolean isRecordTheSpeedOfTime2() {
		return isRecordTheSpeedOfTime2;
	}

	public static void setRecordTheSpeedOfTime2(boolean isRecordTheSpeedOfTime2) {
		AccelerationRecordsConstants.isRecordTheSpeedOfTime2 = isRecordTheSpeedOfTime2;
	}

	public static long getCarSpeed1() {
		return carSpeed1;
	}

	public static void setCarSpeed1(long carSpeed1) {
		AccelerationRecordsConstants.carSpeed1 = carSpeed1;
	}

	public static long getCarSpeed2() {
		return carSpeed2;
	}

	public static void setCarSpeed2(long carSpeed2) {
		AccelerationRecordsConstants.carSpeed2 = carSpeed2;
	}

}

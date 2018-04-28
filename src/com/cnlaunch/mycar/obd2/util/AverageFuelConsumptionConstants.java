package com.cnlaunch.mycar.obd2.util;

/**
 * 
 * <功能简述> <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-6-28
 * @since DBS V100
 */
public class AverageFuelConsumptionConstants {
	private static boolean isRecordTheAverageFuelConsumptionOfTime1 = true;
	private static boolean isRecordTheAverageFuelConsumptionOfTime2 = false;

	private static long averageFuelConsumptionTimes1 = 0l;
	private static long averageFuelConsumptionTimes2 = 0l;

	private static long averageFuelConsumption1 = 0l;
	private static long averageFuelConsumption2 = 0l;

	public static boolean isRecordTheAverageFuelConsumptionOfTime1() {
		return isRecordTheAverageFuelConsumptionOfTime1;
	}

	public static void setRecordTheAverageFuelConsumptionOfTime1(
			boolean isRecordTheAverageFuelConsumptionOfTime1) {
		AverageFuelConsumptionConstants.isRecordTheAverageFuelConsumptionOfTime1 = isRecordTheAverageFuelConsumptionOfTime1;
	}

	public static boolean isRecordTheAverageFuelConsumptionOfTime2() {
		return isRecordTheAverageFuelConsumptionOfTime2;
	}

	public static void setRecordTheAverageFuelConsumptionOfTime2(
			boolean isRecordTheAverageFuelConsumptionOfTime2) {
		AverageFuelConsumptionConstants.isRecordTheAverageFuelConsumptionOfTime2 = isRecordTheAverageFuelConsumptionOfTime2;
	}

	public static long getAverageFuelConsumptionTimes1() {
		return averageFuelConsumptionTimes1;
	}

	public static void setAverageFuelConsumptionTimes1(
			long averageFuelConsumptionTimes1) {
		AverageFuelConsumptionConstants.averageFuelConsumptionTimes1 = averageFuelConsumptionTimes1;
	}

	public static long getAverageFuelConsumptionTimes2() {
		return averageFuelConsumptionTimes2;
	}

	public static void setAverageFuelConsumptionTimes2(
			long averageFuelConsumptionTimes2) {
		AverageFuelConsumptionConstants.averageFuelConsumptionTimes2 = averageFuelConsumptionTimes2;
	}

	public static long getAverageFuelConsumption1() {
		return averageFuelConsumption1;
	}

	public static void setAverageFuelConsumption1(long averageFuelConsumption1) {
		AverageFuelConsumptionConstants.averageFuelConsumption1 = averageFuelConsumption1;
	}

	public static long getAverageFuelConsumption2() {
		return averageFuelConsumption2;
	}

	public static void setAverageFuelConsumption2(long averageFuelConsumption2) {
		AverageFuelConsumptionConstants.averageFuelConsumption2 = averageFuelConsumption2;
	}

}

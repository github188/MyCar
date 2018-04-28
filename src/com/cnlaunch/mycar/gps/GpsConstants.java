package com.cnlaunch.mycar.gps;


public class GpsConstants {
	public static String DIR_NAME_GPX = "gpx";// ������gpx�ļ���sd���ϵ�����Ŀ¼
	public static String DIR_NAME_PHOTO = "photo";// GPS��Ƭ��sd���ϵ�����Ŀ¼
	public static String DIR_NAME_NOTE = "note";// GPS������sd���ϵ�����Ŀ¼
	public static String DIR_NAME_TMP = "tmp";// GPS��sd������ʱ�ļ�������Ŀ¼
	public static String FILE_NAME_TMP_PHOTO = "tmp.jpg";// GPS��sd������ʱ�ļ�������Ŀ¼
	public static String FILE_ANME_TMP_NOTE = "tmp.txt";// GPS��sd������ʱ�ļ�������Ŀ¼
	
	public static String GPS_OFFSET_SERVICE_URL = "http://webapi.dbscar.com:8081/GoogleMap/ExcursionAPI.ashx";// GPSƫ��ֵ���Ϸ�����
	//public static String GPS_OFFSET_SERVICE_URL = "http://twebapi.dbs.cnlaunch.com/GoogleMap/ExcursionAPI.ashx";// GPSƫ��ֵ���Է�����
	public static final String GPS_LOGGER_SERVICE_NAME = "com.cnlaunch.mycar.gps.intent.action.GpsLoggerService";
	public static final int GPS_LOGGER_UNKNOWN = -1;
	public static final int GPS_LOGGER_LOGGING = 1;
	public static final int GPS_LOGGER_PAUSED = 2;
	public static final int GPS_LOGGER_STOPPED = 3;
	public static final String GPS_PREFERENCES_OFFSET_X = "PREFERENCES_OFFSET_X";
	public static final String GPS_PREFERENCES_OFFSET_Y = "PREFERENCES_OFFSET_Y";


}

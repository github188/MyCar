package com.cnlaunch.mycar.gps;


public class GpsConstants {
	public static String DIR_NAME_GPX = "gpx";// 导出的gpx文件在sd卡上的数据目录
	public static String DIR_NAME_PHOTO = "photo";// GPS照片在sd卡上的数据目录
	public static String DIR_NAME_NOTE = "note";// GPS记事在sd卡上的数据目录
	public static String DIR_NAME_TMP = "tmp";// GPS在sd卡上临时文件的数据目录
	public static String FILE_NAME_TMP_PHOTO = "tmp.jpg";// GPS在sd卡上临时文件的数据目录
	public static String FILE_ANME_TMP_NOTE = "tmp.txt";// GPS在sd卡上临时文件的数据目录
	
	public static String GPS_OFFSET_SERVICE_URL = "http://webapi.dbscar.com:8081/GoogleMap/ExcursionAPI.ashx";// GPS偏移值线上服务器
	//public static String GPS_OFFSET_SERVICE_URL = "http://twebapi.dbs.cnlaunch.com/GoogleMap/ExcursionAPI.ashx";// GPS偏移值测试服务器
	public static final String GPS_LOGGER_SERVICE_NAME = "com.cnlaunch.mycar.gps.intent.action.GpsLoggerService";
	public static final int GPS_LOGGER_UNKNOWN = -1;
	public static final int GPS_LOGGER_LOGGING = 1;
	public static final int GPS_LOGGER_PAUSED = 2;
	public static final int GPS_LOGGER_STOPPED = 3;
	public static final String GPS_PREFERENCES_OFFSET_X = "PREFERENCES_OFFSET_X";
	public static final String GPS_PREFERENCES_OFFSET_Y = "PREFERENCES_OFFSET_Y";


}

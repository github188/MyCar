package com.cnlaunch.mycar.gps.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author xuzhuowei
 * The GpsTrackProvider stores all static infomathion about GPStrack.
 */
public class GpsTrack {
	//The authority fo this provider
	public static final String AUTHORITY = "com.cnlaunch.mycar";
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY);
	
	/**
	 * @author xuzhuowei
	 *This table contains 
	 */
	public static final class Tracks extends TracksColumns implements android.provider.BaseColumns{
		public static final String TABLE = "tracks";
		public static final String  CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.track";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.track";
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE);
	    static final String SQL_CREATE_TABLE = 
	    	         "CREATE TABLE " + TABLE + 
	    	         "(" + " " + _ID           + " " + _ID_TYPE + 
	    	         "," + " " + NAME          + " " + NAME_TYPE + 
	    	         "," + " " + CREATE_TIME   + " " + CREATE_TIME_TYPE + 
	    	         ");";

	}
	
	/**
	 * @author xuzhuowei
	 *This table contains segments
	 */
	public static final class Segments extends SegmentsColumns implements android.provider.BaseColumns{
		public static final String TABLE = "segments";
		public static final String  CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.segment";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.segment";
	
	    static final String SQL_CREATE_TABLE = 
	    	         "CREATE TABLE " + TABLE + 
	    	         "(" + " " + _ID   + " " + _ID_TYPE + 
	    	         "," + " " + TRACK + " " + TRACK_TYPE + 
	    	         ");";

	}
	
	public static final class Waypoints extends WaypointsColumns implements android.provider.BaseColumns{
		public static final String TABLE = "waypoints";
		public static final String  CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.waypoint";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.waypoint";
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE);
		public static final String NOT_GET_OFFSET = "not_get_offset_yet";

		static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + 
	    	      "(" + " " + BaseColumns._ID + " " + _ID_TYPE + 
	    	      "," + " " + LATITUDE  + " " + LATITUDE_TYPE + 
	    	      "," + " " + LONGITUDE + " " + LONGITUDE_TYPE + 
	    	      "," + " " + TIME      + " " + TIME_TYPE + 
	    	      "," + " " + SPEED     + " " + SPEED + 
	    	      "," + " " + SEGMENT   + " " + SEGMENT_TYPE + 
	    	      "," + " " + TRACK     + " " + TRACK_TYPE + 
	    	      "," + " " + OFFSET_X   + " " + OFFSET_X_TYPE + 
	    	      "," + " " + OFFSET_Y   + " " + OFFSET_Y_TYPE + 
	    	      "," + " " + OFFSET_GETTED   + " " + OFFSET_GETTED_TYPE + 
	    	      "," + " " + ACCURACY  + " " + ACCURACY_TYPE + 
	    	      "," + " " + ALTITUDE  + " " + ALTITUDE_TYPE + 
	    	      "," + " " + BEARING   + " " + BEARING_TYPE + 
	    	      ");";

	
	}
	
	public static final class Media extends MediaColumns implements android.provider.BaseColumns{
		public static final String TABLE = "media";
		public static final String  CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.cnlaunch.mycar.media";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.cnlaunch.mycar.media";
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE);
		
		static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + 
			      "(" + " " + BaseColumns._ID       + " " + _ID_TYPE + 
			      "," + " " + TRACK    + " " + TRACK_TYPE + 
			      "," + " " + SEGMENT  + " " + SEGMENT_TYPE + 
			      "," + " " + WAYPOINT + " " + WAYPOINT_TYPE + 
			      "," + " " + URI      + " " + URI_TYPE + 
			      "," + " " + KIND     + " " + KIND_TYPE + 
			      "," + " " + CREATE_TIME   + " " + CREATE_TIME_TYPE + 

			      ");";
	
	}
	
	/**
	 * @author xuzhuowei
	 *Columns from the tracks table.
	 */
	public static class TracksColumns{
		public static final String NAME	= "name";
		public static final String CREATE_TIME = "createTime";
		static final String NAME_TYPE = "TEXT";
		static final String CREATE_TIME_TYPE = "INTEGER NOT NULL";
		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	}

	/**
	 * @author xuzhuowei
	 *Columns from segments table.
	 */
	public static class SegmentsColumns{
		public static final String TRACK = "track";
		static final String TRACK_TYPE = "INTEGER NOT NULL";
		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	}
	
	/**
	 * @author xuzhuowei
	 *Columns from waypoints table.
	 */
	public static class WaypointsColumns{
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String TIME = "time";
		public static final String SPEED = "speed";
		public static final String TRACK = "track";
		public static final String SEGMENT = "tracksegment";
		public static final String OFFSET_X = "offset_x";//轨迹点显示时的位置修正值，直接适用于对GOOGLE地图19级的显示修正
		public static final String OFFSET_Y = "offset_y";
		public static final String OFFSET_GETTED = "offset_getted";
		public static final String ACCURACY	= "accuracy";
		public static final String ALTITUDE	= "altitude";
		public static final String BEARING = "bearing";
		
		/**
		 * 尚未从服务器未获取到位置修正值
		 */
		public static final int OFFSET_GETTED_NOT_YET = 0;
		/**
		 * 已经服务器未获取到位置修正值
		 */
		public static final int OFFSET_GETTED_YET = 1;
		
		static final String LATITUDE_TYPE = "REAL NOT NULL";
		static final String LONGITUDE_TYPE = "REAL NOT NULL";
		static final String TIME_TYPE = "INTEGER NOT NULL";
		static final String SPEED_TYPE = "REAL NOT NULL";
		static final String TRACK_TYPE = "INTEGER NOT NULL";
		static final String SEGMENT_TYPE = "INTEGER NOT NULL";
		static final String OFFSET_X_TYPE = "INTEGER";
		static final String OFFSET_Y_TYPE =	"INTEGER";
		static final String OFFSET_GETTED_TYPE = "INTEGER";
		static final String ACCURACY_TYPE = "REAL";
		static final String ALTITUDE_TYPE = "REAL";
		static final String BEARING_TYPE = "REAL";
		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	}
	
	/**
	 * @author xuzhuowei
	 *Columns from media table.
	 */
	public static class MediaColumns
	{
		public static final String TRACK = "track";
		public static final String SEGMENT = "segment";
		public static final String WAYPOINT = "waypoint";
		public static final String URI = "uri";
		public static final String KIND = "kind";
		public static final String CREATE_TIME = "createTime";
		
		static final String TRACK_TYPE = "INTEGER NOT NULL";
		static final String SEGMENT_TYPE = "INTEGER NOT NULL";
		static final String WAYPOINT_TYPE = "INTEGER NOT NULL";
		static final String URI_TYPE = "TEXT";
		static final String KIND_TYPE = "TEXT";
		static final String CREATE_TIME_TYPE = "INTEGER NOT NULL";
		static final String _ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	}
	
	
}

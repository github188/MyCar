package com.cnlaunch.mycar.gps.database;

import java.util.Date;

import com.cnlaunch.mycar.gps.database.GpsTrack.Media;
import com.cnlaunch.mycar.gps.database.GpsTrack.MediaColumns;
import com.cnlaunch.mycar.gps.database.GpsTrack.Segments;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.TracksColumns;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import com.cnlaunch.mycar.gps.database.GpsTrack.WaypointsColumns;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

public class GpsDatabaseHelper extends SQLiteOpenHelper {

	private static final void d(String log) {
		Log.e("MyCar.Gps", log);
	}

	private Context mContext;
	private static String mDatebaseName = "gps_anonymous";
	private static int mVersion = 1;

	public GpsDatabaseHelper(Context context) {
		// Change the value of 'mDatebaseName' for different user
		super(context, mDatebaseName, null, mVersion);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Waypoints.SQL_CREATE_TABLE);
		db.execSQL(Segments.SQL_CREATE_TABLE);
		db.execSQL(Tracks.SQL_CREATE_TABLE);
		db.execSQL(Media.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing to do for the present.
	}

	/**
	 * Tidy the database.
	 */
	public void vacuum() {
		new Thread() {
			@Override
			public void run() {
				SQLiteDatabase sqldb = getWritableDatabase();
				sqldb.execSQL("VACUUM");
			}
		}.start();
	}

	int bulkInsertWaypoint(long trackId, long segmentId,
			ContentValues[] valuesArray) {
		if (trackId < 0 || segmentId < 0) {
			throw new IllegalArgumentException(
					"Track and segments may not the less the 0.");
		}
		int inserted = 0;
		SQLiteDatabase sqldb = getWritableDatabase();
		sqldb.beginTransaction();
		try {
			for (ContentValues args : valuesArray) {
				args.put(Waypoints.TRACK, trackId);
				args.put(Waypoints.SEGMENT, segmentId);

				long id = sqldb.insert(Waypoints.TABLE, null, args);
				if (id >= 0) {
					inserted++;
				}
			}
			sqldb.setTransactionSuccessful();
		} finally {
			if (sqldb.inTransaction()) {
				sqldb.endTransaction();
			}
		}

		return inserted;
	}

	long insertWaypoint(long trackId, long segmentId, Location location) {
		if (trackId < 0 || segmentId < 0) {
			throw new IllegalArgumentException(
					"Track and segments may not the less then 0.");
		}

		SQLiteDatabase sqldb = getWritableDatabase();

		ContentValues args = new ContentValues();
		args.put(WaypointsColumns.TRACK, trackId);
		args.put(WaypointsColumns.SEGMENT, segmentId);
		args.put(WaypointsColumns.TIME, location.getTime());
		args.put(WaypointsColumns.LATITUDE, location.getLatitude());
		args.put(WaypointsColumns.LONGITUDE, location.getLongitude());
		args.put(WaypointsColumns.SPEED, location.getSpeed());
		args.put(WaypointsColumns.ACCURACY, location.getAccuracy());
		args.put(WaypointsColumns.ALTITUDE, location.getAltitude());
		args.put(WaypointsColumns.BEARING, location.getBearing());

		long waypointId = sqldb.insert(Waypoints.TABLE, null, args);

		ContentResolver resolver = this.mContext.getContentResolver();
		Uri notifyUri = Uri.withAppendedPath(Tracks.CONTENT_URI, trackId
				+ "/segments/" + segmentId + "/waypoints");
		resolver.notifyChange(notifyUri, null);

		// Log.d( TAG, "Waypoint stored: "+notifyUri);
		return waypointId;
	}

	/**
	 * Insert a URI for a given waypoint/segment/track in the media table
	 * 
	 * @param trackId
	 * @param segmentId
	 * @param waypointId
	 * @param mediaUri
	 * @return
	 */
	long insertMedia(long trackId, long segmentId, long waypointId,
			String mediaUri) {
		if (trackId < 0 || segmentId < 0 || waypointId < 0) {
			throw new IllegalArgumentException(
					"Track, segments and waypoint may not the less then 0.");
		}
		SQLiteDatabase sqldb = getWritableDatabase();
		long currentTime = new Date().getTime();

		ContentValues args = new ContentValues();
		args.put(MediaColumns.TRACK, trackId);
		args.put(MediaColumns.SEGMENT, segmentId);
		args.put(MediaColumns.WAYPOINT, waypointId);
		args.put(MediaColumns.URI, mediaUri);
		args.put(
				MediaColumns.KIND,
				mediaUri.substring(mediaUri.lastIndexOf(".") + 1,
						mediaUri.length()));
		args.put(MediaColumns.CREATE_TIME, currentTime);

		// Log.d( TAG, "Media stored in the datebase: "+mediaUri );

		long mediaId = sqldb.insert(Media.TABLE, null, args);

		ContentResolver resolver = this.mContext.getContentResolver();
		Uri notifyUri = Uri.withAppendedPath(Tracks.CONTENT_URI, trackId + "/"
				+ Segments.TABLE + "/" + segmentId + "/" + Waypoints.TABLE
				+ "/" + waypointId + "/" + Media.TABLE);
		resolver.notifyChange(notifyUri, null);
		// Log.d( TAG, "Notify: "+notifyUri );
		resolver.notifyChange(Media.CONTENT_URI, null);
		// Log.d( TAG, "Notify: "+Media.CONTENT_URI );

		return mediaId;
	}

	int deleteTrack(long trackId) {
		SQLiteDatabase sqldb = getWritableDatabase();
		int affected = 0;
		Cursor cursor = null;
		long segmentId = -1;

		try {
			sqldb.beginTransaction();
			// Iterate on each segement to delete each
			cursor = sqldb.query(Segments.TABLE, new String[]{Segments._ID},
					Segments.TRACK + "= ?",
					new String[]{String.valueOf(trackId)}, null, null, null,
					null);
			if (cursor.moveToFirst()) {
				do {
					segmentId = cursor.getLong(0);
					affected += deleteSegment(sqldb, trackId, segmentId);
				} while (cursor.moveToNext());
			} else {
				d("Did not find the last active segment");
			}
			// Delete the track
			affected += sqldb.delete(Tracks.TABLE, Tracks._ID + "= ?",
					new String[]{String.valueOf(trackId)});

			sqldb.setTransactionSuccessful();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (sqldb.inTransaction()) {
				sqldb.endTransaction();
			}
		}

		ContentResolver resolver = this.mContext.getContentResolver();
		resolver.notifyChange(Tracks.CONTENT_URI, null);
		resolver.notifyChange(
				ContentUris.withAppendedId(Tracks.CONTENT_URI, trackId), null);

		return affected;
	}

	/**
	 * @param mediaId
	 * @return
	 */
	int deleteMedia(long mediaId) {
		SQLiteDatabase sqldb = getWritableDatabase();

		Cursor cursor = null;
		long trackId = -1;
		long segmentId = -1;
		long waypointId = -1;
		try {
			cursor = sqldb.query(Media.TABLE, new String[]{Media.TRACK,
					Media.SEGMENT, Media.WAYPOINT}, Media._ID + "= ?",
					new String[]{String.valueOf(mediaId)}, null, null, null,
					null);
			if (cursor.moveToFirst()) {
				trackId = cursor.getLong(0);
				segmentId = cursor.getLong(0);
				waypointId = cursor.getLong(0);
			} else {
				d("Did not find the media element to delete");
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		int affected = sqldb.delete(Media.TABLE, Media._ID + "= ?",
				new String[]{String.valueOf(mediaId)});

		ContentResolver resolver = this.mContext.getContentResolver();
		Uri notifyUri = Uri.withAppendedPath(Tracks.CONTENT_URI, trackId
				+ "/segments/" + segmentId + "/waypoints/" + waypointId
				+ "/media");
		resolver.notifyChange(notifyUri, null);
		notifyUri = Uri.withAppendedPath(Tracks.CONTENT_URI, trackId
				+ "/segments/" + segmentId + "/media");
		resolver.notifyChange(notifyUri, null);
		notifyUri = Uri
				.withAppendedPath(Tracks.CONTENT_URI, trackId + "/media");
		resolver.notifyChange(notifyUri, null);
		resolver.notifyChange(
				ContentUris.withAppendedId(Media.CONTENT_URI, mediaId), null);

		return affected;
	}

	int deleteSegment(SQLiteDatabase sqldb, long trackId, long segmentId) {
		int affected = sqldb.delete(Segments.TABLE, Segments._ID + "= ?",
				new String[]{String.valueOf(segmentId)});

		// Delete all waypoints from segments
		affected += sqldb.delete(Waypoints.TABLE, Waypoints.SEGMENT + "= ?",
				new String[]{String.valueOf(segmentId)});
		// Delete all media from segment
		affected += sqldb.delete(Media.TABLE, Media.TRACK + "= ? AND "
				+ Media.SEGMENT + "= ?", new String[]{String.valueOf(trackId),
				String.valueOf(segmentId)});

		ContentResolver resolver = this.mContext.getContentResolver();
		resolver.notifyChange(
				Uri.withAppendedPath(Tracks.CONTENT_URI, trackId + "/segments/"
						+ segmentId), null);
		resolver.notifyChange(
				Uri.withAppendedPath(Tracks.CONTENT_URI, trackId + "/segments"),
				null);

		return affected;
	}

	int updateTrack(long trackId, String name) {
		int updates;
		String whereclause = Tracks._ID + " = " + trackId;
		ContentValues args = new ContentValues();
		args.put(Tracks.NAME, name);

		// Execute the query.
		SQLiteDatabase sqldb = getWritableDatabase();
		updates = sqldb.update(Tracks.TABLE, args, whereclause, null);

		ContentResolver resolver = this.mContext.getContentResolver();
		Uri notifyUri = ContentUris.withAppendedId(Tracks.CONTENT_URI, trackId);
		resolver.notifyChange(notifyUri, null);

		return updates;
	}

	int updateWaypoint(long waypointId, long offsetX, long offsetY,
			int offsetGetted) {
		int updates;
		String whereclause = Waypoints._ID + " = " + waypointId;
		ContentValues args = new ContentValues();
		args.put(Waypoints.OFFSET_X, offsetX);
		args.put(Waypoints.OFFSET_Y, offsetY);
		args.put(Waypoints.OFFSET_GETTED, offsetGetted);
		SQLiteDatabase sqldb = getWritableDatabase();
		updates = sqldb.update(Waypoints.TABLE, args, whereclause, null);

		return updates;
	}

	long toNextTrack(String name) {
		long currentTime = new Date().getTime();
		ContentValues args = new ContentValues();
		args.put(TracksColumns.NAME, name);
		args.put(TracksColumns.CREATE_TIME, currentTime);

		SQLiteDatabase sqldb = getWritableDatabase();
		long trackId = sqldb.insert(Tracks.TABLE, null, args);

		ContentResolver resolver = this.mContext.getContentResolver();
		resolver.notifyChange(Tracks.CONTENT_URI, null);

		return trackId;
	}

	long toNextSegment(long trackId) {
		SQLiteDatabase sqldb = getWritableDatabase();

		ContentValues args = new ContentValues();
		args.put(Segments.TRACK, trackId);
		long segmentId = sqldb.insert(Segments.TABLE, null, args);

		ContentResolver resolver = this.mContext.getContentResolver();
		resolver.notifyChange(
				Uri.withAppendedPath(Tracks.CONTENT_URI, trackId + "/segments"),
				null);

		return segmentId;
	}

}

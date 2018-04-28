package com.cnlaunch.mycar.gps.database;

import java.util.List;

import com.cnlaunch.mycar.gps.database.GpsTrack.Media;
import com.cnlaunch.mycar.gps.database.GpsTrack.Segments;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

public class GpsTrackProvider extends ContentProvider {
	private static final void d(String log) {
		Log.e("MyCar.Gps", log);
	}

	private static final int TRACKS = 1;
	private static final int TRACK_ID = 2;
	private static final int TRACK_MEDIA = 3;
	private static final int TRACK_WAYPOINTS = 4;
	private static final int SEGMENTS = 5;
	private static final int SEGMENT_ID = 6;
	private static final int SEGMENT_MEDIA = 7;
	private static final int WAYPOINTS = 8;
	private static final int WAYPOINT_ID = 9;
	private static final int WAYPOINT_MEDIA = 10;
	private static final int MEDIA = 11;
	private static final int MEDIA_ID = 12;
	private static final int WAYPOINTS_ALL_ID = 13;
	private static final int WAYPOINTS_ID_NOT_GET_OFFSET = 14;
	private static final int WAYPOINTS_ALL = 15;

	private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks", TRACKS);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#", TRACK_ID);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#/media", TRACK_MEDIA);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#/waypoints",
				TRACK_WAYPOINTS);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#/segments", SEGMENTS);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#/segments/#",
				SEGMENT_ID);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#/segments/#/media",
				SEGMENT_MEDIA);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "tracks/#/segments/#/waypoints",
				WAYPOINTS);
		sUriMatcher.addURI(GpsTrack.AUTHORITY,
				"tracks/#/segments/#/waypoints/#", WAYPOINT_ID);
		sUriMatcher.addURI(GpsTrack.AUTHORITY,
				"tracks/#/segments/#/waypoints/#/media", WAYPOINT_MEDIA);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "media", MEDIA);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "media/#", MEDIA_ID);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "waypoints/#", WAYPOINTS_ALL_ID);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "waypoints/"
				+ Waypoints.NOT_GET_OFFSET, WAYPOINTS_ID_NOT_GET_OFFSET);
		sUriMatcher.addURI(GpsTrack.AUTHORITY, "waypoints", WAYPOINTS_ALL);
	}

	private GpsDatabaseHelper mDbHelper;

	@Override
	public boolean onCreate() {
		if (mDbHelper == null) {
			mDbHelper = new GpsDatabaseHelper(getContext());
		}

		return true;

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = sUriMatcher.match(uri);
		int affected = 0;
		switch (match) {
		case TRACK_ID:
			affected = this.mDbHelper.deleteTrack(new Long(uri
					.getLastPathSegment()).longValue());
			break;
		case MEDIA_ID:
			affected = this.mDbHelper.deleteMedia(new Long(uri
					.getLastPathSegment()).longValue());
			break;
		default:
			affected = 0;
			break;
		}
		return affected;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		String mime = null;
		switch (match) {
		case TRACKS:
			mime = Tracks.CONTENT_TYPE;
			break;
		case TRACK_ID:
			mime = Tracks.CONTENT_ITEM_TYPE;
			break;
		case SEGMENTS:
			mime = Segments.CONTENT_TYPE;
			break;
		case SEGMENT_ID:
			mime = Segments.CONTENT_ITEM_TYPE;
			break;
		case WAYPOINTS_ALL:
		case WAYPOINTS:
			mime = Waypoints.CONTENT_TYPE;
			break;
		case WAYPOINTS_ID_NOT_GET_OFFSET:
		case WAYPOINTS_ALL_ID:
		case WAYPOINT_ID:
			mime = Waypoints.CONTENT_ITEM_TYPE;
			break;
		case MEDIA_ID:
		case TRACK_MEDIA:
		case SEGMENT_MEDIA:
		case WAYPOINT_MEDIA:
			mime = Media.CONTENT_ITEM_TYPE;
			break;
		case MEDIA:
			mime = Media.CONTENT_TYPE;
			break;
		case UriMatcher.NO_MATCH:
		default:
			d("There is not MIME type defined for URI " + uri);
			break;
		}
		return mime;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri insertedUri = null;
		int match = sUriMatcher.match(uri);
		List<String> pathSegments = null;
		long trackId = -1;
		long segmentId = -1;
		long waypointId = -1;
		long mediaId = -1;
		switch (match) {
		case WAYPOINTS:
			pathSegments = uri.getPathSegments();
			trackId = Long.parseLong(pathSegments.get(1));
			segmentId = Long.parseLong(pathSegments.get(3));
			Location loc = new Location("");
			Double latitude = values.getAsDouble(Waypoints.LATITUDE);
			Double longitude = values.getAsDouble(Waypoints.LONGITUDE);
			Long time = values.getAsLong(Waypoints.TIME);
			Float speed = values.getAsFloat(Waypoints.SPEED);
			if (time == null) {
				time = System.currentTimeMillis();
			}
			if (speed == null) {
				speed = 0f;
			}
			loc.setLatitude(latitude);
			loc.setLongitude(longitude);
			loc.setTime(time);
			loc.setSpeed(speed);

			if (values.containsKey(Waypoints.ACCURACY)) {
				loc.setAccuracy(values.getAsFloat(Waypoints.ACCURACY));
			}
			if (values.containsKey(Waypoints.ALTITUDE)) {
				loc.setAltitude(values.getAsDouble(Waypoints.ALTITUDE));

			}
			if (values.containsKey(Waypoints.BEARING)) {
				loc.setBearing(values.getAsFloat(Waypoints.BEARING));
			}
			waypointId = this.mDbHelper.insertWaypoint(trackId, segmentId, loc);
			// Log.d( TAG,
			// "Have inserted to segment "+segmentId+" with waypoint "+waypointId
			// );
			insertedUri = ContentUris.withAppendedId(uri, waypointId);
			break;
		case WAYPOINT_MEDIA:
			pathSegments = uri.getPathSegments();
			trackId = Long.parseLong(pathSegments.get(1));
			segmentId = Long.parseLong(pathSegments.get(3));
			waypointId = Long.parseLong(pathSegments.get(5));
			String mediaUri = values.getAsString(Media.URI);
			mediaId = this.mDbHelper.insertMedia(trackId, segmentId,
					waypointId, mediaUri);
			insertedUri = ContentUris
					.withAppendedId(Media.CONTENT_URI, mediaId);
			break;
		case SEGMENTS:
			pathSegments = uri.getPathSegments();
			trackId = Integer.parseInt(pathSegments.get(1));
			segmentId = this.mDbHelper.toNextSegment(trackId);
			insertedUri = ContentUris.withAppendedId(uri, segmentId);
			break;
		case TRACKS:
			String name = (values == null) ? "" : values
					.getAsString(Tracks.NAME);
			trackId = this.mDbHelper.toNextTrack(name);
			insertedUri = ContentUris.withAppendedId(uri, trackId);
			break;

		default:
			d("Unable to match the insert URI: " + uri.toString());
			insertedUri = null;
			break;
		}
		return insertedUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sUriMatcher.match(uri);

		String tableName = null;
		String whereclause = null;
		String sortorder = sortOrder;
		List<String> pathSegments = uri.getPathSegments();
		switch (match) {
		case TRACKS:
			tableName = Tracks.TABLE;
			break;
		case TRACK_ID:
			tableName = Tracks.TABLE;
			whereclause = Tracks._ID + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case SEGMENTS:
			tableName = Segments.TABLE;
			whereclause = Segments.TRACK + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case SEGMENT_ID:
			tableName = Segments.TABLE;
			whereclause = Segments.TRACK + " = "
					+ new Long(pathSegments.get(1)).longValue() + " and "
					+ Segments._ID + " = "
					+ new Long(pathSegments.get(3)).longValue();
			break;
		case WAYPOINTS_ID_NOT_GET_OFFSET:
			tableName = Waypoints.TABLE;
			whereclause = Waypoints.OFFSET_GETTED + " is null or "
					+ Waypoints.OFFSET_GETTED + " = "
					+ Waypoints.OFFSET_GETTED_NOT_YET + " or "
					+ Waypoints.OFFSET_GETTED + " = ''";

			break;
		case WAYPOINTS_ALL_ID:
			tableName = Waypoints.TABLE;
			whereclause = Waypoints._ID + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case WAYPOINTS_ALL:
			tableName = Waypoints.TABLE;
			break;
		case WAYPOINTS:
			tableName = Waypoints.TABLE;
			whereclause = Waypoints.SEGMENT + " = "
					+ new Long(pathSegments.get(3)).longValue();
			break;
		case WAYPOINT_ID:
			tableName = Waypoints.TABLE;
			whereclause = Waypoints.SEGMENT + " = "
					+ new Long(pathSegments.get(3)).longValue() + " and "
					+ Waypoints._ID + " = "
					+ new Long(pathSegments.get(5)).longValue();
			break;
		case TRACK_WAYPOINTS:
			tableName = Waypoints.TABLE + " INNER JOIN " + Segments.TABLE
					+ " ON " + Segments.TABLE + "." + Segments._ID + "=="
					+ Waypoints.SEGMENT;
			whereclause = Waypoints.TABLE + "." + Segments.TRACK + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case TRACK_MEDIA:
			tableName = Media.TABLE + " left join " + Waypoints.TABLE + " on "
					+ Media.TABLE + "." + Media.WAYPOINT + "=="
					+ Waypoints.TABLE + "." + Waypoints._ID;
			whereclause = Media.TABLE + "." + Media.TRACK + " = "
					+ new Long(pathSegments.get(1)).longValue();
			break;
		case SEGMENT_MEDIA:
			tableName = Media.TABLE + " left join " + Waypoints.TABLE + " on "
					+ Media.TABLE + "." + Media.WAYPOINT + "=="
					+ Waypoints.TABLE + "." + Waypoints._ID;
			whereclause = Media.TABLE + "." + Media.TRACK + " = "
					+ new Long(pathSegments.get(1)).longValue() + " and "
					+ Media.TABLE + "." + Media.SEGMENT + " = "
					+ new Long(pathSegments.get(3)).longValue();
			break;
		case MEDIA:
			tableName = Media.TABLE;
			break;
		case WAYPOINT_MEDIA:
			tableName = Media.TABLE;
			whereclause = Media.TRACK + " = "
					+ new Long(pathSegments.get(1)).longValue() + " and "
					+ Media.SEGMENT + " = "
					+ new Long(pathSegments.get(3)).longValue() + " and "
					+ Media.WAYPOINT + " = "
					+ new Long(pathSegments.get(5)).longValue();
			break;
		default:
			d("Unable to come to an action in the query uri: " + uri.toString());
			return null;
		}

		// SQLiteQueryBuilder is a helper class that creates the
		// proper SQL syntax for us.
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

		// Set the table we're querying.
		qBuilder.setTables(tableName);

		// If the query ends in a specific record number, we're
		// being asked for a specific record, so set the
		// WHERE clause in our query.
		if (whereclause != null) {
			qBuilder.appendWhere(whereclause);
		}

		// Make the query.
		SQLiteDatabase sqlDb = this.mDbHelper.getWritableDatabase();
		Cursor c = qBuilder.query(sqlDb, projection, selection, selectionArgs,
				null, null, sortorder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues givenValues, String selection,
			String[] selectionArgs) {
		int updates = -1;

		int match = sUriMatcher.match(uri);

		switch (match) {
		case TRACK_ID:

			long trackId = new Long(uri.getLastPathSegment()).longValue();
			String name = givenValues.getAsString(Tracks.NAME);
			updates = mDbHelper.updateTrack(trackId, name);
			break;
		case WAYPOINTS_ALL_ID:
			long waypointId = new Long(uri.getLastPathSegment()).longValue();
			long offsetX = givenValues.getAsLong(Waypoints.OFFSET_X);
			long offsetY = givenValues.getAsLong(Waypoints.OFFSET_Y);
			int offsetGetted = givenValues
					.getAsInteger(Waypoints.OFFSET_GETTED);
			updates = mDbHelper.updateWaypoint(waypointId, offsetX, offsetY,
					offsetGetted);

			break;

		default:
			d("Unable to come to an action in the query uri" + uri.toString());
			return -1;
		}
		return updates;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] valuesArray) {
		int inserted = 0;
		int match = sUriMatcher.match(uri);
		switch (match) {
		case WAYPOINTS:
			List<String> pathSegments = uri.getPathSegments();
			int trackId = Integer.parseInt(pathSegments.get(1));
			int segmentId = Integer.parseInt(pathSegments.get(3));
			inserted = this.mDbHelper.bulkInsertWaypoint(trackId, segmentId,
					valuesArray);
			break;
		default:
			inserted = super.bulkInsert(uri, valuesArray);
			break;
		}
		return inserted;
	}

}

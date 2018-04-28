package com.cnlaunch.mycar.gps.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xmlpull.v1.XmlSerializer;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.gps.GpsConstants;
import com.cnlaunch.mycar.gps.database.GpsTrack.Segments;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

public class GpxCreator {
	public static final String NS_SCHEMA = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String NS_GPX_11 = "http://www.topografix.com/GPX/1/1";
	public static final String NS_GPX_10 = "http://www.topografix.com/GPX/1/0";
	public static final String NS_OGT_10 = "http://gpstracker.android.sogeti.nl/GPX/1/0";
	public static final SimpleDateFormat ZULU_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "gps.GPXCreator";
	private String mName = "Untitled";
	private long mTime = 0;

	public GpxCreator(Context context) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();
	}

	public File export(Uri trackUri) {
		return this.export(trackUri,
				new File(Env.getAppRootDirInSdcard(),GpsConstants.DIR_NAME_GPX));
	}

	public File export(Uri trackUri, File outputFileDir) {
		if (!outputFileDir.exists()) {
			if (!outputFileDir.mkdirs()) {
				return null;
			}
		}

		FileOutputStream fos = null;
		BufferedOutputStream buf = null;
		getTrackInfo(trackUri);

		File gpxFile = new File(outputFileDir, mName.replace(":", "").replace(" ", "") + ".gpx");

		try {
			XmlSerializer serializer = Xml.newSerializer();

			fos = new FileOutputStream(gpxFile);
			buf = new BufferedOutputStream(fos, 8 * 8192);
			serializer.setOutput(buf, "UTF-8");

			serializeTrack(trackUri, serializer);

			buf.close();
			buf = null;
			fos.close();
			fos = null;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (buf != null) {
				try {
					buf.close();
				} catch (IOException e) {
					Log.e(TAG,
							"Failed to close buf after completion, ignoring.",
							e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Log.e(TAG,
							"Failed to close fos after completion, ignoring.",
							e);
				}
			}
		}

		return gpxFile;
	}

	private void serializeTrack(Uri trackUri, XmlSerializer serializer)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startDocument("UTF-8", true);
		serializer.setPrefix("xsi", NS_SCHEMA);
		serializer.setPrefix("gpx10", NS_GPX_10);
		serializer.setPrefix("ogt10", NS_OGT_10);
		serializer.text("\n");
		serializer.startTag("", "gpx");
		serializer.attribute(null, "version", "1.1");
		serializer.attribute(null, "creator", "cnlaunch");
		serializer.attribute(NS_SCHEMA, "schemaLocation", NS_GPX_11
				+ " http://www.topografix.com/gpx/1/1/gpx.xsd");
		serializer.attribute(null, "xmlns", NS_GPX_11);

		serializeTrackHeader(serializer, trackUri);

		serializer.text("\n");
		serializer.startTag("", "trk");
		serializer.text("\n");
		serializer.startTag("", "name");
		serializer.text(mName);
		serializer.endTag("", "name");

		serializeSegments(serializer,
				Uri.withAppendedPath(trackUri, "segments"));

		serializer.text("\n");
		serializer.endTag("", "trk");
		serializer.text("\n");
		serializer.endTag("", "gpx");
		serializer.endDocument();

	}

	private void serializeSegments(XmlSerializer serializer, Uri segments)
			throws IllegalArgumentException, IllegalStateException, IOException {
		Cursor segmentCursor = null;
		ContentResolver resolver = mContext.getContentResolver();
		try {
			segmentCursor = resolver.query(segments,
					new String[] { Segments._ID }, null, null, null);
			if (segmentCursor.moveToFirst()) {
				do {
					Uri waypoints = Uri.withAppendedPath(segments,
							segmentCursor.getLong(0) + "/waypoints");
					serializer.text("\n");
					serializer.startTag("", "trkseg");
					serializeWaypoints(serializer, waypoints);
					serializer.text("\n");
					serializer.endTag("", "trkseg");
				} while (segmentCursor.moveToNext());
			}
		} finally {
			if (segmentCursor != null) {
				segmentCursor.close();
			}
		}

	}

	private void serializeWaypoints(XmlSerializer serializer, Uri waypoints)
			throws IllegalArgumentException, IllegalStateException, IOException {
		Cursor waypointsCursor = null;
		ContentResolver resolver = mContext.getContentResolver();
		try {
			waypointsCursor = resolver.query(waypoints, new String[] {
					Waypoints.LONGITUDE, Waypoints.LATITUDE, Waypoints.TIME,
					Waypoints.ALTITUDE, Waypoints._ID, Waypoints.SPEED,
					Waypoints.ACCURACY, Waypoints.BEARING }, null, null, null);
			if (waypointsCursor.moveToFirst()) {
				do {

					serializer.text("\n");
					serializer.startTag("", "trkpt");
					serializer.attribute(null, "lat",
							Double.toString(waypointsCursor.getDouble(1)));
					serializer.attribute(null, "lon",
							Double.toString(waypointsCursor.getDouble(0)));
					serializer.text("\n");
					serializer.startTag("", "ele");
					serializer.text(Double.toString(waypointsCursor
							.getDouble(3)));
					serializer.endTag("", "ele");
					serializer.text("\n");
					serializer.startTag("", "time");
					Date time = new Date(waypointsCursor.getLong(2));
					serializer.text(ZULU_DATE_FORMAT.format(time));
					serializer.endTag("", "time");
					serializer.text("\n");
					serializer.startTag("", "extensions");

					double speed = waypointsCursor.getDouble(5);
					double accuracy = waypointsCursor.getDouble(6);
					double bearing = waypointsCursor.getDouble(7);
					if (speed > 0.0) {
						quickTag(serializer, NS_GPX_10, "speed",
								Double.toString(speed));
					}
					if (accuracy > 0.0) {
						quickTag(serializer, NS_OGT_10, "accuracy",
								Double.toString(accuracy));
					}
					if (bearing != 0.0) {
						quickTag(serializer, NS_GPX_10, "course",
								Double.toString(bearing));
					}
					serializer.endTag("", "extensions");
					serializer.text("\n");
					serializer.endTag("", "trkpt");
				} while (waypointsCursor.moveToNext());
			}
		} finally {
			if (waypointsCursor != null) {
				waypointsCursor.close();
			}
		}
	}

	private void quickTag(XmlSerializer serializer, String ns, String tag,
			String content) throws IllegalArgumentException,
			IllegalStateException, IOException {
		serializer.text("\n");
		serializer.startTag(ns, tag);
		serializer.text(content);
		serializer.endTag(ns, tag);
	}

	private void serializeTrackHeader(XmlSerializer serializer, Uri trackUri) throws IllegalArgumentException, IllegalStateException, IOException {

		serializer.text("\n");
		serializer.startTag("", "metadata");
		serializer.text("\n");
		serializer.startTag("", "time");
		Date time = new Date(mTime);
		serializer.text(ZULU_DATE_FORMAT.format(time));
		serializer.endTag("", "time");
		serializer.text("\n");
		serializer.endTag("", "metadata");

	}

	private void getTrackInfo(Uri trackUri) {
		Cursor trackCursor = null;
		try {
			trackCursor = mContentResolver.query(trackUri, new String[] {
					Tracks._ID, Tracks.NAME, Tracks.CREATE_TIME }, null, null,
					null);
			if (trackCursor != null) {
				if (trackCursor.moveToFirst()) {
					mName = trackCursor.getString(1);
					mTime = trackCursor.getLong(2);
				}
			}
		} finally {
			trackCursor.close();
		}
	}

}

package com.cnlaunch.mycar.gps.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.gps.database.GpsTrack.Segments;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class GpxParser {
	private Context mContext;
	private ContentResolver mContentResolver;
	private String TAG = "gps.GPXParser";

	public GpxParser(Context context) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();
	}

	public Uri importUri(Uri importGpxFileUri) {
		Uri ret = null;
		String trackName = null;
		InputStream fis = null;

		// 取文件名为轨迹名
		if (importGpxFileUri.getScheme().equals("file")) {
			trackName = importGpxFileUri.getLastPathSegment();
		}

		try {
			fis = mContentResolver.openInputStream(importGpxFileUri);
			ret = improtTrack(fis, trackName);
		} catch (IOException e) {
			Log.e(TAG,
					mContext.getResources().getString(
							R.string.error_gps_improt_io));
		}

		return ret;
	}

	private Uri improtTrack(InputStream fis, String trackName) {
		// GPX 文件格式参考 http://zh.wikipedia.org/wiki/Gpx
		Uri trackUri = null;
		Uri segmentUri = null;
		ContentValues lastPosition = null;
		Vector<ContentValues> bulk = new Vector<ContentValues>();
		Long improtDate = new Long(new Date().getTime());

		boolean isName = false;
		boolean isTrk = false;
		boolean isTrkseg = false;
		boolean isTrkpt = false;
		boolean isEle = false;
		boolean isTime = false;
		boolean isCourse = false;
		boolean isAccuracy = false;
		boolean isSpeed = false;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xmlParser = factory.newPullParser();
			xmlParser.setInput(fis, "utf-8");

			int t = xmlParser.getEventType();
			while (t != XmlPullParser.END_DOCUMENT) {
				// 如果找到Name标记，则使用该标记的值作为轨迹的名称
				if (t == XmlPullParser.START_TAG) {
					if (xmlParser.getName().equals(Gpx.Element.NAME)) {
						isName = true;
						Log.e(TAG,"isName:" + isName);
					} else {
						// 如果未找到Name标记，则使用轨迹文件名为 轨迹的名称
						ContentValues trackContent = new ContentValues();
						trackContent.put(Tracks.NAME, trackName);
						
						final String tag = xmlParser.getName();
						if (tag.equals(Gpx.Element.TRK)
								&& trackUri == null) {
							trackUri = startTrack(trackContent);
						}else if(tag.equals(Gpx.Element.TRKSEG)){
							segmentUri = startSegment(trackUri);
						}else if(tag.equals(Gpx.Element.TRKPT)){
							lastPosition = new ContentValues();
							final int ATTRIBUTE_COUNT = xmlParser.getAttributeCount();
							String attributeName;
							for(int i = 0; i< ATTRIBUTE_COUNT;i++){
								attributeName = xmlParser.getAttributeName(i);
								if(attributeName.equals(Gpx.Atribute.LAT)){
									lastPosition.put(Waypoints.LATITUDE, new Double(xmlParser.getAttributeValue(i)));
								}else if(attributeName.equals(Gpx.Atribute.LON)){
									lastPosition.put(Waypoints.LONGITUDE, new Double(xmlParser.getAttributeValue(i)));
								}
							}
							
						}else if(tag.equals(Gpx.Element.ELE)){
							isEle = true;
						}else if(tag.equals(Gpx.Element.TIME)){
							isTime = true;
						}else if(tag.equals(Gpx.Element.COURSE)){
							isCourse = true;
						}else if(tag.equals(Gpx.Element.ACCURACY)){
							isAccuracy = true;
						}else if(tag.equals(Gpx.Element.SPEED)){
							isSpeed = true;
						}

					}
				} else if (t == XmlPullParser.END_TAG) {
					final String tag = xmlParser.getName();
					if (tag.equals(Gpx.Element.NAME)) {
						isName = false;
					} else if (tag.equals(Gpx.Element.TRK)) {
						isTrk = false;
					} else if (tag.equals(Gpx.Element.TRKSEG)) {
						isTrkseg = false;
						if (segmentUri == null) {
							segmentUri = startSegment(trackUri);
						}
						mContentResolver.bulkInsert(Uri.withAppendedPath(
								segmentUri, Waypoints.TABLE), bulk
								.toArray(new ContentValues[bulk.size()]));
						bulk.clear();
					} else if (tag.equals(Gpx.Element.TRKPT)) {
						isTrkpt = false;
						if (lastPosition != null) {
							if (!lastPosition.containsKey(Waypoints.TIME)) {
								lastPosition.put(Waypoints.TIME, improtDate);
							}
							if (!lastPosition.containsKey(Waypoints.SPEED)) {
								lastPosition.put(Waypoints.SPEED, 0);
							}
							bulk.add(lastPosition);
						}
						lastPosition = null;
					} else if (tag.equals(Gpx.Element.ELE)) {
						isEle = false;
					} else if (tag.equals(Gpx.Element.TIME)) {
						isTime = false;
					} else if (tag.equals(Gpx.Element.COURSE)) {
						isCourse = false;
					} else if (tag.equals(Gpx.Element.ACCURACY)) {
						isAccuracy = false;
					} else if (tag.equals(Gpx.Element.SPEED)) {
						isSpeed = false;
					}
				} else if(t == XmlPullParser.TEXT){
					String text = xmlParser.getText();
					if(isName){
						//TODO
						Log.e(TAG,text);
						ContentValues nameValues = new ContentValues();
						nameValues.put(Tracks.NAME,text);
						if(trackUri == null){
							trackUri = startTrack(nameValues);
						}else{
							mContentResolver.update(trackUri, nameValues, null, null);
						}
						
					}else if(lastPosition !=null && isSpeed){
						lastPosition.put(Waypoints.SPEED, Double.parseDouble(text));
					}else if(lastPosition !=null && isAccuracy){
						lastPosition.put(Waypoints.ACCURACY, Double.parseDouble(text));
					}else if(lastPosition !=null && isCourse){
						lastPosition.put(Waypoints.BEARING, Double.parseDouble(text));
					}else if(lastPosition !=null && isEle){
						lastPosition.put(Waypoints.ALTITUDE, Double.parseDouble(text));
					}else if(lastPosition !=null && isTime){
						lastPosition.put(Waypoints.TIME, parseXmlDateTime(text));
					}
				}
				t = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG,mContext.getResources().getString(R.string.error_gps_improt_xml_parse));
		} catch (IOException e) {
			Log.e(TAG,mContext.getResources().getString(R.string.error_gps_improt_xml_parse_io));
		} catch (ParseException e) {
			Log.e(TAG,mContext.getResources().getString(R.string.error_gps_improt_xml_parse_time_parse));
		}

		return trackUri;
	}

	private Uri startSegment(Uri trackUri) {
		if (trackUri == null) {
			trackUri = startTrack(new ContentValues());
		}
		return mContentResolver.insert(
				Uri.withAppendedPath(trackUri, Segments.TABLE),
				new ContentValues());
	}

	private Uri startTrack(ContentValues trackContent) {
		return mContentResolver.insert(Tracks.CONTENT_URI, trackContent);
	}
	
	private Long parseXmlDateTime(String text) throws ParseException {
	      if(text==null)
	      {
	         throw new ParseException("Unable to parse dateTime "+text+" of length ", 0);
	      }
	      
	      final SimpleDateFormat ZULU_DATE_FORMAT    = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	      final SimpleDateFormat ZULU_DATE_FORMAT_MS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	      final SimpleDateFormat ZULU_DATE_FORMAT_BC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
	      
	      Long dateTime = null;
	      int length = text.length();
	      switch (length)
	      {
	         case 20:
	            dateTime = new Long(ZULU_DATE_FORMAT.parse(text).getTime());
	            break;
	         case 23:
	            dateTime = new Long(ZULU_DATE_FORMAT_BC.parse(text).getTime());
	            break;
	         case 24:
	            dateTime = new Long(ZULU_DATE_FORMAT_MS.parse(text).getTime());
	            break;
	         default:
	            throw new ParseException("Unable to parse dateTime "+text+" of length "+length, 0);
	      }
	      return dateTime;
	}
}

class Gpx {
	class Atribute {
		static final String LAT = "lat";
		static final String LON = "lon";
	};

	class Element {
		static final String TRK = "trk";
		static final String TRKSEG = "trkseg";
		static final String TRKPT = "trkpt";
		static final String ELE = "ele";
		static final String TIME = "time";

		static final String COURSE = "course";
		static final String ACCURACY = "accuracy";
		static final String SPEED = "speed";

		static final String NAME = "name";

	}
}
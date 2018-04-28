package com.cnlaunch.mycar.gps;

import java.util.ArrayList;
import java.util.Random;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.utils.GoogleMapOffsetUtil;
import com.cnlaunch.mycar.gps.database.GpsTrack.Media;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class IconOverlay extends Overlay {
	private final String TAG = "IconOverlay";
	private static Random rnd = new Random();
	private MapDisplayActivity mMapDisplayActivity;
	private Uri mSegmentUri;
	private MapView mMapView;
	private Handler mHandler;
	private ArrayList<MediaIcon> mMediaIcons;
	private ArrayList<Rect> mRects;
	private Paint mIconPaint;

	private Bitmap imageIconNote;
	private Bitmap imageIconPhoto;

	private Projection mProjection;

	private GeoPoint mGeoTopLeft;
	private GeoPoint mGeoBottumRight;

	private int mWidth;
	private int mHeight;

	private final Runnable mTrackCalculator = new Runnable() {
		public void run() {
			IconOverlay.this.calculateTrackAsync();
		}
	};

	public IconOverlay(MapDisplayActivity mapDisplayActivity, Uri segmentUri,
			MapView mapView, Handler handler) {
		mMapDisplayActivity = mapDisplayActivity;
		mSegmentUri = segmentUri;
		mMapView = mapView;
		mHandler = handler;
		mProjection = mMapView.getProjection();

		mIconPaint = new Paint();
		mIconPaint.setColor(Color.RED);
		mIconPaint.setDither(true);
		mIconPaint.setShadowLayer(2f, 2f, 2f, Color.GRAY);
		mIconPaint.setAntiAlias(true);

		mMediaIcons = new ArrayList<MediaIcon>();
		mRects = new ArrayList<Rect>();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		if (mSegmentUri != null) {
			mWidth = canvas.getWidth();
			mHeight = canvas.getHeight();
			drawIcons(canvas);
			calculateTrack();
		}
	}

	public void drawIcons(Canvas canvas) {
		synchronized (mMediaIcons) {
			mRects.clear();
			// 地图偏移补偿系数
			double ratioX = GoogleMapOffsetUtil.getRatioX(mMapView.getZoomLevel());
			double ratioY = GoogleMapOffsetUtil.getRatioY(mMapView.getZoomLevel());
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mMapDisplayActivity);
			long preOffsetX = preferences.getLong(GpsConstants.GPS_PREFERENCES_OFFSET_Y, 0);
			long preOffsetY = preferences.getLong(GpsConstants.GPS_PREFERENCES_OFFSET_Y, 0);

			for (MediaIcon mediaIcon : mMediaIcons) {

				imageIconNote = BitmapFactory.decodeResource(
						mMapDisplayActivity.getResources(),
						R.drawable.gps_icon_note);
				imageIconPhoto = BitmapFactory.decodeResource(
						mMapDisplayActivity.getResources(),
						R.drawable.gps_icon_photo);
				Bitmap icon = null;
				if (mediaIcon.getMediaType() == MediaIcon.NOTE) {
					icon = imageIconNote;
				} else {
					icon = imageIconPhoto;
				}
				int w = icon.getWidth();
				int h = icon.getHeight();

				Point p = new Point();
				GeoPoint geoPoint = new GeoPoint(
						(int) (mediaIcon.getLatitude() * 1E6),
						(int) (mediaIcon.getLongitude() * 1E6));
				mProjection.toPixels(geoPoint, p);

				int offsetX = mediaIcon.getOffsetX();
				int offsetY = mediaIcon.getOffsetY();

				if (mediaIcon.isOffsetGetted()) {
					GoogleMapOffsetUtil
							.pointOffset(p, offsetX, offsetY, ratioX, ratioY);
					preOffsetX = offsetX;
					preOffsetY = offsetY;
				} else {
					GoogleMapOffsetUtil.pointOffset(p, preOffsetX, preOffsetY, ratioX,
							ratioY);

				}
				canvas.drawBitmap(icon, p.x - w / 2, p.y - h, mIconPaint);

				// 计算热点时，上下左右扩大10个像素
				final int HIT_AREA_EXPAND = 10;
				mRects.add(new Rect(p.x - w / 2 - HIT_AREA_EXPAND, p.y - h
						- HIT_AREA_EXPAND, p.x - w / 2 + w + HIT_AREA_EXPAND,
						p.y + HIT_AREA_EXPAND));

			}
		}
	}

	private void calculateTrack() {
		mHandler.removeCallbacks(mTrackCalculator);
		mHandler.post(mTrackCalculator);

	}

	private void calculateTrackAsync() {
		GeoPoint oldTopLeft = mGeoTopLeft;
		GeoPoint oldBottumRight = mGeoBottumRight;
		mGeoTopLeft = mProjection.fromPixels(0, 0);
		mGeoBottumRight = mProjection.fromPixels(mWidth, mHeight);

		if (oldTopLeft == null
				|| oldBottumRight == null
				|| mGeoTopLeft.getLatitudeE6() / 100 != oldTopLeft
						.getLatitudeE6() / 100
				|| mGeoTopLeft.getLongitudeE6() / 100 != oldTopLeft
						.getLongitudeE6() / 100
				|| mGeoBottumRight.getLatitudeE6() / 100 != oldBottumRight
						.getLatitudeE6() / 100
				|| mGeoBottumRight.getLongitudeE6() / 100 != oldBottumRight
						.getLongitudeE6() / 100) {

			ContentResolver resolver = mMapDisplayActivity.getContentResolver();
			Uri mediaUri = Uri.withAppendedPath(mSegmentUri, Media.TABLE);
			Cursor mediaCursor = resolver.query(mediaUri, new String[] {
					Media.URI, Waypoints.LATITUDE, Waypoints.LONGITUDE,
					Waypoints.OFFSET_X, Waypoints.OFFSET_Y,
					Waypoints.OFFSET_GETTED }, null, null, null);
			try {
				ArrayList<MediaIcon> mediaIcons = null;
				if (mediaCursor != null) {

					mediaIcons = new ArrayList<MediaIcon>();
					while (mediaCursor.moveToNext()) {
						String strUri = mediaCursor.getString(mediaCursor
								.getColumnIndex(Media.URI));
						double la = mediaCursor.getDouble(mediaCursor
								.getColumnIndex(Waypoints.LATITUDE));
						double lo = mediaCursor.getDouble(mediaCursor
								.getColumnIndex(Waypoints.LONGITUDE));
						int mediaType = strUri.contains(".txt") ? MediaIcon.NOTE
								: MediaIcon.PHOTO;
						int offsetX = mediaCursor.getInt(mediaCursor
								.getColumnIndex(Waypoints.OFFSET_X));
						int offsetY = mediaCursor.getInt(mediaCursor
								.getColumnIndex(Waypoints.OFFSET_Y));
						int offset_getted = mediaCursor.getInt(mediaCursor
								.getColumnIndex(Waypoints.OFFSET_GETTED));

						mediaIcons.add(new MediaIcon(la, lo, strUri, mediaType,
								offsetX, offsetY,
								offset_getted == Waypoints.OFFSET_GETTED_YET));

					}
					mediaCursor.close();
					mediaCursor = null;
					synchronized (mMediaIcons) {
						mMediaIcons = mediaIcons;
					}
					mMapDisplayActivity.onDataOverlayChanged();
				}

			} finally {
				if (mediaCursor != null) {
					mediaCursor.close();
				}
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_UP:
			hitTest((int) e.getX(), (int) e.getY());
			break;
		default:
			break;
		}
		return super.onTouchEvent(e, mapView);
	}

	private void hitTest(int x, int y) {
		synchronized (mMediaIcons) {
			int len = mRects.size();
			for (int i = len-1; i >= 0; i--) {
				if (mRects.get(i).contains(x, y)) {
					String str = mMediaIcons.get(i).getMediaUri();
					if (str != null) {
						Uri uri = Uri.parse(str);
						Intent intent = new Intent(Intent.ACTION_VIEW);
					
						if (str.contains(".txt")) {
							mMapDisplayActivity.startActivity(intent
									.setDataAndType(uri, "text/plain"));
						} else if (str.contains(".jpg")) {
							mMapDisplayActivity.startActivity(intent
									.setDataAndType(uri, "image/jpg"));
						}
						Log.e(TAG, intent.toString());
					}
					break;
				}
			}

		}

	}

	private class MediaIcon {
		public static final int PHOTO = 0;
		public static final int NOTE = 1;
		private double mLatitude;
		private double mLongitude;
		private String mMediaUri;
		private int mMediaType;
		private int mOffsetX;
		private int mOffsetY;
		private boolean mOffsetGetted;

		public MediaIcon(double latitude, double longitude, String mediaUri,
				int mediaType, int offsetX, int offsetY, boolean offsetGetted) {
			mLatitude = latitude;
			mLongitude = longitude;
			mMediaUri = mediaUri;
			mMediaType = mediaType;
			mOffsetX = offsetX;
			mOffsetY = offsetY;
			mOffsetGetted = offsetGetted;
		}

		public double getLatitude() {
			return mLatitude;
		}

		public double getLongitude() {
			return mLongitude;
		}

		public String getMediaUri() {
			return mMediaUri;
		}

		public int getMediaType() {
			return mMediaType;
		}

		public int getOffsetX() {
			return mOffsetX;
		}

		public int getOffsetY() {
			return mOffsetY;
		}

		public boolean isOffsetGetted() {
			return mOffsetGetted;
		}

	}

}

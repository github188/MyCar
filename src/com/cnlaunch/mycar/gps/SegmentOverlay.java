package com.cnlaunch.mycar.gps;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cnlaunch.mycar.common.utils.GoogleMapOffsetUtil;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class SegmentOverlay extends Overlay {
	private final String TAG = "SegmentOverlay";
	private MapDisplayActivity mMapDisplayActivity;
	private Uri mSegmentUri;
	private MapView mMapView;
	private Handler mHandler;
	private Path mTrackPath;
	private Paint mPathPaint;
	private Projection mProjection;

	private GeoPoint mGeoTopLeft;
	private GeoPoint mGeoBottumRight;

	private int mWidth;
	private int mHeight;

	private final Runnable mTrackCalculator = new Runnable() {
		public void run() {
			SegmentOverlay.this.calculateTrackAsync();
		}
	};

	public SegmentOverlay(MapDisplayActivity mapDisplayActivity,
			Uri segmentUri, MapView mapView, Handler handler) {
		mMapDisplayActivity = mapDisplayActivity;
		mSegmentUri = segmentUri;
		mMapView = mapView;
		mHandler = handler;
		mProjection = mMapView.getProjection();

		mPathPaint = new Paint();
		mPathPaint.setColor(Color.RED);
		mPathPaint.setDither(true);
		mPathPaint.setStyle(Paint.Style.STROKE);
		mPathPaint.setStrokeJoin(Paint.Join.ROUND);
		mPathPaint.setStrokeCap(Paint.Cap.ROUND);
		mPathPaint.setShadowLayer(2f, 2f, 2f, Color.GRAY);
		mPathPaint.setStrokeWidth(2);
		mPathPaint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		// drawLastGeoPoint(canvas);
		if (mSegmentUri != null) {
			drawSegment(canvas, mapView);
		}
	}

	private void drawSegment(Canvas canvas, MapView mapView) {
		mWidth = canvas.getWidth();
		mHeight = canvas.getHeight();

		drawPath(canvas);
		calculateTrack();
	}

	private void drawPath(Canvas canvas) {
		if (mTrackPath != null) {

			canvas.drawPath(mTrackPath, mPathPaint);
		} else {
			Log.e(TAG, "mTrackPath is null");
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
			Uri waypointUri = Uri
					.withAppendedPath(mSegmentUri, Waypoints.TABLE);
			Cursor waypointsCursor = resolver.query(waypointUri, new String[] {
					Waypoints._ID, Waypoints.LATITUDE, Waypoints.LONGITUDE,
					Waypoints.OFFSET_X, Waypoints.OFFSET_Y,
					Waypoints.OFFSET_GETTED }, null, null, null);
			try {
				ArrayList<GeoPoint> waypoints = null;
				if (waypointsCursor != null) {

					waypoints = new ArrayList<GeoPoint>();

					Path path = new Path();

					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mMapDisplayActivity);
					long preOffsetX = preferences.getLong(GpsConstants.GPS_PREFERENCES_OFFSET_Y, 0);
					long preOffsetY = preferences.getLong(GpsConstants.GPS_PREFERENCES_OFFSET_Y, 0);
					int len = waypointsCursor.getCount();
					boolean isFirstElement = true;

					// 地图偏移补偿系数
					double ratioX = GoogleMapOffsetUtil.getRatioX(mMapView
							.getZoomLevel());
					double ratioY = GoogleMapOffsetUtil.getRatioY(mMapView
							.getZoomLevel());
					while (waypointsCursor.moveToNext()) {
						int id = waypointsCursor.getInt(waypointsCursor
								.getColumnIndex(Waypoints._ID));
						double la = waypointsCursor.getDouble(waypointsCursor
								.getColumnIndex(Waypoints.LATITUDE));
						double lo = waypointsCursor.getDouble(waypointsCursor
								.getColumnIndex(Waypoints.LONGITUDE));
						int offsetX = waypointsCursor.getInt(waypointsCursor
								.getColumnIndex(Waypoints.OFFSET_X));
						int offsetY = waypointsCursor.getInt(waypointsCursor
								.getColumnIndex(Waypoints.OFFSET_Y));
						int isRequestedOffset = waypointsCursor
								.getInt(waypointsCursor
										.getColumnIndex(Waypoints.OFFSET_GETTED));
						GeoPoint geoPoint = new GeoPoint((int) (la * 1000000),
								(int) (lo * 1000000));
						waypoints.add(geoPoint);

						Point p = new Point();
						mProjection.toPixels(geoPoint, p);

						// 地图偏移补偿
						if (isRequestedOffset == Waypoints.OFFSET_GETTED_YET) {// 如果尚未从服务器上请求到偏移坐标，则使用前一个点的偏移值
							GoogleMapOffsetUtil.pointOffset(p, offsetX, offsetY, ratioX, ratioY);
							preOffsetX = offsetX;
							preOffsetY = offsetY;
						} else {
							GoogleMapOffsetUtil.pointOffset(p, preOffsetX, preOffsetY, ratioX, ratioY);
						}

						if (isFirstElement) {
							path.moveTo(p.x, p.y);
							isFirstElement = false;
						} else {
							path.lineTo(p.x, p.y);
						}
					}

					mTrackPath = path;
					// Log.e(TAG, "mSegmentUri=" + mSegmentUri + "路点数:"
					// + waypoints.size());
					// Log.e(TAG, "Path:" + mTrackPath);
					mMapDisplayActivity.onDataOverlayChanged();
				}

			} finally {
				if (waypointsCursor != null) {
					waypointsCursor.close();
				}
			}
		}
	}

	
}

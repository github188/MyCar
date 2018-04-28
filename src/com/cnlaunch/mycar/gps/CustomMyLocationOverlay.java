package com.cnlaunch.mycar.gps;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.GoogleMapOffsetUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class CustomMyLocationOverlay extends MyLocationOverlay {
	private final String TAG = "CustomMyLocationOverlay";
	private long REQUEST_TIMEOUT = 1000;//最少隔1秒，才发纠偏请求
	private final int THRESHOLD_VALUE = 20;//最少移动50个像素，才发纠偏请求
	private Context mContext;
	private MapView mMapView;
	private long mOffsetX = 0;
	private long mOffsetY = 0;
	private Location mPreLocation = null;
	private OffsetRequestThread mOffsetRequestThread;
	private GeoPoint mMyLocation;
	private long mLastUpdate = 0;


	public CustomMyLocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		mContext = context;
		mMapView = mapView;
		restoreLatestOffset();
		mOffsetRequestThread = new OffsetRequestThread();
		mOffsetRequestThread.setDaemon(true);
		mOffsetRequestThread.start();
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView,
			Location lastFix, GeoPoint myLocation, long when) {

		double la = lastFix.getLatitude();
		double lo = lastFix.getLongitude();

		synchronized (mOffsetRequestThread) {
			if (isNeedUpdateOffset(lastFix)) {
				mLastUpdate = System.currentTimeMillis();
				mOffsetRequestThread.requestOffset(la, lo);
			}
		}
		mPreLocation = lastFix;
		mMyLocation = addOffset(myLocation);
		super.drawMyLocation(canvas, mapView, lastFix, mMyLocation, when);
	}

	private boolean isNeedUpdateOffset(Location lastFix) {
		if (System.currentTimeMillis() - mLastUpdate < REQUEST_TIMEOUT) {
			return false;
		}
		
		if(!Env.isNetworkAvailable(mContext)){
			return true;
		}

		if (mPreLocation != null) {
			GeoPoint gp1 = new GeoPoint(
					(int) (mPreLocation.getLatitude() * 1E6),
					(int) (mPreLocation.getLongitude() * 1E6));
			GeoPoint gp2 = new GeoPoint((int) (lastFix.getLatitude() * 1E6),
					(int) (lastFix.getLongitude() * 1E6));
			Point p1 = new Point();
			Point p2 = new Point();
			mMapView.getProjection().toPixels(gp1, p1);
			mMapView.getProjection().toPixels(gp2, p2);

			if (Math.abs(p1.x - p2.x) < THRESHOLD_VALUE
					&& Math.abs(p1.y - p2.y) < THRESHOLD_VALUE) {
				return false;
			}
		}
		return true;
	}

	private String doGet(String url) {
		try {
			HttpGet method = new HttpGet(url);

			DefaultHttpClient client = new DefaultHttpClient();

			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			if (status != HttpStatus.SC_OK)
				throw new Exception("");
			String strResult = EntityUtils.toString(response.getEntity(),
					"UTF-8");
			Log.e(TAG, url);
			Log.e(TAG, strResult);
			return strResult;
		} catch (Exception e) {
			return null;
		}
	}

	private class OffsetRequestThread extends Thread {
		private static final int MSG_REQUEST_OFFSET = 0;

		private Looper looper;
		private Handler mHandler;

		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler() {

				@Override
				public void handleMessage(android.os.Message msg) {
					switch (msg.what) {
					case MSG_REQUEST_OFFSET:
						Bundle data = msg.getData();
						double lo = data.getDouble("lo");
						double la = data.getDouble("la");
						String url = GpsConstants.GPS_OFFSET_SERVICE_URL+"?lonAndLat="
								+ lo + "," + la;
						String result = doGet(url);
						if (result!=null && result.contains(",")) {
							String[] r = result.split(",");
							mOffsetX = Long.parseLong(r[0]);
							mOffsetY = Long.parseLong(r[1]);
							saveLastOffset();
						}

						break;
					default:
						break;
					}
				};

			};
			looper = Looper.myLooper();
			Looper.loop();
		}

		public void requestOffset(double la, double lo) {
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_REQUEST_OFFSET;
			Bundle data = msg.getData();
			data.putDouble("lo", lo);
			data.putDouble("la", la);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
	}

	@Override
	public synchronized void disableMyLocation() {
		super.disableMyLocation();
	}
	
	@Override
	public synchronized boolean enableMyLocation(){
		return super.enableMyLocation();
	}

	@Override
	public GeoPoint getMyLocation() {
		return mMyLocation;
	}

	public GeoPoint addOffset(GeoPoint geoPoint) {
		Point p = new Point();
		mMapView.getProjection().toPixels(geoPoint, p);
		p.offset((int) (mOffsetX * GoogleMapOffsetUtil.getRatioX(mMapView
				.getZoomLevel())), (int) (mOffsetY * GoogleMapOffsetUtil
				.getRatioX(mMapView.getZoomLevel())));

		Paint paint = new Paint();
		paint.setColor(Color.RED);
		return mMapView.getProjection().fromPixels(p.x, p.y);
	}
	
	private void saveLastOffset() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = preferences.edit();
		editor.putLong(GpsConstants.GPS_PREFERENCES_OFFSET_X, mOffsetX);
		editor.putLong(GpsConstants.GPS_PREFERENCES_OFFSET_Y, mOffsetY);
		editor.commit();
	}
	private void restoreLatestOffset() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		mOffsetX = preferences.getLong(GpsConstants.GPS_PREFERENCES_OFFSET_X, 0);
		mOffsetY = preferences.getLong(GpsConstants.GPS_PREFERENCES_OFFSET_Y, 0);
	}
}

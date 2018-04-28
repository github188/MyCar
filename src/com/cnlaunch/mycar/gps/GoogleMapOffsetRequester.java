package com.cnlaunch.mycar.gps;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;

public class GoogleMapOffsetRequester extends Thread {
	private static final int GET_OFFSET = 0;
	private static final int QUIT_LOOP = 1;
	private static final int MIN_WAYPOINT_PER_REQUEST = 20;// 每次请求偏移值时的最少路点数
	private static final int MAN_WAYPOINT_PER_REQUEST = 500;// 每次请求偏移值时的最大路点数

	private final static String TAG = "OffsetRequester";
	private Handler mHandler;
	private Context mContext;

	public GoogleMapOffsetRequester(Context context) {
		mContext = context;
	}

	@Override
	public void run() {
		Looper.prepare();
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				_handleMessage(msg);
			}

		};
		Looper.loop();
	}

	private void _handleMessage(Message msg) {
		switch (msg.what) {
		case GET_OFFSET:
			doRequest();
			break;
		case QUIT_LOOP:
			Looper.myLooper().quit();
			break;
		default:
			break;
		}
	}

	private void doRequest() {
		if (!Env.isNetworkAvailable(mContext)) {
			return;
		}

		Uri uri = Uri.withAppendedPath(Waypoints.CONTENT_URI,
				Waypoints.NOT_GET_OFFSET);
		Cursor waypointsCursor = mContext.getContentResolver().query(
				uri,
				new String[] { Waypoints._ID, Waypoints.LATITUDE,
						Waypoints.LONGITUDE }, null, null, null);
		ArrayList<HashMap<String, String>> list = null;
		try {
			if (waypointsCursor != null) {

				if (waypointsCursor.getCount() < MIN_WAYPOINT_PER_REQUEST) {
					return;
				}

				list = new ArrayList<HashMap<String, String>>();
				int count = 0;
				while (waypointsCursor.moveToNext()
						&& count < MAN_WAYPOINT_PER_REQUEST) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(Waypoints._ID, waypointsCursor.getString(0));
					map.put(Waypoints.LATITUDE, waypointsCursor.getString(1));
					map.put(Waypoints.LONGITUDE, waypointsCursor.getString(2));
					list.add(map);
					count++;
				}

			}
		} finally {
			waypointsCursor.close();
		}
		String result = doPost(list);
		if (result != null) {
			Log.e(TAG, result);
			dealHttpResult(result, list);
		}

	}

	private String doPost(ArrayList<HashMap<String, String>> list) {
		if (list != null && list.size() == 0) {
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String lonAndLat = null;
		int len = list.size();
		for (int i = 0; i < len; i++) {
			HashMap<String, String> map = list.get(i);
			if (lonAndLat == null) {
				lonAndLat = map.get(Waypoints.LONGITUDE) + ","
						+ map.get(Waypoints.LATITUDE);
			} else {
				lonAndLat += ";" + map.get(Waypoints.LONGITUDE) + ","
						+ map.get(Waypoints.LATITUDE);
			}
		}
		Log.e(TAG, lonAndLat);
		params.add(new BasicNameValuePair("lonAndLat", lonAndLat));
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60 * 1000);
		HttpPost httpPost = new HttpPost(GpsConstants.GPS_OFFSET_SERVICE_URL);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return null;
		}

		try {
			HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				return result;

			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.toString());
			return null;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return null;
		}

		return null;
	}

	private void dealHttpResult(String result,
			ArrayList<HashMap<String, String>> list) {
		String[] arrResult = result.split(";");
		if (arrResult.length != list.size()) {
			Log.e(TAG, "请求包含" + list.size() + "组数据，但只返回了" + arrResult.length
					+ "组纠正值");
			return;
		}

		int len = arrResult.length;
		for (int i = 0; i < len; i++) {
			String t = arrResult[i];
			if (t.contains(",")) {
				String[] r = t.split(",");
				try {
					long offset_x = Long.parseLong(r[0]);
					long offset_y = Long.parseLong(r[1]);
					HashMap<String, String> map = list.get(i);
					String waypointId = map.get(Waypoints._ID);
					Uri uri = Uri.withAppendedPath(Waypoints.CONTENT_URI,
							waypointId);
					ContentValues values = new ContentValues();
					values.put(Waypoints.OFFSET_X, offset_x);
					values.put(Waypoints.OFFSET_Y, offset_y);
					values.put(Waypoints.OFFSET_GETTED,
							Waypoints.OFFSET_GETTED_YET);
					mContext.getContentResolver().update(uri, values, null,
							null);
				} catch (NumberFormatException e) {
					Log.e(TAG, "纠偏接口，返回值格式有误");
				}
			}
		}

	}

	public Handler getHandler() {
		return mHandler;
	}

	public void checkAndRequestOffsetFromWeb() {
		Message msg = Message.obtain();
		msg.what = GET_OFFSET;
		mHandler.sendMessage(msg);
	}

	public void loopQuit() {
		Message msg = Message.obtain(mHandler);
		msg.what = QUIT_LOOP;
		msg.sendToTarget();
	}
}

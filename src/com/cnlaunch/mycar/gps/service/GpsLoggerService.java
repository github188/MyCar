package com.cnlaunch.mycar.gps.service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.gps.GoogleMapOffsetRequester;
import com.cnlaunch.mycar.gps.GpsConstants;
import com.cnlaunch.mycar.gps.MapDisplayActivity;
import com.cnlaunch.mycar.gps.database.GpsTrack.Media;
import com.cnlaunch.mycar.gps.database.GpsTrack.Segments;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;

public class GpsLoggerService extends Service {

	private final boolean DEBUG = true;
	private static final boolean VERBOSE = true;
	private static final String TAG = "GpsLoggerService";

	private static final String PREFERENCES_SERVICE_STATE_TRACKID = "PREFERENCES_SERVICE_STATE_TRACKID";
	private static final String PREFERENCES_SERVICE_STATE_SEGMENTID = "PREFERENCES_SERVICE_STATE_SEGMENTID";
	private static final String PREFERENCES_SERVICE_STATE_LOGGINGSTATE = "PREFERENCES_SERVICE_STATE_LOGGINGSTATE";

	private static final int MSG_START_LOGGING = 1;
	private static final int MSG_STOP_LOOPER = 2;
	private static final int MSG_GPS_PROBLEM = 3;

	private static final int RES_STRING_LOGGING_DISABLED = R.string.gps_provider_undisabled;

	private LocationManager mLocationManager;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mWakeLock;
	private Handler mHandler;

	private long mTrackId = -1;
	private long mSegmentId = -1;
	private long mWaypointId = -1;
	private long mWaypointCount = 0;

	private int mLoggingState = GpsConstants.GPS_LOGGER_STOPPED;
	private int mGpsStatus = LocationProvider.AVAILABLE;

	private Location mPreviousLocation;// �㼣�켣���Ѽ�¼�µ����һ��
	private float mPreviousSpeed;
	private ArrayList<Location> badLocations;
	private Notification mNotification;

	private boolean mHaveSendNotificationForProviderDisabled;
	private boolean mStartNextSegment;

	private Timer mHeartbeatTimer;
	private TimerTask mHeartbeat = null;
	private long INTERVAL_TIME = 5000L;
	private float MIN_DISTANCE = 5F;
	private long CHECK_PERIOD = 120 * 1000;// ��������Ϊ2����
	/** С����С����ʱ�����򲻼�¼ */
	private static final int MIN_STEP = 5;
	/** ������󲽳�ʱ����ʼһ���µ�segment */
	private static final int MAX_STEP = 200;
	/** ����ٶȣ�ÿ��85�ף�Լ300KM/H */
	private static final int MAX_SPPED = 85;
	/** �����ٶȣ�40m/s^2 */
	private static final int MAX_ACCELERATION = 40;
	/** ��С�ɽ��վ��� */
	private static final int MAX_ACCURACY = 100;
	private GoogleMapOffsetRequester googleMapOffsetRequester;
	private Resources r;

	private void d(String str) {
		if (DEBUG)
			Log.d(TAG, str);
	}

	private void v(String str) {
		if (VERBOSE)
			Log.v(TAG, str);
	}

	private void e(String str) {
		Log.e(TAG, str);
	}

	class Heartbeat extends TimerTask {

		@Override
		public void run() {

			if (isLogging()) {
				v("����:" + System.currentTimeMillis() + "  �켣�ɼ���");
				Location checkLocation = mPreviousLocation;

				Location managerLocation = mLocationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (managerLocation != null && checkLocation != null) {
					if (checkLocation.distanceTo(managerLocation) < MAX_STEP) {
						checkLocation = managerLocation.getTime() > checkLocation
								.getTime() ? managerLocation : checkLocation;
					}
				}

				if (checkLocation == null
						|| checkLocation.getTime() + CHECK_PERIOD < System
								.currentTimeMillis()) {
					mLoggingState = GpsConstants.GPS_LOGGER_PAUSED;
					v("2����֮�ڣ�δ�ɼ�����Ч��GPS���꣬�����ɼ�����"
							+ LocationManager.GPS_PROVIDER);
					resumeLogging();
				}
			} else {
				v("����:" + System.currentTimeMillis() + "  �켣�ɼ���ͣ��"
						+ LocationManager.GPS_PROVIDER);
			}

			// ��ÿ������ʱ����ⲢΪ��δ��ƫ�Ĺ켣�㣬�������������ƫֵ
			googleMapOffsetRequester.checkAndRequestOffsetFromWeb();

		}
	}

	private class GpsLoggerServiceThread extends Thread {
		public Semaphore ready = new Semaphore(0);

		GpsLoggerServiceThread() {
			this.setName("GpsLoggerServiceThread");
		}

		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					_handleMessage(msg);
				}
			};
			ready.release();
			Looper.loop();

		}

	}

	private void _handleMessage(Message msg) {
		d("_handleMessage()" + msg);

		switch (msg.what) {
		case MSG_START_LOGGING:
			mWaypointCount = getWaypointCount(mTrackId);
			startListening(LocationManager.GPS_PROVIDER);
			break;
		case MSG_STOP_LOOPER:
			Looper.myLooper().quit();
			break;
		case MSG_GPS_PROBLEM:
			break;
		default:
			break;
		}

	}

	private LocationListener mLocationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			v("onLocationChanged( Location " + location + " )");

			if (isLogging()) {
				location = filteLocation(location);

				if (location != null) {
					if (mStartNextSegment) {
						mStartNextSegment = false;

						// �������һ���켣��̫Զ����û����һ���㣬��ʼһ���µ�segment
						if (mPreviousLocation == null
								|| location.distanceTo(mPreviousLocation) > MAX_STEP) {
							startNewSegment();
						}
					}
					saveLocation(location);
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			d("onProviderDisabled()" + provider);

			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				d("GPS������");
				if (isLogging()) {
					sendNotificationOnDisableProvider();
				}
			}

		}

		@Override
		public void onProviderEnabled(String provider) {
			d("onProviderEnabled()" + provider);

			if (isLogging()) {
				if (provider.equals(LocationManager.GPS_PROVIDER)) {
					d("GPS����");
					mStartNextSegment = true;
					startLogging();
				}
				updateNotification();

				if (mHaveSendNotificationForProviderDisabled) {
					sendNotificationForProviderEnable();
				}
			}

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				mGpsStatus = status;
				e("onStatusChanged()--->" + provider + ":" + mGpsStatus
						+ extras.toString());
			}
		}
	};

	private void sendNotificationOnDisableProvider() {
		int icon = R.drawable.ic_maps_indicator_current_position;
		CharSequence tickerText = getResources().getString(
				R.string.gps_provider_undisabled);
		long when = System.currentTimeMillis();
		Notification gpsNotification = new Notification(icon, tickerText, when);
		gpsNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		CharSequence contentTitle = getResources()
				.getString(R.string.gps_track);
		CharSequence contentText = getResources().getString(
				R.string.gps_provider_undisabled);
		Intent notificationIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		gpsNotification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(RES_STRING_LOGGING_DISABLED,
				gpsNotification);
		mHaveSendNotificationForProviderDisabled = true;
	}

	protected Location filteLocation(Location location) {
		// �����һ����Ϊ�գ�����ǰ���������Ϊ��һ����ʱ���е�ǰ��Ϊ��Ч��
		if (mPreviousLocation == null || location == null) {
			return location;
		}

		// ����ɼ�Ƶ�ȹ��ߣ���ǰ��Ϊ��Ч��
		if (location.getTime() - mPreviousLocation.getTime() < INTERVAL_TIME) {
			e("�ɼ�Ƶ�ȹ���");
			return null;
		}

		// ��Ч��:
		// ��ǰ���ǰһ���㡰̫����
		// �ж��ƶ��ٶȣ��������
		// ����̫��
		//
		if (location.distanceTo(mPreviousLocation) < MIN_STEP
				|| (location.hasSpeed() && location.getSpeed() > MAX_SPPED)
				|| (location.hasAccuracy() && location.getAccuracy() > MAX_ACCURACY)

		) {
			e("̫��/̫��/����̫��");
			return null;
		}

		// ���ٶ�̫�죨����GPS�ź��е��ٶȼ�ʱ�����жϣ�
		if ((mPreviousLocation.hasSpeed() && location.hasSpeed() && Math
				.abs((location.getSpeed() - mPreviousLocation.getSpeed())
						* 1000
						/ (location.getTime() - mPreviousLocation.getTime())) > MAX_ACCELERATION)) {
			e("���ٶ�̫��1");
			return chooseLeastBadLocation(location);
		}

		// ���ٶ�̫�죨����ʵ��λ�����ٶȣ��ٸ���ʱ�����жϣ�
		float speed = location.distanceTo(mPreviousLocation)
				/ (location.getTime() - mPreviousLocation.getTime());
		if (mPreviousSpeed > 0.0
				&& Math.abs(speed - mPreviousSpeed) * 1000
						/ (location.getTime() - mPreviousLocation.getTime()) > MAX_ACCELERATION) {
			e("���ٶ�̫��2");
			mPreviousSpeed = speed;
			return chooseLeastBadLocation(location);
		}
		mPreviousSpeed = speed;
		return location;
	}

	/**�������²ɼ�����3���㣬����*���*�ĵ�
	 * @param location
	 * @return
	 */
	private Location chooseLeastBadLocation(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ��״̬����״̬���������С���ע��
	 */
	private void setupNotification() {
		d("setupNotification() ---------> Notification");
		mNotificationManager.cancel(R.layout.gps_map);

		int icon = R.drawable.ic_maps_indicator_current_position;
		CharSequence tickerText = getResources().getString(
				R.string.gps_service_start);
		long when = System.currentTimeMillis();

		mNotification = new Notification(icon, tickerText, when);
		mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
	}

	/**
	 * ��ʾ�����״̬�����������С��е���ʾ��Ϣ
	 */
	private void updateNotification() {
		d("updateNotification() begin");

		// ����ͣ�����ڲɼ���ʱ��������֪ͨ��
		if (mLoggingState != GpsConstants.GPS_LOGGER_LOGGING
				&& mLoggingState != GpsConstants.GPS_LOGGER_PAUSED) {
			return;
		}

		if (mNotification == null) {
			e("mNotification == null ֪ͨ��δ��װ");
			return;
		}

		CharSequence contentTitle = getResources()
				.getString(R.string.gps_track);
		CharSequence contentText = null;

		switch (mLoggingState) {
		case GpsConstants.GPS_LOGGER_LOGGING:
			if (mWaypointCount > 0) {
				contentText = r.getString(R.string.gps_get_waypoint,
						mWaypointCount);
			} else {
				contentText = r.getString(R.string.gps_no_waypoint);
			}
//			switch (mGpsStatus) {
//			case LocationProvider.OUT_OF_SERVICE:
//				contentText = r.getString(R.string.gps_out_of_service);
//				break;
//			case LocationProvider.TEMPORARILY_UNAVAILABLE:
//				contentText = r.getString(R.string.gps_temporarily_unavailable);
//				break;
//			case LocationProvider.AVAILABLE:
//			default:
//				break;
//			}

			break;
		case GpsConstants.GPS_LOGGER_PAUSED:
			contentText = "�㼣��¼����ͣ";
			break;
		default:
			break;
		}

		Intent notificationIntent = new Intent(this, MapDisplayActivity.class);
		notificationIntent.setData(ContentUris.withAppendedId(
				Tracks.CONTENT_URI, mTrackId));
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(R.layout.gps_map, mNotification);
		d("updateNotification()" + "end----> " + contentText);

	}

	/**
	 * ��λ�����Ѵ򿪣��������򿪶�λ���ܡ�����Ϣ��ʾ
	 */
	private void sendNotificationForProviderEnable() {
		mNotificationManager.cancel(RES_STRING_LOGGING_DISABLED);
		mHaveSendNotificationForProviderDisabled = false;
		Toast.makeText(this, R.string.gps_provider_gps, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * �������״̬����
	 */
	private void crashProtectState() {
		d("crashProtectState()");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = preferences.edit();
		editor.putLong(PREFERENCES_SERVICE_STATE_TRACKID, mTrackId);
		editor.putLong(PREFERENCES_SERVICE_STATE_SEGMENTID, mSegmentId);
		editor.putInt(PREFERENCES_SERVICE_STATE_LOGGINGSTATE, mLoggingState);
		editor.commit();

	}

	/**
	 * �ָ��ѱ�����GPS�ɼ����񣬼����ɼ�
	 */
	private synchronized void crashRestoreState() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		long previousLoggingState = preferences.getInt(
				PREFERENCES_SERVICE_STATE_LOGGINGSTATE,
				GpsConstants.GPS_LOGGER_STOPPED);
		if (previousLoggingState == GpsConstants.GPS_LOGGER_LOGGING
				|| previousLoggingState == GpsConstants.GPS_LOGGER_PAUSED) {
			Log.w(TAG, "�ָ��ѱ�����GPS�ɼ����񣬼����ɼ�");

			mTrackId = preferences.getLong(PREFERENCES_SERVICE_STATE_TRACKID,
					-1);
			mSegmentId = preferences.getLong(
					PREFERENCES_SERVICE_STATE_SEGMENTID, -1);

			setupNotification();
			updateNotification();

			if (previousLoggingState == GpsConstants.GPS_LOGGER_LOGGING) {
				mLoggingState = GpsConstants.GPS_LOGGER_PAUSED;
				resumeLogging();
			} else if (previousLoggingState == GpsConstants.GPS_LOGGER_PAUSED) {
				mLoggingState = GpsConstants.GPS_LOGGER_LOGGING;
				pauseLogging();

			}
		}
		v("crashRestoreState() mTrackId=" + mTrackId);

	}

	private void stopListening() {
		if (mHeartbeat != null) {
			mHeartbeat.cancel();
			mHeartbeat = null;
		}
		mLocationManager.removeUpdates(mLocationListener);
	}

	private void startListening(String provider) {
		d("--------------->startListening()" + provider);
		mLocationManager.removeUpdates(mLocationListener);
		mLocationManager.requestLocationUpdates(provider, INTERVAL_TIME,
				MIN_DISTANCE, mLocationListener);

		if (mHeartbeat != null) {
			mHeartbeat.cancel();
			mHeartbeat = null;
		}
		mHeartbeat = new Heartbeat();
		mHeartbeatTimer.schedule(mHeartbeat, CHECK_PERIOD, CHECK_PERIOD);

	}

	/**
	 * ��ͣGPS��¼����
	 */
	public synchronized void pauseLogging() {
		d("pauseLogging()");
		if (this.mLoggingState == GpsConstants.GPS_LOGGER_LOGGING) {
			stopListening();
			mLoggingState = GpsConstants.GPS_LOGGER_PAUSED;
			mPreviousLocation = null;
			crashProtectState();

		}
		updateNotification();
		updateWakeLock();
	}

	/**
	 * �ָ�GPS��¼����
	 */
	private synchronized void resumeLogging() {
		d("resumeLogging() + trackId="+mTrackId + ",segmentId=" + mSegmentId);
		if (this.mLoggingState == GpsConstants.GPS_LOGGER_PAUSED) {
			sendStartLoggingMsg();
			this.mLoggingState = GpsConstants.GPS_LOGGER_LOGGING;
			mStartNextSegment = true;
			updateWakeLock();
			updateNotification();
			crashProtectState();
		}

	}

	/**
	 * ����GPS��¼����
	 */
	private synchronized void startLogging() {
		d("startLogging()");

		if (this.mLoggingState == GpsConstants.GPS_LOGGER_STOPPED) {
			mLoggingState = GpsConstants.GPS_LOGGER_LOGGING;
			startNewTrack();
			mWaypointCount = 0;
			sendStartLoggingMsg();
			setupNotification();
			updateNotification();
			crashProtectState();
			d("trackId="+mTrackId + ",segmentId=" + mSegmentId);
		}
		updateWakeLock();

	}

	/**
	 * ֹͣGPS��¼����
	 */
	private synchronized void stopLogging() {
		d("stopLogging()");
		this.mLoggingState = GpsConstants.GPS_LOGGER_STOPPED;
		this.mTrackId = -1;
		crashProtectState();
		stopListening();
		mNotificationManager.cancel(R.layout.gps_map);
		updateWakeLock();
	}

	/**
	 * ���Ϳ�ʼ��¼�㼣����Ϣ
	 */
	private synchronized void sendStartLoggingMsg() {
		Message msg = Message.obtain();
		msg.what = MSG_START_LOGGING;
		mHandler.sendMessage(msg);
	}

	/** ��ʼ�µ��㼣Ƭ�� */
	private void startNewSegment() {
		this.mPreviousLocation = null;
		Uri newSegment = this.getContentResolver().insert(
				Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId + "/"
						+ Segments.TABLE), new ContentValues(0));
		mSegmentId = new Long(newSegment.getLastPathSegment()).longValue();
	}

	/** ��ʼ�µ��㼣��¼ */
	private void startNewTrack() {
		this.mPreviousLocation = null;
		Uri newTrack = this.getContentResolver().insert(Tracks.CONTENT_URI,
				new ContentValues(0));
		mTrackId = new Long(newTrack.getLastPathSegment()).longValue();
		startNewSegment();

		googleMapOffsetRequester.checkAndRequestOffsetFromWeb();
		d("startNewTrack()" + mTrackId);
	}

	/** ����켣�� */
	private void saveLocation(Location location) {
		if (!isLogging()) {
			d("�㼣��¼����δ�������޷������µĹ켣��");
			return;
		}

		if (location == null) {
			d("�����µĹ켣�㣬ʧ��.location����Ϊ��");
			return;
		}

		mPreviousLocation = location;
		d("saveLocation(" + location + ")");

		ContentValues args = new ContentValues();
		args.put(Waypoints.LATITUDE, new Double(location.getLatitude()));
		args.put(Waypoints.LONGITUDE, new Double(location.getLongitude()));
		args.put(Waypoints.SPEED, new Float(location.getSpeed()));
		args.put(Waypoints.TIME, new Long(System.currentTimeMillis()));
		args.put(Waypoints.OFFSET_GETTED, Waypoints.OFFSET_GETTED_NOT_YET);

		if (location.hasAccuracy()) {
			args.put(Waypoints.ACCURACY, new Float(location.getAccuracy()));
		}

		if (location.hasAltitude()) {
			args.put(Waypoints.ALTITUDE, location.getAltitude());
		}

		if (location.hasBearing()) {
			args.put(Waypoints.BEARING, new Float(location.getBearing()));
		}

		Uri waypointsUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId
				+ "/" + Segments.TABLE + "/" + mSegmentId + "/"
				+ Waypoints.TABLE);
		Uri insertedUri = this.getContentResolver().insert(waypointsUri, args);
		if (insertedUri != null) {
			mWaypointId = Long.parseLong(insertedUri.getLastPathSegment());
			mWaypointCount++;
			updateNotification();
		}

	}

	private boolean isLogging() {
		return mLoggingState == GpsConstants.GPS_LOGGER_LOGGING;
	}

	public GpsLoggerService() {
		d("GpsLoggerService()");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		r = getResources();
		d("onCreate()");
		GpsLoggerServiceThread looper = new GpsLoggerServiceThread();
		looper.start();
		try {
			looper.ready.acquire();
		} catch (InterruptedException e) {
			d("GPSLoggerServiceThread����ʱ���������ж��쳣" + e);
		}

		mHeartbeatTimer = new Timer("heartbeat", true);

		googleMapOffsetRequester = new GoogleMapOffsetRequester(
				GpsLoggerService.this);
		googleMapOffsetRequester.start();

		mLoggingState = GpsConstants.GPS_LOGGER_STOPPED;
		mStartNextSegment = false;
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mHaveSendNotificationForProviderDisabled = true;

		crashRestoreState();
		mWaypointCount = getWaypointCount(mTrackId);
	}

	private long getWaypointCount(long trackId) {
		int waypointCount = 0;
		if (trackId > 0) {
			Cursor waypointsCountCurosr = null;
			Uri countUri = Uri.withAppendedPath(Tracks.CONTENT_URI, trackId
					+ "/" + Waypoints.TABLE);
			try {
				waypointsCountCurosr = getContentResolver().query(countUri,
						new String[] { "count(*) AS count" }, null, null, null);
				if (waypointsCountCurosr != null
						&& waypointsCountCurosr.moveToFirst()) {
					waypointCount = waypointsCountCurosr.getInt(0);
				} else {
					waypointCount = 0;
				}
			} finally {
				if (waypointsCountCurosr != null) {
					waypointsCountCurosr.close();
				}
			}
		}
		return waypointCount;
	}

	@Override
	public void onDestroy() {
		d("onDestroy()");
		super.onDestroy();

		mHeartbeatTimer.cancel();
		mHeartbeatTimer.purge();

		stopLogging();
		loopQuit();

		googleMapOffsetRequester.loopQuit();
	}

	private void loopQuit() {
		Message msg = Message.obtain();
		msg.what = MSG_STOP_LOOPER;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return Service.START_STICKY;
	}

	public void handleCommand(Intent intent) {
		d("handleCommand():" + intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		d("onBind():" + intent);
		return this.mBinder;
	}

	private IBinder mBinder = new IGpsLoggerServiceRemote.Stub() {

		@Override
		public Uri storeMediaUri(Uri mediaUri) throws RemoteException {
			return GpsLoggerService.this.storeMediaUri(mediaUri);
		}

		@Override
		public void storeDerivedDataSource(String sourceName)
				throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void stopLogging() throws RemoteException {
			GpsLoggerService.this.stopLogging();

		}

		@Override
		public long startLogging() throws RemoteException {
			GpsLoggerService.this.startLogging();
			d("startLogging() throws RemoteException");
			return mTrackId;
		}

		@Override
		public long resumeLogging() throws RemoteException {
			GpsLoggerService.this.resumeLogging();
			return mSegmentId;
		}

		@Override
		public void pauseLogging() throws RemoteException {
			GpsLoggerService.this.pauseLogging();
			d("pauseLogging() throws RemoteException");

		}

		@Override
		public int loggingState() throws RemoteException {
			return mLoggingState;
		}

		@Override
		public boolean isMediaPrepared() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Location getLastWaypoint() throws RemoteException {
			return GpsLoggerService.this.getLastWaypoint();
		}

		@Override
		public long getTrackId() throws RemoteException {
			return mTrackId;
		}
	};

	/**
	 * @return ���һ�βɼ�����GPS����
	 */
	private Location getLastWaypoint() {
		if (isLogging()) {
			return mPreviousLocation;
		}
		return null;
	}

	private Uri storeMediaUri(Uri mediaUri) {
		if (mTrackId != -1 && mSegmentId != -1 && mWaypointId != -1) {
			Uri saveUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId
					+ "/" + Segments.TABLE + "/" + mSegmentId + "/"
					+ Waypoints.TABLE + "/" + mWaypointId + "/" + Media.TABLE);
			ContentValues values = new ContentValues();
			values.put(Media.TRACK, mTrackId);
			values.put(Media.SEGMENT, mSegmentId);
			values.put(Media.WAYPOINT, mWaypointId);
			values.put(Media.URI, mediaUri.toString());
			return getContentResolver().insert(saveUri, values);
		}
		d("���޹켣�㣬ý������δ�ܼ������ݿ�");
		return null;
	}

	/**
	 * ��ϵͳ����
	 */
	private void updateWakeLock() {
		if (isLogging()) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			if (mWakeLock != null) {
				mWakeLock.release();
				mWakeLock = null;
			}
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			mWakeLock.acquire();
		} else {
			if (mWakeLock != null) {
				mWakeLock.release();
				mWakeLock = null;
			}
		}
	}

}
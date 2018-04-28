package com.cnlaunch.mycar.gps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.common.utils.StringUtil;
import com.cnlaunch.mycar.gps.database.GpsTrack.Media;
import com.cnlaunch.mycar.gps.database.GpsTrack.Segments;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import com.cnlaunch.mycar.gps.service.GpsLoggerServiceManager;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapDisplayActivity extends MapActivity {
	private final String TAG = "MapDisplayActivity";
	private final int REQUEST_CODE_PHOTOHRAPH = 1;
	private final String TRACK_ID = "track_id";

	private Context mContext;
	private Handler mHandler;
	private MapView mMapView;
	private GeoPoint mLastGeoPoint;
	private MapController mMapController;
	private CustomMyLocationOverlay mMylocation;
	private WakeLock mWakeLock;

	private SegmentOverlay mLastSegmentOverlay;

	private ContentObserver mTrackSegmentsObserver;
	private ContentObserver mSegmentWaypointsObserver;
	private ContentObserver mTrackMediasObserver;

	private SharedPreferences mSharedPreferences;
	private OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;

	private Uri mTrackUri;
	private long mTrackId = -1;
	private long mLastSegmentId = -1;
	private GpsLoggerServiceManager mGpsLoggerServiceManager;
	private ContentResolver mResolver;

	private ImageButton button_menu;
	private ImageButton button_start;
	private ImageButton button_pause;
	private ImageButton button_stop;
	private ImageButton button_note;
	private ImageButton button_photo;
	private ImageButton button_locate;
	private ImageButton button_resume;
	private View gps_menu_before_start;
	private View gps_menu_after_start;

	private Runnable mServiceConnected;

	/**
	 * �˱��������� �� onPause()ʱ�����㼣��¼�����״̬�� ��onDestroy()ʱ�����˱����Ƿ�Ϊ �����ڼ�¼�С���������ͣ��¼����
	 * ������ǣ���ر��㼣��¼����
	 */
	private int mGpsLoggingState = GpsConstants.GPS_LOGGER_UNKNOWN;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gps_map);
		mContext = this;
		createListeners();

		button_menu = (ImageButton) findViewById(R.id.gps_button_menu);
		button_start = (ImageButton) findViewById(R.id.gps_button_start);
		button_pause = (ImageButton) findViewById(R.id.gps_button_pause);
		button_stop = (ImageButton) findViewById(R.id.gps_button_stop);
		button_note = (ImageButton) findViewById(R.id.gps_button_note);
		button_photo = (ImageButton) findViewById(R.id.gps_button_photo);
		button_locate = (ImageButton) findViewById(R.id.gps_button_locate);
		button_resume = (ImageButton) findViewById(R.id.gps_button_resume);
		gps_menu_before_start = findViewById(R.id.gps_menu_before_start);
		gps_menu_after_start = findViewById(R.id.gps_menu_after_start);

		// ׼����ͼͼ��
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setClickable(true);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setTraffic(false);// ��ͨͼ

		// ��ʼ��ͼ��ʾ����
		mMapController = mMapView.getController();
		mMapController.setZoom(15);// ��ʼ�Ŵ���

		// ��ǰ���ڵ�λ��
		mMylocation = new CustomMyLocationOverlay(mContext, mMapView);

		mGpsLoggerServiceManager = new GpsLoggerServiceManager(
				MapDisplayActivity.this);

		mResolver = getContentResolver();

		// �㼣�����߳�
		final Semaphore calulatorSemaphore = new Semaphore(0);
		Thread calulator = new Thread("OverlayCalculator") {
			@Override
			public void run() {
				Looper.prepare();
				mHandler = new Handler();
				calulatorSemaphore.release();
				Looper.loop();
			}
		};
		calulator.start();
		try {
			calulatorSemaphore.acquire();
		} catch (InterruptedException e) {
			Log.e(TAG, "Failed waiting for a semaphore", e);
		}

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		mSharedPreferences
				.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

		mTrackUri = getIntent().getData();
		if (mTrackUri != null) {
			long trackId = Long.parseLong(mTrackUri.getLastPathSegment());
			setTrackId(trackId);
		}
		// �����㼣ͼ��
		createDataOverlays();

		// ȷ����ͼ��Ұ����λ��
		updateMapCenter();

		// ע�ᰴť�¼�
		setViewListener();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		// ����
		if (requestCode == REQUEST_CODE_PHOTOHRAPH) {
			new Thread() {
				public void run() {
					mGpsLoggerServiceManager.startup(MapDisplayActivity.this,
							mServiceConnected);

					File tmpPhoto = new File(new File(
							Env.getAppRootDirInSdcard(),
							GpsConstants.DIR_NAME_TMP),
							GpsConstants.FILE_NAME_TMP_PHOTO);

					if (!tmpPhoto.exists()) {
						//Log.e(TAG, "������Ƭʱ��������SD���Ƿ�װ����");
						return;
					}

					File saveDir = new File(Env.getAppRootDirInSdcard(),
							GpsConstants.DIR_NAME_PHOTO);
					if (!saveDir.exists()) {
						if (!saveDir.mkdirs()) {
							//Log.e(TAG, "������Ƭʱ��������SD���Ƿ�װ����");
							return;
						}
					}

					File savePhoto = new File(saveDir, new Date().getTime()
							+ ".jpg");
					tmpPhoto.renameTo(savePhoto);
					Uri imgUri = Uri.fromFile(savePhoto);
					mGpsLoggerServiceManager.storeMediaUri(imgUri);

				}
			}.start();
		}

	}

	private void checkLocationProviderEnable() {
		if (!Env.isGpsLocationProviderEnable(mContext)) {
			showAlertToOpenGps();
		}
	}

	private void showAlertToOpenGps() {
		Toast.makeText(mContext, R.string.gps_unavailable, Toast.LENGTH_LONG).show();
	}

	private void showDialogToOpenLocationProvider() {
		final CustomDialog customDialog = new CustomDialog(this);
		customDialog.setTitle(R.string.gps_notice);
		customDialog.setMessage(R.string.gps_provider_undisabled);
		customDialog.setPositiveButton(R.string.gps_open,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
						customDialog.dismiss();

					}
				});
		customDialog.setNegativeButton(R.string.gps_cancle,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						customDialog.dismiss();
					}
				});
		customDialog.show();
	}

	private void setTrackId(long trackId) {
		if (mTrackId != trackId && trackId > 0) {
			mTrackId = trackId;
			mTrackUri = ContentUris.withAppendedId(Tracks.CONTENT_URI, trackId);
			updateTitleBar();
			createDataOverlays();
		}
	}

	private void updateTitleBar() {
		Cursor trackCursor = managedQuery(
				ContentUris.withAppendedId(Tracks.CONTENT_URI, this.mTrackId),
				new String[] { Tracks.NAME }, null, null, null);
		if (trackCursor != null && trackCursor.moveToFirst()) {
			String trackName = trackCursor.getString(0);
			this.setTitle(this.getString(R.string.gps_track) + ": " + trackName);
		}
	}

	protected GeoPoint getLastGeoPoint() {
		Cursor waypoint = null;
		GeoPoint lastPoint = null;
		// ��service�л�ȡ���һ����
		Location lastLoc = mGpsLoggerServiceManager.getLastWaypoint();
		if (lastLoc != null) {
			int microLatitude = (int) (lastLoc.getLatitude() * 1E6d);
			int microLongitude = (int) (lastLoc.getLongitude() * 1E6d);
			lastPoint = new GeoPoint(microLatitude, microLongitude);
		}

		// ���δ��ȡ������ӵ�ǰ�켣�У���ȡ���һ����
		if ((lastPoint == null || lastPoint.getLatitudeE6() == 0 || lastPoint
				.getLongitudeE6() == 0) && mTrackId != -1) {
			try {
				ContentResolver resolver = this.getContentResolver();
				waypoint = resolver.query(
						Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId
								+ "/waypoints"), new String[] {
								Waypoints.LATITUDE,
								Waypoints.LONGITUDE,
								"max(" + Waypoints.TABLE + "." + Waypoints._ID
										+ ")" }, null, null, null);
				if (waypoint != null && waypoint.moveToLast()) {
					int microLatitude = (int) (waypoint.getDouble(0) * 1E6d);
					int microLongitude = (int) (waypoint.getDouble(1) * 1E6d);
					lastPoint = new GeoPoint(microLatitude, microLongitude);
				}
			} finally {
				if (waypoint != null) {
					waypoint.close();
				}
			}
		}

		// �����û�л�ȡ���㣬���ȡ��ǰ�ֻ��ܶ�ȡ�����һ����
		if (lastPoint == null || lastPoint.getLatitudeE6() == 0
				|| lastPoint.getLongitudeE6() == 0) {
			lastPoint = getLastKnowGeopointLocation();
		}
		return lastPoint;
	}

	/**
	 * @return �ֻ���ǰ���һ�βɼ�����������Ϣ
	 */
	private GeoPoint getLastKnowGeopointLocation() {
		// Ĭ��λ���ǡ�Ԫ���ܲ����ڵء�
		int microLatitude = 22664035;
		int microLongitude = 114054175;
		LocationManager locationManager = (LocationManager) this
				.getApplication().getSystemService(Context.LOCATION_SERVICE);
		Location locationCoarse = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (locationCoarse != null) {
			microLatitude = (int) (locationCoarse.getLatitude() * 1E6);
			microLongitude = (int) (locationCoarse.getLongitude() * 1E6);
		}

		GeoPoint geoPoint = new GeoPoint(microLatitude, microLongitude);
		return geoPoint;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// �󶨷���
		mGpsLoggerServiceManager.startup(this, mServiceConnected);

		// ���gps�Ƿ��ڴ�״̬
		checkLocationProviderEnable();

		// ע���㼣��¼���ݱ仯�۲���
		Uri trackUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId
				+ "/segments");
		Uri lastSegmentUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId
				+ "/segments/" + mLastSegmentId + "/waypoints");
		Uri mediaUri = ContentUris.withAppendedId(Media.CONTENT_URI, mTrackId);

		mResolver = this.getContentResolver();
		mResolver.unregisterContentObserver(mTrackSegmentsObserver);
		mResolver.unregisterContentObserver(mSegmentWaypointsObserver);
		mResolver.unregisterContentObserver(mTrackMediasObserver);

		mResolver
				.registerContentObserver(trackUri, false, mTrackMediasObserver);
		mResolver.registerContentObserver(lastSegmentUri, true,
				mSegmentWaypointsObserver);
		mResolver.registerContentObserver(mediaUri, true, mTrackMediasObserver);
		mMylocation.enableMyLocation();
		mMylocation.enableCompass();
		acquireWakeLock();
	}

	@Override
	protected void onSaveInstanceState(Bundle save) {
		super.onSaveInstanceState(save);
		save.putLong(TRACK_ID, this.mTrackId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle load) {
		if (load != null) {
			super.onRestoreInstanceState(load);
			long loadTrackId = load.getLong(TRACK_ID);
			setTrackId(loadTrackId);
		}

	}

	@Override
	protected void onPause() {
		mGpsLoggingState = mGpsLoggerServiceManager.getLoggingState();
		this.mGpsLoggerServiceManager.shutdown(this);
		mMylocation.disableMyLocation();
		mMylocation.disableCompass();
		releaseWakeLock();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mResolver.unregisterContentObserver(mTrackSegmentsObserver);
		mResolver.unregisterContentObserver(mSegmentWaypointsObserver);
		mResolver.unregisterContentObserver(mTrackMediasObserver);

		mHandler.post(new Runnable() {
			public void run() {
				Looper.myLooper().quit();
			}
		});
		mSharedPreferences
				.unregisterOnSharedPreferenceChangeListener(this.mSharedPreferenceChangeListener);

		if (mGpsLoggingState != GpsConstants.GPS_LOGGER_LOGGING
				&& mGpsLoggingState != GpsConstants.GPS_LOGGER_PAUSED) {
			stopService(new Intent(GpsConstants.GPS_LOGGER_SERVICE_NAME));
			//Log.v(TAG, "û�д����㼣��¼�����������˳�����ʱ���ر��㼣��¼����");
		}

	}

	private void createDataOverlays() {
		mLastSegmentOverlay = null;
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mMylocation);

		if (mTrackId == -1) {
			SegmentOverlay segmentOverlay = new SegmentOverlay(this, null,
					mMapView, mHandler);
			mMapView.getOverlays().add(segmentOverlay);
			return;
		}

		// ����㼣ͼ��
		Cursor segments = null;
		try {
			Uri segmentsUri = Uri.withAppendedPath(Tracks.CONTENT_URI,
					this.mTrackId + "/segments");
			segments = mResolver.query(segmentsUri,
					new String[] { Segments._ID }, null, null, null);
			if (segments != null && segments.moveToFirst()) {
				do {
					long segmentId = segments.getLong(0);
					Uri segmentUri = ContentUris.withAppendedId(segmentsUri,
							segmentId);
					SegmentOverlay segmentOverlay = new SegmentOverlay(this,
							segmentUri, mMapView, mHandler);
					IconOverlay iconOverlay = new IconOverlay(this, segmentUri,
							mMapView, mHandler);
					mMapView.getOverlays().add(segmentOverlay);
					mMapView.getOverlays().add(iconOverlay);
					mLastSegmentOverlay = segmentOverlay;
					mLastSegmentId = segmentId;
				} while (segments.moveToNext());
			}
		} finally {
			if (segments != null) {
				segments.close();
			}
		}

		Uri lastSegmentUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId
				+ "/" + Segments.TABLE + "/" + mLastSegmentId + "/"
				+ Waypoints.TABLE);
		mResolver.unregisterContentObserver(mSegmentWaypointsObserver);
		mResolver.registerContentObserver(lastSegmentUri, false,
				mSegmentWaypointsObserver);
	}

	public void onDataOverlayChanged() {
		// ���㼣
		this.mMapView.postInvalidate();
	}

	private void updateDataOverlays() {
		Uri segmentsUri = Uri.withAppendedPath(Tracks.CONTENT_URI,
				this.mTrackId + "/" + Segments.TABLE);
		Cursor segmentsCursor = null;
		List<?> overlays = mMapView.getOverlays();
		int segmentOverlaysCount = 0;

		for (Object overlay : overlays) {
			if (overlay instanceof SegmentOverlay) {
				segmentOverlaysCount++;
			}
		}
		try {
			segmentsCursor = mResolver.query(segmentsUri,
					new String[] { Segments._ID }, null, null, null);
			if (segmentsCursor != null
					&& segmentsCursor.getCount() == segmentOverlaysCount) {
				//Log.e(TAG, "segment�ļ�¼����������ʾ��ͼ����һ��");
			} else {
				// ���segment�ļ�¼����������ʾ��ͼ������һ�£���ˢ����������
				createDataOverlays();
			}
		} finally {
			if (segmentsCursor != null) {
				segmentsCursor.close();
			}
		}
	}

	/**
	 * ע��ؼ��¼�
	 */
	private void setViewListener() {

		button_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMenu();

			}
		});
		button_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Env.isGpsLocationProviderEnable(mContext)) {
					startGpsRecord();
				} else {
					showDialogToOpenLocationProvider();
				}
			}

		});
		button_resume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resumeGpsRecord();
			}

		});
		button_pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pauseGpsRecord();
			}
		});
		button_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopGpsRecord();

			}
		});
		button_note.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mGpsLoggerServiceManager.getLastWaypoint() != null) {
					showNoteDialog();
				} else {
					final CustomDialog customDialog = new CustomDialog(
							MapDisplayActivity.this);
					customDialog
							.setMessage(getString(R.string.gps_note_with_no_waypoint));
					customDialog.setTitle(R.string.gps_notice);
					customDialog.setNegativeButton(R.string.gps_cancle,
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									customDialog.dismiss();
								}
							});
					customDialog.setPositiveButton(R.string.gps_go_on,
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									customDialog.dismiss();
									showNoteDialog();
								}

							});
					customDialog.show();
				}

			}
		});
		button_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mGpsLoggerServiceManager.getLastWaypoint() != null) {
					getPhoto();
				} else {
					final CustomDialog customDialog = new CustomDialog(
							MapDisplayActivity.this);
					customDialog.setTitle(getString(R.string.gps_notice));
					customDialog
							.setMessage(getString(R.string.gps_photo_with_no_waypoint));
					customDialog.setNegativeButton(
							getString(R.string.gps_cancle),
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									customDialog.dismiss();

								}
							});
					customDialog.setPositiveButton(
							getString(R.string.gps_go_on),
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									customDialog.dismiss();
									getPhoto();
								}

							});
					customDialog.show();
				}

			}
		});
		button_locate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				moveToCurrentTrack();
				setMapCenterToFoot();

			}
		});

	}

	/**
	 * ���ݷ���״̬�����ÿ��ư�ť״̬
	 */
	private void freshControlState() {
		int loggingState = mGpsLoggerServiceManager.getLoggingState();

		switch (loggingState) {
		case GpsConstants.GPS_LOGGER_LOGGING:
			gps_menu_before_start.setVisibility(View.GONE);
			gps_menu_after_start.setVisibility(View.VISIBLE);
			button_resume.setVisibility(View.GONE);
			button_pause.setVisibility(View.VISIBLE);
			break;
		case GpsConstants.GPS_LOGGER_PAUSED:
			gps_menu_before_start.setVisibility(View.GONE);
			gps_menu_after_start.setVisibility(View.VISIBLE);
			button_resume.setVisibility(View.VISIBLE);
			button_pause.setVisibility(View.GONE);
			break;
		case GpsConstants.GPS_LOGGER_STOPPED:
		case GpsConstants.GPS_LOGGER_UNKNOWN:// ������ֹͣ �� ״̬δ֪ʱ��ֻ��ʾ����ʼ����ť
			gps_menu_before_start.setVisibility(View.VISIBLE);
			gps_menu_after_start.setVisibility(View.GONE);
			break;
		default:
			break;
		}

	}

	private void showMenu() {
		startActivity(new Intent(mContext, TrackListActivity.class));
	}

	private void getPhoto() {
		if (!Env.isSDCardAvailable(mContext)) {
			Toast.makeText(mContext, R.string.gps_cannot_save_photo_for_sdcard_unavailable, Toast.LENGTH_LONG);
			return;
		}
		File dir = new File(Env.getAppRootDirInSdcard(),
				GpsConstants.DIR_NAME_TMP);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				//Log.e(TAG, "������Ƭ�ļ���ʱ��������SD���Ƿ�װ����");
				return;
			}
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(dir, GpsConstants.FILE_NAME_TMP_PHOTO)));
		startActivityForResult(intent, REQUEST_CODE_PHOTOHRAPH);
	}

	private void showNoteDialog() {
		if (!Env.isSDCardAvailable(mContext)) {
			//Log.e(TAG, "SD���쳣���޷��洢����");
			return;
		}

		final CustomDialog customDialog = new CustomDialog(this);
		final EditText editText = new EditText(mContext);
		editText.setMinLines(6);
		customDialog.setView(editText);
		customDialog.setTitle(R.string.gps_note);

		customDialog.setPositiveButton(R.string.gps_save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = editText.getText().toString();
				if (StringUtil.filterBlank(text).length() > 0) {
					saveNote(text);
				}
				customDialog.dismiss();
			}
		});
		customDialog.setNegativeButton(R.string.gps_cancel, new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();

			}
		});
		customDialog.show();
	}

	private void saveNote(final String text) {
		new Thread() {
			public void run() {
				mGpsLoggerServiceManager.startup(MapDisplayActivity.this,
						mServiceConnected);

				File dir = new File(Env.getAppRootDirInSdcard(),
						GpsConstants.DIR_NAME_NOTE);
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						//Log.e(TAG, "�������ʱ��������SD���Ƿ�װ����");
						return;
					}
				}

				File note = new File(dir, new Date().getTime() + ".txt");

				FileOutputStream fos;

				try {
					fos = new FileOutputStream(note);
					OutputStreamWriter writer = new OutputStreamWriter(fos,
							"UTF-8");
					writer.write(text);
					writer.close();
					fos.flush();
					fos.close();

					Uri noteUri = Uri.fromFile(note);
					mGpsLoggerServiceManager.storeMediaUri(noteUri);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	protected void stopGpsRecord() {
		final CustomDialog customDialog = new CustomDialog(this);
		customDialog.setTitle(R.string.gps_tip);
		customDialog.setMessage(R.string.gps_ensure_to_stop_track);
		customDialog.setPositiveButton(R.string.gps_sure, new OnClickListener() {

			@Override
			public void onClick(View v) {

				mGpsLoggerServiceManager.stopGpsLogging();
				freshControlState();
				customDialog.dismiss();
			}
		});
		customDialog.setNegativeButton(R.string.gps_cancel, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog.dismiss();
			}
		});
		customDialog.show();

	}

	protected void resumeGpsRecord() {
		mGpsLoggerServiceManager.resumeGpsLogging();
		freshControlState();
	}

	protected void pauseGpsRecord() {
		mGpsLoggerServiceManager.pauseGpsLogging();
		freshControlState();
	}

	protected void startGpsRecord() {
		final CustomDialog customDialog = new CustomDialog(this);
		final EditText editText = new EditText(mContext);
		editText.setMinLines(3);
		editText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		editText.setText(Format.DateStr.getDateTime() + getString(R.string.gps_track_start));
		editText.selectAll();
		customDialog.setTitle(R.string.gps_track_name);
		customDialog.setView(editText);

		customDialog.setPositiveButton(R.string.gps_save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				String trackName = editText.getText().toString();
				if (trackName.length() <= 0) {
					Toast.makeText(mContext, R.string.gps_track_name_cannot_be_blank, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				Log.e(TAG, "mGpsLoggerServiceManager.startGpsLogging()");
				Long trackId = mGpsLoggerServiceManager.startGpsLogging();
				updateTrackName(trackId, trackName);
				setTrackId(trackId);
				freshControlState();
				customDialog.dismiss();
			}

		});
		customDialog.setNegativeButton(R.string.gps_cancel, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog.dismiss();
			}
		});
		customDialog.show();
	}

	/**
	 * �޸Ĺ켣����
	 * 
	 * @param trackId
	 * @param trackName
	 */
	private void updateTrackName(Long trackId, String trackName) {
		Uri trackUri = ContentUris.withAppendedId(Tracks.CONTENT_URI, trackId);
		ContentValues values = new ContentValues();
		values.put(Tracks.NAME, trackName);
		mResolver.update(trackUri, values, null, null);
	}

	/**
	 * ����ͼ��ʾ������Ϊ���ڼ�¼�Ĺ켣
	 */
	private void moveToCurrentTrack() {
		setTrackId(mGpsLoggerServiceManager.getTrackId());
	}

	/**
	 * ����ǰλ�ã��ƶ�����Ļ����
	 */
	private void setMapCenterToFoot() {
		GeoPoint currentGeoPoint = mMylocation.getMyLocation();
		if (currentGeoPoint == null) {
			currentGeoPoint = mMylocation
					.addOffset(getLastKnowGeopointLocation());
		}
		mMapController.animateTo(currentGeoPoint);

	}

	/**
	 * ����ͼ��λ�����켣���յ㡣����յ㲻���ڣ�ͬ��λ����ǰλ��
	 */
	private void updateMapCenter() {
		mLastGeoPoint = getLastGeoPoint();
		if (mLastGeoPoint != null) {
			mMapController.animateTo(mMylocation.addOffset(mLastGeoPoint));
		} else {
			setMapCenterToFoot();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * ��ʼ���󶨷���ʱ��ִ�еĻص�����
	 */
	private void createListeners() {
		mServiceConnected = new Runnable() {
			public void run() {
				freshControlState();
				if (mTrackId == -1) {
					setTrackId(mGpsLoggerServiceManager.getTrackId());
				}
//				Log.e(TAG, "�󶨷���ɹ���Ļص���mServiceConnected"
//						+ mGpsLoggerServiceManager.getTrackId());
			}
		};

		mSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				// TODO Auto-generated method stub

			}

		};

		mTrackMediasObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfUpdate) {
				if (!selfUpdate) {
					if (mLastSegmentOverlay != null) {
						MapDisplayActivity.this.updateDataOverlays();
					}
				}
			}
		};
		mTrackSegmentsObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfUpdate) {
				if (!selfUpdate) {
					MapDisplayActivity.this.updateDataOverlays();
				}
			}
		};
		mSegmentWaypointsObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfUpdate) {
				if (!selfUpdate) {
					if (mLastSegmentOverlay != null) {
						updateMapCenter();
					}
				}
			}
		};
	}

	/**
	 * ������������Ļ�ر�
	 */
	private void acquireWakeLock() {
		PowerManager pm = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
		mWakeLock.acquire();
	}

	/**
	 * �ͷ���������Ļ�ر�
	 */
	private void releaseWakeLock() {
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}
}

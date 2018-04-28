package com.cnlaunch.mycar.gps;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.gps.database.GpsTrack.Media;
import com.cnlaunch.mycar.gps.database.GpsTrack.Tracks;
import com.cnlaunch.mycar.gps.database.GpsTrack.Waypoints;
import com.cnlaunch.mycar.gps.service.GpsLoggerServiceManager;
import com.cnlaunch.mycar.gps.tasks.GpxCreator;
import com.cnlaunch.mycar.gps.tasks.GpxParser;

public class TrackListActivity extends BaseActivity {
	private final String TAG = "TrackListActivity";
	private ListView listview_track_list;
	private SimpleAdapter trackAdapter;
	private Handler mHandler;
	private final int MSG_REFRESH_DATA = 0;
	private final int MSG_EXPORT_START = 1;
	private final int MSG_EXPORT_SUCC = 2;
	private final int MSG_EXPORT_ERR = 3;
	private final String GPX_FILE_PATH = "gpx_file_path";
	private GpsLoggerServiceManager mGpsLoggerServiceManager;
	private ProgressDialog mProgressDialog;

	private Context mContext;
	private ArrayList<HashMap<String, String>> mTrackList;
	private final String orderby = Tracks._ID + " DESC";
	private final String WAYPOINT_COUNT = "waypoint_count";
	private final String NOTE_COUNT = "note_count";
	private final String PHOTO_COUNT = "photo_count";

	@Override
	protected void onResume() {
		super.onResume();
		// 绑定服务
		mGpsLoggerServiceManager.startup(mContext, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 解绑服务
		mGpsLoggerServiceManager.shutdown(mContext);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = TrackListActivity.this;
		mGpsLoggerServiceManager = new GpsLoggerServiceManager(mContext);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_REFRESH_DATA:
					trackAdapter.notifyDataSetChanged();
					break;
				case MSG_EXPORT_START:
					mProgressDialog
							.setMessage(getString(R.string.gps_wait_for_create_gpxfile));
					break;
				case MSG_EXPORT_SUCC:
					mProgressDialog.dismiss();
					final CustomAlertDialog customAlertDialog = new CustomAlertDialog(
							TrackListActivity.this);
					customAlertDialog.setTitle(R.string.gps_notice);
					customAlertDialog
							.setMessage(getString(R.string.gps_export_succ)
									+ "\n"
									+ msg.getData().getString("gpx_file_path"));
					customAlertDialog.setPositiveButton(R.string.gps_ensure,
							new android.view.View.OnClickListener() {

								@Override
								public void onClick(View v) {
									customAlertDialog.dismiss();
								}
							});
					customAlertDialog.show();
					break;
				case MSG_EXPORT_ERR:
					mProgressDialog.dismiss();
					final CustomAlertDialog customAlertDialog1 = new CustomAlertDialog(
							TrackListActivity.this);
					customAlertDialog1.setTitle(R.string.gps_notice);
					customAlertDialog1
							.setMessage(getString(R.string.gps_export_failed));
					customAlertDialog1.setPositiveButton(R.string.gps_ensure,
							new android.view.View.OnClickListener() {

								@Override
								public void onClick(View v) {
									customAlertDialog1.dismiss();
								}
							});
					customAlertDialog1.show();
					break;
				default:
					break;
				}
			}
		};

		// 导入GPX文件
		importGpx();

		setContentView(R.layout.gps_track_list, R.layout.custom_title);
		setCustomeTitleLeft(R.string.track_list);
		setCustomeTitleRight("");

		listview_track_list = (ListView) findViewById(R.id.listview_track_list);

		Cursor tracksCursor = managedQuery(Tracks.CONTENT_URI, new String[] {
				Tracks._ID, Tracks.NAME, Tracks.CREATE_TIME }, null, null,
				orderby);

		if (tracksCursor == null || tracksCursor.getCount() == 0) {
			Toast.makeText(this, R.string.gps_no_track, Toast.LENGTH_LONG).show();
			return;
		}

		mTrackList = new ArrayList<HashMap<String, String>>();
		while (tracksCursor.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Tracks._ID, tracksCursor.getString(0));
			map.put(Tracks.NAME, tracksCursor.getString(1));
			map.put(Tracks.CREATE_TIME, Format.DateStr.getDateTime(new Date(
					tracksCursor.getLong(2))));
			map.put(WAYPOINT_COUNT, "0");
			map.put(NOTE_COUNT, "0");
			map.put(PHOTO_COUNT, "0");
			mTrackList.add(map);
		}

		String[] fromColumns = new String[] { Tracks.NAME, Tracks.CREATE_TIME,
				WAYPOINT_COUNT, NOTE_COUNT, PHOTO_COUNT };
		int[] toItems = new int[] { R.id.name, R.id.create_time,
				R.id.gps_waypoint_count, R.id.gps_note_count,
				R.id.gps_photo_count };

		trackAdapter = new SimpleAdapter(this, mTrackList,
				R.layout.gps_track_list_item, fromColumns, toItems) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				HashMap<String, String> map = mTrackList.get(position);
				TextView textview_waypoint_count = (TextView) view
						.findViewById(R.id.gps_waypoint_count);
				TextView textview_note_count = (TextView) view
						.findViewById(R.id.gps_note_count);
				TextView textview_photo_count = (TextView) view
						.findViewById(R.id.gps_photo_count);
				View view_waypoint_count_icon = view
						.findViewById(R.id.gps_waypoint_count_icon);
				View view_note_count_icon = view
						.findViewById(R.id.gps_note_count_icon);
				View view_photo_count_icon = view
						.findViewById(R.id.gps_photo_count_icon);
				if (map.get(WAYPOINT_COUNT).equals("0")) {
					textview_waypoint_count.setVisibility(View.INVISIBLE);
					view_waypoint_count_icon.setVisibility(View.INVISIBLE);
				} else {
					textview_waypoint_count.setVisibility(View.VISIBLE);
					view_waypoint_count_icon.setVisibility(View.VISIBLE);
				}
				if (map.get(NOTE_COUNT).equals("0")) {
					textview_note_count.setVisibility(View.INVISIBLE);
					view_note_count_icon.setVisibility(View.INVISIBLE);
				} else {
					textview_note_count.setVisibility(View.VISIBLE);
					view_note_count_icon.setVisibility(View.VISIBLE);
				}
				if (map.get(PHOTO_COUNT).equals("0")) {
					textview_photo_count.setVisibility(View.INVISIBLE);
					view_photo_count_icon.setVisibility(View.INVISIBLE);
				} else {
					textview_photo_count.setVisibility(View.VISIBLE);
					view_photo_count_icon.setVisibility(View.VISIBLE);
				}
				return view;
			}
		};
		listview_track_list.setAdapter(trackAdapter);

		listview_track_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				HashMap<String, String> map = mTrackList.get(position);
				dialogEdit(Long.parseLong(map.get(Tracks._ID)));

			}
		});

		loadOtherContent();

	}

	private void importGpx() {
		Intent intent = getIntent();
		final Uri gpxFileUri = intent.getData();
		if (gpxFileUri != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
			String path = gpxFileUri.getPath();
			if (path == null) {
				return;
			}
			final File gpxFilePath = new File(path);
			Log.e(TAG, gpxFilePath.toString());

			final CustomAlertDialog customAlertDialog = new CustomAlertDialog(
					TrackListActivity.this);
			customAlertDialog.setMessage(getString(R.string.gps_is_ensure_to_import));
			customAlertDialog.setNegativeButton(R.string.gps_cancle,
					new android.view.View.OnClickListener() {

						@Override
						public void onClick(View v) {
							customAlertDialog.cancel();
							customAlertDialog.dismiss();
							TrackListActivity.this.finish();
						}
					});
			customAlertDialog.setPositiveButton(R.string.gps_ensure,
					new android.view.View.OnClickListener() {

						@Override
						public void onClick(View v) {
							customAlertDialog.cancel();
							customAlertDialog.dismiss();

							if (gpxFilePath.exists()) {
								new Thread() {
									@Override
									public void run() {
										GpxParser parser = new GpxParser(
												mContext);
										Uri trackUri = parser.importUri(Uri
												.fromFile(gpxFilePath));
										showTrackInMap(trackUri);
									}
								}.start();

							}
						}
					});
			customAlertDialog.show();
		}
	}

	private void loadOtherContent() {
		new Thread() {
			@Override
			public void run() {
				loadStatistics();
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_REFRESH_DATA;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	private void loadStatistics() {
		synchronized (mTrackList) {
			Cursor waypointsCursor = managedQuery(Waypoints.CONTENT_URI,
					new String[] { Waypoints.TRACK,
							"count(*) as " + WAYPOINT_COUNT },
					" 0==0) group by (Waypoints.TRACK", null, Waypoints.TRACK
							+ " desc ");
			if (waypointsCursor != null) {
				int start = 0;
				while (waypointsCursor.moveToNext()) {
					String trackId = waypointsCursor.getString(0);
					String waypointCount = waypointsCursor.getString(1);
					int len = mTrackList.size();
					int end = len + start;
					for (int i = start; i < end; i++) {
						HashMap<String, String> map = mTrackList.get(i % len);
						if (map.get(Tracks._ID).equals(trackId)) {
							map.put(WAYPOINT_COUNT, waypointCount);
							break;
						}
					}
					start++;

				}
			}

			Cursor mediaCursor = managedQuery(Media.CONTENT_URI, new String[] {
					Media.TRACK, Media.KIND, "count(*) as " + WAYPOINT_COUNT },
					" 0==0) group by (" + Waypoints.TRACK + "),(" + Media.KIND,
					null, Waypoints.TRACK + " desc, " + Media.KIND + " desc ");
			if (mediaCursor != null) {
				int start = 0;
				while (mediaCursor.moveToNext()) {
					String trackId = mediaCursor.getString(0);
					String kind = mediaCursor.getString(1);
					String mediaCount = mediaCursor.getString(2);
					int len = mTrackList.size();
					int end = len + start;
					for (int i = start; i < end; i++) {
						HashMap<String, String> map = mTrackList.get(i % len);
						if (map.get(Tracks._ID).equals(trackId)) {
							if (kind.equals("txt")) {
								map.put(NOTE_COUNT, mediaCount);
							} else if (kind.equals("jpg")) {
								map.put(PHOTO_COUNT, mediaCount);
							} else {
								Log.e(TAG, "轨迹点上关联未知类型的文件，其后缀名为：" + kind);
							}
							break;
						}
					}
					start++;

				}
			}
		}

	}

	protected void dialogEdit(final long trackId) {
		final Uri trackUri = Uri.withAppendedPath(Tracks.CONTENT_URI,
				String.valueOf(trackId));
		String mItems[] = mContext.getResources().getStringArray(R.array.gps_track_list_operate);
		final CustomDialog customDialog = new CustomDialog(
				TrackListActivity.this);
		customDialog.setTitle(R.string.gps_tarck_opterate);
		customDialog.setItems(mItems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					showEditTrackName(trackId);
					break;
				case 1:
					if (mGpsLoggerServiceManager.getTrackId() != trackId) {
						mContext.getContentResolver().delete(trackUri, null,
								null);
					} else {
						customDialog.dismiss();
						final CustomAlertDialog customAlertDialog = new CustomAlertDialog(
								TrackListActivity.this);
						customAlertDialog.setTitle(R.string.gps_notice);
						customAlertDialog.setMessage(R.string.gps_track_del_failed);
						customAlertDialog.setPositiveButton(R.string.gps_ensure,
								new android.view.View.OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										customAlertDialog.dismiss();
									}
								});
						customAlertDialog.show();
					}
					refreshList();
					break;
				case 2:
					showGpxExportDialog(trackUri);
					break;
				case 3:
					showTrackInMap(trackUri);
					break;
				default:
					break;
				}

			}
		});
		customDialog.show();

	}

	protected void showGpxExportDialog(final Uri trackUri) {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getString(R.string.gps_wait_for_export));
		mProgressDialog.show();

		new Thread() {
			@Override
			public void run() {
				mHandler.obtainMessage(MSG_EXPORT_START).sendToTarget();

				GpxCreator gpxCreator = new GpxCreator(mContext);
				File exportGpxFilePath = gpxCreator.export(trackUri);
				if (exportGpxFilePath == null) {
					mHandler.obtainMessage(MSG_EXPORT_ERR).sendToTarget();
				} else {
					Bundle bundle = new Bundle();
					bundle.putString(GPX_FILE_PATH,
							exportGpxFilePath.toString());
					Message msg = mHandler.obtainMessage(MSG_EXPORT_SUCC);
					msg.setData(bundle);
					msg.sendToTarget();
				}

			};
		}.start();

	}

	protected void refreshList() {
		synchronized (mTrackList) {
			mTrackList.clear();
			Cursor tracksCursor = managedQuery(
					Tracks.CONTENT_URI,
					new String[] { Tracks._ID, Tracks.NAME, Tracks.CREATE_TIME },
					null, null, orderby);
			while (tracksCursor.moveToNext()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Tracks._ID, tracksCursor.getString(0));
				map.put(Tracks.NAME, tracksCursor.getString(1));
				map.put(Tracks.CREATE_TIME, Format.DateStr
						.getDateTime(new Date(tracksCursor.getLong(2))));
				map.put(WAYPOINT_COUNT, "0");
				map.put(NOTE_COUNT, "0");
				map.put(PHOTO_COUNT, "0");

				mTrackList.add(map);
			}
			trackAdapter.notifyDataSetChanged();
		}

		loadOtherContent();

	}

	protected void showTrackInMap(Uri trackUri) {
		Intent intent = new Intent();
		intent.setData(trackUri);
		intent.setClass(this, MapDisplayActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		this.finish();
	}

	protected void showEditTrackName(final long trackId) {
		final CustomDialog customDialog = new CustomDialog(this);
		final EditText editText = new EditText(mContext);
		customDialog.setTitle(R.string.gps_edit_track_name);
		customDialog.setView(editText);
		customDialog.setPositiveButton(R.string.gps_ensure,
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String trackName = editText.getText().toString();
						if (trackName != null) {
							Uri currentTrackUri = Uri.withAppendedPath(
									Tracks.CONTENT_URI, String.valueOf(trackId));
							ContentValues values = new ContentValues();
							values.put(Tracks.NAME, trackName);

							getContentResolver().update(currentTrackUri,
									values, null, null);
							refreshList();
							customDialog.dismiss();
						}

					}
				});
		customDialog.setNegativeButton(R.string.gps_cancle,
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						customDialog.dismiss();

					}
				});

		customDialog.show();

	}
}
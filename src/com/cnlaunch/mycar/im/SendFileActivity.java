package com.cnlaunch.mycar.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;

public class SendFileActivity extends ImBaseActivity {
	private ListView listview_sendfile_log;
	private TextView textview_no_log;
	private SimpleAdapter mAdapter;
	private List<HashMap<String, String>> mSendFileLog = new ArrayList<HashMap<String, String>>();
	private List<HashMap<String, String>> mSendFileLogNew = new ArrayList<HashMap<String, String>>();
	private Handler mHandler;
	private final int LIST_DATA_REFRESHED = 1;
	private HashMap<Integer, SendFileTaskOperator> sendFileTaskOperators = null;

	private Context mContext;
	private ContentResolver mResolver;
	private ContentObserver mSendFileTaskContentObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_send_file, R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_send_file_log);
		setCustomeTitleRight("");
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LIST_DATA_REFRESHED:
					freshList();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}

		};

		mContext = this;
		mResolver = mContext.getContentResolver();

		initSendFileTaskOperator();

		findView();
		addListener();

		mSendFileTaskContentObserver = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfUpdate) {
				prepareSendFileLog();
			}
		};
	}

	@Override
	public void onResume() {
		prepareSendFileLog();
		mResolver.unregisterContentObserver(mSendFileTaskContentObserver);
		mResolver.registerContentObserver(SendFileTask.CONTENT_URI, true,
				mSendFileTaskContentObserver);
		super.onResume();
	}

	@Override
	public void onPause() {
		mResolver.unregisterContentObserver(mSendFileTaskContentObserver);
		super.onPause();
	}

	protected void freshList() {
		synchronized (mSendFileLogNew) {
			if (mSendFileLogNew.size() > 0) {
				listview_sendfile_log.setVisibility(View.VISIBLE);
				textview_no_log.setVisibility(View.GONE);
				mSendFileLog.clear();
				for (HashMap<String, String> map : mSendFileLogNew) {
					mSendFileLog.add(map);
				}
				mSendFileLogNew.clear();
				mAdapter.notifyDataSetChanged();
			} else {
				listview_sendfile_log.setVisibility(View.GONE);
				textview_no_log.setVisibility(View.VISIBLE);
			}
		}
	}

	private void prepareSendFileLog() {
		new Thread() {
			@Override
			public void run() {
				synchronized (mSendFileLogNew) {
					mSendFileLogNew.clear();
					Cursor c = mResolver
							.query(SendFileTask.CONTENT_URI,
									new String[] { SendFileTask._ID,
											SendFileTask.FILE_ID,
											SendFileTask.FILE_NAME,
											SendFileTask.FILE_SIZE,
											SendFileTask.STATE }, null, null,
									null);

					try {
						while (c.moveToNext()) {
							HashMap<String, String> map = new HashMap<String, String>();
							map.put(SendFileTask._ID, c.getString(0));
							map.put(SendFileTask.FILE_NAME, c.getString(2));
							map.put(SendFileTask.FILE_SIZE,
									Format.getFileSize(c.getLong(3)));
							map.put(SendFileTask.STATE,
									String.valueOf(c.getLong(4)));
							mSendFileLogNew.add(map);
						}
					} finally {
						c.close();
						c = null;
					}

					Message msg = mHandler.obtainMessage();
					msg.what = LIST_DATA_REFRESHED;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	private void findView() {
		listview_sendfile_log = (ListView) findViewById(R.id.listview_sendfile_log);
		textview_no_log = (TextView) findViewById(R.id.textview_no_log);
		findViewById(R.id.im_textbutton_menu_sendfile).setBackgroundResource(
				R.drawable.manager_toolbar_bg_selected);

	}

	private void addListener() {
		initSendFileLogListView();
	}

	private void initSendFileLogListView() {
		mAdapter = new SimpleAdapter(mContext, mSendFileLog,
				R.layout.im_send_file_item, new String[] {
						SendFileTask.FILE_NAME, SendFileTask.FILE_SIZE },
				new int[] { R.id.textview_filename, R.id.textview_filesize, }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView textview_accept_state = (TextView) v
						.findViewById(R.id.textview_accept_state);
				ImageView imageview_task_type = (ImageView) v
						.findViewById(R.id.imageview_task_type);
				HashMap<String, String> map = (HashMap<String, String>) mSendFileLog
						.get(position);

				int state = Integer.parseInt(map.get(SendFileTask.STATE));
				String stateStr = mContext.getResources().getString(
						R.string.im_sendfile_task_status_exception);
				final SendFileTaskOperator sendFileTaskOperator = sendFileTaskOperators
						.get(state);
				if (sendFileTaskOperator != null) {
					stateStr = sendFileTaskOperator.getDescription();
				}

				if (state < SendFileTask.RECEIVER_STATE_MAX_NUM) {
					imageview_task_type
							.setImageResource(R.drawable.ic_im_notice_download);
				} else {
					imageview_task_type
							.setImageResource(R.drawable.ic_im_notice_upload);
				}

				textview_accept_state.setText(stateStr);
				return v;
			}
		};

		listview_sendfile_log.setAdapter(mAdapter);
		listview_sendfile_log.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent
						.getItemAtPosition(position);
				String fileId = map.get(SendFileTask.FILE_ID);
				int state = Integer.parseInt(map.get(SendFileTask.STATE));
				showRecordOpDialog(fileId, state, position);
			}
		});

	}

	private void showRecordOpDialog(String fileId, int state,
			final int itemPositionInList) {

		final SendFileTaskOperator sendFileTaskOperator = sendFileTaskOperators
				.get(state);
		if (sendFileTaskOperator.getItems() == null) {
			return;
		}

		CustomDialog customeDialog = new CustomDialog(mContext);
		customeDialog.setTitle(R.string.im_send_file);
		customeDialog.setItems(sendFileTaskOperator.getItems(),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendFileTaskOperator.dealItemClickEvent(which,
								itemPositionInList);
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 任务操作，使用的状态机
	 */
	private abstract class SendFileTaskOperator {
		private String mStateDescription = "";
		private String[] mItems = null;

		public abstract void dealItemClickEvent(int clickWhich,
				int itemPositionInList);

		public SendFileTaskOperator(int state, String stateDescription,
				String[] items) {
			this.mStateDescription = stateDescription;
			this.mItems = items;
		}

		public String[] getItems() {
			return mItems;
		}

		public String getDescription() {
			return mStateDescription;
		}

	}

	private void initSendFileTaskOperator() {
		sendFileTaskOperators = new HashMap<Integer, SendFileTaskOperator>();
		sendFileTaskOperators
				.put(SendFileTask.STATE_RECEIVER_GET_REQUEST,
						new SendFileTaskOperator(
								SendFileTask.STATE_RECEIVER_GET_REQUEST,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_receiver_get_request),
								new String[] {
										mContext.getResources().getString(
												R.string.im_refuse),
										mContext.getResources().getString(
												R.string.im_accept) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								if (clickWhich == 0) {
									refuseFileSendTask(itemPositionInList);
								} else {
									acceptFileSendTask(itemPositionInList);
								}

							}
						});
		sendFileTaskOperators.put(
				SendFileTask.STATE_RECEIVER_ACCEPTED,
				new SendFileTaskOperator(SendFileTask.STATE_RECEIVER_ACCEPTED,
						mContext.getResources().getString(
								R.string.im_sendfile_status_receiver_accepted),
						new String[] {
								mContext.getResources().getString(
										R.string.im_sendfile_download),
								mContext.getResources().getString(
										R.string.im_sendfile_del_log_item) }) {

					@Override
					public void dealItemClickEvent(int clickWhich,
							int itemPositionInList) {
						if (clickWhich == 0) {
							acceptFileSendTask(itemPositionInList);
						} else {
							deleteFileSendTask(itemPositionInList);
						}
					}
				});

		sendFileTaskOperators
				.put(SendFileTask.STATE_RECEIVER_DOWNLOADING,
						new SendFileTaskOperator(
								SendFileTask.STATE_RECEIVER_DOWNLOADING,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_receiver_downloading),
								new String[] { mContext.getResources()
										.getString(R.string.im_stop_download) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								stopDownloadFile(itemPositionInList);
							}
						});
		sendFileTaskOperators
				.put(SendFileTask.STATE_RECEIVER_DOWNLAOD_FAILED,
						new SendFileTaskOperator(
								SendFileTask.STATE_RECEIVER_DOWNLAOD_FAILED,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_receiver_download_failed),
								new String[] {
										mContext.getResources().getString(
												R.string.im_retry_download),
										mContext.getResources()
												.getString(
														R.string.im_sendfile_del_log_item) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								if (clickWhich == 0) {
									acceptFileSendTask(itemPositionInList);
								} else {
									deleteFileSendTask(itemPositionInList);
								}
							}
						});
		sendFileTaskOperators
				.put(SendFileTask.STATE_RECEIVER_DOWNLOAD_FINISHED,
						new SendFileTaskOperator(
								SendFileTask.STATE_RECEIVER_DOWNLOAD_FINISHED,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_receiver_download_succ),
								new String[] { mContext
										.getResources()
										.getString(
												R.string.im_sendfile_del_log_item) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								deleteFileSendTask(itemPositionInList);
							}
						});
		sendFileTaskOperators.put(
				SendFileTask.STATE_RECEIVER_REFUSED,
				new SendFileTaskOperator(SendFileTask.STATE_RECEIVER_REFUSED,
						mContext.getResources().getString(
								R.string.im_sendfile_status_receiver_refuse),
						new String[] { mContext.getResources().getString(
								R.string.im_sendfile_del_log_item) }) {

					@Override
					public void dealItemClickEvent(int clickWhich,
							int itemPositionInList) {
						deleteFileSendTask(itemPositionInList);
					}
				});
		sendFileTaskOperators.put(
				SendFileTask.STATE_SENDER_UPLOADING,
				new SendFileTaskOperator(SendFileTask.STATE_SENDER_UPLOADING,
						mContext.getResources().getString(
								R.string.im_sendfile_status_sender_upload),
						new String[] { mContext.getResources().getString(
								R.string.im_stop_upload) }) {

					@Override
					public void dealItemClickEvent(int clickWhich,
							int itemPositionInList) {
						stopFileSendTask(itemPositionInList);

					}
				});

		sendFileTaskOperators
				.put(SendFileTask.STATE_SENDER_UPLOAD_FAILED,
						new SendFileTaskOperator(
								SendFileTask.STATE_SENDER_UPLOAD_FAILED,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_sender_upload_failed),
								new String[] {
										mContext.getResources().getString(
												R.string.im_retry_upload),
										mContext.getResources()
												.getString(
														R.string.im_sendfile_del_log_item) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								if (clickWhich == 0) {
									sendMessageToSendFile(itemPositionInList);
								} else {
									deleteFileSendTask(itemPositionInList);
								}

							}
						});
		sendFileTaskOperators
				.put(SendFileTask.STATE_SENDER_UPLOAD_FINISHED,
						new SendFileTaskOperator(
								SendFileTask.STATE_SENDER_UPLOAD_FINISHED,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_sender_upload_succ),
								new String[] {
										mContext.getResources()
												.getString(
														R.string.im_retry_sender_send_request),
										mContext.getResources()
												.getString(
														R.string.im_sendfile_del_log_item) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								if (clickWhich == 0) {
									sendRequestToReceiver(itemPositionInList);
								} else {
									deleteFileSendTask(itemPositionInList);
								}
							}
						});
		sendFileTaskOperators
				.put(SendFileTask.STATE_SENDER_SEND_REQUEST,
						new SendFileTaskOperator(
								SendFileTask.STATE_SENDER_SEND_REQUEST,
								mContext.getResources()
										.getString(
												R.string.im_sendfile_status_sender_send_request),
								new String[] { mContext
										.getResources()
										.getString(
												R.string.im_sendfile_del_log_item) }) {

							@Override
							public void dealItemClickEvent(int clickWhich,
									int itemPositionInList) {
								deleteFileSendTask(itemPositionInList);

							}
						});
		sendFileTaskOperators.put(
				SendFileTask.STATE_SENDER_GET_RESPONSE_ACCEPTED,
				new SendFileTaskOperator(
						SendFileTask.STATE_SENDER_GET_RESPONSE_ACCEPTED,
						mContext.getResources().getString(
								R.string.im_sender_get_response_accepted),
						new String[] { mContext.getResources().getString(
								R.string.im_sendfile_del_log_item) }) {

					@Override
					public void dealItemClickEvent(int clickWhich,
							int itemPositionInList) {
						deleteFileSendTask(itemPositionInList);

					}
				});
		sendFileTaskOperators.put(
				SendFileTask.STATE_SENDER_GET_RESPONSE_REFUSED,
				new SendFileTaskOperator(
						SendFileTask.STATE_SENDER_GET_RESPONSE_REFUSED,
						mContext.getResources().getString(
								R.string.im_sender_get_response_refused),
						new String[] { mContext.getResources().getString(
								R.string.im_sendfile_del_log_item) }) {

					@Override
					public void dealItemClickEvent(int clickWhich,
							int itemPositionInList) {
						deleteFileSendTask(itemPositionInList);

					}
				});

	}

	protected void stopFileSendTask(int itemPositionInList) {
		int sendFileTaskId = getSendFileTaskId(itemPositionInList);

		ContentValues values = new ContentValues();
		values.put(SendFileTask.STATE, SendFileTask.STATE_SENDER_UPLOAD_FAILED);
		mResolver.update(
				Uri.withAppendedPath(SendFileTask.CONTENT_URI,
						String.valueOf(sendFileTaskId)), values, null, null);
		Log.e("IM", "stopFileSendTask(" + sendFileTaskId + ")");
	}

	protected void stopDownloadFile(int itemPositionInList) {
		int sendFileTaskId = getSendFileTaskId(itemPositionInList);

		ContentValues values = new ContentValues();
		values.put(SendFileTask.STATE,
				SendFileTask.STATE_RECEIVER_DOWNLAOD_FAILED);
		mResolver.update(
				Uri.withAppendedPath(SendFileTask.CONTENT_URI,
						String.valueOf(sendFileTaskId)), values, null, null);
	}

	/**
	 * 发送消息给service上传文件
	 * 
	 * @param itemPositionInList
	 */
	protected void sendMessageToSendFile(int itemPositionInList) {
		int sendFileTaskId = getSendFileTaskId(itemPositionInList);

		Message msg = new Message();
		msg.what = ImMsgIds.ORDER_SNED_FILE;
		Bundle data = new Bundle();
		data.putString(SendFileTask._ID, String.valueOf(sendFileTaskId));
		msg.setData(data);
		ImMsgQueue.getInstance().addMessage(msg);
	}

	protected void sendRequestToReceiver(int itemPositionInList) {
		int sendFileTaskId = getSendFileTaskId(itemPositionInList);
		if (sendFileTaskId != 0) {
			ContentValues values = new ContentValues();
			values.put(SendFileTask.STATE,
					SendFileTask.STATE_SENDER_SEND_REQUEST);
			mResolver
					.update(Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(sendFileTaskId)), values, null, null);

			// 发送消息到service，通知文件接收者，去下载文件
			Message msg = new Message();
			msg.what = ImMsgIds.NOTICE_SEND_FILE_REQUEST_MESSAGE;
			Bundle data = new Bundle();
			data.putInt(SendFileTask._ID, sendFileTaskId);
			msg.setData(data);
			ImMsgQueue.getInstance().addMessage(msg);

		}
	}

	protected void deleteFileSendTask(int itemPositionInList) {

		int sendFileTaskId = getSendFileTaskId(itemPositionInList);
		// Log.e("IM","deleteFileSendTask()" + itemPositionInList +
		// "sendFileTaskId = "+sendFileTaskId);
		if (sendFileTaskId != 0) {
			ContentValues values = new ContentValues();
			values.put(SendFileTask.STATE, SendFileTask.STATE_RECEIVER_ACCEPTED);
			mResolver.delete(
					Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(sendFileTaskId)), null, null);
		}
	}

	private void acceptFileSendTask(int itemPositionInList) {
		int sendFileTaskId = getSendFileTaskId(itemPositionInList);
		if (sendFileTaskId != 0) {
			ContentValues values = new ContentValues();
			values.put(SendFileTask.STATE, SendFileTask.STATE_RECEIVER_ACCEPTED);
			mResolver
					.update(Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(sendFileTaskId)), values, null, null);

			// 发送消息到service，下载文件
			Message msg = new Message();
			msg.what = ImMsgIds.ORDER_DOWNLOAD_FILE;
			Bundle data = new Bundle();
			data.putInt(SendFileTask._ID, sendFileTaskId);
			msg.setData(data);
			ImMsgQueue.getInstance().addMessage(msg);
		}
	}

	private void refuseFileSendTask(int itemPositionInList) {
		int sendFileTaskId = getSendFileTaskId(itemPositionInList);
		if (sendFileTaskId != 0) {
			ContentValues values = new ContentValues();
			values.put(SendFileTask.STATE, SendFileTask.STATE_RECEIVER_REFUSED);
			mResolver
					.update(Uri.withAppendedPath(SendFileTask.CONTENT_URI,
							String.valueOf(sendFileTaskId)), values, null, null);

			Message msg = new Message();
			msg.what = ImMsgIds.ORDER_SEND_FILE_REFUSED;
			Bundle data = new Bundle();
			data.putInt(SendFileTask._ID, sendFileTaskId);
			msg.setData(data);
			ImMsgQueue.getInstance().addMessage(msg);

		} else {
			Log.e("IM", "refuseFileSendTask() -> 找不到任务ID");
		}
	}

	/**
	 * 获取所操作的列表项，所对应的任务的任务id
	 * 
	 * @param itemPositionInList
	 * @return
	 */
	private int getSendFileTaskId(int itemPositionInList) {
		HashMap<String, String> map = mSendFileLog.get(itemPositionInList);
		if (map != null) {
			return Integer.parseInt(map.get(SendFileTask._ID));
		}
		return 0;
	}

}
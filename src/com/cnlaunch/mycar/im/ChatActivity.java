package com.cnlaunch.mycar.im;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.im.action.FaceManager;
import com.cnlaunch.mycar.im.common.ChatMessageUtil;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.database.ImData.LastChat;
import com.cnlaunch.mycar.im.database.ImData.SendFileTask;
import com.cnlaunch.mycar.im.model.ChatMessageModel;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.receiver.TextChatMessageReceiver;

public class ChatActivity extends ImBaseActivity {
	private ImageButton imagebutton_face;
	private ImageButton imagebutton_sendfile;
	private Button button_submit;
	private EditText edittext_input;
	private LinearLayout layout_chat_log;
	private LayoutInflater inflater;
	private ScrollView scrollview_chat_log;

	private final int REQUEST_CODE_FACE = 1;
	private final int REQUEST_CODE_SENDFILE = 2;

	private Handler mHandler;
	private final int MSG_DATA_REFRESHED = 1;

	private String mTargetFaceId;
	private String mTargetUserUid;
	private String mTargetNickName;
	private String mCcno;

	private Context mContext;
	private ContentObserver mChatLogContentObserver;
	private ContentResolver mResolver;

	private List<ChatMessageModel> mChatLog = new ArrayList<ChatMessageModel>();
	List<HashMap<String, Integer>> mFaceList = new ArrayList<HashMap<String, Integer>>();

	private int mLatestId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat, R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_title_chat);
		setCustomeTitleRight("");

		inflater = getLayoutInflater();
		mContext = this;
		mResolver = mContext.getContentResolver();

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_DATA_REFRESHED:
					freshChatLogList();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}

		};

		findView();
		addListener();

		if (getIntent() != null
				&& getIntent().hasExtra(ImConstant.LastChatKeys.USERUID)) {
			mTargetUserUid = getIntent().getStringExtra(
					ImConstant.LastChatKeys.USERUID);
			Cursor c = null;
			try {

				c = mResolver.query(Friends.CONTENT_URI, new String[] {
						Friends.FACEID, Friends.NICKNAME, Friends.CCNO },
						Friends.USERUID + " = ? and " + Friends.GROUPUID
								+ "!=?", new String[] { mTargetUserUid,
								Friends.GROUP_UID_STRANGER }, null);
				if (c != null && c.moveToNext()) {
					mTargetFaceId = c.getString(0);
					mTargetNickName = c.getString(1);
					mCcno = c.getString(2);
				} else {
					Toast.makeText(mContext,
							R.string.im_can_not_chat_with_stranger,
							Toast.LENGTH_SHORT).show();
					this.finish();
				}
			} finally {
				if (c != null) {
					c.close();
				}
			}

			if (mTargetNickName != null && mCcno != null) {
				setCustomeTitleLeft(mContext.getResources().getString(
						R.string.im_with)
						+ " "
						+ mTargetNickName
						+ " - "
						+ mCcno
						+ " "
						+ mContext.getResources()
								.getString(R.string.im_chating));
			}
		} else {
			this.finish();
		}
		registerMsgObserver();

		prepareChatLog();

		setAllChatLogToIsReadState();

		cancelAllNofice();

		mChatLogContentObserver = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfUpdate) {
				prepareChatLog();
			}
		};
	}

	private void cancelAllNofice() {
		((NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(R.drawable.ic_im_notice_new_chat_message);

	}

	private void setAllChatLogToIsReadState() {
		// 将最近聊天记录表中的与该用户的记数器置0

		Cursor c = null;
		try {
			c = mResolver.query(LastChat.CONTENT_URI, new String[] {
					LastChat._ID, LastChat.UNREAD_COUNT }, LastChat.USERUID
					+ "=?", new String[] { mTargetUserUid }, null);

			if (c != null && c.moveToNext()) {
				int id = c.getInt(0);
				int unreadCount = c.getInt(1);
				c.close();
				c = null;

				if (unreadCount != 0) {
					ContentValues valuesLastChat = new ContentValues();
					valuesLastChat.put(LastChat.UNREAD_COUNT, 0);
					mResolver.update(
							Uri.withAppendedPath(LastChat.CONTENT_URI,
									String.valueOf(id)), valuesLastChat, null,
							null);

				}

			}
		} finally {
			if (c != null) {
				c.close();
			}
		}

		// 将聊天记录表中的，该用户发送的消息置为已读
		ContentValues valuesChatLog = new ContentValues();
		valuesChatLog.put(ChatLog.ISREAD, true);
		mResolver.update(ChatLog.CONTENT_URI, valuesChatLog, ChatLog.SENDER
				+ " = ?", new String[] { mTargetUserUid });

	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		if (intent.hasExtra(TextChatMessageReceiver.START_FROM_NOTIFICATION)) {
			startActivity(new Intent(this, FriendListActivity.class));
			this.finish();
		} else {
			super.onBackPressed();
		}
	}

	protected void freshChatLogList() {
		String useruid = ImSession.getInstence().getUseruid();
		synchronized (mChatLog) {
			for (ChatMessageModel model : mChatLog) {
				if (model.getSenderUID().equals(useruid)) {
					addMyMessage(model.getContent(),
							new Date(model.getSendTime()));
				} else {
					addTargetMessage(model.getContent(),
							new Date(model.getSendTime()));
				}
			}
		}
		scrollToBottom();
	}

	protected void prepareChatLog() {
		//聊天窗口中，显示的最早的聊天记录，距当前时间的最大毫秒数
		final int EARLIEST_MESSAGE_MILLISECOND = 6 * 60
				* 60 * 1000;
		new Thread() {
			@Override
			public void run() {
				synchronized (mChatLog) {
					mChatLog.clear();
					Long latestChatMessage = (new Date()).getTime() - EARLIEST_MESSAGE_MILLISECOND;

					Cursor c = mResolver.query(ChatLog.CONTENT_URI,
							new String[] { ChatLog.SENDER, ChatLog.RECEIVER,
									ChatLog.CONTENT, ChatLog.SENDTIME,
									ChatLog._ID }, "(" + ChatLog.SENDER
									+ "=? or " + ChatLog.RECEIVER
									+ "=? )  and " + ChatLog.SENDTIME + " > "
									+ latestChatMessage + " and " + ChatLog._ID
									+ " > " + mLatestId, new String[] {
									mTargetUserUid, mTargetUserUid },
							ChatLog._ID + " asc");

					if (c == null) {
						return;
					}
					try {
						while (c.moveToNext()) {
							ChatMessageModel model = new ChatMessageModel(
									c.getString(0), c.getString(1),
									c.getString(2), c.getLong(3));
							mLatestId = c.getInt(4);
							mChatLog.add(model);
						}
					} finally {
						c.close();
					}
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_DATA_REFRESHED;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		unRegisterMsgObserver();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		mResolver.unregisterContentObserver(mChatLogContentObserver);
		mResolver.registerContentObserver(ChatLog.CONTENT_URI, true,
				mChatLogContentObserver);
		ImSession.getInstence().setCurrentActivity(ImSession.ACTIVITY_CHAT);
		ImSession.getInstence().setCurrentTargetUserUid(mTargetUserUid);
	}

	@Override
	public void onPause() {
		ImSession.getInstence().setCurrentActivity(ImSession.ACTIVITY_OTHER);
		mResolver.unregisterContentObserver(mChatLogContentObserver);
		super.onPause();
	}

	private void unRegisterMsgObserver() {

	}

	private void registerMsgObserver() {

	}

	private void findView() {
		imagebutton_face = (ImageButton) findViewById(R.id.imagebutton_face);
		imagebutton_sendfile = (ImageButton) findViewById(R.id.imagebutton_sendfile);
		button_submit = (Button) findViewById(R.id.button_submit);
		edittext_input = (EditText) findViewById(R.id.edittext_input);
		layout_chat_log = (LinearLayout) findViewById(R.id.layout_chat_log);
		scrollview_chat_log = (ScrollView) findViewById(R.id.scrollview_chat_log);
	}

	private void addListener() {
		button_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String chatContent = edittext_input.getText().toString();
				if (chatContent.length() <= 0) {
					return;
				} else {
					sendChatMessage(chatContent);
				}
			}
		});

		imagebutton_face.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(
						new Intent(mContext, FacesActivity.class),
						REQUEST_CODE_FACE);
			}
		});
		imagebutton_sendfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(mContext,
						FileSelectActivity.class), REQUEST_CODE_SENDFILE);
			}
		});
		edittext_input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					scrollToBottom();
				}

			}
		});
	}

	protected void sendChatMessage(String chatContent) {
		// 清空输入框
		edittext_input.setText("");

		Message msg = new Message();
		msg.what = ImMsgIds.ORDER_SEND_CHAT_MESSAGE;
		Bundle data = new Bundle();
		data.putString(ChatLog.CONTENT, chatContent);
		data.putString(ChatLog.SENDER, ImSession.getInstence().getUseruid());
		data.putString(ChatLog.RECEIVER, mTargetUserUid);
		msg.setData(data);
		ImMsgQueue.getInstance().addMessage(msg);
	}

	private void scrollToBottom() {
		mHandler.post(new Runnable() {
			public void run() {
				int offset = layout_chat_log.getMeasuredHeight()
						- scrollview_chat_log.getHeight();
				if (offset > 0) {
					scrollview_chat_log.smoothScrollTo(0, offset);
				}
			}
		});
	}

	/**
	 * 如果是自己发送的消息
	 * 
	 * @param chatContent
	 * @param sendTime
	 */
	private void addMyMessage(String chatContent, Date sendTime) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.im_chat_right, null);
		ImageView imageview_face = (ImageView) layout
				.findViewById(R.id.imageview_face);
		TextView textview_chat_content = (TextView) layout
				.findViewById(R.id.textview_chat_content);
		TextView textview_chat_sendtime = (TextView) layout
				.findViewById(R.id.textview_chat_sendtime);
		imageview_face.setImageResource(FaceManager.getUserFace(ImSession
				.getInstence().getmUserModel().getFaceId()));

		if (sendTime.getDate() != new Date().getDate()) {
			textview_chat_sendtime.setText(Format.DateStr
					.getMonthDayHourMinute(sendTime));
		} else {
			textview_chat_sendtime.setText(Format.DateStr
					.getHourMinute(sendTime));
		}
		// textview_chat_content.setText(chatContent);
		ChatMessageUtil.putInTextView(textview_chat_content, chatContent);

		layout_chat_log.addView(layout);
		scrollToBottom();
	}

	/**
	 * 如果是对方发过来的消息
	 * 
	 * @param chatContent
	 * @param sendTime
	 */
	private void addTargetMessage(String chatContent, Date sendTime) {
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.im_chat_left, null);
		ImageView imageview_face = (ImageView) layout
				.findViewById(R.id.imageview_face);
		TextView textview_chat_content = (TextView) layout
				.findViewById(R.id.textview_chat_content);
		TextView textview_chat_sendtime = (TextView) layout
				.findViewById(R.id.textview_chat_sendtime);
		imageview_face.setImageResource(FaceManager.getUserFace(mTargetFaceId));

		if (sendTime.getDate() != new Date().getDate()) {
			textview_chat_sendtime.setText(Format.DateStr
					.getMonthDayHourMinute(sendTime));
		} else {
			textview_chat_sendtime.setText(Format.DateStr
					.getHourMinute(sendTime));
		}

		ChatMessageUtil.putInTextView(textview_chat_content, chatContent);
		layout_chat_log.addView(layout);

		scrollToBottom();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_FACE:
			if (resultCode == RESULT_OK && data != null) {
				edittext_input.append(data
						.getStringExtra(FacesActivity.FACE_NAME));
			}
			break;
		case REQUEST_CODE_SENDFILE:
			if (resultCode == RESULT_OK && data != null) {
				String filePath = data.getStringExtra("data");
				// Log.e("IM", "传送文件路径：" + filePath);
				sendMessageToSendFile(filePath);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void sendMessageToSendFile(String filePath) {
		File file = new File(filePath);
		ContentResolver resolver = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(SendFileTask.FILE_ID, "");
		values.put(SendFileTask.SEND_USERUID, ImSession.getInstence()
				.getUseruid());
		values.put(SendFileTask.RECEIVE_USERUID, mTargetUserUid);
		values.put(SendFileTask.FILE_NAME, file.getName());
		values.put(SendFileTask.FILE_SIZE, file.length());
		values.put(SendFileTask.SEND_TIME, new Date().getTime());
		values.put(SendFileTask.FILE_PATH, filePath);
		values.put(SendFileTask.STATE, SendFileTask.STATE_SENDER_UPLOADING);
		Uri uri = resolver.insert(SendFileTask.CONTENT_URI, values);

		int id = Integer.parseInt(uri.getLastPathSegment());
		if (id != -1) {

			Message msg = new Message();
			msg.what = ImMsgIds.ORDER_SNED_FILE;
			Bundle data = new Bundle();
			data.putString(SendFileTask._ID, String.valueOf(id));
			msg.setData(data);
			ImMsgQueue.getInstance().addMessage(msg);
		} else {
			Toast.makeText(mContext, R.string.im_fail_to_create_sendfile_task,
					Toast.LENGTH_LONG);
		}

	}
}
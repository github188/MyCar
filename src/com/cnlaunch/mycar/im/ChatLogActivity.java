package com.cnlaunch.mycar.im;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.utils.Format;
import com.cnlaunch.mycar.im.action.FaceManager;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.TextMessageNoticeManager;
import com.cnlaunch.mycar.im.database.ImData.ChatLog;
import com.cnlaunch.mycar.im.database.ImData.LastChat;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class ChatLogActivity extends ImBaseActivity {
	private ListView listview_chat_log;

	private List<HashMap<String, String>> mChatLog = new ArrayList<HashMap<String, String>>();
	private List<HashMap<String, String>> mChatLogNew = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mAdapter = null;

	private Handler mHandler;
	private final int LIST_DATA_REFRESHED = 1;

	private Context mContext;
	private ContentObserver mChatLogContentObserver;
	private ContentResolver mResolver;

	private BroadcastReceiver mBroadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_chat_log, R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_title_chat_log);
		setCustomeTitleRight("");
		mContext = this;
		mResolver = mContext.getContentResolver();

		findView();
		addListener();

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

		mChatLogContentObserver = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfUpdate) {
				prepareChatLog();
			}
		};

		createBroadcastReceiver();
	}

	private void createBroadcastReceiver() {
		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				prepareChatLog();
			}
		};

	}

	protected void freshList() {
		synchronized (mChatLogNew) {
			mChatLog.clear();
			for (HashMap<String, String> map : mChatLogNew) {
				mChatLog.add(map);
			}
			mChatLogNew.clear();
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerMsgObserver();
		mResolver.unregisterContentObserver(mChatLogContentObserver);
		mResolver.registerContentObserver(ChatLog.CONTENT_URI, true,
				mChatLogContentObserver);
		ImSession.getInstence().setCurrentActivity(ImSession.ACTIVITY_CHATLOG);
		prepareChatLog();
		registerReceiver(mBroadcastReceiver, new IntentFilter(
				ImConstant.BROADCAST_NEW_TEXT_CHAT_MESSAGE));
		
		//消除通知栏所有通知
		TextMessageNoticeManager.cancelAllNotice(mContext);
	}

	@Override
	public void onPause() {
		unregisterReceiver(mBroadcastReceiver);
		ImSession.getInstence().setCurrentActivity(ImSession.ACTIVITY_OTHER);
		mResolver.unregisterContentObserver(mChatLogContentObserver);
		unRegisterMsgObserver();
		super.onPause();
	}

	private void unRegisterMsgObserver() {

	}

	private void registerMsgObserver() {

	}

	private void findView() {
		listview_chat_log = (ListView) findViewById(R.id.listview_chat_log);
		findViewById(R.id.im_textbutton_menu_chatlog).setBackgroundResource(
				R.drawable.manager_toolbar_bg_selected);
	}

	private void addListener() {
		initChatLogListView();
	}

	protected void prepareChatLog() {
		new Thread() {
			@Override
			public void run() {
				synchronized (mChatLogNew) {
					mChatLogNew.clear();
					Cursor c = mResolver.query(Uri.withAppendedPath(
							LastChat.CONTENT_URI, "newest"), null, null, null,
							null);
					if (c != null) {
						try {
							while (c.moveToNext()) {
								HashMap<String, String> map = new HashMap<String, String>();
								map.put(ImConstant.LastChatKeys.USERUID,
										c.getString(0));
								map.put(ImConstant.LastChatKeys.CONTENT,
										c.getString(1));
								map.put(ImConstant.LastChatKeys.SENDTIME,
										c.getString(2));
								map.put(ImConstant.LastChatKeys.FACEID,
										c.getString(3));
								map.put(ImConstant.LastChatKeys.NICKNAME,
										c.getString(4));
								map.put(ImConstant.LastChatKeys.CCNO,
										c.getString(5));
								map.put(ImConstant.LastChatKeys.UNREAD_COUNT,
										c.getString(6));
								mChatLogNew.add(map);
							}
						} finally {
							c.close();
							c = null;
						}
					}

					Message msg = mHandler.obtainMessage();
					msg.what = LIST_DATA_REFRESHED;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	private void initChatLogListView() {
		mAdapter = new SimpleAdapter(mContext, mChatLog,
				R.layout.im_chat_log_list_item, new String[] {
						ImConstant.LastChatKeys.CONTENT,
						ImConstant.LastChatKeys.NICKNAME,
						ImConstant.LastChatKeys.CCNO }, new int[] {
						R.id.im_list_item_lastword, R.id.im_list_item_nickname,
						R.id.im_list_item_ccno }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				ImageView imageview_user_face = (ImageView) v
						.findViewById(R.id.imageview_user_face);
				TextView im_list_item_lastchattime = (TextView) v
						.findViewById(R.id.im_list_item_lastchattime);
				TextView im_list_item_unread_message_count = (TextView) v
						.findViewById(R.id.im_list_item_unread_message_count);
				// 表情
				imageview_user_face.setImageResource(FaceManager
						.getUserFace((String) mChatLog.get(position).get(
								ImConstant.LastChatKeys.FACEID)));
				// 未读信息数量
				int unreadMessageCount = Integer.parseInt(mChatLog
						.get(position)
						.get(ImConstant.LastChatKeys.UNREAD_COUNT));
				if (unreadMessageCount == 0) {
					im_list_item_unread_message_count
							.setVisibility(View.INVISIBLE);
					im_list_item_unread_message_count.setText("");
				} else {
					im_list_item_unread_message_count
							.setVisibility(View.VISIBLE);
					im_list_item_unread_message_count.setText(String
							.valueOf(unreadMessageCount));
				}

				// 最后聊天时间
				Date date = new Date(Long.parseLong(mChatLog.get(position).get(
						ImConstant.LastChatKeys.SENDTIME)));
				if (date.getDate() != new Date().getDate()) {
					im_list_item_lastchattime.setText(Format.DateStr
							.getMonthDayHourMinute(date));
				} else {
					im_list_item_lastchattime.setText(Format.DateStr
							.getHourMinute(date));
				}
				return v;
			}

		};
		listview_chat_log.setAdapter(mAdapter);
		listview_chat_log.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String useruid = (String) mChatLog.get(position).get(
						ImConstant.LastChatKeys.USERUID);
				if (isOnline(useruid))
				{
				     
				    Intent intent = new Intent();
				    intent.setClass(mContext, ChatActivity.class);
				    intent.putExtra(ImConstant.LastChatKeys.USERUID, useruid);
				    ChatLogActivity.this.startActivity(intent);
				}
				else
				{
                    Toast.makeText(mContext,
                        R.string.im_can_not_chat_with_friend_not_online,
                        Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
	
    private boolean isOnline(String uuid) {
        Set<String> friendOnlineSet = ImSession.getInstence()
                .getFriendOnlineSet();
        if (friendOnlineSet != null) {
            if (friendOnlineSet.contains(uuid)) {
                return true;
            }
        }
        return false;
    }
}
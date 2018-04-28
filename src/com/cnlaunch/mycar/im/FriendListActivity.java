package com.cnlaunch.mycar.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.action.FaceManager;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgObserver;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class FriendListActivity extends ImBaseActivity {
	private ListView listveiw_friend_list;

	private SimpleAdapter mFriendListAdapter;
	private List<HashMap<String, Object>> mFriendList;

	private ProgressDialog mUpdateFriendListProgressDialog;

	private ImMsgObserver mFriendListUpdateObserver;
	private ImMsgObserver mFriendOnlineListUpdateObserver;

	private Context mContext = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_friend_list, R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_title_friend_list);
		setCustomeTitleRight("");

		findView();
		addListener();
		createMsgObserver();
		ImMsgQueue.addMessage(ImMsgIds.ORDER_UPDATE_FRIEND_LIST);
		ImMsgQueue.addMessage(ImMsgIds.ORDER_UPDATE_FRIEND_ONLINE_LIST);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		unRegisterMsgObserver();
		ImSession.getInstence().setCurrentActivity(ImSession.ACTIVITY_OTHER);
		hideUpdateOnlineListProgressDialog();
		super.onPause();
	}

	@Override
	public void onResume() {
		freshFriendList();
		registerMsgObserver();
		ImSession.getInstence().setCurrentActivity(
				ImSession.ACTIVITY_ONLINELIST);
		super.onResume();
	}

	private void findView() {
		listveiw_friend_list = (ListView) findViewById(R.id.listveiw_friend_list);
		findViewById(R.id.im_textbutton_menu_friend).setBackgroundResource(
				R.drawable.manager_toolbar_bg_selected);
	}

	private void hideUpdateOnlineListProgressDialog() {
		if (mUpdateFriendListProgressDialog != null) {
			mUpdateFriendListProgressDialog.dismiss();
		}
	}

	private void addListener() {

		mFriendList = new ArrayList<HashMap<String, Object>>();
		mFriendListAdapter = new SimpleAdapter(this, mFriendList,
				R.layout.im_friend_list_item, new String[] {
						ImConstant.FriendKeys.NICKNAME,
						ImConstant.FriendKeys.CCNO, }, new int[] {
						R.id.im_list_item_nickname, R.id.im_list_item_ccno }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				ImageView imageview_user_face = (ImageView) v
						.findViewById(R.id.imageview_user_face);

				Boolean isOnline = (Boolean) (mFriendList.get(position)
						.get(ImConstant.FriendKeys.IS_ONLINE));
				if (isOnline) {
					imageview_user_face.setImageResource(FaceManager
							.getUserFace((String) mFriendList.get(position)
									.get(ImConstant.FriendKeys.FACEID)));
				} else {
					imageview_user_face.setImageResource(FaceManager
							.getUserFaceGray((String) mFriendList.get(position)
									.get(ImConstant.FriendKeys.FACEID)));

				}
				return v;
			}

		};
		listveiw_friend_list.setAdapter(mFriendListAdapter);
		listveiw_friend_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				String useruid = (String) mFriendList.get(position).get(
						ImConstant.FriendKeys.USERUID);

				Boolean isOnline = (Boolean) (mFriendList.get(position)
						.get(ImConstant.FriendKeys.IS_ONLINE));
				if (isOnline) {
					Intent intent = new Intent();
					intent.setClass(mContext, ChatActivity.class);
					intent.putExtra(ImConstant.FriendKeys.USERUID, useruid);
					FriendListActivity.this.startActivity(intent);
				} else {
					Toast.makeText(mContext,
							R.string.im_can_not_chat_with_friend_not_online,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		listveiw_friend_list
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						String useruid = (String) mFriendList.get(position)
								.get(ImConstant.FriendKeys.USERUID);
						Intent intent = new Intent();
						intent.setClass(mContext, ImFriendInfoActivity.class);
						intent.putExtra(ImConstant.FriendKeys.USERUID, useruid);
						FriendListActivity.this.startActivity(intent);

						return false;
					}

				});
	}

	private void createMsgObserver() {
		mFriendListUpdateObserver = new ImMsgObserver(
				ImMsgIds.REPLY_FRIEND_LIST_UPDATED, this) {

			@Override
			public void dealMessage(Message msg) {
				freshFriendList();
			}
		};
		mFriendOnlineListUpdateObserver = new ImMsgObserver(
				ImMsgIds.REPLY_FRIEND_ONLINE_LIST_UPDATED, this) {

			@Override
			public void dealMessage(Message msg) {
				freshFriendList();
			}
		};
	}

	private void registerMsgObserver() {
		unRegisterMsgObserver();
		ImMsgQueue.getInstance().registerObserver(mFriendListUpdateObserver);
		ImMsgQueue.getInstance().registerObserver(
				mFriendOnlineListUpdateObserver);
	}

	private void freshFriendList() {
		List<IMMyFriendComModel> friendList = ImSession.getInstence()
				.getFriendList();
		Set<String> friendOnlineSet = ImSession.getInstence()
				.getFriendOnlineSet();
		mFriendList.clear();

		List<HashMap<String, Object>> onlineFriendList = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> offlineFriendList = new ArrayList<HashMap<String, Object>>();

		if (friendList != null) {
			for (IMMyFriendComModel user : friendList) {
				// ½«×Ô¼ºÅÅ³ý
				if (user.getUserUID().equals(
						ImSession.getInstence().getUseruid())) {
					continue;
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(ImConstant.FriendKeys.NICKNAME, user.getNickName());
				map.put(ImConstant.FriendKeys.CCNO, user.getCCNo());
				map.put(ImConstant.FriendKeys.FACEID,
						String.valueOf(user.getFaceID()));
				map.put(ImConstant.FriendKeys.USERUID, user.getUserUID());
				if (friendOnlineSet != null) {
					if (friendOnlineSet.contains(user.getUserUID())) {
						map.put(ImConstant.FriendKeys.IS_ONLINE, true);
						onlineFriendList.add(0, map);
					} else {
						map.put(ImConstant.FriendKeys.IS_ONLINE, false);
						offlineFriendList.add(map);
					}

				} else {
					map.put(ImConstant.FriendKeys.IS_ONLINE, false);
					offlineFriendList.add(map);
				}
			}

			mFriendList.addAll(onlineFriendList);
			mFriendList.addAll(offlineFriendList);
		}

		mFriendListAdapter.notifyDataSetChanged();
		setCustomeTitleRight(onlineFriendList.size()
				+ "/"
				+ mFriendList.size()
				+ mContext.getResources().getString(
						R.string.im_x_friends_online));
		hideUpdateOnlineListProgressDialog();

	}

	private void unRegisterMsgObserver() {
		ImMsgQueue.getInstance().unRegisterObserver(mFriendListUpdateObserver);
		ImMsgQueue.getInstance().unRegisterObserver(
				mFriendOnlineListUpdateObserver);
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MyCarActivity.class));
		this.finish();
	}

}
package com.cnlaunch.mycar.im.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.im.action.PostBox;
import com.cnlaunch.mycar.im.action.SocketManager;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.receiver.ClientGetFriendListByUIDReceiver;
import com.cnlaunch.mycar.im.receiver.ClientGetFriendOnlineListByUIDReceiver;
import com.cnlaunch.mycar.im.receiver.ClientModifiedUserInfoReceiver;
import com.cnlaunch.mycar.im.receiver.DestClientAddFriendReceiver;
import com.cnlaunch.mycar.im.receiver.DestClientDelFriendReceiver;
import com.cnlaunch.mycar.im.receiver.LSCallAllSomeoneOfflineReceiver;
import com.cnlaunch.mycar.im.receiver.LSCallAllSomeoneOnlineReceiver;
import com.cnlaunch.mycar.im.receiver.LSCallSomeoneFriendHimSelfOfflineReceiver;
import com.cnlaunch.mycar.im.receiver.LSCallSomeoneFriendHimSelfOnlineReceiver;
import com.cnlaunch.mycar.im.receiver.LoginedAtAnotherReceiver;
import com.cnlaunch.mycar.im.receiver.OnlineUserListReceiver;
import com.cnlaunch.mycar.im.receiver.SendFileRequestReceiver;
import com.cnlaunch.mycar.im.receiver.SendFileResponeReceiver;
import com.cnlaunch.mycar.im.receiver.SourceClientAddFriendReceiver;
import com.cnlaunch.mycar.im.receiver.SourceClientDelFriendReceiver;
import com.cnlaunch.mycar.im.receiver.TextChatMessageReceiver;

public class ImSession {
	public final static int LOGIN_STATE_NONE = 0;
	public final static int LOGIN_STATE_LOGINING = 1;
	public final static int LOGIN_STATE_LOGINED = 2;
	public final static int LOGIN_STATE_LOGOUTING = 3;
	public final static int LOGIN_STATE_NETWORKD_DISCONNECTED = 4;
	public final static int LOGIN_STATE_EXIT = 5;
	public final static int LOGIN_STATE_LOGINED_AT_OTHER_PLACE = 6;

	public final static int ACTIVITY_OTHER = 0;
	public final static int ACTIVITY_CHAT = 1;
	public final static int ACTIVITY_CHATLOG = 2;
	public final static int ACTIVITY_ONLINELIST = 3;
	/**
	 * 当mCurrentActivity等于ACTIVITY_CHATLOG时，该字段表再当前的聊天对象
	 */
	private String mCurrentTargetUserUid = null;
	private PostBox mPostBox = null;

	/**
	 * 当前打开页面
	 */
	private int mCurrentActivity = ACTIVITY_OTHER;

	private static ImSession mImSession = new ImSession();
	private UserModel mUserModel;

	private int mLoginState = LOGIN_STATE_NONE;

	private List<UserModel> mOnlineUsers;
	private List<IMMyFriendComModel> mFriendList;
	private Set<String> mFriendOnlineSet = new HashSet<String>();

	private final byte[] LOCK_ONLINE_USERS = new byte[0];
	private final byte[] LOCK_FRIEND_LIST = new byte[0];
	private final byte[] LOCK_FRIEND_ONLINE_SET = new byte[0];

	private ImSession() {
	}

	public static ImSession getInstence() {
		if (mImSession == null) {
			mImSession = new ImSession();
			Log.e("IM", "ImSession -> 创建Session实例");
		}
		return mImSession;
	}

	public PostBox getPostBox() {
		return mPostBox;
	}

	public static void initSession(Context context) {
		Log.e("IM", "ImSession.initSession()");
		getInstence().buildPostBox(context);
	}

	public static void cleanSession() {
		Log.e("IM", "ImSession.cleanSession()");
		mImSession = null;
		SocketManager.getInstance().dismiss();
	}

	public static boolean isLogined() {
		return getInstence().getLoginState() == LOGIN_STATE_LOGINED;
	}

	public static boolean isNetworkDisconnected() {
		return getInstence().getLoginState() == LOGIN_STATE_NETWORKD_DISCONNECTED;
	}

	public static void setNetworkDisconnected() {
		getInstence().setLoginState(LOGIN_STATE_NETWORKD_DISCONNECTED);
	}

	public static boolean isExit() {
		return getInstence().getLoginState() == LOGIN_STATE_EXIT;
	}

	public boolean isMyFriendOnLine(String friendUserUid) {
		return mFriendOnlineSet.contains(friendUserUid);
	}

	public int getLoginState() {
		return mLoginState;
	}

	public void setLoginState(int loginStatus) {
		this.mLoginState = loginStatus;
	}

	public UserModel getmUserModel() {
		return mUserModel;
	}

	public void setmUserModel(UserModel mUserModel) {
		this.mUserModel = mUserModel;
	}

	public String getUseruid() {
		if (mUserModel != null) {
			return mUserModel.getUseruid();
		}
		return null;

	}

	public List<UserModel> getOnlineUsers() {
		synchronized (LOCK_ONLINE_USERS) {
			return mOnlineUsers;
		}
	}

	public List<IMMyFriendComModel> getFriendList() {
		synchronized (LOCK_FRIEND_LIST) {
			return mFriendList;
		}
	}

	public Set<String> getFriendOnlineSet() {
		synchronized (LOCK_FRIEND_ONLINE_SET) {
			return mFriendOnlineSet;
		}
	}

	public void setOnlineUsers(List<UserModel> onlineUsers) {
		synchronized (LOCK_ONLINE_USERS) {
			this.mOnlineUsers = onlineUsers;
		}
		ImMsgQueue.addMessage(ImMsgIds.REPLY_ONLINE_LIST_UPDATED);
	}

	public void setFriendList(List<IMMyFriendComModel> friendList) {
		synchronized (LOCK_FRIEND_LIST) {
			this.mFriendList = friendList;
		}
		ImMsgQueue.addMessage(ImMsgIds.REPLY_FRIEND_LIST_UPDATED);
	}

	public void setFriendOnlineSet(Set<String> friendOnlineSet) {
		synchronized (LOCK_FRIEND_ONLINE_SET) {
			this.mFriendOnlineSet = friendOnlineSet;
		}
		ImMsgQueue.addMessage(ImMsgIds.REPLY_FRIEND_ONLINE_LIST_UPDATED);
	}

	public String getCurrentTargetUserUid() {
		return this.mCurrentTargetUserUid;
	}

	public void setCurrentTargetUserUid(String currentTargetUserUid) {
		this.mCurrentTargetUserUid = currentTargetUserUid;
	}

	public int getCurrentActivity() {
		return this.mCurrentActivity;
	}

	public void setCurrentActivity(int currentActivity) {
		this.mCurrentActivity = currentActivity;
	}

	public static void exit() {
		getInstence().setLoginState(LOGIN_STATE_EXIT);
	}

	public void addOnlineUsers(UserModel userModel) {
		if (userModel == null) {
			return;
		}
		String useruid = userModel.getUseruid();

		synchronized (LOCK_ONLINE_USERS) {
			if (userModel != null && mOnlineUsers != null) {
				Iterator<UserModel> i = mOnlineUsers.iterator();
				while (i.hasNext()) {
					if (i.next().getUseruid().equals(useruid)) {
						i.remove();
						break;
					}
				}
				mOnlineUsers.add(userModel);
				ImMsgQueue.addMessage(ImMsgIds.REPLY_ONLINE_LIST_UPDATED);
			}
		}

	}

	public void addOnlineFriend(String useruid) {
		if (useruid == null) {
			return;
		}
		synchronized (LOCK_FRIEND_ONLINE_SET) {
			if (!mFriendOnlineSet.contains(useruid)) {
				mFriendOnlineSet.add(useruid);
				ImMsgQueue
						.addMessage(ImMsgIds.REPLY_FRIEND_ONLINE_LIST_UPDATED);
			}
		}
	}

	public void addFriend(IMMyFriendComModel model) {
		if (model == null) {
			return;
		}
		String useruid = model.getUserUID();

		synchronized (LOCK_FRIEND_LIST) {
			if (mFriendList != null) {
				Iterator<IMMyFriendComModel> i = mFriendList.iterator();
				while (i.hasNext()) {
					if (i.next().getUserUID().equals(useruid)) {
						i.remove();
						break;
					}
				}
				mFriendList.add(model);

				if (model.getOnlineStatus() == IMMyFriendComModel.ONLINE_STATE_ON) {
					addOnlineFriend(model.getUserUID());
				}
				ImMsgQueue.addMessage(ImMsgIds.REPLY_FRIEND_LIST_UPDATED);
			}
		}
	}

	public void removeFriend(String userUid) {
		if (userUid == null) {
			return;
		}
		synchronized (LOCK_FRIEND_LIST) {
			if (mFriendList != null) {
				Iterator<IMMyFriendComModel> i = mFriendList.iterator();
				while (i.hasNext()) {
					if (i.next().getUserUID().equals(userUid)) {
						i.remove();
						if (mFriendOnlineSet.contains(userUid)) {
							mFriendOnlineSet.remove(userUid);
						}
						ImMsgQueue
								.addMessage(ImMsgIds.REPLY_FRIEND_LIST_UPDATED);
						break;
					}
				}

			}
		}
	}

	public void removeOnlineUsers(UserModel userModel) {
		if (userModel == null) {
			return;
		}
		String useruid = userModel.getUseruid();

		synchronized (LOCK_ONLINE_USERS) {
			if (mOnlineUsers != null) {
				Iterator<UserModel> i = mOnlineUsers.iterator();
				while (i.hasNext()) {
					if (i.next().getUseruid().equals(useruid)) {
						i.remove();
						ImMsgQueue
								.addMessage(ImMsgIds.REPLY_ONLINE_LIST_UPDATED);
						break;
					}
				}
			}
		}

	}

	public void removeOnlineFriend(String useruid) {
		if (useruid == null) {
			return;
		}
		synchronized (LOCK_FRIEND_ONLINE_SET) {
			if (mFriendOnlineSet.contains(useruid)) {
				mFriendOnlineSet.remove(useruid);
				ImMsgQueue
						.addMessage(ImMsgIds.REPLY_FRIEND_ONLINE_LIST_UPDATED);
			}
		}
	}

	public void buildPostBox(Context context) {
		mPostBox = new PostBox(ImSession.getInstence().getUseruid());

		// 注册消息接收者
		mPostBox.regiestReceiver(new ClientGetFriendListByUIDReceiver(context));
		mPostBox.regiestReceiver(new ClientGetFriendOnlineListByUIDReceiver(
				context));
		mPostBox.regiestReceiver(new ClientModifiedUserInfoReceiver(context));
		mPostBox.regiestReceiver(new DestClientAddFriendReceiver(context));
		mPostBox.regiestReceiver(new DestClientDelFriendReceiver(context));
		mPostBox.regiestReceiver(new LoginedAtAnotherReceiver(context));
		mPostBox.regiestReceiver(new LSCallAllSomeoneOfflineReceiver(context));
		mPostBox.regiestReceiver(new LSCallAllSomeoneOnlineReceiver(context));
		mPostBox.regiestReceiver(new LSCallSomeoneFriendHimSelfOfflineReceiver(
				context));
		mPostBox.regiestReceiver(new LSCallSomeoneFriendHimSelfOnlineReceiver(
				context));
		mPostBox.regiestReceiver(new OnlineUserListReceiver(context));
		mPostBox.regiestReceiver(new SendFileRequestReceiver(context));
		mPostBox.regiestReceiver(new SendFileResponeReceiver(context));
		mPostBox.regiestReceiver(new SourceClientAddFriendReceiver(context));
		mPostBox.regiestReceiver(new SourceClientDelFriendReceiver(context));
		mPostBox.regiestReceiver(new TextChatMessageReceiver(context));

		mPostBox.open();
	}

	private void closePostBox() {
		if (mPostBox != null) {
			mPostBox.close();
			mPostBox = null;
		}

	}

}

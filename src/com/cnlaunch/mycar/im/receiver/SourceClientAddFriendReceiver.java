package com.cnlaunch.mycar.im.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.im.FriendListActivity;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.model.IMAddFriendComModel;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class SourceClientAddFriendReceiver extends LetterReceiver {
	private final int mId = ImConstant.Reciver.SERVER_SOURCE_ACK_ADD_FRIEND;
	private Context mContext;

	public SourceClientAddFriendReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(ILetter letter) {
		IMAddFriendComModel imAddFriendComModel = JsonConvert.fromJson(
				letter.getContent(), IMAddFriendComModel.class);
		// 添加者，收到信封后，取出添加是否成功的状态，如果成功，将被添加者的信息，并写入好友列表
		if (imAddFriendComModel.getStatus() == IMAddFriendComModel.STATUS_RESPONSE_SUCC) {
			IMMyFriendComModel model = imAddFriendComModel
					.getDestUserComModel();
			ImSession.getInstence().addFriend(model);
			setNotice(model.getNickName() + "(" + model.getCCNo() + ")");

			// 更新数据库中的好友记录
			ContentResolver resolver = mContext.getContentResolver();
			ContentValues[] valuesArray = new ContentValues[1];
			int i = 0;

			ContentValues values = new ContentValues();
			values.put(Friends.IM_MY_FRIEND_RELATION_ID,
					model.getIMMyFriendRelationID());
			values.put(Friends.PARENT_FRIEND_UID, model.getParentFriendUID());
			values.put(Friends.GROUPNAME, model.getGroupName());
			values.put(Friends.GROUPUID, model.getGroupUID());
			values.put(Friends.USERUID, model.getUserUID());
			values.put(Friends.NICKNAME, model.getNickName());
			values.put(Friends.FACEID, model.getFaceID());
			values.put(Friends.CCNO, model.getCCNo());
			values.put(Friends.USERSIGN, model.getUserSign());
			values.put(Friends.TASKID, model.getTaskID());
			values.put(Friends.NAME_REMARK, model.getNameRemark());
			valuesArray[i++] = values;

			resolver.bulkInsert(Friends.CONTENT_URI, valuesArray);
		}

	}

	private void setNotice(String userNickNameAndCCNo) {
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int noticeId = R.layout.im_add_friend;

		// notificationManager.cancel(noticeId);

		Intent intent = new Intent(mContext, FriendListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

		int icon = R.drawable.ic_im_notice_new_chat_message;
		String noticeContent = "添加好友成功";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, noticeContent, when);
		notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(mContext, "添加好友成功", userNickNameAndCCNo
				+ "已成为你的好友", contentIntent);

		notificationManager.notify(noticeId, notification);
		Log.e("AddFriend", userNickNameAndCCNo + "已成为你的好友");

	}

}

package com.cnlaunch.mycar.im.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.FriendsUtils;
import com.cnlaunch.mycar.im.database.ImData.Friends;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;
import com.google.gson.Gson;

public class ClientGetFriendListByUIDReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.CLIENT_GETFRIENDLISTBYUID_REQUEST;
	private Context mContext;

	public ClientGetFriendListByUIDReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dealLetter(ILetter letter) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<IMMyFriendComModel>>() {
		}.getType();

		ArrayList<IMMyFriendComModel> friendList = (ArrayList<IMMyFriendComModel>) (new Gson()
				.fromJson(letter.getContent(), type));

		if (friendList != null) {
			// 更新session中的好友列表
			ImSession.getInstence().setFriendList(friendList);

			// 更新数据库中的好友记录
			ContentResolver resolver = mContext.getContentResolver();
			// 先将数据库中已有的好友记录中的groupid字段，置为 陌生人
			FriendsUtils.setAllToStranger(mContext);

			//再根据从服务器上获取到的好友列表，更新数据库
			ContentValues[] valuesArray = new ContentValues[friendList.size()];
			int i = 0;
			for (IMMyFriendComModel model : friendList) {
				ContentValues values = new ContentValues();
				values.put(Friends.IM_MY_FRIEND_RELATION_ID,
						model.getIMMyFriendRelationID());
				values.put(Friends.PARENT_FRIEND_UID,
						model.getParentFriendUID());
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
			}
			resolver.bulkInsert(Friends.CONTENT_URI, valuesArray);
		}
	}

}

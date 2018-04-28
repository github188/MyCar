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
			// ����session�еĺ����б�
			ImSession.getInstence().setFriendList(friendList);

			// �������ݿ��еĺ��Ѽ�¼
			ContentResolver resolver = mContext.getContentResolver();
			// �Ƚ����ݿ������еĺ��Ѽ�¼�е�groupid�ֶΣ���Ϊ İ����
			FriendsUtils.setAllToStranger(mContext);

			//�ٸ��ݴӷ������ϻ�ȡ���ĺ����б��������ݿ�
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

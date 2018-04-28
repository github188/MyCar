package com.cnlaunch.mycar.im.receiver;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.FriendsUtils;
import com.cnlaunch.mycar.im.model.IMDelFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class DestClientDelFriendReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.SERVER_DEST_ACK_DEL_FRIEND;
	private Context mContext;

	public DestClientDelFriendReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(ILetter letter) {
		Log.e("IM", "�ұ�����ɾ��������");
		IMDelFriendComModel imDelFriendComModel = JsonConvert.fromJson(
				letter.getContent(), IMDelFriendComModel.class);

		String userUid = imDelFriendComModel.getSourceUserUID();

		// �����ݿ��У������û���Ϊİ����
		FriendsUtils.setToStranger(mContext,userUid);

		// �������б���ɾ��
		ImSession.getInstence().removeFriend(userUid);
	}
}

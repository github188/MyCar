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
		Log.e("IM", "我被别人删啦！！！");
		IMDelFriendComModel imDelFriendComModel = JsonConvert.fromJson(
				letter.getContent(), IMDelFriendComModel.class);

		String userUid = imDelFriendComModel.getSourceUserUID();

		// 在数据库中，将该用户置为陌生人
		FriendsUtils.setToStranger(mContext,userUid);

		// 从在线列表中删除
		ImSession.getInstence().removeFriend(userUid);
	}
}

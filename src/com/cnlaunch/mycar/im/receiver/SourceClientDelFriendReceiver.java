package com.cnlaunch.mycar.im.receiver;

import android.content.Context;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.database.FriendsUtils;
import com.cnlaunch.mycar.im.model.IMDelFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;

public class SourceClientDelFriendReceiver extends LetterReceiver {
	private final int mId = ImConstant.Reciver.SERVER_SOURCE_ACK_DEL_FRIEND;
	private Context mContext;

	public SourceClientDelFriendReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(ILetter letter) {
		IMDelFriendComModel imDelFriendComModel = JsonConvert.fromJson(
				letter.getContent(), IMDelFriendComModel.class);

		String userUid = imDelFriendComModel.getDestUserUID();

		// 在数据库中，将该用户置为陌生人
		FriendsUtils.setToStranger(mContext,userUid);
		
		// 从在线列表中删除
		if (imDelFriendComModel.getStatus() == IMDelFriendComModel.STATUS_RESPONSE_DEL_SUCC) {
			ImSession.getInstence().removeFriend(userUid);
		}
		
		ImMsgQueue.addMessage(ImMsgIds.REPLY_DEL_FRIEND);
	}

}

package com.cnlaunch.mycar.im.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.ImSession;
import com.google.gson.Gson;

public class ClientGetFriendOnlineListByUIDReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.CLIENT_GETFRIENDONLINELISTBYUID_REQUEST;
	private Context mContext;

	public ClientGetFriendOnlineListByUIDReceiver(Context context) {
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

		ArrayList<IMMyFriendComModel> onLineFriendList = (ArrayList<IMMyFriendComModel>) (new Gson()
				.fromJson(letter.getContent(), type));

		if (onLineFriendList != null) {

			// 点亮好友列表
			int i = 0;
			for (IMMyFriendComModel model : onLineFriendList) {
				ImSession.getInstence().addOnlineFriend(model.getUserUID());
			}
		}
	}
}

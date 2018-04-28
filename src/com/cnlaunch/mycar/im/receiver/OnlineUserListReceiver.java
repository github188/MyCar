package com.cnlaunch.mycar.im.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.UserModel;
import com.google.gson.Gson;

public class OnlineUserListReceiver  extends LetterReceiver {

	private final int mId = ImConstant.Reciver.GET_ONLINEUSERINFO_LIST;
	private Context mContext;

	public OnlineUserListReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dealLetter(final ILetter letter) {
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<UserModel>>() {
		}.getType();

		ArrayList<UserModel> onlineList = (ArrayList<UserModel>) (new Gson()
				.fromJson(letter.getContent(), type));

		// 更新session中的在线列表
		ImSession.getInstence().setOnlineUsers(onlineList);
	}

}

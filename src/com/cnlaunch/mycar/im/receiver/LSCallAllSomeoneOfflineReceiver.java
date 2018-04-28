package com.cnlaunch.mycar.im.receiver;

import android.content.Context;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.UserModel;

public class LSCallAllSomeoneOfflineReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.SERVER_CALLALL_SOMEONEOFFLINE;
	private Context mContext;

	public LSCallAllSomeoneOfflineReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {
		UserModel userModel = JsonConvert.fromJson(letter.getContent(),
				UserModel.class);
		if (userModel != null) {
			ImSession.getInstence().removeOnlineUsers(userModel);
		}
	}

}

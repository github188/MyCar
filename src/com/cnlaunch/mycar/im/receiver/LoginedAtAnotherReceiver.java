package com.cnlaunch.mycar.im.receiver;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.model.ImSession;

public class LoginedAtAnotherReceiver extends LetterReceiver {

	private final int mId = ImConstant.Reciver.SERVER_CALL_CLIENT_LOGINEDATANOTHER;
	private Context mContext;

	public LoginedAtAnotherReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(final ILetter letter) {
		Log.e("IM","Œ“±ªÃﬂ¡À");
		ImSession.cleanSession();
		ImSession.getInstence().setLoginState(ImSession.LOGIN_STATE_LOGINED_AT_OTHER_PLACE);
	}

}

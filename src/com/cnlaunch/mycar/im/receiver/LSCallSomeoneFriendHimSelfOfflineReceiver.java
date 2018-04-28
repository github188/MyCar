package com.cnlaunch.mycar.im.receiver;

import android.content.Context;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.LetterReceiver;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.UserModel;

public class LSCallSomeoneFriendHimSelfOfflineReceiver  extends LetterReceiver{

	private final int mId = ImConstant.Reciver.SERVER_CALLSOMEONEFRIEND_HIMSELFOFFLINE;
	private Context mContext;

	public LSCallSomeoneFriendHimSelfOfflineReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(ILetter letter) {
		String userUid = letter.getContent();
		if (userUid != null) {
			ImSession.getInstence().removeOnlineFriend(userUid);
		}
	}

}

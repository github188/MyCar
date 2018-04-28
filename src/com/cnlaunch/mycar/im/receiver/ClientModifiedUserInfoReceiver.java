package com.cnlaunch.mycar.im.receiver;

import android.content.Context;

import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.LetterReceiver;

public class ClientModifiedUserInfoReceiver  extends LetterReceiver{

	private final int mId = ImConstant.Reciver.CLIENT_MODIFIED_USERINFO;
	private Context mContext;

	public ClientModifiedUserInfoReceiver(Context context) {
		mContext = context;
	}

	@Override
	public int getId() {
		return mId;
	}

	@Override
	public void dealLetter(ILetter letter) {
		// TODO Auto-generated method stub
		
	}

}

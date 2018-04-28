package com.cnlaunch.mycar.im.service;

import java.util.TimerTask;

import android.util.Log;

import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.Letter;
import com.cnlaunch.mycar.im.model.ImSession;

public class HeartbeatTask extends TimerTask{

	@Override
	public void run() {
		if(ImSession.isLogined()){
			Letter letter = new Letter();
			letter.setSender(ImSession.getInstence().getUseruid());
			letter.setReceiver(ImConstant.SYS_LOGIN_SERVER);
			letter.setContent("");
			Envelope envelope = new Envelope(
					ImConstant.MessageSource.CLIENT_TO_SERVER,
					ImConstant.MessageCategory.CLIENT_SERVER_LOGIN_HEARTBEAT,
					letter);
			ImSession.getInstence().getPostBox().send(envelope);
			Log.v("IM","Heartbeat -> " + System.currentTimeMillis());
		}
	}

}

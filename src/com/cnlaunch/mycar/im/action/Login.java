package com.cnlaunch.mycar.im.action;

import android.util.Log;

import com.cnlaunch.mycar.im.action.SocketManager.OnReceiveEnvelopeListener;
import com.cnlaunch.mycar.im.common.Envelope;
import com.cnlaunch.mycar.im.common.ILetter;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.common.Letter;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.LoginInfo;
import com.cnlaunch.mycar.im.model.LoginReply;

public class Login {
	public static final int LOGIN_SUCC = 0;
	public static final int LOGIN_FAIL = 1;
	public static final int LOGIN_REPEAT = 2;

	public static final int LOGOUT_SUCC = 3;
	public static final int LOGOUT_FAIL = 4;
	public static final int LOGOUT_TIMEOUT = 5;
	
	private LoginInfo mLoginInfo;

	private OnLoginListener mOnLoginListener = null;

	public Login() {

	}

	public void doLogin(String username, String password,
			OnLoginListener onLoginReply) {
		ImSession.getInstence().setLoginState(ImSession.LOGIN_STATE_LOGINING);
		mOnLoginListener = onLoginReply;

		SocketManager.getInstance().setOnReceiveEnvelopeListener(
				new OnReceiveEnvelopeListener() {

					@Override
					public void dealEnvelope(Envelope envelope) {
						if(envelope.getCategory() != ImConstant.MessageCategory.REPLY){
							return;
						}
						
						//Log.e("IM", "收到登录反馈 ");
						ILetter letter = null;
						if (envelope != null) {
							letter = envelope.getLetter();
						}

						if (letter != null) {
							String content = letter.getContent();

							//Log.e("IM", "反馈内容 -> " + content);

							LoginReply loginReply = JsonConvert.fromJson(content,
									LoginReply.class);
							if (loginReply != null) {
								if (loginReply.isSucc()) {
									ImSession.getInstence().setLoginState(
											ImSession.LOGIN_STATE_LOGINED);
									mOnLoginListener.onSucc(loginReply);
								} else {
									mOnLoginListener.onFail(loginReply);
								}
							} else {
								mOnLoginListener.onSucc(loginReply);
							}
						}
					}
				});

		Log.e("IM","Login.doLogin()"+System.currentTimeMillis());

		mLoginInfo = new LoginInfo("Android", "1", username, password, "");

		Letter requestLetter = new Letter(ImConstant.SYS_LOGIN_SERVER,
				mLoginInfo.getUsername(), JsonConvert.toJson(mLoginInfo));

		//Log.e("IM", "登录请求  --> "+ +System.currentTimeMillis() + " -> " + JsonConvert.toJson(requestLetter));

		Envelope envelope = new Envelope(
				ImConstant.MessageSource.CLIENT_TO_SERVER,
				ImConstant.MessageCategory.LOGIN_IN, requestLetter);

		SocketManager.getInstance().send(envelope);
	}

	public static interface OnLoginListener {
		public void onSucc(LoginReply loginReply);

		public void onFail(LoginReply loginReply);

	}

}

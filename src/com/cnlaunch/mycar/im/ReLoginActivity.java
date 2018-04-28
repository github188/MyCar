package com.cnlaunch.mycar.im;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.im.action.Login;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgObserver;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.model.ImSession;

public class ReLoginActivity extends BaseActivity {
	private TextView textview_relogin_message;
	private Button button_relogin;
	private Button button_exit_im;
	private ImMsgObserver mLoginReplyObserver;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_relogin,R.layout.custom_title);
		setCustomeTitleLeft(R.string.im_relogin);
		setCustomeTitleRight("");
		mContext = this;

		findView();
		addListener();
		createMsgObserver();
		initReloginMessage();
	}

	private void initReloginMessage() {
		String msg = "";
		switch (ImSession.getInstence().getLoginState()) {
		case ImSession.LOGIN_STATE_LOGINED_AT_OTHER_PLACE:
			msg = mContext.getResources().getString(R.string.im_you_has_being_kick_offline);
			break;
		case ImSession.LOGIN_STATE_NETWORKD_DISCONNECTED:
			msg = mContext.getResources().getString(R.string.im_net_error_need_relogin);
			break;
		default:
			msg = mContext.getResources().getString(R.string.im_you_has_offline_need_relogin);
			break;
		}
		textview_relogin_message.setText(msg);
	}

	@Override
	public void onPause() {
		unRegisterMsgObserver();
		super.onPause();
	}

	@Override
	public void onResume() {
		registerMsgObserver();
		super.onResume();
	}

	private void findView() {
		textview_relogin_message = (TextView) findViewById(R.id.textview_relogin_message);
		button_relogin = (Button) findViewById(R.id.button_relogin);
		button_exit_im = (Button) findViewById(R.id.button_exit_im);
	}

	private void addListener() {
		button_relogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = ImMsgIds.ORDER_LOGIN;
				Bundle data = new Bundle();
				data.putString("username", MyCarActivity.cc);
				data.putString("password", MyCarActivity.password);
				msg.setData(data);
				ImMsgQueue.getInstance().addMessage(msg);

			}
		});
		button_exit_im.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImSession.exit();
				ReLoginActivity.this.finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// 后退键，等同于退出IM
		ImSession.exit();
		ReLoginActivity.this.finish();
	}

	private void createMsgObserver() {
		mLoginReplyObserver = new ImMsgObserver(ImMsgIds.REPLY_LOGIN, this) {

			@Override
			public void dealMessage(Message msg) {
				switch (msg.arg1) {
				case Login.LOGIN_SUCC:
					Toast.makeText(mContext, R.string.im_login_succ, Toast.LENGTH_SHORT).show();
					ReLoginActivity.this.finish();
					break;
				case Login.LOGOUT_FAIL:
					Toast.makeText(mContext, R.string.im_login_fail, Toast.LENGTH_SHORT).show();
					textview_relogin_message.setText(msg.getData().getString(
							"data"));
					break;
				case Login.LOGIN_REPEAT:
					Toast.makeText(mContext, R.string.im_is_logined, Toast.LENGTH_SHORT)
							.show();
					break;
				case Login.LOGOUT_TIMEOUT:
					Toast.makeText(mContext, R.string.im_net_error_server_connect_time_out,
							Toast.LENGTH_SHORT).show();
					//Log.e("IM", "连接超时，无法连接到服务器");
					break;
				default:
					break;
				}
			}
		};
	}


	private void registerMsgObserver() {
		ImMsgQueue.getInstance().registerObserver(mLoginReplyObserver);
	}

	private void unRegisterMsgObserver() {
		ImMsgQueue.getInstance().unRegisterObserver(mLoginReplyObserver);
	}

}
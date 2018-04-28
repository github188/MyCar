package com.cnlaunch.mycar.im;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.im.action.Login;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgObserver;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.service.ImServiceManager;
import com.cnlaunch.mycar.usercenter.LoginActivity;

public class ImLoginActivity extends Activity implements OnClickListener {

	private Button button_login;
	private Button button_logout;

	private EditText edittext_username;
	private EditText edittext_password;

	private TextView test_log_info;
	private CustomDialog mCustomDialog;

	private ImServiceManager mImServiceManager;

	private Context mContext;

	private ImMsgObserver mLoginReplyObserver;
	private ImMsgObserver mLogoutReplyObserver;

	private ImMsgObserver mOnlineListUpdateObserver;
	private boolean isReadyToJumpToImMainActivity = false;

	private Runnable onServiceConnected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.im_login);
		mContext = this;

		findView();
		addListener();

		mImServiceManager = new ImServiceManager(mContext);
		onServiceConnected = new Runnable() {

			@Override
			public void run() {
				showDialogToLogin(MyCarActivity.cc, MyCarActivity.password);
			}
		};
		createMsgObserver();
	}

	/**
	 * 用户中心正在登录中
	 * 
	 * @return
	 */
	private boolean userCenterIsLogining() {
		return MyCarActivity.loginState == Constants.LOGIN_STATE_LOGINING;
	}

	/**
	 * 用户中心未登录
	 * 
	 * @return
	 */
	private boolean userCenterIsLogouted() {
		return MyCarActivity.loginState == Constants.LOGIN_STATE_LOGOUTED;
	}

	/**
	 * 用户中心未登录
	 * 
	 * @return
	 */
	private boolean userCenterIsLogined() {
		return MyCarActivity.loginState == Constants.LOGIN_STATE_LOGINED;
	}

	@Override
	public void onDestroy() {
		Log.e("IM", "ImLoginActivity.onDestroy()");
		// mImServiceManager.stopImService(this);
		if (mCustomDialog != null) {
			mCustomDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		registerMsgObserver();
		mImServiceManager.startup(mContext, onServiceConnected);

		if (userCenterIsLogouted()) {
			Toast.makeText(mContext, R.string.im_please_login_usercenter_first,
					Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ImLoginActivity.this, LoginActivity.class));
			this.finish();
		} else if (userCenterIsLogining()) {
			Toast.makeText(mContext,
					R.string.im_usercenter_logining_please_wait,
					Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
		super.onResume();
	}

	@Override
	public void onPause() {
		mImServiceManager.shutdown(mContext);
		unRegisterMsgObserver();
		super.onPause();
	}

	private void findView() {

		button_login = (Button) findViewById(R.id.button_login);
		button_logout = (Button) findViewById(R.id.button_logout);
		test_log_info = (TextView) findViewById(R.id.test_log_info);
		edittext_username = (EditText) findViewById(R.id.edittext_username);
		edittext_password = (EditText) findViewById(R.id.edittext_password);

		mCustomDialog = new CustomDialog(this);
	}

	private void addListener() {

		button_login.setOnClickListener(this);
		button_logout.setOnClickListener(this);

	}

	private void createMsgObserver() {
		mLoginReplyObserver = new ImMsgObserver(ImMsgIds.REPLY_LOGIN, this) {

			@Override
			public void dealMessage(Message msg) {
				switch (msg.arg1) {
				case Login.LOGIN_SUCC:
					updateDialog(R.string.im_login_succ,
							R.string.im_reading_friend_list);
					jumpToTargetAcitvity();
					break;
				case Login.LOGOUT_FAIL:
					String result = msg.getData().getString("data");
					updateDialog(R.string.im_login_fail, result);
					break;
				case Login.LOGIN_REPEAT:
					updateDialog(R.string.im_is_logined,
							R.string.im_reading_friend_list);
					jumpToTargetAcitvity();
					break;
				case Login.LOGOUT_TIMEOUT:
					updateDialog(R.string.im_login_fail,
							R.string.im_net_error_server_connect_time_out);
					break;
				default:
					break;
				}
			}

		};

		mLogoutReplyObserver = new ImMsgObserver(ImMsgIds.REPLY_LOGOUT, this) {

			@Override
			public void dealMessage(Message msg) {
				Toast.makeText(mContext, R.string.im_is_offline,
						Toast.LENGTH_SHORT).show();
			}
		};

		mOnlineListUpdateObserver = new ImMsgObserver(
				ImMsgIds.REPLY_FRIEND_LIST_UPDATED, this) {

			@Override
			public void dealMessage(Message msg) {
				isReadyToJumpToImMainActivity = true;
				jumpToTargetAcitvity();
			}
		};
	}
	private void jumpToTargetAcitvity(boolean needFreshFriendList) {
		if(needFreshFriendList){
			jumpToTargetAcitvity();
		}else{
			isReadyToJumpToImMainActivity = true;
			jumpToTargetAcitvity();
		}
	}
	private void jumpToTargetAcitvity() {
		if (beforeJumpToTargetActivity()) {
			Intent intent = getIntent();
			if (intent.hasExtra(ImConstant.STR_IM_JUMP_TARGET)) {
				if (intent.getStringExtra(ImConstant.STR_IM_JUMP_TARGET)
						.equals(ImConstant.JUMP_TARGET_CHAT_LOG)) {
					startActivity(new Intent(mContext, ChatLogActivity.class));
				} else {
					startActivity(new Intent(mContext, FriendListActivity.class));
				}
			} else {
				startActivity(new Intent(mContext, FriendListActivity.class));
			}
			ImLoginActivity.this.finish();
		} else {
			// 向service请求更新好友列表
			// Log.e("IM", "---> 向service请求更新好友列表");
			ImMsgQueue.addMessage(ImMsgIds.ORDER_UPDATE_FRIEND_LIST);

		}
	}

	private boolean beforeJumpToTargetActivity() {
		return isReadyToJumpToImMainActivity;
	}

	private void registerMsgObserver() {
		ImMsgQueue.getInstance().registerObserver(mLoginReplyObserver);
		ImMsgQueue.getInstance().registerObserver(mLogoutReplyObserver);
		ImMsgQueue.getInstance().registerObserver(mOnlineListUpdateObserver);
	}

	private void unRegisterMsgObserver() {
		ImMsgQueue.getInstance().unRegisterObserver(mLoginReplyObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mLogoutReplyObserver);
		ImMsgQueue.getInstance().unRegisterObserver(mOnlineListUpdateObserver);
	}

	private boolean isLogined() {
		return mImServiceManager.isLogined();
	}

	@Override
	public void onClick(View v) {
		if (!isLogined() && v.getId() != R.id.button_login) {
			Toast.makeText(mContext, R.string.im_please_login,
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (isLogined() && v.getId() == R.id.button_login) {
			Toast.makeText(mContext, R.string.im_is_logined, Toast.LENGTH_SHORT)
					.show();
			jumpToTargetAcitvity();
			return;
		}

		switch (v.getId()) {
		case R.id.button_login:
			// Log.e("IM", "UI->发起登录请求。。。");

			showDialogToLogin(edittext_username.getText().toString(),
					edittext_password.getText().toString());

			break;
		case R.id.button_logout:
			test_log_info.setText("");
			ImMsgQueue.addMessage(ImMsgIds.ORDER_LOGOUT);

		default:
			break;
		}
	}

	private void showDialogToLogin(String username, String password) {
		if (userCenterIsLogined()) {
			if (username == null || password == null || username.length() == 0
					|| password.length() == 0) {
				Toast.makeText(mContext,
						R.string.im_please_login_usercenter_first,
						Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			return;
		}

		if (isLogined()) {
			jumpToTargetAcitvity(false);
			return;
		}

		showDialog();

		Message msg = new Message();
		msg.what = ImMsgIds.ORDER_LOGIN;
		Bundle data = new Bundle();
		data.putString("username", username);
		data.putString("password", password);
		msg.setData(data);
		ImMsgQueue.getInstance().addMessage(msg);

	}

	private void showDialog() {

		Log.e("IM", "showDialog()");
		mCustomDialog.setTitle(R.string.im_logining);
		mCustomDialog.setMessage(mContext.getResources().getString(
				R.string.im_logining_please_wait));
		mCustomDialog.show();
		mCustomDialog.setPositiveButton(R.string.im_cancel,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						ImLoginActivity.this.finish();
					}
				});
		mCustomDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				ImLoginActivity.this.finish();
			}
		});
	}

	private void updateDialog(int titleResId, String result) {
		String title = mContext.getResources().getString(titleResId);
		updateDialog(title, result);
	}

	private void updateDialog(int titleResId, int resultContentResId) {
		String title = mContext.getResources().getString(titleResId);
		String resultContent = mContext.getResources().getString(
				resultContentResId);
		updateDialog(title, resultContent);
	}

	private void updateDialog(String title, String resultContent) {
		mCustomDialog.setTitle(title).setMessage(resultContent);
		mCustomDialog.setPositiveButton(R.string.im_cancel,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						ImLoginActivity.this.finish();
					}
				});
	}
}
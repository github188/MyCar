package com.cnlaunch.mycar.usercenter;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.updatecenter.DeviceActivateGuideActivity;
import com.cnlaunch.mycar.usercenter.database.User;
import com.cnlaunch.mycar.usercenter.model.LoginResult;
import com.j256.ormlite.dao.Dao;

/**
 * @description 
 * @author 向远茂
 * @date：2012-4-10
 */
public class UserRegisterConfirmActivity extends BaseActivity {
	// 调试log信息target
	private static final String TAG = "UserRegisterActivity";
	private static final boolean D = true;
	TextView tvCC;
	TextView tvUsername;
	TextView tvPassword;
	TextView tvEmail;
	TextView tvMobile;
	Button btnActivate;
	Button btnBuy;
	Button btnEnsure;
	
	String userName;
	String password;
	String rePassword;
	String email;
	String mobile;
	String cc;
	SharedPreferences loginSP; // 登录的SharedPreferences
	Map accountsMap; // 账号Map，主要用于回显已登录记录
	Map registerMap;
	private Resources resources;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 去掉标题
		setContentView(R.layout.usercenter_register_result, R.layout.custom_title);
		setCustomeTitleLeft(R.string.usercenter_dbscar_register);
		setCustomeTitleRight("");
		resources = getResources();
		init();
	}
	private void init()
	{
		tvCC = (TextView) findViewById(R.id.usercenter_tv_cc);
		tvUsername = (TextView) findViewById(R.id.usercenter_tv_username);
		tvEmail = (TextView) findViewById(R.id.usercenter_tv_email);
		tvMobile = (TextView) findViewById(R.id.usercenter_tv_mobile);
		btnActivate = (Button) findViewById(R.id.btn_activate);
		//btnBuy = (Button) findViewById(R.id.btn_buy);
		btnEnsure = (Button) findViewById(R.id.usercenter_btn_ensure);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null)
		{
			cc = bundle.containsKey("cc") ? bundle.getString("cc") : ""; 
			userName = bundle.containsKey("userName") ? bundle.getString("userName") : "";
			tvCC.setText(resources.getString(R.string.usercenter_cc) + cc);
			tvUsername.setText(resources.getString(R.string.usercenter_username) + userName);
			tvEmail.setText(resources.getString(R.string.usercenter_email) + (bundle.containsKey("email") ? bundle.getString("email") : ""));
			tvMobile.setText(resources.getString(R.string.usercenter_mobile) + (bundle.containsKey("mobile") ? bundle.getString("mobile") : ""));
		}
		
		btnActivate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserRegisterConfirmActivity.this, DeviceActivateGuideActivity.class);
				startActivity(intent);
				finish();
			}
		});
//		btnBuy.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//                Uri uri = Uri.parse("http://www.dbscar.com");
//                startActivity(new Intent(Intent.ACTION_VIEW, uri));
//                UserRegisterConfirmActivity.this.finish();
//				
//			}
//		});
		btnEnsure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//executeLogin();
				Intent intent = new Intent(UserRegisterConfirmActivity.this, MyCarActivity.class);
				startActivity(intent);
				UserRegisterConfirmActivity.this.finish();
			}
		});
	}
	
	/**
	 * 执行登录
	 */
	private void executeLogin() {
		
		//startProgressDialog("正在登录...");

		// 执行登录
		new LoginThread(cc, password, Constants.SERVICE_LOGIN_METHOD_NAME,
				mHandler,UserRegisterConfirmActivity.this).start();		
	}

	/**
	 * 保存登录信息
	 * 
	 * @param settings
	 * @param obj
	 */
	private  void saveLoginInfo(SharedPreferences settings, Object obj) {
		// 保存登录信息
		settings.edit()
				.putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGIN)
				.commit(); // 已登录
		settings.edit()
		.putString(UsercenterConstants.LAST_LOGIN_PWD,
				password).commit(); // 密码

		settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, true).commit(); // 自动登录

		LoginResult loginResult = (LoginResult) obj;
		settings.edit()
				.putString(UsercenterConstants.LAST_LOGIN_ACCOUNT,
						userName).commit(); // 登录账号
		settings.edit().putString(UsercenterConstants.LOGIN_TOKEN, loginResult.token)
				.commit(); // 登录令牌
		settings.edit()
				.putLong(UsercenterConstants.LOGIN_SERVICE_TIME,
						loginResult.serverSystemTime).commit(); // 服务器时间
        settings.edit().putString(UsercenterConstants.LOGIN_CC, loginResult.cc).commit(); // cc
		// 指定当前数据库名称
		MyCarConfig.currentCCToDbName = loginResult.cc + ".db";
		loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);

        loginSP.edit()
        .remove(userName).commit();
		loginSP.edit()
		.putString(userName,
				password).commit(); // 记录登录

		MyCarActivity.cc = loginResult.cc; // CC号码
		MyCarActivity.password = password;
		MyCarActivity.isLogin = true; // 是否登录： 是
		MyCarActivity.token = loginResult.token; // 令牌
		MyCarActivity.csInterval = new Date().getTime()
				- loginResult.serverSystemTime; // 客户端和服务端的时间间隔
		MyCarActivity.accountsArray = getAccounts(); // 给登录记录重新赋值
		
		// 发送广播更新登录状态
		Intent intent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
		intent.putExtra("isLoginFlag", true);
		sendBroadcast(intent);
		
		// 发送广播更新登录状态
		Intent titleIntent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
		intent.putExtra("isLoginFlag", true);
		sendBroadcast(titleIntent);
	}
	/**
	 * 从SharedPreferences里面取出登录记录，key是CC号码，value就是密码
	 * 
	 * @return
	 */
	private String[] getAccounts() {
		// 从SharedPreferences里面取出登录记录
		loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);
		accountsMap = loginSP.getAll();
		String[] accountsArray;
		// key是CC号码，value就是密码
		Set accountsSet = accountsMap.keySet();
		if (accountsSet != null && accountsSet.size() > 0) {
			accountsArray = new String[accountsSet.size()];
			Iterator it = accountsSet.iterator();
			int i = 0;
			while (it.hasNext()) {
				accountsArray[i++] = it.next().toString();
			}
			return accountsArray;
		}
		return null;
	}
	/**
	 * 创建用户表 当用户注册成功后会为该用户生成以该用户CC号 + “.db”为名称的数据库，
	 * 同时会生成一张用户表，里面默认生成CC号，用户昵称，手机号码，邮箱地址 四个默认主要字段，其余的用户信息由用户自行添加
	 * 
	 * @param cc
	 */
	private void createUser(String cc) {

		Dao<User, Integer> dao = getHelper().getDao(User.class);
		User userCC = new User();
		User userMobile = new User();
		User userEmail = new User();
		User userNickname = new User();
		User userUserName = new User();
		userCC.setLabel("CC");
		userCC.setValue(cc);
		userNickname.setLabel(resources.getString(R.string.usercenter_nickname_));
		userNickname.setValue("");
		userMobile.setLabel(resources.getString(R.string.usercenter_mobile_));
		userMobile.setValue(mobile);
		userEmail.setLabel(resources.getString(R.string.usercenter_email_));
		userEmail.setValue(email);
		userUserName.setLabel(resources.getString(R.string.usercenter_username_));
		userUserName.setValue(userName);
		try {
			dao.createOrUpdate(userCC);
			dao.createOrUpdate(userUserName);
			dao.createOrUpdate(userNickname);
			dao.createOrUpdate(userEmail);
			dao.createOrUpdate(userMobile);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 处理登录消息
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UsercenterConstants.LOGIN_RESULT: // 登录结果
				if (D)
					Log.i(TAG, "登录结果: " + msg.obj);

				//stopProgressDialog();

				// 获得SharedPreferences对象
				SharedPreferences settings = getSharedPreferences(
						UsercenterConstants.MYCAR_SHARED_PREFERENCES,
						Context.MODE_WORLD_WRITEABLE);
				switch (msg.arg1) {
				case UsercenterConstants.RESULT_SUCCESS: // 登录成功

					// 保存登录信息
					saveLoginInfo(settings, msg.obj);
					//askRegisterDevice();
					break;
				case UsercenterConstants.RESULT_FAIL: // 登录失败
					MyCarActivity.failLogin(); // 更新类变量的状态
					// 保存登录信息
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
					//dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_EXCEPTION: // 登录异常
					MyCarActivity.failLogin(); // 更新类变量的状态
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
					//dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_USERNAME_OR_PASSWORD_ERROR:
					MyCarActivity.failLogin(); // 更新类变量的状态
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
					//dialog(msg.obj.toString());
					break;
				}
				break;
			}
		}
	};
}



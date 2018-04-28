package com.cnlaunch.mycar.usercenter;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.cnlaunch.mycar.ChangeUserOnlineState;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.updatecenter.DeviceRegisterActivity;
import com.cnlaunch.mycar.usercenter.database.User;
import com.cnlaunch.mycar.usercenter.database.UsercenterDao;
import com.cnlaunch.mycar.usercenter.model.LoginResult;
import com.cnlaunch.mycar.usercenter.model.RegisterResult;
import com.cnlaunch.mycar.usercenter.model.WSUser;
import com.j256.ormlite.dao.Dao;

public class UserRegisterActivity extends BaseActivity {
	// 调试log信息target
	private static final String TAG = "UserRegisterActivity";
	private static final boolean D = true;
	
	EditText etUsername;
	EditText etPassword;
	EditText etRePassword;
	EditText etEmail;
	EditText etMobile;
	Button btnOk;
	Button btnCancel;
	CheckBox cbShowPassword;
    ProgressDialog pdlg;                       // 进度对话框
    private boolean isOpenProgress = false;   // 是否打开进度框
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
		setContentView(R.layout.usercenter_register, R.layout.custom_title);
		setCustomeTitleLeft(R.string.usercenter_dbscar_register);
		setCustomeTitleRight("");
		resources = getResources();
		// 检查网络连接
		if (!checkNetWork())
		{
			dialog(resources.getString(R.string.usercenter_netword_not_connect));
		}
		findControl();
	}
	@Override
	protected void onStart() {
		super.onStart();
		//registerTitleReceive();
		isOpenProgress = true;
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		isOpenProgress = true;
	}
	@Override
	protected void onStop() {
		stopProgressDialog();
		super.onStop();
	}
	private void findControl()
	{
		
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		etRePassword = (EditText) findViewById(R.id.et_re_password);
		etEmail = (EditText) findViewById(R.id.et_email);
		etMobile = (EditText) findViewById(R.id.et_mobile);
		btnOk = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		cbShowPassword = (CheckBox) findViewById(R.id.usercenter_cb_show_pwd);
		cbShowPassword.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { 
                if(cbShowPassword.isChecked()){//显示密码为可见内容 
                	etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); 
                	etRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); 
                	cbShowPassword.setText(resources.getString(R.string.uc_hide_password)); 
                }else{//隐藏密码为不可见内容 
                	etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                	etRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); 
                    cbShowPassword.setText(resources.getString(R.string.uc_show_password)); 
                } 
            } 
        }); 
		btnOk.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				if (!checkNetWork())
				{
					dialog(resources.getString(R.string.usercenter_netword_not_connect));
				}
				else
				{
					// 注册用户
					register();
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
				intent.putExtra("forward", 1); // 登录成功跳转到主界面
				startActivity(intent);
				UserRegisterActivity.this.finish();
			}
		});
	}
	
	/**
	 * 检查网络连接
	 * @return
	 */
	private boolean checkNetWork() 
	{
		//检查网络连接
		ConnectivityManager mConnectivityManager = (ConnectivityManager)UserRegisterActivity
				.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if(mNetworkInfo!=null && mNetworkInfo.isConnected())
		{
			return true;
		}
		return false;
	}
	/**
	 * 弹出对话框
	 * @param message
	 * @author xiangyuanmao
	 */
	protected void dialog(String message) {
		final CustomDialog customDialog=new CustomDialog(this);
		customDialog.setMessage(message); // 弹出信息
		customDialog.setTitle(resources.getString(R.string.uc_notice));
		customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						customDialog.dismiss();
					}
				});
//		customDialog.setNegativeButton("取消", 	new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(UserRegisterActivity.this, MyCarActivity.class);
//				startActivity(intent);
//				customDialog.dismiss();
//				UserRegisterActivity.this.finish();
//				
//			}
//		});
		customDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	private void stopProgressDialog()
	{
		if(D) Log.d(TAG, "stopProgressDialog come in ");
		if (pdlg == null)
		{		
			if(D) Log.d(TAG, "pdlg == null ");
			return ;
		}
		else
		{
			if(D) Log.d(TAG, pdlg.toString());
			if (pdlg.isShowing())
			{
				if(D) Log.d(TAG, "pdlg is showing");
				pdlg.dismiss();
			}
		}
	}
	
	/**
	 * 开启进度对话框
	 * @param pdlg
	 * @param message
	 */
	private void startProgressDialog(String message)
	{
		if (isOpenProgress)
		{
			if (pdlg == null)
			{
				// 实例化一个进度框
				pdlg = new ProgressDialog(this);
			}
			else if (pdlg.isShowing())
			{
				pdlg.dismiss();
			}
			pdlg.setMessage(message);
			pdlg.show();
		}
	}
	
	class RegisterThread extends Thread
	{
		TreeMap paraMap;
		public RegisterThread(TreeMap map)
		{
			paraMap = map;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			RequestParameter requestParameter = new RequestParameter(
			    Constants.SERVICE_LOGIN, "registeUser", null, paraMap,
					false);
			WebServiceManager wsm = new WebServiceManager(requestParameter);
	
			// 获得注册结果对象
			Object object = wsm.execute().object;
			SoapObject result = null;
			if (object != null && object instanceof SoapObject)
			{
				result = (SoapObject) object;
				
				// 取到soap对象中的第一个元素
				SoapObject so = (SoapObject) result.getProperty(0);
				
				// 实例化本地注册结果对象
				RegisterResult registerResult = new RegisterResult();
				
				// 取得服务端返回的响应code
				int code = so.hasProperty("code") ? new Integer(so.getProperty("code")
						.toString()).intValue() : -1;
				registerResult.message = UserCenterCommon.getWebserviceResponseMessage(resources, code);
				if (code == UsercenterConstants.RESULT_SUCCESS)
				{
					
					// 取得服务端返回的CC号码
					registerResult.cc = so.getProperty("cc").toString();
					// 取得服务端返回的初始密码
					registerResult.initPassword = so
							.getProperty("initPassword").toString();
					
					// 通知UI主线程登录结果
					mHandler.obtainMessage(UsercenterConstants.REGISTER_RESULT,
							code, 0, registerResult).sendToTarget();
				}
				else
				{
					// 通知UI主线程登录结果
					mHandler.obtainMessage(UsercenterConstants.REGISTER_RESULT,
							code, 0, registerResult).sendToTarget();
				}
				
			}
			// 网络不是很稳定，很有必要判空
			else
			{
				// 通知UI主线程登录结果
				mHandler.obtainMessage(UsercenterConstants.REGISTER_RESULT,
						UsercenterConstants.RESULT_FAIL, 0, resources.getString(R.string.usercenter_fail_to_register)).sendToTarget();
		
			}
		}
	}

	private boolean check(String userName, String password, String rePasswrod, String email)
	{
		if (userName == null || userName.equals(""))
		{
			dialog(resources.getString(R.string.uc_username_is_null));
			return false;
		}
		//增加用户名校检功能
		if (!UserCenterCommon.checkStrLength(userName, 20, 1)||!UserCenterCommon.checkStrLength(userName, 5, 2))
		{
			dialog(resources.getString(R.string.uc_username_is_illegal_1));
			return false;
		}
		if (!UserCenterCommon.isUserName(userName))
		{
			dialog(resources.getString(R.string.uc_username_is_illegal));
			return false;
		}
		
		if (!UserCenterCommon.checkPasswordRegular(password))
		{
			
			dialog(resources.getString(R.string.uc_password_is_illegal));
			return false;
		}
		if (!UserCenterCommon.checkPasswordRegular(rePasswrod))
		{
			
			dialog(resources.getString(R.string.uc_repassword_is_illegal));
			return false;
		}
		if (!password.equals(rePasswrod))
		{
			dialog(resources.getString(R.string.uc_password_different));
			return false;
		}
		if (email == null || email.equals(""))
		{
			dialog(resources.getString(R.string.uc_email_is_null));
			return false;
		}
		if (!UserCenterCommon.isEmail(email))
		{
			dialog(resources.getString(R.string.uc_email_is_illegal));
			return false;
		}
		return true;
	}
	private void register()
	{
		userName = etUsername.getText().toString().trim();
		password = etPassword.getText().toString().trim();
		rePassword = etRePassword.getText().toString().trim();
		email = etEmail.getText().toString().trim();
		mobile = etMobile.getText().toString().trim();
		
		 if(!check( userName, password, rePassword, email))
		 {
			 return;
		 }
		
		TreeMap paraMap = new TreeMap();
//		userName = "randy7";
//		password = "123456";
//		email = "randy1098@126.com";
//		mobile = "13927421015";
		registerMap = paraMap;
		paraMap.put("userName", userName);
		paraMap.put("password", password);
		paraMap.put("email", email);
		paraMap.put("mobile", mobile);
		new RegisterThread(paraMap).start();
		startProgressDialog(resources.getString(R.string.usercenter_registing));
	}

	
	// 处理登录消息
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UsercenterConstants.LOGIN_RESULT: // 登录结果
				if (D)
					Log.i(TAG, "登录结果: " + msg.obj);

				stopProgressDialog();

				// 获得SharedPreferences对象
				SharedPreferences settings = getSharedPreferences(
						UsercenterConstants.MYCAR_SHARED_PREFERENCES,
						Context.MODE_WORLD_WRITEABLE);
				switch (msg.arg1) {
				case UsercenterConstants.RESULT_SUCCESS: // 登录成功

					// 保存登录信息
					saveLoginInfo(settings, msg.obj);
					// 生成用户数据库，指定当前数据库名称
					MyCarConfig.currentCCToDbName = cc + ".db";
                	WSUser wsUser = new WSUser();
                	wsUser.cc = cc;
                	wsUser.userName = (String)registerMap.get("userName");
                	wsUser.nickname = "";
                	wsUser.email = (String)registerMap.get("email");
                	wsUser.mobile = (String)registerMap.get("mobile");
                	wsUser.isBindEmail = Constants.USERCENTER_NOT_BIND;
                	wsUser.isBindMobile = Constants.USERCENTER_NOT_BIND;
                	
                	// 刷新本地数据库中的User表
                	Dao<User, Integer> dao = getHelper().getDao(User.class);
                	UsercenterDao.updateUser(resources,dao,wsUser); 
                    ChangeUserOnlineState cuos = new ChangeUserOnlineState();
                    cuos.executeAfterLogin();
					// 跳转到注册成功界面
					Intent intent = new Intent(UserRegisterActivity.this, UserRegisterConfirmActivity.class);
					Bundle bundle = new Bundle();	
					bundle.putString("cc", cc);
					bundle.putString("userName", (String)registerMap.get("userName"));
					bundle.putString("password", (String)registerMap.get("password"));
					bundle.putString("email", (String)registerMap.get("email"));
					bundle.putString("mobile", (String)registerMap.get("mobile"));
					intent.putExtras(bundle);
					startActivity(intent);
					 MyCarActivity.loginState = Constants.LOGIN_STATE_LOGINED; // 已登录
					finish();
					break;
				case UsercenterConstants.RESULT_FAIL: // 登录失败
				    MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
					MyCarActivity.failLogin(); // 更新类变量的状态
					// 保存登录信息
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
					dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_EXCEPTION: // 登录异常
				    MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
					MyCarActivity.failLogin(); // 更新类变量的状态
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
					dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_USERNAME_OR_PASSWORD_ERROR:
				    MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // 未登录
					MyCarActivity.failLogin(); // 更新类变量的状态
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // 未登录
					dialog(msg.obj.toString());
					break;
				}
				break;
			case UsercenterConstants.REGISTER_RESULT: // 注册
				if (D)
					Log.i(TAG, "收到注册结果！ ");
				stopProgressDialog();
				switch (msg.arg1) {
				case UsercenterConstants.RESULT_SUCCESS: // 注册成功
					
					RegisterResult registerResult = (RegisterResult) msg.obj;
					cc = registerResult.cc;
					if (D)
						Log.i(TAG, "注册结果: 成功 ! CC：" + cc );
					// 生成用户数据库，指定当前数据库名称
					MyCarConfig.currentCCToDbName = cc + ".db";
                	WSUser wsUser = new WSUser();
                	wsUser.cc = cc;
                	wsUser.userName = (String)registerMap.get("userName");
                	wsUser.nickname = "";
                	wsUser.email = (String)registerMap.get("email");
                	wsUser.mobile = (String)registerMap.get("mobile");
                	wsUser.isBindEmail = Constants.USERCENTER_NOT_BIND;
                	wsUser.isBindMobile = Constants.USERCENTER_NOT_BIND;
                	
                	// 刷新本地数据库中的User表
                	Dao<User, Integer> dao = getHelper().getDao(User.class);
                	UsercenterDao.updateUser(resources,dao,wsUser); 
                	executeLogin();
                	
//					// 跳转到注册成功界面
//					Intent intent = new Intent(UserRegisterActivity.this, UserRegisterConfirmActivity.class);
//					Bundle bundle = new Bundle();	
//					bundle.putString("cc", cc);
//					bundle.putString("userName", (String)registerMap.get("userName"));
//					bundle.putString("password", (String)registerMap.get("password"));
//					bundle.putString("email", (String)registerMap.get("email"));
//					bundle.putString("mobile", (String)registerMap.get("mobile"));
//					intent.putExtras(bundle);
//					startActivity(intent);
//					UserRegisterActivity.this.finish();
					//
					// 弹出对话框，显示注册结果
					//showRegisterResult(resources.getString(R.string.uc_register_success), true);

					break;
				case UsercenterConstants.RESULT_FAIL: // 注册失败
					if (D)
						Log.i(TAG, "注册结果: 失败");
					if (msg.obj != null && msg.obj instanceof RegisterResult)
					{
						
						showRegisterResult(((RegisterResult)msg.obj).message, false);
					}
					else
					{
						showRegisterResult(resources.getString(R.string.intenet_invalid), false);
					}
					break;
				case UsercenterConstants.RESULT_EXCEPTION: // 注册异常
					if (msg.obj != null && msg.obj instanceof RegisterResult)
					{
						
						showRegisterResult(((RegisterResult)msg.obj).message, false);
					}
					else
					{
						showRegisterResult(resources.getString(R.string.intenet_invalid), false);
					}
					if (D)
						Log.i(TAG, "注册结果: 异常");
					break;
				default:
					if (D)
						Log.i(TAG, "注册结果: 失败");
					if (msg.obj != null && msg.obj instanceof RegisterResult)
					{
						
						showRegisterResult(((RegisterResult)msg.obj).message, false);
					}
					else
					{
						showRegisterResult(resources.getString(R.string.intenet_invalid), false);
					}
					break;
				}
				break;
			}
		}
	};
	
	/**
	 * 显示注册结果
	 */
	protected void showRegisterResult(String message, final boolean isSuccess) 
	{

		final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
		customAlertDialog.setMessage(message);
		customAlertDialog.setTitle(resources.getString(R.string.uc_register_result));

		customAlertDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener() {

			@Override
			public void onClick(View v) {
				customAlertDialog.dismiss();
				if (isSuccess)
				{
					executeLogin();
				}
			}
		});
		customAlertDialog.show();
	}

	/**
	 * 询问是否立即注册设备
	 * @param message
	 * @author xiangyuanmao
	 */
	protected void askRegisterDevice() {
		
		final CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
		customAlertDialog.setMessage(resources.getString(R.string.uc_ask_register_device));
		customAlertDialog.setTitle(resources.getString(R.string.uc_notice));

		customAlertDialog.setPositiveButton(resources.getString(R.string.manager_ensure), new OnClickListener() {

			@Override
			public void onClick(View v) {
				customAlertDialog.dismiss();
				Intent intent = new Intent(UserRegisterActivity.this, DeviceRegisterActivity.class);
				startActivity(intent);
				UserRegisterActivity.this.finish();
			}
		});

		customAlertDialog.setNegativeButton(resources.getString(R.string.manager_cancel), new OnClickListener() {

			@Override
			public void onClick(View v) {
				customAlertDialog.dismiss();
				Intent intent = new Intent(UserRegisterActivity.this, MyCarActivity.class);
				startActivity(intent);
				UserRegisterActivity.this.finish();
			}
		});
		customAlertDialog.show();
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
        loginSP.edit().remove(userName).commit();
		loginSP.edit()
		.putString(userName,
				password).commit(); // 记录登录

		MyCarActivity.cc = loginResult.cc; // CC号码
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
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
		intent.putExtra("forward", 1); // 登录成功跳转到主界面
		startActivity(intent);
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
	 * 执行登录
	 */
	private void executeLogin() {
		
		startProgressDialog(resources.getString(R.string.usercenter_logining));

		// 执行登录
		new LoginThread(cc, password, Constants.SERVICE_LOGIN_METHOD_NAME,
				mHandler,UserRegisterActivity.this).start();		
	}
}

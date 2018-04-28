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
	// ����log��Ϣtarget
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
    ProgressDialog pdlg;                       // ���ȶԻ���
    private boolean isOpenProgress = false;   // �Ƿ�򿪽��ȿ�
	String userName;
	String password;
	String rePassword;
	String email;
	String mobile;
	String cc;
	SharedPreferences loginSP; // ��¼��SharedPreferences
	Map accountsMap; // �˺�Map����Ҫ���ڻ����ѵ�¼��¼
	Map registerMap;
	private Resources resources;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ȥ������
		setContentView(R.layout.usercenter_register, R.layout.custom_title);
		setCustomeTitleLeft(R.string.usercenter_dbscar_register);
		setCustomeTitleRight("");
		resources = getResources();
		// �����������
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
                if(cbShowPassword.isChecked()){//��ʾ����Ϊ�ɼ����� 
                	etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); 
                	etRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); 
                	cbShowPassword.setText(resources.getString(R.string.uc_hide_password)); 
                }else{//��������Ϊ���ɼ����� 
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
					// ע���û�
					register();
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
				intent.putExtra("forward", 1); // ��¼�ɹ���ת��������
				startActivity(intent);
				UserRegisterActivity.this.finish();
			}
		});
	}
	
	/**
	 * �����������
	 * @return
	 */
	private boolean checkNetWork() 
	{
		//�����������
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
	 * �����Ի���
	 * @param message
	 * @author xiangyuanmao
	 */
	protected void dialog(String message) {
		final CustomDialog customDialog=new CustomDialog(this);
		customDialog.setMessage(message); // ������Ϣ
		customDialog.setTitle(resources.getString(R.string.uc_notice));
		customDialog.setPositiveButton(resources.getString(R.string.bluetoothconnect_input_ok),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						customDialog.dismiss();
					}
				});
//		customDialog.setNegativeButton("ȡ��", 	new OnClickListener() {
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
	 * �رս��ȶԻ���
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
	 * �������ȶԻ���
	 * @param pdlg
	 * @param message
	 */
	private void startProgressDialog(String message)
	{
		if (isOpenProgress)
		{
			if (pdlg == null)
			{
				// ʵ����һ�����ȿ�
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
	
			// ���ע��������
			Object object = wsm.execute().object;
			SoapObject result = null;
			if (object != null && object instanceof SoapObject)
			{
				result = (SoapObject) object;
				
				// ȡ��soap�����еĵ�һ��Ԫ��
				SoapObject so = (SoapObject) result.getProperty(0);
				
				// ʵ��������ע��������
				RegisterResult registerResult = new RegisterResult();
				
				// ȡ�÷���˷��ص���Ӧcode
				int code = so.hasProperty("code") ? new Integer(so.getProperty("code")
						.toString()).intValue() : -1;
				registerResult.message = UserCenterCommon.getWebserviceResponseMessage(resources, code);
				if (code == UsercenterConstants.RESULT_SUCCESS)
				{
					
					// ȡ�÷���˷��ص�CC����
					registerResult.cc = so.getProperty("cc").toString();
					// ȡ�÷���˷��صĳ�ʼ����
					registerResult.initPassword = so
							.getProperty("initPassword").toString();
					
					// ֪ͨUI���̵߳�¼���
					mHandler.obtainMessage(UsercenterConstants.REGISTER_RESULT,
							code, 0, registerResult).sendToTarget();
				}
				else
				{
					// ֪ͨUI���̵߳�¼���
					mHandler.obtainMessage(UsercenterConstants.REGISTER_RESULT,
							code, 0, registerResult).sendToTarget();
				}
				
			}
			// ���粻�Ǻ��ȶ������б�Ҫ�п�
			else
			{
				// ֪ͨUI���̵߳�¼���
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
		//�����û���У�칦��
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

	
	// �����¼��Ϣ
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UsercenterConstants.LOGIN_RESULT: // ��¼���
				if (D)
					Log.i(TAG, "��¼���: " + msg.obj);

				stopProgressDialog();

				// ���SharedPreferences����
				SharedPreferences settings = getSharedPreferences(
						UsercenterConstants.MYCAR_SHARED_PREFERENCES,
						Context.MODE_WORLD_WRITEABLE);
				switch (msg.arg1) {
				case UsercenterConstants.RESULT_SUCCESS: // ��¼�ɹ�

					// �����¼��Ϣ
					saveLoginInfo(settings, msg.obj);
					// �����û����ݿ⣬ָ����ǰ���ݿ�����
					MyCarConfig.currentCCToDbName = cc + ".db";
                	WSUser wsUser = new WSUser();
                	wsUser.cc = cc;
                	wsUser.userName = (String)registerMap.get("userName");
                	wsUser.nickname = "";
                	wsUser.email = (String)registerMap.get("email");
                	wsUser.mobile = (String)registerMap.get("mobile");
                	wsUser.isBindEmail = Constants.USERCENTER_NOT_BIND;
                	wsUser.isBindMobile = Constants.USERCENTER_NOT_BIND;
                	
                	// ˢ�±������ݿ��е�User��
                	Dao<User, Integer> dao = getHelper().getDao(User.class);
                	UsercenterDao.updateUser(resources,dao,wsUser); 
                    ChangeUserOnlineState cuos = new ChangeUserOnlineState();
                    cuos.executeAfterLogin();
					// ��ת��ע��ɹ�����
					Intent intent = new Intent(UserRegisterActivity.this, UserRegisterConfirmActivity.class);
					Bundle bundle = new Bundle();	
					bundle.putString("cc", cc);
					bundle.putString("userName", (String)registerMap.get("userName"));
					bundle.putString("password", (String)registerMap.get("password"));
					bundle.putString("email", (String)registerMap.get("email"));
					bundle.putString("mobile", (String)registerMap.get("mobile"));
					intent.putExtras(bundle);
					startActivity(intent);
					 MyCarActivity.loginState = Constants.LOGIN_STATE_LOGINED; // �ѵ�¼
					finish();
					break;
				case UsercenterConstants.RESULT_FAIL: // ��¼ʧ��
				    MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
					MyCarActivity.failLogin(); // �����������״̬
					// �����¼��Ϣ
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // δ��¼
					dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_EXCEPTION: // ��¼�쳣
				    MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
					MyCarActivity.failLogin(); // �����������״̬
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // δ��¼
					dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_USERNAME_OR_PASSWORD_ERROR:
				    MyCarActivity.loginState = Constants.LOGIN_STATE_LOGOUTED; // δ��¼
					MyCarActivity.failLogin(); // �����������״̬
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // δ��¼
					dialog(msg.obj.toString());
					break;
				}
				break;
			case UsercenterConstants.REGISTER_RESULT: // ע��
				if (D)
					Log.i(TAG, "�յ�ע������ ");
				stopProgressDialog();
				switch (msg.arg1) {
				case UsercenterConstants.RESULT_SUCCESS: // ע��ɹ�
					
					RegisterResult registerResult = (RegisterResult) msg.obj;
					cc = registerResult.cc;
					if (D)
						Log.i(TAG, "ע����: �ɹ� ! CC��" + cc );
					// �����û����ݿ⣬ָ����ǰ���ݿ�����
					MyCarConfig.currentCCToDbName = cc + ".db";
                	WSUser wsUser = new WSUser();
                	wsUser.cc = cc;
                	wsUser.userName = (String)registerMap.get("userName");
                	wsUser.nickname = "";
                	wsUser.email = (String)registerMap.get("email");
                	wsUser.mobile = (String)registerMap.get("mobile");
                	wsUser.isBindEmail = Constants.USERCENTER_NOT_BIND;
                	wsUser.isBindMobile = Constants.USERCENTER_NOT_BIND;
                	
                	// ˢ�±������ݿ��е�User��
                	Dao<User, Integer> dao = getHelper().getDao(User.class);
                	UsercenterDao.updateUser(resources,dao,wsUser); 
                	executeLogin();
                	
//					// ��ת��ע��ɹ�����
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
					// �����Ի�����ʾע����
					//showRegisterResult(resources.getString(R.string.uc_register_success), true);

					break;
				case UsercenterConstants.RESULT_FAIL: // ע��ʧ��
					if (D)
						Log.i(TAG, "ע����: ʧ��");
					if (msg.obj != null && msg.obj instanceof RegisterResult)
					{
						
						showRegisterResult(((RegisterResult)msg.obj).message, false);
					}
					else
					{
						showRegisterResult(resources.getString(R.string.intenet_invalid), false);
					}
					break;
				case UsercenterConstants.RESULT_EXCEPTION: // ע���쳣
					if (msg.obj != null && msg.obj instanceof RegisterResult)
					{
						
						showRegisterResult(((RegisterResult)msg.obj).message, false);
					}
					else
					{
						showRegisterResult(resources.getString(R.string.intenet_invalid), false);
					}
					if (D)
						Log.i(TAG, "ע����: �쳣");
					break;
				default:
					if (D)
						Log.i(TAG, "ע����: ʧ��");
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
	 * ��ʾע����
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
	 * ѯ���Ƿ�����ע���豸
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
	 * �����û��� ���û�ע��ɹ����Ϊ���û������Ը��û�CC�� + ��.db��Ϊ���Ƶ����ݿ⣬
	 * ͬʱ������һ���û�������Ĭ������CC�ţ��û��ǳƣ��ֻ����룬�����ַ �ĸ�Ĭ����Ҫ�ֶΣ�������û���Ϣ���û��������
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
	 * �����¼��Ϣ
	 * 
	 * @param settings
	 * @param obj
	 */
	private  void saveLoginInfo(SharedPreferences settings, Object obj) {
		// �����¼��Ϣ
		settings.edit()
				.putString(UsercenterConstants.LOGIN_STATE, UsercenterConstants.LOGIN_STATE_LOGIN)
				.commit(); // �ѵ�¼
		settings.edit()
		.putString(UsercenterConstants.LAST_LOGIN_PWD,
				password).commit(); // ����

		settings.edit().putBoolean(UsercenterConstants.IS_AUTO_LOGIN, true).commit(); // �Զ���¼

		LoginResult loginResult = (LoginResult) obj;
		settings.edit()
				.putString(UsercenterConstants.LAST_LOGIN_ACCOUNT,
						userName).commit(); // ��¼�˺�
		settings.edit().putString(UsercenterConstants.LOGIN_TOKEN, loginResult.token)
				.commit(); // ��¼����
		settings.edit()
				.putLong(UsercenterConstants.LOGIN_SERVICE_TIME,
						loginResult.serverSystemTime).commit(); // ������ʱ��
        settings.edit().putString(UsercenterConstants.LOGIN_CC, loginResult.cc).commit(); // cc
		// ָ����ǰ���ݿ�����
		MyCarConfig.currentCCToDbName = loginResult.cc + ".db";
		loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);
        loginSP.edit().remove(userName).commit();
		loginSP.edit()
		.putString(userName,
				password).commit(); // ��¼��¼

		MyCarActivity.cc = loginResult.cc; // CC����
		MyCarActivity.isLogin = true; // �Ƿ��¼�� ��
		MyCarActivity.token = loginResult.token; // ����
		MyCarActivity.csInterval = new Date().getTime()
				- loginResult.serverSystemTime; // �ͻ��˺ͷ���˵�ʱ����
		MyCarActivity.accountsArray = getAccounts(); // ����¼��¼���¸�ֵ
		
		// ���͹㲥���µ�¼״̬
		Intent intent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
		intent.putExtra("isLoginFlag", true);
		sendBroadcast(intent);
		
		// ���͹㲥���µ�¼״̬
		Intent titleIntent = new Intent(Constants.MAIN_TITLE_ACTION_USERCENTER);
		intent.putExtra("isLoginFlag", true);
		sendBroadcast(titleIntent);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
		intent.putExtra("forward", 1); // ��¼�ɹ���ת��������
		startActivity(intent);
	}
	/**
	 * ��SharedPreferences����ȡ����¼��¼��key��CC���룬value��������
	 * 
	 * @return
	 */
	private String[] getAccounts() {
		// ��SharedPreferences����ȡ����¼��¼
		loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);
		accountsMap = loginSP.getAll();
		String[] accountsArray;
		// key��CC���룬value��������
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
	 * ִ�е�¼
	 */
	private void executeLogin() {
		
		startProgressDialog(resources.getString(R.string.usercenter_logining));

		// ִ�е�¼
		new LoginThread(cc, password, Constants.SERVICE_LOGIN_METHOD_NAME,
				mHandler,UserRegisterActivity.this).start();		
	}
}

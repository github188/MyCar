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
 * @author ��Զï
 * @date��2012-4-10
 */
public class UserRegisterConfirmActivity extends BaseActivity {
	// ����log��Ϣtarget
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
	SharedPreferences loginSP; // ��¼��SharedPreferences
	Map accountsMap; // �˺�Map����Ҫ���ڻ����ѵ�¼��¼
	Map registerMap;
	private Resources resources;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ȥ������
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
	 * ִ�е�¼
	 */
	private void executeLogin() {
		
		//startProgressDialog("���ڵ�¼...");

		// ִ�е�¼
		new LoginThread(cc, password, Constants.SERVICE_LOGIN_METHOD_NAME,
				mHandler,UserRegisterConfirmActivity.this).start();		
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

        loginSP.edit()
        .remove(userName).commit();
		loginSP.edit()
		.putString(userName,
				password).commit(); // ��¼��¼

		MyCarActivity.cc = loginResult.cc; // CC����
		MyCarActivity.password = password;
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
	// �����¼��Ϣ
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UsercenterConstants.LOGIN_RESULT: // ��¼���
				if (D)
					Log.i(TAG, "��¼���: " + msg.obj);

				//stopProgressDialog();

				// ���SharedPreferences����
				SharedPreferences settings = getSharedPreferences(
						UsercenterConstants.MYCAR_SHARED_PREFERENCES,
						Context.MODE_WORLD_WRITEABLE);
				switch (msg.arg1) {
				case UsercenterConstants.RESULT_SUCCESS: // ��¼�ɹ�

					// �����¼��Ϣ
					saveLoginInfo(settings, msg.obj);
					//askRegisterDevice();
					break;
				case UsercenterConstants.RESULT_FAIL: // ��¼ʧ��
					MyCarActivity.failLogin(); // �����������״̬
					// �����¼��Ϣ
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // δ��¼
					//dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_EXCEPTION: // ��¼�쳣
					MyCarActivity.failLogin(); // �����������״̬
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // δ��¼
					//dialog(msg.obj.toString());
					break;
				case UsercenterConstants.RESULT_USERNAME_OR_PASSWORD_ERROR:
					MyCarActivity.failLogin(); // �����������״̬
					settings.edit().putString(UsercenterConstants.LOGIN_STATE,
							UsercenterConstants.LOGIN_STATE_LOGOUT).commit(); // δ��¼
					//dialog(msg.obj.toString());
					break;
				}
				break;
			}
		}
	};
}



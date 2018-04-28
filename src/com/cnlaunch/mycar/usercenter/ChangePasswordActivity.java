package com.cnlaunch.mycar.usercenter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.utils.md5.Md5Helper;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;

/**
 * 用户中心修改密码界面的功能代码
 * 
 * @author jiangjun
 * 
 */

public class ChangePasswordActivity extends BaseActivity implements
		OnClickListener {

	// 调试log信息target
	private static final String TAG = "ChangePasswordActivity";
	private static final boolean D = true;

	/* 界面控件 */
	private TextView etAccount; // 账号编辑框
	private EditText etOrgPwd; // 原密码编辑框
	private EditText etNewPwd; // 新密码编辑框
	private EditText etNewPwdAgain; // 再次新密码编辑框
	private Button btnSubmitChangePwd; // 提交修改按钮
	private Button btnCancelChangePwd; // 取消修改按钮
    private boolean isFinish = false;
	Resources resources;
	ProgressDialog mPropgressDlg; // 修改密码进度对话框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.usercenter_change_password,
				R.layout.custom_title);

		// 左标题
		setCustomeTitleLeft(R.string.usercenter);

		// 右标题
		setCustomeTitleRight(R.string.usercenter_change_pwd);

		/* 初始化界面控件 */
		etAccount = (TextView) findViewById(R.id.usercenter_change_pwd_et_account);
		etOrgPwd = (EditText) findViewById(R.id.usercenter_change_pwd_et_pwd);
		etNewPwd = (EditText) findViewById(R.id.usercenter_change_pwd_et_newpwd);
		etNewPwdAgain = (EditText) findViewById(R.id.usercenter_change_pwd_et_newpwd_again);
		btnSubmitChangePwd = (Button) findViewById(R.id.usercenter_change_pwd_btn_submit_change_pwd);
		btnCancelChangePwd = (Button) findViewById(R.id.usercenter_change_pwd_btn_cancel_change_pwd);

		// 账户直接从参数中获取
		String strAcount = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			strAcount = extras.getString("account");
		}
		etAccount.setText(strAcount);

		/* 为界面控件设置单击监听事件 */
		btnSubmitChangePwd.setOnClickListener(this);
		btnCancelChangePwd.setOnClickListener(this);
		resources = getResources();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.usercenter_change_pwd_btn_submit_change_pwd:// 修改密码
			submitChangPassword(); // 提交修改密码
			break;

		case R.id.usercenter_change_pwd_btn_cancel_change_pwd:// 取消修改密码
			cancelChangPassword();
			break;

		default:
			break;
		}
	}

	/* 修改密码的功能代码 */
	private void submitChangPassword() {

		// /* 帐号规则检查 */
		// CharSequence strAccount = etAccount.getText();
		// if (!UserCenterCommon.checkAccountRegular(strAccount.toString())) {
		//
		// /* 提示用户帐号格式不对，应该是CC号，或手机号，或邮箱规则 */
		// showToast(UsercenterConstants.USERCENTER_TOAST_ACCOUNT_INVALID,
		// Toast.LENGTH_SHORT);
		//
		// /* 用户帐号获取焦点并全选 */
		// etAccount.requestFocus();
		// etAccount.selectAll();
		//
		// return;
		// }

		/* 原密码校验规则检查 */
		CharSequence strPwd = etOrgPwd.getText();
		if (!UserCenterCommon.checkPasswordRegular(strPwd.toString())) {

			/* 提示用户原密码格式不对 */
			showToast(resources.getString(R.string.usercenter_toast_org_pwd_invalid),
					Toast.LENGTH_SHORT);

			/* 用户原密码获取焦点并全选 */
			etOrgPwd.requestFocus();
			etOrgPwd.selectAll();

			return;

		}

		/* 输入的新密码规则检查 */
		CharSequence strNewPwd = etNewPwd.getText();
		if (!UserCenterCommon.checkPasswordRegular(strNewPwd.toString())) {

			/* 提示用户新密码格式不对 */
			showToast(resources.getString(R.string.usercenter_toast_new_pwd_invalid),
					Toast.LENGTH_SHORT);

			/* 用户新密码获取焦点并全选 */
			etNewPwd.requestFocus();
			etNewPwd.selectAll();

			return;

		}

		/* 检查两次输入新密码是否一致 */
		CharSequence strNewPwdAgain = etNewPwdAgain.getText();
		if (strNewPwd.toString().compareTo(strNewPwdAgain.toString()) != 0) {

			// 提示用户两次新输入的密码不相同
			showToast(resources.getString(R.string.usercenter_toast_new_pwd_not_equal),
					Toast.LENGTH_SHORT);

			/* 用户再次输入新密码获取焦点并全选 */
			etNewPwdAgain.requestFocus();
			etNewPwdAgain.selectAll();

			return;

		}

		// 向服务器提交修改密码
		executeChangePwd(mHandler);
	}

	/*
	 * 显示提示信息
	 */
	private void showToast(CharSequence strTip, int duration) {

		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, strTip, duration);
		toast.show();
	}
	/**
	 * 数据加密
	 */
	//TODO
	private String getMd5Pawword(String password)
	{
		MessageDigest messageDigest = null;
        try {
        	messageDigest  = MessageDigest.getInstance("MD5");
	        if (StringUtils.isNotEmpty(password))
	        {
	
				messageDigest.update((password).getBytes("UTF-8"));
	
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return Md5Helper.byteArrayToHexString(messageDigest.digest());
	}
	/*
	 * 执行修改密码线程
	 */
	private void executeChangePwd(Handler handler) {

		mPropgressDlg = new ProgressDialog(this);
		mPropgressDlg
				.setTitle(resources.getString(R.string.usercenter_change_pwd_progress_dlg_title));
		mPropgressDlg
				.setMessage(resources.getString(R.string.usercenter_change_pwd_progress_dlg_body));
		mPropgressDlg.show();

		new Thread() {

			@Override
			public void run() {
				try {
					// 获取帐号，密码
					CharSequence strAccount = etAccount.getText();
					CharSequence strPwd = etOrgPwd.getText();
					CharSequence strNewPwd = etNewPwd.getText();

					// 封装请求参数
					TreeMap paraMap = new TreeMap();
					paraMap.put("newPwd", getMd5Pawword(strNewPwd.toString())); // 新密码
					paraMap.put("oldPwd", getMd5Pawword(strPwd.toString())); // 密码
					paraMap.put("cc", strAccount.toString()); // 账号

					RequestParameter requestParameter = new RequestParameter(
							Constants.SERVICE_USERCENTER, "modifyPassWord",
							null, paraMap, true);
					WebServiceManager wsm = new WebServiceManager(
							requestParameter);
					SoapObject result = (SoapObject) wsm.execute().object;

					if (result != null && result.getProperty(0) != null) {

						mPropgressDlg.cancel();

						SoapObject so = (SoapObject) result.getProperty(0);

						WSResult changePwdResult = new WSResult();
						String strCode = so.getProperty("code") == null ? "-1"
								: so.getProperty("code").toString();
						changePwdResult.code = Integer.parseInt(strCode);
						changePwdResult.message = so.getProperty("message") == null ? resources.getString(R.string.usercenter_message_no_defined)
								: so.getProperty("message").toString();

						int isSuccess = Integer.parseInt(strCode);

						// 通知UI主线程修改密码结果
						mHandler.obtainMessage(
								UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD,
								isSuccess, 0, UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess)).sendToTarget();

					} else {

						mHandler.obtainMessage(
								UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD,
								UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_connect_service_timeout))
								.sendToTarget();
					}
				}

				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					if (D)
						Log.d(TAG, "修改密码异常");

					// 通知UI修改密码结果
					mHandler.obtainMessage(
							UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD,
							UsercenterConstants.RESULT_EXCEPTION, 0,  resources.getString(R.string.usercenter_usercenter_exception))
							.sendToTarget();
				}
			}

		}.start();

	}

	/*
	 * 处理修改密码消息
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// 关闭进度对话框
			mPropgressDlg.cancel();

			switch (msg.what) {
			case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD: // 修改密码结果

				if (D)
					Log.i(TAG, "修改密码结果: " + msg.obj);

				switch (msg.arg1) {

				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_SUCCESS: // 修改密码成功

					promptDialog(resources.getString(R.string.usercenter_result_change_pwd_success_prompt));

					SharedPreferences loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
							Context.MODE_WORLD_WRITEABLE);
					loginSP.edit().remove(etAccount.getText().toString().trim()).commit();
					loginSP.edit().putString(etAccount.getText().toString().trim(), etNewPwd.getText().toString())
					.commit(); // 记录登录
					MyCarActivity.password = etNewPwd.getText().toString();
					// 将密码控件置空
					reset();
					isFinish = true; // 关闭本界面
					break;

				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_FAILED: // 修改密码失败

					promptDialog(resources.getString(R.string.usercenter_result_change_pwd_failed_prompt));
					break;

				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_OLD_PWDE_RROR: // 原密码错误
				    
				    promptDialog(resources.getString(R.string.usercenter_change_pwd_old_pwd_wrong));
					break;

				case UsercenterConstants.RESULT_EXCEPTION: // 修改密码异常

					promptDialog(resources.getString(R.string.usercenter_result_change_pwd_exception_prompt));
					break;
				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_WRONG_ORIGINAL_PWD:
					promptDialog(((WSResult)msg.obj).message);
					break;
				default:
					break;
				}

				break;

			}

		}
	};

	private void reset()
	{
		// 将密码控件置空
		etOrgPwd.setText("");
		etNewPwd.setText("");
		etNewPwdAgain.setText("");
	}
	/**
	 * 修改密码线程结束后的对话框
	 * 
	 * @param message
	 */
	private void promptDialog(String message) {

		final CustomDialog customDialog = new CustomDialog(this);
		customDialog.setMessage(message); // 弹出信息
		customDialog.setTitle(resources.getString(R.string.uc_notice));
		customDialog.setPositiveButton(R.string.ok,new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog.dismiss();
				if (isFinish)
				{
					finish();
				}
				else
				{
					reset();
				}
				
			}
		});
		customDialog.show();
	}

	private void cancelChangPassword() {

		this.finish();
	}

}

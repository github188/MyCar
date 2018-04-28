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
 * �û������޸��������Ĺ��ܴ���
 * 
 * @author jiangjun
 * 
 */

public class ChangePasswordActivity extends BaseActivity implements
		OnClickListener {

	// ����log��Ϣtarget
	private static final String TAG = "ChangePasswordActivity";
	private static final boolean D = true;

	/* ����ؼ� */
	private TextView etAccount; // �˺ű༭��
	private EditText etOrgPwd; // ԭ����༭��
	private EditText etNewPwd; // ������༭��
	private EditText etNewPwdAgain; // �ٴ�������༭��
	private Button btnSubmitChangePwd; // �ύ�޸İ�ť
	private Button btnCancelChangePwd; // ȡ���޸İ�ť
    private boolean isFinish = false;
	Resources resources;
	ProgressDialog mPropgressDlg; // �޸�������ȶԻ���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.usercenter_change_password,
				R.layout.custom_title);

		// �����
		setCustomeTitleLeft(R.string.usercenter);

		// �ұ���
		setCustomeTitleRight(R.string.usercenter_change_pwd);

		/* ��ʼ������ؼ� */
		etAccount = (TextView) findViewById(R.id.usercenter_change_pwd_et_account);
		etOrgPwd = (EditText) findViewById(R.id.usercenter_change_pwd_et_pwd);
		etNewPwd = (EditText) findViewById(R.id.usercenter_change_pwd_et_newpwd);
		etNewPwdAgain = (EditText) findViewById(R.id.usercenter_change_pwd_et_newpwd_again);
		btnSubmitChangePwd = (Button) findViewById(R.id.usercenter_change_pwd_btn_submit_change_pwd);
		btnCancelChangePwd = (Button) findViewById(R.id.usercenter_change_pwd_btn_cancel_change_pwd);

		// �˻�ֱ�ӴӲ����л�ȡ
		String strAcount = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			strAcount = extras.getString("account");
		}
		etAccount.setText(strAcount);

		/* Ϊ����ؼ����õ��������¼� */
		btnSubmitChangePwd.setOnClickListener(this);
		btnCancelChangePwd.setOnClickListener(this);
		resources = getResources();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.usercenter_change_pwd_btn_submit_change_pwd:// �޸�����
			submitChangPassword(); // �ύ�޸�����
			break;

		case R.id.usercenter_change_pwd_btn_cancel_change_pwd:// ȡ���޸�����
			cancelChangPassword();
			break;

		default:
			break;
		}
	}

	/* �޸�����Ĺ��ܴ��� */
	private void submitChangPassword() {

		// /* �ʺŹ����� */
		// CharSequence strAccount = etAccount.getText();
		// if (!UserCenterCommon.checkAccountRegular(strAccount.toString())) {
		//
		// /* ��ʾ�û��ʺŸ�ʽ���ԣ�Ӧ����CC�ţ����ֻ��ţ���������� */
		// showToast(UsercenterConstants.USERCENTER_TOAST_ACCOUNT_INVALID,
		// Toast.LENGTH_SHORT);
		//
		// /* �û��ʺŻ�ȡ���㲢ȫѡ */
		// etAccount.requestFocus();
		// etAccount.selectAll();
		//
		// return;
		// }

		/* ԭ����У������� */
		CharSequence strPwd = etOrgPwd.getText();
		if (!UserCenterCommon.checkPasswordRegular(strPwd.toString())) {

			/* ��ʾ�û�ԭ�����ʽ���� */
			showToast(resources.getString(R.string.usercenter_toast_org_pwd_invalid),
					Toast.LENGTH_SHORT);

			/* �û�ԭ�����ȡ���㲢ȫѡ */
			etOrgPwd.requestFocus();
			etOrgPwd.selectAll();

			return;

		}

		/* ���������������� */
		CharSequence strNewPwd = etNewPwd.getText();
		if (!UserCenterCommon.checkPasswordRegular(strNewPwd.toString())) {

			/* ��ʾ�û��������ʽ���� */
			showToast(resources.getString(R.string.usercenter_toast_new_pwd_invalid),
					Toast.LENGTH_SHORT);

			/* �û��������ȡ���㲢ȫѡ */
			etNewPwd.requestFocus();
			etNewPwd.selectAll();

			return;

		}

		/* ������������������Ƿ�һ�� */
		CharSequence strNewPwdAgain = etNewPwdAgain.getText();
		if (strNewPwd.toString().compareTo(strNewPwdAgain.toString()) != 0) {

			// ��ʾ�û���������������벻��ͬ
			showToast(resources.getString(R.string.usercenter_toast_new_pwd_not_equal),
					Toast.LENGTH_SHORT);

			/* �û��ٴ������������ȡ���㲢ȫѡ */
			etNewPwdAgain.requestFocus();
			etNewPwdAgain.selectAll();

			return;

		}

		// ��������ύ�޸�����
		executeChangePwd(mHandler);
	}

	/*
	 * ��ʾ��ʾ��Ϣ
	 */
	private void showToast(CharSequence strTip, int duration) {

		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, strTip, duration);
		toast.show();
	}
	/**
	 * ���ݼ���
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
	 * ִ���޸������߳�
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
					// ��ȡ�ʺţ�����
					CharSequence strAccount = etAccount.getText();
					CharSequence strPwd = etOrgPwd.getText();
					CharSequence strNewPwd = etNewPwd.getText();

					// ��װ�������
					TreeMap paraMap = new TreeMap();
					paraMap.put("newPwd", getMd5Pawword(strNewPwd.toString())); // ������
					paraMap.put("oldPwd", getMd5Pawword(strPwd.toString())); // ����
					paraMap.put("cc", strAccount.toString()); // �˺�

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

						// ֪ͨUI���߳��޸�������
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
						Log.d(TAG, "�޸������쳣");

					// ֪ͨUI�޸�������
					mHandler.obtainMessage(
							UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD,
							UsercenterConstants.RESULT_EXCEPTION, 0,  resources.getString(R.string.usercenter_usercenter_exception))
							.sendToTarget();
				}
			}

		}.start();

	}

	/*
	 * �����޸�������Ϣ
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// �رս��ȶԻ���
			mPropgressDlg.cancel();

			switch (msg.what) {
			case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD: // �޸�������

				if (D)
					Log.i(TAG, "�޸�������: " + msg.obj);

				switch (msg.arg1) {

				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_SUCCESS: // �޸�����ɹ�

					promptDialog(resources.getString(R.string.usercenter_result_change_pwd_success_prompt));

					SharedPreferences loginSP = getSharedPreferences(UsercenterConstants.LOGIN_SHARED_PREFERENCES,
							Context.MODE_WORLD_WRITEABLE);
					loginSP.edit().remove(etAccount.getText().toString().trim()).commit();
					loginSP.edit().putString(etAccount.getText().toString().trim(), etNewPwd.getText().toString())
					.commit(); // ��¼��¼
					MyCarActivity.password = etNewPwd.getText().toString();
					// ������ؼ��ÿ�
					reset();
					isFinish = true; // �رձ�����
					break;

				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_FAILED: // �޸�����ʧ��

					promptDialog(resources.getString(R.string.usercenter_result_change_pwd_failed_prompt));
					break;

				case UsercenterConstants.USERCENTER_RESULT_CHANGE_PWD_OLD_PWDE_RROR: // ԭ�������
				    
				    promptDialog(resources.getString(R.string.usercenter_change_pwd_old_pwd_wrong));
					break;

				case UsercenterConstants.RESULT_EXCEPTION: // �޸������쳣

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
		// ������ؼ��ÿ�
		etOrgPwd.setText("");
		etNewPwd.setText("");
		etNewPwdAgain.setText("");
	}
	/**
	 * �޸������߳̽�����ĶԻ���
	 * 
	 * @param message
	 */
	private void promptDialog(String message) {

		final CustomDialog customDialog = new CustomDialog(this);
		customDialog.setMessage(message); // ������Ϣ
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

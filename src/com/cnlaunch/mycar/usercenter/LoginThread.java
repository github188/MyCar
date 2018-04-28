package com.cnlaunch.mycar.usercenter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.utils.md5.Md5Helper;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.usercenter.model.LoginResult;

/**
 * ��¼�̣߳�ִ���û���¼�������ֻ�����ʱ���Զ���¼���û��ֶ���¼
 * @author xiangyuanmao
 *
 */
public class LoginThread extends Thread
{
    // ����log��Ϣtarget
   private static final String TAG = "LoginThread";
   private static final boolean D = true;
   
	// ȡ��ϵͳSharedPreferences
	SharedPreferences sp ;   
	TreeMap paraMap ; // ����
	RequestParameter requestParameter;// �����������
	SoapObject result; // ��¼���
	Handler mHandler; // ��UI�߳̽�����Handler
	Context context;
	Resources resources;
	/**
	 * �Զ���¼���캯��
	 * @param nameSpace
	 * @param url
	 * @param methodName
	 */
	public LoginThread(String methodName,Handler handler,Context context)
	{
	    this.context = context;
	    resources = context.getResources();
		// ȡ������¼�˵ĵ�¼�˺ź�����
		if (sp.getString(UsercenterConstants.LAST_LOGIN_ACCOUNT, null) != null 
				&& sp.getString(UsercenterConstants.LAST_LOGIN_PWD, null) != null)
		{
	
			// ��װ�������
			paraMap = new TreeMap();
			paraMap.put("loginKey", sp.getString(UsercenterConstants.LAST_LOGIN_ACCOUNT, null)); // �˺�
			paraMap.put("mobileAppVersion", "1.1"); // �汾
			paraMap.put("password", getMd5Pawword(sp.getString(UsercenterConstants.LAST_LOGIN_PWD, null))); // ����
			requestParameter = new RequestParameter(Constants.SERVICE_LOGIN,methodName,null,paraMap,false);
			this.mHandler = handler;
		}
	}
	
 
	public static String getMd5Pawword(String password)
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
        String md5Password = Md5Helper.byteArrayToHexString(messageDigest.digest());
        Log.d(TAG, "�õ�Md5���룺" + md5Password);
       return md5Password;
	}
	/**
	 * �ֶ���¼�Ĺ��췽��
	 * @param account
	 * @param password
	 * @param nameSpace
	 * @param url
	 * @param methodName
	 * @param handler
	 */
	public LoginThread(String account,String password, String methodName,Handler handler,Context context)
	{
	       this.context = context;
	        resources = context.getResources();
		// ��װ�������
		paraMap = new TreeMap();

		// ���ò���
		paraMap.put("loginKey", account); // �˺�
		paraMap.put("mobileAppVersion", Constants.MYCAR_VERSION); // �汾��
		
	
        paraMap.put("password", getMd5Pawword(password)); // ����
        
		requestParameter = new RequestParameter(Constants.SERVICE_LOGIN, methodName,null,paraMap,false);
		this.mHandler = handler;
	}
	
	@Override
	public void run() 
	{
		try {
			// ʵ����WebServiceManager����
			WebServiceManager wsm = new WebServiceManager(requestParameter);
			Object object = wsm.execute().object;
			if (object instanceof SoapObject)
			{
				result = (SoapObject)object;
			}
			
			// �õ���¼״̬
			if (result != null && result.getProperty(0) != null)
			{
				SoapObject so = (SoapObject)result.getProperty(0);
				// �Ƿ�ɹ�
				int isSuccess = new Integer(so.getProperty("code") == null 
						? "-1" : so.getProperty("code").toString()).intValue();
				String message = so.getProperty("message") == null ? "" : so.getProperty("message").toString();
				if (isSuccess == UsercenterConstants.RESULT_SUCCESS)
				{
					LoginResult loginResult = new LoginResult();
					
					// cc
					loginResult.cc = so.getProperty("cc") == null 
							? "" : so.getProperty("cc").toString();
					
					// ����
					loginResult.token = so.getProperty("token") == null ? "" : so.getProperty("token").toString();
					
					// ������ʱ��
					loginResult.serverSystemTime = so.getProperty("serverSystemTime") == null 
							? 0L : new Long(so.getProperty("serverSystemTime").toString()).longValue();
					
					// ������Ϣ
					loginResult.message = message;

					// ֪ͨUI���̵߳�¼���
			        mHandler.obtainMessage(UsercenterConstants.LOGIN_RESULT, isSuccess, 0, loginResult)
			        .sendToTarget();

				}
				else
				{
					// ֪ͨUI���̵߳�¼���
			        mHandler.obtainMessage(UsercenterConstants.LOGIN_RESULT, isSuccess, 0, 
			            UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess))
			     
			        .sendToTarget();
				}
			}
			else
			{
		        mHandler.obtainMessage(UsercenterConstants.LOGIN_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_service_login_timeout))
		        .sendToTarget();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		    if (D) Log.d(TAG, "��¼�쳣");
			// ֪ͨUI���̵߳�¼���
	        mHandler.obtainMessage(UsercenterConstants.LOGIN_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_login_exception))
	        .sendToTarget();
		}	
	}	
}

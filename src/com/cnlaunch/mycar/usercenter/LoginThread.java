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
 * 登录线程，执行用户登录，包括手机启动时的自动登录和用户手动登录
 * @author xiangyuanmao
 *
 */
public class LoginThread extends Thread
{
    // 调试log信息target
   private static final String TAG = "LoginThread";
   private static final boolean D = true;
   
	// 取得系统SharedPreferences
	SharedPreferences sp ;   
	TreeMap paraMap ; // 参数
	RequestParameter requestParameter;// 请求参数对象
	SoapObject result; // 登录结果
	Handler mHandler; // 和UI线程交互的Handler
	Context context;
	Resources resources;
	/**
	 * 自动登录构造函数
	 * @param nameSpace
	 * @param url
	 * @param methodName
	 */
	public LoginThread(String methodName,Handler handler,Context context)
	{
	    this.context = context;
	    resources = context.getResources();
		// 取得最后登录人的登录账号和密码
		if (sp.getString(UsercenterConstants.LAST_LOGIN_ACCOUNT, null) != null 
				&& sp.getString(UsercenterConstants.LAST_LOGIN_PWD, null) != null)
		{
	
			// 封装请求参数
			paraMap = new TreeMap();
			paraMap.put("loginKey", sp.getString(UsercenterConstants.LAST_LOGIN_ACCOUNT, null)); // 账号
			paraMap.put("mobileAppVersion", "1.1"); // 版本
			paraMap.put("password", getMd5Pawword(sp.getString(UsercenterConstants.LAST_LOGIN_PWD, null))); // 密码
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
        Log.d(TAG, "得到Md5密码：" + md5Password);
       return md5Password;
	}
	/**
	 * 手动登录的构造方法
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
		// 封装请求参数
		paraMap = new TreeMap();

		// 设置参数
		paraMap.put("loginKey", account); // 账号
		paraMap.put("mobileAppVersion", Constants.MYCAR_VERSION); // 版本号
		
	
        paraMap.put("password", getMd5Pawword(password)); // 密码
        
		requestParameter = new RequestParameter(Constants.SERVICE_LOGIN, methodName,null,paraMap,false);
		this.mHandler = handler;
	}
	
	@Override
	public void run() 
	{
		try {
			// 实例化WebServiceManager对象
			WebServiceManager wsm = new WebServiceManager(requestParameter);
			Object object = wsm.execute().object;
			if (object instanceof SoapObject)
			{
				result = (SoapObject)object;
			}
			
			// 得到登录状态
			if (result != null && result.getProperty(0) != null)
			{
				SoapObject so = (SoapObject)result.getProperty(0);
				// 是否成功
				int isSuccess = new Integer(so.getProperty("code") == null 
						? "-1" : so.getProperty("code").toString()).intValue();
				String message = so.getProperty("message") == null ? "" : so.getProperty("message").toString();
				if (isSuccess == UsercenterConstants.RESULT_SUCCESS)
				{
					LoginResult loginResult = new LoginResult();
					
					// cc
					loginResult.cc = so.getProperty("cc") == null 
							? "" : so.getProperty("cc").toString();
					
					// 令牌
					loginResult.token = so.getProperty("token") == null ? "" : so.getProperty("token").toString();
					
					// 服务器时间
					loginResult.serverSystemTime = so.getProperty("serverSystemTime") == null 
							? 0L : new Long(so.getProperty("serverSystemTime").toString()).longValue();
					
					// 错误信息
					loginResult.message = message;

					// 通知UI主线程登录结果
			        mHandler.obtainMessage(UsercenterConstants.LOGIN_RESULT, isSuccess, 0, loginResult)
			        .sendToTarget();

				}
				else
				{
					// 通知UI主线程登录结果
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
		    if (D) Log.d(TAG, "登录异常");
			// 通知UI主线程登录结果
	        mHandler.obtainMessage(UsercenterConstants.LOGIN_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, resources.getString(R.string.usercenter_login_exception))
	        .sendToTarget();
		}	
	}	
}

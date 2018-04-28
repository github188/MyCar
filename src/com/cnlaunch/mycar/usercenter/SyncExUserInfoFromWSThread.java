package com.cnlaunch.mycar.usercenter;

import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.usercenter.database.ExUser;

/**
 * @description 
 * @author 向远茂
 * @date：2012-5-2
 */
/**
 * 构建一个线程对象，用于异步发送请求道服务端同步用户信息
 * @author xiangyuanmao
 *
 */
public class SyncExUserInfoFromWSThread extends Thread
{
    // 调试log信息target
    private static final String TAG = "ExUserInfoActivity";
    private static final boolean D = true;
    Resources resources;
	TreeMap paraMap;
	Handler sHandler;
	Context mCountext;
	public SyncExUserInfoFromWSThread(TreeMap map, Handler handler,Context context)
	{
		this.paraMap = map;
		this.sHandler = handler;
		this.mCountext = context;
		resources = context.getResources();
	}
	
	@Override
	public void run() 
	{
		try {
			RequestParameter rp = new RequestParameter(
			    Constants.SERVICE_USERCENTER,"getUserExt",null,paraMap,true);
			WebServiceManager wsm = new WebServiceManager(mCountext,rp);
			SoapObject object;
			WSBaseResult wSBaseResult = wsm.execute();
			switch (wSBaseResult.responseCode)
			{
			case 0:
				object = (SoapObject)wSBaseResult.object;
				
				// 得到修改用户信息结果对象
				if (wSBaseResult != null && object.getProperty(0) != null)
				{
					SoapObject so = (SoapObject)object.getProperty(0);
					
					// 是否成功
					int isSuccess = new Integer(so.getProperty("code") == null 
							? "-1" : so.getProperty("code").toString()).intValue();
					
					String message = UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess);
					// 通知UI主线程修改用户信息结果
					if (isSuccess == 0)
					{
						ExUser eu = new ExUser();
						eu.setAddress(so.getPropertySafely("address").toString());												
						eu.setCity(so.getPropertySafely("city").toString());												
						eu.setCompanyName(so.getPropertySafely("companyName").toString());												
						eu.setContinent(so.getPropertySafely("continent").toString());												
						eu.setCountry(so.getPropertySafely("country").toString());												
						eu.setFamilyPhone(so.getPropertySafely("familyPhone").toString());												
						eu.setFirstName(so.getPropertySafely("firstName").toString());																							
						eu.setLastName(so.getPropertySafely("lastName").toString());												
						eu.setLatitude(so.getPropertySafely("latitude").toString());												
						eu.setLongitude(so.getPropertySafely("longitude").toString());												
						eu.setMarkAddress(so.getPropertySafely("markAddress").toString());												
						eu.setOfficePhone(so.getPropertySafely("officePhone").toString());												
						eu.setProvince(so.getPropertySafely("province").toString());												
						eu.setUserId(so.getPropertySafely("userId").toString());												
						eu.setZipCode(so.getPropertySafely("zipCode").toString());		
						sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, isSuccess, 0, eu)
						.sendToTarget();
					}
					else
					{
						sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, isSuccess, 0, message)
						.sendToTarget();
					}
				}
				else
				{
					sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1))
					.sendToTarget();
				}
				break;
			
			default:
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (D) Log.d(TAG, "同步用户信息到服务器异常");
			// 通知UI主线程登录结果
			sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1))
			.sendToTarget();
		}	
	}
}



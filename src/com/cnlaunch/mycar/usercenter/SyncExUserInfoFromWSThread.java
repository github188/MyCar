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
 * @author ��Զï
 * @date��2012-5-2
 */
/**
 * ����һ���̶߳��������첽��������������ͬ���û���Ϣ
 * @author xiangyuanmao
 *
 */
public class SyncExUserInfoFromWSThread extends Thread
{
    // ����log��Ϣtarget
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
				
				// �õ��޸��û���Ϣ�������
				if (wSBaseResult != null && object.getProperty(0) != null)
				{
					SoapObject so = (SoapObject)object.getProperty(0);
					
					// �Ƿ�ɹ�
					int isSuccess = new Integer(so.getProperty("code") == null 
							? "-1" : so.getProperty("code").toString()).intValue();
					
					String message = UserCenterCommon.getWebserviceResponseMessage(resources, isSuccess);
					// ֪ͨUI���߳��޸��û���Ϣ���
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
			if (D) Log.d(TAG, "ͬ���û���Ϣ���������쳣");
			// ֪ͨUI���̵߳�¼���
			sHandler.obtainMessage(UsercenterConstants.SYNC_USERINFO_FROM_SERVICE_RESULT, UsercenterConstants.RESULT_EXCEPTION, 0, UserCenterCommon.getWebserviceResponseMessage(resources, -1))
			.sendToTarget();
		}	
	}
}



package com.cnlaunch.mycar.manager.net;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;

/**
 * @author xuzhuowei 车辆管家网络层
 */
public class NetRequest {

	private Context context;
	private TreeMap<String, Object> mParaMap; // 业务参数
	private SoapObject mResult; // 请求接收到的数据
	private RequestParameter mRequestParameter; // 请求打包参数
	private int ServiceId;
	private String mMethodName;
	private List list;
	private static HashMap<String, Integer> serviceMap;
	private static HashMap<String, String> listParaMap;

	public NetRequest() {
	}

	public NetRequest(Context context, TreeMap<String, Object> paraMap,
			String methodName) {
		this.context = context;
		this.mParaMap = paraMap;
		this.mMethodName = methodName;
	}

	public NetRequest(Context context, TreeMap<String, Object> paraMap,
			List list, String methodName) {
		this.context = context;
		this.mParaMap = paraMap;
		this.mMethodName = methodName;
		this.list = list;
	}

	public void setPara(Context context, TreeMap<String, Object> paraMap,
			String methodName) {
		this.context = context;
		this.mParaMap = paraMap;
		this.mMethodName = methodName;
	}

	public boolean doRequest() {
		Log.e("SyncJob", "doRequest");
		if (list == null) {

			mRequestParameter = new RequestParameter(getServiceId(mMethodName),
					mMethodName, null, mParaMap, true);
		} else {
			WebServiceManager.setObjectArrayParameter(mParaMap, list,
					Constants.WEBSERVICE_NAME_SPACE,
					getListParaName(mMethodName));
			mRequestParameter = new RequestParameter(getServiceId(mMethodName),
					mMethodName, null, mParaMap, true);
			WebServiceManager wsm = new WebServiceManager(context,
					mRequestParameter);
		}
		WebServiceManager wsm = new WebServiceManager(context,
				mRequestParameter);

		WSBaseResult wSBaseResult = wsm.execute();
		if (wSBaseResult != null) {
			// 处理业务返回值
			Object object = wSBaseResult.object;
			if (object != null) {
				Log.v("Manager", "NetRequest:" + object.toString());
				mResult = (SoapObject) object;
			}
		}
		return mResult != null;
	}

	private String getListParaName(String methodName) {
		if (listParaMap == null) {
			listParaMap = new HashMap<String, String>();
			listParaMap.put(Constants.SERVICE_MANAGER_METHOD_UPLOAD_ACCOUNT,
					"accounts");
			listParaMap
					.put(Constants.SERVICE_MANAGER_METHOD_UPLOAD_OIL, "oils");
			listParaMap.put(
					Constants.SERVICE_MANAGER_METHOD_UPLOAD_MANAGER_SETTING,
					"managerSettings");
			listParaMap.put(Constants.SERVICE_MANAGER_METHOD_UPLOAD_USER_CAR,
					"userCars");
			listParaMap.put(
					Constants.SERVICE_MANAGER_METHOD_UPLOAD_BILL_CATEGORY,
					"categorys");
		}
		return listParaMap.get(methodName);
	}

	private int getServiceId(String methodName) {
		if (serviceMap == null) {
			serviceMap = new HashMap<String, Integer>();
			serviceMap.put(Constants.SERVICE_MANAGER_METHOD_UPLOAD_ACCOUNT,
					Constants.SERVICE_MANAGER_ACCOUNT);
			serviceMap.put(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_ACCOUNT,
					Constants.SERVICE_MANAGER_ACCOUNT);
			serviceMap.put(Constants.SERVICE_MANAGER_METHOD_UPLOAD_OIL,
					Constants.SERVICE_MANAGER_OIL);
			serviceMap.put(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_OIL,
					Constants.SERVICE_MANAGER_OIL);
			serviceMap.put(
					Constants.SERVICE_MANAGER_METHOD_UPLOAD_MANAGER_SETTING,
					Constants.SERVICE_MANAGER_MANAGER_SETTING);
			serviceMap.put(
					Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_MANAGER_SETTING,
					Constants.SERVICE_MANAGER_MANAGER_SETTING);
			serviceMap.put(Constants.SERVICE_MANAGER_METHOD_UPLOAD_USER_CAR,
					Constants.SERVICE_MANAGER_USER_CAR);
			serviceMap.put(Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_USER_CAR,
					Constants.SERVICE_MANAGER_USER_CAR);
			serviceMap.put(
					Constants.SERVICE_MANAGER_METHOD_UPLOAD_BILL_CATEGORY,
					Constants.SERVICE_MANAGER_BILL_CATEGORY);
			serviceMap.put(
					Constants.SERVICE_MANAGER_METHOD_DOWNLOAD_BILL_CATEGORY,
					Constants.SERVICE_MANAGER_BILL_CATEGORY);
		}
		return serviceMap.get(methodName);
	}

	public SoapObject getResult() {
		return mResult;
	}

}

package com.cnlaunch.mycar.updatecenter.webservice;

import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.updatecenter.dbscar.ApkUpdateInfo;
import com.cnlaunch.mycar.updatecenter.onekeydiag.CarBaseInfo;
import com.cnlaunch.mycar.updatecenter.onekeydiag.CarBrand;
import com.cnlaunch.mycar.updatecenter.onekeydiag.CarBrandList;
import com.cnlaunch.mycar.updatecenter.onekeydiag.OneKeyDiag;
import com.cnlaunch.mycar.updatecenter.onekeydiag.OneKeyDiagResult;
import com.cnlaunch.mycar.updatecenter.onekeydiag.RemainConfigurationCount;
import com.cnlaunch.mycar.updatecenter.onekeydiag.SoftLanguageItem;
import com.cnlaunch.mycar.updatecenter.onekeydiag.SoftwareLanguageList;

public class SoapResponseHandler
{
	private final static String TAG = "SoapResponse";
	public final static int ERROR_SOAP_FAULT = 0 ;
	public final static int ERROR_NULL_OBJ = 1 ;
	public final static int ERROR_NOT_SOAP_OBJ = 2 ;
	public final static int ERROR_UNKNOWN_METHOD = 3 ;
	public final static int ERROR_SOAP_COMMUNICATION = 4;
	
	Context context;
	OnSoapObjectConvertListener listener;
	String methodName;
	Object obj;
	
	
	public SoapResponseHandler(Context ctx,Object obj,String methodName,OnSoapObjectConvertListener l)
	{
		this.context = ctx;
		this.obj = obj;
		this.listener = l;
		this.methodName = methodName;
	}
	
	public void convert(Object obj)
	{
		if (obj == null)
		{
			notifyError(ERROR_NULL_OBJ, "null pointer");
			return;
		}
		
		if (obj instanceof SoapFault || obj instanceof SoapFault12)
		{
			notifyError(ERROR_SOAP_FAULT, obj);
			return;
		}
		
		if (obj instanceof SoapObject)
		{
			SoapObject soRoot = (SoapObject)obj;

			startConvert();
			
			SoapObject soWSResult = (SoapObject) soRoot.getProperty(0);
			
			String msg = "";
			int code = -1;
			Object convertedResult = null;
			
			// 解析出WSResult类的msg 和 code
			if(soWSResult.hasProperty("message"))
			{
				msg = soWSResult.getProperty("message").toString();
			}
			
			if(soWSResult.hasProperty("code"))
			{
				code = Integer.valueOf((soWSResult.getProperty("code").toString()));
			}
			
			if (code == -1)
			{
				// 系统错误
				notifyError(ERROR_SOAP_COMMUNICATION,obj);
				return;
			}
			
			// 根据不同的接口解析不同的对象
			if(methodName.equals(SoapMethod.QUERY_APK_UPDATE_INFO) // 客户端升级信息查询
					|| methodName.equals(SoapMethod.QUERY_BIN_FILE_UPDATE_INFO))// download.bin 升级信息查询
			{
				if(soWSResult.hasProperty("SoftMaxVersion"))
				{
					SoapObject soSoftMaxVersion = (SoapObject) soWSResult.getProperty("SoftMaxVersion");
					
					int detailId = soSoftMaxVersion.hasProperty("versionDetailId") ?  
							Integer.valueOf((soSoftMaxVersion.getProperty("versionDetailId").toString())) : -1;
							
					int flag = soSoftMaxVersion.hasProperty("forceUpgrade") ? 
							Integer.valueOf((soSoftMaxVersion.getProperty("forceUpgrade").toString())) : -1;
							
					String versionNumber = soSoftMaxVersion.hasProperty("versionNo") ? 
							soSoftMaxVersion.getProperty("versionNo").toString() : "";
							
					String softDescrption = soSoftMaxVersion.hasProperty("softDescription") ?
							soSoftMaxVersion.getProperty("softDescription").toString() : "";
					
					ApkUpdateInfo info = new ApkUpdateInfo();
					info.setDetailId(detailId);
					info.setFlag(flag);
					info.setUpdateDescription(softDescrption);
					info.setVersionNumber(versionNumber);
					convertedResult = info;
				}
			}
			else if(methodName.equals(SoapMethod.QUERY_REMAINING_CONFIGURABLE_COUNT))// 剩余可配置次数
			{
				if(soWSResult.hasProperty("remainingCount"))
				{
					int howmany = Integer.valueOf((soWSResult.getProperty("remainingCount").toString()));
					RemainConfigurationCount count = new RemainConfigurationCount(howmany);
					convertedResult = count;
				}
			}
			else if(methodName.equals(SoapMethod.CHECK_SERIAL_NUMBER))// 验证序列号
			{
				convertedResult = null;
			}
			else if(methodName.equals(SoapMethod.REGISTER_PRODUCT))// 注册产品
			{
				convertedResult = null;
			}
			else if (methodName.equals(SoapMethod.QUERY_HISTORICAL_CONFIG_INFO))// 历史配置信息
			{
				convertedResult = handleCarBaseInfo(soWSResult);
			}
			else if (methodName.equals(SoapMethod.QUERY_CAR_BRAND_LIST_BY_VIN))// 查询车系列表
			{
				if(soWSResult.hasProperty("carBrandList"))
				{
					SoapObject soCarBrandList = (SoapObject) soWSResult.getProperty("carBrandList");
					if (soCarBrandList != null && soCarBrandList.getPropertyCount() > 0)
					{
						CarBrandList carBrandList = new  CarBrandList(context);
						
						for (int i = 0; i < soCarBrandList.getPropertyCount(); i++)
						{
							CarBrand carBrand = new CarBrand();
							carBrand.setCarBrandId(((SoapObject) soCarBrandList.getProperty(i)).getProperty("carBrandId").toString());
							carBrand.setCarBrandName(((SoapObject) soCarBrandList.getProperty(i)).getProperty("carBrandName").toString());
							carBrandList.addItem(carBrand);
						}
						convertedResult = carBrandList;
					}
				}
			}
			else if (methodName.equals(SoapMethod.BEGIN_ONE_KEY_CALC) 
							|| methodName.equals(SoapMethod.ONE_KEY_DIAG_CALC))
			{
				convertedResult = handleOnkeyDiagResult(soWSResult);
			}
			else if (methodName.equals(SoapMethod.QUERY_DIAG_SOFT_LANGUAGE_LIST))// 查询诊断软件的语言列表
			{
				if(soWSResult.hasProperty("softLanguageList"))
				{
					SoapObject soLanguageList = (SoapObject) soWSResult.getProperty("softLanguageList");
					if (soLanguageList != null && soLanguageList.getPropertyCount() > 0)
					{
						int count = soLanguageList.getPropertyCount();
						SoftLanguageItem[] languageItems = new SoftLanguageItem[count];
						for (int i = 0; i < count; i++)
						{
							SoftLanguageItem item = new SoftLanguageItem();
							SoapObject soLanguage = (SoapObject) (soLanguageList.getProperty(i));
							if(soLanguage.hasProperty("versionDetailId"))
							{
								item.setVersionDetailId(Integer.valueOf((soLanguage.getProperty("versionDetailId").toString())));								
							}
							if(soLanguage.hasProperty("lanName"))
							{
								item.setLanguageName(((SoapObject) soLanguageList.getProperty(i)).getProperty("lanName").toString());
							}
							languageItems[i] = item;
						}
						convertedResult = new SoftwareLanguageList(context,languageItems);
					}
				}
			}
			else// 不支持的方法
			{
				notifyError(ERROR_UNKNOWN_METHOD,obj);
				return;
			}
			Log.d(TAG, methodName +"() --> soWSResult: " + soWSResult.toString());
			done(code, methodName, msg, convertedResult);
		}
	}
	
	private CarBaseInfo handleCarBaseInfo(SoapObject soWSResult)
	{
		// 获取车系信息列表
		if(soWSResult.hasProperty("carBaseInfo"))
		{
			SoapObject soCarBaseInfo = (SoapObject)soWSResult.getProperty("carBaseInfo");
			CarBaseInfo cbi = new CarBaseInfo();
			cbi.setEmpyt(true);
			if(soCarBaseInfo.hasProperty("diagEntranceId"))// 获取诊断ID
			{
				cbi.setDiagEntranceId(soCarBaseInfo.getProperty("diagEntranceId").toString());
			}
			if(soCarBaseInfo.hasProperty("carBrandName"))// 车系名称
			{
				cbi.setCarBrandName(soCarBaseInfo.getProperty("carBrandName").toString());
			}
			if(soCarBaseInfo.hasProperty("carBrandId"))// 车系ID
			{
				cbi.setCarBrandId(soCarBaseInfo.getProperty("carBrandId").toString());
			}
			if(soCarBaseInfo.hasProperty("carBrandVin"))// 车辆VIN码
			{
				cbi.setCarBrandVin(soCarBaseInfo.getProperty("carBrandVin").toString());
			}
			if(soCarBaseInfo.hasProperty("carProducingAreaId"))// 产地ID
			{
				cbi.setCarProducingAreaId(Integer.valueOf(soCarBaseInfo.getProperty("carProducingAreaId").toString()));
			}
			if(soCarBaseInfo.hasProperty("carModel"))// 车型
			{
				cbi.setCarModel(soCarBaseInfo.getProperty("carModel").toString());
			}
			if(soCarBaseInfo.hasProperty("carProducingYear"))// 年款
			{
				cbi.setCarProducingYear(soCarBaseInfo.getProperty("carProducingYear").toString());
			}
			if(soCarBaseInfo.hasProperty("carEngineType"))// 发动机类型
			{
				cbi.setCarEngineType(soCarBaseInfo.getProperty("carEngineType").toString());
			}
			if(soCarBaseInfo.hasProperty("carDisplacement"))// 排量
			{
				cbi.setCarDisplacement(soCarBaseInfo.getProperty("carDisplacement").toString());
			}
			if(soCarBaseInfo.hasProperty("carGearboxType"))// 波箱
			{
				cbi.setCarGearboxType(soCarBaseInfo.getProperty("carGearboxType").toString());
			}
			return cbi;
		}
		return null;
	}
	
	private OneKeyDiagResult handleOnkeyDiagResult(SoapObject so)
	{
		// 开始一键诊断
		OneKeyDiag oneKeyDiag  = null;
		OneKeyDiagResult oneKeyDiagResult = new OneKeyDiagResult(); 
		if(so.hasProperty("oneKeyDiag"))// 解析一键诊断结果
		{
			oneKeyDiag = new OneKeyDiag(context);
			SoapObject soOneKeyDiag = (SoapObject)so.getProperty("oneKeyDiag");
			
			if(soOneKeyDiag.hasProperty("conditionId")) // 条件码
			{
				String conditionID = soOneKeyDiag.getProperty("conditionId").toString();
				oneKeyDiag.setConditionId(conditionID);
			}
			if(soOneKeyDiag.hasProperty("value")) // 返回值列表
			{
				oneKeyDiag.setValue(soOneKeyDiag.getProperty("value").toString());
			}
			if(soOneKeyDiag.hasProperty("endFlag"))// 一键计算的结束标志
			{
				int endFlag = Integer.valueOf(soOneKeyDiag.getProperty("endFlag").toString());
				oneKeyDiag.setEndFlag(endFlag);
			}
			oneKeyDiagResult.setOneKeyDiag(oneKeyDiag);
		}
		
		if(so.hasProperty("carBaseInfo"))// 车型基本信息
		{
			SoapObject soCarBaseInfo = (SoapObject)so.getProperty("carBaseInfo");// ???
			CarBaseInfo cbi = new CarBaseInfo();
			oneKeyDiagResult.setCarBaseInfo(cbi);
		}
		return oneKeyDiagResult;
	}
	
	private void startConvert()
	{
		if (listener != null)
		{
			listener.onStart();
		}
	}
	
	private void notifyError(int code,Object err)
	{
		if (listener != null)
		{
			listener.onError(code, err);
		}
	}
	
	private void done(int code,String method,String msg,Object r)
	{
		SoapResponse response = new SoapResponse(code, methodName, msg, r);
		convertDone(response);
	}
	
	private void convertDone(SoapResponse response)
	{
		if (listener!=null)
		{
			listener.onConvertResult(response);
		}
	}
}

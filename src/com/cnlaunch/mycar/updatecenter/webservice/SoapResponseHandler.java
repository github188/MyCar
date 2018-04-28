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
			
			// ������WSResult���msg �� code
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
				// ϵͳ����
				notifyError(ERROR_SOAP_COMMUNICATION,obj);
				return;
			}
			
			// ���ݲ�ͬ�Ľӿڽ�����ͬ�Ķ���
			if(methodName.equals(SoapMethod.QUERY_APK_UPDATE_INFO) // �ͻ���������Ϣ��ѯ
					|| methodName.equals(SoapMethod.QUERY_BIN_FILE_UPDATE_INFO))// download.bin ������Ϣ��ѯ
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
			else if(methodName.equals(SoapMethod.QUERY_REMAINING_CONFIGURABLE_COUNT))// ʣ������ô���
			{
				if(soWSResult.hasProperty("remainingCount"))
				{
					int howmany = Integer.valueOf((soWSResult.getProperty("remainingCount").toString()));
					RemainConfigurationCount count = new RemainConfigurationCount(howmany);
					convertedResult = count;
				}
			}
			else if(methodName.equals(SoapMethod.CHECK_SERIAL_NUMBER))// ��֤���к�
			{
				convertedResult = null;
			}
			else if(methodName.equals(SoapMethod.REGISTER_PRODUCT))// ע���Ʒ
			{
				convertedResult = null;
			}
			else if (methodName.equals(SoapMethod.QUERY_HISTORICAL_CONFIG_INFO))// ��ʷ������Ϣ
			{
				convertedResult = handleCarBaseInfo(soWSResult);
			}
			else if (methodName.equals(SoapMethod.QUERY_CAR_BRAND_LIST_BY_VIN))// ��ѯ��ϵ�б�
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
			else if (methodName.equals(SoapMethod.QUERY_DIAG_SOFT_LANGUAGE_LIST))// ��ѯ�������������б�
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
			else// ��֧�ֵķ���
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
		// ��ȡ��ϵ��Ϣ�б�
		if(soWSResult.hasProperty("carBaseInfo"))
		{
			SoapObject soCarBaseInfo = (SoapObject)soWSResult.getProperty("carBaseInfo");
			CarBaseInfo cbi = new CarBaseInfo();
			cbi.setEmpyt(true);
			if(soCarBaseInfo.hasProperty("diagEntranceId"))// ��ȡ���ID
			{
				cbi.setDiagEntranceId(soCarBaseInfo.getProperty("diagEntranceId").toString());
			}
			if(soCarBaseInfo.hasProperty("carBrandName"))// ��ϵ����
			{
				cbi.setCarBrandName(soCarBaseInfo.getProperty("carBrandName").toString());
			}
			if(soCarBaseInfo.hasProperty("carBrandId"))// ��ϵID
			{
				cbi.setCarBrandId(soCarBaseInfo.getProperty("carBrandId").toString());
			}
			if(soCarBaseInfo.hasProperty("carBrandVin"))// ����VIN��
			{
				cbi.setCarBrandVin(soCarBaseInfo.getProperty("carBrandVin").toString());
			}
			if(soCarBaseInfo.hasProperty("carProducingAreaId"))// ����ID
			{
				cbi.setCarProducingAreaId(Integer.valueOf(soCarBaseInfo.getProperty("carProducingAreaId").toString()));
			}
			if(soCarBaseInfo.hasProperty("carModel"))// ����
			{
				cbi.setCarModel(soCarBaseInfo.getProperty("carModel").toString());
			}
			if(soCarBaseInfo.hasProperty("carProducingYear"))// ���
			{
				cbi.setCarProducingYear(soCarBaseInfo.getProperty("carProducingYear").toString());
			}
			if(soCarBaseInfo.hasProperty("carEngineType"))// ����������
			{
				cbi.setCarEngineType(soCarBaseInfo.getProperty("carEngineType").toString());
			}
			if(soCarBaseInfo.hasProperty("carDisplacement"))// ����
			{
				cbi.setCarDisplacement(soCarBaseInfo.getProperty("carDisplacement").toString());
			}
			if(soCarBaseInfo.hasProperty("carGearboxType"))// ����
			{
				cbi.setCarGearboxType(soCarBaseInfo.getProperty("carGearboxType").toString());
			}
			return cbi;
		}
		return null;
	}
	
	private OneKeyDiagResult handleOnkeyDiagResult(SoapObject so)
	{
		// ��ʼһ�����
		OneKeyDiag oneKeyDiag  = null;
		OneKeyDiagResult oneKeyDiagResult = new OneKeyDiagResult(); 
		if(so.hasProperty("oneKeyDiag"))// ����һ����Ͻ��
		{
			oneKeyDiag = new OneKeyDiag(context);
			SoapObject soOneKeyDiag = (SoapObject)so.getProperty("oneKeyDiag");
			
			if(soOneKeyDiag.hasProperty("conditionId")) // ������
			{
				String conditionID = soOneKeyDiag.getProperty("conditionId").toString();
				oneKeyDiag.setConditionId(conditionID);
			}
			if(soOneKeyDiag.hasProperty("value")) // ����ֵ�б�
			{
				oneKeyDiag.setValue(soOneKeyDiag.getProperty("value").toString());
			}
			if(soOneKeyDiag.hasProperty("endFlag"))// һ������Ľ�����־
			{
				int endFlag = Integer.valueOf(soOneKeyDiag.getProperty("endFlag").toString());
				oneKeyDiag.setEndFlag(endFlag);
			}
			oneKeyDiagResult.setOneKeyDiag(oneKeyDiag);
		}
		
		if(so.hasProperty("carBaseInfo"))// ���ͻ�����Ϣ
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

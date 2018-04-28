package com.cnlaunch.mycar.crecorder;

import java.util.Date;
import java.util.TreeMap;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;

/**@author luxingsong
 * CRecorder��������������
 * �漰������Ҫ�����ǣ�
 * 1.����431�ļ� 
 * 2.����431�ļ�
 * 3.
 * */
public class CRecorderWebServiceOperation
{
	private static final String WS_SERV_METHOD_UPLOAD = "uploadDiagDataFile";
	private static final String WS_SERV_METHOD_DOWNLOAD = "downloadHisDiagData";
	private static final String WS_SERV_METHOD_HISTDATA = "downloadHisDiagDataList";
	private static final String WS_SERV_NS = "http://www.x431.com";
	private static final String FILE_STORAGE_PATH="/sdcard/mycar/crecorder/";
	/**
	 * ������ʷ�������
	 * @author luxingsong
	 * @param dataId Ҫ��ѯ�������ļ�����
	 * @return void
	 * */
	public static void downloadHisDiagData(String dataId)
	{
		TreeMap paramList = new TreeMap();
		paramList.put("id",dataId);
		RequestParameter rp = new RequestParameter
				(Constants.SERVICE_BUSINESS,/*ҵ������*/ 
				 WS_SERV_METHOD_DOWNLOAD, /*�ӿڷ�����*/
				 null, 
				 paramList, 
				 false);
		WebServiceManager wsm = new WebServiceManager(rp);
		WSBaseResult ret = wsm.execute();
		if(ret!=null && ret.responseCode!=-1)
		{ 
			//�����صĽ��������Ľ������431�ļ�
		}
	}
	/**������ʷ��������б�
	 * @author luxingsong
	 * @param pageNo ҳ��
	 * @param pageSize ÿҳ��¼��
	 * @param diagDataCondition ��ѯ����
	 * @return void
	 * */
	public static void downloadHisDiagDataList(int pageNo, int pageSize, DiagDataCondition diagDataCondition)
	{
		
	}
	/**@author luxingsong
	 * @param  diagDataFile ��������ļ�
	 * ������������ļ�
	 * ע��:��ڲ�����������Ҫ��ת��ΪBase64 String����
	 * ��Ҫ�ϴ����ļ�תΪbyte����Ȼ���ٽ�byte��תΪ�ַ���
	 * */
	public static void uploadDiagDataFile(DiagDataFile diagDataFile)
	{
		TreeMap paramList = new TreeMap();
		paramList.put("file",diagDataFile);
		RequestParameter rp = new RequestParameter
				(Constants.SERVICE_BUSINESS,/*ҵ������*/ 
				 WS_SERV_METHOD_UPLOAD, /*�ӿڷ�����*/
				 null, 
				 paramList, 
				 false);
		WebServiceManager wsm = new WebServiceManager(rp);
		WSBaseResult ret = wsm.execute();
	}
}
/**
 * @author luxingsong
 * ����������صĲ�ѯ����
 * */
class DiagDataCondition
{
	String fileName;
	Date  createTimeFrom;
	Date createTimeto;
}
/**@author luxingsong
 * �ӷ��������ص���������ļ�
 * */
class DiagDataFileResult
{
	DiagDataFile dataFile;
}
/**@author luxingsong
 * �ӷ��������ص���������ļ�������
 * */
class DiagDataFile
{
	String fileName;
	String dataDesc;
	String dataHandler;//actual data here
}
package com.cnlaunch.mycar.crecorder;

import java.util.Date;
import java.util.TreeMap;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WSBaseResult;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;

/**@author luxingsong
 * CRecorder的网络服务操作类
 * 涉及到的主要操作是：
 * 1.上载431文件 
 * 2.下载431文件
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
	 * 下载历史诊断数据
	 * @author luxingsong
	 * @param dataId 要查询的数据文件名称
	 * @return void
	 * */
	public static void downloadHisDiagData(String dataId)
	{
		TreeMap paramList = new TreeMap();
		paramList.put("id",dataId);
		RequestParameter rp = new RequestParameter
				(Constants.SERVICE_BUSINESS,/*业务类型*/ 
				 WS_SERV_METHOD_DOWNLOAD, /*接口方法名*/
				 null, 
				 paramList, 
				 false);
		WebServiceManager wsm = new WebServiceManager(rp);
		WSBaseResult ret = wsm.execute();
		if(ret!=null && ret.responseCode!=-1)
		{ 
			//处理返回的结果，这里的结果就是431文件
		}
	}
	/**下载历史诊断数据列表
	 * @author luxingsong
	 * @param pageNo 页码
	 * @param pageSize 每页记录数
	 * @param diagDataCondition 查询条件
	 * @return void
	 * */
	public static void downloadHisDiagDataList(int pageNo, int pageSize, DiagDataCondition diagDataCondition)
	{
		
	}
	/**@author luxingsong
	 * @param  diagDataFile 诊断数据文件
	 * 上载诊断数据文件
	 * 注意:入口参数的数据域要被转换为Base64 String类型
	 * 把要上传的文件转为byte流，然后再将byte流转为字符串
	 * */
	public static void uploadDiagDataFile(DiagDataFile diagDataFile)
	{
		TreeMap paramList = new TreeMap();
		paramList.put("file",diagDataFile);
		RequestParameter rp = new RequestParameter
				(Constants.SERVICE_BUSINESS,/*业务类型*/ 
				 WS_SERV_METHOD_UPLOAD, /*接口方法名*/
				 null, 
				 paramList, 
				 false);
		WebServiceManager wsm = new WebServiceManager(rp);
		WSBaseResult ret = wsm.execute();
	}
}
/**
 * @author luxingsong
 * 诊断数据下载的查询条件
 * */
class DiagDataCondition
{
	String fileName;
	Date  createTimeFrom;
	Date createTimeto;
}
/**@author luxingsong
 * 从服务器返回的诊断数据文件
 * */
class DiagDataFileResult
{
	DiagDataFile dataFile;
}
/**@author luxingsong
 * 从服务器返回的诊断数据文件描述符
 * */
class DiagDataFile
{
	String fileName;
	String dataDesc;
	String dataHandler;//actual data here
}
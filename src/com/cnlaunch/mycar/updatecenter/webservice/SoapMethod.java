package com.cnlaunch.mycar.updatecenter.webservice;
/**
 * soap 接口方法
 * */
public class SoapMethod
{
	//手机客户端的升级接口
	public final static String QUERY_APK_UPDATE_INFO ="getPhoneSoftMaxVersion";/*查询版本信息*/
	public final static String QUERY_BIN_FILE_UPDATE_INFO ="getBinFileMaxVersion";/*查询download.bin版本信息*/
	
	//诊断软件的升级接口
	public final static String QUERY_DIAGSOFT_VERSION="queryDiagSofts";/*查询诊断软件的更新列表*/
	public final static String QUERY_DIAGSOFT_VERSION_DETAIL="queryDiagSoftVesionDetails";/*查询具体的诊断软件*/
	
	//诊断产品注册接口  checkDBSCarProduct(String serialNo，String chipID)
	public final static String CHECK_SERIAL_NUMBER = "checkDBSCarProduct";
	public final static String REGISTER_PRODUCT = "registerDBSCarProduct";
	
	// 动态配置接口
	public final static String QUERY_REMAINING_CONFIGURABLE_COUNT = "getRemainingCount";// 剩余的可配置次数
	public final static String QUERY_HISTORICAL_CONFIG_INFO = "getConfigedAllInfo";// 车辆历史配置信息
	public final static String QUERY_CAR_BRAND_LIST_BY_VIN  = "getCarBrandListByVINAndLan";// 根据VIN 获取车系列表
	public final static String BEGIN_ONE_KEY_CALC  = "beginOneKeyDiagCalc";
	public final static String ONE_KEY_DIAG_CALC  = "OneKeyDiagCalc";
	public final static String QUERY_DIAG_SOFT_LANGUAGE_LIST  = "getLatestDiagSoftLan";// 获取最新版本软件支持的语言列表
	public final static String QUERY_LATEST_DIAG_SOFTS = "queryLatestDiagSofts";//获取最新版本诊断软件
	
}

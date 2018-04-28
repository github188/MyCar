package com.cnlaunch.mycar.updatecenter;

/**
 * 升级中心的相关常量
 * @author luxingsong
 * */
public interface UpdateCenterConstants
{
	public final static int PLATFORM_ANDROID = 0;// android平台标志
	public final static int UPDATE_METHOD_ONLINE = 0; // 在线升级
	public final static int UPDATE_METHOD_LOCAL = 1;  // 本地升级
	// for release
	public final static String WebServiceURL = "http://mycar.dbscar.com";
	// for debug
//	public final static String WebServiceOfficalIP = "http://tmycar.cnlaunch.com";
	
	/*升级服务器URL地址*/
	public final static String UPDATE_PUBLIC_SOFTWARE_URL = WebServiceURL + "/services/publicSoftService";// Mycar公共软件查询
	public final static String UPDATE_PUBLIC_SOFTWARE_DOWNLOAD_URL = WebServiceURL + "/mobile/softCenter/downloadPhoneSoftWs.action";//Mycar软件下载
	public final static String UPDATE_DIAG_SOFTWARE_URL = WebServiceURL + "/services/diagSoftService";// 诊断软件信息
	public final static String UPDATE_DIAG_SOFTWARE_DATA_URL = WebServiceURL + "/mobile/softCenter/downloadDiagSoftWs.action";// 诊断软件数据下载
	public final static String UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL = WebServiceURL + "/mobile/softCenter/downloadLatestDiagSoft.action";// 一键诊断方式的诊断软件数据下载
	public final static String UPDATE_PRODUCT_REGISTER_URL = WebServiceURL + "/services/productService";// 产品[设备] 注册的URL
	public final static String UPDATE_USER_CENTER_URL = WebServiceURL + "/services/userservice"; 
	public final static String UPDATE_DIAG_ONKEY_CONFIG_SERVICE = WebServiceURL + "/services/oneKeyDiagService"; 

	/*升级配置数据 Key String*/
	public final static String AUTO_UPDATE = "auto_update";
	public final static String AUTO_COMMIT_LOG ="auto_commit_log";
	public final static String SHOW_VERSION_INFO ="show_version_info";
	public final static String DEVICE_ACTIVATE_GUIDE_NOT_SHOW_AGAIN ="device_activate_guide";
	public final static String SHARE_PREF_UPDATE_SETTINGS="sharePrefUpdateSettings";
	
	/*升级的应用程序保存位置*/
	public final static String SDCARD 			   = "/mnt/sdcard";
	public final static String CNLAUNCH_DIR        = SDCARD + "/cnlaunch";
	public final static String DBSCAR_DIR          =  CNLAUNCH_DIR + "/dbsCar";
	public final static String VEHICLES_DIR 	   = DBSCAR_DIR + "/vehicles";
	public final static String APK_DIR             = DBSCAR_DIR + "/apk";
	public final static String TEMP_DIR            = DBSCAR_DIR + "/temp";
	public final static String UPDATE_LOG_DIR      = DBSCAR_DIR + "/update_log";
	public final static String DOWNLOAD_BIN_DIR      = DBSCAR_DIR + "/downloadbin";
	
	
	public final static String WEB_SERV_UPDATE_CENTER_NS = "http://www.x431.com";/*命名空间*/
	
	//手机客户端的升级接口
	public final static String WEB_SERV_METHOD_QUERY_MOBI_CLIENT_VERSION ="getPhoneSoftMaxVersion";/*查询版本信息*/
	public final static String WEB_SERV_METHOD_DOWNLOAD_MOBI_CLIENT = "downloadPhoneSoft";/*下载移动客户端*/
	
	//诊断软件的升级接口
	public final static String WEB_SERV_METHOD_QUERY_DIAGSOFT_VERSION="queryDiagSofts";/*查询诊断软件的更新列表*/
	public final static String WEB_SERV_METHOD_QUERY_DIAGSOFT_VERSION_DETAIL="queryDiagSoftVesionDetails";/*查询具体的诊断软件*/
	public final static String WEB_SERV_METHOD_QUERY_DIAGSOFT_DOWNLOAD="downloadDiagSoft";
	
	//诊断产品注册接口  checkDBSCarProduct(String serialNo，String chipID)
	public final static String WEB_SERV_METHOD_CHECK_SERIAL_NUMBER = "checkDBSCarProduct";
	public final static String WEB_SERV_METHOD_APPEND_USER_INFO = "addUserExtInfo";
	public final static String WEB_SERV_METHOD_REGISTER_PRODUCT = "registerDBSCarProduct";
	
	/*----------end of Web Service --------------------------*/
	/** 设备的工作模式 **/
	public final static byte BOOT_MODE = 0x00;
	public final static byte DOWNLOAD_BIN_MODE = 0x01;
}

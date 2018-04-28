package com.cnlaunch.mycar.updatecenter;

/**
 * �������ĵ���س���
 * @author luxingsong
 * */
public interface UpdateCenterConstants
{
	public final static int PLATFORM_ANDROID = 0;// androidƽ̨��־
	public final static int UPDATE_METHOD_ONLINE = 0; // ��������
	public final static int UPDATE_METHOD_LOCAL = 1;  // ��������
	// for release
	public final static String WebServiceURL = "http://mycar.dbscar.com";
	// for debug
//	public final static String WebServiceOfficalIP = "http://tmycar.cnlaunch.com";
	
	/*����������URL��ַ*/
	public final static String UPDATE_PUBLIC_SOFTWARE_URL = WebServiceURL + "/services/publicSoftService";// Mycar���������ѯ
	public final static String UPDATE_PUBLIC_SOFTWARE_DOWNLOAD_URL = WebServiceURL + "/mobile/softCenter/downloadPhoneSoftWs.action";//Mycar�������
	public final static String UPDATE_DIAG_SOFTWARE_URL = WebServiceURL + "/services/diagSoftService";// ��������Ϣ
	public final static String UPDATE_DIAG_SOFTWARE_DATA_URL = WebServiceURL + "/mobile/softCenter/downloadDiagSoftWs.action";// ��������������
	public final static String UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL = WebServiceURL + "/mobile/softCenter/downloadLatestDiagSoft.action";// һ����Ϸ�ʽ����������������
	public final static String UPDATE_PRODUCT_REGISTER_URL = WebServiceURL + "/services/productService";// ��Ʒ[�豸] ע���URL
	public final static String UPDATE_USER_CENTER_URL = WebServiceURL + "/services/userservice"; 
	public final static String UPDATE_DIAG_ONKEY_CONFIG_SERVICE = WebServiceURL + "/services/oneKeyDiagService"; 

	/*������������ Key String*/
	public final static String AUTO_UPDATE = "auto_update";
	public final static String AUTO_COMMIT_LOG ="auto_commit_log";
	public final static String SHOW_VERSION_INFO ="show_version_info";
	public final static String DEVICE_ACTIVATE_GUIDE_NOT_SHOW_AGAIN ="device_activate_guide";
	public final static String SHARE_PREF_UPDATE_SETTINGS="sharePrefUpdateSettings";
	
	/*������Ӧ�ó��򱣴�λ��*/
	public final static String SDCARD 			   = "/mnt/sdcard";
	public final static String CNLAUNCH_DIR        = SDCARD + "/cnlaunch";
	public final static String DBSCAR_DIR          =  CNLAUNCH_DIR + "/dbsCar";
	public final static String VEHICLES_DIR 	   = DBSCAR_DIR + "/vehicles";
	public final static String APK_DIR             = DBSCAR_DIR + "/apk";
	public final static String TEMP_DIR            = DBSCAR_DIR + "/temp";
	public final static String UPDATE_LOG_DIR      = DBSCAR_DIR + "/update_log";
	public final static String DOWNLOAD_BIN_DIR      = DBSCAR_DIR + "/downloadbin";
	
	
	public final static String WEB_SERV_UPDATE_CENTER_NS = "http://www.x431.com";/*�����ռ�*/
	
	//�ֻ��ͻ��˵������ӿ�
	public final static String WEB_SERV_METHOD_QUERY_MOBI_CLIENT_VERSION ="getPhoneSoftMaxVersion";/*��ѯ�汾��Ϣ*/
	public final static String WEB_SERV_METHOD_DOWNLOAD_MOBI_CLIENT = "downloadPhoneSoft";/*�����ƶ��ͻ���*/
	
	//�������������ӿ�
	public final static String WEB_SERV_METHOD_QUERY_DIAGSOFT_VERSION="queryDiagSofts";/*��ѯ�������ĸ����б�*/
	public final static String WEB_SERV_METHOD_QUERY_DIAGSOFT_VERSION_DETAIL="queryDiagSoftVesionDetails";/*��ѯ�����������*/
	public final static String WEB_SERV_METHOD_QUERY_DIAGSOFT_DOWNLOAD="downloadDiagSoft";
	
	//��ϲ�Ʒע��ӿ�  checkDBSCarProduct(String serialNo��String chipID)
	public final static String WEB_SERV_METHOD_CHECK_SERIAL_NUMBER = "checkDBSCarProduct";
	public final static String WEB_SERV_METHOD_APPEND_USER_INFO = "addUserExtInfo";
	public final static String WEB_SERV_METHOD_REGISTER_PRODUCT = "registerDBSCarProduct";
	
	/*----------end of Web Service --------------------------*/
	/** �豸�Ĺ���ģʽ **/
	public final static byte BOOT_MODE = 0x00;
	public final static byte DOWNLOAD_BIN_MODE = 0x01;
}

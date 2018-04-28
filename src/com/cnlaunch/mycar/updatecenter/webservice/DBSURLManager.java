package com.cnlaunch.mycar.updatecenter.webservice;

public final class DBSURLManager
{
	public static boolean isRelease = true;
	
	/*升级服务器URL地址*/
	private final static String UPDATE_PUBLIC_SOFTWARE_URL = "/services/publicSoftService";// Mycar公共软件查询
	private final static String PUBLIC_SOFTWARE_DOWNLOAD_URL = "/mobile/softCenter/downloadPhoneSoftWs.action";//Mycar软件下载
	private final static String UPDATE_DIAG_SOFTWARE_URL = "/services/diagSoftService";// 诊断软件信息
	private final static String UPDATE_DIAG_SOFTWARE_DATA_URL = "/mobile/softCenter/downloadDiagSoftWs.action";// 诊断软件数据下载
	private final static String UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL = "/mobile/softCenter/downloadLatestDiagSoft.action";// 一键诊断方式的诊断软件数据下载
	private final static String PRODUCT_REGISTER_URL = "/services/productService";// 产品[设备] 注册的URL
	private final static String DIAG_ONKEY_CONFIG_SERVICE = "/services/oneKeyDiagService"; // 设备动态配置
	
	private static final String getBaseUrl()
	{
		if (isRelease)
		{
			return "http://mycar.dbscar.com";// 发行
		}
		return "http://tmycar.cnlaunch.com";// 内测
	}
	
	/**
	 * 获取公共软件的下载URL 
	 * @return
	 */
	public static String getPublicSoftURL()
	{
		return getBaseUrl() + UPDATE_PUBLIC_SOFTWARE_URL;
	}
	
	/**
	 * 获取客户端下载的URL
	 * @return
	 */
	public static String getApkDownloadURL()
	{
		return getBaseUrl() + PUBLIC_SOFTWARE_DOWNLOAD_URL;
	}
	
	/**
	 * 产品注册服务的URL
	 * @return
	 */
	public static String getProductRegistrationURL()
	{
		return getBaseUrl() + PRODUCT_REGISTER_URL;
	}
	
	/**
	 * 诊断软件配置URL
	 * @return
	 */
	public static String getDiagConfigURL()
	{
		return getBaseUrl() + DIAG_ONKEY_CONFIG_SERVICE;
	}
}

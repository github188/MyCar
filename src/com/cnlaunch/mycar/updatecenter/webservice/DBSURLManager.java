package com.cnlaunch.mycar.updatecenter.webservice;

public final class DBSURLManager
{
	public static boolean isRelease = true;
	
	/*����������URL��ַ*/
	private final static String UPDATE_PUBLIC_SOFTWARE_URL = "/services/publicSoftService";// Mycar���������ѯ
	private final static String PUBLIC_SOFTWARE_DOWNLOAD_URL = "/mobile/softCenter/downloadPhoneSoftWs.action";//Mycar�������
	private final static String UPDATE_DIAG_SOFTWARE_URL = "/services/diagSoftService";// ��������Ϣ
	private final static String UPDATE_DIAG_SOFTWARE_DATA_URL = "/mobile/softCenter/downloadDiagSoftWs.action";// ��������������
	private final static String UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL = "/mobile/softCenter/downloadLatestDiagSoft.action";// һ����Ϸ�ʽ����������������
	private final static String PRODUCT_REGISTER_URL = "/services/productService";// ��Ʒ[�豸] ע���URL
	private final static String DIAG_ONKEY_CONFIG_SERVICE = "/services/oneKeyDiagService"; // �豸��̬����
	
	private static final String getBaseUrl()
	{
		if (isRelease)
		{
			return "http://mycar.dbscar.com";// ����
		}
		return "http://tmycar.cnlaunch.com";// �ڲ�
	}
	
	/**
	 * ��ȡ�������������URL 
	 * @return
	 */
	public static String getPublicSoftURL()
	{
		return getBaseUrl() + UPDATE_PUBLIC_SOFTWARE_URL;
	}
	
	/**
	 * ��ȡ�ͻ������ص�URL
	 * @return
	 */
	public static String getApkDownloadURL()
	{
		return getBaseUrl() + PUBLIC_SOFTWARE_DOWNLOAD_URL;
	}
	
	/**
	 * ��Ʒע������URL
	 * @return
	 */
	public static String getProductRegistrationURL()
	{
		return getBaseUrl() + PRODUCT_REGISTER_URL;
	}
	
	/**
	 * ����������URL
	 * @return
	 */
	public static String getDiagConfigURL()
	{
		return getBaseUrl() + DIAG_ONKEY_CONFIG_SERVICE;
	}
}

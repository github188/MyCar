package com.cnlaunch.mycar.updatecenter.webservice;
/**
 * soap �ӿڷ���
 * */
public class SoapMethod
{
	//�ֻ��ͻ��˵������ӿ�
	public final static String QUERY_APK_UPDATE_INFO ="getPhoneSoftMaxVersion";/*��ѯ�汾��Ϣ*/
	public final static String QUERY_BIN_FILE_UPDATE_INFO ="getBinFileMaxVersion";/*��ѯdownload.bin�汾��Ϣ*/
	
	//�������������ӿ�
	public final static String QUERY_DIAGSOFT_VERSION="queryDiagSofts";/*��ѯ�������ĸ����б�*/
	public final static String QUERY_DIAGSOFT_VERSION_DETAIL="queryDiagSoftVesionDetails";/*��ѯ�����������*/
	
	//��ϲ�Ʒע��ӿ�  checkDBSCarProduct(String serialNo��String chipID)
	public final static String CHECK_SERIAL_NUMBER = "checkDBSCarProduct";
	public final static String REGISTER_PRODUCT = "registerDBSCarProduct";
	
	// ��̬���ýӿ�
	public final static String QUERY_REMAINING_CONFIGURABLE_COUNT = "getRemainingCount";// ʣ��Ŀ����ô���
	public final static String QUERY_HISTORICAL_CONFIG_INFO = "getConfigedAllInfo";// ������ʷ������Ϣ
	public final static String QUERY_CAR_BRAND_LIST_BY_VIN  = "getCarBrandListByVINAndLan";// ����VIN ��ȡ��ϵ�б�
	public final static String BEGIN_ONE_KEY_CALC  = "beginOneKeyDiagCalc";
	public final static String ONE_KEY_DIAG_CALC  = "OneKeyDiagCalc";
	public final static String QUERY_DIAG_SOFT_LANGUAGE_LIST  = "getLatestDiagSoftLan";// ��ȡ���°汾���֧�ֵ������б�
	public final static String QUERY_LATEST_DIAG_SOFTS = "queryLatestDiagSofts";//��ȡ���°汾������
	
}

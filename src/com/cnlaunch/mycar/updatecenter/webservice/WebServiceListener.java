package com.cnlaunch.mycar.updatecenter.webservice;

/**
 * ������������ӿ�
 * */
public interface WebServiceListener
{
	/**
	 * ��ʼ��������
	 * @param request
	 */
	public void onStartWebServiceRequest(Object service,SoapRequest request);
	/**
	 * ��ɲ���
	 * */
	public void onWebServiceSuccess(Object service,SoapResponse response);
	/**
	 * ��������
	 */
	public void onWebServiceErrors(Object service,int code,SoapRequest request);
}

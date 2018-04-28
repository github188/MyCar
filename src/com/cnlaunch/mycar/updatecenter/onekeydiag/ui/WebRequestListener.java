package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;
/**
 * @author luxingsong
 * ������������ӿ�
 * */
public interface WebRequestListener
{
	/**
	 * ��ʼ����
	 * */
	public void onStartRequest();
	/**
	 * ��Ӧ���
	 * */
	public void onRequestResult(Object result);
	/**
	 * ���
	 * @param result ���
	 */
	public void onFinished(Object result);
	/**
	 * ��Ӧ����
	 * @param reason �����ԭ��
	 */
	public void onResponseError(Object reason);
}

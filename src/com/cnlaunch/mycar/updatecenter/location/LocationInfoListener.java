package com.cnlaunch.mycar.updatecenter.location;
/**
 * ��γ��λ����Ϣ�����ӿ�
 * */
public interface LocationInfoListener
{
	public final static int ERROR_TIMEOUT = 0;// ��ʱ
	
	public void onStart(Object service);
	public void onLocationResult(Object result);
	public void onError(Object service,int code,Object detail);
}

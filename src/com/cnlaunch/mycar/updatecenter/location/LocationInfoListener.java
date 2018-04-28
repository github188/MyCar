package com.cnlaunch.mycar.updatecenter.location;
/**
 * 经纬度位置信息监听接口
 * */
public interface LocationInfoListener
{
	public final static int ERROR_TIMEOUT = 0;// 超时
	
	public void onStart(Object service);
	public void onLocationResult(Object result);
	public void onError(Object service,int code,Object detail);
}

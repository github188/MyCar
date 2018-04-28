package com.cnlaunch.mycar.updatecenter.webservice;

/**
 * 网络请求监听接口
 * */
public interface WebServiceListener
{
	/**
	 * 开始网络请求
	 * @param request
	 */
	public void onStartWebServiceRequest(Object service,SoapRequest request);
	/**
	 * 完成操作
	 * */
	public void onWebServiceSuccess(Object service,SoapResponse response);
	/**
	 * 发生错误
	 */
	public void onWebServiceErrors(Object service,int code,SoapRequest request);
}

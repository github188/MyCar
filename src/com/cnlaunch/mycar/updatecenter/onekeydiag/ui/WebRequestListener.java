package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;
/**
 * @author luxingsong
 * 网络请求监听接口
 * */
public interface WebRequestListener
{
	/**
	 * 开始请求
	 * */
	public void onStartRequest();
	/**
	 * 响应结果
	 * */
	public void onRequestResult(Object result);
	/**
	 * 完成
	 * @param result 结果
	 */
	public void onFinished(Object result);
	/**
	 * 响应出错
	 * @param reason 出错的原因
	 */
	public void onResponseError(Object reason);
}

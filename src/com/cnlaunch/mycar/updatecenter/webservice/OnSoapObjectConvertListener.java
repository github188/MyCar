package com.cnlaunch.mycar.updatecenter.webservice;

public interface OnSoapObjectConvertListener
{
	public void onStart();
	public void onConvertResult(SoapResponse result);
	public void onError(int code,Object err);
}

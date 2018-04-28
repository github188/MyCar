package com.cnlaunch.mycar.updatecenter.connection;

public interface ConnectionListener
{
	public void onConnectionStart(String addr,Object extra);
	public void onConnecting(String addr,String name);
	public void onConnectionEstablished(String addr,String name);
	public void onConnectionLost(String addr,String name);
	public void onResponse(byte[] data,Object extra);
	public void onConnectionCancel();
	public void onTimeout();
}

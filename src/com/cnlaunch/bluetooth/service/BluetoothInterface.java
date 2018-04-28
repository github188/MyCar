package com.cnlaunch.bluetooth.service;

public interface BluetoothInterface {
	//连接中断通知
	public void BlueConnectLost(String name,String mac);
	//连接成功通知
	public void BlueConnected(String name,String mac);
	//收到数据通知<命令字> + <数据>
	public void GetDataFromService(byte[] databuf,int datalen);
	//超时通知
	public void GetDataTimeout();
	//关闭蓝牙连接界面通知
	public void BlueConnectClose();

}

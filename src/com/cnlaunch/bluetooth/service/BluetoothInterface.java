package com.cnlaunch.bluetooth.service;

public interface BluetoothInterface {
	//�����ж�֪ͨ
	public void BlueConnectLost(String name,String mac);
	//���ӳɹ�֪ͨ
	public void BlueConnected(String name,String mac);
	//�յ�����֪ͨ<������> + <����>
	public void GetDataFromService(byte[] databuf,int datalen);
	//��ʱ֪ͨ
	public void GetDataTimeout();
	//�ر��������ӽ���֪ͨ
	public void BlueConnectClose();

}

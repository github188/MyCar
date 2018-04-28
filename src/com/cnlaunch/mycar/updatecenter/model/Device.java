package com.cnlaunch.mycar.updatecenter.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @description 
 * @author ��Զï
 * @date��2012-4-16
 */
@DatabaseTable
public class Device implements Serializable{
	
	private static final long serialVersionUID = 4353589653332712827L;
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String deviceName; // �豸����
	@DatabaseField
	private String status; // �豸״̬  1 �Ѿ�ע��   
	@DatabaseField
	private String serialPasswd; // ���к�����
	@DatabaseField
	private String configData; // ������Ϣ
	@DatabaseField
	private String serialNum; // �豸���к�
	@DatabaseField
	private String mac; // �豸mac��ַ��������������
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getSerialPasswd() {
		return serialPasswd;
	}
	public void setSerialPasswd(String serialPasswd) {
		this.serialPasswd = serialPasswd;
	}
	public String getConfigData() {
		return configData;
	}
	public void setConfigData(String configData) {
		this.configData = configData;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

package com.cnlaunch.mycar.updatecenter.model;

import java.io.Serializable;

public class SerialInfo implements Serializable
{
	private static final long	serialVersionUID	= -5926679244517441927L;
	String serialNumber;
	String chipId;
	
	public SerialInfo(String serialNumber, String chId)
	{
		this.serialNumber = serialNumber;
		this.chipId = chId;
	}
	
	public String getSerialNumber()
	{
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}
	public String getChipId()
	{
		return chipId;
	}
	public void setChipId(String chipId)
	{
		this.chipId = chipId;
	}
	
}

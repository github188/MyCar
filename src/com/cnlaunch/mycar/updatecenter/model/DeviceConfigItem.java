package com.cnlaunch.mycar.updatecenter.model;

import java.io.Serializable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * ÿ���豸���õ�������   
 * */
@DatabaseTable
public class DeviceConfigItem implements Serializable
{
	private static final long	serialVersionUID	= -7852695919080290773L;
	
	@DatabaseField
	private String vehicleType;// ����
	@DatabaseField
	private String version;// �汾
	@DatabaseField
	private String language;// ����
	@DatabaseField
	private String absolutePath;// ���ݵľ���·��
	
	@DatabaseField(generatedId = true)
	private int daoId;
	
	public String getVehicleType()
	{
		return vehicleType;
	}
	public void setVehiecleType(String vehiecleType)
	{
		this.vehicleType = vehiecleType;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getLanguage()
	{
		return language;
	}
	public void setLanguage(String language)
	{
		this.language = language;
	}
	public String getAbsolutePath()
	{
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath)
	{
		this.absolutePath = absolutePath;
	}
}

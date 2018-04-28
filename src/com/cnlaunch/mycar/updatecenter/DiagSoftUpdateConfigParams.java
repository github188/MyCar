package com.cnlaunch.mycar.updatecenter;

import java.io.Serializable;

/**
 * 升级时候查询配置文件的参数
 * 1.车型名称
 * 2.版本
 * 3.语言类型
 * */
public class DiagSoftUpdateConfigParams implements Serializable
{
	private static final long	serialVersionUID	= 0x12345687851L;
	String vehiecle = "";
	String version  = "";
	String language = "";
	String serialNumber = "";
	String fileAbsolutePath = "";
	int upadteType;/* 升级类型 : [ 1:download.bin升级; 2：断点续传 ; 3: 配置升级 ; 4: 全新升级]*/
	
	public int getUpadteType()
	{
		return upadteType;
	}

	public void setUpadteType(int aUpadteType)
	{
		upadteType = aUpadteType;
	}

	public String getFileAbsolutePath()
	{
		return fileAbsolutePath;
	}

	public void setFileAbsolutePath(String fileAbsolutePath)
	{
		this.fileAbsolutePath = fileAbsolutePath;
	}

	@Override
    public String toString()
    {
        return "DiagSoftUpdateConfigParams [vehiecle=" + vehiecle + ", version=" + version + ", language=" + language + ", serialNumber=" + serialNumber + ", fileAbsolutePath=" + fileAbsolutePath
            + ", upadteType=" + upadteType + "]";
    }

    public String getVehiecle()
	{
		return vehiecle;
	}
	
	public void setVehiecle(String vehiecle)
	{
		this.vehiecle = vehiecle;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
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
	
}

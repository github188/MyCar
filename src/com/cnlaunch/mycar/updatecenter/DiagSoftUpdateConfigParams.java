package com.cnlaunch.mycar.updatecenter;

import java.io.Serializable;

/**
 * ����ʱ���ѯ�����ļ��Ĳ���
 * 1.��������
 * 2.�汾
 * 3.��������
 * */
public class DiagSoftUpdateConfigParams implements Serializable
{
	private static final long	serialVersionUID	= 0x12345687851L;
	String vehiecle = "";
	String version  = "";
	String language = "";
	String serialNumber = "";
	String fileAbsolutePath = "";
	int upadteType;/* �������� : [ 1:download.bin����; 2���ϵ����� ; 3: �������� ; 4: ȫ������]*/
	
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

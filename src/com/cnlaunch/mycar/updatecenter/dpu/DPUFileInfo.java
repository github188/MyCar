package com.cnlaunch.mycar.updatecenter.dpu;

/**
 * DPU 车型诊断文件信息
 * */
public class DPUFileInfo
{
	String fileName;
	String md5sum;
	
	public DPUFileInfo(String fileName, String md5sum)
	{
		this.fileName = fileName;
		this.md5sum = md5sum;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String getMd5sum()
	{
		return md5sum;
	}
	
	public void setMd5sum(String md5sum)
	{
		this.md5sum = md5sum;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((md5sum == null) ? 0 : md5sum.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DPUFileInfo other = (DPUFileInfo) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (md5sum == null) {
			if (other.md5sum != null)
				return false;
		} else if (!md5sum.equals(other.md5sum))
			return false;
		return true;
	}
}

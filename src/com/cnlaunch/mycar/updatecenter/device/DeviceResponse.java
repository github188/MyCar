package com.cnlaunch.mycar.updatecenter.device;
/**
 * …Ë±∏œÏ”¶
 * @author luxingsong
 *
 */
public class DeviceResponse
{
	String id;
	int code;

	Object result;
	
	public DeviceResponse(){}
	
	public DeviceResponse(String id, Object result)
	{
		this.id = id;
		this.result = result;
	}

	public int getCode()
	{
		return code;
	}
	
	public void setCode(int code)
	{
		this.code = code;
	}
	
	public String getId()
	{
		return id;
	}

	public DeviceResponse setId(String id)
	{
		this.id = id;
		return this;
	}

	public Object getResult()
	{
		return result;
	}

	public DeviceResponse setResult(Object result)
	{
		this.result = result;
		return this;
	}
}

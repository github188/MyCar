package com.cnlaunch.mycar.updatecenter.webservice;

import java.util.Map;
import java.util.TreeMap;

import com.cnlaunch.mycar.common.config.Constants;

public class SoapRequest
{
	int type;
	String method;
	String action;
	String url;
	boolean signFlag;
	public final int DEFAULT_TIMEOUT = 50;
	int timeout = DEFAULT_TIMEOUT;
	
	Map<String, Object> paramList = new TreeMap<String, Object>();
	
	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public SoapRequest()
	{
	}
	
	/**
	 * 
	 * @param url
	 * @param meth
	 * @param flag
	 * @param timeout
	 */
	public SoapRequest(String url,String meth,boolean flag,int timeout)
	{
		this.url = url;
		this.method = meth;
		this.signFlag = flag;
		this.timeout = timeout;
		this.type = Constants.SERVICE_BUSINESS;// 业务类型
	}
	
	public SoapRequest setParam(String tag,Object param)
	{
		paramList.put(tag, param);
		return this;
	}
	
	public SoapRequest removeParam(String tagName)
	{
		if(paramList.containsKey(tagName))
		{
			paramList.remove(tagName);
		}
		return this;
	}

	public int getType()
	{
		return type;
	}

	public SoapRequest setType(int type)
	{
		this.type = type;
		return this;
	}

	public String getMethod()
	{
		return method;
	}

	public SoapRequest setMethod(String method)
	{
		this.method = method;
		return this;
	}

	public boolean getSignFlag()
	{
		return signFlag;
	}

	public SoapRequest setSignFlag(boolean signFlag)
	{
		this.signFlag = signFlag;
		return this;
	}

	public Map<String, Object> getParamList()
	{
		return paramList;
	}

	public String getUrl()
	{
		return url;
	}

	public SoapRequest setUrl(String url)
	{
		this.url = url;
		return this;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public SoapRequest setTimeout(int timeout)
	{
		this.timeout = timeout;
		return this;
	}

	public SoapRequest setParamList(TreeMap<String, Object> paramList)
	{
		this.paramList = paramList;
		return this;
	}

	@Override
	public String toString()
	{
		return "SoapRequest [method=" + method + 
				",\n url=" + url + 
				",\n signFlag="+ signFlag + 
				",\n timeout=" + timeout  + 
				",\n paramList="+ paramList + "]\n";
	}
}

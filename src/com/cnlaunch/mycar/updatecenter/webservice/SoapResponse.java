package com.cnlaunch.mycar.updatecenter.webservice;

public class SoapResponse
{
	int code; //  服务器响应码
	String msg;// 服务器的响应消息
	String method;// 响应的接口方法名
	Object result;// 响应的结果
	
	public SoapResponse(){}
	
	/**
	 * 构造函数	
	 * @param c 返回码
	 * @param m 返回的提示信息
	 * @param r 返回的结果对象
	 */
	public SoapResponse(int c,String method,String msg,Object r)
	{
		this.code = c;
		this.msg  = msg;
		this.method = method;
		this.result = r;
	}
	
	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public int getCode()
	{
		return code;
	}
	
	public void setCode(int code)
	{
		this.code = code;
	}
	
	public String getMsg()
	{
		return msg;
	}
	
	public void setMsg(String msg)
	{
		this.msg = msg;
	}
	
	public Object getResult()
	{
		return result;
	}
	
	public void setResult(Object result)
	{
		this.result = result;
	}

	@Override
	public String toString()
	{
		return "SoapResponse [code=" + code + ", msg=" + msg + ", method="
				+ method + ", result=" + result + "]";
	}
	
}

package com.cnlaunch.mycar.updatecenter.webservice;

public class SoapResponse
{
	int code; //  ��������Ӧ��
	String msg;// ����������Ӧ��Ϣ
	String method;// ��Ӧ�Ľӿڷ�����
	Object result;// ��Ӧ�Ľ��
	
	public SoapResponse(){}
	
	/**
	 * ���캯��	
	 * @param c ������
	 * @param m ���ص���ʾ��Ϣ
	 * @param r ���صĽ������
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

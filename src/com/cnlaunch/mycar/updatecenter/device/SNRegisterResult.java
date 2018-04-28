package com.cnlaunch.mycar.updatecenter.device;

public class SNRegisterResult
{
	public final static int RESULT_FAILED           =  654;   /*验证产品失败*/
	public final static int RESULT_SERVER_EXCEPTION =  500;   /*服务端异常  */
	public final static int RESULT_REGISTER_OK      =  0  ;  /* 成功       */
	
	int code = -1;
	String serialNum;
	
	public SNRegisterResult(String sn,int c)
	{
		this.code = c;
		this.serialNum = sn;
	}
	/**获取序列号**/
	public String getSerialNum()
	{
		return serialNum;
	}
	/**注册结果**/
	public int getResult()
	{
		return code;
	}
}

package com.cnlaunch.mycar.updatecenter.device;

public class SNRegisterResult
{
	public final static int RESULT_FAILED           =  654;   /*��֤��Ʒʧ��*/
	public final static int RESULT_SERVER_EXCEPTION =  500;   /*������쳣  */
	public final static int RESULT_REGISTER_OK      =  0  ;  /* �ɹ�       */
	
	int code = -1;
	String serialNum;
	
	public SNRegisterResult(String sn,int c)
	{
		this.code = c;
		this.serialNum = sn;
	}
	/**��ȡ���к�**/
	public String getSerialNum()
	{
		return serialNum;
	}
	/**ע����**/
	public int getResult()
	{
		return code;
	}
}

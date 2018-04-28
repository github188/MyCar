package com.cnlaunch.mycar.updatecenter.device;
/**
 *  产品序列号状态
 *  
1. code=650      未售出
2. code=651      已注册
3. code=652      已作废
4. code=654      验证产品失败
5. code=657      错误的芯片ID
6. code=658      序列号不存在
7. code=659      已被他人注册
9. code=0        验证成功，可以注册
 * */
public class SerialNumber
{
	/**
	 * 该序列号未注册，可以被任何用户注册
	 * **/
	public final static int STATE_NOT_REGISTERED = 0;
	
	/**
	 * 当前用户已经注册该序列号
	 * **/
	public final static int STATE_REGISTERED = 651;
	/**
	 * 已作废
	 */
	public final static int STATE_EXPIRED = 652;
	/**
	 * 序列号未验证就直接注册会返回这个错误
	 * */
	public final static int STATE_ERROR_REGISTER_UNCHECKED_SN = 654;
	/**
	 * 错误的芯片ID
	 */
	public final static int STATE_REGISTERED__INVALID_CHIP_ID = 657;
	/**
	 * 数据库不存在这个序列号
	 * */
	public final static int STATE_ERROR_INVALID_SN = 658;
	/**
	 * 该序列号已经被他人注册，当前用户不能对其做任何操作
	 * **/
	public final static int STATE_REGISTERED_BY_OTHERS = 659;
	/**
	 * 未定义的状态
	 * */
	public final static int STATE_UNDEFINE = -1;
	
	String sn;
	String chipId;
	
	int code;
	
	public SerialNumber(String sn,String id,int c)
	{
		this.sn = sn;
		this.chipId = id;
		this.code = c;
	}
	
	public int getState()
	{
		return code;
	}
	
	public String getSerialNumber()
	{
		return sn;
	}
	
	public String getChipId()
	{
		return chipId;
	}
}

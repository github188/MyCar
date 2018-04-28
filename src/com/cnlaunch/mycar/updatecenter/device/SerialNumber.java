package com.cnlaunch.mycar.updatecenter.device;
/**
 *  ��Ʒ���к�״̬
 *  
1. code=650      δ�۳�
2. code=651      ��ע��
3. code=652      ������
4. code=654      ��֤��Ʒʧ��
5. code=657      �����оƬID
6. code=658      ���кŲ�����
7. code=659      �ѱ�����ע��
9. code=0        ��֤�ɹ�������ע��
 * */
public class SerialNumber
{
	/**
	 * �����к�δע�ᣬ���Ա��κ��û�ע��
	 * **/
	public final static int STATE_NOT_REGISTERED = 0;
	
	/**
	 * ��ǰ�û��Ѿ�ע������к�
	 * **/
	public final static int STATE_REGISTERED = 651;
	/**
	 * ������
	 */
	public final static int STATE_EXPIRED = 652;
	/**
	 * ���к�δ��֤��ֱ��ע��᷵���������
	 * */
	public final static int STATE_ERROR_REGISTER_UNCHECKED_SN = 654;
	/**
	 * �����оƬID
	 */
	public final static int STATE_REGISTERED__INVALID_CHIP_ID = 657;
	/**
	 * ���ݿⲻ����������к�
	 * */
	public final static int STATE_ERROR_INVALID_SN = 658;
	/**
	 * �����к��Ѿ�������ע�ᣬ��ǰ�û����ܶ������κβ���
	 * **/
	public final static int STATE_REGISTERED_BY_OTHERS = 659;
	/**
	 * δ�����״̬
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

package com.cnlaunch.mycar.usercenter.model;

import com.cnlaunch.mycar.common.webservice.WSResult;

/**
 * �ӷ����ͬ���û���Ϣ���ֻ��ķ��ض���
 * @author xiangyuanmao
 *
 */
public class WSUser extends WSResult{

	public String cc	;//	Cc����
	public String nickname;//		�ǳ�
	public String userName; // �û���
	public String isBindEmail; //�Ƿ������  0 ���� 1 ����
	public String isBindMobile; // �Ƿ���ֻ� 0 ���� 1 ����
	public String mobile		;//�ֻ���
	public String email		;//����

}

package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;
/**
 * ѡ����ӿ�
 * */
public interface ChoiceGroup
{
	public String getId();// ���id 
	public String getType();// ������
	public int getFlag();// ��ı�־λ
	public int getCount(); // ѡ�����ѡ�����
	public String[] getItems(); // ��ȡ���е�ѡ��
	public int getCurrentIndex(); // ��ǰѡ����ѡ��
	public String getItemAt(int index);// ��ȡindex����λ�õ�ѡ��
	public void setIndex(int index);// ѡ��index����ѡ��
}

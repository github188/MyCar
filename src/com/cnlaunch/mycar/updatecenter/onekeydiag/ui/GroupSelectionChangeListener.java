package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;
/**
 * @author luxingsong
 * ѡ��仯�����ӿ�
 * */
public interface GroupSelectionChangeListener
{
	/**
	 * ��ѡ��ı�ʱ���������ӿ�
	 * @param group  ��ǰ�ı��ѡ����
	 * @param groupPosition ѡ���������б��������λ��
	 * @param oldIndex ��ѡ�������
	 * @param newIndex ��ѡ���������
	 */
	public void onSelectionChanged(ChoiceGroup group,int groupPosition,int oldIndex,int newIndex);
}

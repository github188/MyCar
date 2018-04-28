package com.cnlaunch.mycar.updatecenter.device;


/**
 * �豸��������
 * */
public interface DeviceUpdateListener
{
	/**
	 * ��ʼ����
	 */
	public void onDeviceUpdateStart();
	/**
	 * ֪ͨ������������
	 * @param action ��������
	 * @param detail ��������
	 */
	public void onDeviceUpdateMessages(int action,String detail);
	/**
	 * ֪ͨ������������
	 * @param action ��������
	 * @param progress ������Ϣ
	 */
	public void onUpdateProgress(int action , ProgressInfo progress);
	/**
	 * �������
	 * @param message
	 */
	public void onDeviceUpdateFinish(String message);
	/**
	 * ֪ͨ�����쳣���ߴ���
	 * @param error ������
	 * @param detail ����ԭ��
	 */
	public void onDeviceUpdateException(int error,Object detail);
}

package com.cnlaunch.mycar.updatecenter.task;
/**
 * ����ļ���
 * @author luxingsong
 */
public interface TaskListener
{
	public void onStart(Object param);
	public void onFinish(Object result);
	public void onError(int code,Object reason);
}

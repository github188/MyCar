package com.cnlaunch.mycar.updatecenter.dpu;
/**
 * DPU ������Ӧ�Ľӿ�
 * */
public interface DpuResponseListener
{	
	public void onResponseCorrect(Object result);
	public void onResponseError(int type,Object detail);
}

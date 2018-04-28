package com.cnlaunch.mycar.updatecenter.dpu;
/**
 * DPU 接收响应的接口
 * */
public interface DpuResponseListener
{	
	public void onResponseCorrect(Object result);
	public void onResponseError(int type,Object detail);
}

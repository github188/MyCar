package com.cnlaunch.mycar.updatecenter.dpu;

public interface DPUResponseHandlerChain
{
	public DPUResponseHandlerChain addNext(DPUResponseHandlerChain nextOne);
	public boolean handleResponse(byte[] dataPackage);
}

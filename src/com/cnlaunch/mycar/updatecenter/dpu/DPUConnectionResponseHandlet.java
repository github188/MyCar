package com.cnlaunch.mycar.updatecenter.dpu;

import android.util.Log;

import com.cnlaunch.mycar.diagnose.util.OrderUtils;

/**
 * DPU 连接请求  2502响应处理
 * */
public class DPUConnectionResponseHandlet implements DPUResponseHandlerChain
{
	private final static String TAG = "DPUConnectionResponseHandlet";
	final String expectId = "6502";
	DpuResponseListener listener;
	DPUResponseHandlerChain next;
	
	byte[] dataPacket;
	
	public DPUConnectionResponseHandlet(byte[] dataPacke,DpuResponseListener listener)
	{
		this.dataPacket = dataPacke;
		this.listener = listener;
	}

	@Override
	public DPUResponseHandlerChain addNext(DPUResponseHandlerChain nextOne)
	{
		return next = nextOne;
	}

	@Override
	public boolean handleResponse(byte[] data)
	{
		if(dataPacket!=null)
		{
			byte[] cmdWordAndCmdParam = OrderUtils.filterOutCommandAndCommandParameters(dataPacket);
			
			byte[] cmdWord = OrderUtils.filterOutCommand(data);
			
			String cmd_subcmd_param_str = OrderUtils.bytesToHexStringNoBar(cmdWordAndCmdParam);
			String cmd_subcmd_str = OrderUtils.bytesToHexStringNoBar(cmdWord);
			if(cmd_subcmd_str.equals(expectId))
			{
				if (cmd_subcmd_param_str.equals("650202"))//
				{
					byte[] checkSum = OrderUtils.filterOut4BytesCheckSum(data);
					if(listener!=null)listener.onResponseCorrect(checkSum);
				}
				else if(cmd_subcmd_param_str.equals("650201"))
				{
					
				}
				else if(cmd_subcmd_param_str.equals("650200"))
				{
					if(listener!=null)listener.onResponseCorrect(null);
				}
				else
				{
					if(listener!=null)listener.onResponseError(2502, "无法连接到DPU");
				}
				
			}
			else
			{
				if(next!=null)
				{
					next.handleResponse(data);
				}
			}
			
		}
		return false;
	}
	
}

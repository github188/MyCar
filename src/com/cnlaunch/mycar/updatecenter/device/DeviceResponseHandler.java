package com.cnlaunch.mycar.updatecenter.device;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.diagnose.constant.DPU_Short;
import com.cnlaunch.mycar.diagnose.constant.DPU_String;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;

/**
 * 设备响应处理
 * */
public class DeviceResponseHandler
{
	private static final String  TAG = "DeviceResponseHandler";
	private boolean D = true;
	
	public interface Listener
	{
		public void onDeviceResponse(DeviceResponse response);
		public void onDeviceError(String request);
	}
	
	Context context;
	ArrayList<Listener> listeners = new ArrayList<DeviceResponseHandler.Listener>();
	
	public DeviceResponseHandler(Context c)
	{
		this.context = c;
	}
	
	synchronized public void addListener(Listener listener)
	{
		if(listener!=null)
			this.listeners.add(listener);
	}
	
	synchronized public void removeListener(Listener listener)
	{
		if(listener!=null)
			this.listeners.remove(listener);
	}
	   synchronized public void removeAllListener()
	    {
	        if(this.listeners!=null)
	            this.listeners.removeAll(listeners); 
	    }
	synchronized private void notifyDeviceResponse(DeviceResponse response)
	{
		if (listeners != null && listeners.size() > 0)
		{
			int len = listeners.size();
			for(int i=0; i< len ;i++)
			{
			    Listener l  = listeners.get(i);
			    l.onDeviceResponse(response);
			}
		}
		
	}
	
	synchronized private void notifyDeviceError(String request)
	{
		int len = listeners.size();
		for(int i=0; i< len ;i++)
		{
		    if (i < listeners.size())
		    {
		        Listener l  = listeners.get(i);
		        l.onDeviceError(request);
		    }
		}
	}
	
	public String getString(int resId)
	{
		if(context != null)
		{
			return context.getString(resId);
		}
		return "";
	}
	
	public void handleResponse(byte[] data)
	{
		byte[] cmdWordAndCmdParam = OrderUtils
				.filterOutCommandAndCommandParameters(data);
		byte[] cmdWord = OrderUtils.filterOutCommand(data);
		String cmd_subcmd_param = OrderUtils
				.bytesToHexStringNoBar(cmdWordAndCmdParam);
		String cmd_subcmd = OrderUtils.bytesToHexStringNoBar(cmdWord);
		DeviceResponse response = new DeviceResponse();
		
		// 把命令字ID 转换为字符串进行比较
		if (cmd_subcmd.equals("2301")||cmd_subcmd.equals("2304"))
		{
			if (D)Log.e(TAG, "与DPU的通信发生异常");
			notifyDeviceError(cmd_subcmd);
		}
		else if (cmd_subcmd.equals("6406"))
		{
		    byte[] param = OrderUtils.filterOutCmdParameters(data);
            if(param.length == 1)
            {
                if(D)Log.e(TAG,"返回的文件断点信息格式错误!");
                notifyDeviceError(cmd_subcmd);
            }
            else
            {
                notifyDeviceResponse(response.setId("2406")
                    .setResult(DPU_String.getBreakPointInfo(data)));
            }
		}
		else if (cmd_subcmd.equals("6104")) 
		{
			if (D)Log.e(TAG, "6104 查询版本信息OK");
			byte[] params = OrderUtils.filterOutCmdParameters(data);
			ArrayList<String> info = DPU_String
					.toStringArray(params);
			
			if(listeners!=null && info!=null && info.size() > 0)
			{
				notifyDeviceResponse(response.setId("2104").setResult(info));
				return;
			}
			
		}
		else if(cmd_subcmd.equals("6103"))// 6103 获取序列号
		{
			byte[] param = OrderUtils.filterOutCmdParameters(data);
			if(param.length == 1)
			{
				if(D)Log.e(TAG,"返回的序列号信息格式错误!");
				notifyDeviceError(cmd_subcmd);
			}
			else
			{
				ArrayList<String> info = DPU_String.toStringArray(param);
				if(info!=null && info.size() >= 2)
				{
					String serialNumber = info.get(1);// 0:设备序列号对应的密码  1:设备序列号   2:PCB版本
					String chipID = info.get(0);
					if(D)Log.e(TAG,"------>序列号:"+ serialNumber);
					if(D)Log.e(TAG,"------>序列号密码:"+ chipID);
					notifyDeviceResponse(response.setId("2103")
							.setResult(new SerialInfo(serialNumber, chipID)));
				}
			}
		}
		/*** 获取软件版本信息   boot  download.bin **/
		else if(cmd_subcmd.equals("6105"))
		{
			byte[] params = OrderUtils.filterOutCmdParameters(data);
			
			if(params!=null && params.length >= 3)// 至少是空的String
			{
				ArrayList<String> info = DPU_String.toStringArray(params);
				
				if(listeners!=null && info!=null && info.size() > 0)
				{
					notifyDeviceResponse(response.setId("2105").setResult(info));
					return;
				}
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/** 蓝牙重命名 **/
		else if (cmd_subcmd.equals("6108")) 
		{
			if (cmd_subcmd_param.equalsIgnoreCase("610800"))
			{
				byte[] params = OrderUtils.filterOutCmdParameters(data);
				if(listeners!=null && params!=null)
				{
					String deviceName = DPU_String.asString(params);
					notifyDeviceResponse(response.setId("2108").setResult(deviceName));
					return;
				}
			}
			else 
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		/** 跳转到 Download.bin 入口  **/
		else if(cmd_subcmd.equals("6111"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if(status!=null && status[0] == 0x00)
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2111").setResult(status));
					return;
				}
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**写入 DPUSYS.INI 配置文件 **/
		else if(cmd_subcmd.equals("6112"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			Log.e(TAG," 写入设备INI配置文件的回应: "+cmd_subcmd_param);
			if(status!=null && status[0] == 0x00)
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2112").setResult(status));
					return;
				}
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**读取 DPUSYS.INI 配置文件 **/
		else if(cmd_subcmd.equals("6113"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			Log.e(TAG," 写入设备INI配置文件的回应: "+cmd_subcmd_param);
			if (status!=null)
			{
				int fileLen = DPU_Short.bytesToDPUShort(status);
				Log.d(TAG,"INI File len :"+ fileLen + " 数据区长度"+(status.length - 2)+" 字节");
				System.out.println(" INI 数据内容:"+OrderUtils.bytesToHexString(status));
				notifyDeviceResponse(response.setId("2113").setResult(new String(status)));
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**查询模式   00:boot模式  01: download.bin 模式**/
		else if(cmd_subcmd.equals("6114"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status != null && status.length >= 1)// boot mode
			{
				int runningMode = -1;
				if (status[0] == (byte)0x00)
				{
					runningMode = DeviceMode.MODE_BOOT;// boot 模式
				}
				if (status[0] == (byte)0x01)
				{
					runningMode = DeviceMode.MODE_DOWNLOA_BIN;// download.bin 模式
				}
				else
				{
					Log.e(TAG,"设备的工做模式是 ???");
				}
				notifyDeviceResponse(response.setId("2114").setResult(runningMode));
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**设置密码**/
		else if(cmd_subcmd.equals("6110"))
		{
			if(cmd_subcmd_param.equals("611000"))// set passwd ok
			{
				notifyDeviceResponse(response.setId("2110").setResult(0x00));
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**设置为默认密码**/
		else if(cmd_subcmd.equals("610f"))
		{
			if(cmd_subcmd_param.equals("610f00"))// boot mode
			{
				notifyDeviceResponse(response.setId("210f").setResult(0x00));
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		else if (cmd_subcmd.equals("6502")) 
		{
			if (cmd_subcmd_param.equals("650202"))// 请求校验字节 OK
			{
				byte[] checkSum = OrderUtils.filterOut4BytesCheckSum(data);
				if (checkSum != null)
				{
					notifyDeviceResponse(response.setId("2502").setResult(checkSum));
				}
			}
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6503"))// DPU连接成功
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (cmd_subcmd_param.equals("650302"))// 校验OK 查询DPU软件硬件信息
			{
				if (D)Log.d(TAG, "--->第3步  接收校验OK");
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2503").setResult(status));
				}
			}
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6504"))
		{
	          byte[] status = OrderUtils.filterOutCmdParameters(data);
	            if (status[0] == 0x00) 
	            {
	                if(listeners!=null)
	                {
	                    notifyDeviceResponse(response.setId("6504").setResult(status));
	                }
	            } 
	            else
	            {
	                if(listeners!=null)
	                {
	                    notifyDeviceError(cmd_subcmd);
	                }
	            }
		}
	      else if (cmd_subcmd.equals("6505"))
	        {
	           byte[] status = OrderUtils.filterOutCmdParameters(data);
	            if (status[0] == 0x00) 
	            {
	                if(listeners!=null)
	                {
	                    notifyDeviceResponse(response.setId("6505").setResult(status));
	                }
	            } 
	            else
	            {
	                if(listeners!=null)
	                {
	                    notifyDeviceError(cmd_subcmd);
	                }
	            }
	        }
		else if (cmd_subcmd.equals("6401"))// 准备升级状态判断
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			
			if (status[0] == 0x00)
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2401").setResult(status));
				}
			} else if(status[0] == 0x07)
			{
		         if(listeners!=null)
	                {
	                    notifyDeviceResponse(response.setId("2401").setResult(status));
	                }
			}
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6402"))// 文件名和文件长度回应
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00) 
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2402").setResult(status));
				}
			} 
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6403"))// 升级文件发送过程
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00) 
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2403").setResult(status));
				}
			} 
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6404"))// 发送文件MD5校验
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00) 
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2404").setResult(status));
				}
			} 
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6405"))// 升级完成
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00) 
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2405").setResult(status));
				}
			} 
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6407"))// 切换到boot升级模式
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00) 
			{
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2407").setResult(status));
				}
			} 
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		} 
		else if (cmd_subcmd.equals("6408"))// md5文件信息提取
		{
			// 进行MD5数据校验 对比
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00)
			{
				// byte[0] byte[1] 文件数量
				byte[] temp = OrderUtils.filterOutCmdParameters(data);
				// 用来记录校验错误的文件信息
				int fileNum = temp[0] << 8 | temp[1];
				if (D)Log.d(TAG, "++++++需要校验MD5的文件总数:" + fileNum);
				int offset = 2;
				HashMap<String,String> md5info = new HashMap<String, String>();
				for (int i = 0; i < fileNum; i++) 
				{
					// DPU_String 与 java 的String类型是有区别的！ 前者多了两个长度字节
					int fileNameLen = (temp[offset] << 8 | temp[offset + 1]);
					// 截取 DPU_String
					byte[] filename_bytes = new byte[fileNameLen - 1];
					for (int j = 0; j < fileNameLen - 1; j++) 
					{
						filename_bytes[j] = temp[offset + 2 + j];
					}
					// 截取 md5字符串
					byte[] md5bytes = new byte[32];
					for (int j = 0; j < 32; j++)
					{
						md5bytes[j] = temp[offset + 2 + fileNameLen + j];
					}
					String fileName = new String(filename_bytes);// 文件名
					String md5OnDpu = new String(md5bytes);// 文件的md5
					md5info.put(fileName, md5OnDpu);
					offset += (2 + fileNameLen + 32);// 更新偏移量位置,计算的依据请参考DPU
				}
				if(listeners!=null)
				{
					notifyDeviceResponse(response.setId("2408").setResult(md5info));
				}
			} 
			else
			{
				if(listeners!=null)
				{
					notifyDeviceError(cmd_subcmd);
				}
			}
		}
	}
}

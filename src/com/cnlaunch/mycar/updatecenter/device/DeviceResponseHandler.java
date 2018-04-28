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
 * �豸��Ӧ����
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
		
		// ��������ID ת��Ϊ�ַ������бȽ�
		if (cmd_subcmd.equals("2301")||cmd_subcmd.equals("2304"))
		{
			if (D)Log.e(TAG, "��DPU��ͨ�ŷ����쳣");
			notifyDeviceError(cmd_subcmd);
		}
		else if (cmd_subcmd.equals("6406"))
		{
		    byte[] param = OrderUtils.filterOutCmdParameters(data);
            if(param.length == 1)
            {
                if(D)Log.e(TAG,"���ص��ļ��ϵ���Ϣ��ʽ����!");
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
			if (D)Log.e(TAG, "6104 ��ѯ�汾��ϢOK");
			byte[] params = OrderUtils.filterOutCmdParameters(data);
			ArrayList<String> info = DPU_String
					.toStringArray(params);
			
			if(listeners!=null && info!=null && info.size() > 0)
			{
				notifyDeviceResponse(response.setId("2104").setResult(info));
				return;
			}
			
		}
		else if(cmd_subcmd.equals("6103"))// 6103 ��ȡ���к�
		{
			byte[] param = OrderUtils.filterOutCmdParameters(data);
			if(param.length == 1)
			{
				if(D)Log.e(TAG,"���ص����к���Ϣ��ʽ����!");
				notifyDeviceError(cmd_subcmd);
			}
			else
			{
				ArrayList<String> info = DPU_String.toStringArray(param);
				if(info!=null && info.size() >= 2)
				{
					String serialNumber = info.get(1);// 0:�豸���кŶ�Ӧ������  1:�豸���к�   2:PCB�汾
					String chipID = info.get(0);
					if(D)Log.e(TAG,"------>���к�:"+ serialNumber);
					if(D)Log.e(TAG,"------>���к�����:"+ chipID);
					notifyDeviceResponse(response.setId("2103")
							.setResult(new SerialInfo(serialNumber, chipID)));
				}
			}
		}
		/*** ��ȡ����汾��Ϣ   boot  download.bin **/
		else if(cmd_subcmd.equals("6105"))
		{
			byte[] params = OrderUtils.filterOutCmdParameters(data);
			
			if(params!=null && params.length >= 3)// �����ǿյ�String
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
		/** ���������� **/
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
		/** ��ת�� Download.bin ���  **/
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
		/**д�� DPUSYS.INI �����ļ� **/
		else if(cmd_subcmd.equals("6112"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			Log.e(TAG," д���豸INI�����ļ��Ļ�Ӧ: "+cmd_subcmd_param);
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
		/**��ȡ DPUSYS.INI �����ļ� **/
		else if(cmd_subcmd.equals("6113"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			Log.e(TAG," д���豸INI�����ļ��Ļ�Ӧ: "+cmd_subcmd_param);
			if (status!=null)
			{
				int fileLen = DPU_Short.bytesToDPUShort(status);
				Log.d(TAG,"INI File len :"+ fileLen + " ����������"+(status.length - 2)+" �ֽ�");
				System.out.println(" INI ��������:"+OrderUtils.bytesToHexString(status));
				notifyDeviceResponse(response.setId("2113").setResult(new String(status)));
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**��ѯģʽ   00:bootģʽ  01: download.bin ģʽ**/
		else if(cmd_subcmd.equals("6114"))
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status != null && status.length >= 1)// boot mode
			{
				int runningMode = -1;
				if (status[0] == (byte)0x00)
				{
					runningMode = DeviceMode.MODE_BOOT;// boot ģʽ
				}
				if (status[0] == (byte)0x01)
				{
					runningMode = DeviceMode.MODE_DOWNLOA_BIN;// download.bin ģʽ
				}
				else
				{
					Log.e(TAG,"�豸�Ĺ���ģʽ�� ???");
				}
				notifyDeviceResponse(response.setId("2114").setResult(runningMode));
			}
			else
			{
				notifyDeviceError(cmd_subcmd);
			}
		}
		/**��������**/
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
		/**����ΪĬ������**/
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
			if (cmd_subcmd_param.equals("650202"))// ����У���ֽ� OK
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
		else if (cmd_subcmd.equals("6503"))// DPU���ӳɹ�
		{
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (cmd_subcmd_param.equals("650302"))// У��OK ��ѯDPU���Ӳ����Ϣ
			{
				if (D)Log.d(TAG, "--->��3��  ����У��OK");
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
		else if (cmd_subcmd.equals("6401"))// ׼������״̬�ж�
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
		else if (cmd_subcmd.equals("6402"))// �ļ������ļ����Ȼ�Ӧ
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
		else if (cmd_subcmd.equals("6403"))// �����ļ����͹���
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
		else if (cmd_subcmd.equals("6404"))// �����ļ�MD5У��
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
		else if (cmd_subcmd.equals("6405"))// �������
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
		else if (cmd_subcmd.equals("6407"))// �л���boot����ģʽ
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
		else if (cmd_subcmd.equals("6408"))// md5�ļ���Ϣ��ȡ
		{
			// ����MD5����У�� �Ա�
			byte[] status = OrderUtils.filterOutCmdParameters(data);
			if (status[0] == 0x00)
			{
				// byte[0] byte[1] �ļ�����
				byte[] temp = OrderUtils.filterOutCmdParameters(data);
				// ������¼У�������ļ���Ϣ
				int fileNum = temp[0] << 8 | temp[1];
				if (D)Log.d(TAG, "++++++��ҪУ��MD5���ļ�����:" + fileNum);
				int offset = 2;
				HashMap<String,String> md5info = new HashMap<String, String>();
				for (int i = 0; i < fileNum; i++) 
				{
					// DPU_String �� java ��String������������ģ� ǰ�߶������������ֽ�
					int fileNameLen = (temp[offset] << 8 | temp[offset + 1]);
					// ��ȡ DPU_String
					byte[] filename_bytes = new byte[fileNameLen - 1];
					for (int j = 0; j < fileNameLen - 1; j++) 
					{
						filename_bytes[j] = temp[offset + 2 + j];
					}
					// ��ȡ md5�ַ���
					byte[] md5bytes = new byte[32];
					for (int j = 0; j < 32; j++)
					{
						md5bytes[j] = temp[offset + 2 + fileNameLen + j];
					}
					String fileName = new String(filename_bytes);// �ļ���
					String md5OnDpu = new String(md5bytes);// �ļ���md5
					md5info.put(fileName, md5OnDpu);
					offset += (2 + fileNameLen + 32);// ����ƫ����λ��,�����������ο�DPU
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

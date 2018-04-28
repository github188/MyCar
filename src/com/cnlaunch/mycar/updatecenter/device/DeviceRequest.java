package com.cnlaunch.mycar.updatecenter.device;

import java.util.Formatter;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.tools.ConditionVariable;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter;
/**
 * �豸����
 * @author luxingsong
 *
 */
public class DeviceRequest
{
	private final static String TAG = "DeviceRequest";
	private final static boolean D = true;
	Object id;
	String url;
	byte[] cmd = null;
	byte[] params = null;
	ConditionVariable completion;
	int timeout;
	Connection connection;
	Context context;
	Activity activity;
	Object  results;
	
	public DeviceRequest()
	{}
	
	public DeviceRequest(Context c,Connection con,String url,byte[] aCmd,
			byte[] aParams)
	{
		this.id = OrderUtils.bytesToHexStringNoBar(aCmd);
		this.url = url;
		this.cmd = aCmd;
		this.params = aParams;
		this.connection = con;
	}
	
	public DeviceRequest(Context c,Connection con,byte[] aCmd,
			byte[] aParams,int timeout,OnDeviceTimeoutListener tl)
	{
		this.id = OrderUtils.bytesToHexStringNoBar(aCmd);
		this.cmd = aCmd;
		this.params = aParams;
		this.connection = con;
		this.timeout = timeout;
		this.timeoutListener = tl;
	}
	
	public Object getReqestId()
	{
		return id.toString();
	}
	
	public DeviceRequest setReqestId(Object o)
	{
		id = o;
		return this;
	}
	
	public byte[] getCmd()
	{
		return cmd;
	}
	
	public DeviceRequest setCmd(byte[] aCmd)
	{
		cmd = aCmd;
		return this;
	}
	
	public byte[] getParams()
	{
		return params;
	}
	
	public DeviceRequest setParams(byte[] aParams)
	{
		params = aParams;
		return this;
	}
		
	/**
	 * �������
	 * @param result ����õ��Ľ��
	 */
	public void complete(Object result)
	{
		this.results = result;
		if(completion!=null)
		{
			completion.set(true);
		}	
	}
	
	/**
	 * �豸��Ӧ��ʱ�����ӿ�
	 * @author luxingsong
	 */
	public interface OnDeviceTimeoutListener
	{
		public void onDeviceTimeout(DeviceRequest deviceRequest);
	}
	
	OnDeviceTimeoutListener timeoutListener;

	public void setOnDeviceTimeoutListener(OnDeviceTimeoutListener tl)
	{
		this.timeoutListener = tl;
	}
	
	public void notifyDeviceTimeout()
	{
		if (timeoutListener!=null)
		{
			timeoutListener.onDeviceTimeout(this);
		}
	}
	
	public int getTimeout()
	{
		return timeout;
	}
	
	public DeviceRequest setTimeout(int aTimeout)
	{
		timeout = aTimeout;
		return this;
	}
	
	/**
	 * �ύ����,��������ᵼ�µ��õ��̱߳�����
	 * @return Object ����õ��Ľ��
	 */
	public Object postAndWait()
	{
		if(connection!=null)
		{
			connection.write(cmd, params);
			completion = new ConditionVariable(false);
			final TimeoutCounter timer = new TimeoutCounter(timeout, new TimeoutCounter.Callback()
			{
				@Override
				public void onTimeout()
				{
				    if (D) Log.d(TAG, "postAndWait --> onTimeout");
					notifyDeviceTimeout();
				}
			});
			try {
				completion.waitForTrue();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;// ���ﷵ��null ������ǿ����ֹ�߳�
			}
			timer.cancel();
			return results;// ������Ӧ���Ľ������
		}
		return null;
	}
	public void waitForTrue()
	{
	    if(completion != null)
	    {
	        try
            {
                completion.waitForTrue();
      
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	    }
	}
	/**
	 * �ύ����,��������ʽ
	 * @return
	 */
	public void post()
	{
		if(connection != null)
		{
			connection.write(cmd, params);
		}
	}
	
	public void dumpInfo()
	{
		if(params != null)
		{
			Log.i(TAG,"����:"+id+"����:["+byteToHex(params)+"]");
		}
		else
		{
			Log.i(TAG,"����:"+id);
		}
	}
	
	public  String byteToHex(byte[] data)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : data ) 
		{
			sb.append(new Formatter().format("%02X-", b));
		}
		return sb.toString();
	}
	
    public void setCounter(byte count)
    {
        connection.setCounter(count);
    }
}

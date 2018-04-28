package com.cnlaunch.mycar.updatecenter.device;

import android.content.Context;

import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.updatecenter.task.TaskListener;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter;

public class DeviceSerialInfoQueryThread extends Thread 
				implements DeviceResponseHandler.Listener,DeviceRequest.OnDeviceTimeoutListener
{
	TaskListener callback;
	TimeoutCounter timer;
	DeviceRequest queryDeviceSerialInfo;
	DeviceRequest rq2504 ;
	DeviceRequest rq2505 ;
	Thread  thread;
	DeviceResponseHandler devResponseHandler;
	boolean timeoutHappened = false;
	
	public DeviceSerialInfoQueryThread(Context context,Connection bluetoothConnection,
											DeviceResponseHandler devhandler,TaskListener cb)
	{
		queryDeviceSerialInfo = new DeviceRequest(context, bluetoothConnection,
										new byte[]{0x21,0x03}, null,10,this);
		rq2504 = new DeviceRequest(context,bluetoothConnection, new byte[]{0x25,0x04}, null,30,this);
		rq2505 = new DeviceRequest(context,bluetoothConnection, new byte[]{0x25,0x05}, null,30,this);
		devResponseHandler = devhandler;
		devResponseHandler.addListener(this);
		thread = this;
		callback = cb;
	}
	
	public void run()
	{
		try
        {
            notifyStart();
            	
            Object ret = queryDeviceSerialInfo.postAndWait();
            
            if (timeoutHappened)
            {
            	return; // 终结线程
            }
            
            if(ret == null)
            {
            	notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, "device response null");
            	return;
            }
        
            devResponseHandler.removeListener(this);
            SerialInfo info = (SerialInfo) ret;
            
//            ret = rq2505.postAndWait();
//            
//            if (timeoutHappened)
//            {
//                return; // 终结线程
//            }
//            
//            if(ret == null)
//            {
//                notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, "device response null");
//                return;
//            }
//            
//            ret = rq2504.postAndWait();
//            
//            if (timeoutHappened)
//            {
//                return; // 终结线程
//            }
//            
//            if(ret == null)
//            {
//                notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, "device response null");
//                return;
//            }
            
            notifyFinish(info);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public void destroy()
	{
		if(thread!=null)
		{
			thread.interrupt();
		}
	}
	
	@Override
	public void onDeviceResponse(DeviceResponse response)
	{
		if(response.getId().equalsIgnoreCase("2103"))
		{
			queryDeviceSerialInfo.complete(response.getResult());				
		}
	}

	@Override
	public void onDeviceError(String request)
	{
		devResponseHandler.removeListener(this);
		notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION, "device response error" + request);
	}
		
	private void notifyStart()
	{
		if(callback!= null)
		{
			callback.onStart(this);
		}
	}
	
	private void notifyFinish(SerialInfo info)
	{
		if(callback!= null)
		{
			callback.onFinish(info);
		}
	}
	
	private void notifyError(int code,Object reason)
	{
		if(callback!= null)
		{
			callback.onError(code, reason);
		}
	}

	@Override
	public void onDeviceTimeout(DeviceRequest deviceRequest)
	{
		timeoutHappened = true;
		if (callback !=null)
		{
			callback.onError(ActionEvent.ERROR_DEVICE_NO_REPLY, deviceRequest);
		}
	}
}

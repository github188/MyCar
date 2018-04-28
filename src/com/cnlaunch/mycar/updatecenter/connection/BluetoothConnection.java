package com.cnlaunch.mycar.updatecenter.connection;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;

public class BluetoothConnection extends Connection implements 
		BluetoothInterface
{
    private boolean D = false;
    private final static String TAG = "BluetoothConnection";
	Context context;
	Activity activity;
	BluetoothDataService bds;
	ConnectionListener listener;
	String mac;
	String devName;
	
	public BluetoothConnection(Context c)
	{
		this.context = c;
		bds = BluetoothDataService.getInstance();
	}
	
	public BluetoothConnection(Activity a)
	{
		this.activity = a;
		bds = BluetoothDataService.getInstance();
	}
	
	@Override
	public String getConnectionType()
	{
		return "bluetooth";
	}

	@Override
	public String getAddress()
	{
		return "bluetooth";
	}

	@Override
	public void write(byte[] cmd,byte[] params)
	{
		if(bds!=null)
		{
			if(params == null)// 不带参数的请求
			{
				bds.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, 
						cmd, null, 0, 3000);
			}
			else
			{
				bds.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, 
						cmd, params, params.length, 3000);
			}
		}
	}

	@Override
	public void addConnectListener(ConnectionListener aListener)
	{
		this.listener = aListener;
		if(bds!=null)
			bds.AddObserver(this);
		if (D)Log.d(TAG, "addConnectListener" + this.listener);
		if (D)Log.d(TAG, "addConnectListener" + bds.displayObserveList());
	}

	@Override
	public void removeConnectListener(ConnectionListener aListener)
	{
		if(bds!=null)
			bds.DelObserver(this);
		  if (D)Log.d(TAG, "removeConnectListener" + this.listener);
	        if (D)Log.d(TAG, "removeConnectListener" + bds.displayObserveList());
	}

	@Override
	public void BlueConnectLost(String aName, String aMac)
	{
		if(listener!=null)
		{
			listener.onConnectionLost(aMac,aName);
		}
	}

	@Override
	public void BlueConnected(String aName, String aMac)
	{
		if(listener!=null)
		{
			listener.onConnectionEstablished(aMac,aName);
		}
	}

	@Override
	public void GetDataFromService(byte[] aDatabuf, int aDatalen)
	{
		if(listener!=null)
		{
			listener.onResponse(aDatabuf, aDatalen);
		}
	}

	@Override
	public void GetDataTimeout()
	{
		if(listener!=null)
		{
			listener.onTimeout();
		}
	}

	@Override
	public void BlueConnectClose()
	{
		if(listener!=null)
		{
			listener.onConnectionCancel();
		}
	}
	@Override
	public boolean isConnected()
	{
		if(bds!=null)
		{
			return bds.IsConnected();
		}
		return false;
	}
	
	/**
	 * 打开连接
	 * */
	@Override
	public void openConnection(String aAddr)
	{
		if(bds.IsConnected())
		{
			if(listener != null)
			{
				listener.onConnectionEstablished(aAddr, "");
				return;
			}
		}
		if(bds!=null)
		{
			bds.ShowBluetoothConnectActivity(activity);
		}
	}

	
	@Override
	public void write(byte[] aData)
	{
	}

    @Override
    public void autoConnection(String mac)
    {
        // TODO Auto-generated method stub
        bds.autoConnectBluetooth(mac);
        
    }

    @Override
    public void reOpenConn()
    {
        // TODO Auto-generated method stub
        if (bds != null)
        {
            bds.StopBlueService(activity);
            bds.ShowBluetoothConnectActivity(activity);
        }
        
    }
    
    public void setCounter(byte count)
    {
        bds.setCounter(count);
    }
}

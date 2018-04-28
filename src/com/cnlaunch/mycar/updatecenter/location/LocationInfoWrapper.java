package com.cnlaunch.mycar.updatecenter.location;

import android.content.Context;

import com.cnlaunch.mycar.rescuevehicles.CellInfoManager;
import com.cnlaunch.mycar.rescuevehicles.CellLocationManager;
import com.cnlaunch.mycar.rescuevehicles.WifiInfoManager;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter;
/**
 * 经纬度信息查询服务
 * */
public class LocationInfoWrapper
{
    Context context;
    LocationInfoListener listener;
    CellLocationManager locationManager;
    
  	public LocationInfoWrapper( Context  context,LocationInfoListener l)
	{
		this.context = context;
		this.listener = l;
	}
  	
  	public void removeListener()
  	{
  		if(listener!=null)
  		{
  			listener = null;
  		}
  	}
  	
	public void getLocationInfo()
	{
		if(context!=null)
		{
			CellInfoManager cellManager = new CellInfoManager(context);
			WifiInfoManager wifiManager = new WifiInfoManager(context);
			
			final TimeoutCounter timer = new TimeoutCounter(10, new TimeoutCounter.Callback()
			{
				@Override
				public void onTimeout()
				{
					notifyError(LocationInfoListener.ERROR_TIMEOUT,null);
				}
			});
			locationManager = new CellLocationManager(context, cellManager, wifiManager)
			{
				@Override
				public void onLocationChanged()
				{
					double latitude = this.latitude(); // 经度
					double longitude = this.longitude(); // 纬度
					timer.cancel();
					this.stop();
					notifyFinish(new LocationInfo(latitude, longitude));
				}
			};
			locationManager.start();	
			notifyStart();
		}
	}
	
	private void notifyStart()
	{
		if(listener!=null){
			listener.onStart(locationManager);
		}
	}
	
	private void notifyError(int code,Object reason)
	{
		if(listener!=null){
			listener.onError(locationManager, code,reason);
		}
	}
	
	private void notifyFinish(Object result)
	{
		if(listener!=null){
			listener.onLocationResult(result);
		}
	}
}

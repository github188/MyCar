package com.cnlaunch.mycar.updatecenter.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChecker
{	
	public static boolean  isConnected(Context c)
	{
		ConnectivityManager CM = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info  = CM.getActiveNetworkInfo();
		if(info != null)
		{
			return info.isConnected();							
		}
		return false;
	}
}

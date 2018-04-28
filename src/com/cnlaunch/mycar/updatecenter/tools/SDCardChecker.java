package com.cnlaunch.mycar.updatecenter.tools;

import android.os.Environment;

public class SDCardChecker
{	
	public static boolean isSDCardMounted()
	{
		return Environment.getExternalStorageState()
				  .equals(android.os.Environment.MEDIA_MOUNTED);
	}
}

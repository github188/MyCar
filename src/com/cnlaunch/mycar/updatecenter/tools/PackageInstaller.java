package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * @author luxingsong  包安装器
 * @version 1.2.0
 * @see SD卡操作的相关API
 * */
public class PackageInstaller 
{
	private static String TAG = "UpdateCenterInstaller";
	private Context context;
	PackageManager pkm;
	
	public PackageInstaller(Context c)
	{
		context = c;
		pkm = c.getPackageManager();
	}
	private void log(String what)
	{
		Log.e(TAG,what);
	}
	private  boolean SDCardIsMounted()
	{
		return Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
	}
	private  void toastWarning(String what)
	{
		if(context!=null)
		{
			Toast.makeText(context, what, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * @author luxingsong
	 * @param fileName 安装的程序包文件名
	 * 从SD卡中安装客户端的APK
	 * */
	public  void installApk(File file)
	{
		if(SDCardIsMounted() 
				&& context!=null )
		{
			if(file.exists())
			{
				// 获取绝对路径
				Uri uri = Uri.parse("file://"+file.getAbsolutePath());
				Intent intent = new Intent(Intent.ACTION_VIEW);  
				intent.setDataAndType(uri,"application/vnd.android.package-archive");  
				context.startActivity(intent);	
			}else
			{
				toastWarning("安装文件"+file+"不存在!");
			}
		}else
		{
			toastWarning("手机没有SD卡或者SD卡没有插好,请检查!");
		} 
	}
	
	/**
	 * @author luxingsong
	 * @param 被删除的文件名
	 * 删除SD卡中的文件
	 * */
	public void deleteFile(File file)
	{
    	if(file.exists())
    	{
        	file.delete();
    	}
    }
}

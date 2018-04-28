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
 * @author luxingsong  ����װ��
 * @version 1.2.0
 * @see SD�����������API
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
	 * @param fileName ��װ�ĳ�����ļ���
	 * ��SD���а�װ�ͻ��˵�APK
	 * */
	public  void installApk(File file)
	{
		if(SDCardIsMounted() 
				&& context!=null )
		{
			if(file.exists())
			{
				// ��ȡ����·��
				Uri uri = Uri.parse("file://"+file.getAbsolutePath());
				Intent intent = new Intent(Intent.ACTION_VIEW);  
				intent.setDataAndType(uri,"application/vnd.android.package-archive");  
				context.startActivity(intent);	
			}else
			{
				toastWarning("��װ�ļ�"+file+"������!");
			}
		}else
		{
			toastWarning("�ֻ�û��SD������SD��û�в��,����!");
		} 
	}
	
	/**
	 * @author luxingsong
	 * @param ��ɾ�����ļ���
	 * ɾ��SD���е��ļ�
	 * */
	public void deleteFile(File file)
	{
    	if(file.exists())
    	{
        	file.delete();
    	}
    }
}

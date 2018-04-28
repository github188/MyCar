package com.cnlaunch.mycar.updatecenter.http;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cnlaunch.mycar.common.webservice.RequestParameter;
import com.cnlaunch.mycar.common.webservice.WebServiceManager;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
/**
 * 
 * Http ���ع�����
 * */
public class HttpDownloadManager
{
	private boolean D = true;
	private final static String TAG = "HttpDownloadWrapper";
	private ArrayList<HttpListener> listeners = new ArrayList<HttpListener>();

	private Context context;
	
	public HttpDownloadManager(Context c)
	{
		this.context = c;
	}
	
	synchronized public void addListener(HttpListener listener)
	{
		if(listener != null)
		{
			listeners.add(listener);
		}
	}
	
	synchronized public void removeListener(HttpListener listener)
	{
		if(listener != null)
		{
			listeners.remove(listener);
		}
	}
	
	synchronized public void removeAllListeners()
	{
		listeners.clear();
	}
	
	/**
	 * ֪ͨ��ʼ����
	 * @param url
	 */
	synchronized void notifyAboutHttpDownloadStart(String url)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			HttpListener l = listeners.get(i);
			l.onHttpStart(url);
		}
	}
	
	/**
	 * ���ؽ���֪ͨ
	 * @param percent
	 * @param speed
	 * @param hour
	 * @param min
	 * @param sec
	 */
	synchronized void notifyAboutHttpDownloadProgress(int percent,int speed,int hour,int min,int sec)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			HttpListener l = listeners.get(i);
			l.onHttpDownloadProgress(percent, speed, hour, min, sec);
		}
	}
	
	/**
	 * ֪ͨ�������
	 * @param target
	 * @param extra
	 */
	synchronized void notifyAboutHttpDownloadFinish(Object target,Object extra)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			HttpListener l = listeners.get(i);
			l.onHttpFinish(target,extra);
		}
	}
	
	synchronized void notifyAboutHttpDownloadException(Object reason)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			HttpListener l = listeners.get(i);
			l.onHttpException(reason);
		}
	}
	
	/**
	 * ����������	
	 * @param ccNumber �û��˻�
	 * @param productionSerialNo ��Ʒ���к�
	 * @param versionDetailID �����ϸid
	 * @param targetPath  �ļ��ı���·��
	 */
	public  void downloadDiagSoft(final String ccNumber,final String productionSerialNo,
				final int versionDetailID,final String targetPath)
	throws IllegalArgumentException
	{
		new Thread()
		{
			public void run()
			{
				if (TextUtils.isEmpty(ccNumber) || TextUtils.isEmpty(productionSerialNo))
				{
					throw new IllegalArgumentException("ccNumber �� productionSerialNo ��Ӧ��Ϊ��");
				}
				
				if (versionDetailID < 0)
				{
					throw new IllegalArgumentException("�汾��ϸid��Ӧ���Ǹ���!");
				}
				
				if (TextUtils.isEmpty(targetPath))
				{
					throw new IllegalArgumentException("û��ָ�������ļ��ı���·��!");
				}
				
				if(D)Log.d(TAG,"downloadDiagSoft() ��http URL ���� ��� �ļ� ...");
				TreeMap<String,Object> map = new TreeMap<String,Object>();
				map.put("cc", ccNumber);// CC ��
				map.put("versionDetailId", versionDetailID);// ��ϸID
				map.put("productSerialNo", productionSerialNo);// ��Ʒ���к�  
				Log.d(TAG,"download CC:"+ccNumber);
				Log.d(TAG,"download versionDetailId:"+versionDetailID);
				Log.d(TAG,"download productSerialNo:"+productionSerialNo);
				WebServiceManager wsm = null;
				String httpUrl = UpdateCenterConstants.UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL;
				RequestParameter requestParameter = new RequestParameter(httpUrl, null, map);
				requestParameter.downloadDir = UpdateCenterConstants.TEMP_DIR;//  ������صı���Ŀ¼
				if(context!=null)
				{	
					wsm = new WebServiceManager(context,requestParameter);
					wsm.executeHttpPost(new HttpDownloadListener());
				}
			}
		}.start();
	} 
	

	/**
	 * ����Android apk ��װ��
	 * @param detailId �����װ������ϸid
	 * @param targetPath ���صõ����ļ�����·��
	 */
	public void downloadApk(final int detailId,final String targetPath) throws IllegalArgumentException
	{
		new Thread()
		{
			public void run()
			{
				if(detailId < 0)
				{
					throw new IllegalArgumentException("detailId ��Ӧ���Ǹ���!");
				}
				
				if(TextUtils.isEmpty(targetPath))
				{
					throw new IllegalArgumentException("Ŀ��·��   targetPath ����Ϊ��!");
				}
				
				if(D)Log.d(TAG,"downloadApk() ��http URL ���� ��� �ļ� ..."+"��ϸ id: "+detailId);
				TreeMap<String,Object> paramList = new TreeMap<String,Object>();
				paramList.put("versionDetailId", detailId);// ��ϸID
				WebServiceManager wsm = null;
				String httpUrl = UpdateCenterConstants.UPDATE_PUBLIC_SOFTWARE_DOWNLOAD_URL;
				RequestParameter requestParameter = new RequestParameter(httpUrl, null, paramList);
				requestParameter.downloadDir = targetPath;// ������ر���Ŀ¼
				if(context!=null)
				{	
					wsm = new WebServiceManager(context,requestParameter);
					wsm.executeHttpPost(new HttpDownloadListener());
				}
			}
		}.start();
	}
	
	/**
	 * �����ļ�
	 * @param url     ���ص�URL
	 * @param destDir �����ļ������Ŀ��·��
	 * @param params  ���ز����б�
	 * @param mustSign  �Ƿ���Ҫǩ����֤
	 */
	public void download(final String url,final String destDir,final TreeMap<String,Object> params,boolean mustSign)
	{
		new Thread()
		{
			public void run()
			{
				WebServiceManager wsm = null;
				RequestParameter requestParameter = new RequestParameter(url, null, params);
				requestParameter.downloadDir = destDir;// ������ر���Ŀ¼
				if(context!=null)
				{	
					wsm = new WebServiceManager(context,requestParameter);
					wsm.executeHttpPost(new HttpDownloadListener());
				}
			}
		}.start();
	}
	
	/**
	 * ����WebServiceManager�����ؽ���
	 * */
	private class HttpDownloadListener implements WebServiceManager.HttpDownloadListener
	{
		@Override
		public void onHttpDownloadStart(String url, Object params)
		{
			notifyAboutHttpDownloadStart(url);
		}

		@Override
		public void onHttpDownloadProgress(int percent, int restHours,
				int restMinutes, int restSeconds)
		{
			notifyAboutHttpDownloadProgress(percent, 0, restHours, restMinutes, restSeconds);
		}

		@Override
		public void onHttpDownloadFinished(File target,Object extra)
		{
			notifyAboutHttpDownloadFinish(target,extra);
		}

		@Override
		public void onHttpDownloadException(Object detail)
		{
			notifyAboutHttpDownloadException(detail);
		}
	}
}

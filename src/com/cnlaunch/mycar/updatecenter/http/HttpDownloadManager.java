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
 * Http 下载管理器
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
	 * 通知开始下载
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
	 * 下载进度通知
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
	 * 通知下载完成
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
	 * 下载诊断软件	
	 * @param ccNumber 用户账户
	 * @param productionSerialNo 产品序列号
	 * @param versionDetailID 软件明细id
	 * @param targetPath  文件的保存路径
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
					throw new IllegalArgumentException("ccNumber 与 productionSerialNo 不应该为空");
				}
				
				if (versionDetailID < 0)
				{
					throw new IllegalArgumentException("版本明细id不应该是负数!");
				}
				
				if (TextUtils.isEmpty(targetPath))
				{
					throw new IllegalArgumentException("没有指定下载文件的保存路径!");
				}
				
				if(D)Log.d(TAG,"downloadDiagSoft() 从http URL 下载 诊断 文件 ...");
				TreeMap<String,Object> map = new TreeMap<String,Object>();
				map.put("cc", ccNumber);// CC 号
				map.put("versionDetailId", versionDetailID);// 明细ID
				map.put("productSerialNo", productionSerialNo);// 产品序列号  
				Log.d(TAG,"download CC:"+ccNumber);
				Log.d(TAG,"download versionDetailId:"+versionDetailID);
				Log.d(TAG,"download productSerialNo:"+productionSerialNo);
				WebServiceManager wsm = null;
				String httpUrl = UpdateCenterConstants.UPDATE_DIAG_SOFTWARE_ONE_KEY_DOWNLOAD_URL;
				RequestParameter requestParameter = new RequestParameter(httpUrl, null, map);
				requestParameter.downloadDir = UpdateCenterConstants.TEMP_DIR;//  软件下载的保存目录
				if(context!=null)
				{	
					wsm = new WebServiceManager(context,requestParameter);
					wsm.executeHttpPost(new HttpDownloadListener());
				}
			}
		}.start();
	} 
	

	/**
	 * 下载Android apk 安装包
	 * @param detailId 软件安装包的明细id
	 * @param targetPath 下载得到的文件保存路径
	 */
	public void downloadApk(final int detailId,final String targetPath) throws IllegalArgumentException
	{
		new Thread()
		{
			public void run()
			{
				if(detailId < 0)
				{
					throw new IllegalArgumentException("detailId 不应该是负数!");
				}
				
				if(TextUtils.isEmpty(targetPath))
				{
					throw new IllegalArgumentException("目标路径   targetPath 不能为空!");
				}
				
				if(D)Log.d(TAG,"downloadApk() 从http URL 下载 诊断 文件 ..."+"明细 id: "+detailId);
				TreeMap<String,Object> paramList = new TreeMap<String,Object>();
				paramList.put("versionDetailId", detailId);// 明细ID
				WebServiceManager wsm = null;
				String httpUrl = UpdateCenterConstants.UPDATE_PUBLIC_SOFTWARE_DOWNLOAD_URL;
				RequestParameter requestParameter = new RequestParameter(httpUrl, null, paramList);
				requestParameter.downloadDir = targetPath;// 软件下载保存目录
				if(context!=null)
				{	
					wsm = new WebServiceManager(context,requestParameter);
					wsm.executeHttpPost(new HttpDownloadListener());
				}
			}
		}.start();
	}
	
	/**
	 * 下载文件
	 * @param url     下载的URL
	 * @param destDir 下载文件保存的目标路径
	 * @param params  下载参数列表
	 * @param mustSign  是否需要签名验证
	 */
	public void download(final String url,final String destDir,final TreeMap<String,Object> params,boolean mustSign)
	{
		new Thread()
		{
			public void run()
			{
				WebServiceManager wsm = null;
				RequestParameter requestParameter = new RequestParameter(url, null, params);
				requestParameter.downloadDir = destDir;// 软件下载保存目录
				if(context!=null)
				{	
					wsm = new WebServiceManager(context,requestParameter);
					wsm.executeHttpPost(new HttpDownloadListener());
				}
			}
		}.start();
	}
	
	/**
	 * 监听WebServiceManager的下载进度
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

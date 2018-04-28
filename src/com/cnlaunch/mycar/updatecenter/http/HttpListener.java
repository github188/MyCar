package com.cnlaunch.mycar.updatecenter.http;

public interface HttpListener
{
	public void onHttpStart(String url);
	public void onHttpDownloadProgress(int percent,int speed,int restHours,int restMinutes,int restSeconds);
	public void onHttpUploadProgress(int percent,int speed,int restHours,int restMinutes,int restSeconds);
	public void onHttpException(Object reason);
	public void onHttpFinish(Object target,Object extra);
}

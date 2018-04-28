package com.cnlaunch.mycar.updatecenter.tools;
/**
 * 通信速度统计类
 * 可以计算发送文件的余下时间
 * */
public class StatisticHelper
{
	int deltaBytes;  
	long deltaTimeInMillis;
	int bytesPerSecond;
	int restTimeInSeconds;

	public StatisticHelper()
	{
	}
	
	public void calcResults(long restBytes,int deltaBytes,long startTime,long endTime)
	{
		deltaTimeInMillis = endTime - startTime;
		if (deltaTimeInMillis!=0)// 小心 除以0异常
		{
			bytesPerSecond = (int)( 1000 * deltaBytes / deltaTimeInMillis) ;// 每秒传输的字节数 Bytes/s
			restTimeInSeconds = (int)((restBytes) / bytesPerSecond);			
		}
		else
		{
			bytesPerSecond = 60;
			restTimeInSeconds = 60;
		}
	}
	
	public int getRestSeconds()
	{
		return restTimeInSeconds % 60;
	}
	
	public int getRestMinutes()
	{
		return restTimeInSeconds / 60;
	}
	
	public int getRestHours()
	{
		return restTimeInSeconds / (60 * 60);
	}
	
	public int getTransmissionSpeed()
	{
		return bytesPerSecond;
	}
}

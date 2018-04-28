package com.cnlaunch.mycar.updatecenter.tools;
/**
 * ͨ���ٶ�ͳ����
 * ���Լ��㷢���ļ�������ʱ��
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
		if (deltaTimeInMillis!=0)// С�� ����0�쳣
		{
			bytesPerSecond = (int)( 1000 * deltaBytes / deltaTimeInMillis) ;// ÿ�봫����ֽ��� Bytes/s
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

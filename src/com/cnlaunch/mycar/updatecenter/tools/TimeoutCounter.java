package com.cnlaunch.mycar.updatecenter.tools;
import java.util.Timer;
import java.util.TimerTask;

import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;

import android.util.Log;
/**
 * 超时服务
 * @author luxingsong
 * */
public class TimeoutCounter 
{
	private final static String TAG = "TimeoutCounter";
	private boolean D = false;
	/**
	 * 超时监听接口
	 * */
	public interface Callback{
		public void onTimeout();
	}
	/**
	 * 倒计时接口
	 * */
	public interface OnCountdownListener
	{
		public void onCountdownStart();
		public void onCounting(int totalSec,int leftSec);
		public void onCountdownEnded();
		public void onContdownCancled();
	}
	
	Timer timer;
	Callback callback;
	boolean countdownCancel = false;
	OnCountdownListener countDownListener;
	int initCountDown = 0;
	/**构造函数,需要超时参数,监听接口
	 * */
	public TimeoutCounter(int timeout_seconds,Callback callback)
	{
		if(timeout_seconds > 0 && FirmwareUpdate.cellState == 0)
		{
			this.timer = new Timer();
			this.callback = callback;
			if(D)Log.d(TAG,this+" 开始超时服务...");
			this.timer.schedule(new CounterTask(), timeout_seconds*1000);			
		}
	}
	
	public TimeoutCounter(int initValue,OnCountdownListener listener)
	{
		this.initCountDown = initValue;
		this.countDownListener = listener;
		new CountDownThread().start();
	}
	
	public void setCallback(Callback callback)
	{
		this.callback = callback;
	}

	public void cancel()
	{
		if(timer!=null){
			if(D)Log.d(TAG,this+"超时服务被取消");
			timer.cancel();
			timer = null;
			callback = null;
		}
	}
	
	class CounterTask extends TimerTask
	{
		public void run()
		{
			if(callback!=null)callback.onTimeout();
		}
	} 
	
	private void notifyCountdownStart()
	{
		if(countDownListener!=null)
			countDownListener.onCountdownStart();
	}
	
	private void notifyCountdown(int totalSec,int leftSec)
	{
		if(countDownListener!=null)
			countDownListener.onCounting(totalSec, leftSec);
	}
	private void notifyCountdownEnd()
	{
		if(countDownListener!=null)
			countDownListener.onCountdownEnded();
	}
	private void notifyCountdownCanceled()
	{
		if(countDownListener!=null)
			countDownListener.onContdownCancled();
	}
	
	public void cancleCountdown()
	{
		this.countdownCancel = true;
		notifyCountdownCanceled();
	}
	
	class CountDownThread extends Thread
	{
		public void run()
		{
			notifyCountdownStart();
			final int initValue = initCountDown;
			while(initCountDown-- > 0 && !countdownCancel)
			{
			    if (FirmwareUpdate.cellState == 0)
			    {
		             try {
		                    Thread.sleep(1000);
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
		                notifyCountdown(initValue, initCountDown);
			    }

			}
			notifyCountdownEnd();
		}
	}
}

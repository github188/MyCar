package com.cnlaunch.mycar.updatecenter.tools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

import android.util.Log;

/**
 * 日志文件
 * */
public class Logger
{
	private final static String TAG = "Logger";
	RandomAccessFile raf;
	File logDir; // 日志存放的目录
	File logFile;// 日志文件
	
	/**构造函数
	 * moduleTag 模块标签
	 * path  日志文件的保存目录
	 * */
	public Logger(String moduleTag,String path)
	{
		Log.e(TAG," 日志文件名称: "+moduleTag+"_"+".log");
		logDir = new File(path);
		logFile = new File(logDir,"MyCar_"+moduleTag+".log");
	}
	
	private void check()
	{
		if(!logDir.exists())
		{
			logDir.mkdirs();
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				Log.e(TAG," 创建日志文件  "+logFile+"异常!"+e.getMessage());
				e.printStackTrace();
			}
		}
		try {
			if(raf==null)	
			{
				raf = new RandomAccessFile(logFile,"rw");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public  synchronized void printf(Object msg)
	{
		check();
		try {
			if(raf!=null){
				raf.seek(raf.length());
				@SuppressWarnings("deprecation")
				DateFormat df = DateFormat.getDateInstance(DateFormat.WEEK_OF_MONTH_FIELD);
				String timestamp = df.toString();
				String logStr;
				logStr = "["+timestamp+"]:\t"+msg.toString()+"\n";
				raf.write(logStr.getBytes());				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void printf(String TAG,Object msg)
	{
		check();
		try {
			if(raf!=null){
				raf.seek(raf.length());
				@SuppressWarnings("deprecation")
				String timestamp = new Date().toLocaleString();
				String logStr;
				if(TAG==null)
				    logStr = "["+timestamp+"]:\t"+" "+msg.toString()+"\n";
				else
					logStr = "["+timestamp+"]:\t"+TAG+" "+msg.toString()+"\n";
				raf.write(logStr.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void error(String TAG,Object msg)
	{
		printf("ERROR: "+TAG,msg);
	}
	
	public void warning(String TAG,Object msg)
	{
		printf("WARNING: "+TAG,msg);
	}
	
	public void info(String TAG,Object msg)
	{
		printf("INFO: "+TAG,msg);
	}
	
	public void close()
	{
		if(this.raf!=null)
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}

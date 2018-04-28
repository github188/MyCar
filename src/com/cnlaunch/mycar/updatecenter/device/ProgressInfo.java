package com.cnlaunch.mycar.updatecenter.device;

import java.io.File;

// 进度信息
public class ProgressInfo
{
	File  file;
	int fileSum;
	int current;
	String msg;
	int totalBytes;
	int sentBytes;
	int speedKB;
	int leftSeconds;
	int leftMinites;
	int leftHours;
	int percent;
	
	public ProgressInfo(){}
	
	public int getFileSum()
	{
		return fileSum;
	}

	public ProgressInfo setFileSum(int fileSum)
	{
		this.fileSum = fileSum;
		return this;
	}

	public int getCurrent()
	{
		return current;
	}

	public ProgressInfo setCurrent(int current)
	{
		this.current = current;
		return this;
	}

	public ProgressInfo(File file, String msg, int totalBytes, int sentBytes,
			int speedKB, int leftSeconds, int leftMinites, int leftHours)
	{
		this.file = file;
		this.msg = msg;
		this.totalBytes = totalBytes;
		this.sentBytes = sentBytes;
		this.speedKB = speedKB;
		this.leftSeconds = leftSeconds;
		this.leftMinites = leftMinites;
		this.leftHours = leftHours;
	}

	public int getPercent()
	{
		return percent;
	}

	public ProgressInfo setPercent(int percent)
	{
		this.percent = percent;
		return this;
	}

	public File getFile()
	{
		return file;
	}

	public ProgressInfo setFile(File file)
	{
		this.file = file;
		return this;
	}

	public String getMsg()
	{
		return msg;
	}

	public ProgressInfo setMsg(String msg)
	{
		this.msg = msg;
		return this;
	}

	public int getTotalBytes()
	{
		return totalBytes;
	}

	public ProgressInfo setTotalBytes(int totalBytes)
	{
		this.totalBytes = totalBytes;
		return this;
	}

	public int getSentBytes()
	{
		return sentBytes;
	}

	public ProgressInfo setSentBytes(int sentBytes)
	{
		this.sentBytes = sentBytes;
		return this;
	}

	public int getSpeedKB()
	{
		return speedKB;
	}

	public ProgressInfo setSpeedKB(int speedKB)
	{
		this.speedKB = speedKB;
		return this;
	}

	public int getLeftSeconds()
	{
		return leftSeconds;
	}

	public ProgressInfo setLeftSeconds(int leftSeconds)
	{
		this.leftSeconds = leftSeconds;
		return this;
	}

	public int getLeftMinites()
	{
		return leftMinites;
	}

	public ProgressInfo setLeftMinites(int leftMinites)
	{
		this.leftMinites = leftMinites;
		return this;
	}

	public int getLeftHours()
	{
		return leftHours;
	}

	public ProgressInfo setLeftHours(int leftHours)
	{
		this.leftHours = leftHours;
		return this;
	}

	@Override
	public String toString()
	{
		return "ProgressInfo [file=" + file + ", msg=" + msg + ", totalBytes="
				+ totalBytes + ", sentBytes=" + sentBytes + ", speedKB="
				+ speedKB + ", leftSeconds=" + leftSeconds + ", leftMinites="
				+ leftMinites + ", leftHours=" + leftHours + "]";
	}
	
}

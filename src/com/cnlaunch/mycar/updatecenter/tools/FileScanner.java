package com.cnlaunch.mycar.updatecenter.tools;


import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

/**
 * 升级文件扫描器
 * 在给定的目录下扫描，找出需要升级的文件
 * */
public class FileScanner
{
  public interface Error
  {
	  public final static int FILE_NOT_FOUND = -1;
  }
  
  public interface Listener
  {
	  public void onScanStart(File dir);
	  public void onScanning(File dir);
	  public void onScanFinished(File dir,File[] result);
	  public void onScanFailed(int err,Object reason);
  }
  
  Context context;
  
  Listener scanListener;
  File dirToScan;
  
  /**
   * 正则表达式过滤列表
   * */
  private final static String[] regExFilterList =
  {
		 "(?i:dpuX431.ggp)",
		 "(?i:sysscan.bin)",
		 "(?i:funcfg.dll)",
		 "(?i:funcfg.bin)",
		 "(?i:menu.bin)",
		 "(?i:udscfg.bin)",
		 "(?i:menu)_[0-9]{1,2}\\.(?i:bin)",
  };
  
  String[] filterList = regExFilterList;
  
  public FileScanner(Context ctx)
  {
	  this.context = ctx;
  }
  
  public FileScanner(Context ctx,File dir,Listener l)
  {
	  this.context = ctx;
	  this.dirToScan = dir;
	  this.scanListener = l;
  }
  
  public void setDirToScan(File dir)
  {
	  this.dirToScan = dir;
  }
  
  public void setFilterList(String[] list)
  {
	  if (filterList == null)
	  {
		  filterList = regExFilterList;
	  }
	  filterList = list;
  }

  public void setScanListener(Listener listener)
  {
	  this.scanListener = listener;
  }
  
  public void doScan()
  {
	  new ScanThread().start();
  }
  
  class ScanThread extends Thread
  {
	  @Override
	  public void run()
	  {
		  if(dirToScan==null || !dirToScan.exists())
		  {
			  scanListener.onScanFailed(Error.FILE_NOT_FOUND,"没有找到指定的文件或者目录 ");
			  return;
		  }
		  
		  if(filterList==null)// 没有过滤条件的扫描
		  {
			  scanListener.onScanFinished(dirToScan, dirToScan.listFiles());
			  return;
		  }
		  
		  if(scanListener!=null)
			  scanListener.onScanStart(dirToScan);
		  
		  
		  File[] list = dirToScan.listFiles(new FileFilter()
		  {
			@Override
			public boolean accept(File pathname)
			{
				if(scanListener != null)
					scanListener.onScanning(pathname);
				for(int i=0;i<filterList.length;i++)
				{
					Pattern p = Pattern.compile(filterList[i]);
					Matcher m = p.matcher(pathname.getName());
					if(m.matches())
					{
						return true;
					}
				}
				return false;
			}
		  });
		  if(scanListener != null)
			  scanListener.onScanFinished(dirToScan, list);
	  }
  }
}

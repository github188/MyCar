package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;

/**
 * 升级文件的数据完整性检查
 * 防止升级文件不完整的时候
 * 也进行操作
 * */
public class FileIntegrityChecker
{
	public interface Listener
	{
		public void onCheck(boolean isComplete,Object reason);
	}
	
	Listener listener;
	
	/**
	 * 必须升级的文件列表[默认是诊断配置升级]
	 * */
	private final static String[] defaultTagStringArray = 
	{
		 "dpuX431.ggp",
		 "sysscan.bin",
		 "funcfg.dll",
		 "funcfg.bin",
		 "menu.bin"
	};
	
	/**
	 * 简单的标记
	 * */
	class TAG
	{
		String name;
		boolean found;
	}
	
	private  String[] TAG_STRING_ARRAY = defaultTagStringArray;
	
	TAG[] tagTable;
	File[] fileList ;
	
	public FileIntegrityChecker(File[] fileList,Listener l)
	{
		this.fileList = fileList;
		this.listener = l;
		initTAGForCheck();
	}
	
	public void setTagList(String[] array)
	{
		this.TAG_STRING_ARRAY = array;
	}
	
	public String[] getTagList()
	{
		return this.TAG_STRING_ARRAY;
	}
	
	private void initTAGForCheck()
	{
		tagTable = new TAG[TAG_STRING_ARRAY.length];
		for (int i = 0; i < defaultTagStringArray.length; i++)
		{
			tagTable[i] = new TAG();
			tagTable[i].name = defaultTagStringArray[i];
			tagTable[i].found = false;
		}
	}
	
	private void validate()
	{
		int count = 0;
		for(int i=0;i<tagTable.length;i++)
		{
			if(tagTable[i].found)
				++count;
		}
		if(count < tagTable.length)
		{
			if(listener!=null)
			{
				listener.onCheck(false, "升级文件不完整,请重新下载软件包来升级.");
				return;
			}	
		}
		listener.onCheck(true, "");
	}
	
	private boolean checkByName(File file)
	{
		if (file == null) return false;
		String fileName = file.getName();
		for(int i=0;i < tagTable.length;i++)
		{
			// 不区分大小写
			if(fileName.equalsIgnoreCase(tagTable[i].name))
			{
				return tagTable[i].found = true;
			}
		}
		return false;
	}
	
	public void doCheck()
	{
		new CheckThread().start();
	}
	
	class CheckThread extends Thread
	{
		public void run()
		{
			if(fileList == null)
			{
				if(listener!=null)
				{
					listener.onCheck(false, "升级文件不完整,请下载正确的软件包.");
				}
				return;
			}
			
			if(fileList.length < defaultTagStringArray.length)
			{
				if(listener!=null)
				{
					listener.onCheck(false, "升级文件不完整,请下载正确的软件包.");
				}	
				return;
			}
			
			for(int j=0;j< fileList.length;j++)
			{
				checkByName(fileList[j]);
			}
			validate();
		}
	}
}

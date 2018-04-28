package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;

/**
 * �����ļ������������Լ��
 * ��ֹ�����ļ���������ʱ��
 * Ҳ���в���
 * */
public class FileIntegrityChecker
{
	public interface Listener
	{
		public void onCheck(boolean isComplete,Object reason);
	}
	
	Listener listener;
	
	/**
	 * �����������ļ��б�[Ĭ���������������]
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
	 * �򵥵ı��
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
				listener.onCheck(false, "�����ļ�������,���������������������.");
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
			// �����ִ�Сд
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
					listener.onCheck(false, "�����ļ�������,��������ȷ�������.");
				}
				return;
			}
			
			if(fileList.length < defaultTagStringArray.length)
			{
				if(listener!=null)
				{
					listener.onCheck(false, "�����ļ�������,��������ȷ�������.");
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

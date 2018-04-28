package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

public class FileLengthUtil
{
	boolean D = true;
	private final static String TAG = "FileLengthUtil";
	/**
	 * 计算一个文件列表中所有文件长度的总和
	 * @param fileList 文件列表
	 * @return 文件长度总和[单位 ： 字节]
	 */
	public static long calcTotalBytesInFileList(ArrayList<File> fileList) 
	{
		if (fileList != null && fileList.size() > 0) 
		{
			int file_cnt = fileList.size();
			long total_length = 0;
			for (int i = 0; i < file_cnt; i++)
			{
				File f = fileList.get(i);
				if (f != null && f.exists() && f.isFile())
				{
					total_length += f.length();
				} 
			}
			Log.d(TAG, "文件总字节数:" + total_length + "Bytes [" + total_length / 1024 + " KB]");
			return total_length;
		}
		return 0;
	}
}

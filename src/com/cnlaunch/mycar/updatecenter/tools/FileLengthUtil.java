package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

public class FileLengthUtil
{
	boolean D = true;
	private final static String TAG = "FileLengthUtil";
	/**
	 * ����һ���ļ��б��������ļ����ȵ��ܺ�
	 * @param fileList �ļ��б�
	 * @return �ļ������ܺ�[��λ �� �ֽ�]
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
			Log.d(TAG, "�ļ����ֽ���:" + total_length + "Bytes [" + total_length / 1024 + " KB]");
			return total_length;
		}
		return 0;
	}
}

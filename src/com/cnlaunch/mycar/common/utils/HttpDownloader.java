package com.cnlaunch.mycar.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * ��������ʹ��httpЭ�� �����ļ�
 * @author xiangyuanmao
 *
 */
public class HttpDownloader {

	private URL url = null; // ˽�е�URL������
	
	/**
	 * �����ı��ļ�
	 * @param urlStr
	 * @return �ı��ļ�������
	 */
	public String download(String urlStr)
	{
		// ����һ���������(�䳤)�ַ�������
		StringBuffer sb = new StringBuffer();
		// ÿһ������
		String line = null; 
		// 
		BufferedReader buffer = null;
		
		try {
			url = new URL(urlStr);
			// ��URL����
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			// ʹ��IO����ȡ����
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			// ��ʼ���ļ�
			while ((line = buffer.readLine()) != null)
			{
				sb.append(line);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d("System.out", sb.toString());
		return sb.toString();
	}
	
	/**
	 * �����ļ�
	 * @param url
	 * @param path
	 * @param fileName
	 * @return -1���ļ����س��� 0: �ļ����سɹ��� 1:�ļ��Ѿ�����
	 */
//	public int downFile(String url, String path, String fileName)
//	{
//		InputStream input = null;
//		try {
//			// ʵ�����ļ�������
//			FileUtils fileUtils = new FileUtils();
//			// �ж��ļ��Ƿ��Ѿ�����
//			if (fileUtils.isFileExist(path + fileName))
//			{
//				return 1;
//			}
//			else
//			{
//				// �������ϻ�ȡ������
//				input = this.getInputStreamFromURL(url);
//				// ���ļ�д��SD��
////				File file = fileUtils.writeToSDFromInput(path, fileName, input);
////				if (file == null)
//				{
//					return -1;
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return -1;
//		} finally {
//			try {
//				// ��ȫ�ر�input��
//				if (input != null)
//				{
//					input.close();	
//				}
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return 0;
//	}
	
    /**
     * ��������ȡ��������
     * @param urlStr
     * @return
     */
	public InputStream getInputStreamFromURL(String urlStr)
	{
		InputStream input  = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			input =  urlConn.getInputStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
	}
}

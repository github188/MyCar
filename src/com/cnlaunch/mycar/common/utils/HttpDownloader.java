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
 * 从网络上使用http协议 下载文件
 * @author xiangyuanmao
 *
 */
public class HttpDownloader {

	private URL url = null; // 私有的URL对象域
	
	/**
	 * 下载文本文件
	 * @param urlStr
	 * @return 文本文件的内容
	 */
	public String download(String urlStr)
	{
		// 声明一个带缓冲的(变长)字符串对象
		StringBuffer sb = new StringBuffer();
		// 每一行数据
		String line = null; 
		// 
		BufferedReader buffer = null;
		
		try {
			url = new URL(urlStr);
			// 打开URL连接
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			// 使用IO流读取数据
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			// 开始读文件
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
	 * 下载文件
	 * @param url
	 * @param path
	 * @param fileName
	 * @return -1：文件下载出错； 0: 文件下载成功； 1:文件已经存在
	 */
//	public int downFile(String url, String path, String fileName)
//	{
//		InputStream input = null;
//		try {
//			// 实例化文件工具类
//			FileUtils fileUtils = new FileUtils();
//			// 判断文件是否已经存在
//			if (fileUtils.isFileExist(path + fileName))
//			{
//				return 1;
//			}
//			else
//			{
//				// 从网络上获取输入流
//				input = this.getInputStreamFromURL(url);
//				// 把文件写到SD卡
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
//				// 安全关闭input流
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
     * 从网络上取得输入流
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

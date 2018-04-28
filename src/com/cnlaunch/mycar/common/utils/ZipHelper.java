package com.cnlaunch.mycar.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.util.Log;

/**
 * 
 * 解压文件类
 * 使用示例:
 * UnzipListener listener = new UnzipListener(){...};
 * ZipHelper zipTool = new ZipHelper(listener);
 * zipTool.unzipFileTo(new File(/the/path/to/your/zip),/the/path/to/your/target_dir);
 *                    
 * */
public class ZipHelper
{
	public final static String TAG ="ZipHelper";

	public interface UnzipListener
    {
    	public void onUnzipping(String file,String destDir,int percent);
    	public void onUnzipFinished(File file,String destDir);
    	public void onUnzipError(File file,Object reason);
    }
    
    UnzipListener unzipListener;
    
	public ZipHelper()
	{}
	
	public ZipHelper(UnzipListener unzipListener)
	{
		this.unzipListener = unzipListener;
	}
	
	public void setUnzipListener(UnzipListener unzipListener)
	{
		this.unzipListener = unzipListener;
	}

	/**
     * 解压zip文件到指定的目录
     * @param theZipFile 被解压的文件
     * @param targetDir  解压目标目录
     * */
	public void unzipFileTo(final File theZipFile,final String targetDir)
	{
		new Thread()
		{
			public void run()
			{
				if((theZipFile == null) || (targetDir==null))
				{
					return;
				}	
				String folderPath = targetDir;
				
				Log.e("zip", "文件路径是否正确：" + theZipFile.exists() + theZipFile.toString());
				try {
					ZipFile zfile = new ZipFile(theZipFile);
					Enumeration<? extends ZipEntry> zList = zfile.entries();
					ZipEntry ze = null;
					byte[] buf = new byte[1024];
					
					while (zList.hasMoreElements()) 
					{
						if(unzipListener!=null)
						{
							unzipListener.onUnzipping(theZipFile.getAbsolutePath(), folderPath, 99);
						}
						ze = (ZipEntry) zList.nextElement();
						if (ze.isDirectory()) 
						{
							Log.d("upZipFile", "ze.getName() = " + ze.getName());
							String dirstr = folderPath + ze.getName();
							// dirstr.trim();
							dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
							Log.d("upZipFile", "str = " + dirstr);
							File f = new File(dirstr);
							f.mkdirs();
							continue;
						}
						Log.d("upZipFile", "ze.getName() = " + ze.getName());
						OutputStream os = new BufferedOutputStream(
								new FileOutputStream(getRealFileName(folderPath,
										ze.getName())));
						InputStream is = new BufferedInputStream(
								zfile.getInputStream(ze));
						int readLen = 0;
						while ((readLen = is.read(buf, 0, 1024)) != -1)
						{
							os.write(buf, 0, readLen);
						}
						is.close();
						os.close();
					}
					if(unzipListener!=null)// 回调通知解压完成
					{
						unzipListener.onUnzipFinished(theZipFile, folderPath);
					}
				} catch (ZipException e) {
					if(unzipListener!=null)// 回调通知解压出错
					{
						unzipListener.onUnzipError(theZipFile, e.getMessage());
					}
					return;
				} catch (IOException e) {
					if(unzipListener!=null)// 回调通知解压出错
					{
						unzipListener.onUnzipError(theZipFile, e.getMessage());
					}
					return;
				}
				
			}
		}.start();
	}
	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * @param baseDir 指定根目录
	 * @param absFileName 相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}
}

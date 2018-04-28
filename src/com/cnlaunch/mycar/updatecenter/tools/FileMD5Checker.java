package com.cnlaunch.mycar.updatecenter.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import android.util.Log;

/**@author luxingsong
 * 文件的MD5校验类，做升级之前要对所有的文件进行一次MD5校验
 * 并且把这个结果保存[类似cookie的作用]，以便下一次升级使用
 * 以及完成升级后做数据完整对比，判断升级是否成功。
 * */
public class FileMD5Checker
{
	public final static String TAG = "FileMD5Checker";
	private boolean D = true;
	private   File recordFile;    //用来记录MD5信息的文件
	private  final static String DefaultName = "md5.inf";
	private FileOutputStream fous;
	byte[] buffer = new byte[1024];
	
	public interface Listener
	{
		public void onValidateMD5Info();
		public void onMD5CalculateException(File file,Object reason);
		public void onMD5Calculating(File file,int percent,int restMinutes,int restSeconds);
		public void onMD5CalculationCompleted(File[] files,HashMap<String,String> result);
		public void onGeneratingMD5Record(File file);
	}
	
	Listener listener;
	
	public void setFileMD5CheckListener(Listener lsn)
	{
		this.listener = lsn;
	}
	
	private File[] checkedFileList;// 被校验的文件列表
	
	public FileMD5Checker(File[] files,Listener lsn) 
	{
		this.checkedFileList = files;
		this.listener = lsn;
		if(checkedFileList!=null)
		{
			if (this.listener!=null)listener.onValidateMD5Info();
			if(checkedFileList.length > 0)
			{
			
				String path = checkedFileList[0].getParent();// 获取当前路径
				path = path.replace(checkedFileList[0].getName(), DefaultName);				
				recordFile = new File(path,DefaultName);// md5 记录文件与升级文件位于同一级目录下
				try 
				{
					recordFile.delete();
					fous  = new FileOutputStream(recordFile);
					if (listener!=null)
					{
						listener.onGeneratingMD5Record(recordFile);							
					}
					if(D)Log.e(TAG,"创建MD5记录信息文件... " + recordFile.getName());
					new CheckThread(checkedFileList).start();
				} 
				catch (IOException e)
				{
					if (listener!=null)
					{
						listener.onMD5CalculateException(recordFile, "创建记录文件失败 "+e.getMessage());							
					}
				}					
			}else
			{
				if (listener!=null)
				{
					listener.onMD5CalculateException(recordFile,"指定的文件列表长度值为0");							
				}
			}
		}
	}

	// 解析MD5信息文件
	public  static HashMap<String,String>  getMD5FromCookie(File file)
	{
		if(file!=null && file.isFile()
				&&file.getName().equalsIgnoreCase(DefaultName))
		{
			byte[] temp = new byte[(int) file.length()];
			try 
			{
				FileInputStream fins = new FileInputStream(file);
				while(fins.read(temp)!=-1);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			HashMap<String, String> md5info = new HashMap<String, String>();
			String infoString = new String(temp);
			String lineSplit = "\n";  // 行分隔符
			String keyValueSplit = "-";// 键值对分隔符
			String[] strArray = infoString.split(lineSplit);
			
			for(int i=0;i<strArray.length;i++)
			{
				String[] key_value = strArray[i].split(keyValueSplit);// 分解出  文件名- MD5 键值对
				md5info.put(key_value[0], key_value[1]);
			}			
			return md5info;
		}
		return  new HashMap<String, String>();
	}
	// 解析MD5信息文件
	public  static HashMap<String,String>  getMD5Info(String str)
	{
		if( str != null )
		{
			HashMap<String, String> md5info = new HashMap<String, String>();
			String infoString = str;
			String lineSplit = "\n";  // 行分隔符
			String keyValueSplit = "-";// 键值对分隔符
			String[] strArray = infoString.split(lineSplit);
			
			for(int i=0;i<strArray.length;i++)
			{
				String[] key_value = strArray[i].split(keyValueSplit);// 分解出  文件名- MD5 键值对
				md5info.put(key_value[0], key_value[1]);
			}			
			return md5info;
		}
		return  new HashMap<String, String>();
	}
	
	/**@author luxingsong
	 * 将字节数组转换为十六进制的字符串
	 * */
    public  static String byteArrayToHex(byte[] hash) 
    {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
    
    /**@author luxingsong
     * 计算文件的MD5校验值
     * */
	public  String calculateMD5sum(File file) throws Exception
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
        FileInputStream  fis = new FileInputStream(file);
        int readLen = 0;
        int calcLen = 0;
        long totalLen = file.length();
        while((readLen = fis.read(buffer))!=-1)
        {
        	calcLen += readLen;
        	md5.update(buffer, 0, readLen);
    		if(listener!=null && totalLen > 0)
    		{
				final int percent =(int) ((100 * calcLen / totalLen ));
				listener.onMD5Calculating(file,percent,0,0);
    		}
        }
        fis.close();// 务必关闭
        return byteArrayToHex(md5.digest());
    }
	
	// 单个文件的校验方法
	public static String calculateSingleFileMD5sum(File file) throws Exception
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
        FileInputStream  fis = new FileInputStream(file);
        int readLen = 0;
        byte[] buff = new byte[256];
        while((readLen = fis.read(buff))!=-1)
        {
        	md5.update(buff, 0, readLen);
        }
        fis.close();// 务必关闭
		StringBuilder sb = new StringBuilder();
		byte[] data = md5.digest();
		for (byte b : data ) 
		{
			sb.append(new Formatter().format("%02x", b));
		}
		return sb.toString();
    }
	
	// 因为文件可能比较大，MD5校验的过程会比较耗时所以最好在线程里完成这个工作
	class CheckThread extends Thread
	{
		File[] files;
		
		public CheckThread(File[] files)
		{
			this.files = files;
		}
		
		public void run()
		{
			if (files==null || files.length < 0)
				return;
			StringBuilder sb = new StringBuilder();
			Log.d(TAG,"FileMD5Checker:"+">>>开始文件MD5校验");
			for (File f : files)
			{
				if(!f.isFile())// 若不是文件，则忽略
					    continue;
				if(f.getName().equalsIgnoreCase(DefaultName))
						continue;
				try {
					String md5 = calculateMD5sum(f);// 把三种格式的类型都记录下来
					sb.append(f.getName().toUpperCase()+"-"+md5+"\n");// MD5文件的记录格式    文件名 - md5  '\n'
					sb.append(f.getName().toLowerCase()+"-"+md5+"\n");// MD5文件的记录格式    文件名 - md5  '\n'
					sb.append(f.getName()+"-"+md5+"\n");			  // MD5文件的记录格式    文件名 - md5  '\n'
				} 
				catch (Exception e)
				{
					if(listener!=null)
					{
						listener.onMD5CalculateException(f,e.getMessage());
					}
					Log.d(TAG,"FileMD5Checker:"+"!!!文件"+f.getName()+"MD5校验出错");
					return ;
				}
			}
			if (D) Log.d(TAG,"======------>MD5 校验结果: "+sb.toString());
			try {
				fous.write(sb.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(listener != null)// 校验完毕,返回结果
			{
				listener.onMD5CalculationCompleted(checkedFileList,getMD5Info(sb.toString()));
			}
			Log.d(TAG,"FileMD5Checker: "+"<<<文件MD5校验完毕!");
		}
	}
}

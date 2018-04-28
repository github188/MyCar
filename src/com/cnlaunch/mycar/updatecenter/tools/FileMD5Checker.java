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
 * �ļ���MD5У���࣬������֮ǰҪ�����е��ļ�����һ��MD5У��
 * ���Ұ�����������[����cookie������]���Ա���һ������ʹ��
 * �Լ���������������������Աȣ��ж������Ƿ�ɹ���
 * */
public class FileMD5Checker
{
	public final static String TAG = "FileMD5Checker";
	private boolean D = true;
	private   File recordFile;    //������¼MD5��Ϣ���ļ�
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
	
	private File[] checkedFileList;// ��У����ļ��б�
	
	public FileMD5Checker(File[] files,Listener lsn) 
	{
		this.checkedFileList = files;
		this.listener = lsn;
		if(checkedFileList!=null)
		{
			if (this.listener!=null)listener.onValidateMD5Info();
			if(checkedFileList.length > 0)
			{
			
				String path = checkedFileList[0].getParent();// ��ȡ��ǰ·��
				path = path.replace(checkedFileList[0].getName(), DefaultName);				
				recordFile = new File(path,DefaultName);// md5 ��¼�ļ��������ļ�λ��ͬһ��Ŀ¼��
				try 
				{
					recordFile.delete();
					fous  = new FileOutputStream(recordFile);
					if (listener!=null)
					{
						listener.onGeneratingMD5Record(recordFile);							
					}
					if(D)Log.e(TAG,"����MD5��¼��Ϣ�ļ�... " + recordFile.getName());
					new CheckThread(checkedFileList).start();
				} 
				catch (IOException e)
				{
					if (listener!=null)
					{
						listener.onMD5CalculateException(recordFile, "������¼�ļ�ʧ�� "+e.getMessage());							
					}
				}					
			}else
			{
				if (listener!=null)
				{
					listener.onMD5CalculateException(recordFile,"ָ�����ļ��б���ֵΪ0");							
				}
			}
		}
	}

	// ����MD5��Ϣ�ļ�
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
			String lineSplit = "\n";  // �зָ���
			String keyValueSplit = "-";// ��ֵ�Էָ���
			String[] strArray = infoString.split(lineSplit);
			
			for(int i=0;i<strArray.length;i++)
			{
				String[] key_value = strArray[i].split(keyValueSplit);// �ֽ��  �ļ���- MD5 ��ֵ��
				md5info.put(key_value[0], key_value[1]);
			}			
			return md5info;
		}
		return  new HashMap<String, String>();
	}
	// ����MD5��Ϣ�ļ�
	public  static HashMap<String,String>  getMD5Info(String str)
	{
		if( str != null )
		{
			HashMap<String, String> md5info = new HashMap<String, String>();
			String infoString = str;
			String lineSplit = "\n";  // �зָ���
			String keyValueSplit = "-";// ��ֵ�Էָ���
			String[] strArray = infoString.split(lineSplit);
			
			for(int i=0;i<strArray.length;i++)
			{
				String[] key_value = strArray[i].split(keyValueSplit);// �ֽ��  �ļ���- MD5 ��ֵ��
				md5info.put(key_value[0], key_value[1]);
			}			
			return md5info;
		}
		return  new HashMap<String, String>();
	}
	
	/**@author luxingsong
	 * ���ֽ�����ת��Ϊʮ�����Ƶ��ַ���
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
     * �����ļ���MD5У��ֵ
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
        fis.close();// ��عر�
        return byteArrayToHex(md5.digest());
    }
	
	// �����ļ���У�鷽��
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
        fis.close();// ��عر�
		StringBuilder sb = new StringBuilder();
		byte[] data = md5.digest();
		for (byte b : data ) 
		{
			sb.append(new Formatter().format("%02x", b));
		}
		return sb.toString();
    }
	
	// ��Ϊ�ļ����ܱȽϴ�MD5У��Ĺ��̻�ȽϺ�ʱ����������߳�������������
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
			Log.d(TAG,"FileMD5Checker:"+">>>��ʼ�ļ�MD5У��");
			for (File f : files)
			{
				if(!f.isFile())// �������ļ��������
					    continue;
				if(f.getName().equalsIgnoreCase(DefaultName))
						continue;
				try {
					String md5 = calculateMD5sum(f);// �����ָ�ʽ�����Ͷ���¼����
					sb.append(f.getName().toUpperCase()+"-"+md5+"\n");// MD5�ļ��ļ�¼��ʽ    �ļ��� - md5  '\n'
					sb.append(f.getName().toLowerCase()+"-"+md5+"\n");// MD5�ļ��ļ�¼��ʽ    �ļ��� - md5  '\n'
					sb.append(f.getName()+"-"+md5+"\n");			  // MD5�ļ��ļ�¼��ʽ    �ļ��� - md5  '\n'
				} 
				catch (Exception e)
				{
					if(listener!=null)
					{
						listener.onMD5CalculateException(f,e.getMessage());
					}
					Log.d(TAG,"FileMD5Checker:"+"!!!�ļ�"+f.getName()+"MD5У�����");
					return ;
				}
			}
			if (D) Log.d(TAG,"======------>MD5 У����: "+sb.toString());
			try {
				fous.write(sb.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(listener != null)// У�����,���ؽ��
			{
				listener.onMD5CalculationCompleted(checkedFileList,getMD5Info(sb.toString()));
			}
			Log.d(TAG,"FileMD5Checker: "+"<<<�ļ�MD5У�����!");
		}
	}
}

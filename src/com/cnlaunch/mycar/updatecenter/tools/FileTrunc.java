package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Formatter;

import android.util.Log;
/**
 * �ļ��и���
 * */
public class FileTrunc
{
    final static int PKG_SIZE = 4;
    final static String TAG = "FileTruncator";
    /**
     * ��ȡ�ļ�
     * @param input  ����ȡ���ļ�
     * @param output ��ȡ�õ����ļ�
     * @param from   ��ȡ��ʼ��λ��
     * @param to	  ��ȡ������λ��
     * @return		  ��ȡ�õ����ļ�
     */
    public static File trunc(File input,File output,long from,long to)
    {
        int pkg_sum = 0;
        long originFileLen = input.length();
        long outPutFileLent = to - from + 1;
        
        pkg_sum = (int) (outPutFileLent / PKG_SIZE);
        
        int restbytes = (int) (outPutFileLent % PKG_SIZE);
        
        Log.d(TAG,"originFileLen "+originFileLen);
        Log.d(TAG,"outPutFileLent "+outPutFileLent);
        Log.d(TAG,"restbytes "+restbytes);
        Log.d(TAG,"pkg_sum "+pkg_sum);
        
        output.delete();
        try
        {
            
        	FileOutputStream fos = new FileOutputStream(output);
            
            RandomAccessFile raf = new RandomAccessFile(input, "rw");
            
            raf.seek(from);
            
            byte[] buffer = new byte[PKG_SIZE];
            
            int count = 0;
            
            for(int i= 0;i< pkg_sum;i++)
            {
                count = raf.read(buffer);
                fos.write(buffer, 0, count);
            }
            
            if(restbytes > 0)
            {
//            	raf.seek(PKG_SIZE * pkg_sum);
            	raf.read(buffer);
            	fos.write(buffer, 0, restbytes);
            }
            
            raf.close();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return output;
    }
    
    /**
     * ���ݴ�����ַ�������Dpusysini�ļ�
     * @param fileContent
     * @param dpusysini
     * @return
     * @since DBS V100
     */
    public static File getDpusysini(String fileContent, File dpusysini)
    {

        byte[] buffer = fileContent.getBytes();
        dpusysini.delete();
        try
        {
            
            FileOutputStream fos = new FileOutputStream(dpusysini);
            fos.write(buffer);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return dpusysini;
    }
    /**
     * ��ȡ�ļ�β�� ��Ҫ�����豸�����ļ�
     * @param origin ����ȡ���ļ�
     * @param output ��ȡ�õ��������ļ�
     * @param len    ��ȡ���ļ�����
     * @return  �����ļ�
     */
    public static File truncTail(File origin,File output,long len)
    {
    	 byte[] temp = getByteRegion(origin, origin.length()-len, origin.length()-4);
    	 try
         {
             FileOutputStream fos = new FileOutputStream(output);
             fos.write(temp, 0, temp.length);
         }
         catch (FileNotFoundException e)
         {
             e.printStackTrace();
         }
         catch (IOException e)
         {
             e.printStackTrace();
         }
         return output;
    }
    /**
     * ��ȡ�ļ��е�ĳ���ֽ�����
     * @param file
     * @param from
     * @param to
     * @return
     */
    public static byte[] getByteRegion(File file,long from,long to)
    {
    	if(from > to) 
    		return null;
    	
    	byte[] result  = new byte[(int) (to - from )];
    	
    	try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(from);
			raf.read(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    /** Test Case
	 * @param args
	 */
	public static void main(String[] args)
	{
		File origin = new File("D:/download.bin");
		FileTrunc fc = new FileTrunc();
		fc.trunc(origin, new File("D:/some.ini"), origin.length() - 226, origin.length());
		byte[] res = fc.getByteRegion(origin, 0x10000, 0x10000 + 6);
		Log.d(TAG,"get bytes:"+ byteToHex(res));
	}
	
	public static String byteToHex(byte[] data)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : data )
		{
			sb.append(new Formatter().format("%02x-", b));
		}
		return sb.toString();
	}
}

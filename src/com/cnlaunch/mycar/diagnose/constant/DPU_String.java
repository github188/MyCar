package com.cnlaunch.mycar.diagnose.constant;

import java.util.ArrayList;
import java.util.Formatter;

import android.util.Log;

import com.cnlaunch.mycar.updatecenter.dpu.ProtocolUtils;
import com.cnlaunch.mycar.updatecenter.model.BreakPointInfo;

/**@author luxingsong
 * DPU字符串数据类型
 * @see DPU通信协议 v1.06
 * */
public final class DPU_String
{
    public static String TAG = "DPU_String";
	int len;
	int str_len;
	String str;
    public static int BASE_POS               = 9;    //参数的起止位置
    public static int PARA_LENGTH_BYTE_COUNT = 2;    // 参数长度的字节数
    public static int MD5_LENGTH             = 32 ;  // md5长度
	public DPU_String(String str)
	{
		this.str = str;
		this.str_len = str.length()+1;// '\0'字符也包括在内
		this.len = str.length()+3;
	}
	
	public byte[] toBytes()
	{
		byte[] len = new byte[2];
		len[0] = (byte) (this.str_len >> 8);
		len[1] = (byte) this.str_len;
		byte[] temp = append(len,str.getBytes());
		byte[] ret = append(temp,"\0".getBytes());// '\0'做字符串结束符,按C惯例
		return ret;
	}
	/**
	 * @author luxingsong
	 * 将DPU_String 字节数组转换为 String类
	 * */
	public static String asString(byte[] data)
	{
		if(data!=null && data.length >=3)
		{
			int len = data[0]<<8 | data[1];
			byte[] ret = new byte[len-1]; 
			System.arraycopy(data, 2, ret, 0, len-1);//copy source 		
			return new String(ret);
		}
		return null;
		
	}
	/**
	 * @author luxingsong
	 * 将DPU_String数组类 转换为 String数组类
	 * */
    public static ArrayList<String> toStringArray(byte[] data)
    {
    	if(data!=null){
    		int total_bytes = data.length;
    		if(total_bytes >= 3){// 最少一个DPU_String
    			int walkthrough = 0;
    			ArrayList<String> result_strings = new ArrayList<String>();
    			while(walkthrough < (total_bytes - 1))
    			{
    				int temp_len = data[walkthrough]<< 8 | data[walkthrough+1];
    				byte[] str_bytes = new byte[temp_len-1];
    				System.arraycopy(data, walkthrough+2, str_bytes, 0,temp_len-1);
    				result_strings.add(new String(str_bytes));
    				walkthrough += temp_len+2;// 忽略2个头字节
    			}
    			return result_strings;
    		}
    	}
    	return null;
    }
    
    public static ArrayList<String> byteToDpuStringArray(byte[] data)
    {
        if (data == null)
        {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>();
        
        return result;
        
        
    }
    
	public int getLength()
	{
		return len;
	}
	
	public static  byte[] append(byte[] src,byte[] data)
	{
		 if(src.length>0 && data.length>0)
		 {
			 byte[] ret = new byte[src.length+data.length];
			 System.arraycopy(src, 0, ret, 0, src.length);//copy source 
			 System.arraycopy(data, 0, ret, src.length, data.length);//copy data
			 return ret;
		 }
		 throw new IllegalArgumentException("byte arguments error");
	}
	
	public static String bytesToHex(byte[] data)
	{
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(new Formatter().format("%02x-", b));
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return "DPU_String [len=" + len + ", str=" + str + "]";
	}
	
	public static void main(String[] args)
	{
		System.out.println("-------------------------- test DPUbytesToString()---------------------");
		DPU_String hello = new DPU_String("Hello");
		System.out.println(DPU_String.asString(hello.toBytes()));
		System.out.println("=====================test DPUStringArrayToStringArray(byte[] data)==========================");
		DPU_String[] da = new DPU_String[]
		{
				new DPU_String("this"),
				new DPU_String("may"),
				new DPU_String("fun"),
				new DPU_String("Gooo!"),
		};
		byte[] dpu_da = da[0].toBytes();
		for (int i = 1; i < da.length; i++) 
		{
			dpu_da = DPU_String.append(dpu_da, da[i].toBytes());
		}
		System.out.println(DPU_String.bytesToHex(dpu_da));
		ArrayList<String> sa = DPU_String.toStringArray(dpu_da);
		System.out.println(sa.size());
		for (int i = 0; i < sa.size(); i++)
		{
			System.out.println(":"+sa.get(i));
		}
	}
	  /**
     * 解析断点信息
     * @param buffer
     * @return
     * @since DBS V100
     */
    public static BreakPointInfo getBreakPointInfo(byte[] buffer)
    {
        if (buffer == null || buffer.length < 38)
        {
            return null;
        }
        BreakPointInfo breakPointInfo = new BreakPointInfo();
        int startPos = BASE_POS + PARA_LENGTH_BYTE_COUNT; // 起始开始截取参数内容的位置
        int length = 0;
        // 第一步：解析出软件名称
        length = buffer[startPos - 1]; // 软件名称参数长度
        byte[] parmByte = new byte[length]; // 软件名称参数内容字节数组
        if (buffer.length >= startPos + length)
        {
            System.arraycopy(buffer, startPos, parmByte, 0, length);
        }
        breakPointInfo.softName = new String(parmByte).trim();

        // 第二步：解析出软件版本
        startPos += length;
        length = buffer[startPos + 1]; // 软件名称参数长度
        parmByte = new byte[length]; // 软件名称参数内容字节数组
        if (buffer.length >= startPos + length)
        {
            System.arraycopy(buffer, startPos, parmByte, 0, length);
        }
        breakPointInfo.softVersion = new String(parmByte).trim();

        // 第三步：解析出软件语言
        startPos += length + 3;
        length = buffer[startPos]; // 软件名称参数长度
        parmByte = new byte[length]; // 软件名称参数内容字节数组
        if (buffer.length >= startPos + length)
        {
            System.arraycopy(buffer, startPos, parmByte, 0, length);
        }
        breakPointInfo.softLanguage = new String(parmByte).trim();

        // 第四步: 是否升级完成
        startPos += length + 1;
        if (buffer.length >= startPos)
        {
            breakPointInfo.isUpdated = (buffer[startPos] == 0 ? true : false);
        }

        // 第五步：已经传输完成的文件数
        startPos += 2;
        if (buffer.length >= startPos)
        {
            breakPointInfo.completeFileCount = buffer[startPos] & 0xFF;
        }

        // 第六步：已经完成的文件的名字
        ArrayList<String> fileNameArray = new ArrayList<String>();
        int fileCount = breakPointInfo.completeFileCount;
        if (fileCount > 0)
        {
            for (int i = 0; i < fileCount; i++)
            {
                if (i == 0) // 第一个文件
                {
                    startPos += 2;
                }
                else
                // 其他文件
                {
                    startPos += length + 2;
                }
                length = buffer[startPos] & 0xFF;
                parmByte = new byte[length]; // 软件名称参数内容字节数组
                if (buffer.length >= startPos + length)
                {
                    System.arraycopy(buffer, startPos, parmByte, 0, length);
                }
                fileNameArray.add(new String(parmByte).trim());
            }
            breakPointInfo.fileArray = fileNameArray;
            startPos += length + 2;
        }
        else
        {
            startPos += 2; 
        }

        // 第七步：正在接受的文件
        
        length = buffer[startPos]; // 软件名称参数长度
        parmByte = new byte[length]; // 软件名称参数内容字节数组
        if (buffer.length >= startPos + length)
        {
            System.arraycopy(buffer, startPos, parmByte, 0, length);
            breakPointInfo.updatingFileName = new String(parmByte).trim();
        }
        boolean isHas = false;
        if (fileNameArray != null && fileNameArray.size() > 0)
        {
            for (int k = 0; k < fileNameArray.size(); k++)
            {
                if (fileNameArray.get(k).equals(breakPointInfo.updatingFileName))
                {
                    isHas = true;
                    break;
                }
            }
        }

        if (isHas)
        {
            breakPointInfo.receivedByteCount = 0l;
        }
        else
        {
            startPos += length + 4;
            if (startPos < buffer.length)
            {
                breakPointInfo.receivedByteCount = new Long((buffer[startPos] & 0xFF) + (buffer[startPos - 1] & 0xFF) * 0x100 + (buffer[startPos - 2] & 0xFF) * 0x100 * 0x100
                    + (buffer[startPos - 3] & 0xFF) * 0x100 * 0x100 * 0x100);
            }
        }

        // 第七部：已接受的MD5
        startPos += 1;
        parmByte = new byte[MD5_LENGTH]; // 软件名称参数内容字节数组
        if (buffer.length >= startPos + MD5_LENGTH)
        {
            System.arraycopy(buffer, startPos, parmByte, 0, MD5_LENGTH);
        }
        breakPointInfo.md5 = new String(parmByte).trim();
        return breakPointInfo;
    }
}

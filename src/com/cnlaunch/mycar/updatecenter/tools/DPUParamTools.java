package com.cnlaunch.mycar.updatecenter.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.cnlaunch.mycar.diagnose.constant.DPU_Long;
import com.cnlaunch.mycar.diagnose.constant.DPU_Short;
import com.cnlaunch.mycar.diagnose.constant.DPU_String;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.updatecenter.model.BreakPointInfo;
/**
 * 
 * DPU 参数转换工具
 * */
public class DPUParamTools
{
	public final static String TAG = "DPUParamTools";

	/**
	 * 获得升级文件列表信息字节数组
	 * @param vechicleType
	 * @param version
	 * @param language
	 * @param fileCount
	 * @param totalSize
	 * @param isForceUpdate
	 * @param needNotUpdateCount
	 * @param needNotFileName
	 * @return
	 * @since DBS V100
	 */ 
	public static byte[] getUpdateInfoBytes(String vechicleType,String version, String language, int fileCount, long totalSize, boolean isForceUpdate, int needNotUpdateCount, ArrayList<String> needNotFileName)
	{
	    // 第一步:计算命令长度
	    int commandLength = 0;
	       // 车型名称
        DPU_String vechicle = new DPU_String(vechicleType.toUpperCase());
        // 版本号
        DPU_String versionNo = new DPU_String(version.toUpperCase());
        // 程序语言
        DPU_String langCode = new DPU_String(language.toUpperCase());
        commandLength = vechicle.getLength() + versionNo.getLength() + langCode.getLength();
        ArrayList<DPU_String> needNotUpdateFileArray = null;

        if (needNotFileName != null && needNotFileName.size() > 0)
        {
            needNotUpdateFileArray = new ArrayList<DPU_String>();
            for (String name : needNotFileName)
            {
                DPU_String fileName = new DPU_String(name.toUpperCase());
                commandLength += fileName.getLength();
                needNotUpdateFileArray.add(fileName);
            }
        }
        commandLength += 9;
        int index = 0;
        byte[] params = new byte[commandLength];// 参数设置区
        // 车型名称
        System.arraycopy(vechicle.toBytes(), 0, params, index,vechicle.getLength());
        index += vechicle.getLength();
        // 版本号
        System.arraycopy(versionNo.toBytes(), 0, params, index,versionNo.getLength());
        index += versionNo.getLength();
        // 语言类型
        System.arraycopy(langCode.toBytes(), 0, params, index,langCode.getLength());
        index += langCode.getLength();

        // 文件数量 [取决于文件列表大小]
        params[index++] = (byte) (fileCount >> 8);
        params[index++] = (byte) fileCount;
        // 文件总长度
        params[index++] = (byte) (totalSize >> 24);
        params[index++] = (byte) (totalSize >> 16);
        params[index++] = (byte) (totalSize >> 8);
        params[index++] = (byte) (totalSize);
        // 是否强制升级
        if (isForceUpdate)
        {
            params[index++] = 0x01;
        }
        else
        {
            params[index++] = 0x00;
        }
        // 不用升级的文件 0 代表没有指定任何不必升级的文件
        params[index++] = (byte) (needNotUpdateCount >> 8);
        params[index++] = (byte) (needNotUpdateCount);
        if (needNotUpdateFileArray != null && needNotUpdateFileArray.size() > 0)
        {
            for (DPU_String dpu_String : needNotUpdateFileArray)
            {
                System.arraycopy(dpu_String.toBytes(), 0, params, index,dpu_String.getLength());
                index += dpu_String.getLength();
            }
        }
        return params;
	}
	
	    
	
	/**
	 * 针对 2401  命令的参数转换工具
	 * @param fileList
	 * @param vehiecle
	 * @param version
	 * @param language
	 * @return
	 */
	public static byte[] convert0(ArrayList<File> fileList, String vehiecle,
			String version, String language) 
	{
		if (vehiecle == null)
			throw new NullPointerException("升级参数 vendor = 空指针!");
		if (version == null)
			throw new NullPointerException("升级参数 version = 空指针!");
		if (language == null)
			throw new NullPointerException("升级参数 language = 空指针!");
		if (fileList == null)
			throw new NullPointerException("升级参数 fileList = 空指针!");
		
		if(language.equals(""))
		{
			language = "CN";// 默认是中文
		}
		// 车型名称
		DPU_String vechicle = new DPU_String(vehiecle.toUpperCase());
		// 版本号
		DPU_String versionNo = new DPU_String(version.toUpperCase());
		// 程序语言
		DPU_String langCode = new DPU_String(language.toUpperCase());
		// 文件数量
		int fileNum = fileList.size();
		// 全部升级文件大小
		int fileTotalSize = (int) FileLengthUtil.calcTotalBytesInFileList(fileList);
		// 强制升级
		byte forceUpdate = 0x01;
		// 根据协议 除了DPU_String类型的长度是可变之外，其余的9个字节是固定的
		int param_total_len = vechicle.getLength() + versionNo.getLength()
				+ langCode.getLength() + 9;

		int index = 0;
		byte[] params = new byte[param_total_len];// 参数设置区
		// 车型名称
		System.arraycopy(vechicle.toBytes(), 0, params, index,vechicle.getLength());
		index += vechicle.getLength();
		// 版本号
		System.arraycopy(versionNo.toBytes(), 0, params, index,versionNo.getLength());
		index += versionNo.getLength();
		// 语言类型
		System.arraycopy(langCode.toBytes(), 0, params, index,langCode.getLength());
		index += langCode.getLength();

		// 文件数量 [取决于文件列表大小]
		params[index++] = (byte) (fileNum >> 8);
		params[index++] = (byte) fileNum;
		// 文件总长度
		params[index++] = (byte) (fileTotalSize >> 24);
		params[index++] = (byte) (fileTotalSize >> 16);
		params[index++] = (byte) (fileTotalSize >> 8);
		params[index++] = (byte) (fileTotalSize);
		// 是否强制升级
		params[index++] = (byte) (forceUpdate);
		// 不用升级的文件 0 代表没有指定任何不必升级的文件
		params[index++] = (byte) (0);
		params[index++] = (byte) (0);
		return params;
	}
	
	/**
	 * 针对 2402命令的参数转换
	 * @param name 文件名
	 * @param file 文件对象，提供文件长度信息
	 * @return
	 */
	public static byte[] fileNameAndLength(String name,File file)
	{
		if(name ==null || file ==null)
			throw new NullPointerException("file name and file obj should not be null!");
		
		DPU_String fileName = new DPU_String(name.toUpperCase());// 文件名称,都是大写
		DPU_Long file_len = new DPU_Long(file.length());// 文件长度
		byte[] params = OrderUtils.appendByteArray(fileName.toBytes(),
									file_len.toBytes());
		return params;
	}
	
	/**
	 * 针对 2112命令的参数转换
	 * @param file 文件对象，需要文件名以及文件长度信息
	 * @return
	 */
	public static byte[] dpuSysIniInfo(File file)
	{
		Log.d(TAG,"dpuSysIniInfo() file len :"+ file.length()+"Bytes");
		byte[] buff = new byte[(int)file.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			int count = 0;
			while((count = fis.read(buff))!=-1);
			Log.d(TAG,"dpuSysIniInfo() buff :"+ count+"Bytes");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DPU_Short file_len = new DPU_Short((short)file.length());// 文件长度
		byte[] params = OrderUtils.appendByteArray(file_len.toBytes(),buff);
		return params;
	}
	
	/**
	 * 2403 命令需要的参数
	 * @param writePos
	 * @param dataChunk
	 * @param dataLen
	 * @return
	 */
	public static byte[] dataChunkParams(long writePos,byte[] dataChunk, int dataLen) 
	{
		DPU_Long write_pos = new DPU_Long(writePos);// 写入位置
		DPU_Short data_len = new DPU_Short((short) dataLen);// 数据长度
		byte[] params = null;
		params = OrderUtils.appendByteArray(write_pos.toBytes(),data_len.toBytes());
		params = OrderUtils.appendByteArray(params, dataChunk);
		return params;
	}
	
	public static byte[] toDPUStringBytes(String str)
	{
		DPU_String dstr = new DPU_String(str);
		return dstr.toBytes();
	}
	/**
	 * 连接校验字 2503
	 * @param level
	 * @param checksum
	 * @return
	 */
	public static byte[] connectChecksumLevel2(byte[] checksum)
	{
		byte[] level = new byte[]{0x02};
		byte[] params = null;
		params = OrderUtils.appendByteArray(level,checksum);
		return params;
	}
	

}

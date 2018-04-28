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
 * DPU ����ת������
 * */
public class DPUParamTools
{
	public final static String TAG = "DPUParamTools";

	/**
	 * ��������ļ��б���Ϣ�ֽ�����
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
	    // ��һ��:���������
	    int commandLength = 0;
	       // ��������
        DPU_String vechicle = new DPU_String(vechicleType.toUpperCase());
        // �汾��
        DPU_String versionNo = new DPU_String(version.toUpperCase());
        // ��������
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
        byte[] params = new byte[commandLength];// ����������
        // ��������
        System.arraycopy(vechicle.toBytes(), 0, params, index,vechicle.getLength());
        index += vechicle.getLength();
        // �汾��
        System.arraycopy(versionNo.toBytes(), 0, params, index,versionNo.getLength());
        index += versionNo.getLength();
        // ��������
        System.arraycopy(langCode.toBytes(), 0, params, index,langCode.getLength());
        index += langCode.getLength();

        // �ļ����� [ȡ�����ļ��б��С]
        params[index++] = (byte) (fileCount >> 8);
        params[index++] = (byte) fileCount;
        // �ļ��ܳ���
        params[index++] = (byte) (totalSize >> 24);
        params[index++] = (byte) (totalSize >> 16);
        params[index++] = (byte) (totalSize >> 8);
        params[index++] = (byte) (totalSize);
        // �Ƿ�ǿ������
        if (isForceUpdate)
        {
            params[index++] = 0x01;
        }
        else
        {
            params[index++] = 0x00;
        }
        // �����������ļ� 0 ����û��ָ���κβ����������ļ�
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
	 * ��� 2401  ����Ĳ���ת������
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
			throw new NullPointerException("�������� vendor = ��ָ��!");
		if (version == null)
			throw new NullPointerException("�������� version = ��ָ��!");
		if (language == null)
			throw new NullPointerException("�������� language = ��ָ��!");
		if (fileList == null)
			throw new NullPointerException("�������� fileList = ��ָ��!");
		
		if(language.equals(""))
		{
			language = "CN";// Ĭ��������
		}
		// ��������
		DPU_String vechicle = new DPU_String(vehiecle.toUpperCase());
		// �汾��
		DPU_String versionNo = new DPU_String(version.toUpperCase());
		// ��������
		DPU_String langCode = new DPU_String(language.toUpperCase());
		// �ļ�����
		int fileNum = fileList.size();
		// ȫ�������ļ���С
		int fileTotalSize = (int) FileLengthUtil.calcTotalBytesInFileList(fileList);
		// ǿ������
		byte forceUpdate = 0x01;
		// ����Э�� ����DPU_String���͵ĳ����ǿɱ�֮�⣬�����9���ֽ��ǹ̶���
		int param_total_len = vechicle.getLength() + versionNo.getLength()
				+ langCode.getLength() + 9;

		int index = 0;
		byte[] params = new byte[param_total_len];// ����������
		// ��������
		System.arraycopy(vechicle.toBytes(), 0, params, index,vechicle.getLength());
		index += vechicle.getLength();
		// �汾��
		System.arraycopy(versionNo.toBytes(), 0, params, index,versionNo.getLength());
		index += versionNo.getLength();
		// ��������
		System.arraycopy(langCode.toBytes(), 0, params, index,langCode.getLength());
		index += langCode.getLength();

		// �ļ����� [ȡ�����ļ��б��С]
		params[index++] = (byte) (fileNum >> 8);
		params[index++] = (byte) fileNum;
		// �ļ��ܳ���
		params[index++] = (byte) (fileTotalSize >> 24);
		params[index++] = (byte) (fileTotalSize >> 16);
		params[index++] = (byte) (fileTotalSize >> 8);
		params[index++] = (byte) (fileTotalSize);
		// �Ƿ�ǿ������
		params[index++] = (byte) (forceUpdate);
		// �����������ļ� 0 ����û��ָ���κβ����������ļ�
		params[index++] = (byte) (0);
		params[index++] = (byte) (0);
		return params;
	}
	
	/**
	 * ��� 2402����Ĳ���ת��
	 * @param name �ļ���
	 * @param file �ļ������ṩ�ļ�������Ϣ
	 * @return
	 */
	public static byte[] fileNameAndLength(String name,File file)
	{
		if(name ==null || file ==null)
			throw new NullPointerException("file name and file obj should not be null!");
		
		DPU_String fileName = new DPU_String(name.toUpperCase());// �ļ�����,���Ǵ�д
		DPU_Long file_len = new DPU_Long(file.length());// �ļ�����
		byte[] params = OrderUtils.appendByteArray(fileName.toBytes(),
									file_len.toBytes());
		return params;
	}
	
	/**
	 * ��� 2112����Ĳ���ת��
	 * @param file �ļ�������Ҫ�ļ����Լ��ļ�������Ϣ
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
		DPU_Short file_len = new DPU_Short((short)file.length());// �ļ�����
		byte[] params = OrderUtils.appendByteArray(file_len.toBytes(),buff);
		return params;
	}
	
	/**
	 * 2403 ������Ҫ�Ĳ���
	 * @param writePos
	 * @param dataChunk
	 * @param dataLen
	 * @return
	 */
	public static byte[] dataChunkParams(long writePos,byte[] dataChunk, int dataLen) 
	{
		DPU_Long write_pos = new DPU_Long(writePos);// д��λ��
		DPU_Short data_len = new DPU_Short((short) dataLen);// ���ݳ���
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
	 * ����У���� 2503
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

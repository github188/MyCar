package com.cnlaunch.mycar.obd2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <功能简述>用于数据传递 <功能详细描述>
 * 
 * @author huangweiyong
 * @version 1.0 2012-5-12
 * @since DBS V100
 */
public class DataExchange {
	// 用于读全部数据流PID参数
	private static byte[] pids = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00,};
	// 用于读全部数据流PID参数（带长度字节）
	private static byte[] pidNumAndPids = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};
	// 用于读全部数据流由pid得到的数据流计算参数（带长度字节）
	private static byte[] pidConvertedIntoDataStreamData = {0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};

	private static byte[] faultCodeContentData = {0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};
	private static byte[] VINContentData = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};

	private static byte[] pidsMeter = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};

	private static String engineSpeed = null;

	public static String getEngineSpeed() {
		return engineSpeed;
	}

	public static void setEngineSpeed(String engineSpeed) {
		DataExchange.engineSpeed = engineSpeed;
	}

	/****************** 接收蓝牙发来的指令 **********************/
	private static byte[] receiveDataFromBluetooth = {0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};

	public static byte[] getReceiveDataFromBluetooth() {
		return receiveDataFromBluetooth;
	}

	public static void setReceiveDataFromBluetooth(
			byte[] receiveDataFromBluetooth) {
		DataExchange.receiveDataFromBluetooth = receiveDataFromBluetooth;
	}

	/****************************************/

	private static String test;

	public static String getTest() {
		return test;
	}

	public static void setTest(String test) {
		DataExchange.test = test;
	}

	public static byte[] getPidsMeter() {
		return pidsMeter;
	}

	public static Map getPidsMap()
	{
	    Map pidsMap = new HashMap();
	    if (pidsMeter != null && pidsMeter.length > 0)
	    {
	        for (byte pid : pidsMeter)
            {
                pidsMap.put(pid, true);
            }
	        
	    }
	    else
	    {
	        return null;
	    }
	    return pidsMap;
	}
	public static byte[] getSupportPids(byte[] pids)
	{
	    ArrayList pidArray = new ArrayList();
	    if (pids != null && pids.length > 0)
	    {
	        for (byte pid : pids)
	        {
	            if(getPidsMap().get((byte)pid) != null && (Boolean)getPidsMap().get((byte)pid))
	            {
	                pidArray.add(pid);
	            }
	        }
	    }
	    else
	    {
	        return null;
	    }
	    byte[] pidBytes = null;
        if (pidArray.size() > 0)
        {
            pidBytes = new byte[pidArray.size()];

            for (int i = 0 ; i < pidArray.size(); i++)
            {
                pidBytes[i] = (Byte)pidArray.get(i);
            }
        }
        return pidBytes;
	}
    public static byte[] getNotSupportPids(byte[] pids)
    {
        ArrayList pidArray = new ArrayList();
        if (pids != null && pids.length > 0)
        {
            for (byte pid : pids)
            {
                if(getPidsMap().get((byte)pid) == null || !(Boolean)getPidsMap().get((byte)pid))
                {
                    pidArray.add(pid);
                }
            }
        }
        else
        {
            return null;
        }
        byte[] pidBytes = null;
        if (pidArray.size() > 0)
        {
            pidBytes = new byte[pidArray.size()];

            for (int i = 0 ; i < pidArray.size(); i++)
            {
                pidBytes[i] = (Byte)pidArray.get(i);
            }
        }
        return pidBytes;
    }
	public static void setPidsMeter(byte[] pidsMeter) {
		DataExchange.pidsMeter = pidsMeter;
	}

	public static byte[] getVINContentData() {
		return VINContentData;
	}

	public static void setVINContentData(byte[] vINContentData) {
		VINContentData = vINContentData;
	}

	public static byte[] getPidConvertedIntoDataStreamData() {
		return pidConvertedIntoDataStreamData;
	}

	public static byte[] getFaultCodeContentData() {
		return faultCodeContentData;
	}

	public static void setFaultCodeContentData(byte[] faultCodeContentData) {
		DataExchange.faultCodeContentData = faultCodeContentData;
	}

	public static void setPidConvertedIntoDataStreamData(
			byte[] pidConvertedIntoDataStreamData) {
		DataExchange.pidConvertedIntoDataStreamData = pidConvertedIntoDataStreamData;
	}

	public static byte[] getPids() {
		return pids;
	}

	public static void setPids(byte[] pids) {
		DataExchange.pids = pids;
	}

	public static byte[] getPidNumAndPids() {
		return pidNumAndPids;
	}

	public static void setPidNumAndPids(byte[] pidNumAndPids) {
		DataExchange.pidNumAndPids = pidNumAndPids;
	}

	public static byte[] getRead() {
		return read;
	}

	public static int getReadNum() {
		return readNum;
	}

	public static byte[] getSend() {
		return send;
	}

	public static int getSendNum() {
		return sendNum;
	}

	public static byte[] read = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00,};
	public static int readNum = 0;
	// 传递参数
	public static byte[] send = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00,};
	public static int sendNum = 0;

	public static void setRead(byte[] read) {
		DataExchange.read = read;
	}

	public static void setReadNum(int readNum) {
		DataExchange.readNum = readNum;
	}

	public static void setSend(byte[] send) {
		DataExchange.send = send;
	}

	public static void setSendNum(int sendNum) {
		DataExchange.sendNum = sendNum;
	}
}

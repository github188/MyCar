package com.cnlaunch.mycar.diagnose.constant;

/**
 * @author luxingsong
 * DPU �Ķ���������
 * @see DPUͨ��Э�� v1.06
 * */
public final class DPU_Short
{
	public final static int TYPE_LEN = 2;// 2 �ֽ� 
	short value;
	
	public DPU_Short(short i)
	{
		this.value = i;
	}
	
	public byte[] toBytes()
	{
		byte[] ret = new byte[TYPE_LEN];
		ret[0] = (byte) (value >> 8);
		ret[1] = (byte) (value);
		return ret;
	}
	public int getLength()
	{
		return 2;
	}
	
	public static short bytesToDPUShort(byte[] data)
	{
		short val = (short) ((data[0]&0xff)<<8 | (data[1]&0xff));
		return val;
	}
	// test 
	public static void main(String[] args)
	{
		DPU_Short sh = new DPU_Short((short) 123);
		System.out.println(DPU_Short.bytesToDPUShort(sh.toBytes()));
	}
}

package com.cnlaunch.bluetooth.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.cnlaunch.bluetooth.BluetoothDeviceListActivity;
import com.cnlaunch.bluetooth.service.BluetoothAdapterService.BlueStateEvent;

public class BluetoothDataService extends Observable implements BluetoothAdapterService.BlueCallback{
	//debug
	private static final String TAG = "BluetoothDataService";
	private static final boolean D = true;
	//单例此蓝牙复位
	private static BluetoothDataService m_bluetooth_service = null;
	//蓝牙服务实例
	private BluetoothAdapterService m_bluetooth_adapter_service = null;
	// 当前蓝牙名字
	private String m_now_bluetoothname = null;
	private String m_now_bluetoothmac = null;
	private boolean m_show_connect_activity = false; //连接状态
	//超时控制变量
	private static final int TIME_MAX = 10; 		//设置默认超时时间,为100ms的整数倍
	private static final int TIME_MAX_SEND = 5; 	//超时重发次数
	private int m_time_send = 0;					//超时重发次数计数器
	private boolean m_time_switch = false;  		//超时控制开关
	private int m_time_counter = 0;					//超时计数器
	private int m_time_max = 0;						//超时等待时间 
	//定时器
	private Timer m_timer = null;
	private TimerTask m_TimerTask;
	//蓝牙状态封装
	public BlueStateEvent m_blue_state = null;
	//蓝牙上一次状态
	public BlueStateEvent m_blue_state_last = null;
	//观察者对象列表
	private ArrayList<BluetoothInterface> m_observer_list = null;
	//蓝牙连接界面专用
	private boolean m_teshu_observer_switch = false;  //当为true时候，屏蔽其他观察者
	private BluetoothInterface m_teshu_observer = null; //特色观察者,当上面值为true时，只给这一个观察者发送消息
	//蓝牙观察者
	Intent m_bluetooth_intent = null;
	
	//单例实现
	public synchronized static BluetoothDataService getInstance()
	{
		if(m_bluetooth_service == null)
			m_bluetooth_service = new BluetoothDataService();
		return m_bluetooth_service;	
	}
	//构造方法
	private BluetoothDataService()
	{
		m_bluetooth_adapter_service = BluetoothAdapterService.getInstance();
		m_bluetooth_adapter_service.AddBlueCaalback(this);
		m_blue_state = m_bluetooth_adapter_service.m_blue_state;  //初始化蓝牙状态
		// 初始化定时器
		m_timer = new Timer();
		m_TimerTask = new TimerTask() 
		{
			@Override
			public void run() 
			{
			//TODO 自动运行，每隔200毫秒自动运行一次
				if(m_time_switch == true)
				{
					m_time_counter ++;
					if(m_time_counter >= m_time_max) //超时时间到
					{
						m_time_send ++;
						m_time_counter = 0;//复位计数器
						if(m_time_send == TIME_MAX_SEND) //达到超时次数
						{
							if(D) Log.i(TAG,"接收超时，模式：" + m_send_mode);
							m_time_switch = false; //关闭超时计数器
							if(m_send_mode == CMD_OneToOne)
								m_send_mode = CMD_NONE; //复位模式
							NotifyGetDataTimeout();
						}
						else
						{
							SendCmd(m_sendbuf, m_sendbuflen); //超时重发
						}
					}
				}
			}
		};
		m_timer.schedule(m_TimerTask, 100, 200); //设置定时器，延时100毫秒执行，每隔200毫秒触发一次。	
		//初始化观察者列表
		m_observer_list = new ArrayList<BluetoothInterface>();
	}
	//************************************************************************//
	//发送命令模式
	public static final int CMD_NONE  	  =	0; 			// 不接收数据等待状态
	public static final int CMD_OneToOne  = 1; 			// 发一帧收一帧
	public static final int CMD_OneToNone = 2; 			// 发一帧不接收
	public static final int CMD_OneToMore = 3; 			// 发一帧收多帧
	public static final int CMD_ReadMode  =	4; 			// 只收数据、监听模式	
	//发送数据模式
	private int m_send_mode = CMD_NONE;
	// 定义发送数据缓冲区
	private final static int m_recvbuff_len = 8*1024;	//数据缓冲区长度
	private byte[] m_sendbuf = new byte[m_recvbuff_len];
	private int m_sendbuflen = 0; // 发送数据长度
	// 计数器
	private byte m_counter = 0; // 循环计数器
	private byte[] m_address = { (byte) 0xF0, (byte) 0xF8 }; // 定义上位机和下位机地址
	
	// 蓝牙数据过滤相关变量
	
	private byte[] 	m_recvbuff = new byte[m_recvbuff_len]; 	// 接收管道缓冲区
	private int 	m_recvbuffStartAddress = 0; 		// 接收管道头地址
	private int 	m_recvbuffDataLength = 0; 			// 接收管道数据长度

	//********************************接口函数*********************************//
	//获取当前连接蓝牙名称
	public String GetConnectedBluetoothName()
	{
		if(m_blue_state.IsConnected())
			return m_now_bluetoothname;
		else
			return null;
	}
	//获取当前连接蓝牙MAC地址
	public String GetConnectedBluetoothMac()
	{
		if(m_blue_state.IsConnected())
			return m_now_bluetoothmac;
		else
			return null;
	}
	//设置当前蓝牙名称和地址
	public boolean SetConnectedBluetooth(String name,String mac)
	{
		if(name.length() > 0)
			m_now_bluetoothname = name;
		else
			return false;
		if(mac.length() == 17)
			m_now_bluetoothmac = mac;
		else
			return false;
		return true;
	}
	//显示蓝牙连接界面
	public void ShowBluetoothConnectActivity(Activity activity)
	{
		if(m_show_connect_activity) 
			return;
		m_show_connect_activity = true; //防止多次重复调用
		if(m_bluetooth_intent == null)
		{
			m_bluetooth_intent = new Intent(activity, BluetoothWatch.class);
			activity.startService(m_bluetooth_intent);
		}
		//获取当前的activity
		Intent intent = new Intent(activity, BluetoothDeviceListActivity.class);
		activity.startActivity(intent);
	}
	//添加观察者
	public synchronized void AddObserver(BluetoothInterface observer)
	{
		if(observer != null)
			m_observer_list.add(observer);
	}
	//删除观察者
	public synchronized void DelObserver(BluetoothInterface observer)
	{
		if(observer != null)
			m_observer_list.remove(observer);
	}
	//通知观察者蓝牙中断
	private synchronized void NotifyBlueConnectLost()
	{
		for(int i = 0; i < m_observer_list.size(); i++)
		{
			BluetoothInterface v_interface = m_observer_list.get(i);
			if(m_teshu_observer_switch == true)
			{
				if(v_interface == m_teshu_observer)
					v_interface.BlueConnectLost(m_now_bluetoothname,m_now_bluetoothmac);
			}
			else
			{
				if(v_interface != null)
					v_interface.BlueConnectLost(m_now_bluetoothname,m_now_bluetoothmac);
			}				
		}
	}
	//通知蓝牙连接成功
	private synchronized void NotifyBlueConnected()
	{
		for(int i = 0; i < m_observer_list.size(); i++)
		{
			BluetoothInterface v_interface = m_observer_list.get(i);
			if(m_teshu_observer_switch == true)
			{
				if(v_interface == m_teshu_observer)
					v_interface.BlueConnected(m_now_bluetoothname,m_now_bluetoothmac);
			}
			else
			{
				if(v_interface != null)
					v_interface.BlueConnected(m_now_bluetoothname,m_now_bluetoothmac);
			}
		}
	}
	//通知收到的数据
	private synchronized void NotifyGetDataFromService(byte[] databuf,int datalen)
	{
		for(int i = 0; i < m_observer_list.size(); i++)
		{
			BluetoothInterface v_interface = m_observer_list.get(i);
			if(m_teshu_observer_switch == true)
			{
				if(v_interface == m_teshu_observer)
					v_interface.GetDataFromService(databuf, datalen);
			}
			else
			{
				if(v_interface != null)
					v_interface.GetDataFromService(databuf, datalen);
			}
		}
			
	}
	//通知接收数据超时
	private synchronized void NotifyGetDataTimeout()
	{
		for(int i = 0; i < m_observer_list.size(); i++)
		{
			BluetoothInterface v_interface = m_observer_list.get(i);
			if(m_teshu_observer_switch == true)
			{
				if(v_interface == m_teshu_observer)
					v_interface.GetDataTimeout();
			}
			else
			{
				if(v_interface != null)
					v_interface.GetDataTimeout();
			}
		}
	}
	//通知观察者蓝牙连接界面返回了
	private synchronized void NotifyBlueConnectClose()
	{
		for(int i = 0; i < m_observer_list.size(); i++)
		{
			BluetoothInterface v_interface = m_observer_list.get(i);
			if(v_interface != null)
					v_interface.BlueConnectClose();
		}
	}
	//结束蓝牙服务 
	public void StopBlueService(Activity activity)
	{
		//终止服务
		if(m_bluetooth_intent != null)
		{
			activity.stopService(m_bluetooth_intent);
			m_bluetooth_intent = null;
		}
		m_bluetooth_adapter_service.StopService();
	}
	//设置是否已经显示蓝牙连接界面
	public synchronized void SetShowConnectActivity(boolean show)
	{
		m_show_connect_activity = show;
	}
	public boolean IsConnected()
	{
		return m_blue_state.IsConnected();
	}
	//重置超时
	public void ResetTimeOut()
	{
		m_time_switch = false;
		m_time_counter = 0;
	}
	//设置特殊观察者，当设置为true时，只给该观察者发送消息
	public void SetSpecialObserver(boolean Pswitch,BluetoothInterface Pobserver)
	{
		m_teshu_observer_switch = Pswitch;
		m_teshu_observer = Pobserver;
	}
	/******************************************************
	 * 该函数用于蓝牙发送数据
	 * 
	 * @param mode
	 *            :发送命令的模式 参加line16的预定义
	 * @param cmd
	 *            :发送命令字
	 * @param Sendbuf
	 *            :发送数据区
	 * @param SendLen
	 *            ：发送数据区内容长度
	 * @param Ptime
	 *            ：接收数据超时控制，时间默认单位100ms的整数倍 。
	 * @return 0--发送成功; -1 -- 发送失败, -2 -- 正在发送...
	 ******************************************************/
	public int SendDataToBluetooth(int mode, byte[] cmd, byte[] Sendbuf,
			int SendLen, int Ptime) 
	{
		//先判断蓝牙连接是否正常，如果不正常，则通知观察者
		if(!m_blue_state.IsConnected())
		{
			//通知观察者
			NotifyBlueConnectLost();
			return -1;
		}
		//检查系统是否正在发送数据
		if(m_time_switch == true)
			return -2;
		int iRet = 0;
		if (cmd.length < 2)
			return -1;
		m_send_mode = mode; // 设置当前数据发送模式
		//计算发送超时时间
		m_time_max = ((Ptime / 100) > TIME_MAX) ? (Ptime / 100): TIME_MAX;
		m_time_send = 0;// 重置超时次数
		m_time_counter = 0; //超时计时清零
		switch (m_send_mode) {
		case CMD_OneToOne:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);	
	        synchronized (this)
	        {
	            m_counter++;
	        }
			m_time_switch = true;
			// 发送命令
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_OneToNone:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);
            synchronized (this)
            {
                m_counter++;
            }
			m_time_switch = false;
			// 发送命令
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_OneToMore:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);
            synchronized (this)
            {
                m_counter++;
            }
			m_time_switch = true;
			// 发送命令
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_ReadMode:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);
            synchronized (this)
            {
                m_counter++;
            }
			m_time_switch = true;
			// 发送命令
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_NONE: // 不做任何处理，丢弃无效数据
			break;
		default:
			break;
		}
		return iRet;
	}
	// 发送一帧命令给蓝牙设备
	private void SendCmd(byte[] cmd, int Plen) 
	{
		if(!m_blue_state.IsConnected())
		{
			//通知观察者
			NotifyBlueConnectLost();
			m_time_switch = false; //关闭超时重发
			return ;
		}
		byte[] commandbuff = new byte[Plen];
		System.arraycopy(cmd, 0, commandbuff, 0, Plen);
		if (m_bluetooth_adapter_service != null)
			m_bluetooth_adapter_service.write(commandbuff);
		String send = bytesToHexString(commandbuff, Plen);
		// if(D) Log.i(TAG,"发送长度：" + Plen);
		if (D)	Log.i(TAG, "发送：" + send);
	}
	//处理收到的蓝牙数据
	private void ReceiveBlueData(byte[] databuf,int datalen)
	{
	    if(D) Log.i(TAG,"收到未校验的数据：" + bytesToHexString(databuf, datalen));
		//先通过计数器过滤垃圾数据
		if(datalen < 8) return;
		if((byte)(databuf[6] + 1) != m_counter) 
		{
			if(D) Log.i(TAG,"计数器校验错误!");
			return;
		}
		if(m_send_mode == CMD_NONE)
		{
			if(D) Log.i(TAG,"中断接收!");
			return;
		}
		else if(m_send_mode == CMD_OneToOne)
		{
			m_time_switch = false;
			m_send_mode = CMD_NONE;
		}
		else if(m_send_mode == CMD_OneToNone)
		{
			m_send_mode = CMD_NONE;
		}
		else if(m_send_mode == CMD_ReadMode)
		{
			m_time_switch = false;
		}
		else if(m_send_mode == CMD_OneToMore)
		{
			m_time_switch = false;
		}
		//发送数据给观察者
		//在这里去掉包头包尾 给用户的数据为<命令字> + <数据>
		//byte [] v_sendbuf = new byte[datalen - 8];
		//System.arraycopy(databuf, 7, v_sendbuf, 0, datalen - 8);
		//NotifyGetDataFromService(v_sendbuf, datalen - 8);
		if(datalen <= 8) return;
		NotifyGetDataFromService(databuf,datalen);
		String v_recv = bytesToHexString(databuf, datalen);
		if(D) Log.i(TAG,"收到：" + v_recv);
	}
	// 把接收到的零散的蓝牙数据整理成标准的一帧帧的数据包！
	private void receiveData(byte[] buff, int length) 
	{
		// 先将接收数据缓存到管道
		int i;
		byte v_check = 0; // 校验字节
		int framelenght = 0; // 帧数据长度

		for (i = 0; i < length; i++) {
			m_recvbuff[(m_recvbuffStartAddress + m_recvbuffDataLength + i) % m_recvbuff_len] = buff[i];
		}
		m_recvbuffDataLength += length;
		if(D) Log.i(TAG,"收到->m_recvbuffDataLength = " + m_recvbuffDataLength);
		// 查找合适数据并返回给界面
		// 先找0x55 0xAA，然后再判断目标地址源地址，再取出数据长度，并校验，如果校验不对则丢弃该帧数据
		if (m_recvbuffDataLength < 8)
			return;
		while (m_recvbuffDataLength >= 8) 
		{
			if (m_recvbuff[(m_recvbuffStartAddress + 0) % m_recvbuff_len] == (byte) 0x55) 
			{
				if (m_recvbuff[(m_recvbuffStartAddress + 1) % m_recvbuff_len] == (byte) 0xAA
						&& m_recvbuff[(m_recvbuffStartAddress + 2) % m_recvbuff_len] == (byte) 0xF8
						&& m_recvbuff[(m_recvbuffStartAddress + 3) % m_recvbuff_len] == (byte) 0xF0) 
				{
					framelenght = (m_recvbuff[(m_recvbuffStartAddress + 4) % m_recvbuff_len] & 0xFF)
								* 0x100	+ (m_recvbuff[(m_recvbuffStartAddress + 5) % m_recvbuff_len] & 0xFF);
					// 判断是否收全了一帧数据
					if (framelenght > m_recvbuffDataLength - 7)
						break;
					// 添加帧头
					v_check = 0;
					byte[] messagebuffer = new byte[framelenght + 7]; // 发送消息缓冲区
					if(D) Log.i(TAG,"messagebuffer = " + (framelenght + 7));
					messagebuffer[0] = m_recvbuff[(m_recvbuffStartAddress + 0) % m_recvbuff_len];
					messagebuffer[1] = m_recvbuff[(m_recvbuffStartAddress + 1) % m_recvbuff_len];
					for (i = 0; i < framelenght + 4; i++) 
					{
						messagebuffer[2 + i] = m_recvbuff[(m_recvbuffStartAddress + 2 + i) % m_recvbuff_len];
						v_check ^= m_recvbuff[(m_recvbuffStartAddress + 2 + i) % m_recvbuff_len];
					}
					if (v_check == m_recvbuff[(m_recvbuffStartAddress + 6 + framelenght) % m_recvbuff_len])// 帧校验正确
					{
						messagebuffer[framelenght + 6] = v_check;
						// 得出收到的数据内容
						// messagebuffer framelenght + 7
						// String v_recv =
						// bytesToHexString(messagebuffer,framelenght + 7);
						//if(D) Log.i(TAG,"输出：addr=" + m_recvbuffStartAddress + "len=" + framelenght + 7);
						//处理收到的蓝牙数据
						ReceiveBlueData(messagebuffer,framelenght + 7);
					}
					else
					{
						if(D) Log.e(TAG,"接收校验错误");
					}
					// 移动管道指针
					m_recvbuffStartAddress = (m_recvbuffStartAddress + framelenght + 7) % m_recvbuff_len;
					if(m_recvbuffStartAddress > m_recvbuff_len)
						m_recvbuffStartAddress = m_recvbuffStartAddress % m_recvbuff_len;
					m_recvbuffDataLength -= framelenght + 7;
					//if(D) Log.i(TAG,"okm_recvbuffDataLength = " + m_recvbuffDataLength);
				}
				else
				{
					m_recvbuffStartAddress++;
					m_recvbuffDataLength--;
					if(D) Log.i(TAG,"m_recvbuffDataLength = " + m_recvbuffDataLength);
				}
			} else // 移动起始地址
			{
				// Log.e("bt","error");
				m_recvbuffStartAddress++;
				m_recvbuffDataLength--;
				if(D) Log.i(TAG,"m_recvbuffDataLength = " + m_recvbuffDataLength);
			}
		}
	}	
	public int AddDataToCommond(byte[] Pcmd, byte[] Pdata, int PdataLen,byte[] Pcommond) 
	{
		int iRet = 0; // 返回组装数据长度
		Arrays.fill(Pcommond, (byte) 0); // 服位发送字符串
		Pcommond[0] = 0x55;
		Pcommond[1] = (byte) 0xAA;
		Pcommond[2] = m_address[0]; // 目标地址
		Pcommond[3] = m_address[1]; // 源地址
		// 添加数据长度
		Pcommond[4] = (byte) ((3 + PdataLen) / 0x100); // 目标地址
		Pcommond[5] = (byte) ((3 + PdataLen) % 0x100); // 源地址
		// 添加计数器
		Pcommond[6] = m_counter;
		// 添加命令字
		Pcommond[7] = Pcmd[0];
		Pcommond[8] = Pcmd[1];
		// 添加命令内容
		for (int i = 0; i < PdataLen; i++) {
			Pcommond[9 + i] = Pdata[i];
		}
		// 计算校验
		Pcommond[9 + PdataLen] = CalcXorChechSum(Pcommond, 2, 8 + PdataLen);
		iRet = 9 + PdataLen + 1;
		return iRet;
	}
	
	@Override
	public void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		m_bluetooth_adapter_service.StopService();
		m_timer.cancel();
		
	}
	/**
	 * @author pufengming
	 * @param data
	 *            数据包字节数组
	 * @param start
	 *            校验起始位置
	 * @param end
	 *            校验的结束位置
	 * @return XOR校验值
	 * */
	public static byte CalcXorChechSum(byte[] data, int start, int end) {
		byte chechsum = 0;
		if (start >= 0 && start < end && data != null && data.length > 0) {
			int calc_len = end - start + 1;
			for (int i = start; i < start + calc_len; i++) {
				chechsum ^= data[i];
			}
			return (byte) chechsum;
		}
		return (byte) 0x00;
	}

	/****
	 * @author luxingsong 把字节数组转换成16进制字符串
	 * @param bArray
	 *            字节数组
	 * @return String 转换成的字符串
	 */
	public static final String bytesToHexString(byte[] bArray, int len) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		// int v_len = bArray.length;
		int last = len - 1;
		//sb.append("0x");
		for (int i = 0; i < len; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append("0");
			sb.append(sTemp.toUpperCase());
			if (i != last)
				sb.append(" ");
		}
		return sb.toString();
	}
	
	//回调函数
	@Override
	public void GetDataFromBlueSocket(byte[] buf, int len) {
		// TODO Auto-generated method stub
		if(D)
		{
			String v_show = bytesToHexString(buf, len);
			//Log.i(TAG,"回调：" + v_show);
		}
		//if(D) Log.i(TAG,"回调数据：" + len);
		receiveData(buf,len);
		if(D) Log.i(TAG,"回调数据OK");
	}
	@Override
	public void GetBluetoothState(BlueStateEvent state) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"获取蓝牙状态!!!");
		m_blue_state_last = m_blue_state;//保存历史状态
		m_blue_state = state;
		ResetTimeOut();
		if(m_blue_state.IsConnected())
		{
			NotifyBlueConnected();
		}
		else if(m_blue_state.IsConnectLost())
		{
			if(m_blue_state_last != null)
			{
				if(m_blue_state_last.IsConnected())
					BlueConnectClose();
				else
					NotifyBlueConnectLost();
			}
			else
				NotifyBlueConnectLost();
		}
	}
	//调用通知
	public void BlueConnectClose()
	{
		NotifyBlueConnectClose();
	}
	
	//通知观察者对象
	public class DataToClient
	{
		private BlueStateEvent m_blue_state = null;
		public boolean IsConnected()
		{
			if(m_blue_state != null)
				return m_blue_state.IsConnected();
			else
				return false;
		}
		public void SetBlueState(BlueStateEvent state)
		{
			m_blue_state = state;
		}
	}
    public void autoConnectBluetooth(String mac)
    {
        m_bluetooth_adapter_service.ConnectDevice(mac);
    }
    
    public String displayObserveList()
    {
        StringBuffer sb = new StringBuffer();
        if (m_observer_list != null && m_observer_list.size() > 0)
        {
            for (Object iterable_element : m_observer_list)
            {
                sb.append(iterable_element.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    // 设置计数器
    public void setCounter(byte count)
    {
        synchronized (this)
        {
            this.m_counter = count;   
            if(D)Log.d(TAG, "计数器："  + m_counter);
        }
    }
}

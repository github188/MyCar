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
	//������������λ
	private static BluetoothDataService m_bluetooth_service = null;
	//��������ʵ��
	private BluetoothAdapterService m_bluetooth_adapter_service = null;
	// ��ǰ��������
	private String m_now_bluetoothname = null;
	private String m_now_bluetoothmac = null;
	private boolean m_show_connect_activity = false;
	//��ʱ���Ʊ���
	private static final int TIME_MAX = 10; 		//����Ĭ�ϳ�ʱʱ��,Ϊ100ms��������
	private static final int TIME_MAX_SEND = 5; 	//��ʱ�ط�����
	private int m_time_send = 0;					//��ʱ�ط�����������
	private boolean m_time_switch = false;  		//��ʱ���ƿ���
	private int m_time_counter = 0;					//��ʱ������
	private int m_time_max = 0;						//��ʱ�ȴ�ʱ�� 
	//��ʱ��
	private Timer m_timer = null;
	private TimerTask m_TimerTask;
	//����״̬��װ
	public BlueStateEvent m_blue_state = null;
	//������һ��״̬
	public BlueStateEvent m_blue_state_last = null;
	//�۲��߶����б�
	private ArrayList<BluetoothInterface> m_observer_list = null;
	//�������ӽ���ר��
	private boolean m_teshu_observer_switch = false;  //��Ϊtrueʱ�����������۲���
	private BluetoothInterface m_teshu_observer = null; //��ɫ�۲���,������ֵΪtrueʱ��ֻ����һ���۲��߷�����Ϣ
	//�����۲���
	Intent m_bluetooth_intent = null;
	
	//����ʵ��
	public synchronized static BluetoothDataService getInstance()
	{
		if(m_bluetooth_service == null)
			m_bluetooth_service = new BluetoothDataService();
		return m_bluetooth_service;	
	}
	//���췽��
	private BluetoothDataService()
	{
		m_bluetooth_adapter_service = BluetoothAdapterService.getInstance();
		m_bluetooth_adapter_service.AddBlueCaalback(this);
		m_blue_state = m_bluetooth_adapter_service.m_blue_state;  //��ʼ������״̬
		// ��ʼ����ʱ��
		m_timer = new Timer();
		m_TimerTask = new TimerTask() 
		{
			@Override
			public void run() 
			{
			//TODO �Զ����У�ÿ��200�����Զ�����һ��
				if(m_time_switch == true)
				{
					m_time_counter ++;
					if(m_time_counter >= m_time_max) //��ʱʱ�䵽
					{
						m_time_send ++;
						m_time_counter = 0;//��λ������
						if(m_time_send == TIME_MAX_SEND) //�ﵽ��ʱ����
						{
							if(D) Log.i(TAG,"���ճ�ʱ��ģʽ��" + m_send_mode);
							m_time_switch = false; //�رճ�ʱ������
							if(m_send_mode == CMD_OneToOne)
								m_send_mode = CMD_NONE; //��λģʽ
							NotifyGetDataTimeout();
						}
						else
						{
							SendCmd(m_sendbuf, m_sendbuflen); //��ʱ�ط�
						}
					}
				}
			}
		};
		m_timer.schedule(m_TimerTask, 100, 200); //���ö�ʱ������ʱ100����ִ�У�ÿ��200���봥��һ�Ρ�	
		//��ʼ���۲����б�
		m_observer_list = new ArrayList<BluetoothInterface>();
	}
	//************************************************************************//
	//��������ģʽ
	public static final int CMD_NONE  	  =	0; 			// ���������ݵȴ�״̬
	public static final int CMD_OneToOne  = 1; 			// ��һ֡��һ֡
	public static final int CMD_OneToNone = 2; 			// ��һ֡������
	public static final int CMD_OneToMore = 3; 			// ��һ֡�ն�֡
	public static final int CMD_ReadMode  =	4; 			// ֻ�����ݡ�����ģʽ	
	//��������ģʽ
	private int m_send_mode = CMD_NONE;
	// ���巢�����ݻ�����
	private final static int m_recvbuff_len = 8*1024;	//���ݻ���������
	private byte[] m_sendbuf = new byte[m_recvbuff_len];
	private int m_sendbuflen = 0; // �������ݳ���
	// ������
	private byte m_counter = 0; // ѭ��������
	private byte[] m_address = { (byte) 0xF0, (byte) 0xF8 }; // ������λ������λ����ַ
	
	// �������ݹ�����ر���
	
	private byte[] 	m_recvbuff = new byte[m_recvbuff_len]; 	// ���չܵ�������
	private int 	m_recvbuffStartAddress = 0; 		// ���չܵ�ͷ��ַ
	private int 	m_recvbuffDataLength = 0; 			// ���չܵ����ݳ���

	//********************************�ӿں���*********************************//
	//��ȡ��ǰ������������
	public String GetConnectedBluetoothName()
	{
		if(m_blue_state.IsConnected())
			return m_now_bluetoothname;
		else
			return null;
	}
	//��ȡ��ǰ��������MAC��ַ
	public String GetConnectedBluetoothMac()
	{
		if(m_blue_state.IsConnected())
			return m_now_bluetoothmac;
		else
			return null;
	}
	//���õ�ǰ�������ƺ͵�ַ
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
	//��ʾ�������ӽ���
	public void ShowBluetoothConnectActivity(Activity activity)
	{
		if(m_show_connect_activity) 
			return;
		m_show_connect_activity = true; //��ֹ����ظ�����
		if(m_bluetooth_intent == null)
		{
			m_bluetooth_intent = new Intent(activity, BluetoothWatch.class);
			activity.startService(m_bluetooth_intent);
		}
		//��ȡ��ǰ��activity
		Intent intent = new Intent(activity, BluetoothDeviceListActivity.class);
		activity.startActivity(intent);
	}
	//��ӹ۲���
	public synchronized void AddObserver(BluetoothInterface observer)
	{
		if(observer != null)
			m_observer_list.add(observer);
	}
	//ɾ���۲���
	public synchronized void DelObserver(BluetoothInterface observer)
	{
		if(observer != null)
			m_observer_list.remove(observer);
	}
	//֪ͨ�۲��������ж�
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
	//֪ͨ�������ӳɹ�
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
	//֪ͨ�յ�������
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
	//֪ͨ�������ݳ�ʱ
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
	//֪ͨ�۲����������ӽ��淵����
	private synchronized void NotifyBlueConnectClose()
	{
		for(int i = 0; i < m_observer_list.size(); i++)
		{
			BluetoothInterface v_interface = m_observer_list.get(i);
			if(v_interface != null)
					v_interface.BlueConnectClose();
		}
	}
	//������������ 
	public void StopBlueService(Activity activity)
	{
		//��ֹ����
		if(m_bluetooth_intent != null)
		{
			activity.stopService(m_bluetooth_intent);
			m_bluetooth_intent = null;
		}
		m_bluetooth_adapter_service.StopService();
	}
	//�����Ƿ��Ѿ���ʾ�������ӽ���
	public synchronized void SetShowConnectActivity(boolean show)
	{
		m_show_connect_activity = show;
	}
	public boolean IsConnected()
	{
		return m_blue_state.IsConnected();
	}
	//���ó�ʱ
	public void ResetTimeOut()
	{
		m_time_switch = false;
		m_time_counter = 0;
	}
	//��������۲��ߣ�������Ϊtrueʱ��ֻ���ù۲��߷�����Ϣ
	public void SetSpecialObserver(boolean Pswitch,BluetoothInterface Pobserver)
	{
		m_teshu_observer_switch = Pswitch;
		m_teshu_observer = Pobserver;
	}
	/******************************************************
	 * �ú�������������������
	 * 
	 * @param mode
	 *            :���������ģʽ �μ�line16��Ԥ����
	 * @param cmd
	 *            :����������
	 * @param Sendbuf
	 *            :����������
	 * @param SendLen
	 *            ���������������ݳ���
	 * @param Ptime
	 *            ���������ݳ�ʱ���ƣ�ʱ��Ĭ�ϵ�λ100ms�������� ��
	 * @return 0--���ͳɹ�; -1 -- ����ʧ��, -2 -- ���ڷ���...
	 ******************************************************/
	public int SendDataToBluetooth(int mode, byte[] cmd, byte[] Sendbuf,
			int SendLen, int Ptime) 
	{
		//���ж����������Ƿ��������������������֪ͨ�۲���
		if(!m_blue_state.IsConnected())
		{
			//֪ͨ�۲���
			NotifyBlueConnectLost();
			return -1;
		}
		//���ϵͳ�Ƿ����ڷ�������
		if(m_time_switch == true)
			return -2;
		int iRet = 0;
		if (cmd.length < 2)
			return -1;
		m_send_mode = mode; // ���õ�ǰ���ݷ���ģʽ
		//���㷢�ͳ�ʱʱ��
		m_time_max = ((Ptime / 100) > TIME_MAX) ? (Ptime / 100): TIME_MAX;
		m_time_send = 0;// ���ó�ʱ����
		m_time_counter = 0; //��ʱ��ʱ����
		switch (m_send_mode) {
		case CMD_OneToOne:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);	
	        synchronized (this)
	        {
	            m_counter++;
	        }
			m_time_switch = true;
			// ��������
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_OneToNone:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);
            synchronized (this)
            {
                m_counter++;
            }
			m_time_switch = false;
			// ��������
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_OneToMore:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);
            synchronized (this)
            {
                m_counter++;
            }
			m_time_switch = true;
			// ��������
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_ReadMode:
			m_sendbuflen = AddDataToCommond(cmd, Sendbuf, SendLen, m_sendbuf);
            synchronized (this)
            {
                m_counter++;
            }
			m_time_switch = true;
			// ��������
			SendCmd(m_sendbuf, m_sendbuflen);
			break;
		case CMD_NONE: // �����κδ���������Ч����
			break;
		default:
			break;
		}
		return iRet;
	}
	// ����һ֡����������豸
	private void SendCmd(byte[] cmd, int Plen) 
	{
		if(!m_blue_state.IsConnected())
		{
			//֪ͨ�۲���
			NotifyBlueConnectLost();
			m_time_switch = false; //�رճ�ʱ�ط�
			return ;
		}
		byte[] commandbuff = new byte[Plen];
		System.arraycopy(cmd, 0, commandbuff, 0, Plen);
		if (m_bluetooth_adapter_service != null)
			m_bluetooth_adapter_service.write(commandbuff);
		String send = bytesToHexString(commandbuff, Plen);
		// if(D) Log.i(TAG,"���ͳ��ȣ�" + Plen);
		if (D)	Log.i(TAG, "���ͣ�" + send);
	}
	//�����յ�����������
	private void ReceiveBlueData(byte[] databuf,int datalen)
	{
	    if(D) Log.i(TAG,"�յ�δУ������ݣ�" + bytesToHexString(databuf, datalen));
		//��ͨ��������������������
		if(datalen < 8) return;
		if((byte)(databuf[6] + 1) != m_counter) 
		{
			if(D) Log.i(TAG,"������У�����!");
			return;
		}
		if(m_send_mode == CMD_NONE)
		{
			if(D) Log.i(TAG,"�жϽ���!");
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
		//�������ݸ��۲���
		//������ȥ����ͷ��β ���û�������Ϊ<������> + <����>
		//byte [] v_sendbuf = new byte[datalen - 8];
		//System.arraycopy(databuf, 7, v_sendbuf, 0, datalen - 8);
		//NotifyGetDataFromService(v_sendbuf, datalen - 8);
		if(datalen <= 8) return;
		NotifyGetDataFromService(databuf,datalen);
		String v_recv = bytesToHexString(databuf, datalen);
		if(D) Log.i(TAG,"�յ���" + v_recv);
	}
	// �ѽ��յ�����ɢ��������������ɱ�׼��һ֡֡�����ݰ���
	private void receiveData(byte[] buff, int length) 
	{
		// �Ƚ��������ݻ��浽�ܵ�
		int i;
		byte v_check = 0; // У���ֽ�
		int framelenght = 0; // ֡���ݳ���

		for (i = 0; i < length; i++) {
			m_recvbuff[(m_recvbuffStartAddress + m_recvbuffDataLength + i) % m_recvbuff_len] = buff[i];
		}
		m_recvbuffDataLength += length;
		if(D) Log.i(TAG,"�յ�->m_recvbuffDataLength = " + m_recvbuffDataLength);
		// ���Һ������ݲ����ظ�����
		// ����0x55 0xAA��Ȼ�����ж�Ŀ���ַԴ��ַ����ȡ�����ݳ��ȣ���У�飬���У�鲻��������֡����
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
					// �ж��Ƿ���ȫ��һ֡����
					if (framelenght > m_recvbuffDataLength - 7)
						break;
					// ���֡ͷ
					v_check = 0;
					byte[] messagebuffer = new byte[framelenght + 7]; // ������Ϣ������
					if(D) Log.i(TAG,"messagebuffer = " + (framelenght + 7));
					messagebuffer[0] = m_recvbuff[(m_recvbuffStartAddress + 0) % m_recvbuff_len];
					messagebuffer[1] = m_recvbuff[(m_recvbuffStartAddress + 1) % m_recvbuff_len];
					for (i = 0; i < framelenght + 4; i++) 
					{
						messagebuffer[2 + i] = m_recvbuff[(m_recvbuffStartAddress + 2 + i) % m_recvbuff_len];
						v_check ^= m_recvbuff[(m_recvbuffStartAddress + 2 + i) % m_recvbuff_len];
					}
					if (v_check == m_recvbuff[(m_recvbuffStartAddress + 6 + framelenght) % m_recvbuff_len])// ֡У����ȷ
					{
						messagebuffer[framelenght + 6] = v_check;
						// �ó��յ�����������
						// messagebuffer framelenght + 7
						// String v_recv =
						// bytesToHexString(messagebuffer,framelenght + 7);
						//if(D) Log.i(TAG,"�����addr=" + m_recvbuffStartAddress + "len=" + framelenght + 7);
						//�����յ�����������
						ReceiveBlueData(messagebuffer,framelenght + 7);
					}
					else
					{
						if(D) Log.e(TAG,"����У�����");
					}
					// �ƶ��ܵ�ָ��
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
			} else // �ƶ���ʼ��ַ
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
		int iRet = 0; // ������װ���ݳ���
		Arrays.fill(Pcommond, (byte) 0); // ��λ�����ַ���
		Pcommond[0] = 0x55;
		Pcommond[1] = (byte) 0xAA;
		Pcommond[2] = m_address[0]; // Ŀ���ַ
		Pcommond[3] = m_address[1]; // Դ��ַ
		// ������ݳ���
		Pcommond[4] = (byte) ((3 + PdataLen) / 0x100); // Ŀ���ַ
		Pcommond[5] = (byte) ((3 + PdataLen) % 0x100); // Դ��ַ
		// ��Ӽ�����
		Pcommond[6] = m_counter;
		// ���������
		Pcommond[7] = Pcmd[0];
		Pcommond[8] = Pcmd[1];
		// �����������
		for (int i = 0; i < PdataLen; i++) {
			Pcommond[9 + i] = Pdata[i];
		}
		// ����У��
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
	 *            ���ݰ��ֽ�����
	 * @param start
	 *            У����ʼλ��
	 * @param end
	 *            У��Ľ���λ��
	 * @return XORУ��ֵ
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
	 * @author luxingsong ���ֽ�����ת����16�����ַ���
	 * @param bArray
	 *            �ֽ�����
	 * @return String ת���ɵ��ַ���
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
	
	//�ص�����
	@Override
	public void GetDataFromBlueSocket(byte[] buf, int len) {
		// TODO Auto-generated method stub
		if(D)
		{
			String v_show = bytesToHexString(buf, len);
			//Log.i(TAG,"�ص���" + v_show);
		}
		//if(D) Log.i(TAG,"�ص����ݣ�" + len);
		receiveData(buf,len);
		if(D) Log.i(TAG,"�ص�����OK");
	}
	@Override
	public void GetBluetoothState(BlueStateEvent state) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"��ȡ����״̬!!!");
		m_blue_state_last = m_blue_state;//������ʷ״̬
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
	//����֪ͨ
	public void BlueConnectClose()
	{
		NotifyBlueConnectClose();
	}
	
	//֪ͨ�۲��߶���
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
    
    // ���ü�����
    public void setCounter(byte count)
    {
        synchronized (this)
        {
            this.m_counter = count;   
            if(D)Log.d(TAG, "��������"  + m_counter);
        }
    }
}

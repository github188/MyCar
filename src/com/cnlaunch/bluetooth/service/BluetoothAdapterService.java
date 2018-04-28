package com.cnlaunch.bluetooth.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BluetoothAdapterService{
	//debug
	private static final String TAG = "BluetoothAdapterService";
    private static final boolean D = false;
    public final BluetoothAdapterService context = BluetoothAdapterService.this;
    // ��������UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //����adapter
    
    private BluetoothAdapter m_adapter = null; // �����󶨲�����
    //���������߳�
    private ConnectThread m_connect_thread = null; // ���������߳�
    private ConnectedThread m_connected_thread = null; // ����ͨѶ�����߳�
    //����״̬����
    public BlueStateEvent m_blue_state = null;
    //�߳�����״̬
    private boolean m_connected_running_state = false;
    //�ص�
    BlueCallback m_getdata = null;
    
    //������ʼ��
    private static BluetoothAdapterService m_bluetooth_service = null;
    //����ʵ��
    public synchronized static BluetoothAdapterService getInstance()
    {
        if (m_bluetooth_service == null)
        {
        	m_bluetooth_service = new BluetoothAdapterService();
        }
        return m_bluetooth_service;
    }
    //�๹�췽�� 
    private BluetoothAdapterService()
    {
    	m_adapter = BluetoothAdapter.getDefaultAdapter();
    	m_blue_state = new BlueStateEvent(); //��ʼ������״̬��
    }
    public interface BlueCallback{
    	public void GetDataFromBlueSocket(byte[] buf,int len);
    	public void GetBluetoothState(BlueStateEvent state);
    }
    public void AddBlueCaalback(BlueCallback callback)
    {
    	//��ʼ���ص�����
    	m_getdata = callback;
    }
    /* ---------------------------�ӿڷ���(��ʼ)-----------------------------------*/
    //����Ϊ����MAC��ַ,����Ϊ17λ����ʽΪ����00:00:00:00��00:00��
    public synchronized boolean ConnectDevice(String deviceAddr)
    {
    	//����������ӣ���ֱ�ӷ���false
    	if(m_blue_state.IsConnecting())
    		return false;
    	BluetoothDevice v_device = m_adapter.getRemoteDevice(deviceAddr);
    	//�豸��ʼ��ʧ�ܣ�Ҳ����false
    	if(v_device == null)
    		return false;
    	//��λͨѶ�߳�
    	if (m_connected_thread != null)
        {
    		m_connected_thread.terminate();
    		m_connected_running_state = false;
    		m_connected_thread.cancel();
    		m_connected_thread = null;
        }
    	//���������߳�
    	m_connect_thread = new ConnectThread(v_device);
    	m_connect_thread.start();
    	//����״̬
        m_blue_state.SetConnecting();
    	return true;
    }
    /**
     * д�ַ���
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public synchronized void write(byte[] out)
    {
        // Create temporary objec
        // Synchronize a copy of the ConnectedThread
        if (!m_blue_state.IsConnected())
        {
            return;
        }
        if(m_connected_thread != null)
        	m_connected_thread.write(out);
    }
    //ֹͣ��������
    public void StopService()
    {
    	if(D) Log.i(TAG,"������������!");
    	m_blue_state.SetListen();
    	if(m_connect_thread != null)
    	{
    		m_connect_thread.cancel();
    		m_connect_thread = null;
    	}
    	if(m_connected_thread != null)
    	{
    		m_connected_thread.terminate();
    		m_connected_thread.cancel();
    		m_connected_thread = null;
    	}
    }
    /* ---------------------------�ӿڷ���(����)-----------------------------------*/
    /****************************���������߳�***************************************/
    private class ConnectThread extends Thread
    {
        private  BluetoothSocket v_socket = null;
        private  BluetoothDevice v_device = null;
        public ConnectThread(BluetoothDevice device)
        {
        	v_device = device;
            try
            {
            	v_socket = v_device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e)
            {
                Log.e(TAG, "create() Blue Socket failed", e);
            }
        }
        public void run()
        {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            //ȡ������	
            m_adapter.cancelDiscovery();
            try
            {
                //��������....
                v_socket.connect();
            }
            catch (IOException e)
            {
            	ConnectThreadFailed();
                //�ر�Socket
                try
                {
                    v_socket.close();
                }
                catch (IOException e2)
                {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }
            //�������������߳�
            synchronized (BluetoothAdapterService.this)
            {
            	m_connect_thread = null;
            }
            //���������̣߳�����ͨ���߳�
            StopConnectAndStartCommunThread(v_socket);
        }
        public void cancel()
        {
            try
            {
                v_socket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    /****************************���������߳�(����)***************************************/
    //����ʧ�ܴ�����
    private void ConnectThreadFailed()
    {
    	m_blue_state.SetListen();
    }
    //���������̣߳�����ͨ���߳�
    private synchronized void StopConnectAndStartCommunThread(BluetoothSocket socket)
    {
    	//��λ�����߳�
    	if(m_connect_thread != null)
        {
        	m_connect_thread.cancel();
        	m_connect_thread = null;
        }
        //��λͨ���߳�
        if(m_connected_thread != null)
        {
        	m_connected_thread.interrupt();
        	m_connected_thread.terminate();
        	m_connected_thread.cancel();
        	m_connected_thread = null;
        }
        //����ͨ���߳�
        m_connected_thread = new ConnectedThread(socket);
        m_connected_thread.start();
        //��������״̬
        m_blue_state.SetConnected();
    }
    
    /****************************����ͨ���߳�***************************************/
    private class ConnectedThread extends Thread
    {
        private BluetoothSocket v_socket = null;
        private InputStream v_in_stream = null;
        private OutputStream v_out_stream = null;
        //�����ݻ�����
        int read_len = 0;
        byte[] buffer = new byte[8000];
        public ConnectedThread(BluetoothSocket socket)
        {
            Log.d(TAG, "create ConnectedThread");
            v_socket = socket;
            //��ȡ���������
            try
            {
            	v_in_stream = socket.getInputStream();
            	v_out_stream = socket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "temp sockets not created", e);
            }
        }
        public void run()
        {
            Log.i(TAG, "BEGIN mConnectedThread");
            m_connected_running_state = true;
            // Keep listening to the InputStream while connected
            try
            {
                while (!this.isInterrupted() && (m_connected_running_state == true))
                {
                	//���ݴ�����������
                    ReadData();
                }
                //if(D) Log.i(TAG,"������������");	
            }
            catch (InterruptedException e)
            {
            	m_connected_running_state = false;
            	ConnectThreadFailed();
                Log.e(TAG, "disconnected", e);
            }
            if(D) Log.i(TAG,"������������");	
        }
        public void ReadData() throws InterruptedException
        {

            try
            {
                read_len = v_in_stream.read(buffer);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                Log.i(TAG, "ReadData fialed", e);
                m_connected_running_state = false;
            	ConnectThreadFailed();
                terminate();
                return;
            }
            // �������ݸ�handler
            // ��������
            //byte[] v_buf = new byte[read_len];
            //for (int i = 0; i < read_len; i++)
            //    v_buf[i] = buffer[i];
            if(m_getdata != null)
            	m_getdata.GetDataFromBlueSocket(buffer, read_len);
        }

        public void terminate()
        {
        	m_connected_running_state = false;
            this.interrupt();
            cancel();
        }
        //����д����
        public void write(byte[] buffer)
        {
            try
            {
            	if(v_out_stream != null)
            	{
            		v_out_stream.flush();
            		//Log.d(TAG, "�������:" + BluetoothDataService.bytesToHexString(buffer, buffer.length));
            		v_out_stream.write(buffer);
            	}
            }
            catch (IOException e)
            {
                Log.i(TAG, "����д����ʧ�ܣ������жϡ�", e);
                m_connected_running_state = false;
            	ConnectThreadFailed();
                terminate();
            }
        }

        public void cancel()
        {
            try
            {
            	if(v_socket != null)
            	{
            		v_socket.close();
            		v_socket = null;
            	}
            	if(v_in_stream != null)
            	{
            		v_in_stream.close();
            		v_in_stream = null;
            	}
            	if(v_out_stream != null)
            	{
            		v_out_stream.close(); 
            		v_out_stream = null;
            	}
            }
            catch (IOException e)
            {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    /****************************����ͨ���̣߳�������***************************************/
    
    public class BlueStateEvent{
    	//Ԥ����ļ�������״̬
        public static final int STATE_NONE = 		0; // ������
        public static final int STATE_LISTEN = 		1; // ����״̬
        public static final int STATE_CONNECTING = 	2; // ��������...
        public static final int STATE_CONNECTED = 	3; // �����Ѿ�����.
        //����״̬����
        private int m_state = STATE_NONE;
      
        /** ����State״̬ */
        private void SetState(int state)
        {
            if (D)
                Log.d(TAG, "setState() " + "����State = " + state);
            m_state = state;
            // ֪ͨ�۲���״̬�����仯
            if(m_getdata != null)
            	m_getdata.GetBluetoothState(m_blue_state);
        }
        //��������Ϊ����״̬
        public void SetListen()
        {
        	SetState(STATE_LISTEN);
        }
        //����Ϊ��������״̬
        public void SetConnecting()
        {
        	SetState(STATE_CONNECTING);
        }
        //����Ϊ�Ѿ�����״̬
        public void SetConnected()
        {
        	SetState(STATE_CONNECTED);
        }
        //��ȡ����״̬
        public synchronized int GetBlueState()
        {
        	return m_state;
        }
        //�ж��Ƿ��Ѿ�����
        public boolean IsConnected()
        {
        	if(m_state == STATE_CONNECTED)
        		return true;
        	else
        		return false;
        }
        //�ж��Ƿ���������
        public boolean IsConnecting()
        {
        	if(m_state == STATE_CONNECTING)
        		return true;
        	else
        		return false;
        }
        //�ж��Ƿ������ж�
        public boolean IsConnectLost()
        {
        	if(m_state == STATE_LISTEN || m_state == STATE_NONE)
        		return true;
        	else
        		return false;
        }
    }
}

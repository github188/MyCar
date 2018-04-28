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
    // 定义蓝牙UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //蓝牙adapter
    
    private BluetoothAdapter m_adapter = null; // 蓝牙绑定操作类
    //蓝牙连接线程
    private ConnectThread m_connect_thread = null; // 蓝牙连接线程
    private ConnectedThread m_connected_thread = null; // 蓝牙通讯监听线程
    //蓝牙状态对象
    public BlueStateEvent m_blue_state = null;
    //线程运行状态
    private boolean m_connected_running_state = false;
    //回调
    BlueCallback m_getdata = null;
    
    //单例初始化
    private static BluetoothAdapterService m_bluetooth_service = null;
    //单例实现
    public synchronized static BluetoothAdapterService getInstance()
    {
        if (m_bluetooth_service == null)
        {
        	m_bluetooth_service = new BluetoothAdapterService();
        }
        return m_bluetooth_service;
    }
    //类构造方法 
    private BluetoothAdapterService()
    {
    	m_adapter = BluetoothAdapter.getDefaultAdapter();
    	m_blue_state = new BlueStateEvent(); //初始化蓝牙状态类
    }
    public interface BlueCallback{
    	public void GetDataFromBlueSocket(byte[] buf,int len);
    	public void GetBluetoothState(BlueStateEvent state);
    }
    public void AddBlueCaalback(BlueCallback callback)
    {
    	//初始化回调方法
    	m_getdata = callback;
    }
    /* ---------------------------接口方法(开始)-----------------------------------*/
    //参数为蓝牙MAC地址,长度为17位。格式为：“00:00:00:00：00:00”
    public synchronized boolean ConnectDevice(String deviceAddr)
    {
    	//如果正在连接，则直接返回false
    	if(m_blue_state.IsConnecting())
    		return false;
    	BluetoothDevice v_device = m_adapter.getRemoteDevice(deviceAddr);
    	//设备初始化失败，也返回false
    	if(v_device == null)
    		return false;
    	//复位通讯线程
    	if (m_connected_thread != null)
        {
    		m_connected_thread.terminate();
    		m_connected_running_state = false;
    		m_connected_thread.cancel();
    		m_connected_thread = null;
        }
    	//启动连接线程
    	m_connect_thread = new ConnectThread(v_device);
    	m_connect_thread.start();
    	//设置状态
        m_blue_state.SetConnecting();
    	return true;
    }
    /**
     * 写字符串
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
    //停止蓝牙服务
    public void StopService()
    {
    	if(D) Log.i(TAG,"结束蓝牙服务!");
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
    /* ---------------------------接口方法(结束)-----------------------------------*/
    /****************************蓝牙连接线程***************************************/
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
            //取消搜索	
            m_adapter.cancelDiscovery();
            try
            {
                //建立连接....
                v_socket.connect();
            }
            catch (IOException e)
            {
            	ConnectThreadFailed();
                //关闭Socket
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
            //重置蓝牙连接线程
            synchronized (BluetoothAdapterService.this)
            {
            	m_connect_thread = null;
            }
            //结束连接线程，启动通信线程
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
    /****************************蓝牙连接线程(结束)***************************************/
    //连接失败处理方法
    private void ConnectThreadFailed()
    {
    	m_blue_state.SetListen();
    }
    //结束连接线程，启动通信线程
    private synchronized void StopConnectAndStartCommunThread(BluetoothSocket socket)
    {
    	//复位连接线程
    	if(m_connect_thread != null)
        {
        	m_connect_thread.cancel();
        	m_connect_thread = null;
        }
        //复位通信线程
        if(m_connected_thread != null)
        {
        	m_connected_thread.interrupt();
        	m_connected_thread.terminate();
        	m_connected_thread.cancel();
        	m_connected_thread = null;
        }
        //启动通信线程
        m_connected_thread = new ConnectedThread(socket);
        m_connected_thread.start();
        //设置蓝牙状态
        m_blue_state.SetConnected();
    }
    
    /****************************蓝牙通信线程***************************************/
    private class ConnectedThread extends Thread
    {
        private BluetoothSocket v_socket = null;
        private InputStream v_in_stream = null;
        private OutputStream v_out_stream = null;
        //读数据缓冲区
        int read_len = 0;
        byte[] buffer = new byte[8000];
        public ConnectedThread(BluetoothSocket socket)
        {
            Log.d(TAG, "create ConnectedThread");
            v_socket = socket;
            //获取输入输出流
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
                	//根据触发，读数据
                    ReadData();
                }
                //if(D) Log.i(TAG,"结束蓝牙服务！");	
            }
            catch (InterruptedException e)
            {
            	m_connected_running_state = false;
            	ConnectThreadFailed();
                Log.e(TAG, "disconnected", e);
            }
            if(D) Log.i(TAG,"结束蓝牙服务！");	
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
            // 发送数据给handler
            // 拷贝数据
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
        //蓝牙写方法
        public void write(byte[] buffer)
        {
            try
            {
            	if(v_out_stream != null)
            	{
            		v_out_stream.flush();
            		//Log.d(TAG, "☆☆☆☆☆发送:" + BluetoothDataService.bytesToHexString(buffer, buffer.length));
            		v_out_stream.write(buffer);
            	}
            }
            catch (IOException e)
            {
                Log.i(TAG, "蓝牙写数据失败，连接中断。", e);
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
    /****************************蓝牙通信线程（结束）***************************************/
    
    public class BlueStateEvent{
    	//预定义的几种蓝牙状态
        public static final int STATE_NONE = 		0; // 无连接
        public static final int STATE_LISTEN = 		1; // 监听状态
        public static final int STATE_CONNECTING = 	2; // 正在连接...
        public static final int STATE_CONNECTED = 	3; // 蓝牙已经连接.
        //蓝牙状态变量
        private int m_state = STATE_NONE;
      
        /** 设置State状态 */
        private void SetState(int state)
        {
            if (D)
                Log.d(TAG, "setState() " + "蓝牙State = " + state);
            m_state = state;
            // 通知观察者状态发生变化
            if(m_getdata != null)
            	m_getdata.GetBluetoothState(m_blue_state);
        }
        //设置蓝牙为监听状态
        public void SetListen()
        {
        	SetState(STATE_LISTEN);
        }
        //设置为正在连接状态
        public void SetConnecting()
        {
        	SetState(STATE_CONNECTING);
        }
        //设置为已经连接状态
        public void SetConnected()
        {
        	SetState(STATE_CONNECTED);
        }
        //获取蓝牙状态
        public synchronized int GetBlueState()
        {
        	return m_state;
        }
        //判断是否已经连接
        public boolean IsConnected()
        {
        	if(m_state == STATE_CONNECTED)
        		return true;
        	else
        		return false;
        }
        //判断是否正在连接
        public boolean IsConnecting()
        {
        	if(m_state == STATE_CONNECTING)
        		return true;
        	else
        		return false;
        }
        //判断是否连接中断
        public boolean IsConnectLost()
        {
        	if(m_state == STATE_LISTEN || m_state == STATE_NONE)
        		return true;
        	else
        		return false;
        }
    }
}

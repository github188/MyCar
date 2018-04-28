package com.cnlaunch.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BluetoothWatch extends Service {
	private static final String TAG = "BluetoothWatch";
	private static final boolean D = false;
	//初始化数据handler
    BluetoothDataService m_blue_data_service = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//注册广播
    	IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    	intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  
    	intent.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
    	intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
    	intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); 
    	intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 
    	intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    	registerReceiver(m_bluetoothReceiver, intent);
		super.onCreate();
		if(D) Log.i(TAG,"BlueToothService OnCreate!");
		m_blue_data_service = BluetoothDataService.getInstance();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(m_bluetoothReceiver);
		super.onDestroy();
	}
	 //注册蓝牙监听广播
    private final BroadcastReceiver m_bluetoothReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
            if(D) Log.i(TAG,"蓝牙消息：" + action);	
            if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) //中断
            {
            	m_blue_data_service.m_blue_state.SetListen();
            	m_blue_data_service.GetBluetoothState(m_blue_data_service.m_blue_state);
            }
		}
	};
}

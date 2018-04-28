/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cnlaunch.bluetooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.bluetooth.service.BluetoothAdapterService;
import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.bluetooth.service.bluetoothClsUtils;
import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class BluetoothDeviceListActivity extends Activity implements BluetoothInterface{
    // Debugging
    private static final String TAG = "BluetoothDeviceListActivity";
    private static final boolean D = false;
    Context context = BluetoothDeviceListActivity.this;
    
    public static final int RESULT_OK = 1; //返回值OK
    public static final int RESULT_FAIL = 0; //返回值失败
    //Handler更新消息
    public static final int HAND_UPDATE_LIST = 101; //更新蓝牙已经配对列表
    public static final int HAND_ADD_LIST=102;	  //蓝牙新增列表
    public static final int HAND_UPDATE_AND_SEARCH = 103; //更新列表并开始查找蓝牙
    public static final int HAND_UPDATE_BAUND_BLUETOOTH = 104; //更新已经配对的蓝牙状态
    public static final int HAND_CHANGE_BLUETOOTH_NAME = 105; //蓝牙改名消息
    public static final int HAND_UPDATE_BLUETOOTH_STATE = 106; //设置蓝牙连接状态
    public static String EXTRA_DEVICE_ADDRESS = "device_address"; //put的蓝牙句柄
    //蓝牙连接状态
    public static final int BLUE_None = 1; 			//未连接
    public static final int BLUE_Connecting = 2; 	//正在连接..
    public static final int BLUE_Connected = 3;		//已经连接成功
    public static final int BLUE_Connectfail = 4;	//连接失败

    // Return Intent extra
    //public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //button 返回、开关、查找
    Button m_but_reurn,m_but_open,m_but_search;
    //注册ListView操作变量
    private ListView m_listview = null;
    //注册List适配器
    private DeviceListAdapter m_listadapter = null;
    //List数据源
    private List<HashMap<String,Object>> m_listdata = new ArrayList<HashMap<String,Object>>();
    //获取系统蓝牙设备
    private BluetoothAdapter m_BluetoothAdapter = null;
    //打开蓝牙进度条
    private ProgressDialog progressdiag;
    //UI更新Handler
    private MyHandler m_handler = null;
    //当前连接蓝牙列表序号
    int m_nowItem = 0;
    //初始化连接service//蓝牙服务
    BluetoothAdapterService m_bluttoothservice = BluetoothAdapterService.getInstance();
	//初始化数据handler
    BluetoothDataService m_blue_data_service = null;
	//修改蓝牙名称，相关变量
	private String m_changename = null;
	private String m_changemac = null;
	//蓝牙改步骤变量
	private int m_changenameprogress = 0; //大于0开始改服务
	private int m_changename_nowstep = 0; //当前正在执行的步骤
	private ProgressDialog progressdiag_1; //改名进度条对话框1
	private ProgressDialog progressdiag_2; //改名进度条对话框2
	private AlertDialog progressdiag_3; //改名进度条对话框3
	private boolean m_changename_ok = false; //蓝牙改名成功或者失败
	//启动改名线程
	ChangeNameProgress m_changenamethread = null;
	//test
	private int m_test = 0;
	//增加对接收数据的判断
	private boolean m_receive_switch = false;
	
	MyCarApplication application;
	//final LayoutInflater inflater = LayoutInflater.from(this); 
	
    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bluetooth_device_list);
        application = (MyCarApplication)getApplication();
        //Drawable drawable = getResources().getDrawable(R.drawable.bluetoothconnect_bkcolor);
        //this.getWindow().setBackgroundDrawable(drawable);
        //this.getWindow().
        //初始化返回值
        setResult(Activity.RESULT_CANCELED);
        //添加进度条
        progressdiag = new ProgressDialog(this);
        progressdiag.setMessage(getResources().getText(
				R.string.bluetoothconnect_open).toString());
        // 初始化界面控件
        //初始化handler
        m_handler = new MyHandler();
        //初始化返回按钮
        m_but_reurn = (Button) findViewById(R.id.bluetoothconnect_button_return);
        m_but_reurn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//m_blue_data_service.SetShowConnectActivity(false);
				Intent intent = getIntent();
			    setResult(RESULT_FAIL,intent);
			    m_blue_data_service.BlueConnectClose();
			    m_receive_switch = false;
				finish();
			}
		});
        //初始化重置蓝牙按钮
        m_but_open = (Button) findViewById(R.id.bluetoothconnect_button_open);
        m_but_open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 等待 蓝牙打开结束
				if(m_bluttoothservice.m_blue_state.IsConnected())
					m_bluttoothservice.StopService();
		     	new StopProgressdiag().start();
		     	progressdiag.show();
			}
		});
        //初始化查询按钮
        m_but_search = (Button) findViewById(R.id.bluetoothconnect_button_search);
        m_but_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//先清除未配对蓝牙列表
				//发消息更新列表
				Message msg = m_handler.obtainMessage(HAND_UPDATE_LIST,HAND_UPDATE_AND_SEARCH,0);
				m_handler.sendMessage(msg);
			}
		});
        //初始化listview
        m_listview = (ListView)findViewById(R.id.bluetoothconnect_listview);
        m_listadapter = new DeviceListAdapter(m_listdata); //初始化adapter
        m_listview.setAdapter(m_listadapter);//绑定adapter和listview
        //添加list点击事件
        m_listview.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		// TODO Auto-generated method stub
        		//取得点击对象ITEM
        		DeviceList holder = (DeviceList)arg1.getTag();
        		//点击开始建立连接或者进行配对
        		if(D) Log.i(TAG,"name=" + holder.Pdevice_name.getText().toString() + 
        				"Mac= " + holder.Pdevice_mac.getText().toString() + 
        				";  pair= " + holder.Pdevice_pair.getText().toString());
        		//增加对蓝牙打开的判断
        		if(m_BluetoothAdapter.isEnabled() == false)
        		{
        			DiagAlertDialog dlg = new DiagAlertDialog(BluetoothDeviceListActivity.this);
        			dlg.setMessage(R.string.bluetoothconnect_tip_bluecancel);
        			dlg.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = getIntent();
						    setResult(RESULT_FAIL,intent);
						    m_blue_data_service.BlueConnectClose();
							finish();
						}
					});
        			dlg.show();
        			return;
        		}
        		//先取消搜索
        		if(m_BluetoothAdapter.isDiscovering())
        			m_BluetoothAdapter.cancelDiscovery();
        		m_but_search.setClickable(true);
				m_but_search.setText(R.string.bluetoothconnect_button_search);
				//发送连接请求
				String address = holder.Pdevice_mac.getText().toString();
				if(address.length() != 17)
				{
					//提示版本不匹配，请升级
					Toast.makeText(context, context.getString(R.string.bluetoothconnect_mac_error),
								Toast.LENGTH_SHORT).show();
					return;
				}	
				
//				// 获取激活注册时保存的蓝牙地址
//				String caheMacAddress = application.getBluetoothMacAddress();
//				if (caheMacAddress != null)
//				{
//                    // 如果选择的地址和保存的蓝牙地址相同，说明是一个序列号，清空缓存的蓝牙地址
//				    if (caheMacAddress.equals(address))
//				    {
//				        application.removeBluetoothMac();
//				    }
//				    // 如果选择的地址和保存的蓝牙地址不同，说明不是一个序列号，拒绝连接
//				    else
//				    {
//	                    //提示序列号不同
//	                    Toast.makeText(context, context.getString(R.string.bluetoothconnect_illegal),
//	                                Toast.LENGTH_SHORT).show();
//				        return;
//				    }
//				}
				//查询是否已经建立连接
				if(m_blue_data_service.m_blue_state.IsConnected())
					return;
				//传递蓝牙名称
				 m_blue_data_service.SetConnectedBluetooth(holder.Pdevice_name.getText().toString(), holder.Pdevice_mac.getText().toString());
			    //建立连接
				m_bluttoothservice.ConnectDevice(address);
	            //设置连接状态
	            m_nowItem = arg2;
	            SetListBluetoothconnectState(m_nowItem,BLUE_Connecting);
        		//禁止点击List
        		//m_listview.setEnabled(false);
        		//禁用Button
        		//m_but_open.setEnabled(false);
        		//m_but_search.setEnabled(false);
        		//m_but_reurn.setEnabled(false);
        	}
		});
        //长按listview效果
        m_listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				AdapterContextMenuInfo menuInfo1 = (AdapterContextMenuInfo)menuInfo;
				int pos = (int)m_listadapter.getItemId(menuInfo1.position);
				HashMap<String, Object> now = null; //数据源
		    	now = (HashMap<String, Object>) m_listadapter.getItem(pos);
		    	
				menu.setHeaderTitle(now.get("device_name").toString());
				int i = 0;
				menu.add(0,i++,0,R.string.bluetoothconnect_set_changename);
				BluetoothDevice device = m_BluetoothAdapter.getRemoteDevice(now.get("device_mac").toString());
				if(device.getBondState() == BluetoothDevice.BOND_BONDED) //只有已经配对的菜添加取消配对按钮
					menu.add(0,i++,0,R.string.bluetoothconnect_set_cancelpair);
				menu.add(0,i++,0,R.string.bluetoothconnect_button_return);
			}
		});
     	// 注册这蓝牙查找BroadcastReceiver
     	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
     	filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  
     	filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
     	filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
     	filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); 
     	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 
    	registerReceiver(m_bluetoothReceiver, filter);
    	
    	//初始化蓝牙
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否有蓝牙设备
    	if(m_BluetoothAdapter == null)
    	{
    		if(D) Log.i(TAG,"无蓝牙设备!");
    		finish();
    	}
    	//启动进度条
        progressdiag.show();
        // 等待 蓝牙打开结束
     	new StartblueProgressdiag().start();
        //只更新列表不重置蓝牙
        //发消息更新列表
		//Message msg = m_handler.obtainMessage(HAND_UPDATE_LIST);
		//m_handler.sendMessage(msg);
     	//初始化Service
     	m_blue_data_service = BluetoothDataService.getInstance();
     	m_blue_data_service.AddObserver(this);
     	m_receive_switch = true;
     	m_blue_data_service.SetShowConnectActivity(true);
    }
    //处理按键消息
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	switch(keyCode)
    	{
    	case KeyEvent.KEYCODE_BACK:
    		m_blue_data_service.BlueConnectClose();
    	}
    	return super.onKeyDown(keyCode, event);
    }
    //长按list出现的菜单点击实现方法 
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	int pos = (int)m_listadapter.getItemId(menuInfo.position);
    	HashMap<String, Object> now = null; //数据源
    	now = (HashMap<String, Object>) m_listadapter.getItem(pos);
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case 0:    	//修改蓝牙名称
    		if(D) Log.i(TAG,"修改名称：" + pos);
    		
    		//获取修改的蓝牙名称和地址
    		m_changename = now.get("device_name").toString();
    		m_changemac = now.get("device_mac").toString();
    		m_nowItem = pos;
    		if(D) Log.i(TAG,"当前蓝牙名称：" + m_changename);
    		//弹出输入对话框
    		showDialog(0);
    		break;
    	case 1:		//取消配对
    		if(D) Log.i(TAG,"取消配对：" + pos);
    		BluetoothDevice device = m_BluetoothAdapter.getRemoteDevice(now.get("device_mac").toString());
    		try {
				bluetoothClsUtils.removeBond(device.getClass(), device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		break;
   		default:
   			break;
    	}
    	return super.onContextItemSelected(item);
    }
    //新建弹出对话框准备
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	// TODO Auto-generated method stub
    	super.onPrepareDialog(id, dialog);
    	if(D) Log.i(TAG,"onPrepareDialog");
    	if(id == 0)//输入名称对话框
    	{
    		if(D) Log.i(TAG,"设置输入框默认名称为：" + m_changename);
    		EditText edittext = (EditText)dialog.findViewById(R.id.bluetooth_inputname);
    		//先清除输入框中的名称
    		edittext.setText("");
    		if(m_changename.contains("DBS"))
    			edittext.setHint(m_changename.substring(4,m_changename.length()));
    		else
    			edittext.setHint(m_changename);
    	}
    	else if(id == 1)
    	{
    		if(m_changename_nowstep == 1)
    		{
    			progressdiag_1.setMessage(this.getString(R.string.bluetoothconnect_blue_connecting));
    		}	
    	}
    	else if(id == 2)
    	{
    		if(m_changename_nowstep == 1)
    		{
    			progressdiag_2.setMessage(this.getString(R.string.bluetoothdevicelist_connect_failed));
    		}
    		else if(m_changename_nowstep == 2) //发送密码验证命令
    		{
    			progressdiag_2.setMessage(this.getString(R.string.bluetoothdevicelist_password_check_failed));
    		}
    	}
    	else if(id == 3) //改名成功或者失败提示
    	{
    		if(D) Log.i(TAG,"prepare改名成功或者失败提示");
    		TextView text = (TextView)progressdiag_3.findViewById(R.id.bluetoothconnect_msg_text);
    		if(m_changename_ok == true)
    		{
    			text.setText(this.getString(R.string.bluetoothconnect_pair_changename_ok));
    		}
    		else
    		{
    			text.setText(this.getString(R.string.bluetoothconnect_pair_changename_false));
    		}
    	}
    }
    //新建一个弹出对话框
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"onCreateDialog");
    	if(id == 0)  //输入名称对话框
    	{
    		//获取布局
    		LayoutInflater inputlayout = getLayoutInflater();
    		final View inputview = inputlayout.inflate(R.layout.bluetooth_device_list_inputname,
    				(ViewGroup)findViewById(R.id.bluetooth_list_inputname));
    		Dialog diag =  new AlertDialog.Builder(context)
       	 	.setTitle(R.string.bluetoothconnect_input_title)
       	 	.setView(inputview)
       	 	.setPositiveButton(R.string.bluetoothconnect_input_ok, 
       			 new DialogInterface.OnClickListener() {
   					
   					@Override
   					public void onClick(DialogInterface dialog, int which) {
   						// TODO Auto-generated method stub
   						//确认处理方法,获取输入的名称
   						EditText edittext = (EditText)inputview.findViewById(R.id.bluetooth_inputname);
   						m_changename = "DBS " + edittext.getText().toString();
   						if(D) Log.i(TAG,"输入名称为：" + m_changename);
   						//隐藏软键盘
   						if(D) Log.i(TAG,"隐藏软键盘");
   						InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
   						//imm.showSoftInput(view,0); //显示软键盘
   						imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0); //隐藏软键盘
   						//建立线程开始改名服务
   						if(m_changenameprogress == 0)
   						{
   							if(D) Log.i(TAG,"开始改名线程");
   							m_changenamethread = new ChangeNameProgress();
   	   						m_changenameprogress = 1;
   	   						m_blue_data_service.SetSpecialObserver(true, BluetoothDeviceListActivity.this);
   	   						m_changenamethread.start();
   	   						//显示进度条
   	   					m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 0,-1,m_changename).sendToTarget(); //改名第一步
   						}
   						else
   						{
   							if(D) Log.i(TAG,"线程未结束，中断线程....");
   							Toast.makeText(context, context.getString(R.string.bluetoothconnect_pair_waitting), Toast.LENGTH_LONG).show();
   							m_changenamethread.interrupt();
   							m_changenameprogress = 0; 
   							m_blue_data_service.SetSpecialObserver(false, null);
   							
   						}
   					}
   				})
   			.setNegativeButton(R.string.bluetoothconnect_input_cancel, 
   					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//finish();
						}
					})
   			.create();
    		return diag;
    	}
    	else if(id == 1) //显示改名进度对话框
    	{
    		
    		progressdiag_1 = new ProgressDialog(context);  
    		progressdiag_1.setIcon(R.drawable.bluetooth_connect_title);  
    		progressdiag_1.setTitle(this.getString(R.string.bluetoothconnect_set_changename));  
    		progressdiag_1.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
    		progressdiag_1.setMessage(this.getString(R.string.bluetoothconnect_blue_connecting));
    		progressdiag_1.setButton2(this.getString(R.string.bluetoothconnect_input_cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(D) Log.i(TAG,"点击取消改名服务:" + m_changename_nowstep);
					//m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 1,-1).sendToTarget(); //改名第一步
					m_changenamethread.interrupt();//结束蓝牙改名线程
					m_changenameprogress = 0;
					m_blue_data_service.SetSpecialObserver(false, null);
				}
			});
    	    return progressdiag_1;  
    	}
    	else if(id == 2) //显示重试对话框
    	{
    		
    		progressdiag_2 =new ProgressDialog(context);  
    		progressdiag_2.setIcon(R.drawable.bluetooth_connect_title);  
    		progressdiag_2.setTitle(this.getString(R.string.bluetoothconnect_set_changename));  
    		progressdiag_2.setProgressStyle(ProgressDialog.BUTTON_POSITIVE); 
    		progressdiag_2.setMessage(this.getString(R.string.bluetoothdevicelist_zhixing_failed));
    		progressdiag_2.setButton(this.getString(R.string.bluetoothconnect_retry), new DialogInterface.OnClickListener(){  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                // TODO Auto-generated method stub  
	            	if(D) Log.i(TAG,"点击重试，step =" + m_changename_nowstep);
	            	//这里执行对话框失败重试事件
	            	if(m_changename_nowstep == 1) //连接状态重试
	            	{
	            		m_changenameprogress = m_changename_nowstep;
	            		showDialog(1);
	            		
	            	}
	            	else if(m_changename_nowstep == 2)
	            	{
	            		
	            	}
	            }  
	        });
    		progressdiag_2.setButton2(this.getString(R.string.bluetoothconnect_input_cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 1,-1).sendToTarget(); //改名第二步
					if(D) Log.i(TAG,"点击取消改名服务:" + m_changename_nowstep);
					m_changenamethread.interrupt();//结束蓝牙改名线程
					m_changenameprogress = 0;
					m_blue_data_service.SetSpecialObserver(false, null);
				}
			});
    	    return progressdiag_2;
    	}
    	else if(id == 3)  //蓝牙改名成功或者失败，提示
    	{
    		if(D) Log.i(TAG,"蓝牙改名完成提示");
    		LayoutInflater inputlayout = getLayoutInflater();
    		final View msgview = inputlayout.inflate(R.layout.bluetooth_device_msg_dialog,null);
    		progressdiag_3 =  new AlertDialog.Builder(context)
       	 	.setTitle(R.string.bluetoothconnect_set_changename)
       	 	.setView(msgview)
       	 	.setPositiveButton(R.string.bluetoothconnect_input_ok, 
       			 new DialogInterface.OnClickListener() {
   					
   					@Override
   					public void onClick(DialogInterface dialog, int which) {
   						// TODO Auto-generated method stub
   						//改名成功刷新蓝牙列表
   						m_changenameprogress = 0;//复位
   						m_blue_data_service.SetSpecialObserver(false, null);
   						if(m_changename_ok)
   							new StartblueProgressdiag().start();
   					}
   				})
   			.create();
    		return progressdiag_3;
    	}
    	else  //扩展...
    	{
    		
    	}
    	return super.onCreateDialog(id);
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	//查询蓝牙打开状态
    	m_test ++;
    	if(D) Log.i(TAG,"m_test = " + m_test);
    	if(D) Log.i(TAG,"Intent= " + getIntent().toString());
    	
    	super.onStart();
    }
    /*********** 设置蓝牙连接提示**********************/
    @SuppressWarnings("unchecked")
	void SetListBluetoothconnectState(int nowItem,int state)
    {
    	String v_status = null;
    	switch (state) 
    	{
		case BLUE_None:
			v_status = getString(R.string.bluetoothconnect_blue_connectnone);
			break;
		case BLUE_Connecting:
			v_status = getString(R.string.bluetoothconnect_blue_connecting);
			break;
		case BLUE_Connected:
			v_status = getString(R.string.bluetoothconnect_blue_connected);
			break;
		case BLUE_Connectfail:
			v_status = getString(R.string.bluetoothconnect_blue_connectfail);
			break;
		default:
			break;
		}
    	HashMap<String, Object> now = null; //数据源
    	now = (HashMap<String, Object>) m_listadapter.getItem(nowItem);
    	now.put("device_status", v_status);
    	if(m_listadapter != null)
    		m_listadapter.notifyDataSetChanged();
    }
    //改名服务线程,启动服务开始改名
    private class ChangeNameProgress extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		while(!this.isInterrupted() && m_changenameprogress > 0)
    		{
    			if(m_changenameprogress == 1) //连接蓝牙...
    			{
    				m_changename_nowstep = m_changenameprogress;
    				//先取消搜索
            		if(m_BluetoothAdapter.isDiscovering())
            			m_BluetoothAdapter.cancelDiscovery();
            		if(m_changemac.length() != 17)
    				{
    					//提示蓝牙MAC地址不正确...
    					Toast.makeText(context, context.getString(R.string.bluetoothconnect_mac_error),
    								Toast.LENGTH_SHORT).show();
    					return;
    				}
            		m_blue_data_service.SetConnectedBluetooth(m_changename, m_changemac);
    				m_bluttoothservice.ConnectDevice(m_changemac);
    	            //设置连接状态
    	            m_handler.obtainMessage(HAND_UPDATE_BLUETOOTH_STATE, m_nowItem,BLUE_Connecting).sendToTarget();
    	            m_changenameprogress = 100; //跳转等待...
            		
    			}
    			else if(m_changenameprogress == 2) //发送改名指令...
    			{
    				//发送蓝牙改名指令,需要先验证密码
    				if(D) Log.i(TAG,"发送21 0F指令");
                	byte [] v_cmd = new byte [] {0x21,0x0F};
                	byte [] v_sendbuf = new byte [] {0x00,0x07,0x30,0x30,0x30,0x30,0x30,0x30,0x00};
                	//验证安全密码，默认输入000000
                	m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
							v_cmd, v_sendbuf, v_sendbuf.length, 1000);
                	m_changename_nowstep = m_changenameprogress; //保存当前步骤
                	m_changenameprogress = 100; //跳转等待...
    			}
    			else if(m_changenameprogress == 3)
    			{
    				//发送蓝牙改名指令
                	byte [] v_cmd = new byte [] {0x21,0x08};
                	byte [] v_sendbuf = new byte [m_changename.length() + 3] ;
                	//拷贝名字
                	Arrays.fill(v_sendbuf, (byte)0x00);
                	v_sendbuf[0] = (byte)((m_changename.length() + 1) / 0x100);
                	v_sendbuf[1] = (byte)((m_changename.length() + 1) % 0x100);
                	for(int i = 0; i < m_changename.length(); i ++)
                	{
                		v_sendbuf[i + 2] = (byte) m_changename.charAt(i);
                	}
                	
                	//验证安全密码，默认输入000000
                	m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
							v_cmd, v_sendbuf, v_sendbuf.length, 1000);
                	m_changename_nowstep = m_changenameprogress; //保存当前步骤
                	m_changenameprogress = 100; //跳转等待...
    			}
    			else  //等待... 
    			{
    				try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    		if(D) Log.i(TAG,"改名线程结束");
    		m_changenameprogress = 0; //结束线程
    		m_blue_data_service.SetSpecialObserver(false, null);
    	}
    }
    //重置蓝牙线程
    class StopProgressdiag extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				//先关闭蓝牙
		    	if(m_BluetoothAdapter.isEnabled())
		    	{
		    		while(true)
		    		{
		    			sleep(500);
		    			if(m_BluetoothAdapter.disable() == true)
		    			{
		    				if(D) Log.i(TAG,"关闭重置蓝牙设备!");
		    				break;
		    			}
		    		}
		    	}
				//获取系统蓝牙设备打开状态
				while(true)
				{
					sleep(500);
					if(!m_BluetoothAdapter.isEnabled())
					{
						if(D) Log.i(TAG,"打开蓝牙设备!");
						//不做提示，强行打开 
			    		if(m_BluetoothAdapter.enable() == true) 
			    			break;
					}
					else
					{
						break;
					}
				}
				while(true)
				{
					sleep(500);
					if(m_BluetoothAdapter.isEnabled())
					{
						if (progressdiag.isShowing()) 
						{
							progressdiag.dismiss();
						}
						//发消息更新列表
						Message msg = m_handler.obtainMessage(HAND_UPDATE_LIST);
						m_handler.sendMessage(msg);
						break;
					}
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    }
    //启动蓝牙线程
    class StartblueProgressdiag extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				
				//获取系统蓝牙设备打开状态
				while(true)
				{
					sleep(100);
					if(!m_BluetoothAdapter.isEnabled())
					{
						if(D) Log.i(TAG,"打开蓝牙设备!");
						//不做提示，强行打开 
			    		if(m_BluetoothAdapter.enable() == true) 
			    			break;
					}
					else
					{
						break;
					}
				}
				while(true)
				{
					sleep(500);
					if(m_BluetoothAdapter.isEnabled())
					{
						if (progressdiag.isShowing()) 
						{
							progressdiag.dismiss();
						}
						if(D) Log.i(TAG,"发消息更新列表!");
						Message msg = m_handler.obtainMessage(HAND_UPDATE_LIST);
						m_handler.sendMessage(msg);
						break;
					}
				}	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    }
    
    //UI更新Handler
    private class MyHandler extends Handler
    {
    	@SuppressWarnings("unchecked")
		@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		super.handleMessage(msg);
    		if(msg.what == HAND_UPDATE_LIST)
			{
    			int i;
		    	//刷新蓝牙已配对列表
		    	if(D) Log.i(TAG,"清除设备列表!");
		    	m_listdata.clear(); //先清除LIST
		    	m_listadapter.notifyDataSetChanged();
		    	//更新列表
		    	Set<BluetoothDevice> v_bluetooth_pairedDevices = m_BluetoothAdapter.getBondedDevices();
		    	for (BluetoothDevice device : v_bluetooth_pairedDevices)
		    	{
		    		//增加名字过滤，只显示launch和DBS开头的蓝牙
		    		if(FilterBluetoothName(device.getName().toString()) == false)
		    			continue;
		    		HashMap<String, Object> v_data = new HashMap<String, Object>();
		    		v_data.put("device_icon", R.drawable.bluetooth_device_default);
		    		v_data.put("device_name", device.getName());
		    		//获取当前蓝牙的连接状态
		         	if(m_blue_data_service.m_blue_state.IsConnected())
		         	{
		         		if(m_blue_data_service.GetConnectedBluetoothMac()
		         				.equals(device.getAddress().toString()))
		         		{
		         			v_data.put("device_status", getString(R.string.bluetoothconnect_blue_connected));
		         		}
		         		else
		         			v_data.put("device_status", getString(R.string.bluetoothconnect_blue_connectnone));
		         	}
		         	else
		         		v_data.put("device_status", getString(R.string.bluetoothconnect_blue_connectnone));
		    		v_data.put("device_mac", device.getAddress());
		    		v_data.put("device_pair", getString(R.string.bluetoothconnect_pair));
		    		m_listdata.add(v_data);
		    		if(D) Log.i(TAG,"加入已配对设备+" + device.getName());
		    	}
		    	m_listadapter.notifyDataSetChanged();
		    	//判断是否需要查找设备
		    	if(msg.arg1 == HAND_UPDATE_AND_SEARCH)
		    	{
		    		if(D) Log.i(TAG,"查找设备...");
		    		if (m_BluetoothAdapter.isDiscovering()) {
		    			m_BluetoothAdapter.cancelDiscovery();
		            }
					m_BluetoothAdapter.startDiscovery();
					//屏蔽查找按钮
					m_but_search.setClickable(false);
					m_but_search.setText(R.string.bluetoothconnect_button_searching);
		    	}
		    	//关闭滚动条
		    	if (progressdiag.isShowing()) 
				{
					progressdiag.dismiss();
				}
			}
    		else if(msg.what == HAND_ADD_LIST) //添加未配对的蓝牙列表
    		{  			
    			BluetoothDevice device = (BluetoothDevice)msg.obj;
    			//增加名字过滤，只显示launch和DBS开头的蓝牙
    			if(FilterBluetoothName(device.getName().toString()) == false)
	    			return;
    			//增加重复查找的列表显示，过滤重复的设备名
    			if(FilterDuplicateName(m_listdata,device.getAddress().toString()) == true)
    					return;
				HashMap<String, Object> v_data = new HashMap<String, Object>();
	    		v_data.put("device_icon", R.drawable.bluetooth_device_enable);
	    		v_data.put("device_name", device.getName());
	    		v_data.put("device_status", getString(R.string.bluetoothconnect_blue_connectnone));
	    		v_data.put("device_mac", device.getAddress());
	    		v_data.put("device_pair", getString(R.string.bluetoothconnect_nopair));
	    		m_listdata.add(v_data);
	    		if(D) Log.i(TAG,"添加新搜索设备+" + device.getName());
	    		m_listadapter.notifyDataSetChanged();
    		}
    		else if(msg.what == HAND_UPDATE_BAUND_BLUETOOTH) //更新已经配对的蓝牙的状态图标
    		{
    			BluetoothDevice device = (BluetoothDevice)msg.obj;
    			//先查找蓝牙位置
    			for(int i = 0; i < m_listadapter.getCount(); i ++)
    			{
    				HashMap<String, Object> now = null; //数据源
    		    	now = (HashMap<String, Object>) m_listadapter.getItem(i);
    				if((now.get("device_mac").toString()).equals((device.getAddress().toString())))	
    				{
    					now.put("device_icon", R.drawable.bluetooth_device_enable);
    					now.put("device_name", device.getName());
    					break;
    				}
    			}
    			m_listadapter.notifyDataSetChanged();
    		}
    		else if(msg.what == HAND_CHANGE_BLUETOOTH_NAME) //蓝牙改名
    		{
    			if(D) Log.i(TAG,"蓝牙改名消息");
    			//第一步---启动进度条,开始建立连接
    			if(msg.arg1 == 0)
    			{
    				if(D) Log.i(TAG,"第一步---启动进度条,开始建立连接");
    				showDialog(1);
    			}
    			//第二步发送改名指令
    			else if(msg.arg1 == 1)
    			{
    				if(D) Log.i(TAG,"第二步发送改名指令");
    				progressdiag_1.setMessage(context.getString(R.string.bluetoothconnect_pair_checkpasswording));
					m_changenameprogress = 2; //第二步发送命令
    			}
    			//第三步 改名指令成功,断开蓝牙连接
    			else if(msg.arg1 == 2)
    			{
    				if(D) Log.i(TAG,"第三步 发送改名指令");
    				progressdiag_1.setMessage(context.getString(R.string.bluetoothconnect_pair_changenameing));
					m_changenameprogress = 3; //第二步发送命令
    			}
    			//第四步 重新刷新蓝牙列表
    			else if(msg.arg1 == 3)
    			{
    				if(D) Log.i(TAG,"第四步 蓝牙改名成功，关闭蓝牙连接");
    				m_blue_data_service.StopBlueService(BluetoothDeviceListActivity.this);
    				progressdiag_1.dismiss();
    				m_changename_ok = true;
    				showDialog(3);
    			}
    			//第五步 刷新完成，关闭进度条
    			else if(msg.arg1 == 4)
    			{
    				if(D) Log.i(TAG,"第五步 刷新完成，关闭进度条");
    			}
    			else //default处理
    			{
    				
    			}
    		}
    		else if(msg.what == HAND_UPDATE_BLUETOOTH_STATE) //设置蓝牙连接状态
    		{
    			SetListBluetoothconnectState(msg.arg1,msg.arg2);
    			if(msg.arg2 == BLUE_Connectfail)
    			{
    				if(m_changenameprogress > 0)
    				{
    					progressdiag_1.dismiss(); //关闭进度条1
    					//showDialog(2);//弹出重试和取消提示
    				}
    				else //暂不处理
    				{    	
    				}
    			}
    		}
    	}
    }
    //LIST点击事件，触发进行蓝牙配对
    
    
    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (m_BluetoothAdapter != null) {
        	m_BluetoothAdapter.cancelDiscovery();
        }
        //释放蓝牙服务观察者
        m_receive_switch = false;
        m_blue_data_service.DelObserver(this);
        m_blue_data_service.SetShowConnectActivity(false);
        m_blue_data_service.SetSpecialObserver(false, null);
        if(D) Log.i(TAG,"释放资源!");
        // 释放蓝牙查找接收广播
        this.unregisterReceiver(m_bluetoothReceiver);
    }
 
    // 初始化接收蓝牙查找广播接收器
    private final BroadcastReceiver m_bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if(D) Log.i(TAG,"接收消息：" + action);	
            // 判断查找到了设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) 
            {
                //获取设备信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断如果已经配对则不重复添加列表
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) 
                {	
                	//过滤掉空的蓝牙名
                	if(device.getName() == null || (device.getName()).equals("") || (device.getName()).equals("null"))
                	{
                	}
                	else
                	{
                		//发消息更新列表
						Message msg = m_handler.obtainMessage(HAND_ADD_LIST,device);
						m_handler.sendMessage(msg);
                	}
                }
                else //处理搜索到已经配对的设备
                {
                	//发消息更新已经配对的蓝牙信号状态
					Message msg = m_handler.obtainMessage(HAND_UPDATE_BAUND_BLUETOOTH,device);
					m_handler.sendMessage(msg);
                }
            
            } //当查找结束后重置状态
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            {
            	//屏蔽查找按钮
            	if(D) Log.e(TAG,"查找结束");
				m_but_search.setClickable(true);
				m_but_search.setText(R.string.bluetoothconnect_button_search);
            }
            //蓝牙状态改变
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
            	//获取设备信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                HashMap<String, Object> now = null; //数据源
                //查找设备列表位置
                for(int i = 0; i < m_listadapter.getCount(); i ++)
    			{
    		    	now = (HashMap<String, Object>) m_listadapter.getItem(i);
    				if((now.get("device_mac").toString()).equals((device.getAddress().toString())))	
    				{
    					if(device.getBondState() == BluetoothDevice.BOND_BONDED)
    						now.put("device_pair", getString(R.string.bluetoothconnect_pair));
    					else
    						now.put("device_pair", getString(R.string.bluetoothconnect_nopair));
    					break;
    				}
    			}
            	m_listadapter.notifyDataSetChanged();
            }
           
        }
    };
    //定义List类
    static class DeviceList
    {
    	private ImageView Pdevice_icon;  	//设备图标
    	private TextView  Pdevice_name;  	//设备名称
    	private TextView  Pdevice_status;	//连接状态提示
    	private TextView  Pdevice_mac;		//设备mac地址
    	private TextView  Pdevice_pair;		//设备配对状态
    }
    //自定义adapter适配器
    class DeviceListAdapter extends BaseAdapter{
    	private List<HashMap<String, Object>> mydata = null; //数据源
    	private LayoutInflater mInflater = null;
    	//构造方法
    	public DeviceListAdapter(List<HashMap<String, Object>> listdata) {
			// TODO Auto-generated constructor stub
    		mydata = listdata;
    		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
    	
    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return mydata.size();
    	}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
		    if (mydata != null && mydata.size() > 0)
		    {
		        
		        return mydata.get(position);
		    }
		    else
		    {
		        return null;
		    }
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DeviceList listview = null;
			
			if(convertView == null)
			{
				//导入布局
	            convertView = mInflater.inflate(R.layout.bluetooth_device_list_item, null);
	            //获得列表ITEM对象
	            listview = new DeviceList();
	            listview.Pdevice_icon = (ImageView)convertView.findViewById(R.id.bluetooth_connect_listitem_image);
	            listview.Pdevice_name = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_name);
	            listview.Pdevice_status = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_connectstatus);
	            listview.Pdevice_mac = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_macaddr);
	            listview.Pdevice_pair = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_pair);
	            //为view设置标签
	            convertView.setTag(listview);
			}
			else
			{
				// 取出listview
				listview = (DeviceList)convertView.getTag();
			}
			//传递参数
            listview.Pdevice_icon.setImageResource((Integer) mydata.get(position).get("device_icon"));
            listview.Pdevice_name.setText((String)mydata.get(position).get("device_name"));
            listview.Pdevice_status.setText((String)mydata.get(position).get("device_status"));
            listview.Pdevice_mac.setText((String)mydata.get(position).get("device_mac"));
            listview.Pdevice_pair.setText((String)mydata.get(position).get("device_pair"));
            //if(D) Log.e(TAG,"布局position = "+position+"name:" + (String)mydata.get(position).get("device_name"));
            
			return convertView;
		}
    	
    }
    @Override
    public void BlueConnected(String name, String mac) {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"蓝牙连接OK");
    	HashMap<String, Object> now = null; //数据源
    	now = (HashMap<String, Object>) m_listadapter.getItem(m_nowItem);
	    if(D) Log.i(TAG,"蓝牙连接成功至-->" + now.get("device_name").toString());
	    if(m_changenameprogress > 0)//蓝牙改名状态，特殊处理
	    {
	    	//蓝牙连接成功，进入下一步，发送改名指令
			if(D) Log.i(TAG,"蓝牙连接成功，开始发送改名指令....");
			//线程进入第二步
			m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 1,-1).sendToTarget(); //改名第二步
		
	    }
	    else
	    {
	    	if(D) Log.i(TAG,"结束自己!");
	    	m_blue_data_service.SetShowConnectActivity(false);
	    	m_receive_switch = false;
//	    	m_blue_data_service.DelObserver(this);
	    	finish();
	    }
    }
    @Override
    public void BlueConnectLost(String name, String mac) {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"蓝牙连接Lost");
    	m_handler.obtainMessage(HAND_UPDATE_BLUETOOTH_STATE, m_nowItem,BLUE_Connectfail).sendToTarget();
    }
    @Override
    public void GetDataFromService(byte[] databuf, int datalen) {
    	// TODO Auto-generated method stub
    	if(m_receive_switch == false) return;  //增加对观察者的补充
    	String writeMessage = BluetoothDataService.bytesToHexString(databuf,datalen);
    	if(D) Log.i(TAG,"收到：" + writeMessage);
    	byte [] v_data = databuf;
    	if(datalen > 9)
    	{
    		switch(v_data[8] & 0xFF)
    		{
    		case 0x10:  //验证密码
    			if((databuf[9] & 0xFF) == 0x00) //密码验证成功
    			{
    				//线程进入第三步
					m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 2,-1).sendToTarget(); //改名第三步
    			}
    			else //失败
    			{
    				progressdiag_1.dismiss(); //关闭进度条1
					showDialog(2);//弹出重试和取消提示
    			}
    			break;
    		case 0x08:	//蓝牙改名
    			if((v_data[9] & 0xFF) == 0x00) //改名成功
    			{
    				//线程进入第四步
					m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 3,-1).sendToTarget(); //改名第四步
    			}
    			else //失败
    			{
    				
    			}
    			break;
    		case 0x0F: //复位命令
    			if((v_data[9] & 0xFF) == 0x00) 
    			{
    				byte [] v_cmd = new byte [] {0x21,0x10};
                	byte [] v_sendbuf = new byte [] {0x00,0x07,0x30,0x30,0x30,0x30,0x30,0x30,0x00};
                	//验证安全密码，默认输入000000
                	m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
							v_cmd, v_sendbuf, v_sendbuf.length, 1000);
    			}
    		default:
    			break;
    		}
    	}
    }
    @Override
    public void GetDataTimeout() {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"超时");
    	if(m_changename_nowstep == 2) //密码验证超时
		{
			progressdiag_1.dismiss(); //关闭进度条1
			showDialog(2);//弹出重试和取消提示
		}
		else
		{
			m_blue_data_service.StopBlueService(BluetoothDeviceListActivity.this);
			progressdiag_1.dismiss();
			m_changename_ok = false;
			showDialog(3);
		}
    }
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
	//增加名字过滤，只显示launch和DBS开头的蓝牙
	boolean FilterBluetoothName(String bluename)
	{
		if(bluename.indexOf("launch") >= 0 ||
				bluename.indexOf("LAUNCH") >= 0 ||
				bluename.indexOf("DBS") >= 0)
			return true;
		else
			return false;	
	}
	//增加重复查找的列表显示，过滤重复的设备名,查找到则返回成功
	boolean FilterDuplicateName(List<HashMap<String,Object>> list,String macname)
	{
		if(list == null)
			return false;
		for(int i = 0; i < list.size(); i ++)
		{
			HashMap<String, Object> v_data = list.get(i);
			if(v_data.get("device_mac").toString().equals(macname))
				return true;
		}
		return false;
	}
}

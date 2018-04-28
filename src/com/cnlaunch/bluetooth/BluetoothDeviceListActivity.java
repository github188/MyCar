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
    
    public static final int RESULT_OK = 1; //����ֵOK
    public static final int RESULT_FAIL = 0; //����ֵʧ��
    //Handler������Ϣ
    public static final int HAND_UPDATE_LIST = 101; //���������Ѿ�����б�
    public static final int HAND_ADD_LIST=102;	  //���������б�
    public static final int HAND_UPDATE_AND_SEARCH = 103; //�����б���ʼ��������
    public static final int HAND_UPDATE_BAUND_BLUETOOTH = 104; //�����Ѿ���Ե�����״̬
    public static final int HAND_CHANGE_BLUETOOTH_NAME = 105; //����������Ϣ
    public static final int HAND_UPDATE_BLUETOOTH_STATE = 106; //������������״̬
    public static String EXTRA_DEVICE_ADDRESS = "device_address"; //put���������
    //��������״̬
    public static final int BLUE_None = 1; 			//δ����
    public static final int BLUE_Connecting = 2; 	//��������..
    public static final int BLUE_Connected = 3;		//�Ѿ����ӳɹ�
    public static final int BLUE_Connectfail = 4;	//����ʧ��

    // Return Intent extra
    //public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //button ���ء����ء�����
    Button m_but_reurn,m_but_open,m_but_search;
    //ע��ListView��������
    private ListView m_listview = null;
    //ע��List������
    private DeviceListAdapter m_listadapter = null;
    //List����Դ
    private List<HashMap<String,Object>> m_listdata = new ArrayList<HashMap<String,Object>>();
    //��ȡϵͳ�����豸
    private BluetoothAdapter m_BluetoothAdapter = null;
    //������������
    private ProgressDialog progressdiag;
    //UI����Handler
    private MyHandler m_handler = null;
    //��ǰ���������б����
    int m_nowItem = 0;
    //��ʼ������service//��������
    BluetoothAdapterService m_bluttoothservice = BluetoothAdapterService.getInstance();
	//��ʼ������handler
    BluetoothDataService m_blue_data_service = null;
	//�޸��������ƣ���ر���
	private String m_changename = null;
	private String m_changemac = null;
	//�����Ĳ������
	private int m_changenameprogress = 0; //����0��ʼ�ķ���
	private int m_changename_nowstep = 0; //��ǰ����ִ�еĲ���
	private ProgressDialog progressdiag_1; //�����������Ի���1
	private ProgressDialog progressdiag_2; //�����������Ի���2
	private AlertDialog progressdiag_3; //�����������Ի���3
	private boolean m_changename_ok = false; //���������ɹ�����ʧ��
	//���������߳�
	ChangeNameProgress m_changenamethread = null;
	//test
	private int m_test = 0;
	//���ӶԽ������ݵ��ж�
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
        //��ʼ������ֵ
        setResult(Activity.RESULT_CANCELED);
        //��ӽ�����
        progressdiag = new ProgressDialog(this);
        progressdiag.setMessage(getResources().getText(
				R.string.bluetoothconnect_open).toString());
        // ��ʼ������ؼ�
        //��ʼ��handler
        m_handler = new MyHandler();
        //��ʼ�����ذ�ť
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
        //��ʼ������������ť
        m_but_open = (Button) findViewById(R.id.bluetoothconnect_button_open);
        m_but_open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// �ȴ� �����򿪽���
				if(m_bluttoothservice.m_blue_state.IsConnected())
					m_bluttoothservice.StopService();
		     	new StopProgressdiag().start();
		     	progressdiag.show();
			}
		});
        //��ʼ����ѯ��ť
        m_but_search = (Button) findViewById(R.id.bluetoothconnect_button_search);
        m_but_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//�����δ��������б�
				//����Ϣ�����б�
				Message msg = m_handler.obtainMessage(HAND_UPDATE_LIST,HAND_UPDATE_AND_SEARCH,0);
				m_handler.sendMessage(msg);
			}
		});
        //��ʼ��listview
        m_listview = (ListView)findViewById(R.id.bluetoothconnect_listview);
        m_listadapter = new DeviceListAdapter(m_listdata); //��ʼ��adapter
        m_listview.setAdapter(m_listadapter);//��adapter��listview
        //���list����¼�
        m_listview.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		// TODO Auto-generated method stub
        		//ȡ�õ������ITEM
        		DeviceList holder = (DeviceList)arg1.getTag();
        		//�����ʼ�������ӻ��߽������
        		if(D) Log.i(TAG,"name=" + holder.Pdevice_name.getText().toString() + 
        				"Mac= " + holder.Pdevice_mac.getText().toString() + 
        				";  pair= " + holder.Pdevice_pair.getText().toString());
        		//���Ӷ������򿪵��ж�
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
        		//��ȡ������
        		if(m_BluetoothAdapter.isDiscovering())
        			m_BluetoothAdapter.cancelDiscovery();
        		m_but_search.setClickable(true);
				m_but_search.setText(R.string.bluetoothconnect_button_search);
				//������������
				String address = holder.Pdevice_mac.getText().toString();
				if(address.length() != 17)
				{
					//��ʾ�汾��ƥ�䣬������
					Toast.makeText(context, context.getString(R.string.bluetoothconnect_mac_error),
								Toast.LENGTH_SHORT).show();
					return;
				}	
				
//				// ��ȡ����ע��ʱ�����������ַ
//				String caheMacAddress = application.getBluetoothMacAddress();
//				if (caheMacAddress != null)
//				{
//                    // ���ѡ��ĵ�ַ�ͱ����������ַ��ͬ��˵����һ�����кţ���ջ����������ַ
//				    if (caheMacAddress.equals(address))
//				    {
//				        application.removeBluetoothMac();
//				    }
//				    // ���ѡ��ĵ�ַ�ͱ����������ַ��ͬ��˵������һ�����кţ��ܾ�����
//				    else
//				    {
//	                    //��ʾ���кŲ�ͬ
//	                    Toast.makeText(context, context.getString(R.string.bluetoothconnect_illegal),
//	                                Toast.LENGTH_SHORT).show();
//				        return;
//				    }
//				}
				//��ѯ�Ƿ��Ѿ���������
				if(m_blue_data_service.m_blue_state.IsConnected())
					return;
				//������������
				 m_blue_data_service.SetConnectedBluetooth(holder.Pdevice_name.getText().toString(), holder.Pdevice_mac.getText().toString());
			    //��������
				m_bluttoothservice.ConnectDevice(address);
	            //��������״̬
	            m_nowItem = arg2;
	            SetListBluetoothconnectState(m_nowItem,BLUE_Connecting);
        		//��ֹ���List
        		//m_listview.setEnabled(false);
        		//����Button
        		//m_but_open.setEnabled(false);
        		//m_but_search.setEnabled(false);
        		//m_but_reurn.setEnabled(false);
        	}
		});
        //����listviewЧ��
        m_listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				AdapterContextMenuInfo menuInfo1 = (AdapterContextMenuInfo)menuInfo;
				int pos = (int)m_listadapter.getItemId(menuInfo1.position);
				HashMap<String, Object> now = null; //����Դ
		    	now = (HashMap<String, Object>) m_listadapter.getItem(pos);
		    	
				menu.setHeaderTitle(now.get("device_name").toString());
				int i = 0;
				menu.add(0,i++,0,R.string.bluetoothconnect_set_changename);
				BluetoothDevice device = m_BluetoothAdapter.getRemoteDevice(now.get("device_mac").toString());
				if(device.getBondState() == BluetoothDevice.BOND_BONDED) //ֻ���Ѿ���ԵĲ����ȡ����԰�ť
					menu.add(0,i++,0,R.string.bluetoothconnect_set_cancelpair);
				menu.add(0,i++,0,R.string.bluetoothconnect_button_return);
			}
		});
     	// ע������������BroadcastReceiver
     	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
     	filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  
     	filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
     	filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
     	filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); 
     	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); 
    	registerReceiver(m_bluetoothReceiver, filter);
    	
    	//��ʼ������
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //�ж��Ƿ��������豸
    	if(m_BluetoothAdapter == null)
    	{
    		if(D) Log.i(TAG,"�������豸!");
    		finish();
    	}
    	//����������
        progressdiag.show();
        // �ȴ� �����򿪽���
     	new StartblueProgressdiag().start();
        //ֻ�����б���������
        //����Ϣ�����б�
		//Message msg = m_handler.obtainMessage(HAND_UPDATE_LIST);
		//m_handler.sendMessage(msg);
     	//��ʼ��Service
     	m_blue_data_service = BluetoothDataService.getInstance();
     	m_blue_data_service.AddObserver(this);
     	m_receive_switch = true;
     	m_blue_data_service.SetShowConnectActivity(true);
    }
    //��������Ϣ
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
    //����list���ֵĲ˵����ʵ�ַ��� 
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	int pos = (int)m_listadapter.getItemId(menuInfo.position);
    	HashMap<String, Object> now = null; //����Դ
    	now = (HashMap<String, Object>) m_listadapter.getItem(pos);
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case 0:    	//�޸���������
    		if(D) Log.i(TAG,"�޸����ƣ�" + pos);
    		
    		//��ȡ�޸ĵ��������ƺ͵�ַ
    		m_changename = now.get("device_name").toString();
    		m_changemac = now.get("device_mac").toString();
    		m_nowItem = pos;
    		if(D) Log.i(TAG,"��ǰ�������ƣ�" + m_changename);
    		//��������Ի���
    		showDialog(0);
    		break;
    	case 1:		//ȡ�����
    		if(D) Log.i(TAG,"ȡ����ԣ�" + pos);
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
    //�½������Ի���׼��
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	// TODO Auto-generated method stub
    	super.onPrepareDialog(id, dialog);
    	if(D) Log.i(TAG,"onPrepareDialog");
    	if(id == 0)//�������ƶԻ���
    	{
    		if(D) Log.i(TAG,"���������Ĭ������Ϊ��" + m_changename);
    		EditText edittext = (EditText)dialog.findViewById(R.id.bluetooth_inputname);
    		//�����������е�����
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
    		else if(m_changename_nowstep == 2) //����������֤����
    		{
    			progressdiag_2.setMessage(this.getString(R.string.bluetoothdevicelist_password_check_failed));
    		}
    	}
    	else if(id == 3) //�����ɹ�����ʧ����ʾ
    	{
    		if(D) Log.i(TAG,"prepare�����ɹ�����ʧ����ʾ");
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
    //�½�һ�������Ի���
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"onCreateDialog");
    	if(id == 0)  //�������ƶԻ���
    	{
    		//��ȡ����
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
   						//ȷ�ϴ�����,��ȡ���������
   						EditText edittext = (EditText)inputview.findViewById(R.id.bluetooth_inputname);
   						m_changename = "DBS " + edittext.getText().toString();
   						if(D) Log.i(TAG,"��������Ϊ��" + m_changename);
   						//���������
   						if(D) Log.i(TAG,"���������");
   						InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
   						//imm.showSoftInput(view,0); //��ʾ�����
   						imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0); //���������
   						//�����߳̿�ʼ��������
   						if(m_changenameprogress == 0)
   						{
   							if(D) Log.i(TAG,"��ʼ�����߳�");
   							m_changenamethread = new ChangeNameProgress();
   	   						m_changenameprogress = 1;
   	   						m_blue_data_service.SetSpecialObserver(true, BluetoothDeviceListActivity.this);
   	   						m_changenamethread.start();
   	   						//��ʾ������
   	   					m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 0,-1,m_changename).sendToTarget(); //������һ��
   						}
   						else
   						{
   							if(D) Log.i(TAG,"�߳�δ�������ж��߳�....");
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
    	else if(id == 1) //��ʾ�������ȶԻ���
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
					if(D) Log.i(TAG,"���ȡ����������:" + m_changename_nowstep);
					//m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 1,-1).sendToTarget(); //������һ��
					m_changenamethread.interrupt();//�������������߳�
					m_changenameprogress = 0;
					m_blue_data_service.SetSpecialObserver(false, null);
				}
			});
    	    return progressdiag_1;  
    	}
    	else if(id == 2) //��ʾ���ԶԻ���
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
	            	if(D) Log.i(TAG,"������ԣ�step =" + m_changename_nowstep);
	            	//����ִ�жԻ���ʧ�������¼�
	            	if(m_changename_nowstep == 1) //����״̬����
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
					//m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 1,-1).sendToTarget(); //�����ڶ���
					if(D) Log.i(TAG,"���ȡ����������:" + m_changename_nowstep);
					m_changenamethread.interrupt();//�������������߳�
					m_changenameprogress = 0;
					m_blue_data_service.SetSpecialObserver(false, null);
				}
			});
    	    return progressdiag_2;
    	}
    	else if(id == 3)  //���������ɹ�����ʧ�ܣ���ʾ
    	{
    		if(D) Log.i(TAG,"�������������ʾ");
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
   						//�����ɹ�ˢ�������б�
   						m_changenameprogress = 0;//��λ
   						m_blue_data_service.SetSpecialObserver(false, null);
   						if(m_changename_ok)
   							new StartblueProgressdiag().start();
   					}
   				})
   			.create();
    		return progressdiag_3;
    	}
    	else  //��չ...
    	{
    		
    	}
    	return super.onCreateDialog(id);
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	//��ѯ������״̬
    	m_test ++;
    	if(D) Log.i(TAG,"m_test = " + m_test);
    	if(D) Log.i(TAG,"Intent= " + getIntent().toString());
    	
    	super.onStart();
    }
    /*********** ��������������ʾ**********************/
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
    	HashMap<String, Object> now = null; //����Դ
    	now = (HashMap<String, Object>) m_listadapter.getItem(nowItem);
    	now.put("device_status", v_status);
    	if(m_listadapter != null)
    		m_listadapter.notifyDataSetChanged();
    }
    //���������߳�,��������ʼ����
    private class ChangeNameProgress extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		while(!this.isInterrupted() && m_changenameprogress > 0)
    		{
    			if(m_changenameprogress == 1) //��������...
    			{
    				m_changename_nowstep = m_changenameprogress;
    				//��ȡ������
            		if(m_BluetoothAdapter.isDiscovering())
            			m_BluetoothAdapter.cancelDiscovery();
            		if(m_changemac.length() != 17)
    				{
    					//��ʾ����MAC��ַ����ȷ...
    					Toast.makeText(context, context.getString(R.string.bluetoothconnect_mac_error),
    								Toast.LENGTH_SHORT).show();
    					return;
    				}
            		m_blue_data_service.SetConnectedBluetooth(m_changename, m_changemac);
    				m_bluttoothservice.ConnectDevice(m_changemac);
    	            //��������״̬
    	            m_handler.obtainMessage(HAND_UPDATE_BLUETOOTH_STATE, m_nowItem,BLUE_Connecting).sendToTarget();
    	            m_changenameprogress = 100; //��ת�ȴ�...
            		
    			}
    			else if(m_changenameprogress == 2) //���͸���ָ��...
    			{
    				//������������ָ��,��Ҫ����֤����
    				if(D) Log.i(TAG,"����21 0Fָ��");
                	byte [] v_cmd = new byte [] {0x21,0x0F};
                	byte [] v_sendbuf = new byte [] {0x00,0x07,0x30,0x30,0x30,0x30,0x30,0x30,0x00};
                	//��֤��ȫ���룬Ĭ������000000
                	m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
							v_cmd, v_sendbuf, v_sendbuf.length, 1000);
                	m_changename_nowstep = m_changenameprogress; //���浱ǰ����
                	m_changenameprogress = 100; //��ת�ȴ�...
    			}
    			else if(m_changenameprogress == 3)
    			{
    				//������������ָ��
                	byte [] v_cmd = new byte [] {0x21,0x08};
                	byte [] v_sendbuf = new byte [m_changename.length() + 3] ;
                	//��������
                	Arrays.fill(v_sendbuf, (byte)0x00);
                	v_sendbuf[0] = (byte)((m_changename.length() + 1) / 0x100);
                	v_sendbuf[1] = (byte)((m_changename.length() + 1) % 0x100);
                	for(int i = 0; i < m_changename.length(); i ++)
                	{
                		v_sendbuf[i + 2] = (byte) m_changename.charAt(i);
                	}
                	
                	//��֤��ȫ���룬Ĭ������000000
                	m_blue_data_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
							v_cmd, v_sendbuf, v_sendbuf.length, 1000);
                	m_changename_nowstep = m_changenameprogress; //���浱ǰ����
                	m_changenameprogress = 100; //��ת�ȴ�...
    			}
    			else  //�ȴ�... 
    			{
    				try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    		if(D) Log.i(TAG,"�����߳̽���");
    		m_changenameprogress = 0; //�����߳�
    		m_blue_data_service.SetSpecialObserver(false, null);
    	}
    }
    //���������߳�
    class StopProgressdiag extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				//�ȹر�����
		    	if(m_BluetoothAdapter.isEnabled())
		    	{
		    		while(true)
		    		{
		    			sleep(500);
		    			if(m_BluetoothAdapter.disable() == true)
		    			{
		    				if(D) Log.i(TAG,"�ر����������豸!");
		    				break;
		    			}
		    		}
		    	}
				//��ȡϵͳ�����豸��״̬
				while(true)
				{
					sleep(500);
					if(!m_BluetoothAdapter.isEnabled())
					{
						if(D) Log.i(TAG,"�������豸!");
						//������ʾ��ǿ�д� 
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
						//����Ϣ�����б�
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
    //���������߳�
    class StartblueProgressdiag extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				
				//��ȡϵͳ�����豸��״̬
				while(true)
				{
					sleep(100);
					if(!m_BluetoothAdapter.isEnabled())
					{
						if(D) Log.i(TAG,"�������豸!");
						//������ʾ��ǿ�д� 
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
						if(D) Log.i(TAG,"����Ϣ�����б�!");
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
    
    //UI����Handler
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
		    	//ˢ������������б�
		    	if(D) Log.i(TAG,"����豸�б�!");
		    	m_listdata.clear(); //�����LIST
		    	m_listadapter.notifyDataSetChanged();
		    	//�����б�
		    	Set<BluetoothDevice> v_bluetooth_pairedDevices = m_BluetoothAdapter.getBondedDevices();
		    	for (BluetoothDevice device : v_bluetooth_pairedDevices)
		    	{
		    		//�������ֹ��ˣ�ֻ��ʾlaunch��DBS��ͷ������
		    		if(FilterBluetoothName(device.getName().toString()) == false)
		    			continue;
		    		HashMap<String, Object> v_data = new HashMap<String, Object>();
		    		v_data.put("device_icon", R.drawable.bluetooth_device_default);
		    		v_data.put("device_name", device.getName());
		    		//��ȡ��ǰ����������״̬
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
		    		if(D) Log.i(TAG,"����������豸+" + device.getName());
		    	}
		    	m_listadapter.notifyDataSetChanged();
		    	//�ж��Ƿ���Ҫ�����豸
		    	if(msg.arg1 == HAND_UPDATE_AND_SEARCH)
		    	{
		    		if(D) Log.i(TAG,"�����豸...");
		    		if (m_BluetoothAdapter.isDiscovering()) {
		    			m_BluetoothAdapter.cancelDiscovery();
		            }
					m_BluetoothAdapter.startDiscovery();
					//���β��Ұ�ť
					m_but_search.setClickable(false);
					m_but_search.setText(R.string.bluetoothconnect_button_searching);
		    	}
		    	//�رչ�����
		    	if (progressdiag.isShowing()) 
				{
					progressdiag.dismiss();
				}
			}
    		else if(msg.what == HAND_ADD_LIST) //���δ��Ե������б�
    		{  			
    			BluetoothDevice device = (BluetoothDevice)msg.obj;
    			//�������ֹ��ˣ�ֻ��ʾlaunch��DBS��ͷ������
    			if(FilterBluetoothName(device.getName().toString()) == false)
	    			return;
    			//�����ظ����ҵ��б���ʾ�������ظ����豸��
    			if(FilterDuplicateName(m_listdata,device.getAddress().toString()) == true)
    					return;
				HashMap<String, Object> v_data = new HashMap<String, Object>();
	    		v_data.put("device_icon", R.drawable.bluetooth_device_enable);
	    		v_data.put("device_name", device.getName());
	    		v_data.put("device_status", getString(R.string.bluetoothconnect_blue_connectnone));
	    		v_data.put("device_mac", device.getAddress());
	    		v_data.put("device_pair", getString(R.string.bluetoothconnect_nopair));
	    		m_listdata.add(v_data);
	    		if(D) Log.i(TAG,"����������豸+" + device.getName());
	    		m_listadapter.notifyDataSetChanged();
    		}
    		else if(msg.what == HAND_UPDATE_BAUND_BLUETOOTH) //�����Ѿ���Ե�������״̬ͼ��
    		{
    			BluetoothDevice device = (BluetoothDevice)msg.obj;
    			//�Ȳ�������λ��
    			for(int i = 0; i < m_listadapter.getCount(); i ++)
    			{
    				HashMap<String, Object> now = null; //����Դ
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
    		else if(msg.what == HAND_CHANGE_BLUETOOTH_NAME) //��������
    		{
    			if(D) Log.i(TAG,"����������Ϣ");
    			//��һ��---����������,��ʼ��������
    			if(msg.arg1 == 0)
    			{
    				if(D) Log.i(TAG,"��һ��---����������,��ʼ��������");
    				showDialog(1);
    			}
    			//�ڶ������͸���ָ��
    			else if(msg.arg1 == 1)
    			{
    				if(D) Log.i(TAG,"�ڶ������͸���ָ��");
    				progressdiag_1.setMessage(context.getString(R.string.bluetoothconnect_pair_checkpasswording));
					m_changenameprogress = 2; //�ڶ�����������
    			}
    			//������ ����ָ��ɹ�,�Ͽ���������
    			else if(msg.arg1 == 2)
    			{
    				if(D) Log.i(TAG,"������ ���͸���ָ��");
    				progressdiag_1.setMessage(context.getString(R.string.bluetoothconnect_pair_changenameing));
					m_changenameprogress = 3; //�ڶ�����������
    			}
    			//���Ĳ� ����ˢ�������б�
    			else if(msg.arg1 == 3)
    			{
    				if(D) Log.i(TAG,"���Ĳ� ���������ɹ����ر���������");
    				m_blue_data_service.StopBlueService(BluetoothDeviceListActivity.this);
    				progressdiag_1.dismiss();
    				m_changename_ok = true;
    				showDialog(3);
    			}
    			//���岽 ˢ����ɣ��رս�����
    			else if(msg.arg1 == 4)
    			{
    				if(D) Log.i(TAG,"���岽 ˢ����ɣ��رս�����");
    			}
    			else //default����
    			{
    				
    			}
    		}
    		else if(msg.what == HAND_UPDATE_BLUETOOTH_STATE) //������������״̬
    		{
    			SetListBluetoothconnectState(msg.arg1,msg.arg2);
    			if(msg.arg2 == BLUE_Connectfail)
    			{
    				if(m_changenameprogress > 0)
    				{
    					progressdiag_1.dismiss(); //�رս�����1
    					//showDialog(2);//�������Ժ�ȡ����ʾ
    				}
    				else //�ݲ�����
    				{    	
    				}
    			}
    		}
    	}
    }
    //LIST����¼������������������
    
    
    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (m_BluetoothAdapter != null) {
        	m_BluetoothAdapter.cancelDiscovery();
        }
        //�ͷ���������۲���
        m_receive_switch = false;
        m_blue_data_service.DelObserver(this);
        m_blue_data_service.SetShowConnectActivity(false);
        m_blue_data_service.SetSpecialObserver(false, null);
        if(D) Log.i(TAG,"�ͷ���Դ!");
        // �ͷ��������ҽ��չ㲥
        this.unregisterReceiver(m_bluetoothReceiver);
    }
 
    // ��ʼ�������������ҹ㲥������
    private final BroadcastReceiver m_bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if(D) Log.i(TAG,"������Ϣ��" + action);	
            // �жϲ��ҵ����豸
            if (BluetoothDevice.ACTION_FOUND.equals(action)) 
            {
                //��ȡ�豸��Ϣ
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // �ж�����Ѿ�������ظ�����б�
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) 
                {	
                	//���˵��յ�������
                	if(device.getName() == null || (device.getName()).equals("") || (device.getName()).equals("null"))
                	{
                	}
                	else
                	{
                		//����Ϣ�����б�
						Message msg = m_handler.obtainMessage(HAND_ADD_LIST,device);
						m_handler.sendMessage(msg);
                	}
                }
                else //�����������Ѿ���Ե��豸
                {
                	//����Ϣ�����Ѿ���Ե������ź�״̬
					Message msg = m_handler.obtainMessage(HAND_UPDATE_BAUND_BLUETOOTH,device);
					m_handler.sendMessage(msg);
                }
            
            } //�����ҽ���������״̬
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            {
            	//���β��Ұ�ť
            	if(D) Log.e(TAG,"���ҽ���");
				m_but_search.setClickable(true);
				m_but_search.setText(R.string.bluetoothconnect_button_search);
            }
            //����״̬�ı�
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
            	//��ȡ�豸��Ϣ
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                HashMap<String, Object> now = null; //����Դ
                //�����豸�б�λ��
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
    //����List��
    static class DeviceList
    {
    	private ImageView Pdevice_icon;  	//�豸ͼ��
    	private TextView  Pdevice_name;  	//�豸����
    	private TextView  Pdevice_status;	//����״̬��ʾ
    	private TextView  Pdevice_mac;		//�豸mac��ַ
    	private TextView  Pdevice_pair;		//�豸���״̬
    }
    //�Զ���adapter������
    class DeviceListAdapter extends BaseAdapter{
    	private List<HashMap<String, Object>> mydata = null; //����Դ
    	private LayoutInflater mInflater = null;
    	//���췽��
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
				//���벼��
	            convertView = mInflater.inflate(R.layout.bluetooth_device_list_item, null);
	            //����б�ITEM����
	            listview = new DeviceList();
	            listview.Pdevice_icon = (ImageView)convertView.findViewById(R.id.bluetooth_connect_listitem_image);
	            listview.Pdevice_name = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_name);
	            listview.Pdevice_status = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_connectstatus);
	            listview.Pdevice_mac = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_macaddr);
	            listview.Pdevice_pair = (TextView)convertView.findViewById(R.id.bluetooth_connect_listitem_pair);
	            //Ϊview���ñ�ǩ
	            convertView.setTag(listview);
			}
			else
			{
				// ȡ��listview
				listview = (DeviceList)convertView.getTag();
			}
			//���ݲ���
            listview.Pdevice_icon.setImageResource((Integer) mydata.get(position).get("device_icon"));
            listview.Pdevice_name.setText((String)mydata.get(position).get("device_name"));
            listview.Pdevice_status.setText((String)mydata.get(position).get("device_status"));
            listview.Pdevice_mac.setText((String)mydata.get(position).get("device_mac"));
            listview.Pdevice_pair.setText((String)mydata.get(position).get("device_pair"));
            //if(D) Log.e(TAG,"����position = "+position+"name:" + (String)mydata.get(position).get("device_name"));
            
			return convertView;
		}
    	
    }
    @Override
    public void BlueConnected(String name, String mac) {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"��������OK");
    	HashMap<String, Object> now = null; //����Դ
    	now = (HashMap<String, Object>) m_listadapter.getItem(m_nowItem);
	    if(D) Log.i(TAG,"�������ӳɹ���-->" + now.get("device_name").toString());
	    if(m_changenameprogress > 0)//��������״̬�����⴦��
	    {
	    	//�������ӳɹ���������һ�������͸���ָ��
			if(D) Log.i(TAG,"�������ӳɹ�����ʼ���͸���ָ��....");
			//�߳̽���ڶ���
			m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 1,-1).sendToTarget(); //�����ڶ���
		
	    }
	    else
	    {
	    	if(D) Log.i(TAG,"�����Լ�!");
	    	m_blue_data_service.SetShowConnectActivity(false);
	    	m_receive_switch = false;
//	    	m_blue_data_service.DelObserver(this);
	    	finish();
	    }
    }
    @Override
    public void BlueConnectLost(String name, String mac) {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"��������Lost");
    	m_handler.obtainMessage(HAND_UPDATE_BLUETOOTH_STATE, m_nowItem,BLUE_Connectfail).sendToTarget();
    }
    @Override
    public void GetDataFromService(byte[] databuf, int datalen) {
    	// TODO Auto-generated method stub
    	if(m_receive_switch == false) return;  //���ӶԹ۲��ߵĲ���
    	String writeMessage = BluetoothDataService.bytesToHexString(databuf,datalen);
    	if(D) Log.i(TAG,"�յ���" + writeMessage);
    	byte [] v_data = databuf;
    	if(datalen > 9)
    	{
    		switch(v_data[8] & 0xFF)
    		{
    		case 0x10:  //��֤����
    			if((databuf[9] & 0xFF) == 0x00) //������֤�ɹ�
    			{
    				//�߳̽��������
					m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 2,-1).sendToTarget(); //����������
    			}
    			else //ʧ��
    			{
    				progressdiag_1.dismiss(); //�رս�����1
					showDialog(2);//�������Ժ�ȡ����ʾ
    			}
    			break;
    		case 0x08:	//��������
    			if((v_data[9] & 0xFF) == 0x00) //�����ɹ�
    			{
    				//�߳̽�����Ĳ�
					m_handler.obtainMessage(HAND_CHANGE_BLUETOOTH_NAME, 3,-1).sendToTarget(); //�������Ĳ�
    			}
    			else //ʧ��
    			{
    				
    			}
    			break;
    		case 0x0F: //��λ����
    			if((v_data[9] & 0xFF) == 0x00) 
    			{
    				byte [] v_cmd = new byte [] {0x21,0x10};
                	byte [] v_sendbuf = new byte [] {0x00,0x07,0x30,0x30,0x30,0x30,0x30,0x30,0x00};
                	//��֤��ȫ���룬Ĭ������000000
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
    	if(D) Log.i(TAG,"��ʱ");
    	if(m_changename_nowstep == 2) //������֤��ʱ
		{
			progressdiag_1.dismiss(); //�رս�����1
			showDialog(2);//�������Ժ�ȡ����ʾ
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
	//�������ֹ��ˣ�ֻ��ʾlaunch��DBS��ͷ������
	boolean FilterBluetoothName(String bluename)
	{
		if(bluename.indexOf("launch") >= 0 ||
				bluename.indexOf("LAUNCH") >= 0 ||
				bluename.indexOf("DBS") >= 0)
			return true;
		else
			return false;	
	}
	//�����ظ����ҵ��б���ʾ�������ظ����豸��,���ҵ��򷵻سɹ�
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

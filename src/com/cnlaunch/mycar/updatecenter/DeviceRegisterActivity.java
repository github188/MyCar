package com.cnlaunch.mycar.updatecenter;

import java.util.ArrayList;
import java.util.List;

import launch.SearchIdUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.connection.ConnectionListener;
import com.cnlaunch.mycar.updatecenter.connection.ConnectionManager;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponseHandler;
import com.cnlaunch.mycar.updatecenter.device.DeviceSerialInfoQueryThread;
import com.cnlaunch.mycar.updatecenter.device.SNRegisterResult;
import com.cnlaunch.mycar.updatecenter.device.SerialNumber;
import com.cnlaunch.mycar.updatecenter.location.LocationInfo;
import com.cnlaunch.mycar.updatecenter.location.LocationInfoListener;
import com.cnlaunch.mycar.updatecenter.location.LocationInfoWrapper;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.updatecenter.task.TaskListener;
import com.cnlaunch.mycar.updatecenter.tools.TimeoutCounter;
import com.cnlaunch.mycar.updatecenter.webservice.SoapMethod;
import com.cnlaunch.mycar.updatecenter.webservice.SoapRequest;
import com.cnlaunch.mycar.updatecenter.webservice.SoapResponse;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceListener;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceOperator;

/**
 * 诊断设备注册 ,若成功 则产品激活。
 * */
public class DeviceRegisterActivity extends BaseActivity implements OnClickListener,ConnectionListener
{
    private final static String TAG = "DeviceRegisterActivity";
    private boolean D = true;
    boolean isFront = false;
	Button btDevActivate;
    Button btGoback;
    Button btGoNext;
    Button btCancel;
    
    View  deviceInfoArea;
    ProgressDialog registerProductDialog;
    
    CustomDialog checkSerialNumberDialog;
    TimeoutCounter timeoutCounter;
    
    TextView  tvTitle;
    TextView  tvSerialNumber;
    TextView  tvSerialNumberStatus;
    TextView  tvEmail;
    TextView  tvInstruction;

	EditText edtBluetoothName;
	EditText edtDiagPassword;
	
    String  serialNumber = "";//序列号 
    String  chipID = "";// 序列号密码 从设备获取
    String  deviceName = "";// 设备名[蓝牙]
    String  deviceMac = "";// 设备mac地址[蓝牙]
    WebServiceOperator webSerive;
    
    Connection bluetoothConnection;
    DeviceResponseHandler deviceResponseHandler;
    Context  context = DeviceRegisterActivity.this;
    Activity activity = DeviceRegisterActivity.this;
    MyCarApplication application;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_registration,R.layout.custom_title);
        setCustomeTitleLeft(R.string.upc_device_register);
        setCustomeTitleRight("");
        initViews();
        
        application = (MyCarApplication) getApplication();
        
        deviceResponseHandler = new DeviceResponseHandler(this);
        webSerive = new WebServiceOperator(this);
        
        bluetoothConnection = ConnectionManager.getInstance().getConnection(this, "bluetooth");
        bluetoothConnection.removeConnectListener(this);
        bluetoothConnection.addConnectListener(this);
        bluetoothConnection.openConnection("");
//        bluetoothConnection.openConnection(application.getDevice().get(0).getMac());
    }
    			
	@Override
	protected void onStop()
	{
		bluetoothConnection.removeConnectListener(this);
		//releaseSource();
		super.onStop();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		isFront = true;
	}

	@Override
	protected void onPause()
	{
	    // TODO Auto-generated method stub
	    super.onPause();
	    isFront = false;
	    
	}
	public void initViews()
	{
		tvSerialNumber = (TextView) findViewById(R.id.tv_serial_number);
		tvSerialNumberStatus = (TextView) findViewById(R.id.tv_serial_number_status);
		
		edtBluetoothName = (EditText) findViewById(R.id.edt_bluetooth_name);
		edtDiagPassword = (EditText) findViewById(R.id.edt_diag_password);
		 
		deviceInfoArea = findViewById(R.id.middle_area);
		deviceInfoArea.setVisibility(View.GONE);
		 
	    btDevActivate = (Button) findViewById(R.id.bt_activate_device);
	    btDevActivate.setOnClickListener(this);
	    
	    btGoback = (Button) findViewById(R.id.bt_dev_activate_goback);
	    btGoback.setOnClickListener(this);
	}
		
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.bt_activate_device:
				break;
			case R.id.bt_dev_activate_goback:
				goBack();
				break;
			default:
				break;
		}
	}
	
/**
 *
	验证未通过有以下几种情况：
	1.code=401                    参数为空
	
	2.code=405                    产品不存在
	
	3.code=655                    密码不正确
	
	4.code=654                    产品状态不正确（既验证不通过）
	
	5. code=0  state=651           产品已注册
	
	6. code=0   state=652          产品已作废
	
	7. code=0   state=650          产品未售出
	
	验证通过： code=0    state=653   产品未注册（经销商已做过销售登记可以注册）
* */
	
	final  Handler  handler = new Handler();
    
    private void goBack()
    {
    	finish();
    }  
    CustomAlertDialog dlg;
    private void errorInfoDialog(int message)
    {
        if (dlg == null)
        {
            dlg = new CustomAlertDialog(this);
        }
    	dlg.setTitle(R.string.upc_error);
    	dlg.setCancelable(false);
    	dlg.setMessage(message);
    	dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
    	{
    		@Override
    		public void onClick(View v)
    		{
    			dlg.dismiss();
    			finish();
    		}
    	});
    	if (isFront)
    	{
    	    dlg.show();
    	}
    }
    
    private void errorInfoDialog(String message)
    {
    	final CustomAlertDialog dlg = new CustomAlertDialog(this);
    	dlg.setTitle(R.string.upc_error);
    	dlg.setCancelable(false);
    	dlg.setMessage(message);
    	dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
    	{
    		@Override
    		public void onClick(View v)
    		{
    			dlg.dismiss();
    			finish();
    		}
    	});
    	if (isFront)
    	{
    	    dlg.show();
    	}
    }
    boolean closeFlag = true;
    private void failReadDevieInfoDialog(String message)
    {
        final CustomAlertDialog dlg = new CustomAlertDialog(this);
        dlg.setTitle(R.string.upc_error);
        dlg.setCancelable(false);
        dlg.setMessage(message);
        if (closeFlag)
        {
            dlg.setPositiveButton(R.string.upc_retry,new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dlg.dismiss();
                    {
                        bluetoothConnection.openConnection("");
                    }
                }
            });
        }
        else
        {
            dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dlg.dismiss();
                    {
                        finish();
                    }
                }
            });
        }
            if (isFront)
            {
                dlg.show();
            }
       
    }
   
	@Override
	public void onConnectionStart(String addr, Object extra)
	{
	}

	@Override
	public void onConnecting(String addr, String name)
	{
	}

	@Override
	public void onConnectionEstablished(final String addr, final String name)
	{
		deviceMac = addr;
		if(D) Log.d(TAG,"成功连接到设备 MAC :"+deviceMac);
		// 设备序列号查询
		new DeviceSerialInfoQueryThread(context,bluetoothConnection,deviceResponseHandler,new TaskListener()
		{
			ProgressDialog dlg;
			@Override
			public void onStart(final Object param)
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						dlg = new ProgressDialog(context);
						dlg.setOnKeyListener(new OnKeyListener()
						{
							@Override
							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
							{
								if((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH)
										&& event.getAction()== KeyEvent.ACTION_UP)
								{
									dialog.dismiss();
									if(param != null && param instanceof DeviceSerialInfoQueryThread)
									{
										DeviceSerialInfoQueryThread thread = (DeviceSerialInfoQueryThread)param;
										thread.destroy();
									}
									finish();
								}
								return true;
							}
						});
						dlg.setMessage(getString(R.string.upc_checking_device));
						if (isFront)
						{
						    dlg.show();
						    
						}
					}
				});
			}
			
			@Override
			public void onFinish(Object result)
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if(dlg!=null)dlg.dismiss();
					}
				});
				// 保存硬件信息，验证序列号
				if(result instanceof SerialInfo)
				{
					SerialInfo info = (SerialInfo)result;
					serialNumber = info.getSerialNumber();
					chipID = info.getChipId();
					application.saveDeviceInfo(name, addr, chipID,serialNumber);
					application.setBluetoothMac(serialNumber);
					webSerive.removeAllListeners();
					webSerive.addListener(new SerialInfoCheckListener(info));// 验证序列号
					webSerive.chekcSerialNumber(info.getSerialNumber(), info.getChipId());
					//releaseSource() ;
				}
			}
		    public void releaseSource() 
		    {
		        BluetoothDataService m_blue_service = BluetoothDataService.getInstance();
		        try
		        {
		            if (m_blue_service.IsConnected())
		            {
//		                m_blue_service.StopBlueService(this);
		                m_blue_service.finalize();
		            }
		        }
		        catch (Throwable e)
		        {
		            // TODO: handle exception
		            if (D)
		                Log.e(TAG, e.getMessage());
		        }
		    }
			@Override
			public void onError(int code, Object reason)
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if(dlg!=null)dlg.dismiss();
						failReadDevieInfoDialog(getString(R.string.upc_device_serial_info_querying_failed));
					}
				});
			}
		}).start();
	}

	@Override
	public void onConnectionLost(String addr, String name)
	{
	    if(D) Log.d(TAG,"蓝牙已经断开☆☆☆☆☆☆☆☆☆"+name);
	}

	@Override
	public void onResponse(final byte[] data, Object extra)
	{
	       if(D) Log.d(TAG,"收到蓝牙响应☆☆☆☆☆☆☆☆☆"+data);
		if(deviceResponseHandler != null)
		{
			deviceResponseHandler.handleResponse(data);			
		}
	}

	@Override
	public void onConnectionCancel()
	{	
	       if(D) Log.d(TAG,"☆☆☆☆☆☆☆☆☆onConnectionCancel蓝牙已经取消");
//		bluetoothConnection.removeConnectListener(this);
//		handler.post(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				finish();
//			}
//		});
	}

	@Override
	public void onTimeout()
	{
		bluetoothConnection.removeConnectListener(this);
	}
	
	// 设备序列号信息验证监听
	class SerialInfoCheckListener implements WebServiceListener
	{
		ProgressDialog dlg;
		WebServiceListener thiz  = this;
		SerialInfo snInfo;
		
		public SerialInfoCheckListener(SerialInfo info)
		{
			this.snInfo = info;
		}
		
		@Override
		public void onStartWebServiceRequest(Object service,SoapRequest request)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					dlg = new ProgressDialog(context);
					dlg.setOnKeyListener(new OnKeyListener()
					{
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
						{
							if((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH)
									&& event.getAction()== KeyEvent.ACTION_UP)
							{
								dialog.dismiss();
								webSerive.removeListener(thiz);
								finish();
							}
							return true;
						}
					});
					dlg.setMessage(getString(R.string.upc_checking_device));
					if (isFront)
					{
					    dlg.show();
					}
				}
			});
		}

		@Override
		public void onWebServiceSuccess(Object service, final SoapResponse response)
		{
			webSerive.removeListener(thiz);
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if ( dlg!=null ) dlg.dismiss();
					// 判断序列号的状态
					if (response.getMethod().equals(SoapMethod.CHECK_SERIAL_NUMBER))
					{
						if (response.getCode() == SerialNumber.STATE_NOT_REGISTERED)// 设备未注册
						{
                            webSerive.addListener(new DeviceRegisterListener(snInfo));
                            webSerive.registerProduct(snInfo.getSerialNumber(), snInfo.getChipId(), deviceMac,
                                                            "",// 经度
                                                            "");// 纬度

						}
						else if (response.getCode() == SerialNumber.STATE_REGISTERED)// 设备已经注册
						{
							Log.d(TAG,"设备已经被当前用户注册");
							handler.post(new Runnable()
							{
								@Override
								public void run()
								{
								    // 退出蓝牙
								    
									//showToastS("该设备已经被注册");
									Intent intent = new Intent(DeviceRegisterActivity.this,DiagSoftConfigureActivity.class);
									intent.putExtra("serialInfo", snInfo);
									startActivity(intent);
									finish();
								}
							});
							
						}
						else if (response.getCode() == SerialNumber.STATE_REGISTERED_BY_OTHERS)
						{
							Log.d(TAG, "设备序列号"+snInfo.getSerialNumber()+"已经被其他用户注册");
							handler.post(new Runnable()
							{
								@Override
								public void run()
							            	{
									errorInfoDialog(R.string.upc_the_device_is_registerd_by_others);
								}
							});
						}
						else if (response.getCode() == SerialNumber.STATE_ERROR_INVALID_SN)
						{
							Log.d(TAG,"设备序列号 "+snInfo.getSerialNumber()+"不合法");
							handler.post(new Runnable()
							{
								@Override
								public void run()
								{
									errorInfoDialog(R.string.upc_invalid_product_status);
								}
							});
						}
	                      else if (response.getCode() == SerialNumber.STATE_EXPIRED)
	                        {
	                            Log.d(TAG,"设备序列号 "+snInfo.getSerialNumber()+"已经作废");
	                            handler.post(new Runnable()
	                            {
	                                @Override
	                                public void run()
	                                {
	                                    errorInfoDialog(R.string.upc_expired);
	                                }
	                            });
	                        }
	                      else if (response.getCode() == SerialNumber.STATE_REGISTERED__INVALID_CHIP_ID)
	                        {
	                            Log.d(TAG,"设备序列号 "+snInfo.getSerialNumber()+"错误的芯片ID");
	                            handler.post(new Runnable()
	                            {
	                                @Override
	                                public void run()
	                                {
	                                    errorInfoDialog(R.string.upc_invalid_chip_id);
	                                }
	                            });
	                        }
					}
				}
			});
		}

		@Override
		public void onWebServiceErrors(Object service,int code,SoapRequest request)
		{
			webSerive.removeListener(thiz);
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if( dlg!=null ) dlg.dismiss();
					errorInfoDialog(R.string.upc_network_communication_error);
				}
			});
		}
	}

	// 设备注册监听
	class DeviceRegisterListener implements WebServiceListener
	{
		ProgressDialog pdlg;
		WebServiceListener thiz = this;
		SerialInfo snInfo;
		
		public DeviceRegisterListener(SerialInfo info)
		{
			this.snInfo = info;
		}	
		
		@Override
		public void onStartWebServiceRequest(Object service,SoapRequest request)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					pdlg = new ProgressDialog(context);
					pdlg.setOnKeyListener(new OnKeyListener()
					{
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
						{
							if((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH)
									&& event.getAction()== KeyEvent.ACTION_UP)
							{
								dialog.dismiss();
								webSerive.removeListener(thiz);
								finish();
							}
							return true;
						}
					});
					pdlg.setMessage(getString(R.string.upc_register_device));
					if (isFront)
					{
					    
					    pdlg.show();
					}
				}
			});
		}

		@Override
		public void onWebServiceSuccess(Object service, final SoapResponse response)
		{
			// 注册结果返回处理
			webSerive.removeListener(thiz);
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if(pdlg!=null)pdlg.dismiss();
				}
			});
			
			if(response.getMethod().equals(SoapMethod.REGISTER_PRODUCT))/**序列号注册结果**/
			{
				if(response.getCode() == SNRegisterResult.RESULT_REGISTER_OK)
				{
					Log.d(TAG,"序列号:"+snInfo.getSerialNumber()+"注册成功");
		               List serials = new ArrayList<String>();
	                    serials.add(serialNumber);
	                    application.setSerialNumber(serials);
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							if(pdlg != null)
							{
								pdlg.dismiss();
							}
							
							final CustomAlertDialog dlg = new CustomAlertDialog(DeviceRegisterActivity.this);
							dlg.setTitle(R.string.upc_tips);
							dlg.setMessage(R.string.upc_device_register_succesfully);
							dlg.setCancelable(false);
							dlg.setPositiveButton(R.string.ok, new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									dlg.dismiss();
									Intent intent = new Intent(DeviceRegisterActivity.this,DiagSoftConfigureActivity.class);
									Log.d(TAG,"注册成功 序列号,芯片ID: "+serialNumber+","+chipID);
									SerialInfo siInfo = new SerialInfo(serialNumber,chipID);
									intent.putExtra("serialInfo", siInfo);
									startActivity(intent);
									finish();
								}
							});
							if (isFront)
							dlg.show();
						
						}
					});
				}
				else if(response.getCode() == SNRegisterResult.RESULT_SERVER_EXCEPTION
						 || response.getCode() == SNRegisterResult.RESULT_FAILED)
				{
					Log.d(TAG,"序列号:"+ snInfo.getSerialNumber()+"注册失败!");
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							errorInfoDialog(R.string.upc_failed_to_registered_device);
						}
					});
				}
			}
		}

		@Override
		public void onWebServiceErrors(Object service,int code,SoapRequest request)
		{
			webSerive.removeListener(thiz);
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if(pdlg!=null)pdlg.dismiss();
					errorInfoDialog(getString(R.string.upc_failed_to_registered_device));
				}
			});
		}
	}
}

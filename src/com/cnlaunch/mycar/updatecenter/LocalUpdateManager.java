package com.cnlaunch.mycar.updatecenter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.updatecenter.tools.FileScanner;
import com.cnlaunch.mycar.updatecenter.tools.FileScanner.Listener;

public class LocalUpdateManager extends BaseActivity implements OnClickListener,BluetoothInterface
{
	private final static String TAG = "LocalUpdateManager";
	
	String vehicleDir = UpdateCenterConstants.VEHICLES_DIR;// 诊断升级文件夹目录
	String locale;
	
	String vehicle = "";
	String version = "";
	String language = "";
	/**
	 * 操作按钮
	 * */
	Button btnGoBack;
	Button btnGoUpdate;
	Button btnWriteConfig;
	
	Button btnVehicle;
	Button btnVersion;
	Button btnLanaguage;
	
	Context context  =  LocalUpdateManager.this;
	
	ArrayList<String> vehicles = new ArrayList<String>();	
	ArrayList<String> versions = new ArrayList<String>();
	ArrayList<String> languages= new ArrayList<String>();
	
	BluetoothDataService bluetoothDataService = BluetoothDataService.getInstance();
	boolean isFront = false;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.upc_diag_management,R.layout.custom_title);
        setCustomeTitleLeft(R.string.upc_local_update);
        setCustomeTitleRight("");
        scanVehicles();
		initViews();
		bluetoothDataService.AddObserver(this);
		
//		 vehicle = "TOYOTA";
//		 version = "V03.05";
//		 language = "CN";
	}
	
	@Override
	protected void onResume()
	{
	    // TODO Auto-generated method stub
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
	private void initViews()
	{
		btnGoBack = (Button) findViewById(R.id.bt_go_back);
		btnGoBack.setOnClickListener(this);
		
		btnGoUpdate = (Button) findViewById(R.id.bt_go_update);
		btnGoUpdate.setOnClickListener(this);
		
		btnVehicle = (Button) findViewById(R.id.bt_vehicle);
		btnVehicle.setOnClickListener(new SelectionListener());
		btnVehicle.setText(R.string.upc_choose);
		
		btnVersion = (Button) findViewById(R.id.bt_version);
		btnVersion.setOnClickListener(new SelectionListener());
		btnVersion.setText(R.string.upc_choose);
		
//		btnLanaguage = (Button) findViewById(R.id.bt_language);
//		btnLanaguage.setOnClickListener(new SelectionListener());
//		btnLanaguage.setText(defaultText_ChooseLanguage);
	}
	
	// 扫描可用的车系
	private void scanVehicles()
	{
		vehicles.clear();
		File vehicleDir = new File(UpdateCenterConstants.VEHICLES_DIR);
		String[] files = vehicleDir.list();
		if(files==null)return;
		for (int i = 0; i < files.length; i++)
		{
			vehicles.add(files[i]);
		}
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.bt_go_back:
				finish();
				break;
			case R.id.bt_go_update:
				// 获取配置参数列表
				// 弹出对话框
				// 点击确定，跳转到升级界面
				vehicle = btnVehicle.getText().toString();
				version = btnVersion.getText().toString();
				DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
				params.setVehiecle(vehicle);
				params.setVersion(version);
				language = Locale.getDefault().getLanguage();// 获取语言类型
				params.setLanguage(localeConvert(language));
				params.setFileAbsolutePath(vehicleDir+File.separator+vehicle+File.separator+version);
				Intent intent = new Intent(this,FirmwareUpdate.class);
				intent.putExtra("diagsoft_update_config_params", params);
				startActivity(intent);
				finish();
				break;
			default:
				break;
		}
	}
	
	private  String localeConvert(String loc)
	{
		if(loc.equalsIgnoreCase("zh"))
			return "CN";
		return loc.toUpperCase();
	}
	
	class SelectionListener implements OnClickListener
	{
		String title = getResources().getString(R.string.upc_choose);
		ArrayList<String> data = null;
		@Override
		public void onClick(final View v)
		{
			switch (v.getId())
			{
				case R.id.bt_vehicle:
					scanVehicles();
					data = vehicles;
					break;
				case R.id.bt_version:
					data = versions;
					break;
				default:
					break;
			}
			final CustomAlertDialog dlg = new CustomAlertDialog(LocalUpdateManager.this);
			dlg.setTitle(title);
			ListView lv = new ListView(LocalUpdateManager.this);
			lv.setDividerHeight(1);
			lv.setDivider(getResources().getDrawable(R.drawable.main_divider));
			lv.setFooterDividersEnabled(true);
			lv.setScrollingCacheEnabled(false);
			dlg.setView(lv);
			lv.setAdapter(new CustomAdapter(context,data));
			lv.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id)
				{
					Button bt = (Button) v;
					String oldText = bt.getText().toString();
					String newText = data.get(position);
					if(!oldText.equals(newText))
					{
						bt.setText(newText);
						if(v.getId()==R.id.bt_vehicle)
						{
							vehicle = newText;
							versions.clear();
							File[] versionList = new File(UpdateCenterConstants.VEHICLES_DIR+File.separator+vehicle).listFiles(new FileFilter()
							{
								@Override
								public boolean accept(File pathname)
								{
									return pathname.isDirectory();
								}
							});
							if(versionList != null)
							{   
								int len = versionList.length;
								for (int i = 0; i < len; i++) 
								{
									versions.add(versionList[i].getName());
								}
							}
						}
					}
					dlg.dismiss();
				}
			});
			dlg.setPositiveButton(R.string.upc_cancel, new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dlg.dismiss();
				}
			});
			dlg.setOnKeyListener(new OnKeyListener()
			{
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
				{
					if(keyCode == KeyEvent.KEYCODE_BACK)
					{
						dlg.dismiss();
						finish();
					}
					return false;
				}
			});
			if (isFront)
			dlg.show();	
		}
	}
	
	// 车型名字过滤器
	final FileFilter vendorFilter = new FileFilter()
	{			
		@Override
		public boolean accept(File f)
		{
			if(f.isDirectory())// 是车型名称
				return true;
			else
				return false;
		}
	};
	
	interface FileScanListener
	{
		public void onScanStart(File dir);
		public void onScanning(File dir,File f);
		public void onScanFinish(File dir,File[] list);
	}
	
	// 菜单
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		menu.add(0, 0, 1, R.string.upc_downloadbin_update);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int aFeatureId, MenuItem aItem)
	{
		switch (aItem.getItemId())
		{
			case 0:
				Log.e(TAG,"Download.bin Update");
				
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private class CustomAdapter extends BaseAdapter
	{
		ArrayList<String> data;
		Context cont;
		LayoutInflater inf;

		public CustomAdapter(Context c,ArrayList<String> data)
		{
			cont = c;
			inf = LayoutInflater.from(cont);
			this.data = data;
		}
		public CustomAdapter(Context c,String[] d)
		{
			cont = c;
			inf = LayoutInflater.from(cont);
			this.data = new ArrayList<String>();
			for (int i = 0; i < d.length; i++)
			{
				this.data.add(d[i]);
			}
		}
		
		@Override
		public int getCount()
		{
			return data.size();
		}
		@Override
		public Object getItem(int position)
		{
			return data.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = inf.inflate(R.layout.diagsoft_config_list, null);
			}
			TextView tv = (TextView)convertView.findViewById(R.id.text);
			tv.setText(data.get(position));
			return convertView;
		}
	 }
	
	 private void verifyDeviceConnection(Runnable callback)
	 {
		if (!isDeviceConnected()) 
		{
			establishBluetoothConnection(callback);
		}
		else
		{
			new Thread(callback).start();
		}
	 }
	 
	Runnable connectedCallback;
	
	private void establishBluetoothConnection(Runnable callback)
	{
		connectedCallback = callback;
		if(bluetoothDataService!= null)
		{
			bluetoothDataService.ShowBluetoothConnectActivity(this);
		}	
	}
	
	private boolean isDeviceConnected()
	{
		boolean connected = false;
		if(bluetoothDataService != null)
		{
			connected = bluetoothDataService.IsConnected();
		}
		Log.e(TAG," 设备已连接: "+connected);
		return connected;
	}
	
	@Override
	public void BlueConnectLost(String aName, String aMac)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void BlueConnected(String aName, String aMac)
	{
		if(connectedCallback != null)
		{
			new Thread(connectedCallback).start();
		}
	}

	@Override
	public void GetDataFromService(final byte[] aDatabuf, int aDatalen)
	{
		// TODO Auto-generated method stub
		uiHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				byte[] dpuPackage = OrderUtils.filterReturnDataPackage(aDatabuf);
				dpuResponseHandle(dpuPackage);
			}
		});
	}
	
	private void dpuResponseHandle(byte[] data)
	{
		byte[] cmdWordAndCmdParam = OrderUtils
				.filterOutCommandAndCommandParameters(data);
		byte[] cmdWord = OrderUtils.filterOutCommand(data);
		String cmd_subcmd_paramStr = OrderUtils
				.bytesToHexStringNoBar(cmdWordAndCmdParam);
		String cmd_subcmdStr = OrderUtils.bytesToHexStringNoBar(cmdWord);
		if(cmd_subcmdStr.equals("6112"))
		{
			if(cmd_subcmd_paramStr.equals("611200"))
			{
				Log.d(TAG,"写入配置文件成功");
				uiHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(context, R.string.dpu_write_sysini_ok, Toast.LENGTH_SHORT);
					}
				});
			}
			else
			{
				Log.e(TAG,"写入配置文件失败");
				uiHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(context, R.string.dpu_write_sysini_failed, Toast.LENGTH_SHORT);
					}
				});
			}
		}
	}
	
	@Override
	public void GetDataTimeout()
	{
		
	}
	final Handler uiHandler = new Handler();

	@Override
	public void BlueConnectClose()
	{
		// TODO Auto-generated method stub
		uiHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				finish();
			}
		});
	}
}

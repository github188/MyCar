package com.cnlaunch.mycar.updatecenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
import com.cnlaunch.mycar.common.ui.CustomProgressDialog;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.updatecenter.dbscar.ApkUpdateInfo;
import com.cnlaunch.mycar.updatecenter.http.DefaultHttpListener;
import com.cnlaunch.mycar.updatecenter.http.HttpDownloadManager;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.updatecenter.tools.NetworkChecker;
import com.cnlaunch.mycar.updatecenter.tools.PackageInstaller;
import com.cnlaunch.mycar.updatecenter.tools.SDCardChecker;
import com.cnlaunch.mycar.updatecenter.webservice.SoapMethod;
import com.cnlaunch.mycar.updatecenter.webservice.SoapRequest;
import com.cnlaunch.mycar.updatecenter.webservice.SoapResponse;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceListener;
import com.cnlaunch.mycar.updatecenter.webservice.WebServiceOperator;


public class UpdateCenterMainActivity extends BaseActivity 
{
	private final static String TAG = "UpdateCenterMainActivity.java";
	private boolean D = true;
	private ArrayList<MenuListItem> mMenuListItems;
	private ListView mMenuList = null;
	
	public static final int UPDATE_MY_CAR = 0;
	public static final int UPDATE_DIAG_CARD = 1;
	public static final int FINISH_FIRMWARE_INSTALLATION = 2;
	
	private Activity activity = UpdateCenterMainActivity.this;
	
	private SharedPreferences shUpdateSettings;
	
	WebServiceOperator webservice;
	HttpDownloadManager httpDownloader;
	DefaultHttpListener downloadListener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_center_main_page,R.layout.custom_title);
        setCustomeTitleLeft(R.string.upc_update_center);
        setCustomeTitleRight("");

        shUpdateSettings = getSharedPreferences(UpdateCenterConstants.SHARE_PREF_UPDATE_SETTINGS, 0);
        webservice = new WebServiceOperator(this);
        httpDownloader = new HttpDownloadManager(this);
        downloadListener = new ApkDownloadListener();
        
        //new CopyDownloadbinThread(this).start();
        initViews();
    }
    
    private void initViews()
    {
    	mMenuListItems = new ArrayList<MenuListItem>();
    	/*index: 0 客户端升级*/
        mMenuListItems.add(new MenuListItem(
		    			R.drawable.upc_updte_mycar_icon,
		    			getString(R.string.upc_dbscar_update),
		    			R.drawable.upc_list_triangle
    					));
        /*index: 1  设备升级*/
    	mMenuListItems.add(new MenuListItem(
    					R.drawable.upc_diag_card_update_icon,
    					getString(R.string.upc_device_update),
    					R.drawable.upc_list_triangle
    					));
    	/*index: 2  升级设置*/
    	mMenuListItems.add(new MenuListItem(
    					R.drawable.upc_setting_icon,
    					getString(R.string.upc_settings),
    					R.drawable.upc_list_triangle
    					));
    	/*index: 3  升级配置*/
//    	mMenuListItems.add(new MenuListItem(
//    			R.drawable.upc_setting_icon,
//    			"Diag config",
//    			R.drawable.upc_list_triangle
//    			));
    	mMenuList = (ListView)findViewById(R.id.upc_main_menu_list);
    	mMenuList.setAdapter(new MenuListAdpater(this, mMenuListItems));
    	mMenuList.setOnItemClickListener(new MenuItemClickListener());
    	mMenuList.setFooterDividersEnabled(true);
    }
    
    //菜单点击事件处理
    private class MenuItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			/** 检查 SD卡是否挂载 **/
			if(!SDCardChecker.isSDCardMounted())
			{
				final CustomAlertDialog dlg = new CustomAlertDialog(activity);
				dlg.setTitle(R.string.upc_tips);
				dlg.setMessage(R.string.upc_sdcard_unmounted_error);
				dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener() 
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
							dialog.dismiss();
						}
						return false;
					}
				});
				dlg.show();
				return;
			}
			switch(position)
			{
		    	case 0: //DBSCar升级
		    		if(webservice!=null)
		    		{
		    			webservice.addListener(new ApkUpdateInfoListener());
		    			webservice.queryApkUpdateInfo(Constants.MYCAR_VERSION);
//		    			webservice.queryApkUpdateInfo("V1.04.010");// for test
		    		}
		    		break;
		    	case 1: // 设备升级
		    		// 检查网络连接
		    		if(!NetworkChecker.isConnected(activity))// 没有连接
		    		{
		    			// 跳转到本地升级
		    			final CustomAlertDialog dlg = new CustomAlertDialog(activity);
		    			dlg.setTitle(R.string.upc_tips);
		    			dlg.setMessage(getString(R.string.upc_network_not_active));
		    			dlg.setOnKeyListener(new OnKeyListener()
		    			{
		    				@Override
		    				public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
		    				{
		    					if(aKeyCode == KeyEvent.KEYCODE_BACK)
		    					{
		    						dlg.dismiss();
		    					}
		    					return false;
		    				}
		    			});
		    			dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
		    			{
		    				@Override
		    				public void onClick(View v)
		    				{
		    					dlg.dismiss();
		    					startActivity(new Intent(UpdateCenterMainActivity.this,LocalUpdateManager.class));
		    				}
		    			});
		    			dlg.setNegativeButton(R.string.upc_cancel,new OnClickListener()
		    			{
		    				@Override
		    				public void onClick(View v)
		    				{
		    					dlg.dismiss();
		    				}
		    			});
		    			dlg.show();
		    		}
		    		else // 有网络连接 让用户选择: 1.在线升级  2.本地升级
		    		{
		    			final CustomAlertDialog dlg = new CustomAlertDialog(activity);
		    			dlg.setTitle(R.string.upc_tips);
		    			dlg.setMessage(getString(R.string.upc_update_method_tip));
		    			dlg.setOnKeyListener(new OnKeyListener()
		    			{
		    				@Override
		    				public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
		    				{
		    					if(aKeyCode == KeyEvent.KEYCODE_BACK)
		    					{
		    						dlg.dismiss();
		    					}
		    					return false;
		    				}
		    			});
		    			dlg.setPositiveButton(R.string.upc_online_update,new OnClickListener()
		    			{
		    				@Override
		    				public void onClick(View v)
		    				{
		    					dlg.dismiss();
		    					if(shUpdateSettings.getBoolean(UpdateCenterConstants.DEVICE_ACTIVATE_GUIDE_NOT_SHOW_AGAIN, false))
		    					{
		    						// 直接跳转到 在线升级 [序列号验证---配置检查---> 注册 ---> 配置 ---> 下载 ---> 升级]
		    						startActivity(new Intent(UpdateCenterMainActivity.this,DeviceRegisterActivity.class));
		    					}
		    					else// 需要提示用户插好接头
		    					{
		    						Intent intent = new Intent(UpdateCenterMainActivity.this,DeviceActivateGuideActivity.class);
		    						intent.putExtra("update_method", UpdateCenterConstants.UPDATE_METHOD_ONLINE);// 在线升级
		    						startActivity(intent);		    							
		    					}
		    				}
		    			});
		    			dlg.setNegativeButton(R.string.upc_local_update,new OnClickListener()
		    			{
		    				@Override
		    				public void onClick(View v)
		    				{
		    					dlg.dismiss();
		    					startActivity(new Intent(UpdateCenterMainActivity.this,LocalUpdateManager.class));
//		    					if(shUpdateSettings.getBoolean(UpdateCenterConstants.DEVICE_ACTIVATE_GUIDE_NOT_SHOW_AGAIN, false))
//		    					{
//		    						// 直接跳转到本地升级
//		    					}
//		    					else// 需要提示用户插好接头
//		    					{
//		    						Intent intent = new Intent(UpdateCenterMainActivity.this,DeviceActivateGuideActivity.class);
//		    						intent.putExtra("update_method", UpdateCenterConstants.UPDATE_METHOD_LOCAL);// 本地升级
//		    						startActivity(intent);
//		    					}
		    				}
		    			});
		    			dlg.show();	    				
		    		}
		    		break;
		    	case 2:
		    		startActivity(new Intent(UpdateCenterMainActivity.this,UpdateCenterSettingsActivity.class));
		    		break;
		    	case 3:// 升级配置 test
					Intent intent = new Intent(UpdateCenterMainActivity.this,DiagSoftConfigureActivity.class);
					SerialInfo siInfo = new SerialInfo("980990000518","123456");
					intent.putExtra("serialInfo", siInfo);
					startActivity(intent);
		    		break;
		    	default:
		    		break;
		    }//end switch
		}
    	
    }
    
    class ApkUpdateInfoListener implements WebServiceListener 
    {
		ProgressDialog pdlg;
		ApkUpdateInfoListener thiz = this;
		
		final private void download(int detailId)
		{
			httpDownloader.removeAllListeners();
			httpDownloader.addListener(downloadListener);
			httpDownloader.downloadApk(detailId,UpdateCenterConstants.TEMP_DIR);
		}
		
    	@Override
		public void onStartWebServiceRequest(Object context,SoapRequest request)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					pdlg = new ProgressDialog(activity);
					pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pdlg.setMessage(getString(R.string.upc_querying));
					pdlg.setOnKeyListener(new OnKeyListener()
					{
						@Override
						public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
						{
							if(aKeyCode==KeyEvent.KEYCODE_BACK && aEvent.getAction()== KeyEvent.ACTION_UP)
							{
								webservice.removeListener(thiz);
								pdlg.dismiss();
							}
							return true;
						}
					});
					pdlg.show();
				}
			});
		}

		@Override
		public void onWebServiceSuccess(Object context,final SoapResponse response)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if (pdlg!=null)
					{
						pdlg.dismiss();
					}
					webservice.removeListener(thiz);
					if (response.getMethod().equals(SoapMethod.QUERY_APK_UPDATE_INFO))
					{
						final ApkUpdateInfo info = (ApkUpdateInfo) response.getResult();
						final CustomAlertDialog dlg = new CustomAlertDialog(activity);
						dlg.setTitle(R.string.upc_tips);
						dlg.setMessage(getString(R.string.upc_current_dbscar_version)+":"+
												Constants.MYCAR_VERSION+"\n"  //手机当前的版本 
												+getString(R.string.upc_latest_dbscar_version)+":"
												+info.getVersionNumber()+"\n" //服务器的最新版本
												+info.getUpdateDescription());// 升级描述信息
						dlg.setOnKeyListener(new OnKeyListener()
						{
							@Override
							public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
							{
								if(aKeyCode==KeyEvent.KEYCODE_BACK)
								{
									dlg.dismiss();
								}
								return false;
							}
						});
						dlg.show();
						
						if(info.forceUpdate())// 强制升级
						{
							// 立即升级
							dlg.setNegativeButton(R.string.upc_update_right_now, new OnClickListener()
							{
								@Override
								public void onClick(View aV)
								{
									dlg.dismiss();
									download(info.getDetailId());
								}
							});
						}
						else if(info.optionalUpdate())// 可选升级,非强制
						{
							// 立即升级
							dlg.setPositiveButton(R.string.upc_update_right_now, new OnClickListener()
							{
								@Override
								public void onClick(View aV)
								{
									dlg.dismiss();
									download(info.getDetailId());
								}
							});
							// 以后再说
							dlg.setNegativeButton(R.string.upc_later, new OnClickListener()
							{
								@Override
								public void onClick(View aV)
								{
									dlg.dismiss();
								}
							});
						}
						else if(info.isLatestVersion())// 已经是最新版本,不必升级
						{
							// 确定
							dlg.setMessage(getResString(R.string.upc_current_dbscar_version)+":"
										+Constants.MYCAR_VERSION+","+getResString(R.string.upc_already_latest_version));
							dlg.setPositiveButton(R.string.upc_confirm, new OnClickListener()
							{
								@Override
								public void onClick(View aV)
								{
									dlg.dismiss();
								}
							});
						}
					}
				}
			});
		}
		
		@Override
		public void onWebServiceErrors(Object context,final int code,final SoapRequest request)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if (pdlg!=null)
					{
						pdlg.dismiss();
					}
					webservice.removeListener(thiz);
					if (request!=null)
					{
						String reason = getString(R.string.upc_network_communication_error);
						
						if (code == WebServiceOperator.Error.NETWORK_NOT_AVAILABLE)// 网络不可用
						{
							 reason = getString(R.string.upc_network_not_active);					
						}
						if (code == WebServiceOperator.Error.TIMEOUT)// 请求超时
						{
							 reason = getString(R.string.upc_network_timeout);
						}
						
						showErrorInfoDialog(reason);	
					}
				}
			});
		}
    }
    
    // 客户端软件下载监听
    class ApkDownloadListener extends DefaultHttpListener
    {
    	CustomProgressDialog dlg;
    	DefaultHttpListener listener = this;
		@Override
		public void onHttpStart(String aUrl)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					dlg = new CustomProgressDialog(activity);
					dlg.setTitle(getResources().getString(R.string.upc_download_and_waiting));
					dlg.setStyle(false);
					dlg.setOnKeyListener(new OnKeyListener()
					{
						@Override
						public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
						{
							if(aKeyCode == KeyEvent.KEYCODE_BACK)
							{
								dlg.dismiss();
							}
							return false;
						}
					});
					dlg.show();
				}
			});
		}

		@Override
		public void onHttpDownloadProgress(final int aPercent, int aSpeed,
				int aRestHours, int aRestMinutes, int aRestSeconds)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if(dlg!=null)
					{
						dlg.setProgress(aPercent);
					}
				}
			});
		}

		@Override
		public void onHttpException(final Object aReason)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					httpDownloader.removeListener(listener);
					if (dlg!=null) dlg.dismiss();
					if(aReason!=null)
					{
						if (aReason!=null)
						{
							showErrorInfoDialog(aReason.toString(),activity);
						}
					}
				}
			});
		}

		@Override
		public void onHttpFinish(final Object aTarget,Object extra)
		{
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if (dlg!=null) dlg.dismiss();
					httpDownloader.removeListener(listener);
					if(aTarget!=null && aTarget instanceof File)
					{
						File apkFile = (File)aTarget;
						PackageInstaller installer = new PackageInstaller(activity);
						installer.installApk(apkFile);
					}
				}
			});
		}
    }
    // 错误对话框
    private void showErrorInfoDialog(String message)
    {
    	final CustomAlertDialog dlg = new CustomAlertDialog(activity);
		dlg.setTitle(R.string.upc_error);
		dlg.setMessage(message);
		dlg.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface aDialog, int aKeyCode, KeyEvent aEvent)
			{
				if(aKeyCode==KeyEvent.KEYCODE_BACK)
				{
					dlg.dismiss();
				}
				return false;
			}
		});
		dlg.setPositiveButton(R.string.upc_confirm,new OnClickListener()
		{
			@Override
			public void onClick(View aV)
			{
				dlg.dismiss();
			}
		});
		dlg.show();
    }
    
    final Handler handler = new Handler();
    
    
    /**@author luxingsong
     * 菜单List项
     * */
    private class MenuListItem
    {
    	int mIconId;
    	String mText;
    	int mTriangle;
    	
		public MenuListItem(int mIconId, String mText, int mTriangle)
		{
			this.mIconId = mIconId;
			this.mText = mText;
			this.mTriangle = mTriangle;
		}
		
		public MenuListItem(int mIconId, String mText)
		{
			this.mIconId = mIconId;
			this.mText = mText;
		}
    }
    
    /**@author luxingsong
     * 菜单List的适配器
     * */
    private class MenuListAdpater extends BaseAdapter
    {
    	Context mCont;
    	LayoutInflater mInflator;
    	ArrayList<MenuListItem> mData;
    	
		@Override
		public int getCount()
		{
			return mData.size();
		}

		public MenuListAdpater(Context cont, ArrayList<MenuListItem> data)
		{
			this.mCont = cont;
			this.mData = data;
			mInflator = LayoutInflater.from(mCont);
		}

		@Override
		public Object getItem(int position)
		{
			return mData.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return mData.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = mInflator
						.inflate(R.layout.update_center_menulist_item, null);				
			}
			ImageView icon = (ImageView)convertView
					.findViewById(R.id.update_center_menu_item_icon);
			icon.setBackgroundResource(mData.get(position).mIconId);
			TextView  menuText = (TextView)convertView
					.findViewById(R.id.update_center_menu_item_text);
			menuText.setText(mData.get(position).mText);
			return convertView;
		}
    }
    
    private void showToastL(String what)
    {
    	Toast.makeText(this, what, Toast.LENGTH_LONG).show();
    }
    
    private void showToastS(String what)
    {
    	Toast.makeText(this, what, Toast.LENGTH_SHORT).show();
    }
    
    private void showToastS(int resId)
    {
    	Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
    
	private void showDialog(String title,String content)
	{
		final AlertDialog dlg = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(content)
		.setPositiveButton(R.string.ok,null)
		.create();
		dlg.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				dlg.dismiss();
				return false;
			}
		});
		dlg.show();
	}
	
	private void showDialog(String title,String content,DialogInterface.OnClickListener callback)
	{
		final AlertDialog dlg = new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(content)
				.setPositiveButton(R.string.upc_confirm,callback)
				.setNegativeButton(R.string.cancel, null)
				.create();
		dlg.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				dlg.dismiss();
				return false;
			}
		});
		dlg.show();
	}
	
    public String getResString(int id)
    {
    	return getResources().getString(id);
    }
    
    //  将download.bin 从应用文件目录raw 中拷贝到 "/mnt/sdcard/cnlaunch/dbsCar/downloadbin"目录
 /*   class CopyDownloadbinThread extends Thread
	{
		Context context;
    	InputStream is;
		FileOutputStream fos;
		File downloadbinDir = new File(UpdateCenterConstants.DOWNLOAD_BIN_DIR);
		File downloadbinFile;
		byte[] buff = new byte[1024];
		
		public CopyDownloadbinThread(Context c)
		{
			this.context = c;
		}
		
		public void run()
		{
			if(!SDCardChecker.isSDCardMounted())// 如果sdcard没有挂载就不拷贝了
			{
				return;
			}
			if(!downloadbinDir.exists())
			{
				downloadbinDir.mkdirs();
			}
			
			class FileInfo
			{
				int resId;
				File file;
				public FileInfo(int id,File f)
				{
					this.resId = id;
					this.file  = f;
				}
			}
			
			// 要拷贝的固件文件
			FileInfo[] filesToCopy = new FileInfo[]
			{
					new FileInfo(R.raw.download, new File(downloadbinDir,"/download.bin")),// 新的固件
					new FileInfo(R.raw.download_old, new File(downloadbinDir,"/download_old.bin")),// 老的固件,为了兼容
			};
			
			int file_num = filesToCopy.length;
			int count = 0;
			for(int i=0;i < file_num;i++)
			{
				try {
					is = context.getResources().openRawResource(filesToCopy[i].resId);
					fos = new FileOutputStream(filesToCopy[i].file);
					while((count = is.read(buff))>0)
					{
						fos.write(buff, 0, count);
					}
					is.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}*/
}
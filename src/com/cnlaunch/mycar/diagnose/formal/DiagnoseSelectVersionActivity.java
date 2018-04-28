package com.cnlaunch.mycar.diagnose.formal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import launch.SearchIdUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.domain.DiagnoseBaseActivity;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.diagnose.simplereport.DiagnoseSimpleReportActivity;
import com.cnlaunch.mycar.updatecenter.DeviceRegisterActivity;
import com.cnlaunch.mycar.updatecenter.DiagSoftConfigureActivity;
import com.cnlaunch.mycar.updatecenter.DiagSoftUpdateConfigParams;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.updatecenter.version.VersionNumber;

/**
 * @author pufengming
 */
public class DiagnoseSelectVersionActivity extends DiagnoseBaseActivity implements BluetoothInterface
{
    private static final String TAG = "DiagnoseSelectVersionActivity";
    private static final boolean D = false;
    Context context = DiagnoseSelectVersionActivity.this;
    private TextView m_title;
    // Array adapter for the listview
    private ArrayList<HashMap<String, String>> m_showlist;
    private ListView m_showlistview;
    private SimpleAdapter m_listItemAdapter;
    private Button m_enter_diag;  //进入高级诊断button
    private Button m_enter_fast_diag;  //进入快速诊断
    // private Button m_cancel;
    private final static String TOPATH = "/vehicles/";
    private String m_ggppath;
    //初始化蓝牙服务
	private BluetoothDataService m_blue_service = null;
	//诊断协议服务
	private DiagnoseDataService m_diag_service = null;
	//读版本信息进度条
    private ProgressDialog m_progress_diag;
    //进入诊断与升级之间选择  fasle = diagnose ;  true = updatecenter 
    private boolean m_enter_mode = false;
    //通讯步骤
    private int m_step = 0; 
    //当前对话框样式
    private int m_now_diag = 0;
    //对话框变量
    private DiagAlertDialog m_show_dialog = null;
    //主界面传递的车型名称
    private String m_select_car_name = null;
    //主界面传递的车型版本信息
    private String m_select_car_version = null;
    //判断是否已经复位过了
    private boolean m_bool_enter_reset = false;
    
    private boolean isFront = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // DiagnoseSettings.setCurrentDiagnoseActivity(this);
        setContentView(R.layout.diagnose_formal_selectversion);
        // mTitle = new TextView(this);
        application = (MyCarApplication) getApplication();
        m_title = (TextView) findViewById(R.id.select_version_title);
        m_title.setText(R.string.diag_selectversion);
        m_showlistview = (ListView) findViewById(R.id.select_version_listview);
        m_showlist = new ArrayList<HashMap<String, String>>();
        m_listItemAdapter = new SimpleAdapter(getApplicationContext(), m_showlist, 
        		R.layout.diagnose_formal_selectversion_listitem, 
        		new String[] { "list_name", "list_value" }, 
        		new int[] {R.id.selectversion_list_name, R.id.selectversion_list_value });
        m_showlistview.setAdapter(m_listItemAdapter);
        //添加进度条
        m_progress_diag = new ProgressDialog(this);
        m_progress_diag.setTitle(R.string.version_progress_title);
        m_progress_diag.setMessage(getResources().getText(R.string.version_progress_readversion).toString());
    	// 初始化按钮
        
        m_enter_diag = (Button) findViewById(R.id.select_version_ok);
        m_enter_diag.setText(R.string.version_button_enter_diag);
        m_enter_diag.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
            	if(m_enter_mode == true) //车型不一致进入升级中心
            	{
            	    startUpdateCenter();
//            		Intent intent = new Intent(context,LocalUpdateManager.class);
//            		startActivity(intent);
//            		finish();
            	}
            	else
            	{
            		SendCmdProgress(9);
            	}
            }
        });
        m_enter_diag.setClickable(false);
        m_enter_fast_diag = (Button)findViewById(R.id.select_version_fast_diag);
        m_enter_fast_diag.setText(R.string.version_button_enter_fast_diag);
        m_enter_fast_diag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    
			    
			    
				// TODO Auto-generated method stub
				//进入快速诊断
				Intent intent_fast = new Intent(context,DiagnoseSimpleReportActivity.class);
				intent_fast.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE,
						m_select_car_name); // 车型
				intent_fast.putExtra(Constants.DBSCAR_CURRENT_VERSION,
						m_select_car_version); // 版本
				intent_fast.putExtra(Constants.DBSCAR_SIMPLE_DIAGNOSE, m_ggppath);
				startActivity(intent_fast);
				finish();
			}
		});
        m_enter_fast_diag.setClickable(false);
        //引用蓝牙服务
		m_blue_service = BluetoothDataService.getInstance();
		m_blue_service.AddObserver(this);
		//出事后诊断服务
		m_diag_service = DiagnoseDataService.getInstance();
		//获取传递过来的车型和版本信息
		Intent intent = this.getIntent();
		try {
			m_select_car_name = intent.getStringExtra(Constants.DBSCAR_CURRENT_CAR_TYPE);
			m_select_car_version = intent.getStringExtra(Constants.DBSCAR_CURRENT_VERSION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	//start开始
    	if(D) Log.i(TAG,"onStart");
    	
    	super.onStart();
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"onPause");
//    	isFront = false;
//    	m_progress_diag.dismiss();
//    	if(m_show_dialog != null)
//    	{
//    		m_show_dialog.dismiss();
//    		m_show_dialog = null;
//    	}
    	super.onPause();
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"onResume");
    	isFront = true;
    	//开始读取版本信息
    	//检测SD卡是否可以
    	if(Env.isSDCardAvailable(context) == false)
    	{
    		ShowDialog(true,1,1,R.string.error_title,R.string.version_no_find_sd_card,
					R.string.dialog_ok,0,0);
    	}
    	else if(m_blue_service.IsConnected() == false)
    		m_blue_service.ShowBluetoothConnectActivity(this);
    	else if(m_bool_enter_reset == false)
    		SendCmdProgress(1);
    	super.onResume();
    }
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	//stop暂停
    	if(D) Log.i(TAG,"onStop");
    	isFront = false;
    	super.onStop();
    }
    //更新UI线程
    private final static int MSG_UPDATE_LIST_1 = 101;  //刷新list列表
    private final static int MSG_UPDATE_LIST_2 = 102;  //刷新list列表
    private final static int MSG_UPDATE_MESSAGE = 103;  //刷新进度条信息
    private final static int MSG_SHOWPROGRESS = 104;	//显示进度条
    private final static int MSG_SHOW_ERROR_WINDOW = 105; //显示错误信息对话框
    private final static int MSG_UPDATE_BUTTON_TEXT = 106;		//更新button文本信息
    private final static int MSG_UPDATE_BUTTON_ENABLE = 107;		//设置button状态
    private final static int MSG_SHOW_NOT_VERSION = 108;		//显示版本不一致提示
    private final static int MSG_UPDATE_CLEAR_LISTVIEW = 109;	//清除列表
    private final static int MSG_UPDATE_LIST_3 = 110;  //刷新list列表,BOOT\DOWNLOAD列表
    private final static int MSG_SELECT_VERSION = 111;	//车型语言及版本选择界面
    private final static int MSG_SHOW_UPDATE_DIAGLOG = 112;	//显示进入升级中心提示框。
    private final static int MSG_SHOW_BLUECONNECT_LOST_DLG = 113;	//显示蓝牙连接中断对话框
    private final static int MSG_SHOW_UPDATE_CENTER_DIAGLOG = 114; // 进入升级中心
    String download;
    String serials;
    String vehicle; 
    String version;
    String language;
    String[] requestUpdateParas = new String[5];
    MyCarApplication application;
    private boolean isNeedUpdate()
    {
        boolean isNeedUpdate = false;// 是否需要升级
        String noticeMessage = ""; // 提示信息
        if (application.getCC() != null)
        {
            List<String> updateInfoes = application.getUpdateInfo(application.getCC());
            if (updateInfoes != null && updateInfoes.size() > 0)
            {
                int count = 0; // 记录和DPU序列号一致的记录
                for (String updateInfo : updateInfoes)
                {
                    
                    String[] updateInfoArray = updateInfo.split(Constants.DEVICE_INFO_SEPARATED);
                    String serialsOfRecord = updateInfoArray[1];
                    String vehicleOfRecord = updateInfoArray[2]; 
                    String versionOfRecord = updateInfoArray[3];
                    String languageOfRecord = updateInfoArray[4];
                    requestUpdateParas[0] = serialsOfRecord;
                    requestUpdateParas[1] = vehicleOfRecord;
                    requestUpdateParas[2] = versionOfRecord;
                    requestUpdateParas[3] = languageOfRecord;
                    requestUpdateParas[4] = updateInfoArray[5];
                    // 选出和下位机序列号一致的一项
                    if (serialsOfRecord.equals(serials))
                    {
                        count++;
                        // 比较download.bin版本
                        if (getLocalDownloadBinVersion() != null)
                        {
                            VersionNumber localDownloadBinVersion = new VersionNumber(getLocalDownloadBinVersion());
                            // 如果DPU的download.bin版本和本地版本不一致，设置需要升级download.bin
                            if (localDownloadBinVersion.isGreaterThan(new VersionNumber(download)))
                            //if (!getLocalDownloadBinVersion().equals(download))
                            {
                                application.setIsNeedUpdateDownloadBin(true);
                                isNeedUpdate = true;
                                ShowDialog(true,1,3,R.string.error_title,0,
                                    R.string.firmware_needs_to_be_updated,0,0);
        
                            }
                        }
                        // 比较车型(车型可以不用比较)
                        
                        // 比较诊断软件版本
                        if (!versionOfRecord.equals(version))
                        {
                            // 如果DPU的诊断软件的版本和本地版本不一致，设置需要升级诊断软件
                            application.setIsNeedUpdateDiagnoseSW(true);
                            isNeedUpdate = true;
                            ShowDialog(true,2,11,R.string.error_title,R.string.version_selectcar_not_equal,
                                R.string.version_button_enter_update,R.string.dialog_yes,0);
                        }
                        // 比较语言
                        if (!languageOfRecord.equals(language))
                        {
                            // 如果DPU的诊断软件的语言和本地语言不一致，设置需要升级诊断软件
                            application.setIsNeedUpdateDiagnoseSW(true);
                            //isNeedUpdate = true;
                            ShowDialog(true,2,11,R.string.error_title,R.string.version_selectlang_not_equal,
                                R.string.version_button_enter_update,R.string.dialog_yes,0);
                        }
                        break;// 跳出循环
                    }
                }
//                if (isNeedUpdate)
//                {
//                    startUpdate();
//                }
//                  // 如果接头的序列号和下载的软件序列号不相同，退出高级诊断
//                if (count == 0)
//                {
//                    DiagnoseSelectVersionActivity.this.finish();
//                }
            }
        }
        return isNeedUpdate;
    }
    private void startUpdate(int updateType)
    {
        m_blue_service.DelObserver(this);
        
        
//      DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
//      params.setSerialNumber("963890001047");
//      params.setVehiecle("BMW");
//      params.setVersion("V10.10");
//      params.setLanguage("CN");
//      params.setFileAbsolutePath("/mnt/sdcard/cnlaunch");
//      params.setUpadteType(2);
        
        
        DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
        params.setSerialNumber(requestUpdateParas[0]);
        params.setVehiecle(requestUpdateParas[1]);
        params.setVersion(requestUpdateParas[2]);
        params.setLanguage(requestUpdateParas[3]);
        params.setFileAbsolutePath(requestUpdateParas[4]);
        params.setUpadteType(updateType);
        Intent intent = new Intent(DiagnoseSelectVersionActivity.this,FirmwareUpdate.class);
        intent.putExtra("diagsoft_update_config_params", params);
        Log.d(TAG, params.toString());
        MyCarApplication.params = params;
        //super.releaseSource();
        startActivity(intent);
        DiagnoseSelectVersionActivity.this.finish();
    }
    
    private void startUpdateCenter()
    {
        Intent intent = new Intent(DiagnoseSelectVersionActivity.this,DiagSoftConfigureActivity.class);
        SerialInfo siInfo = new SerialInfo(serials,null);
        intent.putExtra("serialInfo", siInfo);
        startActivity(intent);
        finish();
    }
    public String getLocalDownloadBinVersion()
    {
        File tempFile = new File(UpdateCenterConstants.DBSCAR_DIR, "/temp");
        String[] files = tempFile.list();
        if (files != null && files.length > 0)
        {
            for (String string : files)
            {
                if (string.startsWith("Download_DBScar_"))
                {
                    return (string.substring("Download_DBScar_".length(), string.length() - 7)).replace("_", ".");
                }
            }
        }

        return null;
    }
    private final Handler m_handler = new Handler()
    {
    	@SuppressWarnings("unchecked")
		@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		switch(msg.what)
    		{
    		case MSG_UPDATE_LIST_1:
    			//添加DPU唯一序列号
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("list_name", getResources().getString(R.string.versoin_show_dpuid).toString());
				map.put("list_value",((ArrayList<String>)msg.obj).get(0));
				//m_showlist.add(map);  //不显示DPUID
				//添加产品序列号
				map = new HashMap<String, String>();
				map.put("list_name", getResources().getString(R.string.version_show_serialno).toString());
				map.put("list_value", ((ArrayList<String>)msg.obj).get(1));
				serials = ((ArrayList<String>)msg.obj).get(1);
				m_showlist.add(map);
    			m_listItemAdapter.notifyDataSetChanged();
    			break;
    		case MSG_UPDATE_LIST_2:
    			//添加诊断软件语言
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("list_name", getResources().getString(R.string.version_show_language).toString());
				map1.put("list_value",((ArrayList<String>)msg.obj).get(0));
				//m_showlist.add(map1);  //不显示DPU语言。20120831
				//添加车型名称
				map1 = new HashMap<String, String>();
				map1.put("list_name", getResources().getString(R.string.version_show_carname).toString());
				map1.put("list_value", ((ArrayList<String>)msg.obj).get(1));
				m_showlist.add(map1);
				//添加诊断软件版本
				map1 = new HashMap<String, String>();
				map1.put("list_name", getResources().getString(R.string.version_show_diagversion).toString());
				map1.put("list_value", ((ArrayList<String>)msg.obj).get(2));
				language = ((ArrayList<String>)msg.obj).get(0);
				vehicle = ((ArrayList<String>)msg.obj).get(1);
				version = ((ArrayList<String>)msg.obj).get(2);
				m_showlist.add(map1);
    			m_listItemAdapter.notifyDataSetChanged();
    			break;
    		case MSG_UPDATE_LIST_3:
    			//添加BOOT版本
    			HashMap<String, String> map2 = new HashMap<String, String>();
				map2.put("list_name", getResources().getString(R.string.version_show_boot).toString());
				map2.put("list_value",((ArrayList<String>)msg.obj).get(0));
				m_showlist.add(map2);
				//添加download版本
				map2 = new HashMap<String, String>();
				map2.put("list_name", getResources().getString(R.string.version_show_download).toString());
				map2.put("list_value", ((ArrayList<String>)msg.obj).get(1));
				download = ((ArrayList<String>)msg.obj).get(1);
				m_showlist.add(map2);
    			m_listItemAdapter.notifyDataSetChanged();
    			break;
    		case MSG_UPDATE_MESSAGE: 
    			m_progress_diag.setMessage(getResources().getText(msg.arg1).toString());
    			break;
    		case MSG_SHOWPROGRESS:
    			m_progress_diag.setMessage(getResources().getText(msg.arg1).toString());
    			m_progress_diag.show();
    			break;
    		case MSG_SHOW_ERROR_WINDOW:
    			ShowErrorWindow(msg.arg1);
    			break;
    		case MSG_SHOW_UPDATE_DIAGLOG:
    			//ShowDiaglogToUpdateCenter();
    			m_progress_diag.dismiss();
    			ShowDialog(true,1,50,R.string.error_title,msg.arg1,
    					R.string.version_button_enter_update,0,0);
    			break;
            case MSG_SHOW_UPDATE_CENTER_DIAGLOG:
                //ShowDiaglogToUpdateCenter();
                m_progress_diag.dismiss();
                ShowDialog(true,1,51,R.string.error_title,msg.arg1,
                        R.string.version_button_enter_update,0,0);
                break;
    		case MSG_UPDATE_BUTTON_TEXT:
    			m_enter_diag.setText(msg.arg1);
    			break;
    		case MSG_UPDATE_BUTTON_ENABLE:
    			m_enter_diag.setClickable(msg.arg1 > 0);
    			m_enter_fast_diag.setClickable(msg.arg1 > 0);
    			m_progress_diag.dismiss();
    			m_bool_enter_reset = true;
    			break;
    		case MSG_SHOW_NOT_VERSION:
    			ShowToast();  
    			break;
    		case MSG_UPDATE_CLEAR_LISTVIEW:
    			//启动进度条
            	m_progress_diag.show();
            	//开始读取版本信息
            	m_showlist.clear();
    			break;
    		case MSG_SELECT_VERSION:
    			SelectVersion((ArrayList<String>)msg.obj);
    			break;
    		case MSG_SHOW_BLUECONNECT_LOST_DLG: //显示蓝牙连接中断对话框
    			ShowDialog(true,1,1,R.string.error_title,R.string.version_bluetooth_connect_lost,
    					R.string.dialog_ok,0,0);
    			break;
    		//下面是显示对话框文本
    		case DiagnoseDataService.CMD_SHOW_GETDIALOG:
    			if(D) Log.i(TAG,"收到显示对话框");
    			ShowDialog((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETMENU: 
    			if(D) Log.i(TAG,"收到显示菜单");
    			StartMenuActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_NONE:  //未知对话框显示
    			if(D) Log.i(TAG,"未知显示对话框");
    			break;
    		default:
    			break;
    		}
    		super.handleMessage(msg);
    	}
    };
    //版本选择实现过程
    void SelectVersion(ArrayList<String> list)
    {
    	//先判断与选择的车型软件不一致
		if(m_select_car_name != null   && (!m_select_car_name.equals(list.get(1))))
		{
		    //DiagnoseSelectVersionActivity.this.finish();
			//ShowDiaglogToUpdateCenterYesNo(R.string.version_selectcar_not_equal);
			ShowDialog(true,2,11,R.string.error_title,R.string.version_selectcar_not_equal,
					R.string.version_button_enter_update,R.string.dialog_yes,0);
		}
		//再判断语言,如果有当前系统的语言则默认使用当前系统语言，如果没有则让用户选择是否使用接头的语言
		isNeedUpdate();
		if (SearchIdUtils.FindFileInSDCard(list.get(0)) == false)
		{
			//ShowDiaglogToUpdateCenterYesNo(R.string.version_selectlang_not_equal);
//			ShowDialog(true,2,11,R.string.error_title,R.string.version_selectlang_not_equal,
//					R.string.version_button_enter_update,R.string.dialog_yes,0);
		}
		else
		{
			m_ggppath = list.get(0);
		}
		m_enter_mode = false; //进入诊断
		SearchIdUtils.SearchIdInstance(m_ggppath);
		m_diag_service.InitialGGPInstance(); //初始化diagservice的ggp操作方法
		m_handler.obtainMessage(MSG_UPDATE_BUTTON_TEXT, R.string.version_button_enter_diag, 0).sendToTarget();
        //进入mycar模式
		SendCmdProgress(3);
    }
    //弹出错误提示对话框
    //mode: 1-10 为一个按钮状态 :  1-- 结束本界面  
    //      11-20 为双按钮使用            11-- 提示可以进入升级中心
    void ShowDialog(boolean show,int btn_num,int mode,int title,int Message,int btn_id_ok,int btn_id_cancel,int data)
    {
    	if(m_show_dialog != null)
		{
			m_show_dialog.dismiss();
			m_show_dialog = null;
		}
		m_show_dialog = new DiagAlertDialog(this);
		if(title != 0)
			m_show_dialog.setTitle(title);
		if(Message != 0)
			m_show_dialog.setMessage(Message);
		m_show_dialog.setCancelable(false);
		final int v_mode = mode;
		final int v_data = data;
		if(btn_num == 1)	//单按钮
		{
			m_show_dialog.setPositiveButton(btn_id_ok,new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v_mode == 1)
						finish();
					else if(v_mode == 2) //诊断对话框，OK
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_OK,v_data);
					}
					else if(v_mode == 3) // 升级Download.bin
					{
					    startUpdate(1);
//						Intent intent = new Intent(context,LocalUpdateManager.class);
//		        		startActivity(intent);
//		        		finish();
					}
					else if (v_mode == 50) // 升级未完成
					{
					    startUpdate(2);
					}
					else if (v_mode == 51)
					{
					    startUpdateCenter();
					}
					m_show_dialog.dismiss();
					m_show_dialog = null;
				}
			});
		}
		else if(btn_num == 2)  //双按钮
		{
			m_show_dialog.setPositiveButton(btn_id_ok,new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v_mode == 11)  // 版本不一致，升级车型软件
					{
					    startUpdate(4);
//						Intent intent = new Intent(context,LocalUpdateManager.class);
//		        		startActivity(intent);
//		        		finish();
					}
					else if(v_mode == 12) //显示通讯错误提示
					{
						m_progress_diag.show();
						SendCmdProgress(m_step);
					}
					else if(v_mode == 13) //诊断显示对话框OK,CANCEL
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_OK,v_data);
					}
					else if(v_mode == 14) //诊断显示对话框YES NO
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_YES,v_data);
					}
					else if(v_mode == 15) //诊断显示对话框RETRY CANCEL
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_RETRY,v_data);
					}
					m_show_dialog.dismiss();
					m_show_dialog = null;
				}
			});
			m_show_dialog.setNegativeButton(btn_id_cancel, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v_mode == 11)
					{						
					}
					else if(v_mode == 12)
					{	
					}
					else if(v_mode == 13)
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_CANCEL,v_data);
					}
					else if(v_mode == 14) //诊断显示对话框YES NO
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_NO,v_data);
					}
					else if(v_mode == 15) //诊断显示对话框RETRY CANCEL
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_CANCEL,v_data);
					}
					m_show_dialog.dismiss();
					m_show_dialog = null;
				}
			});
		}
		else	//无按钮
		{
			m_show_dialog.SetShowMode(1);
		}
		if(show)
			m_show_dialog.show();
    }
    //进入升级中心对话框

    void ShowToast()
    {
    	Toast.makeText(this, this.getString(R.string.version_enter_update_toast), Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"onDestroy");
    	m_progress_diag.dismiss();
    	m_blue_service.DelObserver(this);
    	super.onDestroy();
    }
	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"BlueConnectLost" + name);
		//m_blue_service.ShowBluetoothConnectActivity(this);
		if(m_blue_service.m_blue_state_last.IsConnected())
			m_handler.obtainMessage(MSG_SHOW_BLUECONNECT_LOST_DLG).sendToTarget();
	}

	@Override
	public void BlueConnected(String name, String mac) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"BlueConnected" + name);
	}
    //databuf = <命令字> + <数据>
	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		// TODO Auto-generated method stub
		byte[] v_recv_buf = new byte[datalen - 8];
		int v_recv_len = m_diag_service.GetDataFromBluetooth(databuf, datalen, v_recv_buf);
		
		String v_show = BluetoothDataService.bytesToHexString(v_recv_buf,v_recv_len);
		if(D) Log.i(TAG,"SHOW：" + v_show);
		if(v_recv_buf[0] - 0x40 == DiagnoseDataService.CMD_MAIN)
		{

			if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READHARD) //获取硬件版本
			{
				ArrayList<String> v_list = new ArrayList<String>();
				m_diag_service.GetDpuIdandSerialNo(v_recv_buf, v_recv_len, v_list);
				m_handler.obtainMessage(MSG_UPDATE_LIST_1,v_list).sendToTarget(); //刷新UI
				SendCmdProgress(10); //第1步
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READBOOT)  //获取BOOT\DOWNLOAD版本
			{
				ArrayList<String> v_list = new ArrayList<String>();
				m_diag_service.GetbootanddownloadInfo(v_recv_buf, v_recv_len, v_list);
				m_handler.obtainMessage(MSG_UPDATE_LIST_3,v_list).sendToTarget(); //刷新UI
				SendCmdProgress(2); //第二步
			} 
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READSOFT) //获取软件版本
			{
				ArrayList<String> v_listsoft = new ArrayList<String>();
				m_diag_service.GetDiagSoftwareVersion(v_recv_buf, v_recv_len, v_listsoft);
				m_handler.obtainMessage(MSG_UPDATE_LIST_2,v_listsoft).sendToTarget(); //刷新UI
				//检查库文件版本 TOPATH + v_carLang + "/" + printStr + "/" + v_carLang + "_" + v_carname + ".GGP";
				m_ggppath = Env.getAppRootDirInSdcard().getAbsolutePath() + TOPATH + v_listsoft.get(1) + "/" + v_listsoft.get(2) + "/" + 
				            v_listsoft.get(1) + "_" + v_listsoft.get(0) + ".GGP";
				if(D) Log.i(TAG,m_ggppath);
				String v_path1 = Env.getAppRootDirInSdcard().getAbsolutePath() + TOPATH + v_listsoft.get(1) + "/" + v_listsoft.get(2) + "/" + 
			            v_listsoft.get(1) + "_EN" + ".GGP";
				if(D) Log.i(TAG,v_path1);
				//查找库文本
				if (SearchIdUtils.FindFileInSDCard(m_ggppath) == false &&
						SearchIdUtils.FindFileInSDCard(v_path1) == false)
		        {
		            // 提示版本不匹配，请升级
		            //m_handler.obtainMessage(MSG_SHOW_NOT_VERSION,0,0).sendToTarget();
		            m_enter_mode = true; //进入升级中心
		            //m_handler.obtainMessage(MSG_UPDATE_BUTTON_TEXT, R.string.version_button_enter_update, 0).sendToTarget();
		            //提示进入升级中心
		            m_handler.obtainMessage(MSG_SHOW_UPDATE_CENTER_DIAGLOG,R.string.version_enter_update_toast,0).sendToTarget();
		            
		        }
				else  // 进入诊断，初始化ggp库文本
				{
					
					ArrayList<String> v_list = new ArrayList<String>();
					String v_ggpath = Env.getAppRootDirInSdcard().getAbsolutePath() + TOPATH + v_listsoft.get(1) + "/" + v_listsoft.get(2) + "/" + 
				            v_listsoft.get(1) + "_" + Env.GetCurrentLanguage() + ".GGP";
					v_list.add(0,v_ggpath); //插入新的路径
					v_list.add(1,v_listsoft.get(1));  //插入车型
					v_list.add(2,v_listsoft.get(0));  //插入语言
					m_handler.obtainMessage(MSG_SELECT_VERSION, v_list).sendToTarget();
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_SETMODE) //获取模式信息
			{
				if(v_recv_buf[2] == 0x02) //OK
				{
					new WaitAndEnterMycar().start();
				}
				else
				{
					m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,v_recv_buf[2],0).sendToTarget();
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READMODE) //查询是否进入download模式
			{
				if(v_recv_buf[2] == 0x01) //进入download模式
				{
					SendCmdProgress(5);
				}
				else
				{
					m_handler.obtainMessage(MSG_UPDATE_MESSAGE, R.string.version_progress_enter_download, 0).sendToTarget();
					SendCmdProgress(4);
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_INPUTPASS) //验证密码
			{
				if(v_recv_buf[2] == 0x00) //密码验证成功
				{
					SendCmdProgress(7); //安全认证
				}
				else
				{
					//m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,v_recv_buf[2],0).sendToTarget();
					//如果密码输入错误，则重置密码
					m_handler.obtainMessage(MSG_UPDATE_MESSAGE, R.string.version_progress_reset_password, 0).sendToTarget();
					SendCmdProgress(6);
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_RESETPASS) //重置密码
			{
				if(v_recv_buf[2] == 0x00) //重置密码成功
				{
					SendCmdProgress(5); //验证密码
				}
				else
				{
					m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,v_recv_buf[2],0).sendToTarget();
				}
			}
		}
		else if((v_recv_buf[0]- 0x40) == DiagnoseDataService.CMD_OPEN )
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_OPEN_CONNECT) //安全认证1
			{
				//发送认证
				byte[] v_key = null;
				if(v_recv_buf[2] == 0x01)
					v_key = new byte[]{v_recv_buf[3],v_recv_buf[4]};
				else if(v_recv_buf[2] == 0x02)
					v_key = new byte[]{v_recv_buf[3],v_recv_buf[4],v_recv_buf[5],v_recv_buf[6]};
				m_diag_service.SafeCheckEnter(2, v_recv_buf[2],v_key);	
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_OPEN_SAFECHECK) //安全认证2
			{
				if(v_recv_buf[3] == 0x00) //认证成功，可以进入诊断程序
				{
					SendCmdProgress(11); //测试升级状态
					//m_handler.obtainMessage(MSG_UPDATE_BUTTON_ENABLE,1,0).sendToTarget();
				}
				else
				{
					m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,v_recv_buf[2],0).sendToTarget();
				}
			}
		}
		else if(v_recv_buf[0] == DiagnoseDataService.CMD_SHOW)
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETMENU) //显示菜单
			{
				if(D) Log.i(TAG,"显示主菜单");
				m_diag_service.GetShowMenuActivity(m_handler, v_recv_buf, v_recv_len,true);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDIALOG) //显示文本对话框
			{
				m_diag_service.GetShowDialog(m_handler, v_recv_buf, v_recv_len);
			}
		}
		else if((v_recv_buf[0]- 0x40) == DiagnoseDataService.CMD_UPDATE) //验证是否升级成功
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_UPDATE_SENDCONTINUE) //断点续传，判断是否升级完成。
			{
				//判断是否升级完成
				if(m_diag_service.ReadUpdateComplete(v_recv_buf,v_recv_len) == 0)				
					m_handler.obtainMessage(MSG_UPDATE_BUTTON_ENABLE,1,0).sendToTarget();
				else
				{
					//提示进入升级中心
		            m_handler.obtainMessage(MSG_SHOW_UPDATE_DIAGLOG,R.string.version_enter_update_notcomplete,0).sendToTarget();
				}
			}
			else	//进入升级中心，升级
			{
	            //提示进入升级中心
	            m_handler.obtainMessage(MSG_SHOW_UPDATE_DIAGLOG,R.string.version_enter_update_notcomplete,0).sendToTarget();
			}
		}
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"接收超时，step=" + m_step);
		m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,0,0).sendToTarget();
	}
    private void SendCmdProgress(int step)
    {
    	m_step = step;
    	switch(step)
    	{
    	case 1: 		//读取硬件版本信息
    		m_handler.obtainMessage(MSG_UPDATE_CLEAR_LISTVIEW).sendToTarget();
        	int v_iret = m_diag_service.ReadVersionStep(1);
        	if(D) Log.i(TAG,"发送连接：" + v_iret);
    		break;
    	case 2:			//读取软件版本信息
    		//获取软件版本
			m_diag_service.ReadVersionStep(2);
    		break;
    	case 3:			//进入mycar模式
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE, R.string.version_progress_enter_diagmode, 0).sendToTarget();
			m_diag_service.SetAndReadMode((byte)0x02);
    		break;
    	case 4:			//查询是否进入downloadbin
    		m_diag_service.ReadVersionStep(3);
    		break;
    	case 5:			//验证密码
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE,R.string.version_progress_input_password,0).sendToTarget();
    		byte[] v_password = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30};
    		m_diag_service.EnterPassword(v_password, v_password.length);
    		break;
    	case 6:			//重置密码
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE,R.string.version_progress_input_password,0).sendToTarget();
    		byte[] v_password1 = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30};
    		m_diag_service.ResetPassword(v_password1, v_password1.length);
    		break;
    	case 7:			//安全认证1
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE,R.string.version_progress_safecheck,0).sendToTarget();
    		m_diag_service.SafeCheckEnter(1, (byte)0, null);
    		break;
    	case 8:			//安全认证2
    		
    		break;
    	case 9:			//建立连接,进入诊断
    		//显示提示对话框
    		//m_handler.obtainMessage(MSG_SHOWPROGRESS,R.string.version_progress_enter_diag,0).sendToTarget();
    		int v_result = m_diag_service.ConnectDpu();
    		if(v_result < 0)
    			if(D) Log.i(TAG,"发送错误：" + v_result);
    		break;	
    	case 10:		//获取boot和download版本
    		m_diag_service.ReadVersionStep(4);
    		break;
    	case 11:     //查询是否升级完成
    		int v_iRet = m_diag_service.ReadVersionStep(5);
        	if(D) Log.i(TAG,"发送连接：" + v_iRet);
    		break;
    	default:
    		break;
    	}
    }
    //显示错误信息对话框，0为超时错误，大于0为具体错误ID
    private void ShowErrorWindow(int error)
    {
    	int v_err_id = m_diag_service.GetDiagErrorID(error);
    	if(D) Log.e(TAG,"错误ID：" + v_err_id);
    	//关闭进度条
    	m_progress_diag.dismiss();
    	if (isFront)
    	{
    	    ShowDialog(true,2,12,R.string.diag_commun_error_title,v_err_id,
    	        R.string.dialog_retry,R.string.dialog_cancle,0);
    	    
    	}
    }
    class WaitAndEnterMycar extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		SendCmdProgress(4);
    		super.run();
    	}
    }
    //显示对话框
    private void ShowDialog(Bundle bundle)
    {
    	if(bundle.getInt("DIALOG_STYLE") != m_now_diag) //不等的时候需要创建新的dialog
    	{
    		final int v_return_cmd = bundle.getInt("DIALOG_CMD_RETURN");
    		m_now_diag = bundle.getInt("DIALOG_STYLE"); //赋值当前类型    		
    		switch(m_now_diag)
    		{
    		case DiagnoseDataService.DIALOG_STYLE_OK:
    			ShowDialog(false,1,2,0,0,R.string.dialog_ok,0,v_return_cmd);
    			break;
    		case DiagnoseDataService.DIALOG_STYLE_OKCANCEL:
    			ShowDialog(false,2,13,0,0,R.string.dialog_ok,R.string.dialog_cancle,v_return_cmd);
    			
    			break;
    		case DiagnoseDataService.DIALOG_STYLE__YESNO:
    			ShowDialog(false,2,14,0,0,R.string.dialog_yes,R.string.dialog_no,v_return_cmd);
    			break;
    		case DiagnoseDataService.DIALOG_STYLE__RETRYCANCEL:
    			ShowDialog(false,2,15,0,0,R.string.dialog_retry,R.string.dialog_cancle,v_return_cmd);
    			break;
    		case DiagnoseDataService.DIALOG_STYLE__NOBUTTON:
    			ShowDialog(false,0,0,0,0,0,0,0);
    			break;
    		case DiagnoseDataService.DIALOG_STYLE__OKPRINT:
    			ShowDialog(false,1,2,0,0,R.string.dialog_ok,0,v_return_cmd);
    			break;
    		default:
    			break;
    		}
    		m_show_dialog.setTitle(bundle.getString("DIALOG_TITLE"));
    		m_show_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    		m_show_dialog.show();
    	}
    	else  //相等的时候只需要刷新dialog
    	{
    		//m_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    		if(m_show_dialog != null)
    			m_show_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    	}
    }
    //进入下一个对话框
    private void StartMenuActivity(Bundle bundle)
    {
    	if(m_show_dialog != null)
		{
			m_show_dialog.dismiss();
			m_show_dialog = null;
		}
    	Intent to_menu = new Intent(context, DiagnoseMenuActivity.class);
		to_menu.putExtras(bundle);
		this.startActivity(to_menu);
    }
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"蓝牙连接返回");
		finish();
	}
}

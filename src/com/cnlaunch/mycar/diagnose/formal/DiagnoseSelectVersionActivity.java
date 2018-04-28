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
    private Button m_enter_diag;  //����߼����button
    private Button m_enter_fast_diag;  //����������
    // private Button m_cancel;
    private final static String TOPATH = "/vehicles/";
    private String m_ggppath;
    //��ʼ����������
	private BluetoothDataService m_blue_service = null;
	//���Э�����
	private DiagnoseDataService m_diag_service = null;
	//���汾��Ϣ������
    private ProgressDialog m_progress_diag;
    //�������������֮��ѡ��  fasle = diagnose ;  true = updatecenter 
    private boolean m_enter_mode = false;
    //ͨѶ����
    private int m_step = 0; 
    //��ǰ�Ի�����ʽ
    private int m_now_diag = 0;
    //�Ի������
    private DiagAlertDialog m_show_dialog = null;
    //�����洫�ݵĳ�������
    private String m_select_car_name = null;
    //�����洫�ݵĳ��Ͱ汾��Ϣ
    private String m_select_car_version = null;
    //�ж��Ƿ��Ѿ���λ����
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
        //��ӽ�����
        m_progress_diag = new ProgressDialog(this);
        m_progress_diag.setTitle(R.string.version_progress_title);
        m_progress_diag.setMessage(getResources().getText(R.string.version_progress_readversion).toString());
    	// ��ʼ����ť
        
        m_enter_diag = (Button) findViewById(R.id.select_version_ok);
        m_enter_diag.setText(R.string.version_button_enter_diag);
        m_enter_diag.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
            	if(m_enter_mode == true) //���Ͳ�һ�½�����������
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
				//����������
				Intent intent_fast = new Intent(context,DiagnoseSimpleReportActivity.class);
				intent_fast.putExtra(Constants.DBSCAR_CURRENT_CAR_TYPE,
						m_select_car_name); // ����
				intent_fast.putExtra(Constants.DBSCAR_CURRENT_VERSION,
						m_select_car_version); // �汾
				intent_fast.putExtra(Constants.DBSCAR_SIMPLE_DIAGNOSE, m_ggppath);
				startActivity(intent_fast);
				finish();
			}
		});
        m_enter_fast_diag.setClickable(false);
        //������������
		m_blue_service = BluetoothDataService.getInstance();
		m_blue_service.AddObserver(this);
		//���º���Ϸ���
		m_diag_service = DiagnoseDataService.getInstance();
		//��ȡ���ݹ����ĳ��ͺͰ汾��Ϣ
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
    	//start��ʼ
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
    	//��ʼ��ȡ�汾��Ϣ
    	//���SD���Ƿ����
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
    	//stop��ͣ
    	if(D) Log.i(TAG,"onStop");
    	isFront = false;
    	super.onStop();
    }
    //����UI�߳�
    private final static int MSG_UPDATE_LIST_1 = 101;  //ˢ��list�б�
    private final static int MSG_UPDATE_LIST_2 = 102;  //ˢ��list�б�
    private final static int MSG_UPDATE_MESSAGE = 103;  //ˢ�½�������Ϣ
    private final static int MSG_SHOWPROGRESS = 104;	//��ʾ������
    private final static int MSG_SHOW_ERROR_WINDOW = 105; //��ʾ������Ϣ�Ի���
    private final static int MSG_UPDATE_BUTTON_TEXT = 106;		//����button�ı���Ϣ
    private final static int MSG_UPDATE_BUTTON_ENABLE = 107;		//����button״̬
    private final static int MSG_SHOW_NOT_VERSION = 108;		//��ʾ�汾��һ����ʾ
    private final static int MSG_UPDATE_CLEAR_LISTVIEW = 109;	//����б�
    private final static int MSG_UPDATE_LIST_3 = 110;  //ˢ��list�б�,BOOT\DOWNLOAD�б�
    private final static int MSG_SELECT_VERSION = 111;	//�������Լ��汾ѡ�����
    private final static int MSG_SHOW_UPDATE_DIAGLOG = 112;	//��ʾ��������������ʾ��
    private final static int MSG_SHOW_BLUECONNECT_LOST_DLG = 113;	//��ʾ���������ж϶Ի���
    private final static int MSG_SHOW_UPDATE_CENTER_DIAGLOG = 114; // ������������
    String download;
    String serials;
    String vehicle; 
    String version;
    String language;
    String[] requestUpdateParas = new String[5];
    MyCarApplication application;
    private boolean isNeedUpdate()
    {
        boolean isNeedUpdate = false;// �Ƿ���Ҫ����
        String noticeMessage = ""; // ��ʾ��Ϣ
        if (application.getCC() != null)
        {
            List<String> updateInfoes = application.getUpdateInfo(application.getCC());
            if (updateInfoes != null && updateInfoes.size() > 0)
            {
                int count = 0; // ��¼��DPU���к�һ�µļ�¼
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
                    // ѡ������λ�����к�һ�µ�һ��
                    if (serialsOfRecord.equals(serials))
                    {
                        count++;
                        // �Ƚ�download.bin�汾
                        if (getLocalDownloadBinVersion() != null)
                        {
                            VersionNumber localDownloadBinVersion = new VersionNumber(getLocalDownloadBinVersion());
                            // ���DPU��download.bin�汾�ͱ��ذ汾��һ�£�������Ҫ����download.bin
                            if (localDownloadBinVersion.isGreaterThan(new VersionNumber(download)))
                            //if (!getLocalDownloadBinVersion().equals(download))
                            {
                                application.setIsNeedUpdateDownloadBin(true);
                                isNeedUpdate = true;
                                ShowDialog(true,1,3,R.string.error_title,0,
                                    R.string.firmware_needs_to_be_updated,0,0);
        
                            }
                        }
                        // �Ƚϳ���(���Ϳ��Բ��ñȽ�)
                        
                        // �Ƚ��������汾
                        if (!versionOfRecord.equals(version))
                        {
                            // ���DPU���������İ汾�ͱ��ذ汾��һ�£�������Ҫ����������
                            application.setIsNeedUpdateDiagnoseSW(true);
                            isNeedUpdate = true;
                            ShowDialog(true,2,11,R.string.error_title,R.string.version_selectcar_not_equal,
                                R.string.version_button_enter_update,R.string.dialog_yes,0);
                        }
                        // �Ƚ�����
                        if (!languageOfRecord.equals(language))
                        {
                            // ���DPU�������������Ժͱ������Բ�һ�£�������Ҫ����������
                            application.setIsNeedUpdateDiagnoseSW(true);
                            //isNeedUpdate = true;
                            ShowDialog(true,2,11,R.string.error_title,R.string.version_selectlang_not_equal,
                                R.string.version_button_enter_update,R.string.dialog_yes,0);
                        }
                        break;// ����ѭ��
                    }
                }
//                if (isNeedUpdate)
//                {
//                    startUpdate();
//                }
//                  // �����ͷ�����кź����ص�������кŲ���ͬ���˳��߼����
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
    			//���DPUΨһ���к�
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("list_name", getResources().getString(R.string.versoin_show_dpuid).toString());
				map.put("list_value",((ArrayList<String>)msg.obj).get(0));
				//m_showlist.add(map);  //����ʾDPUID
				//��Ӳ�Ʒ���к�
				map = new HashMap<String, String>();
				map.put("list_name", getResources().getString(R.string.version_show_serialno).toString());
				map.put("list_value", ((ArrayList<String>)msg.obj).get(1));
				serials = ((ArrayList<String>)msg.obj).get(1);
				m_showlist.add(map);
    			m_listItemAdapter.notifyDataSetChanged();
    			break;
    		case MSG_UPDATE_LIST_2:
    			//�������������
				HashMap<String, String> map1 = new HashMap<String, String>();
				map1.put("list_name", getResources().getString(R.string.version_show_language).toString());
				map1.put("list_value",((ArrayList<String>)msg.obj).get(0));
				//m_showlist.add(map1);  //����ʾDPU���ԡ�20120831
				//��ӳ�������
				map1 = new HashMap<String, String>();
				map1.put("list_name", getResources().getString(R.string.version_show_carname).toString());
				map1.put("list_value", ((ArrayList<String>)msg.obj).get(1));
				m_showlist.add(map1);
				//����������汾
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
    			//���BOOT�汾
    			HashMap<String, String> map2 = new HashMap<String, String>();
				map2.put("list_name", getResources().getString(R.string.version_show_boot).toString());
				map2.put("list_value",((ArrayList<String>)msg.obj).get(0));
				m_showlist.add(map2);
				//���download�汾
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
    			//����������
            	m_progress_diag.show();
            	//��ʼ��ȡ�汾��Ϣ
            	m_showlist.clear();
    			break;
    		case MSG_SELECT_VERSION:
    			SelectVersion((ArrayList<String>)msg.obj);
    			break;
    		case MSG_SHOW_BLUECONNECT_LOST_DLG: //��ʾ���������ж϶Ի���
    			ShowDialog(true,1,1,R.string.error_title,R.string.version_bluetooth_connect_lost,
    					R.string.dialog_ok,0,0);
    			break;
    		//��������ʾ�Ի����ı�
    		case DiagnoseDataService.CMD_SHOW_GETDIALOG:
    			if(D) Log.i(TAG,"�յ���ʾ�Ի���");
    			ShowDialog((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETMENU: 
    			if(D) Log.i(TAG,"�յ���ʾ�˵�");
    			StartMenuActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_NONE:  //δ֪�Ի�����ʾ
    			if(D) Log.i(TAG,"δ֪��ʾ�Ի���");
    			break;
    		default:
    			break;
    		}
    		super.handleMessage(msg);
    	}
    };
    //�汾ѡ��ʵ�ֹ���
    void SelectVersion(ArrayList<String> list)
    {
    	//���ж���ѡ��ĳ��������һ��
		if(m_select_car_name != null   && (!m_select_car_name.equals(list.get(1))))
		{
		    //DiagnoseSelectVersionActivity.this.finish();
			//ShowDiaglogToUpdateCenterYesNo(R.string.version_selectcar_not_equal);
			ShowDialog(true,2,11,R.string.error_title,R.string.version_selectcar_not_equal,
					R.string.version_button_enter_update,R.string.dialog_yes,0);
		}
		//���ж�����,����е�ǰϵͳ��������Ĭ��ʹ�õ�ǰϵͳ���ԣ����û�������û�ѡ���Ƿ�ʹ�ý�ͷ������
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
		m_enter_mode = false; //�������
		SearchIdUtils.SearchIdInstance(m_ggppath);
		m_diag_service.InitialGGPInstance(); //��ʼ��diagservice��ggp��������
		m_handler.obtainMessage(MSG_UPDATE_BUTTON_TEXT, R.string.version_button_enter_diag, 0).sendToTarget();
        //����mycarģʽ
		SendCmdProgress(3);
    }
    //����������ʾ�Ի���
    //mode: 1-10 Ϊһ����ť״̬ :  1-- ����������  
    //      11-20 Ϊ˫��ťʹ��            11-- ��ʾ���Խ�����������
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
		if(btn_num == 1)	//����ť
		{
			m_show_dialog.setPositiveButton(btn_id_ok,new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v_mode == 1)
						finish();
					else if(v_mode == 2) //��϶Ի���OK
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_OK,v_data);
					}
					else if(v_mode == 3) // ����Download.bin
					{
					    startUpdate(1);
//						Intent intent = new Intent(context,LocalUpdateManager.class);
//		        		startActivity(intent);
//		        		finish();
					}
					else if (v_mode == 50) // ����δ���
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
		else if(btn_num == 2)  //˫��ť
		{
			m_show_dialog.setPositiveButton(btn_id_ok,new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(v_mode == 11)  // �汾��һ�£������������
					{
					    startUpdate(4);
//						Intent intent = new Intent(context,LocalUpdateManager.class);
//		        		startActivity(intent);
//		        		finish();
					}
					else if(v_mode == 12) //��ʾͨѶ������ʾ
					{
						m_progress_diag.show();
						SendCmdProgress(m_step);
					}
					else if(v_mode == 13) //�����ʾ�Ի���OK,CANCEL
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_OK,v_data);
					}
					else if(v_mode == 14) //�����ʾ�Ի���YES NO
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_YES,v_data);
					}
					else if(v_mode == 15) //�����ʾ�Ի���RETRY CANCEL
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
					else if(v_mode == 14) //�����ʾ�Ի���YES NO
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_NO,v_data);
					}
					else if(v_mode == 15) //�����ʾ�Ի���RETRY CANCEL
					{
						m_diag_service.SendDialogButton(DiagnoseDataService.DIALOG_ID_CANCEL,v_data);
					}
					m_show_dialog.dismiss();
					m_show_dialog = null;
				}
			});
		}
		else	//�ް�ť
		{
			m_show_dialog.SetShowMode(1);
		}
		if(show)
			m_show_dialog.show();
    }
    //�����������ĶԻ���

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
    //databuf = <������> + <����>
	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		// TODO Auto-generated method stub
		byte[] v_recv_buf = new byte[datalen - 8];
		int v_recv_len = m_diag_service.GetDataFromBluetooth(databuf, datalen, v_recv_buf);
		
		String v_show = BluetoothDataService.bytesToHexString(v_recv_buf,v_recv_len);
		if(D) Log.i(TAG,"SHOW��" + v_show);
		if(v_recv_buf[0] - 0x40 == DiagnoseDataService.CMD_MAIN)
		{

			if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READHARD) //��ȡӲ���汾
			{
				ArrayList<String> v_list = new ArrayList<String>();
				m_diag_service.GetDpuIdandSerialNo(v_recv_buf, v_recv_len, v_list);
				m_handler.obtainMessage(MSG_UPDATE_LIST_1,v_list).sendToTarget(); //ˢ��UI
				SendCmdProgress(10); //��1��
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READBOOT)  //��ȡBOOT\DOWNLOAD�汾
			{
				ArrayList<String> v_list = new ArrayList<String>();
				m_diag_service.GetbootanddownloadInfo(v_recv_buf, v_recv_len, v_list);
				m_handler.obtainMessage(MSG_UPDATE_LIST_3,v_list).sendToTarget(); //ˢ��UI
				SendCmdProgress(2); //�ڶ���
			} 
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READSOFT) //��ȡ����汾
			{
				ArrayList<String> v_listsoft = new ArrayList<String>();
				m_diag_service.GetDiagSoftwareVersion(v_recv_buf, v_recv_len, v_listsoft);
				m_handler.obtainMessage(MSG_UPDATE_LIST_2,v_listsoft).sendToTarget(); //ˢ��UI
				//�����ļ��汾 TOPATH + v_carLang + "/" + printStr + "/" + v_carLang + "_" + v_carname + ".GGP";
				m_ggppath = Env.getAppRootDirInSdcard().getAbsolutePath() + TOPATH + v_listsoft.get(1) + "/" + v_listsoft.get(2) + "/" + 
				            v_listsoft.get(1) + "_" + v_listsoft.get(0) + ".GGP";
				if(D) Log.i(TAG,m_ggppath);
				String v_path1 = Env.getAppRootDirInSdcard().getAbsolutePath() + TOPATH + v_listsoft.get(1) + "/" + v_listsoft.get(2) + "/" + 
			            v_listsoft.get(1) + "_EN" + ".GGP";
				if(D) Log.i(TAG,v_path1);
				//���ҿ��ı�
				if (SearchIdUtils.FindFileInSDCard(m_ggppath) == false &&
						SearchIdUtils.FindFileInSDCard(v_path1) == false)
		        {
		            // ��ʾ�汾��ƥ�䣬������
		            //m_handler.obtainMessage(MSG_SHOW_NOT_VERSION,0,0).sendToTarget();
		            m_enter_mode = true; //������������
		            //m_handler.obtainMessage(MSG_UPDATE_BUTTON_TEXT, R.string.version_button_enter_update, 0).sendToTarget();
		            //��ʾ������������
		            m_handler.obtainMessage(MSG_SHOW_UPDATE_CENTER_DIAGLOG,R.string.version_enter_update_toast,0).sendToTarget();
		            
		        }
				else  // ������ϣ���ʼ��ggp���ı�
				{
					
					ArrayList<String> v_list = new ArrayList<String>();
					String v_ggpath = Env.getAppRootDirInSdcard().getAbsolutePath() + TOPATH + v_listsoft.get(1) + "/" + v_listsoft.get(2) + "/" + 
				            v_listsoft.get(1) + "_" + Env.GetCurrentLanguage() + ".GGP";
					v_list.add(0,v_ggpath); //�����µ�·��
					v_list.add(1,v_listsoft.get(1));  //���복��
					v_list.add(2,v_listsoft.get(0));  //��������
					m_handler.obtainMessage(MSG_SELECT_VERSION, v_list).sendToTarget();
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_SETMODE) //��ȡģʽ��Ϣ
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
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_READMODE) //��ѯ�Ƿ����downloadģʽ
			{
				if(v_recv_buf[2] == 0x01) //����downloadģʽ
				{
					SendCmdProgress(5);
				}
				else
				{
					m_handler.obtainMessage(MSG_UPDATE_MESSAGE, R.string.version_progress_enter_download, 0).sendToTarget();
					SendCmdProgress(4);
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_INPUTPASS) //��֤����
			{
				if(v_recv_buf[2] == 0x00) //������֤�ɹ�
				{
					SendCmdProgress(7); //��ȫ��֤
				}
				else
				{
					//m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,v_recv_buf[2],0).sendToTarget();
					//������������������������
					m_handler.obtainMessage(MSG_UPDATE_MESSAGE, R.string.version_progress_reset_password, 0).sendToTarget();
					SendCmdProgress(6);
				}
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_MAIN_RESETPASS) //��������
			{
				if(v_recv_buf[2] == 0x00) //��������ɹ�
				{
					SendCmdProgress(5); //��֤����
				}
				else
				{
					m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,v_recv_buf[2],0).sendToTarget();
				}
			}
		}
		else if((v_recv_buf[0]- 0x40) == DiagnoseDataService.CMD_OPEN )
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_OPEN_CONNECT) //��ȫ��֤1
			{
				//������֤
				byte[] v_key = null;
				if(v_recv_buf[2] == 0x01)
					v_key = new byte[]{v_recv_buf[3],v_recv_buf[4]};
				else if(v_recv_buf[2] == 0x02)
					v_key = new byte[]{v_recv_buf[3],v_recv_buf[4],v_recv_buf[5],v_recv_buf[6]};
				m_diag_service.SafeCheckEnter(2, v_recv_buf[2],v_key);	
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_OPEN_SAFECHECK) //��ȫ��֤2
			{
				if(v_recv_buf[3] == 0x00) //��֤�ɹ������Խ�����ϳ���
				{
					SendCmdProgress(11); //��������״̬
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
			if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETMENU) //��ʾ�˵�
			{
				if(D) Log.i(TAG,"��ʾ���˵�");
				m_diag_service.GetShowMenuActivity(m_handler, v_recv_buf, v_recv_len,true);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDIALOG) //��ʾ�ı��Ի���
			{
				m_diag_service.GetShowDialog(m_handler, v_recv_buf, v_recv_len);
			}
		}
		else if((v_recv_buf[0]- 0x40) == DiagnoseDataService.CMD_UPDATE) //��֤�Ƿ������ɹ�
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_UPDATE_SENDCONTINUE) //�ϵ��������ж��Ƿ�������ɡ�
			{
				//�ж��Ƿ��������
				if(m_diag_service.ReadUpdateComplete(v_recv_buf,v_recv_len) == 0)				
					m_handler.obtainMessage(MSG_UPDATE_BUTTON_ENABLE,1,0).sendToTarget();
				else
				{
					//��ʾ������������
		            m_handler.obtainMessage(MSG_SHOW_UPDATE_DIAGLOG,R.string.version_enter_update_notcomplete,0).sendToTarget();
				}
			}
			else	//�����������ģ�����
			{
	            //��ʾ������������
	            m_handler.obtainMessage(MSG_SHOW_UPDATE_DIAGLOG,R.string.version_enter_update_notcomplete,0).sendToTarget();
			}
		}
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"���ճ�ʱ��step=" + m_step);
		m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,0,0).sendToTarget();
	}
    private void SendCmdProgress(int step)
    {
    	m_step = step;
    	switch(step)
    	{
    	case 1: 		//��ȡӲ���汾��Ϣ
    		m_handler.obtainMessage(MSG_UPDATE_CLEAR_LISTVIEW).sendToTarget();
        	int v_iret = m_diag_service.ReadVersionStep(1);
        	if(D) Log.i(TAG,"�������ӣ�" + v_iret);
    		break;
    	case 2:			//��ȡ����汾��Ϣ
    		//��ȡ����汾
			m_diag_service.ReadVersionStep(2);
    		break;
    	case 3:			//����mycarģʽ
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE, R.string.version_progress_enter_diagmode, 0).sendToTarget();
			m_diag_service.SetAndReadMode((byte)0x02);
    		break;
    	case 4:			//��ѯ�Ƿ����downloadbin
    		m_diag_service.ReadVersionStep(3);
    		break;
    	case 5:			//��֤����
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE,R.string.version_progress_input_password,0).sendToTarget();
    		byte[] v_password = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30};
    		m_diag_service.EnterPassword(v_password, v_password.length);
    		break;
    	case 6:			//��������
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE,R.string.version_progress_input_password,0).sendToTarget();
    		byte[] v_password1 = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30};
    		m_diag_service.ResetPassword(v_password1, v_password1.length);
    		break;
    	case 7:			//��ȫ��֤1
    		m_handler.obtainMessage(MSG_UPDATE_MESSAGE,R.string.version_progress_safecheck,0).sendToTarget();
    		m_diag_service.SafeCheckEnter(1, (byte)0, null);
    		break;
    	case 8:			//��ȫ��֤2
    		
    		break;
    	case 9:			//��������,�������
    		//��ʾ��ʾ�Ի���
    		//m_handler.obtainMessage(MSG_SHOWPROGRESS,R.string.version_progress_enter_diag,0).sendToTarget();
    		int v_result = m_diag_service.ConnectDpu();
    		if(v_result < 0)
    			if(D) Log.i(TAG,"���ʹ���" + v_result);
    		break;	
    	case 10:		//��ȡboot��download�汾
    		m_diag_service.ReadVersionStep(4);
    		break;
    	case 11:     //��ѯ�Ƿ��������
    		int v_iRet = m_diag_service.ReadVersionStep(5);
        	if(D) Log.i(TAG,"�������ӣ�" + v_iRet);
    		break;
    	default:
    		break;
    	}
    }
    //��ʾ������Ϣ�Ի���0Ϊ��ʱ���󣬴���0Ϊ�������ID
    private void ShowErrorWindow(int error)
    {
    	int v_err_id = m_diag_service.GetDiagErrorID(error);
    	if(D) Log.e(TAG,"����ID��" + v_err_id);
    	//�رս�����
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
    //��ʾ�Ի���
    private void ShowDialog(Bundle bundle)
    {
    	if(bundle.getInt("DIALOG_STYLE") != m_now_diag) //���ȵ�ʱ����Ҫ�����µ�dialog
    	{
    		final int v_return_cmd = bundle.getInt("DIALOG_CMD_RETURN");
    		m_now_diag = bundle.getInt("DIALOG_STYLE"); //��ֵ��ǰ����    		
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
    	else  //��ȵ�ʱ��ֻ��Ҫˢ��dialog
    	{
    		//m_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    		if(m_show_dialog != null)
    			m_show_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    	}
    }
    //������һ���Ի���
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
		if(D) Log.i(TAG,"�������ӷ���");
		finish();
	}
}

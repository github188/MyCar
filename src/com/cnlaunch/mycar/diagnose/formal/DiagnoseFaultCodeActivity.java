package com.cnlaunch.mycar.diagnose.formal;

import java.util.List;
import java.util.Map;

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

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseSettings;
import com.cnlaunch.mycar.diagnose.domain.DiagnoseBaseActivity;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.updatecenter.UpdateCenterMainActivity;

public class DiagnoseFaultCodeActivity extends DiagnoseBaseActivity implements BluetoothInterface{
	private static final String TAG = "DiagnoseFaultCodeActivity";
    private static final boolean D = true;
	private Button m_faultcode_back_pre;
	private Context context = DiagnoseFaultCodeActivity.this;
	private ListView 		m_dtc_list = null; 
	private List<Map<String, String>> 	m_list_data = null;
	private SimpleAdapter 	m_list_adapter = null;
	//初始化蓝牙服务
	private BluetoothDataService m_blue_service = null;
	//诊断协议服务
	private DiagnoseDataService m_diag_service = null;
	//当前对话框样式
	private int m_now_diag = 0;
	//对话框变量
	private DiagAlertDialog m_show_dialog = null;
	//是否是根目录
    private boolean m_isroot = false;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DiagnoseSettings.setCurrentDiagnoseActivity(this);
		setContentView(R.layout.diagnose_formal_faultcode);

		// 得到数据
		Bundle bundle = getIntent().getExtras();
		//获取故障码列表
		m_list_data = (List<Map<String, String>>) bundle.getSerializable("DTC");
		m_dtc_list = (ListView) findViewById(R.id.diag_dtc_listview);
		m_list_adapter = new SimpleAdapter(context,m_list_data,
				R.layout.diagnose_formal_faultcode_item,
				new String[]{"DTC_ID","DTC_NAME","DTC_STATUS"},
				new int[]{R.id.diagnose_dtc_item_pid, R.id.diagnose_dtc_item_name,R.id.diagnose_dtc_item_status});
		m_dtc_list.setAdapter(m_list_adapter);
		m_faultcode_back_pre = (Button) findViewById(R.id.diag_dtc_back_pre);
		m_faultcode_back_pre.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_diag_service.SendCurrentbackPre(DiagnoseDataService.CMD_SHOW_SETDTC_ADD);
			}
		});
		//引用蓝牙服务
		m_blue_service = BluetoothDataService.getInstance();
		m_blue_service.AddObserver(this);
		//出事后诊断服务
		m_diag_service = DiagnoseDataService.getInstance();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(m_show_dialog != null)
    	{
    		m_show_dialog.dismiss();
    		m_show_dialog = null;
    	}
		m_blue_service.DelObserver(this);
		super.onDestroy();
	}
	//更新UI线程
    private final static int MSG_SHOW_ERROR_WINDOW = 101;  //刷新list列表
    private final static int MSG_SHOW_BLUECONNECT_LOST_DLG = 113;	//显示蓝牙连接中断对话框
    private final Handler m_handler = new Handler()
    {
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		switch(msg.what)
    		{
    		case MSG_SHOW_ERROR_WINDOW:
    			ShowErrorWindow(msg.arg1);
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
	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		m_handler.obtainMessage(MSG_SHOW_BLUECONNECT_LOST_DLG).sendToTarget();
		
	}
	@Override
	public void BlueConnected(String name, String mac) {
		// TODO Auto-generated method stub
	
	}
	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		// TODO Auto-generated method stub
		byte[] v_recv_buf = new byte[datalen - 8];
		int v_recv_len = m_diag_service.GetDataFromBluetooth(databuf, datalen, v_recv_buf);
		String v_show = BluetoothDataService.bytesToHexString(v_recv_buf,v_recv_len);
		if(D) Log.i(TAG,"SHOW：" + v_show);
		if(v_recv_buf[0] == DiagnoseDataService.CMD_SHOW)
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETMENU) //显示菜单
			{
				if(D) Log.i(TAG,"显示主菜单");
				m_diag_service.GetShowMenuActivity(m_handler, v_recv_buf, v_recv_len,m_isroot);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDIALOG) //显示文本对话框
			{
				m_diag_service.GetShowDialog(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_ROOT_DIAG) //退出诊断
			{
				m_isroot = true; //收到根目录提示，当点击事件触发，则取消此判断
			}
		}
	}
	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,0,0).sendToTarget();
	}
	//进入下一个对话框
    private void StartMenuActivity(Bundle bundle)
    {
    	Intent to_menu = new Intent(context, DiagnoseMenuActivity.class);
		to_menu.putExtras(bundle);
		this.startActivity(to_menu);
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
    		if(m_show_dialog != null)
    			m_show_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    	}
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
					else if(v_mode == 3) //只能进入升级中心
					{
						Intent intent = new Intent(context,UpdateCenterMainActivity.class);
		        		startActivity(intent);
		        		finish();
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
					if(v_mode == 11)  //显示进入升级中心提示
					{
						Intent intent = new Intent(context,UpdateCenterMainActivity.class);
		        		startActivity(intent);
		        		finish();
					}
					else if(v_mode == 12) //显示通讯错误提示
					{
						
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
  //显示错误信息对话框，0为超时错误，大于0为具体错误ID
    private void ShowErrorWindow(int error)
    {
    	if(D) Log.e(TAG,"错误ID：" + error);
    	//关闭进度条
    	ShowDialog(true,1,1,R.string.diag_commun_error_title,m_diag_service.GetDiagErrorID(error),
    			R.string.dialog_ok,0,0);
    }
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}

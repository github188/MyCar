package com.cnlaunch.mycar.diagnose.formal;

import java.util.List;
import java.util.Map;

import launch.SearchIdUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseSettings;
import com.cnlaunch.mycar.diagnose.domain.DiagnoseBaseActivity;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.updatecenter.UpdateCenterMainActivity;

public class DiagnoseMenuActivity extends DiagnoseBaseActivity implements BluetoothInterface{
	private Context context = DiagnoseMenuActivity.this;
	private static final String TAG = "DiagnoseMenuActivity";
    private static final boolean D = true;
	private TextView m_menu_title;
	private Button m_button_back_main;
	private Button m_button_back_pre;
	private int m_current_item = 0;
	private ListView 		m_menu_list = null; 
	private List<Map<String, String>> 	m_list_data = null;
	private SimpleAdapter 	m_list_adapter = null;
	//��ʼ����������
	private BluetoothDataService m_blue_service = null;
	//���Э�����
	private DiagnoseDataService m_diag_service = null;
	 //��ǰ�Ի�����ʽ
    private int m_now_diag = 0;
  //�Ի������
    private DiagAlertDialog m_show_dialog = null;
    //�Ƿ��Ǹ�Ŀ¼
    private boolean m_isroot = false;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DiagnoseSettings.setCurrentDiagnoseActivity(this);
		setContentView(R.layout.diagnose_formal_menu);
		// �õ�����
		Bundle bundle = getIntent().getExtras();
		//��ȡ�Ƿ��Ǹ�Ŀ¼��Ϣ
		m_isroot = bundle.getBoolean("DIALOG_ROOT");
		String title = bundle.getString("DIALOG_TITLE");
		// ���ñ���
		m_menu_title = (TextView) findViewById(R.id.diagnose_menu_title);
		//�жϲ˵��Ƿ�Ϊ��
		if(D) Log.i(TAG,"length=" + title.length());
		if(title.length() < 2)
			m_menu_title.setText(R.string.diagnose_menu_show_title);
		else
			m_menu_title.setText(title);
		// �����б�
		m_list_data = (List<Map<String, String>>) bundle.getSerializable("DIALOG_MENU");
		m_menu_list = (ListView) findViewById(R.id.diagnose_menu_listview);
		m_list_adapter = new SimpleAdapter(context,m_list_data,
				R.layout.diagnose_formal_single_checklistview,
				new String[]{"MENU"},new int[]{R.id.single_checklistView});
		m_menu_list.setAdapter(m_list_adapter);
		m_menu_list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				m_current_item = position;
				m_diag_service.SendMenuClickList(m_current_item); //������Ӧ
				m_isroot = false;	//������¼���������ȡ�����ж�
			}
		});
		//�˳����
		m_button_back_main = (Button) findViewById(R.id.diagnose_menu_back_main);
		m_button_back_main.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				m_diag_service.SendDpuReset();
				m_handler.obtainMessage(MSG_EXIT_DIAGNOSE).sendToTarget();
			}
		});

		m_button_back_pre = (Button) findViewById(R.id.diagnose_menu_back_pre);
		m_button_back_pre.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub\
				if(m_isroot) //�ж��Ƿ�Ϊ��Ŀ¼,�ڸ�Ŀ¼������أ�����ʾ�Ƿ��˳�
				{
					ShowDialog(true,2,16,R.string.datastream_graph_tip_title,R.string.diagnose_exit_messge1,
			    			R.string.dialog_yes,R.string.dialog_no,0);
				}
				else
					m_diag_service.SendCurrentbackPre(DiagnoseDataService.CMD_SHOW_SETMENU);
			}
		});
		//������������
		m_blue_service = BluetoothDataService.getInstance();
		m_blue_service.AddObserver(this);
		//���º���Ϸ���
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
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
	//����UI�߳�
    private final static int MSG_SHOW_ERROR_WINDOW = 101;  //ˢ��list�б�
    private final static int MSG_EXIT_DIAGNOSE = 102;		//�˳����
    private final static int MSG_SHOW_BLUECONNECT_LOST_DLG = 113;	//��ʾ���������ж϶Ի���
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
    		case MSG_SHOW_BLUECONNECT_LOST_DLG: //��ʾ���������ж϶Ի���
    			ShowDialog(true,1,1,R.string.error_title,R.string.version_bluetooth_connect_lost,
    					R.string.dialog_ok,0,0);
    			break;
    		case MSG_EXIT_DIAGNOSE: //�˳����
    			if(D) Log.i(TAG,"�˳����");
    			//�ͷ�ggp��
            	SearchIdUtils search = SearchIdUtils.SearchIdInstance(null);
            	search.CloseFile();
				finish();
    			break;
    		//��������ʾ�Ի����ı�
    		case DiagnoseDataService.CMD_SHOW_GETDIALOG:
    		case DiagnoseDataService.CMD_SHOW_GETDIALOGID:
    			if(D) Log.i(TAG,"�յ���ʾ�Ի���");
    			ShowDialog((Bundle)msg.obj);
    			break;  			
    		case DiagnoseDataService.CMD_SHOW_GETMENU: 
    			if(D) Log.i(TAG,"�յ���ʾ�˵�");
    			UpdateMenuActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETDTC_ADD:
    			if(D) Log.i(TAG,"�յ���ʾ������");
    			StartDTCActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETDATASTREAMSELECT:
    			if(D) Log.i(TAG,"�յ�������ѡ��Ի���");
    			StartDataStreamSelectActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETDATASTREAM:
    			if(D) Log.i(TAG,"�յ�������ʾ�Ի���");
    			StartDataStreamShowActivity((Bundle)msg.obj);
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

	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		//m_blue_service.ShowBluetoothConnectActivity(this);
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
		if(D) Log.i(TAG,"SHOW��" + v_show);
		if(v_recv_buf[0] == DiagnoseDataService.CMD_SHOW)
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETMENU) //��ʾ�˵�
			{
				if(D) Log.i(TAG,"��ʾ���˵�");
				m_diag_service.GetShowMenuActivity(m_handler, v_recv_buf, v_recv_len,m_isroot);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDIALOG) //��ʾ�ı��Ի���
			{
				m_diag_service.GetShowDialog(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDIALOGID) //��ʾID�Ի���
			{
				m_diag_service.GetShowDialogID(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDTC_ADD) //��ʾ������
			{
				m_diag_service.GetShowDTCActivityAdd(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDATASTREAMSELECT) //������ѡ��
			{
				m_diag_service.GetShowDatastreamChoiceActivity(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDATASTREAM) //��������ʾ
			{
				m_diag_service.GetShowDatastreamActivity(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_ROOT_DIAG) //�˳����
			{
				m_isroot = true; //�յ���Ŀ¼��ʾ��������¼���������ȡ�����ж�
			}
		}
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		m_handler.obtainMessage(MSG_SHOW_ERROR_WINDOW,0,0).sendToTarget();
	}
	//��ʾ������Ϣ�Ի���0Ϊ��ʱ���󣬴���0Ϊ�������ID
    private void ShowErrorWindow(int error)
    {
    	if(D) Log.e(TAG,"����ID��" + error);
    	//�رս�����
    	ShowDialog(true,1,1,R.string.diag_commun_error_title,m_diag_service.GetDiagErrorID(error),
    			R.string.dialog_ok,0,0);
    }
    //����menu�Ի����Լ�
    @SuppressWarnings("unchecked")
	private void UpdateMenuActivity(Bundle bundle)
    {
    	if(m_show_dialog != null)
    	{
    		m_show_dialog.dismiss();
    		m_show_dialog = null;
    	}
    	m_now_diag = 0; //��λ�Ի���
    	String title = bundle.getString("DIALOG_TITLE");
    	if(title.length() < 1)
			m_menu_title.setText(R.string.diagnose_menu_show_title);
		else
			m_menu_title.setText(title);
    	List<Map<String, String>> v_updata = (List<Map<String, String>>) bundle.getSerializable("DIALOG_MENU");
    	m_list_data.clear();
    	for(int i = 0; i < v_updata.size(); i ++)
    	{
    		m_list_data.add(v_updata.get(i));
    	}
    	m_list_adapter.notifyDataSetChanged();
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
    		if(m_show_dialog != null)
    			m_show_dialog.setMessage(bundle.getString("DIALOG_BODY"));
    	}
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
					else if(v_mode == 3) //ֻ�ܽ�����������
					{
						Intent intent = new Intent(context,UpdateCenterMainActivity.class);
		        		startActivity(intent);
		        		finish();
					}
					else if(v_mode == 4) //��ʾ�޹�����
					{
						m_diag_service.SendCurrentbackPre(DiagnoseDataService.CMD_SHOW_SETDTC_ADD);
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
					if(v_mode == 11)  //��ʾ��������������ʾ
					{
						Intent intent = new Intent(context,UpdateCenterMainActivity.class);
		        		startActivity(intent);
		        		finish();
					}
					else if(v_mode == 12) //��ʾͨѶ������ʾ
					{
						
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
					else if(v_mode == 16) //�˳������ʾ
					{
						m_diag_service.SendDpuReset();
						m_handler.obtainMessage(MSG_EXIT_DIAGNOSE).sendToTarget();
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
					else if(v_mode == 16) ////�˳������ʾ ȡ��
					{
						
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
    //���������Ի���
    private void StartDTCActivity(Bundle bundle)
    {
    	if(bundle.getInt("DTC_NUM") > 0) //�й������ʱ����������ʾ������
    	{
    		Intent to_faultcode = new Intent(context,DiagnoseFaultCodeActivity.class);
        	to_faultcode.putExtras(bundle);
        	this.startActivity(to_faultcode);
    	}
    	else //�޹�����ֱ����ʾ��ʾ��Ϣ
    	{
    		ShowDialog(true,1,4,R.string.diag_dtc_title,R.string.menu_show_nodtc,R.string.dialog_ok,0,0);
    	}
    }
    //����������ѡ��Ի���
    private void StartDataStreamSelectActivity(Bundle bundle)
    {
    	Intent to_datastremChoice = new Intent(context,DiagnoseDataStreamChoiceActivity.class);
    	to_datastremChoice.putExtras(bundle);
    	this.startActivity(to_datastremChoice);
    }
    //�յ���������ʾ�Ի���
    private void StartDataStreamShowActivity(Bundle bundle)
    {
    	Intent to_show_dataStream = new Intent(context,DiagnoseDataStreamShowActivity.class);
    	to_show_dataStream.putExtras(bundle);
    	this.startActivity(to_show_dataStream);
    	m_blue_service.DelObserver(this); //ɾ���۲���
    }
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}

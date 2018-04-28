package com.cnlaunch.mycar.diagnose.formal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseSettings;
import com.cnlaunch.mycar.diagnose.domain.DiagnoseBaseActivity;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.updatecenter.UpdateCenterMainActivity;

public class DiagnoseDataStreamChoiceActivity extends DiagnoseBaseActivity implements BluetoothInterface{
	Context context = DiagnoseDataStreamChoiceActivity.this;
	private static final String TAG = "DiagnoseDataStreamChoiceActivity";
	private static final boolean D = false;
	private ListView 				m_list = null;
	private List<String> 			m_list_data =null;
	private DatastreamChoiceAdapter m_list_adapter = null;
	private Button m_datastream_send_choice = null;
	private Button m_back_pre = null;
	private CheckBox m_datastream_check = null;  //ȫѡ��ť
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DiagnoseSettings.setCurrentDiagnoseActivity(this);
		setContentView(R.layout.diagnose_formal_datastreamchoose);
		// �õ�����
		Bundle bundle = getIntent().getExtras();
		//����listView
		m_list = (ListView) findViewById(R.id.datastream_choice_listview);
		//list�������
		m_list_data = bundle.getStringArrayList("DATASTREAM_NAME");
		m_list_adapter = new DatastreamChoiceAdapter(m_list_data);
		//����list����
		m_list.setAdapter(m_list_adapter);
		//��list������
		m_list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				// TODO Auto-generated method stub
				// ȡ��ViewListStruct����������ʡȥ��ͨ������findViewByIdȥʵ����������Ҫ��selectedʵ���Ĳ���
				ViewListStruct holder = (ViewListStruct)arg1.getTag();
		        // �ı�CheckBox��״̬               
		 		holder.selected.toggle();
		 		// ��CheckBox��ѡ��״����¼����                
		 		m_list_adapter.getIsSelected().put(arg2, holder.selected.isChecked());
		 		String v_showselect = new String("��ǰѡ�У�");
		 		for(int i = 0; i < m_list_adapter.getCount(); i ++)
		 		{
		 			if(m_list_adapter.getIsSelected().get(i))
		 			{
		 				v_showselect += "i =" + i + "; ";
		 			}
		 		}
		 		//Log.e("���ѡ��״̬",v_showselect);	
		 		//m_datastream_check.setChecked(false);
			}
			
		});
		m_datastream_send_choice = (Button) findViewById(R.id.datastream_choice_ok);
		m_datastream_send_choice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//�������
				int i;
				int len = m_list_adapter.getCount() / 8;
				if((m_list_adapter.getCount() % 8) > 0)
					len ++;
				final byte[] parameterData = new byte[len + 4];
				for(i =0; i < len + 4; i++)
				{
					parameterData[i] = 0x00;
				}
				//�����ͷ
				parameterData[0] = 0x00;
				parameterData[1] = 0x00;
				parameterData[2] = 0x00;
				parameterData[3] = (byte)(len&0xFF);
				String v_show = new String("ѡ�У�");
				int v_list_count = m_list_adapter.getCount(); 
				v_show += "����������=" + v_list_count;
				int v_selected = 0;
				boolean v_selected_too_much = false; //ѡ������������100��
				for(i = 0; i < v_list_count; i ++)
				{
					if(m_list_adapter.getIsSelected().get(i))
					{						
						v_show += "i =" + i + ";";
						v_selected ++;
						if(v_selected > 100)
						{
							v_selected_too_much = true;
							v_selected = 100;
							break;
						}
						parameterData[i / 8 + 4] |= 0x01 << (7 - (i % 8));
					}
					else
					{
						//Log.i(TAG,"ûѡ��-->>������" + i);
					}
				}
				//test
				//v_selected_too_much = true;
				if(v_selected_too_much == true) //��ѡ������������100��ʱ��
				{
					//��ʾ�û�
					final DiagAlertDialog dlg = new DiagAlertDialog(DiagnoseDataStreamChoiceActivity.this);
					dlg.setTitle(R.string.datastream_choice_tip_title_adv);
					dlg.setMessage(R.string.datastream_choice_tip_toomuch);
					dlg.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//�رյ�ǰ����
							m_diag_service.SendDatastreamChoice(parameterData);
							dlg.dismiss();
						}
					});
					dlg.setCancelable(false);
					dlg.show();
				}
				else if(v_selected == 0) //���������ѡ������Ϊ0����ʾ��ѡ��������
				{
					ShowDialog(true,1,4,R.string.datastream_graph_tip_title,R.string.datastream_choice_tip_msg,
			    			R.string.dialog_ok,0,0);
				}
				else
				{
					m_diag_service.SendDatastreamChoice(parameterData);
				}
				if(D) Log.i("���ѡ��״̬",v_show);
			}
		});
		m_back_pre = (Button) findViewById(R.id.datastream_choice_back_pre);
		m_back_pre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_diag_service.SendCurrentbackPre(DiagnoseDataService.CMD_SHOW_SETDATASTREAMSELECT);
			}
		});
		//ȫѡ��ť��ʼ��
		m_datastream_check = (CheckBox)findViewById(R.id.datastream_choice_title_checkbox);
		m_datastream_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				int i;
				if(isChecked == true)
				{
					if(D) Log.i(TAG,"ȫѡ������!");
					
					for(i = 0; i < m_list_adapter.getCount(); i ++)
					{
						m_list_adapter.getIsSelected().put(i, true);						
					}
				}
				else
				{
					if(D) Log.i(TAG,"ȡ��ȫѡ������!");
					for(i = 0; i < m_list_adapter.getCount(); i ++)
					{
						m_list_adapter.getIsSelected().put(i, false);
					}
				}
				m_list_adapter.notifyDataSetChanged();
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
	//����UI�߳�
    private final static int MSG_SHOW_ERROR_WINDOW = 101;  //ˢ��list�б�
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
    		//��������ʾ�Ի����ı�
    		case DiagnoseDataService.CMD_SHOW_GETDIALOG:
    			if(D) Log.i(TAG,"�յ���ʾ�Ի���");
    			ShowDialog((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETMENU: 
    			if(D) Log.i(TAG,"�յ���ʾ�˵�");
    			StartMenuActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETDATASTREAMSELECT:
    			if(D) Log.i(TAG,"�յ�������ѡ��Ի���");
    			UpdateStreamSelectActivity((Bundle)msg.obj);
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
	
	//�Զ���ListView������
	class DatastreamChoiceAdapter extends BaseAdapter{
	    //List<Boolean> mChecked;
	    private List<String> listdatastream;
	    // ��������CheckBox��ѡ��״��
	    private HashMap<Integer,Boolean> isSelected;
	     
	    public DatastreamChoiceAdapter(List<String> list){
	    	listdatastream = new ArrayList<String>();
	    	listdatastream = list;
	         
	    	isSelected = new HashMap<Integer,Boolean>();
	        for(int i=0;i<list.size();i++){
	        	getIsSelected().put(i,false);;
	        }
	    }

	    @Override
	    public int getCount() {
	        return listdatastream.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return listdatastream.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       
	        ViewListStruct liststruct = null;
	         
	        if (convertView == null) 
	        {
	            //Log.e(TAG,"position1 = "+position);
	            //���벼��
	            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.diagnose_formal_datastreamchoose_listitem, null);
	            //��ý�ͷ�����
	            liststruct = new ViewListStruct();
	            liststruct.selected = (CheckBox)convertView.findViewById(R.id.diagnose_datastream_choice_list_checkbox);
	            liststruct.name = (TextView)convertView.findViewById(R.id.diagnose_datastream_choice_list_name);
	            //Ϊview���ñ�ǩ
	            convertView.setTag(liststruct);
	            final int pos = position;
	            final CheckBox v_check = liststruct.selected;
	            liststruct.selected.setOnClickListener(new OnClickListener(){
	            	@Override
	            	public void onClick(View v) {
	            		// TODO Auto-generated method stub
	            		
	            		//getIsSelected().put(pos,v_check.isChecked());
//	            		if(getIsSelected().get(pos) == true)
//	            			Log.e("���ѡ��״̬","ѡ�� Pos=" + pos );
//	            		else
//	            			Log.e("���ѡ��״̬","δѡ�� Pos=" + pos );
	            		//m_datastream_check.setChecked(false);
	            	}
	            });
	                        
	        }else{
	            //Log.e(TAG,"position2 = "+position);
	           	// ȡ��liststruct
	            liststruct = (ViewListStruct)convertView.getTag();
	        }
	        //����isSelected������checkbox��ѡ��״��
	        liststruct.selected.setId(position);
	        liststruct.selected.setChecked(getIsSelected().get(position));
	        liststruct.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					getIsSelected().put(buttonView.getId(), isChecked); 
					//Log.e("���ѡ��״̬","�����" + buttonView.getId()+ "----״̬��" + isChecked);
                    notifyDataSetChanged();  
				}
			});
	        //����list��TextView����ʾ
	        liststruct.name.setText(listdatastream.get(position));
	         
	        return convertView;
	    }
	    public HashMap<Integer,Boolean> getIsSelected() 
	    {        
	    	return isSelected;    
	    }    
	    public void setIsSelected(HashMap<Integer,Boolean> PisSelected) 
	    {        
	    	isSelected = PisSelected;    
	    }
	}
	static class ViewListStruct{
        CheckBox selected;
        TextView name;
    }
	
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
		if(D) Log.i(TAG,"SHOW��len=" + (datalen - 8) + "Data:" + v_show);
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
					else if(v_mode == 4)  //ɶҲ����
					{
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
    //��ʾ������Ϣ�Ի���0Ϊ��ʱ���󣬴���0Ϊ�������ID
    private void ShowErrorWindow(int error)
    {
    	if(D) Log.e(TAG,"����ID��" + error);
    	//�رս�����
    	ShowDialog(true,1,1,R.string.diag_commun_error_title,m_diag_service.GetDiagErrorID(error),
    			R.string.dialog_ok,0,0);
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
    //�յ���������ʾ�Ի���
    private void StartDataStreamShowActivity(Bundle bundle)
    {
    	if(m_show_dialog != null)
    	{
    		m_show_dialog.dismiss();
    		m_show_dialog = null;
    	}
    	Intent to_show_dataStream = new Intent(context,DiagnoseDataStreamShowActivity.class);
    	to_show_dataStream.putExtras(bundle);
    	this.startActivity(to_show_dataStream);
    	m_blue_service.DelObserver(this); //ɾ���۲���
    }
    //ˢ���Լ�
    private void UpdateStreamSelectActivity(Bundle bundle)
    {
    	if(m_show_dialog != null)
    	{
    		m_show_dialog.dismiss();
    		m_show_dialog = null;
    	}
    	m_now_diag = 0; //��λ�Ի���
    	m_list_data = bundle.getStringArrayList("DATASTREAM_NAME");
    	m_list_adapter.notifyDataSetChanged();
    }
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}

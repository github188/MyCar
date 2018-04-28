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
	private CheckBox m_datastream_check = null;  //全选按钮
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DiagnoseSettings.setCurrentDiagnoseActivity(this);
		setContentView(R.layout.diagnose_formal_datastreamchoose);
		// 得到数据
		Bundle bundle = getIntent().getExtras();
		//设置listView
		m_list = (ListView) findViewById(R.id.datastream_choice_listview);
		//list获得数据
		m_list_data = bundle.getStringArrayList("DATASTREAM_NAME");
		m_list_adapter = new DatastreamChoiceAdapter(m_list_data);
		//设置list关联
		m_list.setAdapter(m_list_adapter);
		//绑定list监听器
		m_list.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				// TODO Auto-generated method stub
				// 取得ViewListStruct对象，这样就省去了通过层层的findViewById去实例化我们需要的selected实例的步骤
				ViewListStruct holder = (ViewListStruct)arg1.getTag();
		        // 改变CheckBox的状态               
		 		holder.selected.toggle();
		 		// 将CheckBox的选中状况记录下来                
		 		m_list_adapter.getIsSelected().put(arg2, holder.selected.isChecked());
		 		String v_showselect = new String("当前选中：");
		 		for(int i = 0; i < m_list_adapter.getCount(); i ++)
		 		{
		 			if(m_list_adapter.getIsSelected().get(i))
		 			{
		 				v_showselect += "i =" + i + "; ";
		 			}
		 		}
		 		//Log.e("点击选择状态",v_showselect);	
		 		//m_datastream_check.setChecked(false);
			}
			
		});
		m_datastream_send_choice = (Button) findViewById(R.id.datastream_choice_ok);
		m_datastream_send_choice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//组成命令
				int i;
				int len = m_list_adapter.getCount() / 8;
				if((m_list_adapter.getCount() % 8) > 0)
					len ++;
				final byte[] parameterData = new byte[len + 4];
				for(i =0; i < len + 4; i++)
				{
					parameterData[i] = 0x00;
				}
				//填参数头
				parameterData[0] = 0x00;
				parameterData[1] = 0x00;
				parameterData[2] = 0x00;
				parameterData[3] = (byte)(len&0xFF);
				String v_show = new String("选中：");
				int v_list_count = m_list_adapter.getCount(); 
				v_show += "数据流条数=" + v_list_count;
				int v_selected = 0;
				boolean v_selected_too_much = false; //选中数据流超过100条
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
						//Log.i(TAG,"没选中-->>数据流" + i);
					}
				}
				//test
				//v_selected_too_much = true;
				if(v_selected_too_much == true) //当选中数据流超过100条时候
				{
					//提示用户
					final DiagAlertDialog dlg = new DiagAlertDialog(DiagnoseDataStreamChoiceActivity.this);
					dlg.setTitle(R.string.datastream_choice_tip_title_adv);
					dlg.setMessage(R.string.datastream_choice_tip_toomuch);
					dlg.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//关闭当前界面
							m_diag_service.SendDatastreamChoice(parameterData);
							dlg.dismiss();
						}
					});
					dlg.setCancelable(false);
					dlg.show();
				}
				else if(v_selected == 0) //如果数据流选择条数为0，提示请选择数据流
				{
					ShowDialog(true,1,4,R.string.datastream_graph_tip_title,R.string.datastream_choice_tip_msg,
			    			R.string.dialog_ok,0,0);
				}
				else
				{
					m_diag_service.SendDatastreamChoice(parameterData);
				}
				if(D) Log.i("点击选择状态",v_show);
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
		//全选按钮初始化
		m_datastream_check = (CheckBox)findViewById(R.id.datastream_choice_title_checkbox);
		m_datastream_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				int i;
				if(isChecked == true)
				{
					if(D) Log.i(TAG,"全选数据流!");
					
					for(i = 0; i < m_list_adapter.getCount(); i ++)
					{
						m_list_adapter.getIsSelected().put(i, true);						
					}
				}
				else
				{
					if(D) Log.i(TAG,"取消全选数据流!");
					for(i = 0; i < m_list_adapter.getCount(); i ++)
					{
						m_list_adapter.getIsSelected().put(i, false);
					}
				}
				m_list_adapter.notifyDataSetChanged();
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
    		case DiagnoseDataService.CMD_SHOW_GETDATASTREAMSELECT:
    			if(D) Log.i(TAG,"收到数据流选择对话框");
    			UpdateStreamSelectActivity((Bundle)msg.obj);
    			break;
    		case DiagnoseDataService.CMD_SHOW_GETDATASTREAM:
    			if(D) Log.i(TAG,"收到数据显示对话框");
    			StartDataStreamShowActivity((Bundle)msg.obj);
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
	
	//自定义ListView适配器
	class DatastreamChoiceAdapter extends BaseAdapter{
	    //List<Boolean> mChecked;
	    private List<String> listdatastream;
	    // 用来控制CheckBox的选中状况
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
	            //导入布局
	            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.diagnose_formal_datastreamchoose_listitem, null);
	            //获得接头体对象
	            liststruct = new ViewListStruct();
	            liststruct.selected = (CheckBox)convertView.findViewById(R.id.diagnose_datastream_choice_list_checkbox);
	            liststruct.name = (TextView)convertView.findViewById(R.id.diagnose_datastream_choice_list_name);
	            //为view设置标签
	            convertView.setTag(liststruct);
	            final int pos = position;
	            final CheckBox v_check = liststruct.selected;
	            liststruct.selected.setOnClickListener(new OnClickListener(){
	            	@Override
	            	public void onClick(View v) {
	            		// TODO Auto-generated method stub
	            		
	            		//getIsSelected().put(pos,v_check.isChecked());
//	            		if(getIsSelected().get(pos) == true)
//	            			Log.e("点击选择状态","选中 Pos=" + pos );
//	            		else
//	            			Log.e("点击选择状态","未选中 Pos=" + pos );
	            		//m_datastream_check.setChecked(false);
	            	}
	            });
	                        
	        }else{
	            //Log.e(TAG,"position2 = "+position);
	           	// 取出liststruct
	            liststruct = (ViewListStruct)convertView.getTag();
	        }
	        //根据isSelected来设置checkbox的选中状况
	        liststruct.selected.setId(position);
	        liststruct.selected.setChecked(getIsSelected().get(position));
	        liststruct.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					getIsSelected().put(buttonView.getId(), isChecked); 
					//Log.e("点击选择状态","点击：" + buttonView.getId()+ "----状态：" + isChecked);
                    notifyDataSetChanged();  
				}
			});
	        //设置list中TextView的显示
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
		if(D) Log.i(TAG,"SHOW：len=" + (datalen - 8) + "Data:" + v_show);
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
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDTC_ADD) //显示故障码
			{
				m_diag_service.GetShowDTCActivityAdd(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDATASTREAMSELECT) //数据流选择
			{
				m_diag_service.GetShowDatastreamChoiceActivity(m_handler, v_recv_buf, v_recv_len);
			}
			else if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDATASTREAM) //数据流显示
			{
				m_diag_service.GetShowDatastreamActivity(m_handler, v_recv_buf, v_recv_len);
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
					else if(v_mode == 4)  //啥也不干
					{
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
    //收到数据流显示对话框
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
    	m_blue_service.DelObserver(this); //删除观察者
    }
    //刷新自己
    private void UpdateStreamSelectActivity(Bundle bundle)
    {
    	if(m_show_dialog != null)
    	{
    		m_show_dialog.dismiss();
    		m_show_dialog = null;
    	}
    	m_now_diag = 0; //复位对话框
    	m_list_data = bundle.getStringArrayList("DATASTREAM_NAME");
    	m_list_adapter.notifyDataSetChanged();
    }
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}

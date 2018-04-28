package com.cnlaunch.mycar.diagnose.simplereport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseConstant;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.diagnose.service.DiagnoseSimpleReportDataService;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseQuestionCategory;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseQuestionCategoryAdapter;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseShowInfoStr;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseSimpleDataInfoAdapter;
import com.cnlaunch.mycar.rescuevehicles.CellLocationManager;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;
import com.j256.ormlite.dao.Dao;

public class DiagnoseSimpleReportActivity extends BaseActivity implements BluetoothInterface,OnClickListener{
	private static final String TAG = "SimpleReportActivity";
    private static final boolean D = false;
    Context context = DiagnoseSimpleReportActivity.this; //Context ����������
	private View viewQuestionInfo;
	private PopupWindow pop_questionInfo; //������������ϸ����
	private Button btnCarExam; //�������鰴ť
	private ImageButton btnShowDatail;
	private ProgressBar spProgress;  //��ʾ������
	private ListView detailList;
	private TextView questionNum;
	private TextView causeResult;
	private TextView helpAdvice;
	private TextView currentProInfo;
	private LinearLayout scanShowInfoLayout;
    private TextView examNumText;
	private TextView examNumTextInfo;
	private Resources resources;
	private RelativeLayout questionListDatail;
	private List<HashMap<String, String>> simpleReportList;
	private DiagnoseSimpleDataInfoAdapter adapter; 
	private static boolean isShowChange = true;
	private static boolean isShowSimpleReportDetail = true;
	private static boolean isShowSimpleReportItem=true;
	private static int diaLogErrorType=8;////��ֵ��ǰ�Ի����������
	private static Integer ITEM_NUM;
	Map<Integer,LinearLayout>  viewMap;
	private ProgressBar circleProgressBar;
	//�������б�
	 ListView categoryList=null;
	//�������б�������
    private DiagnoseQuestionCategoryAdapter mCategoryAdapter; 
    private List<HashMap<String,List<DiagnoseQuestionCategory>>> questionList = new ArrayList<HashMap<String,List<DiagnoseQuestionCategory>>>(); // ������ϸ��Ϣ
    //��ʼ����������
	private BluetoothDataService m_blue_service = null;
	//���ױ������
	private DiagnoseSimpleReportDataService simple_report_service = null;
	//����ַ�����Ϣ
	private DiagnoseShowInfoStr showInfoStr; 
    //ͨѶ����
    private int m_step = 0; 
    //�����������ӽ�����
	private ProgressDialog progressDialog;
	//�����Ի�����ʾ
	private AlertDialog showDiag;
	//�ж��Ƿ񰴷��ؼ�
	private boolean backState=false;
	//������
	ConditionVariable next = new ConditionVariable(false);
	//��ǰ�Ի�����ʽ
    private int m_now_diag = 0;
    private String  m_select_car_name;
    private String  m_select_car_version;
    private String m_ggppath;
    //���������뵯�����߳�
    private Thread popThread;
    //��ʱ�߳�
    private TimerThread timerThread;
    private boolean enableClick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.diagnose_simple_report, R.layout.custom_title);
		setCustomeTitleLeft(R.string.simple_report);
		setCustomeTitleRight("");
		findView();
		enableClick = true;
		viewMap=new HashMap<Integer,LinearLayout>();
		//������������
		m_blue_service = BluetoothDataService.getInstance();
		//��ӹ۲���
		m_blue_service.AddObserver(this);
		
		//��ȡ�ַ�����Ϣ
		showInfoStr = DiagnoseShowInfoStr.getInstance();
		showInfoStr.setContext(context);
		
		//���ױ������
		simple_report_service = DiagnoseSimpleReportDataService.getInstance();
		simple_report_service.setContext(context);
		simple_report_service.setHandler(carExamHandler);
		simple_report_service.setShowInfoStr(showInfoStr);
		simple_report_service.setActivity(this);		
//		simple_report_service.InitialGGPInstance();

		//������������ϸ�б�
		viewQuestionInfo =this.getLayoutInflater().inflate(
				R.layout.diagnose_simple_report_question, null);	
		pop_questionInfo = new PopupWindow(viewQuestionInfo,Env.getScreenWidth(DiagnoseSimpleReportActivity.this),
				400);		
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(this.getString(R.string.diag_sp_activity_init));
		categoryList =(ListView) viewQuestionInfo.findViewById(R.id.simple_report_question_list_view);   		
		//��ʾ��ʷ���ߵ�ǰ���
		ShowHistoryRecordOrQuickDiag();
	
	}
	private void findView() {
		examNumTextInfo=(TextView)findViewById(R.id.simple_report_exam_num_info);
		btnCarExam=(Button) findViewById(R.id.sp_car_exam);
		btnCarExam.setOnClickListener(this);
		btnShowDatail=(ImageButton)findViewById(R.id.sp_show_datail);
		btnShowDatail.setOnClickListener(this);
		spProgress=(ProgressBar)findViewById(R.id.sp_progress);
		examNumText=(TextView)findViewById(R.id.sp_exam_num_info);
		currentProInfo=(TextView)findViewById(R.id.current_pro_info);		
		resources = getResources();
		simpleReportList=new ArrayList<HashMap<String, String>>();
		detailList = (ListView) findViewById(R.id.simple_report_detail_list);
		questionNum=(TextView)findViewById(R.id.simple_report_question_num);
		causeResult=(TextView)findViewById(R.id.simple_report_cause_result_info);
		helpAdvice=(TextView)findViewById(R.id.simple_report_help_advise_info);		
		questionListDatail=(RelativeLayout)findViewById(R.id.simple_report_question_list);	
		circleProgressBar=(ProgressBar)findViewById(R.id.examCircleProgressBar);
		scanShowInfoLayout=(LinearLayout)findViewById(R.id.diag_sp_scan_show_info);
	}
	/*
	 * �����б�ĸ߶�
	 * */
	private int geQuestionInfoHeight() {
		LinearLayout test_info = (LinearLayout) findViewById(R.id.simple_repor_info_test);
		RelativeLayout question_info = (RelativeLayout) findViewById(R.id.question_num_show);
		return Env.getScreenHeight(this)
		// ��Ļ�߶�,��������߶�,�˵��߶�
				- question_info.getHeight() - test_info.getHeight()
				// ����߶�
				- R.dimen.window_title_size;
	}	
	 /**
     * ��ʾ�������б���Ϣ
     * */
    public void showQuestonList(){
    	  if (isShowSimpleReportDetail) {
    		  btnShowDatail.setImageResource(R.drawable.diagnose_simple_report_up);
    		  showQuestionInfo();
    		  questionListDatail.setVisibility(View.GONE);
    		  isShowSimpleReportDetail = false;
             } else {
            	btnShowDatail.setImageResource(R.drawable.diagnose_simple_report_down);
            	hideQuestionInfo();
            	questionListDatail.setVisibility(View.VISIBLE);
            	isShowSimpleReportDetail =true;
            }
    	
    }
	/**
	 * ��ʾ������Ϣ
	 */
	private void showQuestionInfo() {
		pop_questionInfo.showAsDropDown(findViewById(R.id.question_num_show));
	}

	/**
	 * ���ع�����Ϣ
	 */
	private void hideQuestionInfo() {
	//liaochuanhai
//		pop_questionInfo.dismiss();
	}
	
	/**
	 *  �������б����ݰ�
	 * */
	public void bindQuestionList(List<HashMap<String, List<DiagnoseQuestionCategory>>> list){ 
		 categoryList.removeAllViewsInLayout();
		 questionList=list;
		 if(questionList!=null){
	      mCategoryAdapter=new DiagnoseQuestionCategoryAdapter(this,questionList);  
	      categoryList.setAdapter(mCategoryAdapter);
		 }
		 else{		 
			 categoryList.setAdapter(null); 
		 }
	       
	}
	/*
	 * ���ױ������ϸ�б�����
	 * */
	public void bindSimpleDataList(List<HashMap<String, String>> list){
		// ��listview����
		detailList.removeAllViewsInLayout();//����б�����
		simpleReportList = list;
		if(simpleReportList!=null){
			// ����������������===��ListItem
			adapter =new DiagnoseSimpleDataInfoAdapter(this,simpleReportList);
	
			// ��Ӳ�����ʾ
			detailList.setAdapter(adapter);
		}
		else{
			detailList.setAdapter(null);
		}
	}

    //����UI�߳�
    private final static int SIMPLE_REPORT_STEP_1 = 101;  //ˢ�½�����
    private final static int SIMPLE_REPORT_STEP_2 = 102;  //ˢ�½�����
    private final static int SIMPLE_REPORT_STEP_3 = 103;  //ˢ�½�������Ϣ����ʾ�б�
    private final static int SIMPLE_REPORT_STEP_4 = 104;  //��������Ϣ
    private final static int MSG_SHOW_ERROR_WINDOW = 105; //��ʾ������Ϣ�Ի���
    private final static int SIMPLE_REPORT_STEP_6 = 106; //����button�ı���Ϣ
    private final static int SIMPLE_REPORT_STEP_7 = 107; //����button״̬
    private final static int SIMPLE_REPORT_STEP_8 = 108; //��ʾ�汾��һ����ʾ
    private final static int SIMPLE_REPORT_STEP_9 = 109; //����б�
	private final static int MSG_SHOW_UPDATE_DIAGLOG = 112;	//��ʾ��������������ʾ��
	private final static int DT_CMD_MODE = 1; // ����ģʽ��ʱ
	private final static int DT_APPOINT_DATA_LIST = 2; // ��ȡָ��ϵͳ�������б��ʱ
	private final static int DT_APPOINT_ID_LIST = 3; // ��ȡָ��ID��������ʱ
	private final static int DT_APPOINT_DOC_LIST = 4; //��ȡָ��ϵͳ�������б��ʱ
	private final static int SHOW_QUESTION_LIST = 5; //��ʾ�����ع������б���Ϣ
	private final static int BLUE_TOOTH_LOST = 6; //��ʾ�����ع������б���Ϣ
    private Handler carExamHandler = new Handler() {   
        public void handleMessage(Message msg) {   
    		switch(msg.what)
    		{
    		case SIMPLE_REPORT_STEP_1:
    			//ˢ�½�����    			
				spProgress.setProgress(msg.arg1);    
				circleProgressBar.setVisibility(View.VISIBLE);
//				currentProInfo.setText(R.string.diag_scan_sys_data_stream);
				currentProInfo.setText(R.string.diag_scan_sys_question_code);
				List<DiagnoseQuestionCategory> idTextList=(List<DiagnoseQuestionCategory>)msg.obj;
//				saveSysIDText(idTextList);
    			break;
    		case SIMPLE_REPORT_STEP_2:
    			//ˢ�½�����
    			circleProgressBar.setVisibility(View.VISIBLE);
    			spProgress.setProgress(msg.arg1);     
//	        	examNumText.setText(String.valueOf(msg.arg1));
    			break;
    		case SIMPLE_REPORT_STEP_3: 
    			//ˢ�½����� ˢ���������б�
    			List<HashMap<String, String>> list=(List<HashMap<String, String>>)msg.obj;
    			bindSimpleDataList(list);//ˢ���б�    			
    			spProgress.setProgress(msg.arg1);   
    			circleProgressBar.setVisibility(View.VISIBLE);
    			currentProInfo.setText(R.string.diag_scan_sys_question_code);
    			break;
    		case SIMPLE_REPORT_STEP_4:
    			//ˢ�½����� ˢ�¹������б�
    			List<HashMap<String, List<DiagnoseQuestionCategory>>> list2=(List<HashMap<String, List<DiagnoseQuestionCategory>>>)msg.obj;
    			bindQuestionList(list2);
    			int num=msg.arg2;
    			questionNum.setText(String.valueOf(num));    
    			spProgress.setProgress(100);  
    			circleProgressBar.setVisibility(View.GONE);
	        	examNumText.setText(String.valueOf(msg.arg1));	
	        	scanShowInfoLayout.setGravity(Gravity.CENTER);
	        	currentProInfo.setText(R.string.diag_scan_finish);
//	        	currentProInfo.setGravity(Gravity.CENTER);	        	
	        	SharedPreferences preExamNum2=getSharedPreferences(DiagnoseConstant.PRE_EXAM_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
	        	preExamNum2.edit().putString(DiagnoseConstant.PRE_EXAM_NUM,String.valueOf(msg.arg1)).commit(); 
	        	SharedPreferences preDocNum=getSharedPreferences(DiagnoseConstant.PRE_DOC_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
	        	preDocNum.edit().putInt(DiagnoseConstant.PRE_DOC_NUM,num).commit();   
	        	saveDocInfoList(list2);
//	        	btnCarExam.setEnabled(true);
	        	btnCarExam.setText(R.string.car_exam);
//	        	btnCarExam.setBackgroundResource(R.drawable.main_button_normal);
    			break;
    		case MSG_SHOW_UPDATE_DIAGLOG: 	
    		   	spProgress.setProgress(0);   
    	    	circleProgressBar.setVisibility(View.GONE);
    	    	currentProInfo.setText("");
    	    	btnCarExam.setText(R.string.car_exam);	
    	    	examNumText.setText("");
    			dialog(resources.getString(R.string.diagnose_sp_update_show_messge));
    			break;
    		case MSG_SHOW_ERROR_WINDOW:
    			//��ʾ������Ϣ�Ի���
    			ShowErrorWindow(msg.arg1);
    			break;
    		case SIMPLE_REPORT_STEP_6:
    			//��ȡָ��ID������
    			break;
    		case SIMPLE_REPORT_STEP_7:   
    			circleProgressBar.setVisibility(View.VISIBLE);
    			currentProInfo.setText(R.string.diag_scan_sys_list);
    			spProgress.setProgress(msg.arg1);  
    			break;
    		case SIMPLE_REPORT_STEP_8:  
    			circleProgressBar.setVisibility(View.VISIBLE);
    			spProgress.setProgress(msg.arg1);  
    			break;
    		case DT_CMD_MODE:
    			//����ģʽ��ʱ
    			simple_report_service.EnterPassword();		
    			break;
    		case DT_APPOINT_DATA_LIST:
    			//��ȡָ��ϵͳ�������б��ʱ
    			byte[] param=(byte[])msg.obj;
    			simple_report_service.SendSampleReportCmd(3, param);
    			break;
    		case DT_APPOINT_ID_LIST:
    			//��ȡָ��ID��������ʱ
    			byte[] param2=(byte[])msg.obj;
    			simple_report_service.SendSampleReportCmd(6, param2);
    			break;
    		case DT_APPOINT_DOC_LIST:
    			//��ȡָ��ϵͳ�������б��ʱ
    			byte[] param3=(byte[])msg.obj;
    			simple_report_service.SendSampleReportCmd(4, param3);
    			break;	 
    		case SHOW_QUESTION_LIST:
    			//��ʾ�����ع������б���Ϣ
    			 btnShowDatail.setImageResource(R.drawable.diagnose_simple_report_up);
        		 pop_questionInfo.showAsDropDown(findViewById(R.id.question_num_show));
        		 questionListDatail.setVisibility(View.GONE);        		
        		 isShowSimpleReportDetail = false;
        		 if(popThread!=null){
        			 popThread=null;
        		 }
    			break;	
    			//��������ʾ�Ի����ı�
    		case DiagnoseDataService.CMD_SHOW_GETDIALOG:
    			ShowDialog((Bundle)msg.obj);
    			break;
    		case BLUE_TOOTH_LOST:
    			//��ʾ�����ع������б���Ϣ
    			int connectType= msg.arg2;
    			//�������ӳɹ��ر���ʾ��,���򵯳���ʾ��
    			if(connectType==1){
    				if(showDiag!=null){
    					showDiag.dismiss();
    				}
    			}else{
    			  ShowBluetoothConnWindow(msg.arg1);
    			}
    			break;	  
    		default:
    			break;
    		}
    		super.handleMessage(msg);
        }   
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sp_car_exam:
			if(enableClick){
				//�򿪼��׷���
				String carInfo=((String)btnCarExam.getText()).trim();
				if(carInfo.equals(resources.getString(R.string.car_exam))){
					openSimpleReportDataService();	
				}
				else{
					this.clearPreRecord();
					btnCarExam.setText(R.string.car_exam);
//					enableClick = false;
					simple_report_service.setResetGetMode();// ���븴λģʽ
//					timerThread =new TimerThread();
//					timerThread.start();
					
				}
			}
			else{
				dialog(resources.getString(R.string.diag_sp_timeing_info));
			}
			break; 
		case R.id.sp_show_datail:
	    	// ��ʾ�������б���Ϣ
//			showQuestonList();
			break;
		default:
			break;
		}
	}
	 //��ʾ������Ϣ�Ի���0Ϊ��ʱ���󣬴���0Ϊ�������ID
    private void ShowErrorWindow(int error)
    {
    	int v_err_id = showInfoStr.GetDiagErrorID(error);//simple_report_service.GetDiagErrorID(error);
    	if(D) Log.e(TAG,"����ID��" + v_err_id);
    	//�رս�����
    	spProgress.setProgress(0);   
    	circleProgressBar.setVisibility(View.GONE);
    	currentProInfo.setText("");
//    	btnCarExam.setEnabled(true);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_normal);
    	btnCarExam.setText(R.string.car_exam);
		m_now_diag =diaLogErrorType; //��ֵ��ǰ�Ի����������
		if(error!=7&&error!=8){
			showDiag=new AlertDialog.Builder(this)
	    	.setTitle(R.string.diag_commun_error_title)
	    	.setMessage(v_err_id)
	    	.setPositiveButton(R.string.dialog_retry, new DialogInterface.OnClickListener() {
							@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
						circleProgressBar.setVisibility(View.VISIBLE);			
						currentProInfo.setText(R.string.diag_scan_sys_list);
//					   	btnCarExam.setEnabled(false);
					   	btnCarExam.setText(R.string.diag_stop_car_exam);
						simple_report_service.setOrGetMode(new byte[] { 0x06 });
				}
			})
			.setNegativeButton(R.string.dialog_cancle, null)
			.show();
		}
		else if(error==8){
			showDiag=new AlertDialog.Builder(this)
	    	.setTitle(R.string.diag_sp_sys_info_show)
	    	.setMessage(v_err_id)
	    	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
				}
			}).show();
		}
		else{
			showDiag=new AlertDialog.Builder(this)
	    	.setTitle(R.string.diag_sp_error_title2)
	    	.setMessage(v_err_id)
	    	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub	
				}
			}).show();
		}
    }
	 //
    private void ShowBluetoothConnWindow(int error)
    {
    	int v_err_id = showInfoStr.GetDiagErrorID(error);
    	if(D) Log.e(TAG,"����ID��" + v_err_id);
    	//�رս�����
    	spProgress.setProgress(0);   
    	circleProgressBar.setVisibility(View.GONE);
    	currentProInfo.setText("");
//    	btnCarExam.setEnabled(true);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_normal);
    	btnCarExam.setText(R.string.car_exam);
		if(showDiag!=null){
			showDiag.dismiss();
		}
		showDiag=new AlertDialog.Builder(this)
    	.setTitle(R.string.diag_commun_error_title)
    	.setMessage(v_err_id)
    	.setPositiveButton(R.string.diag_sp_bluetooth_re_connect, new DialogInterface.OnClickListener() {
						@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				m_blue_service.ShowBluetoothConnectActivity(DiagnoseSimpleReportActivity.this);
				
			}
		})
		.setNegativeButton(R.string.dialog_cancle, null)
		.show();
    }
	/**
	 * ��������¼�
	 * 
	 * @param v
	 */
	// TODO
	public void MenuButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.question_num_show:
			// ��ʾ�������б���Ϣ
			//liaochuanhai
//			showQuestonList();
			break;
		default:
			break;
		}
	}
	
	
	 //�������ױ�����������
	public void openSimpleReportDataService(){
    	//���SD���Ƿ����

    	if(Env.isSDCardAvailable(context) == false)
    	{
    		new AlertDialog.Builder(this)
        	.setTitle(R.string.error_title)
        	.setMessage(R.string.version_no_find_sd_card)
        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				finish();
    			}
    		})
    		.show();
    	}
    	else if(m_blue_service.IsConnected() == false){
    		progressDialog.show();
    		m_blue_service.ShowBluetoothConnectActivity(this);
    	}
    	else{   
			this.clearPreRecord();
			enableClick = false;
    		simple_report_service.setOrGetMode(new byte[] { 0x06 });// ��������ģʽ
    		timerThread =new TimerThread();
			timerThread.start();
    	}
    		
		
	}
	@Override
	protected void onStart() {
		if(D) Log.i(TAG,"onStart");
		super.onStart();
	}
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	if(D) Log.i(TAG,"onPause");
    	if(progressDialog!=null){
			progressDialog.dismiss();
		}
    	super.onPause();
    }
	@Override
	protected void onStop() {
		if(D) Log.i(TAG,"onStop");
		super.onStop();
	}
	@Override
	protected void onResume() {
		if(D) Log.i(TAG,"onResume");
		super.onResume();
	}

	//���ٷ���
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (D)
			Log.i(TAG, "onDestroy");
		if(pop_questionInfo!=null){
			pop_questionInfo.dismiss();
		}
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
		if(showDiag!=null){
			showDiag.dismiss();
		}
		m_blue_service.DelObserver(this);
		super.onDestroy();
	}

	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"BlueConnectLost" + name);
		if(!backState){
		   carExamHandler.obtainMessage(BLUE_TOOTH_LOST,6,0).sendToTarget();
		}
	}

	@Override
	public void BlueConnected(String name, String mac) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"BlueConnected" + name);
		 carExamHandler.obtainMessage(BLUE_TOOTH_LOST,6,1).sendToTarget();
	}
	//�յ�����֪ͨ<������> + <����>
	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		String v_show = BluetoothDataService.bytesToHexString(databuf,datalen);
//		if(D)
			Log.i(TAG,"SHOW��" + v_show);
    	simple_report_service.GetDataFromService(databuf, datalen);
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"���ճ�ʱ��step=");
		carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,0,0).sendToTarget();
	}

	/********************* �����޹� ***********************/

	// ���·��ذ�ť�¼�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final DiagAlertDialog dlg = new DiagAlertDialog(this);
			dlg.setTitle(R.string.diagnose_exit_title);
			dlg.setMessage(R.string.diagnose_sp_exit_messge);
			dlg.setPositiveButton(R.string.dialog_yes, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					backState=true;
					if(pop_questionInfo!=null){
						pop_questionInfo.dismiss();
					}
					if(progressDialog!=null){
						progressDialog.dismiss();
					}
					if(showDiag!=null){
						showDiag.dismiss();
					}
					
					simple_report_service.setResetGetMode();// ���븴λģʽ
					simple_report_service.closeGGPInstance();
					finish();
	            	dlg.dismiss();
				}
			});
			dlg.setNegativeButton(R.string.dialog_no, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dlg.dismiss();
				}
			});
			dlg.show();
			return true;
		}
		return false;
	}

	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"SHOW���ر�����" );
		
	}
	//����ϵͳID�ı�����
	public void saveSysIDText(List<DiagnoseQuestionCategory> list){
		SharedPreferences sysIdTextInfo=getSharedPreferences(DiagnoseConstant.SYS_ID_TEXT_PREFS, Context.MODE_WORLD_WRITEABLE);
		sysIdTextInfo.edit().clear().commit();
		for(DiagnoseQuestionCategory info:list){
			sysIdTextInfo.edit().putString(info.getCategoryParentId(),info.getCategoryParentTextID()).commit();
		}
		
	}
	 //���������ID�ַ���
	public void saveDocInfoList(List<HashMap<String, List<DiagnoseQuestionCategory>>> list){
    	SharedPreferences preQuestionInfo = getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);	  
    	preQuestionInfo.edit().clear().commit();
    	String questionStr=simple_report_service.saveDocIDStr(list);
    	preQuestionInfo.edit().putString(Constants.DBSCAR_SIMPLE_DIAGNOSE, m_ggppath).commit();
    	preQuestionInfo.edit().putString(Constants.DBSCAR_CURRENT_CAR_TYPE, m_select_car_name).commit();
    	preQuestionInfo.edit().putString(Constants.DBSCAR_CURRENT_VERSION, m_select_car_version).commit();
    	preQuestionInfo.edit().putString(DiagnoseConstant.CURRENT_LANGUAGE, Env.GetCurrentLanguage()).commit();
    	preQuestionInfo.edit().putString(DiagnoseConstant.PRE_QUESTION_LIST, questionStr).commit();
	    for(int i=0;i<list.size();i++){
		    	 HashMap<String, List<DiagnoseQuestionCategory>> map=list.get(i);
		    	 List<DiagnoseQuestionCategory> questionList=map.get(DiagnoseConstant.V_DOC_PIDID);
		    	 preQuestionInfo.edit().putString(questionList.get(0).getCategoryParentId(), questionList.get(0).getCategoryParentStr()).commit();
		    	 for(int j=0;j<questionList.size();j++){
		    		 DiagnoseQuestionCategory questionCate=questionList.get(j);
		    		 preQuestionInfo.edit().putString(questionCate.getCategoryId(),questionCate.getQuestionNum()+"|"+questionCate.getQuestionInfo()).commit();
		    	 }	    	 
		    	 
	   }
	}
	//��ʾ��ʷ���ߵ�ǰ���
	public void ShowHistoryRecordOrQuickDiag(){
		Intent intent = getIntent();
		if (intent.hasExtra(DiagnoseConstant.DIAG_SP_PUSH_KEY)) {
			if (intent.getStringExtra(DiagnoseConstant.DIAG_SP_PUSH_KEY)
					.equals(DiagnoseConstant.DIAG_SP_PUSH_VALUE)) {
				//��ʾ�ϴ�������
				SharedPreferences preExamNum=getSharedPreferences(DiagnoseConstant.PRE_EXAM_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
				SharedPreferences preQuestionInfo=getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);
				if(preExamNum.contains(DiagnoseConstant.PRE_EXAM_NUM)){				
					String currentLanguage =preQuestionInfo.getString(DiagnoseConstant.CURRENT_LANGUAGE, "");
					if(Env.GetCurrentLanguage().equals(currentLanguage)){
					  examNumText.setText(preExamNum.getString(DiagnoseConstant.PRE_EXAM_NUM, ""));
					}
				}
				//��ʾ��������Ϣ
				String currentLanguage =preQuestionInfo.getString(DiagnoseConstant.CURRENT_LANGUAGE, "");
				m_ggppath=preQuestionInfo.getString(Constants.DBSCAR_SIMPLE_DIAGNOSE, "");
				m_select_car_name = preQuestionInfo.getString(Constants.DBSCAR_CURRENT_CAR_TYPE,"");
				m_select_car_version = preQuestionInfo.getString(Constants.DBSCAR_CURRENT_VERSION,"");
				examNumTextInfo.setText(R.string.pre_exam_num);
				simple_report_service.setM_ggppath(m_ggppath);
				if(Env.GetCurrentLanguage().equals(currentLanguage)){
					String docStr=preQuestionInfo.getString(DiagnoseConstant.PRE_QUESTION_LIST, "");
					List<HashMap<String, List<DiagnoseQuestionCategory>>> list=simple_report_service.GetSimpleAppointQuestionList(docStr);	
					bindQuestionList(list);
					SharedPreferences preDocNum=getSharedPreferences(DiagnoseConstant.PRE_DOC_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
					int num=preDocNum.getInt(DiagnoseConstant.PRE_DOC_NUM, 0);
					questionNum.setText(String.valueOf(num));  
					popThread=new Thread()
				        {
				            @Override
				        	public void run()
				            {
				              try {
								this.sleep(1000);
								carExamHandler.obtainMessage(SHOW_QUESTION_LIST).sendToTarget();//��ʾ��������Ϣ		
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				            }
				        };	
				    popThread.start();
				    
			    }
			}
		}else if(intent.hasExtra(Constants.DBSCAR_SIMPLE_DIAGNOSE)){			
			try {
				m_ggppath=intent.getStringExtra(Constants.DBSCAR_SIMPLE_DIAGNOSE);
				m_select_car_name = intent.getStringExtra(Constants.DBSCAR_CURRENT_CAR_TYPE);
				m_select_car_version = intent.getStringExtra(Constants.DBSCAR_CURRENT_VERSION);
				simple_report_service.setM_ggppath(m_ggppath);
				showDocListPop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
	}

    //��ʾ�Ի���
    private void ShowDialog(Bundle bundle)
    {
		if(D) Log.i(TAG,"�յ���ʾ�Ի���");
		spProgress.setProgress(0);   
    	circleProgressBar.setVisibility(View.GONE);
    	currentProInfo.setText("");
//    	btnCarExam.setEnabled(true);
    	btnCarExam.setText(R.string.car_exam);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_normal);
    	if(bundle.getInt("DIALOG_STYLE") != m_now_diag) //���ȵ�ʱ����Ҫ�����µ�dialog
    	{
    		m_now_diag = bundle.getInt("DIALOG_STYLE"); //��ֵ��ǰ����
    		Builder v_builder = new Builder(this)
    					.setTitle(bundle.getString("DIALOG_TITLE"))
    					.setMessage(bundle.getString("DIALOG_BODY"))
    					.setCancelable(false);
    		switch(m_now_diag)
    		{
    		case DiagnoseSimpleReportDataService.DIALOG_STYLE_OK:
    			v_builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						simple_report_service.setOrGetMode(new byte[] { 0x06 });
					}
				});
    			break;
    		case DiagnoseSimpleReportDataService.DIALOG_STYLE_OKCANCEL:
    			v_builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						setControlAtt();
						simple_report_service.setOrGetMode(new byte[] { 0x06 });
					}
				})
				.setNegativeButton(R.string.dialog_cancle, null);
    			break;
    		case DiagnoseSimpleReportDataService.DIALOG_STYLE__YESNO:
    			v_builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						setControlAtt();
						simple_report_service.setOrGetMode(new byte[] { 0x06 });
					}
				})
				.setNegativeButton(R.string.dialog_cancle, null);
    			break;
    		case DiagnoseSimpleReportDataService.DIALOG_STYLE__RETRYCANCEL:
    			v_builder.setPositiveButton(R.string.dialog_retry, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						setControlAtt();
						simple_report_service.setOrGetMode(new byte[] { 0x06 });
					}
				})
				.setNegativeButton(R.string.dialog_cancle, null);
    			break;
    		case DiagnoseSimpleReportDataService.DIALOG_STYLE__NOBUTTON:
    			break;
    		case DiagnoseSimpleReportDataService.DIALOG_STYLE__OKPRINT:
    			v_builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						setControlAtt();
//						simple_report_service.setOrGetMode(new byte[] { 0x06 });
					}
				})
			.setNegativeButton(R.string.dialog_cancle, null);
    			break;
    		default:
    			break;
    		}
    		if(showDiag != null)
    		{
    			showDiag.dismiss();
    			showDiag = v_builder.create();
    			showDiag.show();
    		}
    		else
    		{
    			showDiag = v_builder.create();
    			showDiag.show();
    		}
    	}
    	else  //��ȵ�ʱ��ֻ��Ҫˢ��dialog
    	{
    		showDiag.setMessage(bundle.getString("DIALOG_BODY"));
    		showDiag.show();
    	}
    }
	//����ϴ�����
	public void clearPreRecord(){
		bindQuestionList(null);
		bindSimpleDataList(null);
		questionNum.setText("0");
		currentProInfo.setText("");
	 	spProgress.setProgress(0);   
		circleProgressBar.setVisibility(View.GONE);
		scanShowInfoLayout.setGravity(Gravity.LEFT);
//		btnCarExam.setEnabled(false);
		btnCarExam.setText(R.string.diag_stop_car_exam);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_pressed);
		examNumTextInfo.setText(R.string.diag_car_cur_check_num);
		examNumText.setText("100");
	}
	public void setControlAtt(){
		circleProgressBar.setVisibility(View.VISIBLE);			
		currentProInfo.setText(R.string.diag_scan_sys_list);
//	   	btnCarExam.setEnabled(false);
		btnCarExam.setText(R.string.diag_stop_car_exam);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_pressed);
	}
	//������ʾ�������б��߳�
	public void showDocListPop(){
		popThread=new Thread()
        {
            @Override
        	public void run()
            {
              try {
				this.sleep(1000);
				carExamHandler.obtainMessage(SHOW_QUESTION_LIST).sendToTarget();//��ʾ��������Ϣ		
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        };	
        popThread.start();
	}
	/**
	 * �����Ի���
	 * @param message
	 * @author liaochuanhai
	 */
	protected void dialog(String message) {
		final CustomDialog customDialog=new CustomDialog(this);
		customDialog.setMessage(message); // ������Ϣ
		customDialog.setTitle(resources.getString(R.string.diag_sp_sys_info_show));
		customDialog.setPositiveButton(resources.getString(R.string.dialog_ok),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						customDialog.dismiss();
					}
				});
		customDialog.show();
	}
    /*
     * ��ʱ��
     */
    class TimerThread extends Thread
    {
    	
        @Override
        public void run()
        {
            try
            {
                this.sleep(10 * 1000);
                enableClick = true;
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

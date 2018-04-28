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
    Context context = DiagnoseSimpleReportActivity.this; //Context 上下文索引
	private View viewQuestionInfo;
	private PopupWindow pop_questionInfo; //弹出故障码详细窗口
	private Button btnCarExam; //车辆体验按钮
	private ImageButton btnShowDatail;
	private ProgressBar spProgress;  //显示进度条
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
	private static int diaLogErrorType=8;////赋值当前对话框错误类型
	private static Integer ITEM_NUM;
	Map<Integer,LinearLayout>  viewMap;
	private ProgressBar circleProgressBar;
	//故障码列表
	 ListView categoryList=null;
	//故障码列表适配器
    private DiagnoseQuestionCategoryAdapter mCategoryAdapter; 
    private List<HashMap<String,List<DiagnoseQuestionCategory>>> questionList = new ArrayList<HashMap<String,List<DiagnoseQuestionCategory>>>(); // 故障详细信息
    //初始化蓝牙服务
	private BluetoothDataService m_blue_service = null;
	//简易报告服务
	private DiagnoseSimpleReportDataService simple_report_service = null;
	//获得字符串信息
	private DiagnoseShowInfoStr showInfoStr; 
    //通讯步骤
    private int m_step = 0; 
    //加载蓝牙连接进度条
	private ProgressDialog progressDialog;
	//弹出对话框显示
	private AlertDialog showDiag;
	//判断是否按返回键
	private boolean backState=false;
	//条件锁
	ConditionVariable next = new ConditionVariable(false);
	//当前对话框样式
    private int m_now_diag = 0;
    private String  m_select_car_name;
    private String  m_select_car_version;
    private String m_ggppath;
    //启动故障码弹出框线程
    private Thread popThread;
    //定时线程
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
		//引用蓝牙服务
		m_blue_service = BluetoothDataService.getInstance();
		//添加观察者
		m_blue_service.AddObserver(this);
		
		//获取字符串信息
		showInfoStr = DiagnoseShowInfoStr.getInstance();
		showInfoStr.setContext(context);
		
		//简易报告服务
		simple_report_service = DiagnoseSimpleReportDataService.getInstance();
		simple_report_service.setContext(context);
		simple_report_service.setHandler(carExamHandler);
		simple_report_service.setShowInfoStr(showInfoStr);
		simple_report_service.setActivity(this);		
//		simple_report_service.InitialGGPInstance();

		//弹出故障码详细列表
		viewQuestionInfo =this.getLayoutInflater().inflate(
				R.layout.diagnose_simple_report_question, null);	
		pop_questionInfo = new PopupWindow(viewQuestionInfo,Env.getScreenWidth(DiagnoseSimpleReportActivity.this),
				400);		
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(this.getString(R.string.diag_sp_activity_init));
		categoryList =(ListView) viewQuestionInfo.findViewById(R.id.simple_report_question_list_view);   		
		//显示历史或者当前诊断
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
	 * 故障列表的高度
	 * */
	private int geQuestionInfoHeight() {
		LinearLayout test_info = (LinearLayout) findViewById(R.id.simple_repor_info_test);
		RelativeLayout question_info = (RelativeLayout) findViewById(R.id.question_num_show);
		return Env.getScreenHeight(this)
		// 屏幕高度,输入区域高度,菜单高度
				- question_info.getHeight() - test_info.getHeight()
				// 标题高度
				- R.dimen.window_title_size;
	}	
	 /**
     * 显示与隐藏列表信息
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
	 * 显示故障信息
	 */
	private void showQuestionInfo() {
		pop_questionInfo.showAsDropDown(findViewById(R.id.question_num_show));
	}

	/**
	 * 隐藏故障信息
	 */
	private void hideQuestionInfo() {
	//liaochuanhai
//		pop_questionInfo.dismiss();
	}
	
	/**
	 *  故障码列表数据绑定
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
	 * 简易报告绑定详细列表数据
	 * */
	public void bindSimpleDataList(List<HashMap<String, String>> list){
		// 绑定listview容器
		detailList.removeAllViewsInLayout();//清空列表数据
		simpleReportList = list;
		if(simpleReportList!=null){
			// 生成适配器，数组===》ListItem
			adapter =new DiagnoseSimpleDataInfoAdapter(this,simpleReportList);
	
			// 添加并且显示
			detailList.setAdapter(adapter);
		}
		else{
			detailList.setAdapter(null);
		}
	}

    //更新UI线程
    private final static int SIMPLE_REPORT_STEP_1 = 101;  //刷新进度条
    private final static int SIMPLE_REPORT_STEP_2 = 102;  //刷新进度条
    private final static int SIMPLE_REPORT_STEP_3 = 103;  //刷新进度条信息，显示列表
    private final static int SIMPLE_REPORT_STEP_4 = 104;  //故障码信息
    private final static int MSG_SHOW_ERROR_WINDOW = 105; //显示错误信息对话框
    private final static int SIMPLE_REPORT_STEP_6 = 106; //更新button文本信息
    private final static int SIMPLE_REPORT_STEP_7 = 107; //设置button状态
    private final static int SIMPLE_REPORT_STEP_8 = 108; //显示版本不一致提示
    private final static int SIMPLE_REPORT_STEP_9 = 109; //清除列表
	private final static int MSG_SHOW_UPDATE_DIAGLOG = 112;	//显示进入升级中心提示框。
	private final static int DT_CMD_MODE = 1; // 设置模式迟时
	private final static int DT_APPOINT_DATA_LIST = 2; // 读取指定系统数据流列表迟时
	private final static int DT_APPOINT_ID_LIST = 3; // 读取指定ID数据流迟时
	private final static int DT_APPOINT_DOC_LIST = 4; //读取指定系统故障码列表迟时
	private final static int SHOW_QUESTION_LIST = 5; //显示与隐藏故障码列表信息
	private final static int BLUE_TOOTH_LOST = 6; //显示与隐藏故障码列表信息
    private Handler carExamHandler = new Handler() {   
        public void handleMessage(Message msg) {   
    		switch(msg.what)
    		{
    		case SIMPLE_REPORT_STEP_1:
    			//刷新进度条    			
				spProgress.setProgress(msg.arg1);    
				circleProgressBar.setVisibility(View.VISIBLE);
//				currentProInfo.setText(R.string.diag_scan_sys_data_stream);
				currentProInfo.setText(R.string.diag_scan_sys_question_code);
				List<DiagnoseQuestionCategory> idTextList=(List<DiagnoseQuestionCategory>)msg.obj;
//				saveSysIDText(idTextList);
    			break;
    		case SIMPLE_REPORT_STEP_2:
    			//刷新进度条
    			circleProgressBar.setVisibility(View.VISIBLE);
    			spProgress.setProgress(msg.arg1);     
//	        	examNumText.setText(String.valueOf(msg.arg1));
    			break;
    		case SIMPLE_REPORT_STEP_3: 
    			//刷新进度条 刷新数据流列表
    			List<HashMap<String, String>> list=(List<HashMap<String, String>>)msg.obj;
    			bindSimpleDataList(list);//刷新列表    			
    			spProgress.setProgress(msg.arg1);   
    			circleProgressBar.setVisibility(View.VISIBLE);
    			currentProInfo.setText(R.string.diag_scan_sys_question_code);
    			break;
    		case SIMPLE_REPORT_STEP_4:
    			//刷新进度条 刷新故障码列表
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
    			//显示错误信息对话框
    			ShowErrorWindow(msg.arg1);
    			break;
    		case SIMPLE_REPORT_STEP_6:
    			//读取指定ID数据流
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
    			//设置模式迟时
    			simple_report_service.EnterPassword();		
    			break;
    		case DT_APPOINT_DATA_LIST:
    			//读取指定系统数据流列表迟时
    			byte[] param=(byte[])msg.obj;
    			simple_report_service.SendSampleReportCmd(3, param);
    			break;
    		case DT_APPOINT_ID_LIST:
    			//读取指定ID数据流迟时
    			byte[] param2=(byte[])msg.obj;
    			simple_report_service.SendSampleReportCmd(6, param2);
    			break;
    		case DT_APPOINT_DOC_LIST:
    			//读取指定系统故障码列表迟时
    			byte[] param3=(byte[])msg.obj;
    			simple_report_service.SendSampleReportCmd(4, param3);
    			break;	 
    		case SHOW_QUESTION_LIST:
    			//显示与隐藏故障码列表信息
    			 btnShowDatail.setImageResource(R.drawable.diagnose_simple_report_up);
        		 pop_questionInfo.showAsDropDown(findViewById(R.id.question_num_show));
        		 questionListDatail.setVisibility(View.GONE);        		
        		 isShowSimpleReportDetail = false;
        		 if(popThread!=null){
        			 popThread=null;
        		 }
    			break;	
    			//下面是显示对话框文本
    		case DiagnoseDataService.CMD_SHOW_GETDIALOG:
    			ShowDialog((Bundle)msg.obj);
    			break;
    		case BLUE_TOOTH_LOST:
    			//显示与隐藏故障码列表信息
    			int connectType= msg.arg2;
    			//蓝牙连接成功关闭提示框,否则弹出提示框
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
				//打开简易服务
				String carInfo=((String)btnCarExam.getText()).trim();
				if(carInfo.equals(resources.getString(R.string.car_exam))){
					openSimpleReportDataService();	
				}
				else{
					this.clearPreRecord();
					btnCarExam.setText(R.string.car_exam);
//					enableClick = false;
					simple_report_service.setResetGetMode();// 进入复位模式
//					timerThread =new TimerThread();
//					timerThread.start();
					
				}
			}
			else{
				dialog(resources.getString(R.string.diag_sp_timeing_info));
			}
			break; 
		case R.id.sp_show_datail:
	    	// 显示与隐藏列表信息
//			showQuestonList();
			break;
		default:
			break;
		}
	}
	 //显示错误信息对话框，0为超时错误，大于0为具体错误ID
    private void ShowErrorWindow(int error)
    {
    	int v_err_id = showInfoStr.GetDiagErrorID(error);//simple_report_service.GetDiagErrorID(error);
    	if(D) Log.e(TAG,"错误ID：" + v_err_id);
    	//关闭进度条
    	spProgress.setProgress(0);   
    	circleProgressBar.setVisibility(View.GONE);
    	currentProInfo.setText("");
//    	btnCarExam.setEnabled(true);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_normal);
    	btnCarExam.setText(R.string.car_exam);
		m_now_diag =diaLogErrorType; //赋值当前对话框错误类型
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
    	if(D) Log.e(TAG,"错误ID：" + v_err_id);
    	//关闭进度条
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
	 * 容器点击事件
	 * 
	 * @param v
	 */
	// TODO
	public void MenuButton_ClickHandler(View v) {
		switch (v.getId()) {
		case R.id.question_num_show:
			// 显示与隐藏列表信息
			//liaochuanhai
//			showQuestonList();
			break;
		default:
			break;
		}
	}
	
	
	 //开启简易报告蓝牙服务
	public void openSimpleReportDataService(){
    	//检测SD卡是否可以

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
    		simple_report_service.setOrGetMode(new byte[] { 0x06 });// 进入简单诊断模式
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

	//销毁服务
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
	//收到数据通知<命令字> + <数据>
	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		String v_show = BluetoothDataService.bytesToHexString(databuf,datalen);
//		if(D)
			Log.i(TAG,"SHOW：" + v_show);
    	simple_report_service.GetDataFromService(databuf, datalen);
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"接收超时，step=");
		carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,0,0).sendToTarget();
	}

	/********************* 界面无关 ***********************/

	// 按下返回按钮事件
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
					
					simple_report_service.setResetGetMode();// 进入复位模式
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
		if(D) Log.i(TAG,"SHOW：关闭蓝牙" );
		
	}
	//保存系统ID文本内容
	public void saveSysIDText(List<DiagnoseQuestionCategory> list){
		SharedPreferences sysIdTextInfo=getSharedPreferences(DiagnoseConstant.SYS_ID_TEXT_PREFS, Context.MODE_WORLD_WRITEABLE);
		sysIdTextInfo.edit().clear().commit();
		for(DiagnoseQuestionCategory info:list){
			sysIdTextInfo.edit().putString(info.getCategoryParentId(),info.getCategoryParentTextID()).commit();
		}
		
	}
	 //保存故障码ID字符串
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
	//显示历史或者当前诊断
	public void ShowHistoryRecordOrQuickDiag(){
		Intent intent = getIntent();
		if (intent.hasExtra(DiagnoseConstant.DIAG_SP_PUSH_KEY)) {
			if (intent.getStringExtra(DiagnoseConstant.DIAG_SP_PUSH_KEY)
					.equals(DiagnoseConstant.DIAG_SP_PUSH_VALUE)) {
				//显示上次体检分数
				SharedPreferences preExamNum=getSharedPreferences(DiagnoseConstant.PRE_EXAM_NUM_PREFS, Context.MODE_WORLD_WRITEABLE);
				SharedPreferences preQuestionInfo=getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);
				if(preExamNum.contains(DiagnoseConstant.PRE_EXAM_NUM)){				
					String currentLanguage =preQuestionInfo.getString(DiagnoseConstant.CURRENT_LANGUAGE, "");
					if(Env.GetCurrentLanguage().equals(currentLanguage)){
					  examNumText.setText(preExamNum.getString(DiagnoseConstant.PRE_EXAM_NUM, ""));
					}
				}
				//显示故障码信息
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
								carExamHandler.obtainMessage(SHOW_QUESTION_LIST).sendToTarget();//显示故障码信息		
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

    //显示对话框
    private void ShowDialog(Bundle bundle)
    {
		if(D) Log.i(TAG,"收到显示对话框");
		spProgress.setProgress(0);   
    	circleProgressBar.setVisibility(View.GONE);
    	currentProInfo.setText("");
//    	btnCarExam.setEnabled(true);
    	btnCarExam.setText(R.string.car_exam);
//		btnCarExam.setBackgroundResource(R.drawable.main_button_normal);
    	if(bundle.getInt("DIALOG_STYLE") != m_now_diag) //不等的时候需要创建新的dialog
    	{
    		m_now_diag = bundle.getInt("DIALOG_STYLE"); //赋值当前类型
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
    	else  //相等的时候只需要刷新dialog
    	{
    		showDiag.setMessage(bundle.getString("DIALOG_BODY"));
    		showDiag.show();
    	}
    }
	//清空上次数据
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
	//启动显示故障码列表线程
	public void showDocListPop(){
		popThread=new Thread()
        {
            @Override
        	public void run()
            {
              try {
				this.sleep(1000);
				carExamHandler.obtainMessage(SHOW_QUESTION_LIST).sendToTarget();//显示故障码信息		
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        };	
        popThread.start();
	}
	/**
	 * 弹出对话框
	 * @param message
	 * @author liaochuanhai
	 */
	protected void dialog(String message) {
		final CustomDialog customDialog=new CustomDialog(this);
		customDialog.setMessage(message); // 弹出信息
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
     * 定时线
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

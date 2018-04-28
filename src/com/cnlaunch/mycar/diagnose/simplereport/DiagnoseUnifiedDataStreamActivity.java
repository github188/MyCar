package com.cnlaunch.mycar.diagnose.simplereport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
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
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseConstant;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;
import com.cnlaunch.mycar.diagnose.service.DiagnoseUnifiedDataStreamService;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseQuestionCategory;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseQuestionCategoryAdapter;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseSimpleDataInfoAdapter;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;

public class DiagnoseUnifiedDataStreamActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "SimpleReportActivity";
    private static final boolean D = true;
    Context context = DiagnoseUnifiedDataStreamActivity.this; //Context ����������
	private Button btnCarExam; //�������鰴ť

    private List<HashMap<String,List<DiagnoseQuestionCategory>>> questionList = new ArrayList<HashMap<String,List<DiagnoseQuestionCategory>>>(); // ������ϸ��Ϣ
    //��ʼ����������
	private BluetoothDataService m_blue_service = null;
	//ͳһ�������������
	private DiagnoseUnifiedDataStreamService simple_report_service = null;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.diagnose_simple_report, R.layout.custom_title);
		setCustomeTitleLeft(R.string.simple_report);
		setCustomeTitleRight("");
		findView();
		//������������
		m_blue_service = BluetoothDataService.getInstance();
		//����ͳһ����������
		simple_report_service = DiagnoseUnifiedDataStreamService.getInstance();
	}
	private void findView() {
		btnCarExam=(Button) findViewById(R.id.sp_car_exam);
		btnCarExam.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sp_car_exam:
			//����ͳһ�������������
			openSimpleReportDataService();
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
    	else if(m_blue_service.IsConnected() == false){ //����������
    		m_blue_service.ShowBluetoothConnectActivity(this);
    	}
    	else{   
    		//���ص�ǰͳһ��������ǰֵ
    		String str=simple_report_service.getCarMileageCurrentValue("030B");
    		if(D) Log.i(TAG,"����ֵ"+str);
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
		super.onDestroy();
	}

	/********************* �����޹� ***********************/

	// ���·��ذ�ť�¼�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			simple_report_service.setResetGetMode();// ���븴λ
			finish();
			return true;
		}
		return false;
	}
}

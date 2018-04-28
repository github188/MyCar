package com.cnlaunch.mycar.diagnose.domain;

import launch.SearchIdUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;

public class DiagnoseBaseActivity extends Activity {
	Context context = DiagnoseBaseActivity.this;
	private  static DiagnoseBaseActivity lastActivity;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// 如果是在诊断主界面点的返回键，则不需要请求断开连接
			final DiagAlertDialog dlg = new DiagAlertDialog(this);
			dlg.setTitle(R.string.diagnose_exit_title);
			dlg.setMessage(R.string.diagnose_exit_messge1);
			dlg.setPositiveButton(R.string.dialog_yes, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				    releaseSource();
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
			
			break;

		}
		return super.onKeyDown(keyCode, event);

	}
	public void releaseSource()
	{
	       //初始化蓝牙服务
        BluetoothDataService m_blue_service = null;
        DiagnoseDataService m_diag_service = null;
        //引用蓝牙服务
        m_blue_service = BluetoothDataService.getInstance();
        m_blue_service.ResetTimeOut();
        m_diag_service = DiagnoseDataService.getInstance();
        if(m_blue_service.IsConnected())
            m_diag_service.SendDpuReset();
        lastActivity.finish();
        //释放ggp库
        SearchIdUtils search = SearchIdUtils.SearchIdInstance(null);
        if(search != null)
            search.CloseFile();
	}
/*
	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		this.finish();
		Log.e("test",this.toString());
		super.startActivity(intent);
		Log.e("test","startActivity");
	}
*/
	public void setContentView(int layoutResID, int layoutTitleResID) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		getWindow()
				.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutTitleResID);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(lastActivity != null)
		{
			Env.releaseWakeLock();
			lastActivity.finish();
		}
		lastActivity = this;
		Env.acquireWakeLock(this);
		//Log.e("test","--------->"+this.toString());
	}

}

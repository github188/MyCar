package com.cnlaunch.mycar.updatecenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
import com.cnlaunch.mycar.usercenter.LoginActivity;
/**
 * 设备激活用户指南, 
 * */
public class DeviceActivateGuideActivity extends BaseActivity
    implements OnClickListener
{
	private final static String TAG = "DeviceActivateGuide";
	private boolean D = true;
	Button btGoback;
	Button btNextStep;
	CheckBox cbNotShowAgain;
	SharedPreferences shpref;
	boolean isFront = false;
	/**
	 * -1 未指定升级方式
	 * 0 : 在线升级
	 * 1   ：本地升级
	 * **/
	int updateMethod = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_activation,R.layout.custom_title);
		setCustomeTitleLeft(R.string.upc_device_activate);
		setCustomeTitleRight("");
		
		Bundle data = getIntent().getExtras();
		if(data!= null)
		{
			updateMethod = data.getInt("update_method",UpdateCenterConstants.UPDATE_METHOD_LOCAL);
			Log.d(TAG,updateMethod == 0 ? "在线升级" : "本地升级");
		}
		
		shpref = getSharedPreferences(UpdateCenterConstants.SHARE_PREF_UPDATE_SETTINGS,0);
		initViews();
	}
	
	@Override
	protected void onResume()
	{
	    // TODO Auto-generated method stub
	    super.onResume();
	    isFront = true;
	}
	@Override
	protected void onPause()
	{
	    // TODO Auto-generated method stub
	    super.onPause();
	    isFront = false;
	}
	private void initViews()
	{
		btGoback = (Button) findViewById(R.id.bt_dev_activate_go_back);
		btGoback.setOnClickListener(this);
		
		btNextStep = (Button) findViewById(R.id.bt_dev_activate_next);
		btNextStep.setOnClickListener(this);
		
		cbNotShowAgain = (CheckBox) findViewById(R.id.cb_device_activate_show_tips);
		cbNotShowAgain.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked)// 下次不必显示
				{
					if(D)Log.d(TAG,"not show tips");
					shpref.edit().putBoolean(UpdateCenterConstants.DEVICE_ACTIVATE_GUIDE_NOT_SHOW_AGAIN, true).commit();
				}
				else
				{
					if(D)Log.d(TAG," show tips");
					shpref.edit().putBoolean("not_show_again", false).commit();
				}
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onClick(View v)
	{
		
		switch (v.getId())
		{
			case R.id.bt_dev_activate_next:
				goNext();
				break;
			case R.id.bt_dev_activate_go_back:
				finish();
				break;
			default:
				break;
		}
	}
	
	private void goNext()
	{
	   if (MyCarActivity.isLogin)
	   {
	       startActivity(new Intent(this,DeviceRegisterActivity.class));
	       finish();
	   }
	   else
	   {
           startActivity(new Intent(this,LoginActivity.class));
	   }
	}
	

}

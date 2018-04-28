package com.cnlaunch.mycar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.usercenter.LoginActivity;
import com.cnlaunch.mycar.usercenter.UsercenterConstants;

/**
 * @description 
 * @author ��Զï
 * @date��2012-4-6
 */
public class DisplayLegalTermsActivity extends Activity {
	// ����log��Ϣtarget
	private static final String TAG = "DisplayLegalTermsActivity";
	private static final boolean D = true;
	
	private SharedPreferences sp; // ȡ��ϵͳSharedPreferences
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ȥ�����⣬ȫ����ʾ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_legal_terms);
		// ȡ�ô洢��
		sp = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);
		// ͬ�ⰴť
		Button btnAgree = (Button)findViewById(R.id.btn_agree);
		
		// ��ӵ���¼�����,���ͬ������������ʾ��ͼ
		btnAgree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// �Ƿ�ͬ�ⷨ�������
				sp.edit().putBoolean(Constants.IS_AGREE_LEGAL_TERMS, true).commit(); 
				// ��ʾ����ʾ��ͼ
				displayDBSCarManual();
			}
		});
		
		// ��ͬ�ⰴť
		Button btnNotAgree = (Button) findViewById(R.id.btn_not_agree);
		
		// ��ӵ���¼������������ͬ�⣬�˳���ϵͳ
		btnNotAgree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �Ƿ�ͬ�ⷨ�������
				sp.edit().putBoolean(Constants.IS_AGREE_LEGAL_TERMS, false).commit(); 
				finish();
			}
		});
	}
	
	/**
	 * ��ʾ�����ֲ�
	 */
	private void displayDBSCarManual() {
//		Intent intent = new Intent(this, DisplayDBSCarManualActivity.class);
//		startActivity(intent);
//		DisplayLegalTermsActivity.this.finish();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("forward", 1); // ��¼�ɹ���ת��������
        startActivity(intent);
        DisplayLegalTermsActivity.this.finish();
	}
}




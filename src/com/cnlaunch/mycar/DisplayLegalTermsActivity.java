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
 * @author 向远茂
 * @date：2012-4-6
 */
public class DisplayLegalTermsActivity extends Activity {
	// 调试log信息target
	private static final String TAG = "DisplayLegalTermsActivity";
	private static final boolean D = true;
	
	private SharedPreferences sp; // 取得系统SharedPreferences
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 去掉标题，全屏显示启动动画
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_legal_terms);
		// 取得存储器
		sp = getSharedPreferences(UsercenterConstants.MYCAR_SHARED_PREFERENCES,
				Context.MODE_WORLD_WRITEABLE);
		// 同意按钮
		Button btnAgree = (Button)findViewById(R.id.btn_agree);
		
		// 添加点击事件监听,如果同意加载软件介绍示意图
		btnAgree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// 是否同意法律条款：是
				sp.edit().putBoolean(Constants.IS_AGREE_LEGAL_TERMS, true).commit(); 
				// 显示操作示意图
				displayDBSCarManual();
			}
		});
		
		// 不同意按钮
		Button btnNotAgree = (Button) findViewById(R.id.btn_not_agree);
		
		// 添加点击事件监听，如果不同意，退出本系统
		btnNotAgree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 是否同意法律条款：否
				sp.edit().putBoolean(Constants.IS_AGREE_LEGAL_TERMS, false).commit(); 
				finish();
			}
		});
	}
	
	/**
	 * 显示操作手册
	 */
	private void displayDBSCarManual() {
//		Intent intent = new Intent(this, DisplayDBSCarManualActivity.class);
//		startActivity(intent);
//		DisplayLegalTermsActivity.this.finish();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("forward", 1); // 登录成功跳转到主界面
        startActivity(intent);
        DisplayLegalTermsActivity.this.finish();
	}
}




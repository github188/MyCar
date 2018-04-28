package com.cnlaunch.mycar.common;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.MyCarConfig;
import com.cnlaunch.mycar.common.database.UserDbHelper;

public class BaseActivity extends MyCarOrmLiteBaseActivity<UserDbHelper> {

	public UserDbHelper getHelper() {
		UserDbHelper helper = super.getHelper();
		String path = helper.getReadableDatabase().getPath().toString();
		String dbName = new File(path).getName();
		if (MyCarConfig.currentCCToDbName!=null && !MyCarConfig.currentCCToDbName.equals(dbName)) {
			helper.close();
			UserDbHelper newHelper = new UserDbHelper(this);
			setHelper(newHelper);
			return newHelper;
		}else{
			return helper;
		}
	}

	private Toast toast;

	public void setContentView(int layoutResID, int layoutTitleResID) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		getWindow()
				.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, layoutTitleResID);
	}

	protected void setCustomeTitleLeft(int resourceId) {
		TextView textView = (TextView) findViewById(R.id.title_left_text);
		if (textView != null) {
			textView.setText(getText(resourceId));
		}
	}

	protected void setCustomeTitleRight(int resourceId) {
		TextView textView = (TextView) findViewById(R.id.title_right_text);
		if (textView != null) {
			textView.setText(getText(resourceId));
		}
	}

	protected void setCustomeTitleLeft(CharSequence charStr) {
		TextView textView = (TextView) findViewById(R.id.title_left_text);
		if (textView != null) {
			textView.setText(charStr);
		}
	}

	protected void setCustomeTitleRight(CharSequence charStr) {
		TextView textView = (TextView) findViewById(R.id.title_right_text);
		if (textView != null) {
			textView.setText(charStr);
		}
	}

	// 显示提示信息
	protected void displayToast(String msg) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	// 显示提示信息
	protected void displayToast(int resId) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	protected void backMain(Context context){
		Intent intent = new Intent(context,MyCarActivity.class);
		intent.putExtra("AnimationFlag", "NO");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);		
	}
}

package com.cnlaunch.mycar;

import com.cnlaunch.mycar.diagnose.loveCarHealth.LoveCarService;
import com.cnlaunch.mycar.diagnose.loveCarHealth.MessageActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
/**
 * 
 * @author huangweiyong
 *
 */
public class MyCarSettingActivity extends PreferenceActivity {
	// debugging
	private final static String TAG = "MyCarSettingActivity";
	/*宣告控件*/
	private final boolean FLAG = true;
	//一直开着    all_along_open_key   CheckBoxPreference
	private CheckBoxPreference all_along_open_key = null;
	//设置爱车体检时间范围     key=setting_time_screen
	private PreferenceScreen settingTimePreferenceScreen = null;
	//设置爱车体检一栏标题的颜色
	private PreferenceCategory titleCategory = null;
	private PreferenceScreen setting_show_screen;
	//输入蓝牙名称
	private EditTextPreference blueToothNamePreference = null;
	private PreferenceScreen setting_about_screen = null;
	private Intent intentService = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//设置布局
		addPreferencesFromResource(R.xml.mycar_setting);
		//设置背景颜色
		this.getListView().setCacheColorHint(android.graphics.Color.TRANSPARENT);
		
		intentService = new Intent(MyCarSettingActivity.this, LoveCarService.class);
		//查找控件
		settingTimePreferenceScreen = (PreferenceScreen) findPreference("setting_time_screen");
		setting_show_screen = (PreferenceScreen)findPreference("setting_show_screen");
		titleCategory = (PreferenceCategory) findPreference("title_key");
		all_along_open_key = (CheckBoxPreference)findPreference("all_along_open_key");
		blueToothNamePreference = (EditTextPreference)findPreference("edittext_key");
		setting_about_screen = (PreferenceScreen)findPreference("setting_about_screen");
		
		if(all_along_open_key.isChecked()){
			settingTimePreferenceScreen.setEnabled(false);
		}else{
			settingTimePreferenceScreen.setEnabled(true);
		}
		//设置监听事件
		all_along_open_key.setOnPreferenceClickListener(new AllAlongOpenKeyListener());
		all_along_open_key.setOnPreferenceChangeListener(new AllAlongOpenKeyChangeListener());
		settingTimePreferenceScreen.setOnPreferenceChangeListener(new SettingTimePreferenceScreenChangeListener());
		setting_about_screen.setOnPreferenceClickListener(new SettingAboutScreenListener());
	}
	
	class AllAlongOpenKeyListener implements Preference.OnPreferenceClickListener {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	class AllAlongOpenKeyChangeListener implements Preference.OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(all_along_open_key.isChecked()){
				if(FLAG){
					Log.i("MyCarSettingActivity","all_along_open_key.isChecked()-------->true");
				}
				settingTimePreferenceScreen.setEnabled(true);
				stopService(intentService);
			}
			if(!all_along_open_key.isChecked()){
				if(FLAG){
					Log.i("MyCarSettingActivity","all_along_open_key.isChecked()-------->false");
				}
				settingTimePreferenceScreen.setEnabled(false);
				startService(intentService);
				if (FLAG) {
					Intent activityIntent = new Intent(MyCarSettingActivity.this, MessageActivity.class);
					activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(activityIntent);
				}
			}
			return true;
		}
	}
	
	class SettingTimePreferenceScreenChangeListener implements Preference.OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	class SettingAboutScreenListener implements Preference.OnPreferenceClickListener {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			if(setting_about_screen.isSelectable()){
				//startActivity(new Intent(MyCarSettingActivity.this,MyCarActivity.class));
			}
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	// debugging
	private final static String TAG = "SettingActivity";
	private final static boolean D = true;
	// 爱车体检打开或关闭  key=open_close_key
	//private CheckBoxPreference openOrCloseKeyPreference = null;
	//设置爱车体检时间范围     key=setting_time_screen
	//private PreferenceScreen settingTimePreferenceScreen = null;
	//一直开着 key=all_along_open_key
	private CheckBoxPreference allAlongOpenKeyPreference = null;
	//开始时间
	private TimePreferenceBegin beginTimePreference = null;
	//结束时间
	private TimePreferenceEnd endTimePreference = null;
	
	private Intent intentService = null;
	private Context context;
	
	private static long beginTime;
	private static long endTime;
	
	Calendar c = Calendar.getInstance();
	
	public static long getBeginTime() {
		return beginTime;
	}

	public static void setBeginTime(long beginTime) {
		MyCarSettingActivity.beginTime = beginTime;
	}

	public static long getEndTime() {
		return endTime;
	}

	public static void setEndTime(long endTime) {
		MyCarSettingActivity.endTime = endTime;
	}

	// 做初始化
	private void init() {
		context = MyCarSettingActivity.this;
		intentService = new Intent(context, MyService.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//设置布局
		addPreferencesFromResource(R.xml.mycar_setting);
		//初始化
		init();
		//根据key值找到控件
		//openOrCloseKeyPreference = (CheckBoxPreference) findPreference("open_close_key");
		//settingTimePreferenceScreen = (PreferenceScreen) findPreference("setting_time_screen");
		allAlongOpenKeyPreference = (CheckBoxPreference) findPreference("all_along_open_key");
		beginTimePreference = (TimePreferenceBegin) findPreference("time_test_begin");
		endTimePreference = (TimePreferenceEnd) findPreference("time_test_end");
		
//		if(!openOrCloseKeyPreference.isChecked()){
//			settingTimePreferenceScreen.setEnabled(false);
//		}
		if(allAlongOpenKeyPreference.isChecked()){
			beginTimePreference.setEnabled(false);
			endTimePreference.setEnabled(false);
		}else{
			beginTimePreference.setEnabled(true);
			endTimePreference.setEnabled(true);
		}
		
		//beginTimePreference.setPositiveButtonText("nihao");
		//设置监听事件
		//openOrCloseKeyPreference.setOnPreferenceClickListener(this);
		//settingTimePreferenceScreen.setOnPreferenceClickListener(this);
		allAlongOpenKeyPreference.setOnPreferenceClickListener(this);
		
	}

	private void operatePreference(Preference preference) {
			if (D) {
				Log.i(TAG, " open loveCar, and isCheckd ="
						+ allAlongOpenKeyPreference.isChecked());
			}
			if(allAlongOpenKeyPreference.isChecked()){
				beginTimePreference.setEnabled(false);
				endTimePreference.setEnabled(false);
				//开启爱车体检service
				startService(intentService);
				if (D) {
					Intent activityIntent = new Intent(context, MessageActivity.class);
					activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(activityIntent);
				}
			}
			if(!allAlongOpenKeyPreference.isChecked()) {
				//关闭爱车体检service
				stopService(intentService);
				if (D) {
					Toast.makeText(context, "服务关闭了", 1).show();
				}
				beginTimePreference.setEnabled(true);
				endTimePreference.setEnabled(true);
			}
			
			if(beginTimePreference.isSelectable()){
				//System.out.println("beginTimePreference.isSelectable()");
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				beginTime = sharedPreferences.getLong("time_test_begin", 10000);
				Date date = new Date(beginTime);
				//System.out.println(date.getHours() + ":" + date.getMinutes());
			}
			
	}

	//当Preference的值发生改变时触发该事件，true则以新值更新控件的状态，false则do noting
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (D) {
		}
		return true;
	}

	//点击事件触发
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (D) {
		}
		//对控件进行操作
		operatePreference(preference);
		return true;
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	*/
/*	
	//点击事件触发
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		
		Log.i("onPreferenceTreeClick","onPreferenceTreeClick");
		//operatePreference(preference);
		
		
		//if(settingTimePreferenceScreen.isSelectable()){
			if(allAlongOpenKeyPreference.isChecked()){
				beginTimePreference.setEnabled(false);
				endTimePreference.setEnabled(false);
				//开启爱车体检service
				startService(intentService);
				if (D) {
					Intent activityIntent = new Intent(context, MessageActivity.class);
					activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(activityIntent);
				}
			}
			if(!allAlongOpenKeyPreference.isChecked() ) {
				//关闭爱车体检service
				stopService(intentService);
				if (D) {
					Toast.makeText(context, "服务关闭了", 1).show();
				}
				beginTimePreference.setEnabled(true);
				endTimePreference.setEnabled(true);
			}
			
			//setting time
			if(beginTimePreference.isSelectable()){
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				beginTime = sharedPreferences.getLong("time_test_begin", 10000);
				endTime = sharedPreferences.getLong("time_test_end", 10000);
				
				Date date = new Date(beginTime);
                Log.i("EX06_10",date.getHours() + ":" + date.getMinutes());
                c.set(Calendar.HOUR_OF_DAY, date.getHours());
                c.set(Calendar.MINUTE, date.getMinutes());
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
				
				Intent intent = new Intent(MyCarSettingActivity.this,
	                    CallAlarm.class); 
	            PendingIntent sender = PendingIntent.getBroadcast(
	            		MyCarSettingActivity.this, 0, intent, 0); 
	            									 
	                                                  * AlarmManager.
	                                                  * RTC_WAKEUP设定服务在系统休眠时同样会执行
	                                                  * 以set()设定的PendingIntent只会执行一次
	                                                  * *
	                                                  
	                AlarmManager am;
	                am = (AlarmManager) getSystemService(ALARM_SERVICE);
	                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
	                    sender);
	                Date date1 = new Date(c.getTimeInMillis());
	                Toast.makeText(MyCarSettingActivity.this, "hahah-->" + date1.getHours() + ":" + date1.getMinutes(), 1).show();
			}
		//}
			
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
*/
	
}

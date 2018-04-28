package com.cnlaunch.mycar.diagnose.loveCarHealth;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
/**
 * 
 * @author huangweiyong
 *
 */
public class BeginTimeBroadcast extends BroadcastReceiver {

	private static String MYACTION = "begin.time.alarm.action";
	Calendar c = Calendar.getInstance();
	@Override
	public void onReceive(Context context, Intent mintent) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		long beginTime = sharedPreferences.getLong("time_test_begin", 10000);

		//if (Intent.ACTION_BOOT_COMPLETED.equals(mintent.getAction())) {
			// 启动完成
			Intent intent = new Intent(context, BeginTimeAlarmreceiver.class);
			intent.setAction(MYACTION);
			PendingIntent sender = PendingIntent.getBroadcast(context, 0,
					intent, 0);
			//SystemClock.sleep(beginTime);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			// 发送广播
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, beginTime,
					sender);
		//}
	}
}

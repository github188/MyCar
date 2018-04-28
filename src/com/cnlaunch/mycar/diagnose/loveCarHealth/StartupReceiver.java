package com.cnlaunch.mycar.diagnose.loveCarHealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
@Deprecated
public class StartupReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, LoveCarService.class);
		context.startService(serviceIntent);
		Intent activityIntent = new Intent(context, MessageActivity.class);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(activityIntent);
	}
}

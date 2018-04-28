package com.cnlaunch.mycar.diagnose.loveCarHealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EndTimeAlarmreceiver extends BroadcastReceiver {

	@Override  
    public void onReceive(Context context, Intent intent) {  
	
        if (intent.getAction().equals("end.time.alarm.action")) {  
            Intent i = new Intent();  
            i.setClass(context, LoveCarService.class);  
            // ����service    
            // ��ε���startService�������������service ���ǻ��ε���onStart   
            context.stopService(i);  
        }  
    }
}

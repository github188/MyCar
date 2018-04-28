package com.cnlaunch.mycar.diagnose.loveCarHealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * laji
 * @author huangweiyong
 *
 */
public class BeginTimeAlarmreceiver extends BroadcastReceiver {

	@Override  
    public void onReceive(Context context, Intent intent) {  
	
        if (intent.getAction().equals("begin.time.alarm.action")) {  
            Intent i = new Intent();  
            i.setClass(context, LoveCarService.class);  
            // ����service    
            // ��ε���startService�������������service ���ǻ��ε���onStart   
            context.startService(i);  
        }  
    }
}

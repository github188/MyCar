package com.cnlaunch.mycar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DBSCarSummaryBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "MyCarActivity";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String msg = intent.getStringExtra("message");
        Log.d(TAG, msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

package com.cnlaunch.mycar.updatecenter.tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cnlaunch.mycar.updatecenter.DiagSoftUpdateConfigParams;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;

/**
 * <功能简述> 接收来电通知
 * <功能详细描述>
 * @author xiangyuanmao
 * @version 1.0 2012-12-7
 * @since DBS V100
 */
public class CallReceiver extends BroadcastReceiver
{
    private boolean D = true;
    private final static String TAG = "CallReceiver";
    private static int lastetState = TelephonyManager.CALL_STATE_IDLE;
    private Context context;
    private static boolean incomingFlag = false;  

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        if(D) Log.d(TAG, "CallReceiver is start...");
//        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        CallListener customPhoneListener = new CallListener(context);
//        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.context = context;
        Bundle bundle = intent.getExtras();
        
        String incoming_number = bundle.getString("incoming_number");
        Log.d(TAG, "CallReceiver Phone Number :" + incoming_number);
        //如果是拨打电话   
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){                          
                incomingFlag = false;  
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);          
                Log.i(TAG, "call OUT:"+phoneNumber);                          
        }else{                          
                //如果是来电   
                TelephonyManager tm =   
                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
                  
                switch (tm.getCallState()) {  
                case TelephonyManager.CALL_STATE_RINGING:  
                        incomingFlag = true;//标识当前是来电   
                        incoming_number = intent.getStringExtra("incoming_number");  
                        Log.i(TAG, "RINGING :"+ incoming_number);  
                        break;  
                case TelephonyManager.CALL_STATE_OFFHOOK:                                  
                        if(incomingFlag){  
                                Log.i(TAG, "incoming ACCEPT :"+ incoming_number);  
                        }  
                        break;  
                  
                case TelephonyManager.CALL_STATE_IDLE:                                  
                        if(incomingFlag){  
                                Log.i(TAG, "incoming IDLE");       
                                DiagSoftUpdateConfigParams params = new DiagSoftUpdateConfigParams();
                                params.setSerialNumber("963890000167");
                                params.setVehiecle("FIAT");
                                params.setVersion("V10.01");
                                params.setLanguage("CN");
                                params.setFileAbsolutePath("/mnt/sdcard/cnlaunch");
                                params.setUpadteType(2);
                                Intent intent1 = new Intent(this.context,FirmwareUpdate.class);
                                intent1.putExtra("diagsoft_update_config_params", params);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                this.context.startActivity(intent1);
                        }  
                        break;  
                }   
        }  
    }
    

//    class CallListener extends PhoneStateListener
//    {
//        
//        private Context context;
//        
//        public CallListener(Context context)
//        {
//            super();
//            // TODO Auto-generated constructor stub
//            this.context = context;
//        }
//        
//        int i = 0;
//        @Override
//        public void onCallStateChanged(int state, String incomingNumber)
//        {
//            // TODO Auto-generated method stub
//            super.onCallStateChanged(state, incomingNumber);
//            FirmwareUpdate.cellState = state;
//            // 电话来电，响铃中状态
//            if (state == TelephonyManager.CALL_STATE_RINGING)
//            {
//                if (D) Log.d(TAG, "电话来电，响铃中状态...");
//            }
//            // 结束电话，空闲状态0 自己主动挂断或对方挂断都是0
//            else if (state == TelephonyManager.CALL_STATE_IDLE)
//            {
//                synchronized(CallListener.this)
//                {
//                    ++i;
//                    if (D) Log.d(TAG, "启动升级" + i + "次");
//                }
//                if (i == 1)
//                {
//
//
//                    
//                }
//                if (D) Log.d(TAG, "结束电话，空闲状态0 自己主动挂断或对方挂断都是0...");
//            }
//            // 通话中 忙音状态2
//            else if (state == TelephonyManager.CALL_STATE_OFFHOOK)
//            {
//                if (D) Log.d(TAG, "通话中 忙音状态2...");
//            }
//        }
//    }
}

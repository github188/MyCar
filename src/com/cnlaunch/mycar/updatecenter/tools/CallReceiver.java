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
 * <���ܼ���> ��������֪ͨ
 * <������ϸ����>
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
        //����ǲ���绰   
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){                          
                incomingFlag = false;  
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);          
                Log.i(TAG, "call OUT:"+phoneNumber);                          
        }else{                          
                //���������   
                TelephonyManager tm =   
                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
                  
                switch (tm.getCallState()) {  
                case TelephonyManager.CALL_STATE_RINGING:  
                        incomingFlag = true;//��ʶ��ǰ������   
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
//            // �绰���磬������״̬
//            if (state == TelephonyManager.CALL_STATE_RINGING)
//            {
//                if (D) Log.d(TAG, "�绰���磬������״̬...");
//            }
//            // �����绰������״̬0 �Լ������Ҷϻ�Է��Ҷ϶���0
//            else if (state == TelephonyManager.CALL_STATE_IDLE)
//            {
//                synchronized(CallListener.this)
//                {
//                    ++i;
//                    if (D) Log.d(TAG, "��������" + i + "��");
//                }
//                if (i == 1)
//                {
//
//
//                    
//                }
//                if (D) Log.d(TAG, "�����绰������״̬0 �Լ������Ҷϻ�Է��Ҷ϶���0...");
//            }
//            // ͨ���� æ��״̬2
//            else if (state == TelephonyManager.CALL_STATE_OFFHOOK)
//            {
//                if (D) Log.d(TAG, "ͨ���� æ��״̬2...");
//            }
//        }
//    }
}

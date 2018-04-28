package com.cnlaunch.mycar.updatecenter.step;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.device.ActionEvent;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest.OnDeviceTimeoutListener;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponse;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponseHandler;
import com.cnlaunch.mycar.updatecenter.device.DeviceUpdateListener;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;

public class CheckSerialNo implements DeviceResponseHandler.Listener,OnDeviceTimeoutListener
{

    String TAG = "CheckSerialNo";
    Context context;
    String serialNumber;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    DeviceRequest rq2103;
    HashMap<String,Object> Queue;
    public CheckSerialNo(Context context, 
        String serialNumber,
        Connection connection,
        DeviceUpdateListener listener,
        DeviceResponseHandler devRespHandler,
        HashMap<String,Object> Queue)
    {
         this.context = context;
         this.serialNumber = serialNumber;
         this.connection = connection;
         this.listener = listener;
         this.devRespHandler = devRespHandler;
         this.Queue = Queue;
         this.rq2103 = new DeviceRequest(context,connection, new byte[]{0x21,0x03},null,10,this);
         this.Queue.put(rq2103.getReqestId().toString(), rq2103);
         devRespHandler.addListener(this);
    }
    public void execute()
    {
        Log.e(TAG,"execute method is come in ...");
        @SuppressWarnings("unchecked")
        Object serialRet = rq2103.postAndWait();
        if ( serialRet == null )
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2103);
            return;
        }
        Log.e(TAG,"DUP序列号信息" + serialRet.toString());
        if(serialRet instanceof SerialInfo)
        {
            SerialInfo info = (SerialInfo)serialRet;
            String serialNumberLocal = info.getSerialNumber();
            String chipID = info.getChipId();
            if (!serialNumber.equals(serialNumberLocal))
            {
                Log.e(TAG,"序列号验证失败" + "Local SN is : " + serialNumberLocal + " User\'s SN is :" + serialNumber);
                notifyError(ActionEvent.ERROR_UPDATE_SERIALS_NOT_SEEM,rq2103);
                return;
            }
            Log.e(TAG,"序列号验证成功" + "Local SN is : " + serialNumberLocal + " User\'s SN is :" + serialNumber);
        }
        destroy();
    }
    

    @Override
    public void onDeviceResponse(DeviceResponse response)
    {
        DeviceRequest rq = (DeviceRequest)Queue.get(response.getId());
        if(rq == null)//
        {
            //connection.reOpenConn();
            notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY,rq);
            return;
        }
        rq.complete(response.getResult());
        
    }

    @Override
    public void onDeviceError(String request)
    {
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY,null);
        
    }
    public void notifyError(int err,DeviceRequest rq)
    {
        if(listener!=null)
        {
            listener.onDeviceUpdateException(err, rq);
        }
    }
    public void destroy()
    {
        this.devRespHandler.removeListener(this);
        this.Queue.remove(rq2103.getReqestId().toString());
    }
    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, deviceRequest);
        
    }
}

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
import com.cnlaunch.mycar.updatecenter.tools.DPUParamTools;

public class ConnectDPU implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    String TAG = "ConnectDUP";
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String,Object> Queue;
    DeviceRequest rq250202;
    DeviceRequest rq2503;
    DeviceRequest rq2401;
    DeviceRequest rq2402;
    DeviceRequest rq2403;
    DeviceRequest rq2404;
    DeviceRequest rq2405;
    DeviceRequest rq2407;
    DeviceRequest rq2408;
    public ConnectDPU(Context context, 
        Connection connection,
        DeviceUpdateListener listener,
        DeviceResponseHandler devRespHandler,
        HashMap<String,Object> Queue)
    {
         this.context = context;
         this.connection = connection;
         this.listener = listener;
         this.devRespHandler = devRespHandler;
         this.Queue = Queue;
         initRequest();
         devRespHandler.addListener(this);
    }
    public void execute()
    {
        
        // 通知客户端，升级开始
        notifyUpdateStart();
        Object ret = rq250202.postAndWait();// 请求连接，使用四字节安全校验
        if (ret==null)
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
            return;
        }
        else
        {
            Log.e(TAG,"250202 使用四字节安全校验连接成功");
            byte[] checksum = (byte[])ret;
            notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "正在连接设备");
            ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// 验证校验字
            if(ret==null)
            {
                notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                return;// 强行退出线程
            }
            Log.e(TAG,"2503 安全检验成功");
            destroy();
        }
    }
    /**
     * 通知升级过程的消息
     * @param action
     * @param msg
     */
    private void notifyActionMessages(int action,String msg)
    {
        if (listener!=null)
        {
            listener.onDeviceUpdateMessages(action, msg);
        }
    }
    public void notifyUpdateStart()
    {
        if (listener!=null)
        {
            listener.onDeviceUpdateStart();
        }
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
        this.Queue.remove(rq250202.getReqestId().toString());
    }
    
    private void initRequest()
    {
        rq250202 = new DeviceRequest(context,connection, new byte[]{0x25,0x02}, new byte[]{02},10,this);
        rq2503 = new DeviceRequest(context,connection, new byte[]{0x25,0x03}, null,10,this);
        rq2401 = new DeviceRequest(context,connection, new byte[]{0x24,0x01}, null,10,this);
        rq2402 = new DeviceRequest(context,connection, new byte[]{0x24,0x02}, null,15,this);
        rq2403 = new DeviceRequest(context,connection, new byte[]{0x24,0x03}, null,15,this);
        rq2404 = new DeviceRequest(context,connection, new byte[]{0x24,0x04}, null,15,this);
        rq2405 = new DeviceRequest(context,connection, new byte[]{0x24,0x05}, null,15,this);
        rq2407 = new DeviceRequest(context,connection, new byte[]{0x24,0x07}, null,10,this);
        rq2408 = new DeviceRequest(context,connection, new byte[]{0x24,0x08}, null,10,this);
        Queue.put(rq2401.getReqestId().toString(), rq2401);
        Queue.put(rq2402.getReqestId().toString(), rq2402);
        Queue.put(rq2403.getReqestId().toString(), rq2403);
        Queue.put(rq2404.getReqestId().toString(), rq2404);
        Queue.put(rq2405.getReqestId().toString(), rq2405);
        Queue.put(rq2407.getReqestId().toString(), rq2407);
        Queue.put(rq2408.getReqestId().toString(), rq2408);
        Queue.put(rq250202.getReqestId().toString(), rq250202);
        Queue.put(rq2503.getReqestId().toString(), rq2503);
    }
    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY,deviceRequest);
    }
}

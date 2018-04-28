package com.cnlaunch.mycar.updatecenter.step;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;

import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.device.ActionEvent;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest.OnDeviceTimeoutListener;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponse;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponseHandler;
import com.cnlaunch.mycar.updatecenter.device.DeviceUpdateListener;
import com.cnlaunch.mycar.updatecenter.device.ProgressInfo;

public class ValidateAllFilesMd5 implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    String TAG = "SendDataToDPU";
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;
    boolean filesAreComplete = false;// 文件是否完整
    HashMap<String, String> md5info;

    DeviceRequest rq2405; // 完成升级
    DeviceRequest rq2408; // 读取DPU接头车型文件信息

    public ValidateAllFilesMd5(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue, HashMap<String, String> md5info)
    {
        this.context = context;
        this.connection = connection;
        this.listener = listener;
        this.devRespHandler = devRespHandler;
        this.Queue = Queue;
        this.md5info = md5info;
        initRequest();
        devRespHandler.addListener(this);
    }

    public void execute()
    {
     
        // MD5 对比
        Map<String, String> md5InDevice = (HashMap<String, String>) rq2408.postAndWait();
        if (md5InDevice == null)
        {
            notifyError(ActionEvent.ERROR_DATA_INTEGRETY, rq2408);
            return;
        }
        Iterator<Map.Entry<String, String>> it = md5InDevice.entrySet().iterator();
        String md5Device;
        String md5Client;
        String fileName;
        while (it.hasNext())
        {
            Map.Entry<String, String> e = it.next();
            fileName = e.getKey();
            md5Device = e.getValue();
            md5Client = md5info.get(fileName);
            if (!md5Client.equals(md5Device))
            {
                notifyError(ActionEvent.ERROR_DATA_INTEGRETY, rq2408);
                return;
            }
        }
        // 升级成功
        Object ret = rq2405.postAndWait();
        if (ret == null)
        {
            notifyError(ActionEvent.ERROR_UPDATE_COMPLETE_INDICATION, rq2405);
            return;
        }
        destroy();
    }

    /**
     * 通知升级过程的消息
     * @param action
     * @param msg
     */
    private void notifyActionMessages(int action, String msg)
    {
        if (listener != null)
        {
            listener.onDeviceUpdateMessages(action, msg);
        }
    }

    public void notifyUpdateStart()
    {
        if (listener != null)
        {
            listener.onDeviceUpdateStart();
        }
    }

    @Override
    public void onDeviceResponse(DeviceResponse response)
    {
        DeviceRequest rq = (DeviceRequest) Queue.get(response.getId());
        if (rq == null)//
        {
            // connection.reOpenConn();
            notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, rq);
            return;
        }
        rq.complete(response.getResult());

    }

    @Override
    public void onDeviceError(String request)
    {
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, null);

    }

    public void notifyError(int err, DeviceRequest rq)
    {
        if (listener != null)
        {
            listener.onDeviceUpdateException(err, rq);
        }
    }

    public void destroy()
    {
        Queue.remove(rq2405);
        Queue.remove(rq2408);
        rq2405 = null;
        rq2408 = null;
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq2405 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x05 }, null, 15, this);
        rq2408 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x08 }, null, 10, this);
        Queue.put(rq2405.getReqestId().toString(), rq2405);
        Queue.put(rq2408.getReqestId().toString(), rq2408);
    }

    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, deviceRequest);
    }

    public void notifyUpdateProgress(int action, ProgressInfo progress)
    {
        if (listener != null)
        {
            listener.onUpdateProgress(action, progress);
        }
    }
}

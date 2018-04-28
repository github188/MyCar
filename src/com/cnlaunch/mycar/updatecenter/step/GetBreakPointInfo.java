package com.cnlaunch.mycar.updatecenter.step;

import java.io.File;
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
import com.cnlaunch.mycar.updatecenter.device.ProgressInfo;
import com.cnlaunch.mycar.updatecenter.model.BreakPointInfo;

public class GetBreakPointInfo implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    String TAG = "GetBreakPointInfo";
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;

    DeviceRequest rq2406;// 读取断点信息

    public GetBreakPointInfo(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue)
    {
        this.context = context;
        this.connection = connection;
        this.listener = listener;
        this.devRespHandler = devRespHandler;
        this.Queue = Queue;
        initRequest();
        devRespHandler.addListener(this);
    }

    public BreakPointInfo execute()
    {
        Object ret = rq2406.postAndWait(); // 文件的长度
        if (ret == null)
        {
            //notifyError(ActionEvent.ERROR_FILE_POSITION_OPERATION, rq2406);
            return null;
        }
        Log.d(TAG, "断点信息：" + ret.toString());
        destroy();
        return (BreakPointInfo)ret;
    
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
        Queue.remove(rq2406);
        rq2406 = null;
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq2406 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x06 }, null, 10, this);
        Queue.put(rq2406.getReqestId().toString(), rq2406);
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

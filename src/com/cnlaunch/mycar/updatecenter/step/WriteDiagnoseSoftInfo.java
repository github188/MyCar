package com.cnlaunch.mycar.updatecenter.step;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.device.ActionEvent;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest.OnDeviceTimeoutListener;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponse;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponseHandler;
import com.cnlaunch.mycar.updatecenter.device.DeviceUpdateListener;
import com.cnlaunch.mycar.updatecenter.device.ProgressInfo;
import com.cnlaunch.mycar.updatecenter.model.UpdateInfo;
import com.cnlaunch.mycar.updatecenter.tools.DPUParamTools;

public class WriteDiagnoseSoftInfo implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    String TAG = "WriteDiagnoseSoftInfo";
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;
    UpdateInfo updateInfo;
    DeviceRequest rq2401; // 写诊断软件相关信息

    public WriteDiagnoseSoftInfo(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue, UpdateInfo updateInfo)
    {
        this.context = context;
        this.connection = connection;
        this.listener = listener;
        this.devRespHandler = devRespHandler;
        this.Queue = Queue;
        this.updateInfo = updateInfo;
        initRequest();
        devRespHandler.addListener(this);
    }

    public void execute()
    {
        ArrayList<File> fileArray = updateInfo.fileList;
        String vehiecle = updateInfo.vehiecle;
        String version = updateInfo.version;
        String language = updateInfo.language;
        Object ret = rq2401.setParams(DPUParamTools.convert0(fileArray, vehiecle, version, language)).postAndWait();// 车型
        if (ret == null)
        {
            notifyError(ActionEvent.ERROR_FILE_INFO_FOR_DEVICE, rq2401);
            return;
        }
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
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq2401 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x01 }, null, 10, this);
        Queue.put(rq2401.getReqestId().toString(), rq2401);
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

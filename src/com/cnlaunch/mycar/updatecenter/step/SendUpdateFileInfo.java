package com.cnlaunch.mycar.updatecenter.step;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.device.ActionEvent;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest;
import com.cnlaunch.mycar.updatecenter.device.DeviceRequest.OnDeviceTimeoutListener;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponse;
import com.cnlaunch.mycar.updatecenter.device.DeviceResponseHandler;
import com.cnlaunch.mycar.updatecenter.device.DeviceUpdateListener;
import com.cnlaunch.mycar.updatecenter.device.ProgressInfo;
import com.cnlaunch.mycar.updatecenter.model.ContinueUpdateFileInStream;
import com.cnlaunch.mycar.updatecenter.model.DPUUpdateFileInStream;
import com.cnlaunch.mycar.updatecenter.model.NormalFileInStream;
import com.cnlaunch.mycar.updatecenter.model.UpdateFileInfo;
import com.cnlaunch.mycar.updatecenter.tools.DPUParamTools;
import com.cnlaunch.mycar.updatecenter.tools.StatisticHelper;

public class SendUpdateFileInfo implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    private static boolean D = false;
    String TAG = "SendUpdateFileInfo";
    
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;
    
    HashMap<String, String> md5info;
    // 是否忽略INI文件
    final static int PKG_SIZE = 4 * 1024;
    byte[] buff = new byte[PKG_SIZE];
    
    private byte[] updateFileByte;
    DeviceRequest rq2401; // 写升级文件信息

    public SendUpdateFileInfo(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue,byte[] updateFileByte )
    {
        this.context        = context;
        this.connection     = connection;
        this.listener       = listener;
        this.devRespHandler = devRespHandler;
        this.Queue          = Queue;
        this.updateFileByte = updateFileByte;
        initRequest();
        devRespHandler.addListener(this);
    }

    /**
     * 执行发送数据
     * @return 
     * @since DBS V100
     */
    public boolean execute()
    {
        
        Object ret = rq2401.setParams(updateFileByte).postAndWait();// 车型 版本   语言  升级的文件总长度...
        if (ret==null)
        {
            notifyError(ActionEvent.ERROR_FILE_INFO_FOR_DEVICE,rq2401);
            return false; 
        }
        if(D)Log.d(TAG, "写入升级文件信息成功！");
        destroy();
        return true;
        
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
        destroy();
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, null);

    }

    public void notifyError(int err, DeviceRequest rq)
    {
        destroy();
        if (listener != null)
        {
            listener.onDeviceUpdateException(err, rq);
        }
    }

    public void destroy()
    {

        Queue.remove(rq2401);
        rq2401 = null;
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq2401 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x01 }, null, 25, this);
        Queue.put(rq2401.getReqestId().toString(), rq2401);
    }

    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        destroy();
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

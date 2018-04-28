package com.cnlaunch.mycar.updatecenter.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.diagnose.constant.DPU_String;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
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
import com.cnlaunch.mycar.updatecenter.tools.FileLengthUtil;
import com.cnlaunch.mycar.updatecenter.tools.StatisticHelper;

public class WriteDpusysini implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    String TAG = "SendDataToDPU";
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;
    File iniFile;

    DeviceRequest rq2112;// 写dpusys.ini配置文件
    DeviceRequest rq2113;// 读dpusys.ini配置文件
    DeviceRequest rq2110;// 输入密码 000000

    public WriteDpusysini(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue, File iniFile)
    {
        this.context = context;
        this.connection = connection;
        this.listener = listener;
        this.devRespHandler = devRespHandler;
        this.Queue = Queue;
        this.iniFile = iniFile;
        initRequest();
        devRespHandler.addListener(this);
    }

    public void execute()
    {

        if (iniFile.exists())
        {
            // 恢复密码为 000000
            final String deaultPwd = "000000";// 默认密码
            DPU_String pwd = new DPU_String(deaultPwd);
            rq2110.setParams(pwd.toBytes());
            Object ret = rq2110.postAndWait();
            if (ret == null)
            {
                notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION, rq2110);
                return;
            }
            Log.d(TAG, "密码验证成功!");

            ret = rq2112.setParams(DPUParamTools.dpuSysIniInfo(iniFile)).postAndWait();
            if (ret == null)
            {
                notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION, rq2112);
                return;
            }
            Log.d(TAG, "写入 DPU SYS INI 配置文件成功!");

            ret = rq2113.postAndWait();
            if (ret == null)
            {
                notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION, rq2113);
                return;
            }
            Log.d(TAG, "读取到的DPU SYS INI" + ret.toString());
            notifyUpdateComplete("升级完成！");
            destroy();
        }
        else
        {
            Log.e(TAG, " 错误 : 没有找到 ini 文件");
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
        Queue.remove(rq2112);
        Queue.remove(rq2113);
        Queue.remove(rq2110);
        rq2110 = null;
        rq2113 = null;
        rq2112 = null;
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq2112 = new DeviceRequest(context, connection, new byte[] { 0x21, 0x12 }, null, 10, this);
        rq2113 = new DeviceRequest(context, connection, new byte[] { 0x21, 0x13 }, null, 10, this);
        rq2110 = new DeviceRequest(context, connection, new byte[] { 0x21, 0x10 }, null, 10, this);
        Queue.put(rq2112.getReqestId().toString(), rq2112);
        Queue.put(rq2113.getReqestId().toString(), rq2113);
        Queue.put(rq2110.getReqestId().toString(), rq2110);
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
    public void notifyUpdateComplete(String message)
    {
        if (listener!=null)
        {
            listener.onDeviceUpdateFinish(message);
        }
    }

}

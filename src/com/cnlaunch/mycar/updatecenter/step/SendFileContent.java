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

public class SendFileContent implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    private static boolean D = false;
    String TAG = "SendFileContent";
    
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;
    
    UpdateFileInfo updateFileInfo;
    HashMap<String, String> md5info;
    // 是否忽略INI文件
    final static int PKG_SIZE = 4 * 1024;
    byte[] buff = new byte[PKG_SIZE];
    
    StatisticHelper helper = new StatisticHelper();

    DeviceRequest rq2402; // 写文件名和文件长度
    DeviceRequest rq2403; // 写文件内容
    DeviceRequest rq2404; // md5校验
    DPUUpdateFileInStream fis;
    boolean isNewUpdat = false;
    public SendFileContent(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue, UpdateFileInfo updateFileInfo,HashMap<String, String> md5info)
    {
        this.context        = context;
        this.connection     = connection;
        this.listener       = listener;
        this.devRespHandler = devRespHandler;
        this.Queue          = Queue;
        this.updateFileInfo = updateFileInfo;
        this.md5info        = md5info;
        this.isNewUpdat     = true;
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
        
        long totalBytes     = updateFileInfo.totalBytes; // 所有文件的总长度
        long byteSent       = updateFileInfo.byteSent;   // 已经发送的文件字节数
        File file           = updateFileInfo.file;       // 要发送的文件
        Object ret = null;

        
        int count    = 0;    // 每次读取到缓冲区的字节数
        long writePos = 0;    // 写入文件的位置
        long start   = 0;    // 发送命令的起始时间
        long end     = 0;    // 发送命令的终止时间
        ProgressInfo progress = new ProgressInfo(); // 进度信息
        boolean isSuccess = false;
        try
        {
            // 如果断点续传
            if (updateFileInfo.isContinueSend)
            {
                if(D)Log.d(TAG, "断点续传" + updateFileInfo.breakPointPos);
                fis = new ContinueUpdateFileInStream(file, "rw", updateFileInfo.breakPointPos);
                writePos = updateFileInfo.breakPointPos;
            }
            else
            {
                // 第一步： 发送文件的名称和长度
                
                if(D)Log.d(TAG, "新升级" + file.getName() + file.length());
                ret = rq2402.setParams(DPUParamTools.fileNameAndLength(file.getName(), file)).postAndWait(); // 文件的长度
                if (ret == null)
                {
                    notifyError(ActionEvent.ERROR_FILE_POSITION_OPERATION, rq2402);
                    return false;
                }
                fis = new NormalFileInStream(file);
            }
            long counter = 0;
            
            while (true)
            {
                if (FirmwareUpdate.cellState != 0)
                {
                    destroy();
                    break;
                }
                if (isNewUpdat && fis.getFilePoint() > 0)
                {
                    isNewUpdat = false;
                    counter = fis.getFilePoint() / 4096;
                }
                if(D)Log.d(TAG, "文件节点：" + writePos); 
                rq2403.setCounter((byte)counter);
                byte[] content = null;
                count = 0;
                
                if (writePos == file.length())
                {
                    isSuccess = true;
                    break;
                }
                if ((count = fis.readFile(buff)) > 0)
                {
                    content = buff;
                }
                else
                {
                    isSuccess = false;
                    break;
                }
                //if(D)Log.d(TAG, "发送第 " +  counter + "包数据");
                start = System.currentTimeMillis();
                if (count < PKG_SIZE) // 如果是最后一包数据
                {
                    byte[] rest = new byte[count];
                    System.arraycopy(content, 0, rest, 0, count);
                    ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos, rest, count)).postAndWait(); // 文件的数据块
                    if (ret == null)
                    {
                        notifyError(ActionEvent.ERROR_DATA_TRANSFER, rq2403);
                        return false;
                    }
                } 
                else
                {
                    ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos, content, count)).postAndWait();
                    if (ret == null)
                    {
                        notifyError(ActionEvent.ERROR_DATA_TRANSFER, rq2403);
                        return false;
                    }
                }
                //if(D)Log.d(TAG, "第 " +  counter + "包数据发送成功！");
                counter++;
                end = System.currentTimeMillis();
                
                writePos += count;
                byteSent += count;
                
                helper.calcResults(totalBytes - byteSent, count, start, end);
                
                progress
                .setFile(file)
                .setCurrent(updateFileInfo.fileNo)
                .setFileSum(updateFileInfo.fileCount)
                .setTotalBytes((int) totalBytes)
                .setSentBytes((int) byteSent)
                .setLeftHours(helper.getRestHours())
                .setLeftMinites(helper.getRestMinutes())
                .setLeftSeconds(helper.getRestSeconds())
                .setPercent((int) (byteSent * 100 / totalBytes));
                
                notifyUpdateProgress(ActionEvent.ACTION_CODE_DATA_TRANSFERING, progress);

            }
            fis.closeStream();
            
        } 
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
            notifyError(ActionEvent.ERROR_DATA_TRANSFER, rq2403);
            return false;
        }
        
        // 第三步:发送MD5校验信息
        try
        {
            if (isSuccess)
            {
                
                String md5 = md5info.get(file.getName());
                ret = rq2404.setParams(md5.getBytes()).postAndWait(); // 文件的MD5
                if (ret == null)
                {
                    notifyError(ActionEvent.ERROR_DATA_INTEGRETY, rq2404);
                    return false;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            notifyError(ActionEvent.ERROR_DATA_INTEGRETY, rq2404);
            return false;
        }
        if(D)Log.d(TAG, "文件发送成功！" );
        return isSuccess;
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
        if(D) Log.d(TAG, "228 destroy...");
        //destroy();
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, null);

    }

    public void notifyError(int err, DeviceRequest rq)
    {
        if(D) Log.d(TAG, "235 destroy...");
        destroy();
        if (listener != null)
        {
            listener.onDeviceUpdateException(err, rq);
        }
    }

    public void destroy()
    {
        if (fis != null)
        {
            fis.closeStream();
            fis = null;
        }
        if (rq2403 != null)
        {
            rq2403.waitForTrue();
        }
        Queue.remove(rq2402);
        Queue.remove(rq2403);
        Queue.remove(rq2404);
        rq2402 = null;
        rq2403 = null;
        rq2404 = null;
        if(D) Log.d(TAG, "line 247 removeListener...");
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq2402 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x02 }, null, 25, this);
        rq2403 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x03 }, null, 25, this);
        rq2404 = new DeviceRequest(context, connection, new byte[] { 0x24, 0x04 }, null, 25, this);
        Queue.put(rq2402.getReqestId().toString(), rq2402);
        Queue.put(rq2403.getReqestId().toString(), rq2403);
        Queue.put(rq2404.getReqestId().toString(), rq2404);
    }

    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        if(D) Log.d(TAG, "268 destroy...");
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

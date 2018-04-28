package com.cnlaunch.mycar.updatecenter.step;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.device.ActionEvent;
import com.cnlaunch.mycar.updatecenter.device.DeviceMode;
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
import com.cnlaunch.mycar.updatecenter.tools.FileMD5Checker;
import com.cnlaunch.mycar.updatecenter.tools.FileTrunc;
import com.cnlaunch.mycar.updatecenter.tools.StatisticHelper;
import com.cnlaunch.mycar.updatecenter.version.VersionNumber;

public class WriteDownloadBin implements DeviceResponseHandler.Listener, OnDeviceTimeoutListener
{

    private static boolean D = true;
    String TAG = "WriteDownloadBin";
    
    Context context;
    Connection connection;
    DeviceUpdateListener listener;
    DeviceResponseHandler devRespHandler;
    HashMap<String, Object> Queue;
    final static int PKG_SIZE = 4 * 1024;
    byte[] buff = new byte[PKG_SIZE];
    File donwloadbin;
    StatisticHelper helper = new StatisticHelper();

    DeviceRequest rq2105;   // 读取诊断头信息
    DeviceRequest rq2114;   // 查询运行模式
    DeviceRequest rq2407;   // 切换到boot模式
    DeviceRequest rq2111;   // 跳转到download.bin入口
    DeviceRequest rq250202; // 重新连接
    DeviceRequest rq2503;   // 验证校验字
    DeviceRequest rq2402;   // 写文件名和文件长度
    DeviceRequest rq2403;   // 写文件内容
    DeviceRequest rq2404;   // md5校验
    DPUUpdateFileInStream fis;

    public WriteDownloadBin(Context context, Connection connection, DeviceUpdateListener listener, DeviceResponseHandler devRespHandler, HashMap<String, Object> Queue)
    {
        this.context        = context;
        this.connection     = connection;
        this.listener       = listener;
        this.devRespHandler = devRespHandler;
        this.Queue          = Queue;
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
        
        // 读取设备上的版本信息
        @SuppressWarnings("unchecked")
        ArrayList<String> versionInfo = (ArrayList<String>) rq2105.postAndWait();
        if ( versionInfo == null )
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2105);
            return false;
        }
        
        String boot              = versionInfo.get(0);      
        String deviceDownloadbin = versionInfo.get(1);
        if(D)Log.d(TAG,"设备上的   boot版本: "+ boot);
        if(D)Log.d(TAG,"设备上的 download.bin版本: " + deviceDownloadbin);
    
        donwloadbin = new File(UpdateCenterConstants.DBSCAR_DIR,"/download/download.bin");
        
        if (donwloadbin.exists())
        {
            // 从 downloadbin 文件获取版本信息
            byte[] versionBytes = FileTrunc.getByteRegion(donwloadbin, 0x10000, 0x10000+6);
            String latestDownloadbin = new String(versionBytes);
            
            VersionNumber latestDownloadbinVersion = new VersionNumber(latestDownloadbin);
            
           if (deviceDownloadbin.equalsIgnoreCase("")// download.bin 不存在 也需要升级
                  ||latestDownloadbinVersion.isGreaterThan(new VersionNumber(deviceDownloadbin)))// 版本不一致,需要升级download.bin
                //if (true)// 版本不一致,需要升级download.bin
            {
                if(D)Log.d(TAG,"download.bin 需要更新 ,开始升级dowload.bin,切换到boot 模式");
                // 查询运行模式如果是boot就不用切换[不发 2407]
                Object ret = rq2114.postAndWait();
                if (ret==null)
                {
                    notifyError(ActionEvent.ERROR_SWITCH_TO_BOOT_MODE,rq2114);
                    return false;
                }
                
                int mode = (Integer) ret;
                if (mode != DeviceMode.MODE_BOOT)// 若不是boot模式
                {
                    ret = rq2407.postAndWait();// 切换到boot模式
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_SWITCH_TO_BOOT_MODE,rq2407);
                        return false;
                    }
                    // 切换成功之后,等待设备稳定
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return false;
                    }
                }   
                if(D)Log.e(TAG,"设备 成功 切换到  boot 升级模式!");
                // ====  重新连接 ============
                ret = rq250202.postAndWait();// 获取校验字
                if (ret == null)
                {
                    notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
                    return false;
                }
                else
                {
                    if(D)Log.e(TAG,"2502 连接 成功");
                    byte[] checksum = (byte[])ret;
                    notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "正在连接设备");
                    ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// 验证校验字
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                        return false;// 强行退出线程
                    }
                    if(D)Log.e(TAG,"2503 安全检验成功");
                }
                // 写download.bin的内容
                if(!writeDownloadbin())
                {
                    return false;
                }
                if(D)Log.e(TAG,"download.bin文件发送成功!");
                // 写md5校验信息
                String md5_downloadbin = null;
                try {
                    md5_downloadbin = FileMD5Checker.calculateSingleFileMD5sum(donwloadbin);
                    ret  = rq2404.setParams(md5_downloadbin.getBytes()).postAndWait();  // 文件的MD5   
                    if(ret==null)
                    {
                        return false;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(D)Log.e(TAG,"download.bin MD5校验成功!");
                // Download.bin 升级成功之后,等待3s ,让设备稳定再跳转
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // 跳转到 download.bin 入口
                ret = rq2111.postAndWait();
                if(ret == null)
                {
                    return false;
                }
                if(D)Log.e(TAG,"跳转到 download.bin 入口成功!");
                // 让设备稳定再连接
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // ====  重新连接   ============
                ret = rq250202.postAndWait();// 获取校验字
                if(ret==null)
                {
                    notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
                }
                else
                {
                    byte[] checksum = (byte[])ret;
                    notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "正在连接设备");
                    ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// 验证校验字
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                    }
                    if(D)Log.e(TAG,"download.bin升级成功!");
                    destroy();
                }
            }
            
        }
        else
        {
            if(D)Log.e(TAG,"download.bin 文件不存在!");
        }
        return true;
    }

    private boolean writeDownloadbin()
    {
        long totalBytes     = donwloadbin.length(); // 所有文件的总长度
        long byteSent       = 0;   // 已经发送的文件字节数
        Object ret = null;

        
        int count    = 0;    // 每次读取到缓冲区的字节数
        int writePos = 0;    // 写入文件的位置
        long start   = 0;    // 发送命令的起始时间
        long end     = 0;    // 发送命令的终止时间
        ProgressInfo progress = new ProgressInfo(); // 进度信息
        boolean isSuccess = false;
        try
        {
            // 第一步： 发送文件的名称和长度
            if(D)Log.d(TAG, "新升级" + donwloadbin.getName() + donwloadbin.length());
            ret = rq2402.setParams(DPUParamTools.fileNameAndLength(donwloadbin.getName(), donwloadbin)).postAndWait(); // 文件的长度
            if (ret == null)
            {
                notifyError(ActionEvent.ERROR_FILE_POSITION_OPERATION, rq2402);
                return false;
            }
            if(D)Log.e(TAG,"写download.bin文件名称和长度成功!");
            fis = new NormalFileInStream(donwloadbin);
            int i = 0;
            while (true)
            {
                if (FirmwareUpdate.cellState != 0)
                {
                    destroy();
                    break;
                }
                byte[] content;
                count = 0;
                if ((count = fis.readFile(buff)) > 0)
                {
                    content = buff;
                }
                else
                {
                    isSuccess = true;
                    break;
                }
                if(D)Log.d(TAG, FirmwareUpdate.cellState + " " + i++ );
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
                end = System.currentTimeMillis();
                
                writePos += count;
                byteSent += count;
                
                helper.calcResults(totalBytes - byteSent, count, start, end);
                progress.setFile(donwloadbin).setCurrent(1).setFileSum(1).setTotalBytes((int) totalBytes).setSentBytes((int) byteSent)
                .setLeftHours(helper.getRestHours()).setLeftMinites(helper.getRestMinutes()).setLeftSeconds(helper.getRestSeconds()).setPercent((int) (byteSent * 100 / totalBytes));
                notifyUpdateProgress(ActionEvent.ACTION_CODE_DATA_TRANSFERING, progress);

            }
            fis.closeStream();
            return isSuccess;
        } 
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
            notifyError(ActionEvent.ERROR_DATA_TRANSFER, rq2403);
            return false;
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
        if(D) Log.d(TAG, "onDeviceError destroy...");
        destroy();
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY, null);

    }

    public void notifyError(int err, DeviceRequest rq)
    {
        if(D) Log.d(TAG, "notifyError destroy...");
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
        Queue.remove(rq2105);
        Queue.remove(rq2114);
        Queue.remove(rq2407);
        Queue.remove(rq2111);
        Queue.remove(rq250202);
        Queue.remove(rq2503);
        Queue.remove(rq2402);
        Queue.remove(rq2403);
        Queue.remove(rq2404);
        rq2105 = null;   // 读取诊断头信息
        rq2114 = null;   // 查询运行模式
        rq2407 = null;   // 切换到boot模式
        rq2111 = null;   // 跳转到download.bin入口
        rq250202 = null; // 重新连接
        rq2503 = null;   // 验证校验字
        rq2402 = null;   // 写文件名和文件长度
        rq2403 = null;   // 写文件内容
        rq2404 = null;   // md5校验
        this.devRespHandler.removeListener(this);
    }

    private void initRequest()
    {
        rq250202 = new DeviceRequest(context,connection, new byte[]{0x25,0x02}, new byte[]{02},30,this);
        rq2503 = new DeviceRequest(context,connection, new byte[]{0x25,0x03}, null,30,this);
        rq2402 = new DeviceRequest(context,connection, new byte[]{0x24,0x02}, null,30,this);
        rq2403 = new DeviceRequest(context,connection, new byte[]{0x24,0x03}, null,30,this);
        rq2404 = new DeviceRequest(context,connection, new byte[]{0x24,0x04}, null,30,this);
        rq2407 = new DeviceRequest(context,connection, new byte[]{0x24,0x07}, null,30,this);
        rq2105 = new DeviceRequest(context,connection, new byte[]{0x21,0x05}, null,30,this);
        rq2111 = new DeviceRequest(context,connection, new byte[]{0x21,0x11}, null,30,this);
        rq2114 = new DeviceRequest(context,connection, new byte[]{0x21,0x14}, null,30,this);
        Queue.put(rq2402.getReqestId().toString(), rq2402);
        Queue.put(rq2403.getReqestId().toString(), rq2403);
        Queue.put(rq2404.getReqestId().toString(), rq2404);
        Queue.put(rq250202.getReqestId().toString(), rq250202);
        Queue.put(rq2503.getReqestId().toString(), rq2503);
        Queue.put(rq2105.getReqestId().toString(), rq2105);
        Queue.put(rq2111.getReqestId().toString(), rq2111);
        Queue.put(rq2114.getReqestId().toString(), rq2114);
    }

    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        if(D) Log.d(TAG, "onDeviceTimeout destroy...");
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

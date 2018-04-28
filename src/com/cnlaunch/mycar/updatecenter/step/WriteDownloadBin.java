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

    DeviceRequest rq2105;   // ��ȡ���ͷ��Ϣ
    DeviceRequest rq2114;   // ��ѯ����ģʽ
    DeviceRequest rq2407;   // �л���bootģʽ
    DeviceRequest rq2111;   // ��ת��download.bin���
    DeviceRequest rq250202; // ��������
    DeviceRequest rq2503;   // ��֤У����
    DeviceRequest rq2402;   // д�ļ������ļ�����
    DeviceRequest rq2403;   // д�ļ�����
    DeviceRequest rq2404;   // md5У��
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
     * ִ�з�������
     * @return 
     * @since DBS V100
     */
    public boolean execute()
    {
        
        // ��ȡ�豸�ϵİ汾��Ϣ
        @SuppressWarnings("unchecked")
        ArrayList<String> versionInfo = (ArrayList<String>) rq2105.postAndWait();
        if ( versionInfo == null )
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2105);
            return false;
        }
        
        String boot              = versionInfo.get(0);      
        String deviceDownloadbin = versionInfo.get(1);
        if(D)Log.d(TAG,"�豸�ϵ�   boot�汾: "+ boot);
        if(D)Log.d(TAG,"�豸�ϵ� download.bin�汾: " + deviceDownloadbin);
    
        donwloadbin = new File(UpdateCenterConstants.DBSCAR_DIR,"/download/download.bin");
        
        if (donwloadbin.exists())
        {
            // �� downloadbin �ļ���ȡ�汾��Ϣ
            byte[] versionBytes = FileTrunc.getByteRegion(donwloadbin, 0x10000, 0x10000+6);
            String latestDownloadbin = new String(versionBytes);
            
            VersionNumber latestDownloadbinVersion = new VersionNumber(latestDownloadbin);
            
           if (deviceDownloadbin.equalsIgnoreCase("")// download.bin ������ Ҳ��Ҫ����
                  ||latestDownloadbinVersion.isGreaterThan(new VersionNumber(deviceDownloadbin)))// �汾��һ��,��Ҫ����download.bin
                //if (true)// �汾��һ��,��Ҫ����download.bin
            {
                if(D)Log.d(TAG,"download.bin ��Ҫ���� ,��ʼ����dowload.bin,�л���boot ģʽ");
                // ��ѯ����ģʽ�����boot�Ͳ����л�[���� 2407]
                Object ret = rq2114.postAndWait();
                if (ret==null)
                {
                    notifyError(ActionEvent.ERROR_SWITCH_TO_BOOT_MODE,rq2114);
                    return false;
                }
                
                int mode = (Integer) ret;
                if (mode != DeviceMode.MODE_BOOT)// ������bootģʽ
                {
                    ret = rq2407.postAndWait();// �л���bootģʽ
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_SWITCH_TO_BOOT_MODE,rq2407);
                        return false;
                    }
                    // �л��ɹ�֮��,�ȴ��豸�ȶ�
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return false;
                    }
                }   
                if(D)Log.e(TAG,"�豸 �ɹ� �л���  boot ����ģʽ!");
                // ====  �������� ============
                ret = rq250202.postAndWait();// ��ȡУ����
                if (ret == null)
                {
                    notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
                    return false;
                }
                else
                {
                    if(D)Log.e(TAG,"2502 ���� �ɹ�");
                    byte[] checksum = (byte[])ret;
                    notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "���������豸");
                    ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// ��֤У����
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                        return false;// ǿ���˳��߳�
                    }
                    if(D)Log.e(TAG,"2503 ��ȫ����ɹ�");
                }
                // дdownload.bin������
                if(!writeDownloadbin())
                {
                    return false;
                }
                if(D)Log.e(TAG,"download.bin�ļ����ͳɹ�!");
                // дmd5У����Ϣ
                String md5_downloadbin = null;
                try {
                    md5_downloadbin = FileMD5Checker.calculateSingleFileMD5sum(donwloadbin);
                    ret  = rq2404.setParams(md5_downloadbin.getBytes()).postAndWait();  // �ļ���MD5   
                    if(ret==null)
                    {
                        return false;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(D)Log.e(TAG,"download.bin MD5У��ɹ�!");
                // Download.bin �����ɹ�֮��,�ȴ�3s ,���豸�ȶ�����ת
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // ��ת�� download.bin ���
                ret = rq2111.postAndWait();
                if(ret == null)
                {
                    return false;
                }
                if(D)Log.e(TAG,"��ת�� download.bin ��ڳɹ�!");
                // ���豸�ȶ�������
                try{
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // ====  ��������   ============
                ret = rq250202.postAndWait();// ��ȡУ����
                if(ret==null)
                {
                    notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
                }
                else
                {
                    byte[] checksum = (byte[])ret;
                    notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "���������豸");
                    ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// ��֤У����
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                    }
                    if(D)Log.e(TAG,"download.bin�����ɹ�!");
                    destroy();
                }
            }
            
        }
        else
        {
            if(D)Log.e(TAG,"download.bin �ļ�������!");
        }
        return true;
    }

    private boolean writeDownloadbin()
    {
        long totalBytes     = donwloadbin.length(); // �����ļ����ܳ���
        long byteSent       = 0;   // �Ѿ����͵��ļ��ֽ���
        Object ret = null;

        
        int count    = 0;    // ÿ�ζ�ȡ�����������ֽ���
        int writePos = 0;    // д���ļ���λ��
        long start   = 0;    // �����������ʼʱ��
        long end     = 0;    // �����������ֹʱ��
        ProgressInfo progress = new ProgressInfo(); // ������Ϣ
        boolean isSuccess = false;
        try
        {
            // ��һ���� �����ļ������ƺͳ���
            if(D)Log.d(TAG, "������" + donwloadbin.getName() + donwloadbin.length());
            ret = rq2402.setParams(DPUParamTools.fileNameAndLength(donwloadbin.getName(), donwloadbin)).postAndWait(); // �ļ��ĳ���
            if (ret == null)
            {
                notifyError(ActionEvent.ERROR_FILE_POSITION_OPERATION, rq2402);
                return false;
            }
            if(D)Log.e(TAG,"дdownload.bin�ļ����ƺͳ��ȳɹ�!");
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
                if (count < PKG_SIZE) // ��������һ������
                {
                    byte[] rest = new byte[count];
                    System.arraycopy(content, 0, rest, 0, count);
                    ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos, rest, count)).postAndWait(); // �ļ������ݿ�
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
     * ֪ͨ�������̵���Ϣ
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
        rq2105 = null;   // ��ȡ���ͷ��Ϣ
        rq2114 = null;   // ��ѯ����ģʽ
        rq2407 = null;   // �л���bootģʽ
        rq2111 = null;   // ��ת��download.bin���
        rq250202 = null; // ��������
        rq2503 = null;   // ��֤У����
        rq2402 = null;   // д�ļ������ļ�����
        rq2403 = null;   // д�ļ�����
        rq2404 = null;   // md5У��
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

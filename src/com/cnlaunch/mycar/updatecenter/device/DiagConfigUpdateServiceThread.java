package com.cnlaunch.mycar.updatecenter.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.diagnose.constant.DPU_String;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.model.SerialInfo;
import com.cnlaunch.mycar.updatecenter.tools.DPUParamTools;
import com.cnlaunch.mycar.updatecenter.tools.FileIntegrityChecker;
import com.cnlaunch.mycar.updatecenter.tools.FileLengthUtil;
import com.cnlaunch.mycar.updatecenter.tools.FileMD5Checker;
import com.cnlaunch.mycar.updatecenter.tools.FileScanner;
import com.cnlaunch.mycar.updatecenter.tools.FileTrunc;
import com.cnlaunch.mycar.updatecenter.tools.StatisticHelper;
import com.cnlaunch.mycar.updatecenter.version.VersionNumber;
import com.cnlaunch.mycar.updatecenter.version.VersionPattern;

/**
 * 用于诊断软件升级的服务线程
 * */
public class DiagConfigUpdateServiceThread extends Thread 
                        implements DeviceResponseHandler.Listener,DeviceRequest.OnDeviceTimeoutListener
{
    String TAG = "DiagConfigUpdateServiceThread";
    Connection connection;
    String vehiecle;
    String version;
    String language;


    int errorCode;
    boolean errorHappened = false;
    
    String baseDir;// 升级文件的文件夹位置
    boolean filesAreComplete = false;// 文件是否完整
    ArrayList<File> fileArray = new ArrayList<File>();
    HashMap<String,String> md5info;
    // 是否忽略INI文件
    boolean ignoreDPUsysiniFile = false;
    
    final static int PKG_SIZE = 4*1024;
    byte[] buff = new byte[PKG_SIZE];
    StatisticHelper helper = new StatisticHelper();
    /**
     * 需要的命令请求
     * */
    DeviceRequest rq250202;
    DeviceRequest rq2503;
    DeviceRequest rq2401;
    DeviceRequest rq2402;
    DeviceRequest rq2403;
    DeviceRequest rq2404;
    DeviceRequest rq2405;
    DeviceRequest rq2407;
    DeviceRequest rq2408;
    DeviceRequest rq2105;// 查询 download 版本
    DeviceRequest rq2111;// 跳转到 download.bin 入口
    DeviceRequest rq2112;// 写dpusys.ini配置文件
    DeviceRequest rq2113;// 写dpusys.ini配置文件
    DeviceRequest rq2114;// 查询运行模式 Boot / download.bin
    DeviceRequest rq2110;// 输入密码  000000
    DeviceRequest rq2103;// 查询序列号
    HashMap<String,Object> Queue = new HashMap<String, Object>();
    
    Context context;
    
    /**
     * 升级过程的观察者
     * */
    DeviceUpdateListener listener;
    /**
     * 设备响应处理
     * */
    DeviceResponseHandler deviceResponseHandle;
    String serialNumber;
    MyCarApplication application;
    /**
     * 设备升级服务
     * @param ctx
     * @param devRespHandler 设备响应处理
     * @param con  连接对象
     * @param baseDir 诊断文件的目录
     * @param params  汽车配置参数
     * @param files   升级文件列表
     * @param md5     升级文件的 md5 信息
     */
    public DiagConfigUpdateServiceThread(Context ctx,DeviceResponseHandler devRespHandler,
            Connection con,String baseDir,String[] params,File[] files,HashMap<String,String> md5, String serialNumber)
    {
        connection = con;
        deviceResponseHandle = devRespHandler;
        deviceResponseHandle.addListener(this);
        this.context = ctx;
        
        this.vehiecle = params[0];
        this.version  = params[1];
        this.language = params[2];
        
        this.baseDir = baseDir;
        for(int i=0;i<files.length;i++)
        {
            fileArray.add(files[i]);
        }
        
        this.md5info = md5;
        application = (MyCarApplication)((Activity)ctx).getApplication();
        this.serialNumber = serialNumber;
    }
    
    synchronized public void setUpdateListener(DeviceUpdateListener l)
    {
        this.listener  = l;
    }
    
    synchronized public void removeUpdateListener(DeviceUpdateListener l)
    {
        this.listener  = null;
    }
    
    public void forceStop()
    {
        deviceResponseHandle.removeListener(this);
        this.interrupt();
    }
    
    @Override
    public void run()
    {
//      FileScanner fileScanner = new FileScanner(context);// 扫描出需要升级的文件
//      fileScanner.setDirToScan(new File(diagSoftDir));// 诊断文件所在的目录  
//        
//      fileScanner.setScanListener(new FileScanListener());
//      fileScanner.doScan();
        
        Object ret = null;
        
        if(vehiecle == null || version == null)
        {
            throw new NullPointerException("Error in Parameters , cannot be Null");
        }
        /**
         * 初始化请求,无法确定的参数区域暂时先设为 null, 后面再填充
         * */
        rq250202 = new DeviceRequest(context,connection, new byte[]{0x25,0x02}, new byte[]{02},10,this);
        rq2503 = new DeviceRequest(context,connection, new byte[]{0x25,0x03}, null,10,this);
        rq2401 = new DeviceRequest(context,connection, new byte[]{0x24,0x01}, null,10,this);
        rq2402 = new DeviceRequest(context,connection, new byte[]{0x24,0x02}, null,15,this);
        rq2403 = new DeviceRequest(context,connection, new byte[]{0x24,0x03}, null,15,this);
        rq2404 = new DeviceRequest(context,connection, new byte[]{0x24,0x04}, null,15,this);
        rq2405 = new DeviceRequest(context,connection, new byte[]{0x24,0x05}, null,15,this);
        rq2407 = new DeviceRequest(context,connection, new byte[]{0x24,0x07}, null,10,this);
        rq2408 = new DeviceRequest(context,connection, new byte[]{0x24,0x08}, null,10,this);
        rq2105 = new DeviceRequest(context,connection, new byte[]{0x21,0x05}, null,10,this);
        rq2111 = new DeviceRequest(context,connection, new byte[]{0x21,0x11}, null,10,this);
        rq2112 = new DeviceRequest(context,connection, new byte[]{0x21,0x12}, null,10,this);
        rq2113 = new DeviceRequest(context,connection, new byte[]{0x21,0x13}, null,10,this);
        rq2114 = new DeviceRequest(context,connection, new byte[]{0x21,0x14}, null,10,this);
        rq2110 = new DeviceRequest(context,connection, new byte[]{0x21,0x10},null,10,this);
        rq2103 = new DeviceRequest(context,connection, new byte[]{0x21,0x03},null,10,this);
        /**
         * 添加到请求队列
         * */
        Queue.put(rq2401.getReqestId().toString(), rq2401);
        Queue.put(rq2402.getReqestId().toString(), rq2402);
        Queue.put(rq2403.getReqestId().toString(), rq2403);
        Queue.put(rq2404.getReqestId().toString(), rq2404);
        Queue.put(rq2405.getReqestId().toString(), rq2405);
        Queue.put(rq2407.getReqestId().toString(), rq2407);
        Queue.put(rq2408.getReqestId().toString(), rq2408);
        Queue.put(rq250202.getReqestId().toString(), rq250202);
        Queue.put(rq2503.getReqestId().toString(), rq2503);
        Queue.put(rq2105.getReqestId().toString(), rq2105);
        Queue.put(rq2111.getReqestId().toString(), rq2111);
        Queue.put(rq2112.getReqestId().toString(), rq2112);
        Queue.put(rq2113.getReqestId().toString(), rq2113);
        Queue.put(rq2114.getReqestId().toString(), rq2114);
        Queue.put(rq2110.getReqestId().toString(), rq2110);
        Queue.put(rq2103.getReqestId().toString(), rq2103);
        
        //===================================   serialNumber =====================================
      
        @SuppressWarnings("unchecked")
        Object serialRet = rq2103.postAndWait();
        if ( serialRet == null )
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2103);
            return;
        }
        Log.e(TAG,"step4: 2103 获得序列号成功");
        Log.e(TAG,"序列号" + serialRet.toString());
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
//        // 统一数据流[可选的升级文件]
//        File udscfgFile  = new File(baseDir,"/vehicles/"+vehiecle+"/"+version+"/"+"udscfg.bin");
//        if (udscfgFile.exists())
//        {
//            fileArray.add(udscfgFile);
//            try {
//                String md5  = FileMD5Checker.calculateSingleFileMD5sum(udscfgFile);
//                md5info.put(udscfgFile.getName().toUpperCase(), md5);
//                md5info.put(udscfgFile.getName().toLowerCase(), md5);
//                md5info.put(udscfgFile.getName(), md5);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        
        Log.d(TAG,"开始升级设备配置");
        notifyUpdateStart();
        //===================================  step 1 =====================================
        ret = rq250202.postAndWait();// 获取校验字
        if (ret==null)
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
            return;
        }
        else
        {
            Log.e(TAG,"step1: 2502 安全检验成功");
            byte[] checksum = (byte[])ret;
            notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "正在连接设备");
            //===================================  step 2 =====================================
            ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// 验证校验字
            if(ret==null)
            {
                notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                return;// 强行退出线程
            }
            Log.e(TAG,"step2: 2503 安全检验成功");
        }
        //===================================  step 3  dowload.bin =====================================
        // 读取设备上的版本信息
        @SuppressWarnings("unchecked")
        ArrayList<String> versionInfo = (ArrayList<String>) rq2105.postAndWait();
        if ( versionInfo == null )
        {
            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2105);
            return;
        }
     
        
        
        if (application.isNeedUpdateDownloadBin())
        {
            String boot = versionInfo.get(0);
            String deviceDownloadbin = versionInfo.get(1);
//            String deviceDownloadVersion = "V10.24";// test only 
            Log.d(TAG,"设备上的   boot版本: "+ boot);
            Log.d(TAG,"设备上的 download.bin版本: "+deviceDownloadbin);
        
            File donwloadbin = null;
            donwloadbin = new File(baseDir,"/download/download.bin");
            // test 
//        boot = "V19.23";
            if(new VersionPattern(boot).isSameWith(new VersionPattern("V11.22")))
            {
                //donwloadbin = new File(baseDir,"/downloadbin/download_old.bin");
                Log.d(TAG,"旧版本的BOOT");
            }
            else if(new VersionPattern(boot).isSameWith(new VersionPattern("V11.22.333")))
            {
                //donwloadbin = new File(baseDir,"/downloadbin/download.bin");
                Log.d(TAG,"新版本的BOOT");
            }
            else
            {
                Log.e(TAG,"错误: 未定义的BOOT版本类型");
                notifyError(ActionEvent.ERROR_BOOT_VERSION_NUMBER,rq2105);
                return;
            }
            
            if (donwloadbin.exists())
            {
                // 从 downloadbin 文件获取版本信息
                byte[] versionBytes = FileTrunc.getByteRegion(donwloadbin, 0x10000, 0x10000+6);
                String latestDownloadbin = new String(versionBytes);
                
                VersionNumber latestDownloadbinVersion = new VersionNumber(latestDownloadbin);
                
                if (latestDownloadbinVersion.isLessEqualTo(new VersionNumber("V10.38")))
                {
                    // ignoreDPUsysiniFile = true; // 不必写入 dpu sysini 文件 ,兼容低版本的设备!
                }
                
                // for test 
                // deviceDownloadbin = "V09.16";
                
                if (deviceDownloadbin.equalsIgnoreCase("")// download.bin 不存在 也需要升级
                        ||latestDownloadbinVersion.isGreaterThan(new VersionNumber(deviceDownloadbin)))// 版本不一致,需要升级download.bin
                {
                    Log.d(TAG,"download.bin 需要更新 ,开始升级dowload.bin,切换到boot 模式");
                    
                    ret = rq2114.postAndWait();// 查询运行模式如果是boot就不用切换[不发 2407]
                    if (ret==null)
                    {
                        notifyError(ActionEvent.ERROR_SWITCH_TO_BOOT_MODE,rq2114);
                        return;
                    }
                    
                    int mode = (Integer) ret;
                    
                    if (mode != DeviceMode.MODE_BOOT)// 若不是boot模式
                    {
                        ret = rq2407.postAndWait();// 切换到boot模式
                        if(ret==null)
                        {
                            notifyError(ActionEvent.ERROR_SWITCH_TO_BOOT_MODE,rq2407);
                            return;
                        }
                        // 切换成功之后,等待设备稳定
                        try{
                            delayInSeconds(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return ;
                        }
                    }   
                    Log.e(TAG,"设备 成功 切换到  boot 升级模式!");
                    // ====  重新连接 ============
                    ret = rq250202.postAndWait();// 获取校验字
                    if (ret==null)
                    {
                        notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
                        return;
                    }
                    else
                    {
                        Log.e(TAG,"2502 安全检验成功");
                        byte[] checksum = (byte[])ret;
                        notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "正在连接设备");
                        //===================================  step 2 =====================================
                        ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// 验证校验字
                        if(ret==null)
                        {
                            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                            return;// 强行退出线程
                        }
                        Log.e(TAG,"2503 安全检验成功");
                    }
                    
                    ret = rq2402.setParams(DPUParamTools.fileNameAndLength("download.bin",donwloadbin)).postAndWait();  // 文件名和文件长度
                    if (ret == null)
                    {
                        return;
                    }
                    
                    long totalLen = donwloadbin.length();
                    InputStream fis;
                    try {
                        int count = 0;
                        int writePos = 0;
                        long start = 0;
                        long end = 0;
                        fis = new FileInputStream(donwloadbin);
                        ProgressInfo progress = new ProgressInfo();
                        while((count =fis.read(buff))>0)
                        {
                            if (FirmwareUpdate.cellState > -1)
                            {
                                start = System.currentTimeMillis();
                                if(count < PKG_SIZE)// 最后一包数据
                                {
                                    byte[] rest = new byte[count];
                                    System.arraycopy(buff, 0, rest, 0, count);
                                    ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos,rest,count)).postAndWait();  // 文件的数据块
                                    if(ret==null)
                                    {
                                        notifyError(ActionEvent.ERROR_DATA_TRANSFER,rq2403);
                                        return;
                                    }
                                }
                                else
                                {
                                    ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos, buff, count)).postAndWait();
                                    if(ret==null)
                                    {
                                        notifyError(ActionEvent.ERROR_DATA_TRANSFER,rq2403);
                                        return;
                                    }
                                }
                                end = System.currentTimeMillis();
                                writePos += count;
                                
                                helper.calcResults(totalLen - writePos, count, start, end);
                                
                                progress.setFile(donwloadbin).setCurrent(1).setFileSum(1)
                                        .setTotalBytes((int)totalLen).setSentBytes((int)writePos)
                                        .setLeftHours(helper.getRestHours()).setLeftMinites(helper.getRestMinutes())
                                        .setLeftSeconds(helper.getRestSeconds())
                                        .setPercent((int)(writePos *100 / totalLen));
                                
                                notifyUpdateProgress(ActionEvent.ACTION_CODE_DATA_TRANSFERING,progress);
                            }
                           
                        }
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally{
                        // close all
                    }
                    String md5_downloadbin = "";
                    try {
                        md5_downloadbin = FileMD5Checker.calculateSingleFileMD5sum(donwloadbin);
                        ret  = rq2404.setParams(md5_downloadbin.getBytes()).postAndWait();  // 文件的MD5   
                        if(ret==null)
                        {
                            return;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    
                    // Download.bin 升级成功之后,等待3s ,让设备稳定再跳转
                    try{
                        delayInSeconds(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return ;
                    }
                    
                    ret = rq2111.postAndWait();// 跳转到 download.bin 入口
                    if(ret == null)
                    {
                        return;
                    }
                    
                    // 让设备稳定再连接
                    try{
                        delayInSeconds(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return ;
                    }
                    
                    // ====  重新连接   ============
                    ret = rq250202.postAndWait();// 获取校验字
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq250202);
                        return;
                    }
                    else
                    {
                        Log.e(TAG,"step1: 2502 安全检验成功");
                        byte[] checksum = (byte[])ret;
                        notifyActionMessages(ActionEvent.ACTION_CODE_CONNECT_DEVICE, "正在连接设备");
                        ret = rq2503.setParams(DPUParamTools.connectChecksumLevel2(checksum)).postAndWait();// 验证校验字
                        if(ret==null)
                        {
                            notifyError(ActionEvent.ERROR_CONNECT_DEVICE,rq2503);
                            return;// 强行退出线程
                        }
                        Log.e(TAG,"step2: 2503 安全检验成功");
                        Log.e(TAG,"☆☆☆☆☆☆☆download.bin升级成功!");
                    }
                }
                
            }
            else
            {
                Log.e(TAG,"download.bin 文件不存在!!!!!!!!!");
            }
            application.setIsNeedUpdateDownloadBin(false);
        }
    
        
        //==============step4  升级诊断软件======================================
        if (application.isNeedUpdateDiagnose())
        {
            ret = rq2401.setParams(DPUParamTools.convert0(fileArray, vehiecle, version, language)).postAndWait();// 车型 版本   语言  升级的文件总长度...
            if (ret==null)
            {
                notifyError(ActionEvent.ERROR_FILE_INFO_FOR_DEVICE,rq2401);
                return;
            }
            
            long totalBytes = FileLengthUtil.calcTotalBytesInFileList(fileArray);
            long byteSent = 0;
            for(int i = 0;i < fileArray.size(); i++)
            {
                ret = rq2402.setParams(DPUParamTools.fileNameAndLength(fileArray.get(i).getName(),
                                                        fileArray.get(i))).postAndWait();  // 文件的长度
                if (ret==null)
                {
                    notifyError(ActionEvent.ERROR_FILE_POSITION_OPERATION,rq2402);
                    return;
                }
                int count = 0;
                int writePos = 0;
                long start;
                long end;
                
                ProgressInfo progress = new ProgressInfo();
                //int cccc = 0;
                try {
                    InputStream fis = new FileInputStream(fileArray.get(i));
                    while(true)
                    {
                       // Log.d(TAG,"发送" + cccc);
                        if ((count = fis.read(buff)) > 0 && FirmwareUpdate.cellState > -1)
                        {
                           // Log.d(TAG,"发送" + cccc++);
                            start = System.currentTimeMillis();
                            if(count < PKG_SIZE)
                            {
                                byte[] rest = new byte[count];
                                System.arraycopy(buff, 0, rest, 0, count);
                                ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos,rest,count)).postAndWait();  // 文件的数据块
                                if(ret==null)
                                {
                                    notifyError(ActionEvent.ERROR_DATA_TRANSFER,rq2403);
                                    return;
                                }
                            }
                            else
                            {
                                ret = rq2403.setParams(DPUParamTools.dataChunkParams(writePos, buff, count)).postAndWait();
                                if (ret==null)
                                {
                                    notifyError(ActionEvent.ERROR_DATA_TRANSFER,rq2403);
                                    return;
                                }
                            }
                            end = System.currentTimeMillis();
                            
                            writePos += count;
                            byteSent += count;
                            
                            helper.calcResults(totalBytes - byteSent, count, start, end);
                            
                            progress.setFile(fileArray.get(i)).setCurrent(i+1).setFileSum(fileArray.size())
                                    .setTotalBytes((int)totalBytes).setSentBytes((int)byteSent)
                                    .setLeftHours(helper.getRestHours()).setLeftMinites(helper.getRestMinutes())
                                    .setLeftSeconds(helper.getRestSeconds())
                                    .setPercent((int)(byteSent *100 /totalBytes));
                            
                            notifyUpdateProgress(ActionEvent.ACTION_CODE_DATA_TRANSFERING,progress);
                        }
                        else
                        {break;}
                       
                    }
                    fis.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    notifyError(ActionEvent.ERROR_DATA_TRANSFER,rq2403);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    notifyError(ActionEvent.ERROR_DATA_TRANSFER,rq2403);
                    return;
                }
                try 
                {
                    String md5 = md5info.get(fileArray.get(i).getName());
                    ret  = rq2404.setParams(md5.getBytes()).postAndWait();  // 文件的MD5           
                    if (ret==null)
                    {
                        notifyError(ActionEvent.ERROR_DATA_INTEGRETY,rq2404);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyError(ActionEvent.ERROR_DATA_INTEGRETY,rq2404);
                    return;
                }
            }
            // MD5 对比
            Map<String,String> md5InDevice =(HashMap<String,String>)rq2408.postAndWait();
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
                    notifyError(ActionEvent.ERROR_DATA_INTEGRETY,rq2408);
                    return;
                }
            }
            ret = rq2405.postAndWait();
            if (ret==null)
            {
                notifyError(ActionEvent.ERROR_UPDATE_COMPLETE_INDICATION,rq2405);
                return;
            }
            
            if (!ignoreDPUsysiniFile)
            {
                //========================== 写入 dpusys.ini ==================================
                File fileSYSINI = new File(baseDir,"/vehicles"+"/"+vehiecle+"/"+version+"/"+"dpusys.ini");
                if(fileSYSINI.exists())
                {
                    // 恢复密码为  000000
                    final String  deaultPwd = "000000";// 默认密码
                    DPU_String pwd = new DPU_String(deaultPwd);
                    rq2110.setParams(pwd.toBytes());
                    ret = rq2110.postAndWait();
                    if(ret == null)
                    {
                        notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION,rq2110);
                        return;
                    }
                    Log.d(TAG,"------>密码验证成功!");
                    ret = rq2112.setParams(DPUParamTools.dpuSysIniInfo(fileSYSINI)).postAndWait();
                    if(ret==null)
                    {
                        notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION,rq2112);
                        return;
                    }
                    Log.d(TAG,"==========> 写入 DPU SYS INI 配置文件成功!" + fileSYSINI.getAbsolutePath());
                    ret = rq2113.postAndWait();
                    if (ret==null)
                    {
                        notifyError(ActionEvent.ERROR_DEVICE_EXCEPTION,rq2113);
                        return;
                    }
                    Log.d(TAG," ===> INI" + ret.toString());
                }else{
                    Log.e(TAG," 错误 : 没有找到 ini 文件" +fileSYSINI.getAbsolutePath());
                }
            }
            // 升级完后,是否需要升级诊断软件：否
            application.setIsNeedUpdateDiagnoseSW(false);
        }
        
        Log.d(TAG,"===== 完成升级！");
        notifyUpdateComplete("升级完成!");
        deviceResponseHandle.removeListener(this);
        Queue.clear();
    }
        
    /**
     * 设备回应出错 
     * */
    @Override
    public void onDeviceError(String request)
    {
        //connection.reOpenConn();
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY,null);
    }
    
    /**
     * 设备回应正常
     * */
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
    
    public  String byteToHex(byte[] data)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : data ) 
        {
            sb.append(new Formatter().format("%02X-", b));
        }
        return sb.toString();
    }
        
    public void notifyUpdateStart()
    {
        if (listener!=null)
        {
            listener.onDeviceUpdateStart();
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
    
    public void notifyUpdateComplete(String message)
    {
        if (listener!=null)
        {
            listener.onDeviceUpdateFinish(message);
        }
    }
        
    public void notifyUpdateProgress(int action ,ProgressInfo progress)
    {
        if (listener!=null)
        {
            listener.onUpdateProgress(action,progress);
        }
    }
    
    public int getError()
    {
        return errorCode;
    }
    
    public boolean errorHappened()
    {
        return errorHappened;
    }
    
    public void notifyError(int err,DeviceRequest rq)
    {
        errorHappened = true;
        errorCode = err;
        if(listener!=null)
        {
            listener.onDeviceUpdateException(err, rq);
        }
    }
    
    private static void delayInSeconds(int seconds) throws InterruptedException
    {
        Thread.sleep(seconds*1000);
    }

    @Override
    public void onDeviceTimeout(DeviceRequest deviceRequest)
    {
        //connection.reOpenConn();
        notifyError(ActionEvent.ERROR_DEVICE_NO_REPLY,deviceRequest);
    }
    
    class FileScanListener implements FileScanner.Listener
    {
        @Override
        public void onScanStart(File dir)
        {
        }
        @Override
        public void onScanning(File dir)
        {
            notifyActionMessages(ActionEvent.ACTION_CODE_SCANNING_FILES ,"Scanning file for update");
        }

        @Override
        public void onScanFinished(File dir, File[] result)
        {
            FileIntegrityChecker integCheck = new FileIntegrityChecker(result, 
                    new FileIntegrityChecker.Listener()
            {
                @Override
                public void onCheck(final boolean isComplete, final Object reason)
                {
                    if(isComplete)// 文件完整
                    {
                        filesAreComplete = true;
                    }
                }
            });
            integCheck.doCheck();
        }
        
        @Override
        public void onScanFailed(int err, Object reason)
        {   
            notifyError(ActionEvent.ERROR_FILE_NOT_COMPLETE,null);
        }
    }
}

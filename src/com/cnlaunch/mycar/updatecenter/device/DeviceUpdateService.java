package com.cnlaunch.mycar.updatecenter.device;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.cnlaunch.mycar.MyCarApplication;
import com.cnlaunch.mycar.updatecenter.FirmwareUpdate;
import com.cnlaunch.mycar.updatecenter.UpdateCenterConstants;
import com.cnlaunch.mycar.updatecenter.connection.Connection;
import com.cnlaunch.mycar.updatecenter.connection.ConnectionManager;
import com.cnlaunch.mycar.updatecenter.tools.StatisticHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/***
 * 后台运行的设备升级服务
 **/
public class DeviceUpdateService extends Service
{
    String TAG = "DeviceUpdateService";
    Connection connection;  // 连接
    String vehiecle;        // 车型
    String version;         // 版本 
    String language;        // 语言
    String serialNumber;    // 序列号
    
    
    int errorCode;          // 错误码
    boolean errorHappened = false;          // 错误是否发生
    String baseDir;                         // 升级文件的文件夹位置
    boolean filesAreComplete = false;       // 文件是否完整
    ArrayList<File> fileArray = new ArrayList<File>();    // 文件列表
    HashMap<String,String> md5info;                       // MD5检验信息
    boolean ignoreDPUsysiniFile = false;                  // 是否忽略INI文件
    final static int PKG_SIZE = 4*1024;                   // 每一包数据大小   
    byte[] buff = new byte[PKG_SIZE];                     // 每一包数据
    StatisticHelper helper = new StatisticHelper();       // 统计帮助类
    
    
    HashMap<String,Object> Queue = new HashMap<String, Object>();  // 请求队列
    Context context;                                               // 上下文
    DeviceUpdateListener listener;                                 // 升级过程的观察者
    DeviceResponseHandler deviceResponseHandle;                    // 设备响应处理
    MyCarApplication application;                                  // 全局应用对象
    
    /**
     * 需要的命令请求
     * */
    DeviceRequest rq250202; // 高级安全校验 四字节
    DeviceRequest rq2503;   // 安全检验
    DeviceRequest rq2401;   // 准备从端升级命令（车型）
    DeviceRequest rq2402;   // 升级数据文件名
    DeviceRequest rq2403;   // 升级数据文件数据内容
    DeviceRequest rq2404;   // 升级数据文件内容校验数据发送
    DeviceRequest rq2405;   // 完成升级
    DeviceRequest rq2407;   // 启动更新固件
    DeviceRequest rq2408;   // 读取DPU接头车型文件信息
    DeviceRequest rq2105;// 查询 download 版本
    DeviceRequest rq2111;// 跳转到 download.bin 入口
    DeviceRequest rq2112;// 写dpusys.ini配置文件
    DeviceRequest rq2113;// 写dpusys.ini配置文件
    DeviceRequest rq2114;// 查询运行模式 Boot / download.bin
    DeviceRequest rq2110;// 输入密码  000000
    DeviceRequest rq2103;// 查询序列号
	
	@Override
	public void onCreate()
	{
		Log.d(TAG,"Device Update Service is created!");
		super.onCreate();
		
//        connection =  connection = ConnectionManager.getInstance().getConnection(FirmwareUpdate.this, "bluetooth");
//        deviceResponseHandle = devRespHandler;
//        deviceResponseHandle.addListener(this);
//        this.context = ctx;
//        
//        this.vehiecle = params[0];
//        this.version  = params[1];
//        this.language = params[2];
        
        
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent)
	{
		super.onRebind(intent);
	}


	

	@Override
	public IBinder onBind(Intent intent)
	{
		return new ServiceBinder();
	}
	
	public class ServiceBinder extends Binder
	{
		public DeviceUpdateService getService()
		{
			return DeviceUpdateService.this;
		}
	}

}

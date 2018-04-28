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
 * ��̨���е��豸��������
 **/
public class DeviceUpdateService extends Service
{
    String TAG = "DeviceUpdateService";
    Connection connection;  // ����
    String vehiecle;        // ����
    String version;         // �汾 
    String language;        // ����
    String serialNumber;    // ���к�
    
    
    int errorCode;          // ������
    boolean errorHappened = false;          // �����Ƿ���
    String baseDir;                         // �����ļ����ļ���λ��
    boolean filesAreComplete = false;       // �ļ��Ƿ�����
    ArrayList<File> fileArray = new ArrayList<File>();    // �ļ��б�
    HashMap<String,String> md5info;                       // MD5������Ϣ
    boolean ignoreDPUsysiniFile = false;                  // �Ƿ����INI�ļ�
    final static int PKG_SIZE = 4*1024;                   // ÿһ�����ݴ�С   
    byte[] buff = new byte[PKG_SIZE];                     // ÿһ������
    StatisticHelper helper = new StatisticHelper();       // ͳ�ư�����
    
    
    HashMap<String,Object> Queue = new HashMap<String, Object>();  // �������
    Context context;                                               // ������
    DeviceUpdateListener listener;                                 // �������̵Ĺ۲���
    DeviceResponseHandler deviceResponseHandle;                    // �豸��Ӧ����
    MyCarApplication application;                                  // ȫ��Ӧ�ö���
    
    /**
     * ��Ҫ����������
     * */
    DeviceRequest rq250202; // �߼���ȫУ�� ���ֽ�
    DeviceRequest rq2503;   // ��ȫ����
    DeviceRequest rq2401;   // ׼���Ӷ�����������ͣ�
    DeviceRequest rq2402;   // ���������ļ���
    DeviceRequest rq2403;   // ���������ļ���������
    DeviceRequest rq2404;   // ���������ļ�����У�����ݷ���
    DeviceRequest rq2405;   // �������
    DeviceRequest rq2407;   // �������¹̼�
    DeviceRequest rq2408;   // ��ȡDPU��ͷ�����ļ���Ϣ
    DeviceRequest rq2105;// ��ѯ download �汾
    DeviceRequest rq2111;// ��ת�� download.bin ���
    DeviceRequest rq2112;// дdpusys.ini�����ļ�
    DeviceRequest rq2113;// дdpusys.ini�����ļ�
    DeviceRequest rq2114;// ��ѯ����ģʽ Boot / download.bin
    DeviceRequest rq2110;// ��������  000000
    DeviceRequest rq2103;// ��ѯ���к�
	
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

package com.cnlaunch.mycar.crecorder;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.jni.FileUtils;
import com.cnlaunch.mycar.jni.JniX431File;
import com.cnlaunch.mycar.jni.LSX_AUTOINFO;
import com.cnlaunch.mycar.jni.LSX_BASEINFO;
import com.cnlaunch.mycar.jni.LSX_USERINFO;
import com.cnlaunch.mycar.jni.X431Integer;
import com.cnlaunch.mycar.jni.X431String;
/**
 * 当前卡中数据
 * @author luxingsong
 * @author huanglixin 
 * 
 */
public class CRecorderDataLayoutActivity extends Activity {
	boolean D = true;
	//状态结果
	private static final int STAT_READING_ERROR = -1;
	private static final int STAT_READING = 0;
	private static final int STAT_GET_BASEINFO = 1;
	private static final int STAT_GET_AUTOINFO = 2;
	private static final int STAT_GET_USERINFO = 3;
	//文件信息类型
	private static final int BASE_INFO = 0;
	private static final int USER_INFO = 1;
	private static final int AUTO_INFO = 2;
	// JniX431File对象
	private JniX431File _431file = new JniX431File();
	TextView tv_baseinfo;
	TextView tv_userinfo;
	TextView tv_autoinfo;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		// 取消标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 禁止屏幕休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 全屏幕
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        setContentView(R.layout.crecorder_data_layout);
        tv_baseinfo = (TextView)findViewById(R.id.crecorder_tv_baseinfo);
        tv_userinfo = (TextView)findViewById(R.id.crecorder_tv_userinfo);
        tv_autoinfo = (TextView)findViewById(R.id.crecorder_tv_autoinfo);
        
        String x431file = (String)getIntent().getExtras().getString("file");
        get431FileDetailInfo(BASE_INFO,x431file);
        get431FileDetailInfo(USER_INFO,x431file);
        get431FileDetailInfo(AUTO_INFO,x431file);
    }
    /**@author luxingsong
     * 信息界面刷新
     * */
    final Handler handler = new Handler()
    {
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case STAT_READING:
					tv_baseinfo.setText("基本信息读取中...");
					tv_autoinfo.setText("车辆信息读取中...");
					tv_userinfo.setText("用户信息读取中...");
					break;
				case STAT_GET_BASEINFO://基本信息读取
					tv_baseinfo.setText(msg.obj.toString());
					break;
				case STAT_GET_USERINFO://用户信息读取
					tv_userinfo.setText(msg.obj.toString());
					break;
				case STAT_GET_AUTOINFO://车辆信息读取
					tv_autoinfo.setText(msg.obj.toString());
					break;
				case STAT_READING_ERROR://读取错误
					tv_baseinfo.setText(msg.obj.toString());
					tv_autoinfo.setText(msg.obj.toString());
					tv_userinfo.setText(msg.obj.toString());
					break;
				default:
					break;
			}
		}
    };
    /**读取基本信息
     * */
    private void get431FileDetailInfo(final int infoType,final String file)
    {
    	new Thread()
    	{
    		public void run()
    		{
    			Log.e("crd","Read Base Info Thread Begin....");
    			handler.obtainMessage(STAT_READING).sendToTarget();
    			StringBuilder sb = new StringBuilder();
    			String ret = "";
    			int hlsx = _431file.lsx_init();
    			if(hlsx != -1)
    			{
    				try {
						sleep(1000);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
    				String strFileName = FileUtils.sdCardGetDirectoryPath()
    						+ File.separator + "crecorder/data/"+file;
    				//文件存在
    				if(FileUtils.isFileExist(strFileName))
    				{
    					X431String filename = new X431String(strFileName);
    					X431Integer error = new X431Integer(10);
    					// 打开文件
    					int lsx_file = _431file.lsx_open(hlsx, filename, JniX431File.MODE_READ,error);
    					if(lsx_file == 0)//无法打开文件
    					{
    						_431file.lsx_close(lsx_file);
    						_431file.lsx_deinit(hlsx);
    						ret ="无法打开X431文件!";
    						handler.obtainMessage(STAT_READING_ERROR,ret).sendToTarget();
    						return;
    					}
    					int iRet = -1;//文件操作返回结果
    					switch(infoType)//431信息类型
    					{
    						case BASE_INFO:
    							LSX_BASEINFO baseinfo = new LSX_BASEINFO();
    							Log.e("crd","读取基本信息  开始");
    							iRet = _431file.lsx_read_baseinfo(lsx_file, baseinfo);
    							if(iRet==0)
    							{
    								sb.append("数据文件名称: "+file);sb.append("\n");
    								sb.append("序列号: "+(baseinfo.serialno==null?"":baseinfo.serialno));sb.append("\n");
    								sb.append("诊断版本: "+(baseinfo.diagversion==null?"":baseinfo.diagversion));sb.append("\n");
    								sb.append("产品ID： "+baseinfo.productid);sb.append("\n");
    								sb.append("解码页数: "+baseinfo.codepage);sb.append("\n");
    								sb.append("语言类型: "+baseinfo.langcode);sb.append("\n");
    								sb.append("创建时间: "+(baseinfo.creationtime==null?"":baseinfo.creationtime));sb.append("\n");
    								ret = sb.toString();
    								handler.obtainMessage(STAT_GET_BASEINFO,ret).sendToTarget();
    								Log.e("crd","读取基本信息 OK ："+ret);
    								return;
    							}
    							break;
    						case USER_INFO:
    							LSX_USERINFO userinfo = new LSX_USERINFO();
    							iRet = _431file.lsx_read_userinfo(lsx_file, userinfo);
    							if(iRet==0)
    							{
    								sb.append("用户名: "+(userinfo.name));sb.append("\n");
    								sb.append("许可证: "+(userinfo.license));sb.append("\n");
    								sb.append("联系电话： "+userinfo.phone);sb.append("\n");
    								ret = sb.toString();
    								handler.obtainMessage(STAT_GET_USERINFO,ret).sendToTarget();
    								
    							}
    							break;
    						case AUTO_INFO:
    							LSX_AUTOINFO autoinfo = new LSX_AUTOINFO();
    							iRet = _431file.lsx_read_autoinfo(lsx_file, autoinfo);
    							if(iRet==0)
    							{
    								sb.append("底盘: "+(autoinfo.chassis==null?"":autoinfo.chassis));sb.append("\n");
    								sb.append("排量: "+(autoinfo.displacement==null?"":autoinfo.displacement));sb.append("\n");
    								sb.append("发动机模型： "+autoinfo.enginemodel);sb.append("\n");
    								sb.append("制造产地: "+autoinfo.madein);sb.append("\n");
    								sb.append("VIN: "+autoinfo.vin);sb.append("\n");
    								sb.append("出厂年份： "+(autoinfo.year==null?"":autoinfo.year));sb.append("\n");
    								ret = sb.toString();
    								handler.obtainMessage(STAT_GET_AUTOINFO,ret).sendToTarget();
    							}
    							break;
    						default:
    							break;
    					}//end of switch 
    					_431file.lsx_close(lsx_file);
    					_431file.lsx_deinit(hlsx);
    				}else//文件不存在
    				{
    					if(D)Log.e("crd","x431文件不存在!");
    					handler.obtainMessage(STAT_READING_ERROR,"x431文件不存在!").sendToTarget();
    				}
    			}else//文件读取错误
    			{
    				_431file.lsx_deinit(hlsx);
    				handler.obtainMessage(STAT_READING_ERROR,"x431文件读取错误!").sendToTarget();
    			}
    		}//end of run()
    	}.start();
    }
}
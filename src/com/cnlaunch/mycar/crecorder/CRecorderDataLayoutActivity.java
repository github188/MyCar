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
 * ��ǰ��������
 * @author luxingsong
 * @author huanglixin 
 * 
 */
public class CRecorderDataLayoutActivity extends Activity {
	boolean D = true;
	//״̬���
	private static final int STAT_READING_ERROR = -1;
	private static final int STAT_READING = 0;
	private static final int STAT_GET_BASEINFO = 1;
	private static final int STAT_GET_AUTOINFO = 2;
	private static final int STAT_GET_USERINFO = 3;
	//�ļ���Ϣ����
	private static final int BASE_INFO = 0;
	private static final int USER_INFO = 1;
	private static final int AUTO_INFO = 2;
	// JniX431File����
	private JniX431File _431file = new JniX431File();
	TextView tv_baseinfo;
	TextView tv_userinfo;
	TextView tv_autoinfo;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		// ȡ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��ֹ��Ļ����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// ȫ��Ļ
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
     * ��Ϣ����ˢ��
     * */
    final Handler handler = new Handler()
    {
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case STAT_READING:
					tv_baseinfo.setText("������Ϣ��ȡ��...");
					tv_autoinfo.setText("������Ϣ��ȡ��...");
					tv_userinfo.setText("�û���Ϣ��ȡ��...");
					break;
				case STAT_GET_BASEINFO://������Ϣ��ȡ
					tv_baseinfo.setText(msg.obj.toString());
					break;
				case STAT_GET_USERINFO://�û���Ϣ��ȡ
					tv_userinfo.setText(msg.obj.toString());
					break;
				case STAT_GET_AUTOINFO://������Ϣ��ȡ
					tv_autoinfo.setText(msg.obj.toString());
					break;
				case STAT_READING_ERROR://��ȡ����
					tv_baseinfo.setText(msg.obj.toString());
					tv_autoinfo.setText(msg.obj.toString());
					tv_userinfo.setText(msg.obj.toString());
					break;
				default:
					break;
			}
		}
    };
    /**��ȡ������Ϣ
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
    				//�ļ�����
    				if(FileUtils.isFileExist(strFileName))
    				{
    					X431String filename = new X431String(strFileName);
    					X431Integer error = new X431Integer(10);
    					// ���ļ�
    					int lsx_file = _431file.lsx_open(hlsx, filename, JniX431File.MODE_READ,error);
    					if(lsx_file == 0)//�޷����ļ�
    					{
    						_431file.lsx_close(lsx_file);
    						_431file.lsx_deinit(hlsx);
    						ret ="�޷���X431�ļ�!";
    						handler.obtainMessage(STAT_READING_ERROR,ret).sendToTarget();
    						return;
    					}
    					int iRet = -1;//�ļ��������ؽ��
    					switch(infoType)//431��Ϣ����
    					{
    						case BASE_INFO:
    							LSX_BASEINFO baseinfo = new LSX_BASEINFO();
    							Log.e("crd","��ȡ������Ϣ  ��ʼ");
    							iRet = _431file.lsx_read_baseinfo(lsx_file, baseinfo);
    							if(iRet==0)
    							{
    								sb.append("�����ļ�����: "+file);sb.append("\n");
    								sb.append("���к�: "+(baseinfo.serialno==null?"":baseinfo.serialno));sb.append("\n");
    								sb.append("��ϰ汾: "+(baseinfo.diagversion==null?"":baseinfo.diagversion));sb.append("\n");
    								sb.append("��ƷID�� "+baseinfo.productid);sb.append("\n");
    								sb.append("����ҳ��: "+baseinfo.codepage);sb.append("\n");
    								sb.append("��������: "+baseinfo.langcode);sb.append("\n");
    								sb.append("����ʱ��: "+(baseinfo.creationtime==null?"":baseinfo.creationtime));sb.append("\n");
    								ret = sb.toString();
    								handler.obtainMessage(STAT_GET_BASEINFO,ret).sendToTarget();
    								Log.e("crd","��ȡ������Ϣ OK ��"+ret);
    								return;
    							}
    							break;
    						case USER_INFO:
    							LSX_USERINFO userinfo = new LSX_USERINFO();
    							iRet = _431file.lsx_read_userinfo(lsx_file, userinfo);
    							if(iRet==0)
    							{
    								sb.append("�û���: "+(userinfo.name));sb.append("\n");
    								sb.append("���֤: "+(userinfo.license));sb.append("\n");
    								sb.append("��ϵ�绰�� "+userinfo.phone);sb.append("\n");
    								ret = sb.toString();
    								handler.obtainMessage(STAT_GET_USERINFO,ret).sendToTarget();
    								
    							}
    							break;
    						case AUTO_INFO:
    							LSX_AUTOINFO autoinfo = new LSX_AUTOINFO();
    							iRet = _431file.lsx_read_autoinfo(lsx_file, autoinfo);
    							if(iRet==0)
    							{
    								sb.append("����: "+(autoinfo.chassis==null?"":autoinfo.chassis));sb.append("\n");
    								sb.append("����: "+(autoinfo.displacement==null?"":autoinfo.displacement));sb.append("\n");
    								sb.append("������ģ�ͣ� "+autoinfo.enginemodel);sb.append("\n");
    								sb.append("�������: "+autoinfo.madein);sb.append("\n");
    								sb.append("VIN: "+autoinfo.vin);sb.append("\n");
    								sb.append("������ݣ� "+(autoinfo.year==null?"":autoinfo.year));sb.append("\n");
    								ret = sb.toString();
    								handler.obtainMessage(STAT_GET_AUTOINFO,ret).sendToTarget();
    							}
    							break;
    						default:
    							break;
    					}//end of switch 
    					_431file.lsx_close(lsx_file);
    					_431file.lsx_deinit(hlsx);
    				}else//�ļ�������
    				{
    					if(D)Log.e("crd","x431�ļ�������!");
    					handler.obtainMessage(STAT_READING_ERROR,"x431�ļ�������!").sendToTarget();
    				}
    			}else//�ļ���ȡ����
    			{
    				_431file.lsx_deinit(hlsx);
    				handler.obtainMessage(STAT_READING_ERROR,"x431�ļ���ȡ����!").sendToTarget();
    			}
    		}//end of run()
    	}.start();
    }
}
package com.cnlaunch.mycar.diagnose.formal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.DiagAlertDialog;
import com.cnlaunch.mycar.diagnose.domain.DiagnoseBaseActivity;
import com.cnlaunch.mycar.diagnose.service.DiagnoseDataService;

public class DiagnoseDataStreamShowGraphActivity extends DiagnoseBaseActivity implements OnGestureListener,BluetoothInterface{
	Context context = DiagnoseDataStreamShowGraphActivity.this;
	private static final String TAG = "DiagnoseDataStreamShowGraphActivity";
    private static final boolean D = true;
    //滑动变量
  	private GestureDetector detector; 
  	//初始化消息
  	public static final int DRAW_INIT_INDIVALTE	= 1; //初始化绘图对象
  	public static final int DRAW_INDIVALTE	= 2;	 //正常刷新图像
  	//定义按钮
  	private Button m_but_return = null;
  	private Button m_but_up = null;
  	private Button m_but_down = null;
  	//定义绘图线程
  	private DrawThread m_drawthread = null;
  	//定义是否开始绘图
  	private boolean m_boolondraw = false;	//打开绘图标志，为true的时候才可以绘图
  	private boolean m_boolupdateview = false; //为true的时候绘图一次，然后置状态为为false
  	//定义绘图界面
  	private SurfaceView mydrawview;
  	private SurfaceHolder sfhandler; 
  //-----------------------绘图相关数据------------------------------------
  	//绘图缓冲区 
  	public  List<Map<String, Object>> ListData; //接收到的数据流数据
  	String m_show_name = "测试数据流";
  	List<ArrayList<Float>> m_list = new ArrayList<ArrayList<Float>>();
  	private int m_graph_num = 0; //支持图像显示的数据流条数
  	private int[] m_graph_ID_list = null;
  	private int m_nowItem = 0; //当前数据流ITEM 
	//初始化蓝牙服务
	private BluetoothDataService m_blue_service = null;
	//诊断协议服务
	private DiagnoseDataService m_diag_service = null;
	//数据bundle
	Bundle m_bundle = null;
  	//定时器
  	private Timer m_timer = new Timer();
  	//-----------------------绘图相关数据------------------------------------
  	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mydrawview myview = new Mydrawview(this);
        setContentView(R.layout.diagnose_formal_datastream_showgraph);
        //屏幕滑动变量初始化
        detector = new GestureDetector(this);
        //接收初始化数据
        m_bundle = getIntent().getExtras();
		ListData = (List<Map<String, Object>>) m_bundle.get("DATASTREAM");
		if(ListData == null)
		{
			if(D) Log.i(TAG,"获取bundler失败!");
		}
		else
		{
			if(InitGraphList() == true)
			{
				if(D) Log.i(TAG,"数据初始化成功!");
			}
			else //初始化失败
			{
				final DiagAlertDialog dlg = new DiagAlertDialog(this);
				dlg.setTitle(R.string.datastream_graph_tip_title);
				dlg.setMessage(R.string.datastream_graph_tip_message);
				dlg.setPositiveButton(R.string.back_pre, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//关闭当前界面
						finishemyself();
						dlg.dismiss();
					}
				});
				dlg.setCancelable(false);
				dlg.show();
			}
		}
        //初始化返回按钮
        m_but_return = (Button)findViewById(R.id.drawdatastream_showgraph_but_return);
        m_but_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishemyself();
			}
		});
        //初始化上一页按钮
        m_but_up = (Button)findViewById(R.id.drawdatastream_showgraph_but_up);
        m_but_up.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(m_nowItem > 0)
				{
					m_nowItem --;
					m_boolupdateview = true;
				}
				else
				{
					Toast.makeText(context, context.getString(R.string.datastream_graph_first_page), Toast.LENGTH_SHORT).show();
				}
			}
		});
        //初始化下一页按钮
        m_but_down = (Button)findViewById(R.id.drawdatastream_showgraph_but_down);
        m_but_down.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Drawgraph(1);
				if(m_nowItem < m_graph_num - 1)
				{
					m_nowItem ++;
					m_boolupdateview = true;
				}
				else
				{
					Toast.makeText(context, context.getString(R.string.datastream_graph_last_page), Toast.LENGTH_SHORT).show();
				}
			}
		});
        mydrawview = (SurfaceView) this.findViewById(R.id.drawdatastream_paint);  
        sfhandler = mydrawview.getHolder();
        sfhandler.addCallback(new MyCallBack());
        mydrawview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float x = event.getX();   
	            float y = event.getY();
	            int action = event.getAction();
	            //Toast.makeText(GraphDemoActivity.this, "屏幕点击事件:" + action, Toast.LENGTH_LONG).show();
	            if(D) Log.i(TAG,"屏幕点击事件:" + action);
				return false;
			}
		
		});
        //启动绘图线程
        m_drawthread = new DrawThread();
        m_drawthread.start();
        if(D) Log.i(TAG,"onCreate");
		//引用蓝牙服务
		m_blue_service = BluetoothDataService.getInstance();
		m_blue_service.AddObserver(this);
		//出事后诊断服务
		m_diag_service = DiagnoseDataService.getInstance();
    }
  	//注册绘图回调方法
  	class MyCallBack implements SurfaceHolder.Callback{

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			Log.i("Surface:", "Change");
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.i("Surface:", "Created");
			//Drawgraph();
			m_boolondraw = true;
			//m_boolupdateview = true;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.i("Surface:", "Destroyed");
			m_boolondraw = false;
		}
    	
    }
  	//横竖屏切换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    	{
    		if(D) Log.i(TAG,"当前为横屏");
    	}
    	else
    	{
    		if(D) Log.i(TAG,"当前为竖屏");
    	}
    }
    @Override
    protected void onStart() 
    {
    	// TODO Auto-generated method stub
    	super.onStart();
    	//获取坐标
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    private class DrawThread extends Thread{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		while(!this.isInterrupted())
    		{
    			if(m_boolondraw && m_boolupdateview == true) //绘图
    			{
    				//if(D) Log.i(TAG,"线程绘图~~~~~~~~~~~");
    				Drawgraph();
    			}
    			else
    			{
    				try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    		if(D) Log.i(TAG,"线程结束");
    	}
    }
    //解析数据方法,初始化数据流数据
    private boolean InitGraphList()
    {
    	boolean v_iRet = false;
    	float v_get = 0; //获取List数据
    	String v_getstr = null;
    	int i = 0;
    	m_graph_ID_list = new int[ListData.size()];
    	m_graph_num = 0;
    	for(i = 0; i < ListData.size(); i ++)
    	{
    		v_getstr = ListData.get(i).get("DATASTREAM_VALUE").toString();
    		try
			{
    			v_get = Float.parseFloat(v_getstr);
			}
			catch (NumberFormatException e) 
			{
				// TODO: handle exception
				if(D) Log.e(TAG,"转换出错,num="+ i +";" + e.toString());
				continue;
			}	
    		m_list.add(new ArrayList<Float>());
    		m_graph_ID_list[m_graph_num] = i;
    		m_graph_num ++;
    		if(D) Log.i(TAG,"data=" + v_get + ", i = " + i);
    	}
    	m_boolupdateview = true;
    	if(m_graph_num > 0)
    		v_iRet = true;
    	return v_iRet;
    }
  //绘图方法
    void Drawgraph()
    {
    	Canvas canvas = sfhandler.lockCanvas();// 获取画布  
    	if(canvas == null) return; //申请不成功返回
    	//if(D) Log.i(TAG,"x=" + canvas.getWidth() + ",y=" + canvas.getHeight());
    	canvas.drawColor(Color.BLACK);
    	Rect rect = new Rect(10,10,canvas.getWidth() - 10,canvas.getHeight() - 10);
        Paint paint = new Paint();  
        paint.setAntiAlias(true);
        //绘制边框
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
        canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
        canvas.drawLine(rect.right, rect.bottom, rect.left, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.top, paint);
        //绘制格子---------------------------
        //定义虚线
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        paint.setPathEffect(effects);
        int i;
        int v_pos = 0;
        for(i = 1; i < 6; i ++) //画横虚线
        {
        	v_pos = rect.top + rect.height() * i / 6;
        	canvas.drawLine(rect.left, v_pos, rect.right, v_pos, paint);
        }
        for(i = 1; i < 6; i ++) //画竖虚线
        {
        	v_pos = rect.left + rect.width() * i / 6;
        	canvas.drawLine(v_pos, rect.top, v_pos, rect.bottom, paint);
        }
        //判断是否有数据可画
        if(m_graph_num <= 0)
        {
        	sfhandler.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像  
            //绘图完成置状态位
            m_boolupdateview = false;
            return;
        }
        //文本输出字体
        paint.reset();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setTextSize(rect.width() / 25);
        //计算数据量 ----------------------------------------
        int v_datalen = m_list.get(0).size();
        //数据太少不画图
        if(v_datalen < 2)
        {
        	sfhandler.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像  
            //绘图完成置状态位
            m_boolupdateview = false;
            return;
        }
        //取得最大最小值
        if(m_list.size() < m_nowItem + 1) //无列表的时候不刷新
        	return;
        float v_max = 0,v_min = 0,v_temp = 0;
        v_min = m_list.get(m_nowItem).get(0); 	//赋初值
        v_max =  v_min;							//赋初值
        for(i = 0; i < m_list.get(m_nowItem).size(); i ++)
        {
        	v_temp = m_list.get(m_nowItem).get(i);
        	if(D) Log.e(TAG,"now= " + m_nowItem + "| i = " + i + ",data= " + v_temp);
        	if(v_max < v_temp)
        		v_max = v_temp;
        	if(v_min > v_temp)
        		v_min = v_temp;
        }
        float v_diff = v_max - v_min;  //差值
        //文本输出位置
        float[] pos = new float[2];
        pos[0] = rect.left + 5;
        pos[1] = rect.top + rect.height() / 20;
        if(D) Log.i(TAG,"pos= " + pos[0] + "," + pos[1]);
        int v_nowID = m_graph_ID_list[m_nowItem];
        if(D) Log.i(TAG,"m_nowItem= " + m_nowItem + ", v_nowID= " + v_nowID);
        //if(D) Log.i(TAG,"DATA=" + ListData.get(v_nowID).get("dataStreamInfo").toString());
        //写数据流文本 ,获取当前显示的数据流ID 
        canvas.drawText(ListData.get(v_nowID).get("DATASTREAM_NAME").toString() + ":" + 
        		m_list.get(m_nowItem).get(m_list.get(m_nowItem).size() - 1).toString()
        		, pos[0],pos[1], paint); 
        //绘制刻度值
        if(v_diff == 0) //只写一个刻度
        {
        	pos[0] = rect.left;
        	pos[1] = rect.top + rect.height() / 2;
        	canvas.drawText(String.valueOf(v_max).toString()
            		, pos[0],pos[1], paint); 
        }
        else //标准刻度,画3个刻度
        {
        	//画上面的刻度
        	pos[0] = rect.left;
        	pos[1] = rect.top + rect.height() / 6;
        	canvas.drawText(String.valueOf(v_max).toString()
            		, pos[0],pos[1], paint);
        	//画中间刻度
        	pos[0] = rect.left;
        	pos[1] = rect.top + rect.height() / 2;
        	canvas.drawText(String.valueOf(v_max - v_diff / 2).toString()
            		, pos[0],pos[1], paint); 
        	//画下面的刻度
        	pos[0] = rect.left;
        	pos[1] = rect.bottom - rect.height() / 6;
        	canvas.drawText(String.valueOf(v_min).toString()
            		, pos[0],pos[1], paint); 
        }
        //绘制波形
        paint.reset();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.GREEN);
        //绘制波形的上下起点
        int v_top = rect.top + rect.height() / 6;
        int v_bottom = rect.bottom - rect.height() / 6;
        int v_high = v_bottom - v_top; //高度
        int x1,x2,y1,y2; //坐标点
        x2 = rect.left;
        if(v_diff == 0) //过滤掉直线显示
        	y2 = rect.top + rect.height() / 2;
        else
        	y2 = (int) (v_bottom - (m_list.get(m_nowItem).get(0) - v_min) * v_high / v_diff);
        for(i = 1; i < v_datalen; i ++)
        {
        	x1 = x2;
        	y1 = y2;
        	x2 = rect.left + rect.width() * i / v_datalen;
        	if(v_diff == 0)
            	y2 = rect.top + rect.height() / 2;
            else
            	y2 = (int) (v_bottom - (m_list.get(m_nowItem).get(i) - v_min) * v_high / v_diff);
        	canvas.drawLine(x1, y1, x2, y2, paint);
        }
        
	    // 从资源文件中生成位图      	    
        sfhandler.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像  
        //绘图完成置状态位
        m_boolupdateview = false;
    }
    //清除绘图区域
    void Cleargraph()
    {
    	Canvas canvas = sfhandler.lockCanvas(null);  
        canvas.drawColor(Color.BLACK);// 清除画布  
        sfhandler.unlockCanvasAndPost(canvas);  
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(D) Log.i(TAG,"onDestroy");
    	m_blue_service.DelObserver(this);
    	//绘图置状态位
        m_boolupdateview = false;
        //m_boodstopdrawthread = false;
        m_drawthread.interrupt();
        //关定时器
        m_timer.cancel();
    }
    private void finishemyself()
    {
    	Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("DATASTREAM", (ArrayList<? extends Parcelable>)ListData);
		Intent intent = new Intent(context, DiagnoseDataStreamShowActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	 if(D) Log.i(TAG,"事件：onTouchEvent");
    	//return super.onTouchEvent(event);
    	 return this.detector.onTouchEvent(event);
    }
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		 if(D) Log.i(TAG,"事件：onDown");
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"事件：onFling,e1x=" + e1.getX() + ",e2x=" + e2.getX());
		if(D) Log.i(TAG,"事件：onFling,e1y=" + e1.getY() + ",e2y=" + e2.getY());
		
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"事件：onLongPress");
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"事件：onScroll");
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"事件：onShowPress");
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"事件：onSingleTapUp");
		return false;
	}
	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void BlueConnected(String name, String mac) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {
		// TODO Auto-generated method stub
		byte[] v_recv_buf = new byte[datalen - 8];
		int v_recv_len = m_diag_service.GetDataFromBluetooth(databuf, datalen, v_recv_buf);
		String v_show = BluetoothDataService.bytesToHexString(v_recv_buf,v_recv_len);
		if(D) Log.i(TAG,"SHOW：" + v_show);
		if(v_recv_buf[0] == DiagnoseDataService.CMD_SHOW)
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDATASTREAM) //数据流显示
			{
				m_diag_service.GetShowDatastreamgraph(m_bundle, v_recv_buf, v_recv_len);
				UpdateShowGraphData();
			}
		}
	}
	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		
	}
	private void UpdateShowGraphData()
	{
		if(D) Log.i(TAG,"收到数据流刷新广播消息!");
		List<Map<String, Object>> Updata = (List<Map<String, Object>>) m_bundle.get("DATASTREAM");
		String v_data = null;
		float v_get = 0;
		for(int i = 0; i < m_graph_num; i ++)
		{
			v_data = Updata.get(m_graph_ID_list[i]).get("DATASTREAM_VALUE").toString();
			try
			{
    			v_get = Float.parseFloat(v_data);
			}
			catch (NumberFormatException e) 
			{
				// TODO: handle exception
				if(D) Log.e(TAG,"转换出错,num="+ i +"-->置为0");
				v_get = 0;
			}
			//把转换结果添加到列表
			//if(D) Log.e(TAG,"转换：i=" + i + "|list=" + m_graph_ID_list[i] +
			//		"->Data:S=" + v_data + ";F= " + v_get);
			m_list.get(i).add(v_get);
		}
		if(m_boolupdateview == false) //如果刷新完成就继续刷新，不然就只更新数据等待
			m_boolupdateview = true;
	}
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}

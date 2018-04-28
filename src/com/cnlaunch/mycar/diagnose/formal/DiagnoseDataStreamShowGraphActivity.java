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
    //��������
  	private GestureDetector detector; 
  	//��ʼ����Ϣ
  	public static final int DRAW_INIT_INDIVALTE	= 1; //��ʼ����ͼ����
  	public static final int DRAW_INDIVALTE	= 2;	 //����ˢ��ͼ��
  	//���尴ť
  	private Button m_but_return = null;
  	private Button m_but_up = null;
  	private Button m_but_down = null;
  	//�����ͼ�߳�
  	private DrawThread m_drawthread = null;
  	//�����Ƿ�ʼ��ͼ
  	private boolean m_boolondraw = false;	//�򿪻�ͼ��־��Ϊtrue��ʱ��ſ��Ի�ͼ
  	private boolean m_boolupdateview = false; //Ϊtrue��ʱ���ͼһ�Σ�Ȼ����״̬ΪΪfalse
  	//�����ͼ����
  	private SurfaceView mydrawview;
  	private SurfaceHolder sfhandler; 
  //-----------------------��ͼ�������------------------------------------
  	//��ͼ������ 
  	public  List<Map<String, Object>> ListData; //���յ�������������
  	String m_show_name = "����������";
  	List<ArrayList<Float>> m_list = new ArrayList<ArrayList<Float>>();
  	private int m_graph_num = 0; //֧��ͼ����ʾ������������
  	private int[] m_graph_ID_list = null;
  	private int m_nowItem = 0; //��ǰ������ITEM 
	//��ʼ����������
	private BluetoothDataService m_blue_service = null;
	//���Э�����
	private DiagnoseDataService m_diag_service = null;
	//����bundle
	Bundle m_bundle = null;
  	//��ʱ��
  	private Timer m_timer = new Timer();
  	//-----------------------��ͼ�������------------------------------------
  	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mydrawview myview = new Mydrawview(this);
        setContentView(R.layout.diagnose_formal_datastream_showgraph);
        //��Ļ����������ʼ��
        detector = new GestureDetector(this);
        //���ճ�ʼ������
        m_bundle = getIntent().getExtras();
		ListData = (List<Map<String, Object>>) m_bundle.get("DATASTREAM");
		if(ListData == null)
		{
			if(D) Log.i(TAG,"��ȡbundlerʧ��!");
		}
		else
		{
			if(InitGraphList() == true)
			{
				if(D) Log.i(TAG,"���ݳ�ʼ���ɹ�!");
			}
			else //��ʼ��ʧ��
			{
				final DiagAlertDialog dlg = new DiagAlertDialog(this);
				dlg.setTitle(R.string.datastream_graph_tip_title);
				dlg.setMessage(R.string.datastream_graph_tip_message);
				dlg.setPositiveButton(R.string.back_pre, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//�رյ�ǰ����
						finishemyself();
						dlg.dismiss();
					}
				});
				dlg.setCancelable(false);
				dlg.show();
			}
		}
        //��ʼ�����ذ�ť
        m_but_return = (Button)findViewById(R.id.drawdatastream_showgraph_but_return);
        m_but_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishemyself();
			}
		});
        //��ʼ����һҳ��ť
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
        //��ʼ����һҳ��ť
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
	            //Toast.makeText(GraphDemoActivity.this, "��Ļ����¼�:" + action, Toast.LENGTH_LONG).show();
	            if(D) Log.i(TAG,"��Ļ����¼�:" + action);
				return false;
			}
		
		});
        //������ͼ�߳�
        m_drawthread = new DrawThread();
        m_drawthread.start();
        if(D) Log.i(TAG,"onCreate");
		//������������
		m_blue_service = BluetoothDataService.getInstance();
		m_blue_service.AddObserver(this);
		//���º���Ϸ���
		m_diag_service = DiagnoseDataService.getInstance();
    }
  	//ע���ͼ�ص�����
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
  	//�������л�
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    	if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    	{
    		if(D) Log.i(TAG,"��ǰΪ����");
    	}
    	else
    	{
    		if(D) Log.i(TAG,"��ǰΪ����");
    	}
    }
    @Override
    protected void onStart() 
    {
    	// TODO Auto-generated method stub
    	super.onStart();
    	//��ȡ����
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
    			if(m_boolondraw && m_boolupdateview == true) //��ͼ
    			{
    				//if(D) Log.i(TAG,"�̻߳�ͼ~~~~~~~~~~~");
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
    		if(D) Log.i(TAG,"�߳̽���");
    	}
    }
    //�������ݷ���,��ʼ������������
    private boolean InitGraphList()
    {
    	boolean v_iRet = false;
    	float v_get = 0; //��ȡList����
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
				if(D) Log.e(TAG,"ת������,num="+ i +";" + e.toString());
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
  //��ͼ����
    void Drawgraph()
    {
    	Canvas canvas = sfhandler.lockCanvas();// ��ȡ����  
    	if(canvas == null) return; //���벻�ɹ�����
    	//if(D) Log.i(TAG,"x=" + canvas.getWidth() + ",y=" + canvas.getHeight());
    	canvas.drawColor(Color.BLACK);
    	Rect rect = new Rect(10,10,canvas.getWidth() - 10,canvas.getHeight() - 10);
        Paint paint = new Paint();  
        paint.setAntiAlias(true);
        //���Ʊ߿�
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
        canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, paint);
        canvas.drawLine(rect.right, rect.bottom, rect.left, rect.bottom, paint);
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.top, paint);
        //���Ƹ���---------------------------
        //��������
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        paint.setPathEffect(effects);
        int i;
        int v_pos = 0;
        for(i = 1; i < 6; i ++) //��������
        {
        	v_pos = rect.top + rect.height() * i / 6;
        	canvas.drawLine(rect.left, v_pos, rect.right, v_pos, paint);
        }
        for(i = 1; i < 6; i ++) //��������
        {
        	v_pos = rect.left + rect.width() * i / 6;
        	canvas.drawLine(v_pos, rect.top, v_pos, rect.bottom, paint);
        }
        //�ж��Ƿ������ݿɻ�
        if(m_graph_num <= 0)
        {
        	sfhandler.unlockCanvasAndPost(canvas);// �����������ύ���õ�ͼ��  
            //��ͼ�����״̬λ
            m_boolupdateview = false;
            return;
        }
        //�ı��������
        paint.reset();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setTextSize(rect.width() / 25);
        //���������� ----------------------------------------
        int v_datalen = m_list.get(0).size();
        //����̫�ٲ���ͼ
        if(v_datalen < 2)
        {
        	sfhandler.unlockCanvasAndPost(canvas);// �����������ύ���õ�ͼ��  
            //��ͼ�����״̬λ
            m_boolupdateview = false;
            return;
        }
        //ȡ�������Сֵ
        if(m_list.size() < m_nowItem + 1) //���б��ʱ��ˢ��
        	return;
        float v_max = 0,v_min = 0,v_temp = 0;
        v_min = m_list.get(m_nowItem).get(0); 	//����ֵ
        v_max =  v_min;							//����ֵ
        for(i = 0; i < m_list.get(m_nowItem).size(); i ++)
        {
        	v_temp = m_list.get(m_nowItem).get(i);
        	if(D) Log.e(TAG,"now= " + m_nowItem + "| i = " + i + ",data= " + v_temp);
        	if(v_max < v_temp)
        		v_max = v_temp;
        	if(v_min > v_temp)
        		v_min = v_temp;
        }
        float v_diff = v_max - v_min;  //��ֵ
        //�ı����λ��
        float[] pos = new float[2];
        pos[0] = rect.left + 5;
        pos[1] = rect.top + rect.height() / 20;
        if(D) Log.i(TAG,"pos= " + pos[0] + "," + pos[1]);
        int v_nowID = m_graph_ID_list[m_nowItem];
        if(D) Log.i(TAG,"m_nowItem= " + m_nowItem + ", v_nowID= " + v_nowID);
        //if(D) Log.i(TAG,"DATA=" + ListData.get(v_nowID).get("dataStreamInfo").toString());
        //д�������ı� ,��ȡ��ǰ��ʾ��������ID 
        canvas.drawText(ListData.get(v_nowID).get("DATASTREAM_NAME").toString() + ":" + 
        		m_list.get(m_nowItem).get(m_list.get(m_nowItem).size() - 1).toString()
        		, pos[0],pos[1], paint); 
        //���ƿ̶�ֵ
        if(v_diff == 0) //ֻдһ���̶�
        {
        	pos[0] = rect.left;
        	pos[1] = rect.top + rect.height() / 2;
        	canvas.drawText(String.valueOf(v_max).toString()
            		, pos[0],pos[1], paint); 
        }
        else //��׼�̶�,��3���̶�
        {
        	//������Ŀ̶�
        	pos[0] = rect.left;
        	pos[1] = rect.top + rect.height() / 6;
        	canvas.drawText(String.valueOf(v_max).toString()
            		, pos[0],pos[1], paint);
        	//���м�̶�
        	pos[0] = rect.left;
        	pos[1] = rect.top + rect.height() / 2;
        	canvas.drawText(String.valueOf(v_max - v_diff / 2).toString()
            		, pos[0],pos[1], paint); 
        	//������Ŀ̶�
        	pos[0] = rect.left;
        	pos[1] = rect.bottom - rect.height() / 6;
        	canvas.drawText(String.valueOf(v_min).toString()
            		, pos[0],pos[1], paint); 
        }
        //���Ʋ���
        paint.reset();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.GREEN);
        //���Ʋ��ε��������
        int v_top = rect.top + rect.height() / 6;
        int v_bottom = rect.bottom - rect.height() / 6;
        int v_high = v_bottom - v_top; //�߶�
        int x1,x2,y1,y2; //�����
        x2 = rect.left;
        if(v_diff == 0) //���˵�ֱ����ʾ
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
        
	    // ����Դ�ļ�������λͼ      	    
        sfhandler.unlockCanvasAndPost(canvas);// �����������ύ���õ�ͼ��  
        //��ͼ�����״̬λ
        m_boolupdateview = false;
    }
    //�����ͼ����
    void Cleargraph()
    {
    	Canvas canvas = sfhandler.lockCanvas(null);  
        canvas.drawColor(Color.BLACK);// �������  
        sfhandler.unlockCanvasAndPost(canvas);  
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(D) Log.i(TAG,"onDestroy");
    	m_blue_service.DelObserver(this);
    	//��ͼ��״̬λ
        m_boolupdateview = false;
        //m_boodstopdrawthread = false;
        m_drawthread.interrupt();
        //�ض�ʱ��
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
    	 if(D) Log.i(TAG,"�¼���onTouchEvent");
    	//return super.onTouchEvent(event);
    	 return this.detector.onTouchEvent(event);
    }
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		 if(D) Log.i(TAG,"�¼���onDown");
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"�¼���onFling,e1x=" + e1.getX() + ",e2x=" + e2.getX());
		if(D) Log.i(TAG,"�¼���onFling,e1y=" + e1.getY() + ",e2y=" + e2.getY());
		
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"�¼���onLongPress");
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"�¼���onScroll");
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"�¼���onShowPress");
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"�¼���onSingleTapUp");
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
		if(D) Log.i(TAG,"SHOW��" + v_show);
		if(v_recv_buf[0] == DiagnoseDataService.CMD_SHOW)
		{
			if(v_recv_buf[1] == DiagnoseDataService.CMD_SHOW_GETDATASTREAM) //��������ʾ
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
		if(D) Log.i(TAG,"�յ�������ˢ�¹㲥��Ϣ!");
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
				if(D) Log.e(TAG,"ת������,num="+ i +"-->��Ϊ0");
				v_get = 0;
			}
			//��ת�������ӵ��б�
			//if(D) Log.e(TAG,"ת����i=" + i + "|list=" + m_graph_ID_list[i] +
			//		"->Data:S=" + v_data + ";F= " + v_get);
			m_list.get(i).add(v_get);
		}
		if(m_boolupdateview == false) //���ˢ����ɾͼ���ˢ�£���Ȼ��ֻ�������ݵȴ�
			m_boolupdateview = true;
	}
	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		
	}
}

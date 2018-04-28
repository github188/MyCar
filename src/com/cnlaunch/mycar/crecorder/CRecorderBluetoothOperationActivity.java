package com.cnlaunch.mycar.crecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnlaunch.mycar.R;

/**@author luxingsong
 * CRecorder的蓝牙服务操作类
 * 涉及到的主要操作是：从诊断卡读取诊断数据文件
 * */
public class CRecorderBluetoothOperationActivity extends Activity
{
	RelativeLayout root_layout;
	LinearLayout   bottom_layout;
	Dialog dlg;
	Button bt_import;
	Button bt_see_detail;
	Button bt_cancel;
	ListView list;//用来显示文件列表
	ReadThread readerThread;
	final String[] progressTitle = new String[]
	{
			"数据传输中.",
			"数据传输中..",
			"数据传输中...",
			"数据传输中.....",
			"数据接收完毕!"
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crecorder_bt_dialog);
		initViews();
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		if(readerThread.done ==false)
		{
			readerThread.done = true;
		}
		Intent data = new Intent();
		File f = new File("v2.x431");
		data.putExtra("return",f);
		setResult(0,data);
		super.onDestroy();
	}
	public void initViews()
	{
		root_layout = (RelativeLayout)findViewById(R.id.crecorder_bt_root_layout);
		bt_import =(Button)findViewById(R.id.crecorder_import);
		bt_see_detail =(Button)findViewById(R.id.crecorder_see_detail);
		bt_cancel =(Button)findViewById(R.id.crecorder_cancel);
		
		bt_import.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				readerThread.done = true;
				Log.e("crd","文件导入");
				Intent data = new Intent();
				File f = new File("v2.x431");
				data.putExtra("return",f);
				CRecorderBluetoothOperationActivity.this.setResult(0,data);
				CRecorderBluetoothOperationActivity.this.finish();
			}
		});
		
		bt_see_detail.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CRecorderBluetoothOperationActivity.this.setTitle("查看详细数据");
				dlg = new AlertDialog.Builder(CRecorderBluetoothOperationActivity.this)
					.setTitle("详细数据：")
					.setMessage("123456789.x431")
					.create();
//				dlg.show();
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CRecorderBluetoothOperationActivity.this.finish();
			}
		});
		
		list = (ListView)findViewById(R.id.crecorder_lv_bt_data);
		list.setAdapter(new BtAdapter(this));
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// TODO Auto-generated method stub
				RowView row = (RowView)list
						.getAdapter()
						.getItem(position);
				Log.e("crd","read data item at"+position);
			}
		});
		BtAdapter adp = (BtAdapter)list.getAdapter();
		adp.addItem("=========诊断数据文件=========");
		readerThread = new ReadThread();
		readerThread.start();
	}
	/**
	 * UI界面更新
	 * */
	private final Handler handler = new Handler(){
		public void handleMessage(Message msg)
		{
			BtAdapter adp = (BtAdapter)list.getAdapter();
			adp.addItem(msg.obj.toString());
//			int off = root_layout.getMeasuredHeight() -list.getHeight();
//			list.scrollTo(0,-50);
//			Log.e("crd","root_height:"+root_layout.getMeasuredHeight());
//			Log.e("crd","list_height:"+list.getHeight());
//			Log.e("crd","list_measured_height:"+list.getMeasuredHeight());
//			Log.e("crd","last visible position:"+list.getLastVisiblePosition());
			switch(msg.what)
			{
				case ReadThread.LOADING:
					CRecorderBluetoothOperationActivity
			             .this.setTitle(progressTitle[msg.arg1%4]);
					break;
				case ReadThread.FINISH:
					CRecorderBluetoothOperationActivity
		             	.this.setTitle(progressTitle[progressTitle.length-1]);
					break;
				default:
					break;
			}
		}
	};
	
	/**@author luxingsong
	 * 蓝牙的数据读取线程，通过这个线程和诊断卡通信
	 * 获取x431的文件信息
	 **/
	private class ReadThread extends Thread
	{
		private final static int LOADING = 1;
		private final static int FINISH = 0;
		public boolean done=false;
		private int i = 0;
		public void run()
		{
			while(!done)
			{
				//蓝牙通信，读取数据
				try 
				{
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("crd","Read thread run...");
				Date d = new Date();
				handler.obtainMessage(LOADING,i,i,String.valueOf(i)+d.toGMTString()+".x431").sendToTarget();
				if(i++>15)
				{
					done = true;
					handler.obtainMessage(FINISH,i,i,String.valueOf(i)+d.toGMTString()+".x431").sendToTarget();
				}
			}
		}
	}
	
	private class BtAdapter extends BaseAdapter
	{
		Context cont;
		ArrayList<RowView> data = new ArrayList<RowView>();	
		public BtAdapter(Context c)
		{
			this.cont = c;
		}
		public void addItem(String file)
		{
			RowView row = new RowView();
			row.file = file;
			data.add(row);
			notifyDataSetChanged();
		}
		
		public void deleteAllItem()
		{
			data.clear();
			notifyDataSetChanged();
		}
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder vhd;
			if(convertView == null)
			{
				LayoutInflater inf  = LayoutInflater.from(cont);
				convertView = inf.inflate(R.layout.crecorder_bt_list_item, null);
				vhd = new ViewHolder();
				vhd.tv = (TextView) convertView.findViewById(R.id.crecorder_bt_file);
				convertView.setTag(vhd);
			}else
			{
				vhd = (ViewHolder) convertView.getTag();
			}
			vhd.tv.setText(data.get(position).file);
			return convertView;
		}
	}
	static class ViewHolder
	{
		TextView tv;
	}
	class RowView
	{
		String file;
	}
}

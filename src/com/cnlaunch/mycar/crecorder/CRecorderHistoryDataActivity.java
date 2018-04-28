package com.cnlaunch.mycar.crecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;

public class CRecorderHistoryDataActivity extends Activity {
	Button  bt_history;
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crecorder_data_layout);
//		Log.i("历史数据:","456");
		ListView crecorder_history_lv=(ListView) findViewById(R.id.crecorder_lv);
		List<String> list=new ArrayList<String>();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
		try
		{
				File sdCardDir = Environment
						.getExternalStorageDirectory();// 获取SDCard目录
				
				File fileName=new File("mycar.x431");
				if(fileName.exists()){
					FileInputStream fileInputStream = new FileInputStream("mycar.x431");
					byte [] buffer = new byte[100];
					fileInputStream.read(buffer);
					
					String s = new String(buffer);
					String[] sa=new String[100];
					sa=s.split("\\|");
					for(int i=0;i<sa.length;i++)
					{
						list.add(sa[i]);
					}
				}
				else{
				File readFile = new File(sdCardDir, "mycar.x431");
				FileInputStream fileInputStream = new FileInputStream(readFile);
				byte [] buffer = new byte[1024];
				fileInputStream.read(buffer);
				String s = new String(buffer);
				String[] sa;
//				Log.e("历史数据:","123");
				sa=s.split("\\|");
				for(int i=0;i<sa.length;i++)
				{
					list.add(sa[i]);
//					Log.e("历史数据:",sa[i]);
				}
			}
			}
		
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
	}
	else
	{
		Toast.makeText(CRecorderHistoryDataActivity.this, "请插入sd卡", Toast.LENGTH_LONG).show();
	}
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		crecorder_history_lv.setAdapter(adapter);
	}
}
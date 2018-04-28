package com.cnlaunch.mycar.crecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;

public  class CRecorderReadDataActivity extends BaseActivity 
{
    File sdPath= Environment
			.getExternalStorageDirectory();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crecorder_data_layout,R.layout.custom_title);

        ListView crecorder_lv=(ListView)findViewById(R.id.crecorder_lv);
        ArrayList<String> list=new ArrayList<String>(); 
        File dir=new File(sdPath+"/data");
        dir.mkdir();
        File newFile = new File(dir+"/data.db");
        try {
			newFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
//        try{
//        	byte[] buffer=new byte[100];
//        	FileInputStream fis=new FileInputStream(newFile);
//        	fis.read(buffer);
//        	fis.close();
//        	String data=new String(buffer);
//        	String[] s=data.split("\n");
//        	Log.e("历史数据:","1");
//        	String car=null;
//        	String val=null;
//        	for(int i=0;i<s.length;i++)
//        	{
//        	String[] sa=s[i].split("=");
//        	car=new String(sa[0]);
//        	val=new String(sa[1]);
//        	list.add(sa[0]);
//        	System.out.println(car);
//        	}
//        	Log.e("历史数据:","3");
//        }
//        catch(IOException e)
//        {
//        	e.printStackTrace();
//        }
    	Log.e("历史数据:","123");
        String data="launch|bwm|benz|cherry";
        String[] s=data.split("\\|");
        for(int i=0;i<s.length;i++)
        {
        	list.add(s[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
               (this,android.R.layout.simple_expandable_list_item_1,list);        
		crecorder_lv.setAdapter(adapter);
		
		//数据存储
        if (Environment.getExternalStorageState().equals(
        		Environment.MEDIA_MOUNTED)) {
        try {
//				writeFile.setDateSource("mycar.x431");
				if(newFile.exists()){
				FileOutputStream outStream = new FileOutputStream(
						newFile);
				outStream.write(data.getBytes());
				outStream.close();
				Toast.makeText(CRecorderReadDataActivity.this, "存储成功", Toast.LENGTH_LONG)
						.show();
				Log.e("历史数据:","123");
			
			}
			
			else{
				
				newFile.createNewFile();
				FileOutputStream outStream = new FileOutputStream(
						newFile);
				outStream.write(data.getBytes());
				outStream.close();
				Toast.makeText(CRecorderReadDataActivity.this, "存储成功", Toast.LENGTH_SHORT)
						.show();
				
			}
				
//			Log.e("历史数据:","123");
		}
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        }
        else
        {
        	Toast.makeText(CRecorderReadDataActivity.this, "请插入sd卡", Toast.LENGTH_LONG)
        	.show();
        }      
    }
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		// TODO Auto-generated method stub
//		Toast.makeText(this, "1", Toast.LENGTH_SHORT).show(); 
//		
//	}
}
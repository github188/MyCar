package com.cnlaunch.mycar.crecorder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cnlaunch.mycar.R;
/**
 * crecorder通过蓝牙获取数据
 * 
 * @author huanglixin
 * 
 */
public class CRecorderDataFromDPU extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView lv1=new ListView(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getData());
        lv1.setAdapter(adapter);
        lv1.setOnItemLongClickListener(new OnItemClickListener());
        setContentView(lv1);}
        private List<String> getData(){
        List<String> data  = new ArrayList<String>();
        data.add("toyota-k6-2011-2-85.x431");
        data.add("BMW-x6-2008-7-15.x431");
        data.add("CHERRY-2011-8-10.x431");
        data.add("CHERRY-2011-3-10.x431");
        data.add("CHERRY-2011-4-10.x431");
        data.add("CHERRY-2011-9-10.x431");
        return data;
    }
        
    private class OnItemClickListener implements OnItemLongClickListener{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			Toast.makeText(CRecorderDataFromDPU.this, "你点击了第"+arg2+"项", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}

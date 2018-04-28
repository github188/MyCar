package com.cnlaunch.mycar.updatecenter.onekeydiag.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomAlertDialog;
// 弹出对话框,类似SPINNER控件的效果
public class PopupSelectDialog
{
	private final static String TAG = "PopupSelectDialog";
	Context context;
	Activity activity;
	Dialog dlg;
	GroupSelectionChangeListener listener;
	int groupPosition;
	
	public PopupSelectDialog(int pos,Activity act,final ChoiceGroup group,GroupSelectionChangeListener l)
	{
		this.context = act;
		this.activity = act;
		this.listener = l;
		this.groupPosition = pos;
		
		final CustomAlertDialog dlg = new CustomAlertDialog(activity);
		dlg.setTitle(group.getType()); // 组类型
		ListView lv = new ListView(context);
		lv.setDividerHeight(1);
		lv.setFooterDividersEnabled(true);
		lv.setScrollingCacheEnabled(false);
		dlg.setView(lv);
		lv.setAdapter(new CustomAdapter(context, group.getItems()));
		
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				int indexOld = group.getCurrentIndex();
				if(position != indexOld)
				{
					group.setIndex(position);
					if(listener!=null)
					{
						listener.onSelectionChanged(group,groupPosition, indexOld, position);						
					}
				}
				dlg.dismiss();
			}
		});
		
		dlg.setNegativeButton(R.string.upc_cancel, new OnClickListener()
		{
			public void onClick(View v)
			{
				dlg.dismiss();
			}
		});
		dlg.show();			
	}
	
	private class CustomAdapter extends BaseAdapter 
	{
		ArrayList<String> data;
		Context cont;
		LayoutInflater inf;
		
		@SuppressWarnings("unused")
		public CustomAdapter(Context c,ArrayList<String> data)
		{
			cont = c;
			inf = LayoutInflater.from(cont);
			this.data = data;
		}
		
		public CustomAdapter(Context c,String[] data)
		{
			cont = c;
			inf = LayoutInflater.from(cont);
			this.data = new ArrayList<String>();
			
			if(data ==null)
			{
				Log.e(TAG ," items[] == null");
				return;
			}
			for (int i = 0; i < data.length; i++)
			{
				this.data.add(data[i]);
			}
		}
		
		@Override
		public int getCount()
		{
			return data.size();
		}
		
		
		@Override
		public Object getItem(int position)
		{
			return data.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = inf.inflate(R.layout.popup_list_item, null);
			TextView tv = (TextView)convertView.findViewById(R.id.select_item);
			tv.setText(data.get(position));
			return convertView;
		}
	 }
}

package com.cnlaunch.mycar.diagnose.simplereport.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cnlaunch.mycar.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class DiagnoseSimpleDataInfoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	List<HashMap<String, String>> listItems;
	private ListView detailList;
	private Map<Integer,Integer> layoutMap=null;
	public DiagnoseSimpleDataInfoAdapter(Context context,
			List<HashMap<String, String>> list) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.listItems = list;	
		layoutMap=new  HashMap<Integer,Integer>();
	}
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		convertView=null;
		final int num=position;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.diagnose_simple_report_detail, null);
			holder.data_stream_name=(TextView)convertView.findViewById(R.id.sp_detail_data_stream_name);
			holder.data_stream_value=(TextView)convertView.findViewById(R.id.sp_detail_data_stream_value);
			holder.simple_report_maxvalue=(TextView)convertView.findViewById(R.id.sp_detail_simple_report_maxvalue);
			holder.simple_report_minvalue=(TextView)convertView.findViewById(R.id.sp_detail_simple_report_minvalue);
			holder.cause_result_info=(TextView)convertView.findViewById(R.id.simple_report_cause_result_info);
			holder.help_advise_info=(TextView)convertView.findViewById(R.id.simple_report_help_advise_info);
			holder.layout = (LinearLayout)convertView.findViewById(R.id.simple_report_cause_help);//线性布局方式		
			holder.entryLayout = (LinearLayout)convertView.findViewById(R.id.sp_detail_data_entry);//线性布局方式		
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> instance=listItems.get(position);
		holder.data_stream_name.setText(instance.get("dataStreamName"));
		holder.data_stream_value.setText(instance.get("dataStreamValue"));
		holder.simple_report_maxvalue.setText(instance.get("maxvalue"));
		holder.simple_report_minvalue.setText(instance.get("minvalue"));
		holder.cause_result_info.setText(instance.get("causeResult"));
		holder.help_advise_info.setText(instance.get("helpAdvice"));
		holder.layout.setId(position);		
		if(layoutMap.containsKey(num)){
			int type=layoutMap.get(num);
			holder.layout.setVisibility(type);
		}
		
		holder.entryLayout.setOnClickListener(new View.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            		if(layoutMap.containsKey(num)){
            			int type=layoutMap.get(num);
            			if(type==View.VISIBLE){
            				holder.layout.setVisibility(View.GONE);
            				type=View.GONE;
            			}
            			else{            				
            				holder.layout.setVisibility(View.VISIBLE);
            				type=View.VISIBLE;

            			}
            			layoutMap.put(num,type);
            			 
            		}else{
            			holder.layout.setVisibility(View.VISIBLE);
                		layoutMap.put(num, View.VISIBLE);

            		}
             
            }  
  
		});
		return convertView;
	}
	class ViewHolder {
		 TextView data_stream_name;
		 TextView data_stream_value;
		 TextView simple_report_maxvalue;
		 TextView simple_report_minvalue;
		 TextView cause_result_info;
		 TextView help_advise_info;
		 LinearLayout layout = null; 
		 LinearLayout entryLayout = null; 
    }

}

package com.cnlaunch.mycar.updatecenter;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
/**@author luxingsong
 * 用户本地的升级设置数据
 * */
public class UpdateCenterSettingsActivity extends BaseActivity 
{
	 private ListView lvOption;//选项列表
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_center_settings,R.layout.custom_title);
        setCustomeTitleLeft(R.string.upc_settings);
        setCustomeTitleRight("");
        initViews();
    }
    
    /**
	  * @author luxingsong
	  * 初始化控件
	  * */
	 private void initViews()
	 {
		 ArrayList<RowOption> options = new ArrayList<RowOption>();
		 //在这里添加选项  id[int]  resID[int]  key[String]  value[boolean]
//		 options.add(new RowOption(0,R.string.upc_setting_auto_update,UpdateCenterConstants.AUTO_UPDATE, false));
//		 options.add(new RowOption(1,R.string.upc_setting_auto_commit_log,UpdateCenterConstants.AUTO_COMMIT_LOG, false));
		 options.add(new RowOption(2,R.string.upc_not_show_device_update_guide,UpdateCenterConstants.DEVICE_ACTIVATE_GUIDE_NOT_SHOW_AGAIN, false));
		 
		 lvOption = (ListView)findViewById(R.id.upc_lv_settings_option);
		 // 注意这个顺序
		 CustomAdapter adapter  = new CustomAdapter(this,options);
		 SharePrefDataStore dataStore = new SharePrefDataStore(this);
		 adapter.setDataStore(dataStore);
		 adapter.initDataFromDataStore();//初始化数据库的数据
		 lvOption.setAdapter(adapter);			 
		 //用户点击设置事件处理
		 lvOption.setOnItemClickListener(new OnItemClickListener()
		 {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				CustomAdapter adp = (CustomAdapter)lvOption.getAdapter();
				adp.toggleState(position);//状态切换
			}
		 });
	 }
	 
	 private class CustomAdapter extends BaseAdapter
	 {
		ArrayList<RowOption> data;
		Context cont;
		LayoutInflater inf;
		DataStore  dataStoreImpl;//数据持久化接口
		public CustomAdapter(Context c,ArrayList<RowOption> data)
		{
			cont = c;
			inf = LayoutInflater.from(cont);
			this.data = data;
		}
		public void setDataStore(DataStore  ds)
		{
			dataStoreImpl = ds;
		}
		public void initDataFromDataStore()
		{
			if(dataStoreImpl!=null)
			{
				int len = this.getCount();
				for(int i=0;i<len;i++)
				{
					String key = data.get(i).getKey();//根据关键字，从持久化中恢复数据
					data.get(i).setChecked(dataStoreImpl.getBool(key));
				}
			}
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
		
		/**选项状态切换
		 * */
		public void toggleState(int pos)
		{
			boolean val;
			if(data.get(pos).isChecked())//old value is true
			{
				val = false;//so ,change to false
			}else
			{
				val = true;
			}
			data.get(pos).setChecked(val);
			notifyDataSetChanged();//更新视图
			//持久化更新数据
			if(dataStoreImpl!=null)
				dataStoreImpl.setBool(data.get(pos).getKey(), val);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			if(convertView == null)
			{
				convertView = inf.inflate(R.layout.update_center_setting_option_item, null);
			}
			CheckedTextView ctv = (CheckedTextView)convertView.findViewById(R.id.upc_setting_checktext);
			ctv.setText(data.get(position).getNameResId());
			if(data.get(position).isChecked())
			{
				ctv.setChecked(true);
			}
			else
			{
				ctv.setChecked(false);
			}
			return convertView;
		}
	 }
	 
	 /**选项
	  * */
	 class RowOption
	 {
		int id;
		int nameResId;//选项名称资源ID
		String key;//用来数据库持久化
		boolean isChecked;
		public String getKey()
		{
			return key;
		}
		public void setKey(String key)
		{
			this.key = key;
		}
		
		/**id  每一个项的ID
		 * resId  文字的资源ID
		 * key    持久化的关键字
		 * isChecked 状态数据
		 * */
		public RowOption(int id, int resId, String key,boolean isChecked)
		{
			this.id = id;
			this.nameResId = resId;
			this.key = key;
			this.isChecked = isChecked;
		}
		
		public int getId()
		{
			return id;
		}
		public void setId(int id)
		{
			this.id = id;
		}
		public int getNameResId()
		{
			return nameResId;
		}
		public void setNameResId(int resId)
		{
			this.nameResId = resId;
		}
		public boolean isChecked()
		{
			return isChecked;
		}
		public void setChecked(boolean isChecked)
		{
			this.isChecked = isChecked;
		}
	 }
	 
	 /**@author luxingsong
	  * 是否自动升级
	  * */
	 public boolean isAutoUpdate()
	 {
		 return  getSharedPreferences(UpdateCenterConstants.SHARE_PREF_UPDATE_SETTINGS, 0)
				 			.getBoolean(UpdateCenterConstants.AUTO_UPDATE, false);
	 }
	 
	 /**@author luxingsong
	  * 是否自动提交日志
	  * */
	 public boolean isAutoCommitLog()
	 {
		 return  getSharedPreferences(UpdateCenterConstants.SHARE_PREF_UPDATE_SETTINGS, 0)
				 			.getBoolean(UpdateCenterConstants.AUTO_COMMIT_LOG, false);
	 }
	 
	 /**状态数据的存取接口
	  * 可能通过sharedpref,
	  * 也可以通过sqlite3或者文件保存这些状态数据
	  * */
	 private interface DataStore
	 {
		 public void setBool(String key,boolean value);
		 public boolean getBool(String key);
		 public Map<String,?> getAll();
	 }
	 
	 // 数据持久化
	 private class SharePrefDataStore implements DataStore
	 {
		SharedPreferences shpref;
		Context cont;
		public SharePrefDataStore(Context c)
		{
            cont = c;
			if(cont!=null)
			{
				this.shpref = getSharedPreferences(UpdateCenterConstants.SHARE_PREF_UPDATE_SETTINGS, 0);
			}
		}
		@Override
		public void setBool(String key, boolean value)
		{
			// TODO Auto-generated method stub
			if(shpref!=null)
				shpref.edit().putBoolean(key, value).commit();
		}
		@Override
		public boolean getBool(String key)
		{
			if(shpref!=null)
				return shpref.getBoolean(key,false);
			return false;
		}
	    public Map<String,?> getAll()
	    {
	    	if(shpref!=null)
	    		return shpref.getAll();
	    	return null;
	    }
	 }
}
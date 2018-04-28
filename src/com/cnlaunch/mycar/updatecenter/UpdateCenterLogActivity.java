package com.cnlaunch.mycar.updatecenter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.BaseActivity;
/**
 * @author luxingsong
 * 升级日志管理类
 * */
public class UpdateCenterLogActivity extends BaseActivity 
{
	 private static final String tag = "UpdateCenterLog";
	 private static final String filePath="/update/log";
	 private ListView mLogList;/*日志列表UI控件*/
	 
	 public void onCreate(Bundle savedInstanceState)
	 {
	        super.onCreate(savedInstanceState);
			setContentView(R.layout.update_center_logs, R.layout.custom_title);
			setCustomeTitleLeft("升级日志");
			setCustomeTitleRight("");
	        initViews();
	 }
	 
	 private void initViews()
	 {
		 mLogList = (ListView)findViewById(R.id.update_center_log_list);
		 ListAdapter adp = new ListAdapter(this);
		 mLogList.setAdapter(adp);
		 adp.addItem(new LogItem("客户端升级V1.0.2",new Date().toLocaleString()));
		 adp.addItem(new LogItem("客户端升级V1.0.3",new Date().toLocaleString()));
		 adp.addItem(new LogItem("客户端升级V1.0.4",new Date().toLocaleString()));
		 adp.addItem(new LogItem("客户端升级V1.0.5",new Date().toLocaleString()));
	 }
	 
	 /**
	  * 日志的操作菜单
	  * @author luxingsong
	  * */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		menu.add(1, 1, 1, "同步升级日志");
		return super.onCreateOptionsMenu(menu);
	}
	 
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
			case 1:
				//同步服务器的日志到本
				break;
			default:
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	/**
	  * @author luxingsong
	  * @param logFile 日志文件名称
	  * 从日志文件中加载数据，用于显示
	  * */
	 private void loadLogData(String logFile){
		 
	 }
	 /**@author luxingsong
	  * list 的适配器类，用于自定义列表的显示
	  * */
	 private class ListAdapter extends BaseAdapter{
		private ArrayList<LogItem> mData = new ArrayList<LogItem>();
		private Context context;
		public ListAdapter(Context cont)
		{
			context = cont;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}
		public void addItem(LogItem item)
		{
			mData.add(item);
			notifyDataSetChanged();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			// TODO Auto-generated method stub
			if(convertView==null){
				convertView = LayoutInflater.from(context).inflate(R.layout.update_center_log_item,null);
			}
			TextView content =(TextView)convertView.findViewById(R.id.update_center_log_item_content);
			TextView timestamp = (TextView)convertView.findViewById(R.id.update_center_log_item_timestamp);
			content.setText(mData.get(position).getContent());//升级内容
			timestamp.setText(mData.get(position).getTimeStamp());//时间戳
			return convertView;
		}
	 }
	 private class LogItem
	 {
		 HashMap<String,Object> data = new HashMap<String,Object>();
		 public LogItem(String t,String c){
			 data.put("timestamp", t);
			 data.put("content", c);
		 }
		 public String getTimeStamp(){
			 return (String)data.get("timestamp");
		 }
		 public String getContent(){
			 return (String)data.get("content");
		 }
	 }
	 private final String xmlns = "cnlaunch";
	 private final String rootTag = "LogList";
	 private final String subTag = "item";
	 /**
	  * @author luxingsong
	  * 生成一个xml字符串，写到文件系统中去
	  * */
	 public String writeXMLogFile(ArrayList<LogItem> items)
	 {
		 XmlSerializer sel= Xml.newSerializer();
		 StringWriter writer = new StringWriter();
		 try {
			sel.setOutput(writer);
			sel.startDocument("UTF-8", true);//设置字符集
			sel.startTag(xmlns,"LogList");//起始标签
			for(LogItem i : items)
			{
				sel.startTag(xmlns, subTag);//item 标签开始
				sel.attribute(xmlns,"timestamp", i.getTimeStamp());
				sel.attribute(xmlns, "content", i.getContent());
				sel.endTag(xmlns, subTag);//item 标签结束
			}
			sel.endTag(xmlns,"LogList");
			sel.endDocument();
			return writer.toString();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return null; 
	 }
	 /**
	  * @author luxingsong
	  * 从xml文件中加载日志记录最后给列表显示
	  * 如果不存在本地日志，则询问用户是否从网络同步
	  * */
	 public LogItem[] loadLogFromXMLFile(File aFile){
		 int size = 100;
		 LogItem[] array = new LogItem[size];
		return array;
	 }
	 /**
	  * @author luxingsong
	  * 向服务器提交升级日志
	  * */
	 public void commitLogFileToServer(){
		 
	 }

}

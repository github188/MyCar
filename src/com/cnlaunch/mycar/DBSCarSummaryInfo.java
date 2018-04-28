package com.cnlaunch.mycar;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;

/**
 * 
 * <功能简述> 主要用于在首页注册车云网的摘要信息，入口处为两个TreeMap，一个保存显示信息，另一个保存点击该信息时
 * 的事件监听。
 * <功能详细描述>
 * @author xiangyuanmao
 * @version 1.0 2012-5-18
 * @since DBS V100
 */
public class DBSCarSummaryInfo
{
    private static final String TAG = "DBSCarSummaryInfo";
    private static final boolean D = true;
    private Context context;
    public DBSCarSummaryInfo(Context context)
    {
        this.context = context;
    }
    /*
     * 显示消息Map
     */
    private static Map<String,DBSCarInfo> notifyMap = new TreeMap<String,DBSCarInfo>();
    
    /*
     * 
     */
    private static Map<String,IDBSCarObserve> lisenrMap = new TreeMap<String,IDBSCarObserve>();
    
    public static ArrayList<DBSCarInfo> notifyList = new ArrayList<DBSCarInfo>();
    public static ArrayList<IDBSCarObserve> lisenerList = new ArrayList<IDBSCarObserve>();
    
    public interface IDBSCarObserve
    {
        public void execute();
    }
    
    public void register(String key, DBSCarInfo dbsCarInfo, IDBSCarObserve dbsCarObserve)
    {
        if (key == null || dbsCarInfo == null || dbsCarObserve == null)
        {
            return;
        }
        notifyMap.put(key, dbsCarInfo);
        lisenrMap.put(key, dbsCarObserve);
        set();
        Log.d(TAG, "车云网摘要 after Register " + notifyList.toString());
    }
    public void unRegister(String key)
    {
        
        if (notifyMap.containsKey(key))
        {
            Log.d(TAG, "车云网摘要 after unRegister fail..." + key);
            notifyMap.remove(key);
            lisenrMap.remove(key);
            set();
        }
        Log.d(TAG, "车云网摘要 after unRegister " + notifyList.toString());
    }
    
    public void set()
    {
        notifyList.clear();
        lisenerList.clear();
        Set keySet = notifyMap.keySet();
        for (Object object : keySet)
        {
            notifyList.add(notifyMap.get(object));
            lisenerList.add(lisenrMap.get(object));
            Log.d(TAG, "车云网摘要" + ((DBSCarInfo)notifyMap.get(object)).count);
        }

        Intent intent = new Intent(Constants.MAIN_DBSCAR_SUMMARY);
        intent.putExtra("message", "收到车云网摘要信息");
        this.context.sendBroadcast(intent);
    }
}

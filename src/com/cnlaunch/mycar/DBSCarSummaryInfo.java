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
 * <���ܼ���> ��Ҫ��������ҳע�ᳵ������ժҪ��Ϣ����ڴ�Ϊ����TreeMap��һ��������ʾ��Ϣ����һ������������Ϣʱ
 * ���¼�������
 * <������ϸ����>
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
     * ��ʾ��ϢMap
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
        Log.d(TAG, "������ժҪ after Register " + notifyList.toString());
    }
    public void unRegister(String key)
    {
        
        if (notifyMap.containsKey(key))
        {
            Log.d(TAG, "������ժҪ after unRegister fail..." + key);
            notifyMap.remove(key);
            lisenrMap.remove(key);
            set();
        }
        Log.d(TAG, "������ժҪ after unRegister " + notifyList.toString());
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
            Log.d(TAG, "������ժҪ" + ((DBSCarInfo)notifyMap.get(object)).count);
        }

        Intent intent = new Intent(Constants.MAIN_DBSCAR_SUMMARY);
        intent.putExtra("message", "�յ�������ժҪ��Ϣ");
        this.context.sendBroadcast(intent);
    }
}

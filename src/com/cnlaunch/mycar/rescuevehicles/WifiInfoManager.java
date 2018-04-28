package com.cnlaunch.mycar.rescuevehicles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 
 * <功能简述> Wifi信息管理
 * <功能详细描述> 获取当前可用的Wifi信息
 * @author xiangyuanmao
 * @version 1.0 2012-5-22
 * @since DBS V100
 */
public class WifiInfoManager
{
    private WifiManager wifiManager; // WIFI管理

    /**
     * 构造器 类初始化时调用的构造器
     * @param paramContext
     * @since DBS V100
     */
    public WifiInfoManager(Context paramContext)
    {

        this.wifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);

    }

    /**
     * 把当前连接的动态Wifi信息和检测到的wifi信息转储到一个列表里面
     * @return
     * @since DBS V100
     */
    public List<WifiInfo> dump()
    {

        if (!this.wifiManager.isWifiEnabled())
        {

            return new ArrayList<WifiInfo>();

        }

        // 建立一个WIFI连接，返回的是当前动态连接的Wifi网络
        android.net.wifi.WifiInfo wifiConnection = this.wifiManager.getConnectionInfo();

        // 当前的WIFI信息
        WifiInfo currentWIFI = null;

        // 如果连接不为空
        if (wifiConnection != null)
        {

            // 当前访问点的基本访问集合标识符,是一个六字节的MAC地址,形如（XX:XX:XX:XX:XX:XX）
            String s = wifiConnection.getBSSID();

            // 当前连接的信号强度
            int i = wifiConnection.getRssi();

            /*
             * 从当前的802.11网络上获取服务集合标识符，如果该标识符是ASCII型的字符串，其结果
             * 将被放到双引号中，别的情况是一个十六进制字符串。如果当前无网络连接就返回null
             */
            String s1 = wifiConnection.getSSID();

            // 实例化一个WIFI信息对象
            currentWIFI = new WifiInfo(s, i, s1);

        }

        // 初始化一个WIFI信息集合
        ArrayList<WifiInfo> lsAllWIFI = new ArrayList<WifiInfo>();

        // 添加当前WIFI信息到集合
        if (currentWIFI != null)
        {

            lsAllWIFI.add(currentWIFI);

        }

        // 得到一个检测到的WIFI信息集合
        List<ScanResult> lsScanResult = this.wifiManager.getScanResults();

        // 添加到WIFI信息集合
        for (ScanResult result : lsScanResult)
        {

            WifiInfo scanWIFI = new WifiInfo(result);

            if (!scanWIFI.equals(currentWIFI))

                lsAllWIFI.add(scanWIFI);

        }

        return lsAllWIFI;

    }

    /**
     * 返回WIFI网络是否可用
     * @return
     * @since DBS V100
     */
    public boolean isWifiEnabled()
    {

        return this.wifiManager.isWifiEnabled();

    }

    /**
     * 返回Json格式的Wifi网络信息
     * @return Json格式的Wifi网络信息
     * @since DBS V100
     */
    public JSONArray wifiInfo()
    {

        JSONArray jsonArray = new JSONArray();

        for (WifiInfo wifi : dump())
        {

            JSONObject localJSONObject = wifi.info();

            jsonArray.put(localJSONObject);

        }

        return jsonArray;

    }

    /**
     * 返回WIFI管理对象
     * @return WIFI管理对象
     * @since DBS V100
     */
    public WifiManager wifiManager()
    {

        return this.wifiManager;

    }

    /**
     * 返回一个JSONArray对象，里面存放的是JSON对象格式的WIFI信息
     * @return JSON对象格式的WIFI信息
     * @since DBS V100
     */
    public JSONArray wifiTowers()
    {

        // 实例化一个JSONArray对象
        JSONArray jsonArray = new JSONArray();

        try
        {

            // 实例化一个当前检测到的WIFI信息迭代器
            Iterator<WifiInfo> localObject = dump().iterator();

            while (true)
            {

                // 当迭代到最后一个元素时返回
                if (!(localObject).hasNext())
                {

                    return jsonArray;

                }

                jsonArray.put(localObject.next().wifi_tower());

            }

        }
        catch (Exception localException)
        {

            Log.e("location", localException.getMessage());

        }

        return jsonArray;

    }

    /**
     * 
     * <功能简述> 描述WIFI信息，包括mac地址、信号强度、服务集合标识符
     * <功能详细描述>
     * @author xiangyuanmao
     * @version 1.0 2012-5-22
     * @since DBS V100
     */
    public class WifiInfo implements Comparable<WifiInfo>
    {

        /**
         * 实现接口的compareTo方法,主要比较信号强度
         * @param wifiinfo WIFI信息
         * @return 0：信号相同；正数表示大于，负数表示小于
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         * @since DBS V100
         */
        public int compareTo(WifiInfo wifiinfo)
        {

            int i = wifiinfo.dBm;

            int j = dBm;

            return i - j;

        }

        /**
         * 复写equals方法
         * @param obj 被比较的对象
         * @return
         * @see java.lang.Object#equals(java.lang.Object)
         * @since DBS V100
         */
        public boolean equals(Object obj)
        {

            boolean flag = false;

            if (obj == this)
            {

                flag = true;

                return flag;

            }
            else
            {

                if (obj instanceof WifiInfo)
                {

                    WifiInfo wifiinfo = (WifiInfo) obj;

                    int i = wifiinfo.dBm;

                    int j = dBm;

                    if (i == j)
                    {

                        String s = wifiinfo.bssid;

                        String s1 = bssid;

                        if (s.equals(s1))
                        {

                            flag = true;

                            return flag;

                        }

                    }

                    flag = false;

                }
                else
                {

                    flag = false;

                }

            }

            return flag;

        }

        /**
         * 复写hashCode方法
         * @return 对象的hash值
         * @see java.lang.Object#hashCode()
         * @since DBS V100
         */
        public int hashCode()
        {

            int i = dBm;

            int j = bssid.hashCode();

            return i ^ j;

        }

        /**
         * 组装一个关于WIFI的JSON对象
         * @return JSON格式的对象
         * @since DBS V100
         */
        public JSONObject info()
        {

            JSONObject jsonobject = new JSONObject();

            try
            {

                String s = bssid;

                jsonobject.put("mac", s);

                String s1 = ssid;

                jsonobject.put("ssid", s1);

                int i = dBm;

                jsonobject.put("dbm", i);

            }
            catch (Exception ex)
            {

            }

            return jsonobject;

        }

        /**
         * 组装JSON对象
         * @return JSON格式的wifi对象
         * @since DBS V100
         */
        public JSONObject wifi_tower()
        {

            JSONObject jsonobject = new JSONObject();

            try
            {

                String s = bssid; // mac地址

                jsonobject.put("mac_address", s);

                int i = dBm;

                jsonobject.put("signal_strength", i); // 信号强度

                String s1 = ssid;

                jsonobject.put("ssid", s1); // 服务集合标识符

                jsonobject.put("age", 0);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return jsonobject;

        }

        public final String bssid; // mac地址

        public final int dBm; // 信号强度

        public final String ssid; // 服务集合标识符

        /**
         * 
         * 构造器
         * 类初始化时调用的构造器
         * @param scanresult 检测到的WIFI信息
         * @since DBS V100
         */
        public WifiInfo(ScanResult scanresult)
        {

            String s = scanresult.BSSID;

            bssid = s;

            int i = scanresult.level;

            dBm = i;

            String s1 = scanresult.SSID;

            ssid = s1;

        }

        /**
         * 
         * 构造器
         * 类初始化时调用的构造器
         * @param s 当前访问点的基本访问集合标识符
         * @param i 当前连接的信号强度
         * @param s1 服务集合标识符
         * @since DBS V100
         */
        public WifiInfo(String s, int i, String s1)
        {

            bssid = s;

            dBm = i;

            ssid = s1;

        }

    }
}

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
 * <���ܼ���> Wifi��Ϣ����
 * <������ϸ����> ��ȡ��ǰ���õ�Wifi��Ϣ
 * @author xiangyuanmao
 * @version 1.0 2012-5-22
 * @since DBS V100
 */
public class WifiInfoManager
{
    private WifiManager wifiManager; // WIFI����

    /**
     * ������ ���ʼ��ʱ���õĹ�����
     * @param paramContext
     * @since DBS V100
     */
    public WifiInfoManager(Context paramContext)
    {

        this.wifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);

    }

    /**
     * �ѵ�ǰ���ӵĶ�̬Wifi��Ϣ�ͼ�⵽��wifi��Ϣת����һ���б�����
     * @return
     * @since DBS V100
     */
    public List<WifiInfo> dump()
    {

        if (!this.wifiManager.isWifiEnabled())
        {

            return new ArrayList<WifiInfo>();

        }

        // ����һ��WIFI���ӣ����ص��ǵ�ǰ��̬���ӵ�Wifi����
        android.net.wifi.WifiInfo wifiConnection = this.wifiManager.getConnectionInfo();

        // ��ǰ��WIFI��Ϣ
        WifiInfo currentWIFI = null;

        // ������Ӳ�Ϊ��
        if (wifiConnection != null)
        {

            // ��ǰ���ʵ�Ļ������ʼ��ϱ�ʶ��,��һ�����ֽڵ�MAC��ַ,���磨XX:XX:XX:XX:XX:XX��
            String s = wifiConnection.getBSSID();

            // ��ǰ���ӵ��ź�ǿ��
            int i = wifiConnection.getRssi();

            /*
             * �ӵ�ǰ��802.11�����ϻ�ȡ���񼯺ϱ�ʶ��������ñ�ʶ����ASCII�͵��ַ���������
             * �����ŵ�˫�����У���������һ��ʮ�������ַ����������ǰ���������Ӿͷ���null
             */
            String s1 = wifiConnection.getSSID();

            // ʵ����һ��WIFI��Ϣ����
            currentWIFI = new WifiInfo(s, i, s1);

        }

        // ��ʼ��һ��WIFI��Ϣ����
        ArrayList<WifiInfo> lsAllWIFI = new ArrayList<WifiInfo>();

        // ��ӵ�ǰWIFI��Ϣ������
        if (currentWIFI != null)
        {

            lsAllWIFI.add(currentWIFI);

        }

        // �õ�һ����⵽��WIFI��Ϣ����
        List<ScanResult> lsScanResult = this.wifiManager.getScanResults();

        // ��ӵ�WIFI��Ϣ����
        for (ScanResult result : lsScanResult)
        {

            WifiInfo scanWIFI = new WifiInfo(result);

            if (!scanWIFI.equals(currentWIFI))

                lsAllWIFI.add(scanWIFI);

        }

        return lsAllWIFI;

    }

    /**
     * ����WIFI�����Ƿ����
     * @return
     * @since DBS V100
     */
    public boolean isWifiEnabled()
    {

        return this.wifiManager.isWifiEnabled();

    }

    /**
     * ����Json��ʽ��Wifi������Ϣ
     * @return Json��ʽ��Wifi������Ϣ
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
     * ����WIFI�������
     * @return WIFI�������
     * @since DBS V100
     */
    public WifiManager wifiManager()
    {

        return this.wifiManager;

    }

    /**
     * ����һ��JSONArray���������ŵ���JSON�����ʽ��WIFI��Ϣ
     * @return JSON�����ʽ��WIFI��Ϣ
     * @since DBS V100
     */
    public JSONArray wifiTowers()
    {

        // ʵ����һ��JSONArray����
        JSONArray jsonArray = new JSONArray();

        try
        {

            // ʵ����һ����ǰ��⵽��WIFI��Ϣ������
            Iterator<WifiInfo> localObject = dump().iterator();

            while (true)
            {

                // �����������һ��Ԫ��ʱ����
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
     * <���ܼ���> ����WIFI��Ϣ������mac��ַ���ź�ǿ�ȡ����񼯺ϱ�ʶ��
     * <������ϸ����>
     * @author xiangyuanmao
     * @version 1.0 2012-5-22
     * @since DBS V100
     */
    public class WifiInfo implements Comparable<WifiInfo>
    {

        /**
         * ʵ�ֽӿڵ�compareTo����,��Ҫ�Ƚ��ź�ǿ��
         * @param wifiinfo WIFI��Ϣ
         * @return 0���ź���ͬ��������ʾ���ڣ�������ʾС��
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
         * ��дequals����
         * @param obj ���ȽϵĶ���
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
         * ��дhashCode����
         * @return �����hashֵ
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
         * ��װһ������WIFI��JSON����
         * @return JSON��ʽ�Ķ���
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
         * ��װJSON����
         * @return JSON��ʽ��wifi����
         * @since DBS V100
         */
        public JSONObject wifi_tower()
        {

            JSONObject jsonobject = new JSONObject();

            try
            {

                String s = bssid; // mac��ַ

                jsonobject.put("mac_address", s);

                int i = dBm;

                jsonobject.put("signal_strength", i); // �ź�ǿ��

                String s1 = ssid;

                jsonobject.put("ssid", s1); // ���񼯺ϱ�ʶ��

                jsonobject.put("age", 0);

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return jsonobject;

        }

        public final String bssid; // mac��ַ

        public final int dBm; // �ź�ǿ��

        public final String ssid; // ���񼯺ϱ�ʶ��

        /**
         * 
         * ������
         * ���ʼ��ʱ���õĹ�����
         * @param scanresult ��⵽��WIFI��Ϣ
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
         * ������
         * ���ʼ��ʱ���õĹ�����
         * @param s ��ǰ���ʵ�Ļ������ʼ��ϱ�ʶ��
         * @param i ��ǰ���ӵ��ź�ǿ��
         * @param s1 ���񼯺ϱ�ʶ��
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

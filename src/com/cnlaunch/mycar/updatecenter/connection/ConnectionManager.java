package com.cnlaunch.mycar.updatecenter.connection;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;

/**
 * ���ӹ���
 */
public class ConnectionManager
{
    private static ConnectionManager instance = new ConnectionManager();
    HashMap<String, Connection> connectionTable = new HashMap<String, Connection>();

    public static ConnectionManager getInstance()
    {
        return instance;
    }

    private ConnectionManager()
    {
    }

    private ConnectionManager(Context c)
    {
    }

    private ConnectionManager(Activity a)
    {
    }

    private static Connection conn = null;

    public static synchronized Connection getSingletonConnection(Activity a)
    {
        if (conn == null)
        {
            conn = new BluetoothConnection(a);
        }
        return conn;

    }

    /**
     * ʹ��һ�����ӷ�ʽ
     */
    public Connection getConnection(Activity a, String type)
    {
        if (a != null && type != null)
        {
            if (type.equalsIgnoreCase("bluetooth"))// ʹ�����������ӷ�ʽ
            {
                Connection c = connectionTable.get(type);
                if (c == null)
                {
                    c = new BluetoothConnection(a);
                    connectionTable.put(type, c);
                }
                return c;
            }
        }
        return null;
    }

    /**
     * ���һ�����õ�ͨ��ͨ��
     */
    public void addChannel(String type, Connection conn)
    {
        if (!connectionTable.containsKey(type) && conn != null)
        {
            connectionTable.put(type, conn);
        }
    }
}

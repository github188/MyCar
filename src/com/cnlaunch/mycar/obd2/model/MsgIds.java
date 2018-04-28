package com.cnlaunch.mycar.obd2.model;

public class MsgIds
{

    public static final int ORDER_ENABLE_BT = 0;
    public static final int REPLY_ENABLED_BT = 1;
    /************************** 蓝牙 begin ****************************/
    public static final int ORDER_DISCONNECT_BT = 2;// 命令关闭蓝牙
    public static final int REPLY_DISCONNECT_BT = 3;// 响应命令并且关闭蓝牙

    public static final int ORDER_OPENED_BT = 4;// 蓝牙已经打开
    public static final int REPLY_OPENED_BT = 5;// 响应蓝牙已经打开

    public static final int ORDER_CONNECTING_BT = 6;// 蓝牙正在连接
    public static final int REPLY_CONNECTING_BT = 7;// 响应蓝牙正在连接

    public static final int ORDER_CONNECTED_BT = 8;// 蓝牙已经连接
    public static final int REPLY_CONNECTED_BT = 9;// 响应蓝牙已经连接

    public static final int ORDER_CONNECT_LOST_BT = 10;// 蓝牙连接丢失
    public static final int REPLY_CONNECT_LOST_BT = 11;// 响应蓝牙连接丢失

    public static final int ORDER_CONNECT_FAILED_BT = 12;// 蓝牙连接失败
    public static final int REPLY_CONNECT_FAILED_BT = 13;// 响应蓝牙连接失败
    /************************** 蓝牙 end ****************************/
    /************************** VIN begin ****************************/
    public static final int ORDER_VIN = 14;// 获取VIN码
    public static final int REPLY_VIN = 15;// 响应获取VIN码
    /************************** VIN end ****************************/
    
    public static final int ORDER_STOP_SERVICE = 16;// 关闭obd2 service

}

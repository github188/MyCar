package com.cnlaunch.mycar.obd2.model;

public class MsgIds
{

    public static final int ORDER_ENABLE_BT = 0;
    public static final int REPLY_ENABLED_BT = 1;
    /************************** ���� begin ****************************/
    public static final int ORDER_DISCONNECT_BT = 2;// ����ر�����
    public static final int REPLY_DISCONNECT_BT = 3;// ��Ӧ����ҹر�����

    public static final int ORDER_OPENED_BT = 4;// �����Ѿ���
    public static final int REPLY_OPENED_BT = 5;// ��Ӧ�����Ѿ���

    public static final int ORDER_CONNECTING_BT = 6;// ������������
    public static final int REPLY_CONNECTING_BT = 7;// ��Ӧ������������

    public static final int ORDER_CONNECTED_BT = 8;// �����Ѿ�����
    public static final int REPLY_CONNECTED_BT = 9;// ��Ӧ�����Ѿ�����

    public static final int ORDER_CONNECT_LOST_BT = 10;// �������Ӷ�ʧ
    public static final int REPLY_CONNECT_LOST_BT = 11;// ��Ӧ�������Ӷ�ʧ

    public static final int ORDER_CONNECT_FAILED_BT = 12;// ��������ʧ��
    public static final int REPLY_CONNECT_FAILED_BT = 13;// ��Ӧ��������ʧ��
    /************************** ���� end ****************************/
    /************************** VIN begin ****************************/
    public static final int ORDER_VIN = 14;// ��ȡVIN��
    public static final int REPLY_VIN = 15;// ��Ӧ��ȡVIN��
    /************************** VIN end ****************************/
    
    public static final int ORDER_STOP_SERVICE = 16;// �ر�obd2 service

}

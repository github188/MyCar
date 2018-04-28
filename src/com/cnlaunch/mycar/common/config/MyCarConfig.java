package com.cnlaunch.mycar.common.config;

public class MyCarConfig
{

    /**
     * �û����ݿ⣺ ��Ҫʹ�����ݿ��ҵ��ģ��ʵ��IDatabaseInit�ӿں���Ҫ�ڴ�ע��������
     * �������ݲ�ͨ��������ã�ʵ�����ݿⴴ��������������ʼ���ݻ����������Ͱ汾���ݿ��е�����
     */
    public static final String[] classesForUserDatabaseInit = { 
        "com.cnlaunch.mycar.manager.database.ManagerDatabaseInit", 
        "com.cnlaunch.mycar.usercenter.database.UsercenterDatabaseInit" };

    public static final String[] classesForDBSCarSummary = { 
        "com.cnlaunch.mycar.PushUserInfoSummary", 
        "com.cnlaunch.mycar.PushImSummary", 
        "com.cnlaunch.mycar.PushBillingSummary",
        "com.cnlaunch.mycar.PushSampleReportSummary"
    	};

    public static final String[] classesForUserOnlineState = {
        "com.cnlaunch.mycar.ImUserOnlineStateListener"
    };
    /**
     * ���ݿ�汾��
     */
    public static final int dataBaseVersion = 1;

    /**
     * ��ǰ��¼�û���CC�Ų��������ݿ�����
     */
    public static String currentCCToDbName = "anonymous.db";

    public static String gpsDirInSdcard = "MyCar/GPS";
}

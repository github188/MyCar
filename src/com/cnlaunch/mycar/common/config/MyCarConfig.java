package com.cnlaunch.mycar.common.config;

public class MyCarConfig
{

    /**
     * 用户数据库： 需要使用数据库的业务模块实现IDatabaseInit接口后，需要在此注册类名，
     * 公共数据层通过反射调用，实现数据库创建，升级，填充初始数据或批量操作低版本数据库中的数据
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
     * 数据库版本号
     */
    public static final int dataBaseVersion = 1;

    /**
     * 当前登录用户的CC号产生的数据库名称
     */
    public static String currentCCToDbName = "anonymous.db";

    public static String gpsDirInSdcard = "MyCar/GPS";
}

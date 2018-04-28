package com.cnlaunch.mycar.common.config;

import java.io.File;

public class Constants
{
    // Mycar版本信息
    public static final String MYCAR_VERSION = "V1.04.020"; // 软件版本 版本使用从资源
    public static final String DOWNLOAD_BIN_BASE_VERSION = "V00.00"; // 软件版本
                                                                     // 版本使用从资源
    public static final String MYCAR_BUILDER_VERSION = "Build:20120926-0001"; // 编译记录版本
    // device
    // 服务端有两个系统，对应不同的名称空间和URL
    public static final int SERVICE_LOGIN = 1; // 登录服务
    public static final int SERVICE_BUSINESS = 2;// 业务服务
    public static final int SERVICE_WEATHER = 3; // 天气服务
    public static final int SERVICE_MANAGER_ACCOUNT = 4; // 车辆管家_记账
    public static final int SERVICE_MANAGER_BILL_CATEGORY = 5; // 车辆管家_自定义类别
    public static final int SERVICE_MANAGER_OIL = 6; // 车辆管家_油耗
    public static final int SERVICE_MANAGER_USER_CAR = 7; // 车辆管家_用户车辆
    public static final int SERVICE_MANAGER_MANAGER_SETTING = 8; // 车辆管家_加油记录
    public static final int SERVICE_USERCENTER = 9; // 用户中心服务
    public static final int SERVICE_RESCUE_VEHICLES = 10;// 车辆救援
    public static final int SERVICE_BLACK_BOX = 11;// 黑匣 子
    public static final int SERVICE_CRECORDER = 12;// 黑匣 子数据列表
    public static final int SERVICE_PRODUCT = 13; // 产品注册
    public static final int SERVICE_PUBLIC_SOFT = 14;
    public static final int SERVICE_CLASS_ONE_KEY_DIAG = 15;// 一键配置
    public static final int SERVICE_EXT_USER_INFO = 16; // 用户扩展信息
    public static final int SERVICE_USER_SECURITY = 17; // 用户安全设置服务
    public static final int SERVICE_BIND_EMAIL = 18; // 绑定邮箱服务

    // ----------------------WebService
    // 主机地址(正式环境)-------------------------------------
    public static final String WEBSERVICE_HOST_USERCENTER = "http://uc.dbscar.com/"; // 用户中心
    public static final String WEBSERVCIE_HOST_BUSINESS = "http://mycar.dbscar.com/"; // 业务逻辑
    
                                                                          
    // ----------------------WebService
    // 主机地址(测试环境)-------------------------------------
    // public static final String WEBSERVICE_HOST_USERCENTER =
    // "http://tuc.dbscar.com/"; // 用户中心
    // public static final String WEBSERVCIE_HOST_BUSINESS =
    // "http://tmycar.dbscar.com/"; //业务 逻辑
//    public static final String WEBSERVICE_HOST_USERCENTER = "http://tuc.cnlaunch.com/"; // 用户中心
//    public static final String WEBSERVCIE_HOST_BUSINESS = "http://tmycar.cnlaunch.com/";   //业务 逻辑          
    // ----------------------名称空间------------------------------------------------
    public static final String WEBSERVICE_NAME_SPACE = "http://www.x431.com";
    // ----------------------用户中心外网访问地址
    public static final String SERVICE_USERCENTER_URL_USER_SECURITY = WEBSERVICE_HOST_USERCENTER + "ucenter/services/usersecurityservice";
    public static final String SERVICE_LOGIN_URL_USERCENTER = WEBSERVICE_HOST_USERCENTER + "services/loginservice";
    public static final String SERVICE_USERCENTER_URL_USERCENTER = WEBSERVICE_HOST_USERCENTER + "services/userservice";
    public static final String SERVICE_USERCENTER_URL_USER_BIND = WEBSERVICE_HOST_USERCENTER + "ucenter/services/bindingservice";
    public static final String SERVICE_EXT_USER_INFO_URL = WEBSERVICE_HOST_USERCENTER + "ucenter/services/userservice";
    public static final String SERVICE_BLACK_BOX_SERVICEMERCHANT_URL = WEBSERVICE_HOST_USERCENTER + "services/diagDataService/downloadHisDiagData";
    public static final String SERVICE_RESCUE_VEHICLES_SERVICEMERCHANT_URL = WEBSERVICE_HOST_USERCENTER + "services/serviceMerchant";
    public static final String SERVICE_BUSINESS_URL = WEBSERVCIE_HOST_BUSINESS + "services/userService";
    public static final String SERVICE_MANAGER_ACCOUNT_URL = WEBSERVCIE_HOST_BUSINESS + "services/accountService";
    public static final String SERVICE_MANAGER_OIL_URL = WEBSERVCIE_HOST_BUSINESS + "services/oilService";
    public static final String SERVICE_MANAGER_MANAGER_SETTING_URL = WEBSERVCIE_HOST_BUSINESS + "services/managerSettingService";
    public static final String SERVICE_MANAGER_CATEGORY_URL = WEBSERVCIE_HOST_BUSINESS + "services/categoryService";
    public static final String SERVICE_MANAGER_USER_CAR_URL = WEBSERVCIE_HOST_BUSINESS + "services/userCarService";
    public static final String SERVICE_CRECORDER_URL = WEBSERVCIE_HOST_BUSINESS + "MyCar/services/diagDataService";
    public static final String SERVICE_ONE_KEY_DIAG = WEBSERVCIE_HOST_BUSINESS + "services/oneKeyDiagService";
    public static final String SERVICE_PRODUCT_SERVICE = WEBSERVCIE_HOST_BUSINESS + "services/productService";
    public static final String SERVICE_PUBLIC_SOFT_SERVICE = WEBSERVCIE_HOST_BUSINESS + "services/publicSoftService";
    
    public static final String SERVICE_WEATHER_URL = "http://webapi.dbscar.com:8081/V20110512/WeatherAPIsh.ashx";
    public static final String SERVICE_MANAGER_METHOD_UPLOAD_ACCOUNT = "uploadAccount";// 上传记账数据
    public static final String SERVICE_MANAGER_METHOD_DOWNLOAD_ACCOUNT = "downloadAccount";// 下载记账数据
    public static final String SERVICE_MANAGER_METHOD_UPLOAD_OIL = "uploadOil";// 上传加油记录
    public static final String SERVICE_MANAGER_METHOD_DOWNLOAD_OIL = "downloadOil";// 下载加油记录
    public static final String SERVICE_MANAGER_METHOD_UPLOAD_MANAGER_SETTING = "uploadManagerSetting ";// 上传车辆管家用户配置
    public static final String SERVICE_MANAGER_METHOD_DOWNLOAD_MANAGER_SETTING = "downloadManagerSetting ";// 下载车辆管家用户配置
    public static final String SERVICE_MANAGER_METHOD_UPLOAD_USER_CAR = "uploadUserCar ";// 上传用户车辆记录
    public static final String SERVICE_MANAGER_METHOD_DOWNLOAD_USER_CAR = "downloadUserCar ";// 下载用户车辆记录
    public static final String SERVICE_MANAGER_METHOD_UPLOAD_BILL_CATEGORY = "uploadBillCategory ";// 上传记账自定义数据
    public static final String SERVICE_MANAGER_METHOD_DOWNLOAD_BILL_CATEGORY = "downloadBillCategoryBycondition ";// 下载记
                                                                                                       // 账自定义数据
    public static final String SERVICE_LOGIN_METHOD_NAME = "userLogin";

    public static String ROOT_DIR = "cnlaunch" + File.separator + "dbsCar";// MyCar在sd卡上的数据目录

    public static final String IS_FIRST_USE = "isFirstUse"; // 是否第一次使用本系统
    public static final String IS_AGREE_LEGAL_TERMS = "isAgreeLegalTerms"; // 是否同意法律条款
    public static final String USERCENTER_IS_BIND = "1";
    public static final String USERCENTER_NOT_BIND = "0";

    // ----------------------------------------主界面标题栏上更新状态图片的广播接收器相关常量--------------------------
    public static String MAIN_TITLE_ACTION_WEATHER = "com.cnlaunch.mycar.action.WEATHER";
    public static String MAIN_TITLE_ACTION_USERCENTER = "com.cnlaunch.mycar.action.USERCENTER";
    public static String Main_TITLE_ACTION_ENGINEFAULT = "com.cnlaunch.mycar.action.ENGINEFAULT";

    public static String UPLOAD_FILE_NAME = "name";
    public static String UPLOAD_FILE_FILENAME = "filename";
    public static String UPLOAD_FILE_SIZE = "size";
    public final static String MAIN_DBSCAR_SUMMARY = "com.cnlaunch.mycar.DBSCarSummaryBroadcastReceiver";
    public static final String OBD2_SERVICE_NAME = "com.cnlaunch.mycar.obd2.intent.action.Obd2DiagnoseService";

    public final static int LOGIN_STATE_LOGINED = 2; // 登录状态：已登录
    public final static int LOGIN_STATE_LOGINING = 1; // 登录状态：登录中
    public final static int LOGIN_STATE_LOGOUTED = 0; // 登录状态：未登录

    public final static String DBSCAR_CURRENT_CAR_TYPE = "current_car_type";
    public final static String DBSCAR_CURRENT_VERSION = "current_version";
    public final static String DBSCAR_SIMPLE_DIAGNOSE = "current_ggp_path";
    public final static boolean DEBUG = true;
    public final static String SHARE_PREFERENCES_DEVICE_INFO = "com.cnlaunch.mycar.update.sharepreference.device";
    public final static String SP_DEVICE_INFO_BLUETOOTH_NAME = "bluetoothName";
    public final static String SP_DEVICE_INFO_MAC_ADDRESS = "macAddress";
    public final static String SP_DEVICE_INFO_VIN = "vehicleVIN";
    public final static String SP_DEVICE_INFO_DOWNLOAD_BIN_VERSION = "downloadBinVersion";
    public final static String SP_DEVICE_INFO_DOWNLOAD_BIN_LANGUAGE = "downloadBinLanguage";
    public final static String SP_DEVICE_INFO_IS_NEED_UPDATE_DOWNLOAD_BIN = "isNeedUpdateDwonloadBin";
    public final static String SP_DEVICE_INFO_IS_NEED_UPDATE_DIAGNOSE_SW = "isNeedUpdateDiagnoseSW";
    public final static String SP_DEVICE_INFO_SERIAL_NUMBER = "serialNumber";
    public final static String SP_DEVICE_INFO_CHIP_ID = "chipID";
    public final static String SP_DEVICE_INFO_LAST_UPDATE_INFO = "lastUpdateInfo";
    public final static String SP_DEVICE_INFO = "deviceInfo";
    public final static String DEVICE_INFO_SEPARATED = "_____";// Separated
}

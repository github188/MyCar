package com.cnlaunch.mycar.usercenter;

public class UsercenterConstants
{
    /************* 自动登录 *****************************************************/
    public static final String MYCAR_SHARED_PREFERENCES = "mycarSP"; // 系统SharedPreferences
    public static final String LOGIN_SHARED_PREFERENCES = "loginSP"; // 登录SharedPreferences
    public static final String IS_AUTO_LOGIN = "isAutoLogin"; // 是否自动登录
    public static final String LOGIN_STATE = "loginState"; // 登录状态
    public static final String LOGIN_SERVICE_TIME = "serviceTime"; // 成功登录后系统返回的时间
    public static final int LOGIN_RESULT = 5; // 登录结果
    public static final int SYNC_USERINFO_TO_SERVICE_RESULT = 6; // 同步用户信息到服务器结果
    public static final int SYNC_USERINFO_FROM_SERVICE_RESULT = 7; // 从服务器同步用户信息到手机结果
    public static final int REGISTER_RESULT = 4; // 登录结果
    public static final int RESULT_SUCCESS = 0; // 登录结果:成功
    public static final int RESULT_FAIL = -1; // 登录结果：失败
    public static final int RESULT_EXCEPTION = 2; // 登录结果:异常 
    public static final int CLICK_DRAG = 3; //点击了下拉菜单
    public static final int START_ANIMATION = 777;  // 启动动画
    public static final int STOP_ANIMATION = 888;   // 关闭动画
    public static final int REGISTER_PARAMETER_ILLEGAL = 506;   // 关闭动画
    
    /************* 请求代码 *****************************************************/
    public static final int REQUEST_CODE_WEBSERVICE = 5; // WebService调用LoginActivity是的requestCode，
    
    public static final int RESULT_USERNAME_OR_PASSWORD_ERROR = 501; // 登录结果:用户名或密码错误
    public static final int RESPONSE_TOKEN_TIMEOUT = -1; // WebService请求token超时
    public static final String LOGIN_STATE_LOGIN = "login"; // 登录状态:已登录
    public static final String LOGIN_STATE_LOGOUT = "logout"; // 登录状态:未登录
//    public static final String LOGIN_STATE_DISPLAY_LOGIN = "已登录"; // 登录状态:已登录
//    public static final String LOGIN_STATE_DISPLAY_LOGOUT = "未登录"; // 登录状态:未登录
    public static final String LAST_LOGIN_ACCOUNT = "lastLoginAccount"; // 最后登录账号
    public static final String LAST_LOGIN_PWD = "lastLoginPwd"; // 最后登录密码
    public static final String LOGIN_TOKEN = "loginToken";// 登录令牌
    public static final String LOGIN_CC = "loginCC";
    
    /************** 用户中心 *****************************************************/
    public static final int USERCENTER_CC_ACOUNT_LENGTH = 10; // CC号码长度
    public static final int USERCENTER_MOBILE_PHONE_LENGTH = 11; // 手机号码长度
    public static final int USERCENTER_PWD_LENGTH_MIN_LIMIT = 6; // 密码最小长度限制
    public static final int USERCENTER_PWD_LENGTH_MAX_LIMIT = 20; // 密码最大长度限制
    
    /* 邮箱字符串正则表达式模式串 */
    public static final String USERCENTER_EMAIL_REGULAR_PATTERN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    /* 电话号码字符串正则表达式模式串 */
    public static final String USERCENTER_MOBILE_PHONE_REGULAR_PATTERN = "^(13[0-9]|15[0|3|6|7|8|9]|18[7|8|9])+[0-9]{8}$";

//    public static final String USERCENTER_TOAST_ACCOUNT_INVALID = "帐号格式不对，请输入CC号，或手机号，或邮箱名。";
//    public static final String USERCENTER_TOAST_ORG_PWD_INVALID = "原密码格式不对";
//    public static final String USERCENTER_TOAST_NEW_PWD_INVALID = "新密码格式不对";
//    public static final String USERCENTER_TOAST_NEW_PWD_NOT_EQUAL = "两次新输入的密码不相同";
    
    public static final int USERCENTER_RESULT_CHANGE_PWD = 5; // 修改密码结果
    public static final int USERCENTER_RESULT_CHANGE_PWD_SUCCESS = 0; // 修改密码结果:成功
    public static final int USERCENTER_RESULT_CHANGE_PWD_FAILED = -1; // 修改密码结果:失败
    public static final int USERCENTER_RESULT_CHANGE_PWD_OLD_PWDE_RROR = 383; // 修改密码结果:原密码错误
    public static final int USERCENTER_RESULT_CHANGE_PWD_WRONG_ORIGINAL_PWD = 411; // 原始密码错误
    public static final int USERCENTER_RESULT_CHANGE_PWD_EXCEPTION = -2; // 修改密码结果:异常
//    
//    public static final String USERCENTER_CHANGE_PWD_PROGRESS_DLG_TITLE = "请稍候..."; // 修改密码进度对话框标题
//    public static final String USERCENTER_CHANGE_PWD_PROGRESS_DLG_BODY = "正在提交修改密码请求...";   // 修改密码进度对话框内容
//    
//    public static final String USERCENTER_RESULT_CHANGE_PWD_SUCCESS_PROMPT = "修改密码成功！\n"; // 修改密码结果:成功
//    public static final String USERCENTER_RESULT_CHANGE_PWD_FAILED_PROMPT = "修改密码失败！\n"; // 修改密码结果:失败
//    public static final String USERCENTER_RESULT_CHANGE_PWD_EXCEPTION_PROMPT = "修改密码过程中出现异常！\n"; // 修改密码结果:异常
    
    public static final int USERCENTER_RESULT_RETRIVED_PWD = 6; // 找回密码结果
    public static final int USERCENTER_RESULT_RETRIVED_SUCCESS = 0; // 找回密码结果:成功
    public static final int USERCENTER_RESULT_RETRIVED_FAILED = -1; // 找回密码结果:失败
    public static final int USERCENTER_RESULT_RETRIVED_EXCEPTION = -2; // 找回密码结果:异常 
    
    public static String ANONYMOUS_DATABASE_NAME = "anonymous.db"; // 匿名数据库名称
    
    public static String USERCENTER_USERINFO_LABEL = "label"; // 用户属性的显示名称
    public static String USERCENTER_USERINFO_VALUE = "value"; // 用户属性的值
    public static String DEVICE_NAME_ = "deviceName"; // 设备名称
    public static String DEVICE_SERIAL = "deviceSerial"; // 序列号
    public static String DEVICE_STATUS = "status"; // 状态
    public static String DEVICE_MAC = "mac";// mac地址
    public static String DEVICE_CHIP_ID = "chipId";
    public static String USERCENTER_USERINFO_ID = "id";// 用户属性ID
//    public static String USERCENTER_DIALOG_OK = "确定"; 
//    public static String USERCENTER_DIALOG_CANCEL = "取消"; 
//    public static String USERCENTER_REQUEST_USERINFO = "正在请求服务器获取用户资料...";
    
    /***************************公共错误码********************************/
    public static final int OPREATE_SUCCESS = 0; // 网络操作成功!
    /**
     * 参数不能为空
     */
    public static final int PARAMETE_IS_NULL_ERROR = 401;

    /**
     * 参数格式错误
     */
    public static final int PARAMETE_FORMAT_ERROR = 402;
    /**
     * 系统异常
     */
    public static final int SYSTEM_ERROR = 500;

    /**
     * 网络异常
     */
    public static final int NETWORK_ERROR = 501;
    /************************用户中心错误码，递减分配***************************/
    /**
     * 用户登录名或者密码错误或用户状态异常
     */
    public static final int LOGIN_FAIL = 399;

    /**
     * 用户状态错误
     */
    public static final int USER_STATUS_ERROR = 398;
    /**
     * 邮箱未设置异常
     */
    public static final int EMAIL_NOT_SET_ERROR = 397;

    /**
     * 用户名重复错误
     */
    public static final int USERNAME_DOUBLE_ERROR = 396;

    /**
     * 邮箱格式错误
     */
    public static final int EMAIL_FORMAT_ERROR = 395;

    /**
     * 手机格式错误
     */
    public static final int MOBILE_FORMAT_ERROR = 394;

    /**
     * 用户名格式错误
     */
    public static final int USERNAME_FORMAT_ERROR = 393;

    /**
     * 密码格式错误
     */
    public static final int PASSWORD_FORMAT_ERROR = 392;
    /**
     * 密码保护问题重复错误
     */
    public static final int SECURITY_QUESTION_DOUBLE_ERROR = 391;
    /**
     * 电话格式错误
     */
    public static final int PHONE_FORMAT_ERROR = 390;

    /**
     * 邮箱已经绑定
     */
    public static final int MOBILE_IS_BIND_ERROR = 389;
    /**
     * 
     */
    public static final int EMAIL_IS_BIND_ERROR = 388;

    /**
     * 密码保护问题已存在异常
     */
    public static final int SECURITY_ANSWER_EXIST_ERROR = 387;

    /**
     * 用户未登录异常
     */
    public static final int NOT_LOGIN_ERROR = 386;

    /**
     * 验证密码保护问题失败
     */
    public static final int VALIDATE_ANSWER_FAIL = 385;

    /**
     * 用户不存在错误
     */
    public static final int USER_NOT_EXIST_ERROR = 384;

    /**
     * 密码错误
     */
    public static final int PASSWORD_ERROR = 383;

    /**32
     * 邮箱重复错误
     */
    public static final int EMAIL_DOUBLE_ERROR = 382;
}

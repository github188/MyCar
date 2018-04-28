package com.cnlaunch.mycar.usercenter;

public class UsercenterConstants
{
    /************* �Զ���¼ *****************************************************/
    public static final String MYCAR_SHARED_PREFERENCES = "mycarSP"; // ϵͳSharedPreferences
    public static final String LOGIN_SHARED_PREFERENCES = "loginSP"; // ��¼SharedPreferences
    public static final String IS_AUTO_LOGIN = "isAutoLogin"; // �Ƿ��Զ���¼
    public static final String LOGIN_STATE = "loginState"; // ��¼״̬
    public static final String LOGIN_SERVICE_TIME = "serviceTime"; // �ɹ���¼��ϵͳ���ص�ʱ��
    public static final int LOGIN_RESULT = 5; // ��¼���
    public static final int SYNC_USERINFO_TO_SERVICE_RESULT = 6; // ͬ���û���Ϣ�����������
    public static final int SYNC_USERINFO_FROM_SERVICE_RESULT = 7; // �ӷ�����ͬ���û���Ϣ���ֻ����
    public static final int REGISTER_RESULT = 4; // ��¼���
    public static final int RESULT_SUCCESS = 0; // ��¼���:�ɹ�
    public static final int RESULT_FAIL = -1; // ��¼�����ʧ��
    public static final int RESULT_EXCEPTION = 2; // ��¼���:�쳣 
    public static final int CLICK_DRAG = 3; //����������˵�
    public static final int START_ANIMATION = 777;  // ��������
    public static final int STOP_ANIMATION = 888;   // �رն���
    public static final int REGISTER_PARAMETER_ILLEGAL = 506;   // �رն���
    
    /************* ������� *****************************************************/
    public static final int REQUEST_CODE_WEBSERVICE = 5; // WebService����LoginActivity�ǵ�requestCode��
    
    public static final int RESULT_USERNAME_OR_PASSWORD_ERROR = 501; // ��¼���:�û������������
    public static final int RESPONSE_TOKEN_TIMEOUT = -1; // WebService����token��ʱ
    public static final String LOGIN_STATE_LOGIN = "login"; // ��¼״̬:�ѵ�¼
    public static final String LOGIN_STATE_LOGOUT = "logout"; // ��¼״̬:δ��¼
//    public static final String LOGIN_STATE_DISPLAY_LOGIN = "�ѵ�¼"; // ��¼״̬:�ѵ�¼
//    public static final String LOGIN_STATE_DISPLAY_LOGOUT = "δ��¼"; // ��¼״̬:δ��¼
    public static final String LAST_LOGIN_ACCOUNT = "lastLoginAccount"; // ����¼�˺�
    public static final String LAST_LOGIN_PWD = "lastLoginPwd"; // ����¼����
    public static final String LOGIN_TOKEN = "loginToken";// ��¼����
    public static final String LOGIN_CC = "loginCC";
    
    /************** �û����� *****************************************************/
    public static final int USERCENTER_CC_ACOUNT_LENGTH = 10; // CC���볤��
    public static final int USERCENTER_MOBILE_PHONE_LENGTH = 11; // �ֻ����볤��
    public static final int USERCENTER_PWD_LENGTH_MIN_LIMIT = 6; // ������С��������
    public static final int USERCENTER_PWD_LENGTH_MAX_LIMIT = 20; // ������󳤶�����
    
    /* �����ַ���������ʽģʽ�� */
    public static final String USERCENTER_EMAIL_REGULAR_PATTERN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    /* �绰�����ַ���������ʽģʽ�� */
    public static final String USERCENTER_MOBILE_PHONE_REGULAR_PATTERN = "^(13[0-9]|15[0|3|6|7|8|9]|18[7|8|9])+[0-9]{8}$";

//    public static final String USERCENTER_TOAST_ACCOUNT_INVALID = "�ʺŸ�ʽ���ԣ�������CC�ţ����ֻ��ţ�����������";
//    public static final String USERCENTER_TOAST_ORG_PWD_INVALID = "ԭ�����ʽ����";
//    public static final String USERCENTER_TOAST_NEW_PWD_INVALID = "�������ʽ����";
//    public static final String USERCENTER_TOAST_NEW_PWD_NOT_EQUAL = "��������������벻��ͬ";
    
    public static final int USERCENTER_RESULT_CHANGE_PWD = 5; // �޸�������
    public static final int USERCENTER_RESULT_CHANGE_PWD_SUCCESS = 0; // �޸�������:�ɹ�
    public static final int USERCENTER_RESULT_CHANGE_PWD_FAILED = -1; // �޸�������:ʧ��
    public static final int USERCENTER_RESULT_CHANGE_PWD_OLD_PWDE_RROR = 383; // �޸�������:ԭ�������
    public static final int USERCENTER_RESULT_CHANGE_PWD_WRONG_ORIGINAL_PWD = 411; // ԭʼ�������
    public static final int USERCENTER_RESULT_CHANGE_PWD_EXCEPTION = -2; // �޸�������:�쳣
//    
//    public static final String USERCENTER_CHANGE_PWD_PROGRESS_DLG_TITLE = "���Ժ�..."; // �޸�������ȶԻ������
//    public static final String USERCENTER_CHANGE_PWD_PROGRESS_DLG_BODY = "�����ύ�޸���������...";   // �޸�������ȶԻ�������
//    
//    public static final String USERCENTER_RESULT_CHANGE_PWD_SUCCESS_PROMPT = "�޸�����ɹ���\n"; // �޸�������:�ɹ�
//    public static final String USERCENTER_RESULT_CHANGE_PWD_FAILED_PROMPT = "�޸�����ʧ�ܣ�\n"; // �޸�������:ʧ��
//    public static final String USERCENTER_RESULT_CHANGE_PWD_EXCEPTION_PROMPT = "�޸���������г����쳣��\n"; // �޸�������:�쳣
    
    public static final int USERCENTER_RESULT_RETRIVED_PWD = 6; // �һ�������
    public static final int USERCENTER_RESULT_RETRIVED_SUCCESS = 0; // �һ�������:�ɹ�
    public static final int USERCENTER_RESULT_RETRIVED_FAILED = -1; // �һ�������:ʧ��
    public static final int USERCENTER_RESULT_RETRIVED_EXCEPTION = -2; // �һ�������:�쳣 
    
    public static String ANONYMOUS_DATABASE_NAME = "anonymous.db"; // �������ݿ�����
    
    public static String USERCENTER_USERINFO_LABEL = "label"; // �û����Ե���ʾ����
    public static String USERCENTER_USERINFO_VALUE = "value"; // �û����Ե�ֵ
    public static String DEVICE_NAME_ = "deviceName"; // �豸����
    public static String DEVICE_SERIAL = "deviceSerial"; // ���к�
    public static String DEVICE_STATUS = "status"; // ״̬
    public static String DEVICE_MAC = "mac";// mac��ַ
    public static String DEVICE_CHIP_ID = "chipId";
    public static String USERCENTER_USERINFO_ID = "id";// �û�����ID
//    public static String USERCENTER_DIALOG_OK = "ȷ��"; 
//    public static String USERCENTER_DIALOG_CANCEL = "ȡ��"; 
//    public static String USERCENTER_REQUEST_USERINFO = "���������������ȡ�û�����...";
    
    /***************************����������********************************/
    public static final int OPREATE_SUCCESS = 0; // ��������ɹ�!
    /**
     * ��������Ϊ��
     */
    public static final int PARAMETE_IS_NULL_ERROR = 401;

    /**
     * ������ʽ����
     */
    public static final int PARAMETE_FORMAT_ERROR = 402;
    /**
     * ϵͳ�쳣
     */
    public static final int SYSTEM_ERROR = 500;

    /**
     * �����쳣
     */
    public static final int NETWORK_ERROR = 501;
    /************************�û����Ĵ����룬�ݼ�����***************************/
    /**
     * �û���¼���������������û�״̬�쳣
     */
    public static final int LOGIN_FAIL = 399;

    /**
     * �û�״̬����
     */
    public static final int USER_STATUS_ERROR = 398;
    /**
     * ����δ�����쳣
     */
    public static final int EMAIL_NOT_SET_ERROR = 397;

    /**
     * �û����ظ�����
     */
    public static final int USERNAME_DOUBLE_ERROR = 396;

    /**
     * �����ʽ����
     */
    public static final int EMAIL_FORMAT_ERROR = 395;

    /**
     * �ֻ���ʽ����
     */
    public static final int MOBILE_FORMAT_ERROR = 394;

    /**
     * �û�����ʽ����
     */
    public static final int USERNAME_FORMAT_ERROR = 393;

    /**
     * �����ʽ����
     */
    public static final int PASSWORD_FORMAT_ERROR = 392;
    /**
     * ���뱣�������ظ�����
     */
    public static final int SECURITY_QUESTION_DOUBLE_ERROR = 391;
    /**
     * �绰��ʽ����
     */
    public static final int PHONE_FORMAT_ERROR = 390;

    /**
     * �����Ѿ���
     */
    public static final int MOBILE_IS_BIND_ERROR = 389;
    /**
     * 
     */
    public static final int EMAIL_IS_BIND_ERROR = 388;

    /**
     * ���뱣�������Ѵ����쳣
     */
    public static final int SECURITY_ANSWER_EXIST_ERROR = 387;

    /**
     * �û�δ��¼�쳣
     */
    public static final int NOT_LOGIN_ERROR = 386;

    /**
     * ��֤���뱣������ʧ��
     */
    public static final int VALIDATE_ANSWER_FAIL = 385;

    /**
     * �û������ڴ���
     */
    public static final int USER_NOT_EXIST_ERROR = 384;

    /**
     * �������
     */
    public static final int PASSWORD_ERROR = 383;

    /**32
     * �����ظ�����
     */
    public static final int EMAIL_DOUBLE_ERROR = 382;
}

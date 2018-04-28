package com.cnlaunch.mycar.diagnose.constant;

public class DiagnoseConstant {
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// 蓝牙状态常量
	public static final int BLUETOOTH_STATE_CONNTED = 3;// 蓝牙已经连接上
	public static final int BLUETOOTH_STATIE_CONNTING = 2;// 蓝牙正在连接
	public static final int BLUETOOTH_STATE_NOT_CONNTED = 1;// 蓝牙还未连接

	// 诊断指令常量
	public static final int BT_ERROR = 0x23;
	public static final int BACKORDER_TOO_LONG = 0x02;
	public static final int BT_COMUNICATION_CHECK_ERROR = 0x01;// 通信校验错误
	public static final int REQUEST_BT_NO_RESPONSE = -1;
	public static final int TESTING = 0;
	public static final int SHOW = 0X22;// 做显示
	public static final int SHOW_TEXTDIALOG = 0X1c;// 显示文本对话框
	public static final int SHOW_INTEGER_TEXTDIALOG = 0x20;// 显示整数文本对话框
	public static final int SHOW_STRING_TEXTDIALOG = 0x24;// 显示字符串文本对话框
	public static final int SHOW_IDDIALOG = 0X1e;// 显示ID对话框

	public static final int SHOW_INTEGER_IDDIALOG = 0x22;// 显示整数ID对话框
	public static final int SHOW_STRING_IDDIALOG = 0x26;// 显示字符串ID对话框
	public static final int SHOW_FILE_IDDIALOG = 0x28;// 显示文件ID对话框

	public static final int SHOW_MENU = 0X10;
	public static final int SHOW_FAULTCODE = 0x12;
	public static final int SHOW_FAULTCODE_FREEZEFRAME = 0X14;
	public static final int SHOW_DATASTREAM_CHOCIE = 0X16;
	public static final int SHOW_DATASTREAM_SHOWING = 0X18;
	public static final int SHOW_VERSION= 0xF0; //显示版本信息

	public static final int REQUEST_BT_RETURN_KEY = 0x65;

	// ID对话框样式
	public static final int DIALOG_STYLE_OK = 1;
	public static final int DIALOG_STYLE_OKCANCEL = 2;
	public static final int DIALOG_STYLE__YESNO = 3;
	public static final int DIALOG_STYLE__RETRYCANCEL = 4;
	public static final int DIALOG_STYLE__NOBUTTON = 5;
	public static final int DIALOG_STYLE__OKPRINT = 6;

	public static final int DIALOG_ID_OK = 0;
	public static final int DIALOG_ID_CANCEL = 1;
	public static final int DIALOG_ID_YES = 2;
	public static final int DIALOG_ID_NO = 3;
	public static final int DIALOG_ID_RETRY = 4;
	public static final int DIALOG_ID_PRINT = 5;

	public static boolean ifDiagnoseCon = false;

	public static boolean isIfDiagnoseCon() {
		return ifDiagnoseCon;
	}

	public static void setIfDiagnoseCon(boolean ifDiagnoseCon) {
		DiagnoseConstant.ifDiagnoseCon = ifDiagnoseCon;
	}
	
	// 简易报告常量
	public static final String PRE_EXAM_NUM_PREFS="preExamNumPrefs";//体检得分首选项
	public static final String PRE_EXAM_NUM ="preExamNum";//体检得分
	public static final String DOC_ID_INFO="docIDInfo";//故障码信息
	public static final String PRE_DOC_NUM_PREFS="preDocNumPrefs";//上次体检故障码数量首选项
	public static final String PRE_DOC_NUM="preDocNum";//上次体检故障码数量
	public static final String PRE_QUESTION_LIST_PREFS="preQuestionListPrefs";//上次体检故障码列表首选项
	public static final String PRE_QUESTION_LIST="preQuestionList";//上次体检故障码列表
	public static final String SYS_ID_TEXT_PREFS="sysIdTextPrefs";//上次体检故障码列表首选项
	public static final String CURRENT_LANGUAGE="currentLanguage";//当前语言
	public static final String V_DOC_PIDID="docPIDID";// 故障码健-值
	public static final String DIAG_SP_PUSH_KEY="diagSpPushKey";// 简易报告消息推送-健
	public static final String DIAG_SP_PUSH_VALUE="diagSpPushValue";// 简易报告消息推送-值
	public static final String DIAG_CAR_TYPE_VER="diagCarTypeVer";//车型与版本
}

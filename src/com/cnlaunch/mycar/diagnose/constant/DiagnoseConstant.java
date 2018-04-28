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

	// ����״̬����
	public static final int BLUETOOTH_STATE_CONNTED = 3;// �����Ѿ�������
	public static final int BLUETOOTH_STATIE_CONNTING = 2;// ������������
	public static final int BLUETOOTH_STATE_NOT_CONNTED = 1;// ������δ����

	// ���ָ���
	public static final int BT_ERROR = 0x23;
	public static final int BACKORDER_TOO_LONG = 0x02;
	public static final int BT_COMUNICATION_CHECK_ERROR = 0x01;// ͨ��У�����
	public static final int REQUEST_BT_NO_RESPONSE = -1;
	public static final int TESTING = 0;
	public static final int SHOW = 0X22;// ����ʾ
	public static final int SHOW_TEXTDIALOG = 0X1c;// ��ʾ�ı��Ի���
	public static final int SHOW_INTEGER_TEXTDIALOG = 0x20;// ��ʾ�����ı��Ի���
	public static final int SHOW_STRING_TEXTDIALOG = 0x24;// ��ʾ�ַ����ı��Ի���
	public static final int SHOW_IDDIALOG = 0X1e;// ��ʾID�Ի���

	public static final int SHOW_INTEGER_IDDIALOG = 0x22;// ��ʾ����ID�Ի���
	public static final int SHOW_STRING_IDDIALOG = 0x26;// ��ʾ�ַ���ID�Ի���
	public static final int SHOW_FILE_IDDIALOG = 0x28;// ��ʾ�ļ�ID�Ի���

	public static final int SHOW_MENU = 0X10;
	public static final int SHOW_FAULTCODE = 0x12;
	public static final int SHOW_FAULTCODE_FREEZEFRAME = 0X14;
	public static final int SHOW_DATASTREAM_CHOCIE = 0X16;
	public static final int SHOW_DATASTREAM_SHOWING = 0X18;
	public static final int SHOW_VERSION= 0xF0; //��ʾ�汾��Ϣ

	public static final int REQUEST_BT_RETURN_KEY = 0x65;

	// ID�Ի�����ʽ
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
	
	// ���ױ��泣��
	public static final String PRE_EXAM_NUM_PREFS="preExamNumPrefs";//���÷���ѡ��
	public static final String PRE_EXAM_NUM ="preExamNum";//���÷�
	public static final String DOC_ID_INFO="docIDInfo";//��������Ϣ
	public static final String PRE_DOC_NUM_PREFS="preDocNumPrefs";//�ϴ���������������ѡ��
	public static final String PRE_DOC_NUM="preDocNum";//�ϴ�������������
	public static final String PRE_QUESTION_LIST_PREFS="preQuestionListPrefs";//�ϴ����������б���ѡ��
	public static final String PRE_QUESTION_LIST="preQuestionList";//�ϴ����������б�
	public static final String SYS_ID_TEXT_PREFS="sysIdTextPrefs";//�ϴ����������б���ѡ��
	public static final String CURRENT_LANGUAGE="currentLanguage";//��ǰ����
	public static final String V_DOC_PIDID="docPIDID";// �����뽡-ֵ
	public static final String DIAG_SP_PUSH_KEY="diagSpPushKey";// ���ױ�����Ϣ����-��
	public static final String DIAG_SP_PUSH_VALUE="diagSpPushValue";// ���ױ�����Ϣ����-ֵ
	public static final String DIAG_CAR_TYPE_VER="diagCarTypeVer";//������汾
}

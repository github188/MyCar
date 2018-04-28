package com.cnlaunch.mycar.diagnose.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import launch.SearchIdUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
//import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.config.Constants;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.constant.DiagnoseConstant;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseQuestionCategory;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseShowInfoStr;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseSimpleReportInfo;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.obd2.util.DiagnosisDataMapping;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;

public class DiagnoseSimpleReportDataService {

	private static final String TAG = "SimpleReportDataService";
	private static final boolean D = true;
	public final int MAX_TIME = 2000; // ��ʱʱ��
	// �����ʽ
	/*
	 * <��ʼ��־> + <Ŀ���ַ> + <Դ��ַ> + <������> + <������> + <������> + <����> + <��У��> <��ʼ��־>:
	 * 2byte 0x55,0xAA <Ŀ���ַ>: 1byte 0xF8 <Դ��ַ>: 1byte 0xF0 <������>: 2byte <������> +
	 * <������> + <����> <������>�� 1byte 0x00~0xFFѭ������ <������>�� 2byte <����>�� <��У��>�� 1byte
	 * <Ŀ���ַ> + <Դ��ַ> + <������> + <������> + <������> + <����>
	 * 
	 * DPUString��ʽ <����> + <����> + <������־> <����> : 2byte <����> + <������־> <����>: <������־>:
	 * 1byte 0x00
	 */// /////////////////////////////////////////////////////////////////////////////////////
		// �����ģʽ SIMPLE
	public final static int CMD_SIMPLE = 0X2A; // �����ģʽ
	public final static int CMD_MAIN = 0x21; // ���û��ȡ����������
	public final static int CMD_OPEN = 0x25; // ��������\��ȫУ��\�Ͽ����Ӽ���·
	public final static int CMD_OPEN_SAFECHECK = 0X03; // ��ȫУ��
	public final static int	CMD_UPDATE = 0x24;	//�������
	// ��������
	public final int CMD_SIMPLE_SCAN = 0X00; // һ��ɨ��ϵͳ֧�ֵ��б�
	public final int CMD_SIMPLE_GETLIST = 0x01; // ��ȡ֧�ֵ�ϵͳ�б�
	public final int CMD_SIMPLE_GETDATASTREAMSELECT = 0X02; // ��ȡָ��ϵͳ�������б�
	public final int CMD_SIMPLE_GETDTC = 0X03; // ��ȡָ��ϵͳ�������б�
	public final int CMD_SIMPLE_CLEARDTC = 0X04; // ���ָ��ϵͳ������
	public final int CMD_SIMPLE_READDATESTREAM = 0X11; // ��ȡָ��ID������
	public final int CMD_SIMPLE_READLAST = 0X13; // ��ȡ�ϴ�����
	public final static int CMD_MAIN_INPUTPASS = 0x10; // ��֤��ȫ����ָ��
	public final static int CMD_OPEN_CONNECT = 0X02; // ��������
	public final static int	CMD_SHOW_GETDIALOG = 0x1C;	//��ʾ�Ի�����Ϣ
	// -----���ױ���ģ������---
	private int[] mDataFlows;
	private byte[] mIntArrayTransitByteArray;
	private SearchIdUtils searchIdUtils;
	private Context context;
	private Handler carExamHandler;
	String mDataFlowsStr = null;
	private Activity activity;
	private String m_ggppath;
	// ������
	ConditionVariable next = new ConditionVariable(false);
	// ����UI�߳�
	private final static int SIMPLE_REPORT_STEP_1 = 101; // ˢ�½�����
	private final static int SIMPLE_REPORT_STEP_2 = 102; // ˢ�½�����
	private final static int SIMPLE_REPORT_STEP_3 = 103; // ˢ�½�������Ϣ����ʾ�б�
	private final static int SIMPLE_REPORT_STEP_4 = 104; // ��ʾ������
	private final static int MSG_SHOW_ERROR_WINDOW = 105; // ��ʾ������Ϣ�Ի���
	private final static int SIMPLE_REPORT_STEP_6 = 106; // �����ı���Ϣ
	private final static int SIMPLE_REPORT_STEP_7 = 107; // �����ı����ֶ�
	private final static int SIMPLE_REPORT_STEP_8 = 108; // ��ʾ�汾��һ����ʾ
	private final static int SIMPLE_REPORT_STEP_9 = 109; // ����б�
	private final static int MSG_SHOW_UPDATE_DIAGLOG = 112;	//��ʾ��������������ʾ��
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
	// ---���ױ���ȫ�ֱ���

	// ���˷��Ͳ���
	byte[] v_sendbufInfo = null;
	// ����������
	byte[] v_dataStreamInfo = null;
	// ����ϵͳID
	ArrayList<Byte> v_sysIdList = null;
	// �����������б�
	ArrayList<Byte> v_dataStream_List = null;
	// ��������������
	int v_dataStream_Num;
	// ��¼��ȡָ��ID������ѭ������
	int totalNum = 0;
	// ��ʼ�����̲���
	int programNum = 0;
	// ������ϸ��Ϣ
	private List<HashMap<String, List<DiagnoseQuestionCategory>>> questionCateList;
	// �������б�
	Map<Integer, DiagnoseSimpleReportInfo> data_Stream_Map = null;
	// ϵͳID�ı�
	HashMap<Integer, String> v_sys_map = null;
	HashMap<String, String> docIDText = null;
	//������������¼
	HashMap<String, Integer> docNumMap = null;
	// ���ױ�����������
	private BluetoothDataService m_blue_service = null;
	// ������
	private byte[] m_commond = null;
	//��ʱ�߳�
    private DelayTimeThread delayTimeThread;
	// �����˷���
	private static DiagnoseSimpleReportDataService m_myservice = null;
	
	public synchronized static DiagnoseSimpleReportDataService getInstance() {
		if (m_myservice == null)
			m_myservice = new DiagnoseSimpleReportDataService();
		return m_myservice;
	}
	//����ַ�����Ϣ
	private DiagnoseShowInfoStr showInfoStr; 
	public void setShowInfoStr(DiagnoseShowInfoStr showInfoStr) {
		this.showInfoStr = showInfoStr;
	}

	// ���췽��
	private DiagnoseSimpleReportDataService() {
		m_commond = new byte[2];
		m_blue_service = BluetoothDataService.getInstance();
	}

	// ����context��ֵ
	public void setContext(Context context) {
		this.context = context;
	}

	// ����Handler��ֵ
	public void setHandler(Handler carExamHandler) {
		this.carExamHandler = carExamHandler;
	}

	// ����Activity��ֵ
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public void setM_ggppath(String m_ggppath) {
		this.m_ggppath = m_ggppath;
	}
	// ��ʼ��ggp����
	public void InitialGGPInstance() {
		SearchIdUtils search = SearchIdUtils.SearchIdInstance(m_ggppath);
		searchIdUtils = SearchIdUtils.SearchIdInstance("");
	}

	/**
	 * ��������(�õ�)ģʽ����
	 * 
	 * @param modeCommand
	 *            GetMode=0 SMARTBOX=1 MYCAR=2 CREADER=3 CRECORDER=4 OBD=5
	 * @since DBS V100
	 */
	public void setOrGetMode(byte[] modeCommand) {
		byte[] v_cmd = new byte[] { 0x21, 0x09 };
		byte[] v_sendbuf = modeCommand;
		if (m_blue_service != null)
			m_blue_service.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd, v_sendbuf,
					v_sendbuf.length, 1500);
	}
	/**
	 * ���͸�λģʽ����
	 * 
	 * @param modeCommand
	 *            GetMode=0 SMARTBOX=1 MYCAR=2 CREADER=3 CRECORDER=4 OBD=5
	 * @since DBS V100
	 */
	public void setResetGetMode() {
		byte[] v_cmd = new byte[] { 0x25, 0x05 };
		if (m_blue_service != null)
			m_blue_service.SendDataToBluetooth(
					BluetoothDataService.CMD_OneToOne, v_cmd,null,0, 1500);
	}
	// ��ȡ���ױ�����Ϣ
	public int ReadSampleReportStep(int step) {
		if (step == 1) // һ��ɨ��ϵͳ֧�ֵ��б�
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_SCAN;
		} else if (step == 2) // ��ȡ֧�ֵ�ϵͳ�б�
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETLIST;
		} else if (step == 3) // ��ȡָ��ϵͳ�������б�
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETDATASTREAMSELECT;
		} else if (step == 4) // ��ȡָ��ϵͳ�������б�
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETDTC;
		} else if (step == 5) // ���ָ��ϵͳ������
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_CLEARDTC;
		} else if (step == 6) // ��ȡָ��ID������
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_READDATESTREAM;
		} else if (step == 7) // ��ȡ�ϴ�����
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_READLAST;
		}
		return m_blue_service
				.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
						m_commond, null, 0, 20*MAX_TIME);
	}

	// ���ͼ��ױ���ָ������
	public void SendSampleReportCmd(int step, byte[] para) {
		byte[] paradata = GetParaByte(para);
		this.SendSampleReportAppointCmd(step, paradata);

	}

	// ��֤����
	public void EnterPassword() {
		byte[] v_password = new byte[] { 0x30, 0x30, 0x30, 0x30, 0x30, 0x30 };
		int passlen = v_password.length;
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_INPUTPASS;

		byte[] v_dpupassword = new byte[passlen + 3];
		int v_dpulen = AddDataToDpuString(v_password, passlen, v_dpupassword,
				v_dpupassword.length);
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
				m_commond, v_dpupassword, v_dpulen, MAX_TIME);
	}

	//��ȫ��֤1: 25 02 01; 2: 25 03 01 ������
	public void SafeCheckEnter(int mode, byte cmd, byte[] para) {
		byte[] paradata = null;
		int paralen = 0;
		if (mode == 1) {
			m_commond[0] = CMD_OPEN;
			m_commond[1] = CMD_OPEN_CONNECT;
			paradata = new byte[1];
			paradata[0] = 0x01;
			paralen = 1;
		} else if (mode == 2) {
			m_commond[0] = CMD_OPEN;
			m_commond[1] = CMD_OPEN_SAFECHECK;
			if (cmd == 0) {
				paradata = new byte[] { 0x00 };
			} else if (cmd == 1) {
				paradata = new byte[] { 0x01, para[0], para[1] };
			} else if (cmd == 2)
				paradata = new byte[] { 0x01, para[0], para[1], para[2],
						para[3] };
			paralen = paradata.length;
		} else
			return;
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne,
				m_commond, paradata, paralen, MAX_TIME);
	}

	// ���ض�Ӧ���ֽ�����
	public byte[] GetParaByte(byte[] para) {
		byte[] paradata = null;
		if (para == null) {
			paradata = new byte[] { (byte) 0xff, (byte) 0xff };
		} else {
			paradata = para;

		}
		return paradata;
	}

	// ���ͼ��ױ���ָ������
	public void SendSampleReportAppointCmd(int mode, byte[] para) {
		if (mode == 3) { // ��ȡָ��ϵͳ�������б�
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETDATASTREAMSELECT;
			m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
					m_commond, para, para.length, 10000);
		} else if (mode == 4) {// ��ȡָ��ϵͳ�������б�
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETDTC;
			m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
					m_commond, para, para.length, 40*1000);
		} else if (mode == 5) {// ���ָ��ϵͳ������
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_CLEARDTC;
			m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
					m_commond, para, para.length, MAX_TIME);
		} else if (mode == 6) {// ��ȡָ��ID������
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_READDATESTREAM;
			m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
					m_commond, para, para.length, MAX_TIME);
		}else if (mode == 11){
			m_commond[0] = CMD_UPDATE;
			m_commond[1] = 0x06;
			m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, null, 0, MAX_TIME);
		} 
		else
			return;
		
	}

	// ���˽������ݵİ�ͷ
	public int GetDataFromBluetooth(byte[] databuf, int datalen, byte[] Recbuf) {
		System.arraycopy(databuf, 7, Recbuf, 0, datalen - 8);
		return datalen - 8;
	}

	// ����DPU String
	public String GetDpuString(byte[] dpustr) {
		byte[] v_str = null;
		if (dpustr.length < 3)
			return "";
		int v_len = (dpustr[0] & 0xFF) * 0x100 + (dpustr[1] & 0xFF);
		v_str = new byte[v_len - 1];
		System.arraycopy(dpustr, 2, v_str, 0, v_len - 1);
		String v_return = null;
		try {
			v_return = new String(v_str, "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v_return;
	}

	// �������������DPU String
	public String GetDpuStringSpecial(byte[] dpustr) {
		byte[] v_str = null;
		if (dpustr.length < 3)
			return "";
		int v_len = (dpustr[0] & 0xFF) * 0x100 + (dpustr[1] & 0xFF);
		v_str = new byte[v_len - 2];
		System.arraycopy(dpustr, 3, v_str, 0, v_len - 2);
		String v_return = null;
		try {
			v_return = new String(v_str, "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v_return;
	}

	// ��װDPUString
	public int AddDataToDpuString(byte[] source, int len, byte[] object,
			int objlen) {
		// �ṩ�Ļ���������̫��
		if (objlen < len + 3)
			return -1;
		// ��ӵ�DPUString
		object[0] = (byte) (((len + 1) / 0x100) & 0xFF);
		object[1] = (byte) (((len + 1) % 0x100) & 0xFF);
		System.arraycopy(source, 0, object, 2, len);
		object[len + 2] = 0x00;
		return len + 3;
	}

	// ��ȡ֧�ֵ�ϵͳ�б�
	public void GetSimpleScanList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "��ȡһ��ɨ��֧�ֵ�ϵͳ�б�");
		Bundle bundle = new Bundle();// ���ڴ�������
		// ��ȡ����ID û�б���0
		bundle.putString("DIALOG_TITLE", "");
		// ��ȡ���������� 2
		v_sysIdList = new ArrayList<Byte>();
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		v_sysIdList.add(databuf[v_start]);
		v_sysIdList.add(databuf[v_start + 1]);
		// ��ȡ�ı�
		v_start += 2;
		List<DiagnoseQuestionCategory> idTextList=new ArrayList<DiagnoseQuestionCategory>();
		v_sys_map = new HashMap<Integer, String>();
		DiagnoseQuestionCategory cateText=null;
		for (int i = 0; i < v_dtc_num; i++) {
			cateText=new DiagnoseQuestionCategory();
			// ϵͳ��ID
			int v_id1 = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			cateText.setCategoryParentId(String.valueOf(v_id1));
			v_sysIdList.add(databuf[v_start]);
			v_sysIdList.add(databuf[v_start + 1]);
			v_sysIdList.add(databuf[v_start + 2]);
			v_sysIdList.add(databuf[v_start + 3]);

			v_start += 4; // ��ַ�������ۼ�
			// �ı�ID
			int v_id2 = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			cateText.setCategoryParentTextID(String.valueOf(v_id2));
			String text_id = searchIdUtils.getMessage(v_id2,
					SearchIdUtils.ID_TEXT_LIB_FILE);
			text_id = DelStringLast(text_id);
			v_sys_map.put(v_id1, text_id);
			v_start += 4; // ��ַ�������ۼ�
			int sysType = databuf[v_start] & 0xff;
			v_start += 1; //ϵͳ����
			idTextList.add(cateText);

		}
		carExamHandler.obtainMessage(SIMPLE_REPORT_STEP_1, programNum, 0,
				idTextList).sendToTarget();// ˢ��UI
	}

	// ��ȡָ��ϵͳ�������б�
	public void GetSimpleAppointDataList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "��ȡָ��ϵͳ�������б�");
		// ϵͳ����
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		// ��ȡ�������б�
		v_start += 2;
		int v_id = 0;
		v_dataStream_List = new ArrayList<Byte>();
		List<Map<String, String>> v_simple_list = new ArrayList<Map<String, String>>();
		HashMap<String, String> map = null;
		for (int i = 0; i < v_dtc_num; i++) {
			map = new HashMap<String, String>();
			// ϵͳID
			v_id = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			v_start += 4; // ��ַ�������ۼ�
			//����������
			int num = (databuf[v_start] & 0xff) * 0x100
					+ (databuf[v_start + 1] & 0xff);
			v_dataStream_Num += num;
			v_start += 2;
			for (int j = 0; j < num; j++) {
				// �������б�
				v_id = (databuf[v_start] & 0xff) * 0x1000000
						+ (databuf[v_start + 1] & 0xff) * 0x10000
						+ (databuf[v_start + 2] & 0xff) * 0x100
						+ (databuf[v_start + 3] & 0xff);
				v_dataStream_List.add(databuf[v_start]);
				v_dataStream_List.add(databuf[v_start + 1]);
				v_dataStream_List.add(databuf[v_start + 2]);
				v_dataStream_List.add(databuf[v_start + 3]);
				String questionList = searchIdUtils.getMessage(v_id,
						SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
				questionList = DelStringLast(questionList);
				map.put("dataStreamName", questionList);  
				v_start += 4; // ��ַ�������ۼ�
				v_simple_list.add(map);

			}
		}
		carExamHandler.obtainMessage(SIMPLE_REPORT_STEP_8, programNum, 0,
				null).sendToTarget();
	}

	// �õ�ָ��������PID����
	private void getSimeplePID() {
		if (v_sysIdList != null && v_sysIdList.size() > 0) {
			v_sendbufInfo = new byte[v_sysIdList.size()];
			for (int i = 0; i < v_sysIdList.size(); i++) {
				v_sendbufInfo[i] = v_sysIdList.get(i);
			}
		}
	}

	// �õ�ָ��Id�������б�����
	private void getSimepleDataStreamInfo() {
		if (v_sysIdList != null && v_sysIdList.size() > 0) {
			v_dataStreamInfo = new byte[v_dataStream_List.size() + 2];
			v_dataStreamInfo[0] = (byte) ((v_dataStream_Num / 0x100) & 0xFF);
			v_dataStreamInfo[1] = (byte) ((v_dataStream_Num % 0x100) & 0xFF);
			for (int i = 0; i < v_dataStream_List.size(); i++) {
				v_dataStreamInfo[i + 2] = v_dataStream_List.get(i);
			}
		}
	}

	// ��ȡָ��ϵͳ�������б�
	public void GetSimpleAppointQuestionList(byte[] databuf, int datalen) {
		int docTotalNum = 0;
		docNumMap=new HashMap<String,Integer>();
		if (D)
			Log.i(TAG, "��ȡָ��ϵͳ�������б�");
		// ��ȡ���������� 2
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		v_start += 2;
		int v_id = 0;
		String question_id;
		String question_status;
		String question_code;
		questionCateList = new ArrayList<HashMap<String, List<DiagnoseQuestionCategory>>>();
		HashMap<String, List<DiagnoseQuestionCategory>> map = null;
		for (int i = 0; i < v_dtc_num; i++) {
			map = new HashMap<String, List<DiagnoseQuestionCategory>>();
			// ϵͳID
			int sys_id = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			// map.put("SIMPLE_SYS_ID", sys_sys_id);
			v_start += 4; // ��ַ�������ۼ�
			int num = (databuf[v_start] & 0xff) * 0x100
					+ (databuf[v_start + 1] & 0xff);
			//��¼ÿ��ϵͳID�Ĺ�������
			docNumMap.put(String.valueOf(sys_id), num);
			docTotalNum += num;
			v_start += 2;
			List<DiagnoseQuestionCategory> categoryList = new ArrayList<DiagnoseQuestionCategory>();
			for (int j = 0; j < num; j++) {
				DiagnoseQuestionCategory categoryInfo = new DiagnoseQuestionCategory();
				List<Map<String, String>> v_simple_list = new ArrayList<Map<String, String>>();
				// ��ȡ������ID
				v_id = (databuf[v_start] & 0xff) * 0x1000000
						+ (databuf[v_start + 1] & 0xff) * 0x10000
						+ (databuf[v_start + 2] & 0xff) * 0x100
						+ (databuf[v_start + 3] & 0xff);
				String category_Id=String.valueOf(v_id);
				categoryInfo.setCategoryId(category_Id);
				question_id = searchIdUtils.getMessage(v_id,
						SearchIdUtils.ID_TROUBLE_CODE_LIB_FILE);
				if(!question_id.equals("")){
					String docNo =DatastreamGetName(question_id); //question_id.substring(0, 5);
					categoryInfo.setQuestionNum(docNo);
					String questionInfo =DatastreamGetUnit(question_id); //DelStringLast(question_id.substring(5))
					categoryInfo.setQuestionInfo(questionInfo);
				}else{
					byte[] docIDs=new byte[]{databuf[v_start],databuf[v_start + 1],databuf[v_start + 2],databuf[v_start + 3]};
					String docNo = bytesToHexStringHasBar(docIDs);				
					categoryInfo.setQuestionNum(showInfoStr.getIDNotdefinedStr());
					categoryInfo.setQuestionInfo("["+docNo+"]");
				}
				
				v_start += 4; // ��ַ�������ۼ�
				// ��ȡ������״̬
				v_id = (databuf[v_start] & 0xff) * 0x1000000
						+ (databuf[v_start + 1] & 0xff) * 0x10000
						+ (databuf[v_start + 2] & 0xff) * 0x100
						+ (databuf[v_start + 3] & 0xff);
				question_status = searchIdUtils.getMessage(v_id,
						SearchIdUtils.ID_TROUBLE_CODE_STATUS_LIB_FILE);
				question_status = DelStringLast(question_status);
				categoryInfo.setCategoryStatus(question_status);

				v_start += 4; // ��ַ�������ۼ�
				// ������ Code DPUString
				v_id = (databuf[v_start] & 0xff) * 0x100
						+ (databuf[v_start + 1] & 0xff) + 2;
				byte[] v_dtc_id = new byte[v_id];
				// ��ֹϵͳ�����������
				if (v_id > databuf.length - v_start)
					v_id = databuf.length - v_start;
				System.arraycopy(databuf, v_start, v_dtc_id, 0, v_id);
				question_code = GetDpuString(v_dtc_id);
				question_code = FindDpuId(question_code); // ���������DPU_ID
				question_code = DelStringLast(question_code);
				categoryInfo.setCategoryParentId(String.valueOf(sys_id));
				if(!v_sys_map.get(sys_id).equals("")){
					categoryInfo.setCategoryParentStr(v_sys_map.get(sys_id));
				}
				else{
					categoryInfo.setCategoryParentStr(showInfoStr.getDataNameNotdefinedStr());
				}
				v_start += v_id;
				categoryList.add(categoryInfo);
			}
			map.put(DiagnoseConstant.V_DOC_PIDID, categoryList);
			questionCateList.add(map);
		}
		int gradeBigNum=0;
		int gradeSmallNum=0;
		Iterator iter = docNumMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();		
			String key = (String) entry.getKey();
			int val = (Integer) entry.getValue();
			if(key.equals("4128")){			 
			    gradeBigNum+=val;
			}else{
				gradeSmallNum+=val;
			}
		}
		int mark=(int)Math.round(60/(gradeBigNum+1))+(40/(gradeSmallNum+1));
		carExamHandler.obtainMessage(SIMPLE_REPORT_STEP_4, mark, docTotalNum,
				questionCateList).sendToTarget();
	}
	//��öԻ����ı���Ϣ CMD_SHOW_GETDIALOG
	public void GetShowDialog(byte[] databuf, int datalen)
	{
		if(D) Log.i(TAG,"�ı��Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		int v_start = 0;
		//����Ի�������
		int v_style = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		bundle.putInt("DIALOG_STYLE", v_style);  //�Ի�������
		//����Ի������
		v_start+=2;
		if(v_style!=5){
			int v_title_len = (databuf[v_start]&0xFF) * 0x100 + (databuf[v_start+1]&0xFF) + 2; //DPUSting����
			byte [] v_title = new byte[v_title_len];
			System.arraycopy(databuf, v_start, v_title, 0, v_title_len);
			String v_show = GetDpuString(v_title);
			v_show = FindDpuId(v_show); //���������DPU_ID
			v_show = DelStringLast(v_show);
			bundle.putString("DIALOG_TITLE", v_show);
			v_start+=v_title_len;
			//����Ի����ı�
			int v_msg_len = (databuf[v_start]&0xFF) * 0x100 + (databuf[v_start+1]&0xFF) + 2;
			byte [] v_msg = new byte[v_msg_len];
			System.arraycopy(databuf, v_start, v_msg, 0, v_msg_len);
			String v_text = GetDpuString(v_msg);
			v_text = FindDpuId(v_text); //���������DPU_ID
			v_text = DelStringLast(v_text);
			if(v_text.equals("")){
				v_text = showInfoStr.getNoSupport();
			}
			bundle.putString("DIALOG_BODY", v_text);
//	        carExamHandler.obtainMessage(CMD_SHOW_GETDIALOG, 0, 0,
//	        		bundle).sendToTarget();
			//ϵͳ��֧��
			carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,7,0).sendToTarget();
		}
	}
	// ���ָ��ϵͳ������
	public void ClearDTCList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "���ָ��ϵͳ������");
		// ��ȡ���������� 2
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		v_start += 2;
		int v_id = 0;
		List<Map<String, String>> v_simple_list = new ArrayList<Map<String, String>>();
		HashMap<String, String> map = null;
		for (int i = 0; i < v_dtc_num; i++) {
			map = new HashMap<String, String>();
			// ϵͳID
			v_id = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			String sys_sys_id = String.valueOf(v_id);
			map.put("SIMPLE_SYS_ID", sys_sys_id);
			v_start += 4; // ��ַ�������ۼ�
			// ������
			v_id = (databuf[v_start] & 0xff);
			String clear_result = String.valueOf(v_id);
			map.put("CLEAR_RESULT", clear_result);
			v_start += 1;
			v_simple_list.add(map);

		}
	}

	// ��ȡָ��ID������
	public void GetSimpleAppointIDList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "��ȡָ��ID������");
		// ��ȡ���������� 2
		int v_start = 0;
		int v_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		v_start += 2;
		int v_id = 0;
		int only_Id = 0;
		for (int i = 0; i < v_num; i++) {
			// ������ID
			v_id = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			only_Id = v_id;
			String data_Stream_Id = searchIdUtils.getMessage(v_id,
					SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
			String v_name = DatastreamGetName(data_Stream_Id);
			String v_unit = DatastreamGetUnit(data_Stream_Id);
			data_Stream_Id = v_name;
			if(!v_unit.equals("")){
				data_Stream_Id+="("+v_unit+")";
			}
			if(data_Stream_Id.equals("")){
				data_Stream_Id = showInfoStr.getDataNameNotdefinedStr();
			}
			v_start += 4; // ��ַ�������ۼ�
			// ������������
			v_id = (databuf[v_start] & 0xff) * 0x100
					+ (databuf[v_start + 1] & 0xff) + 2;
			byte[] v_reselt = new byte[v_id];
			// ��ֹϵͳ�����������
			if (v_id > databuf.length - v_start)
				v_id = databuf.length - v_start;
			System.arraycopy(databuf, v_start, v_reselt, 0, v_id);
			String result = GetDpuStringSpecial(v_reselt);
			result = FindDpuId(result); // ���������DPU_ID
			result = DelStringLast(result);

			v_start += v_id; // ��ַ�ۼ�
			DiagnoseSimpleReportInfo info = null;
			int converType=1;
			boolean flag = data_Stream_Map.containsKey(only_Id);
			try{
				if (OrderUtils.oneByteToHexString(v_reselt[2]).equals("0x81")) {
					if (flag) {
						info = (DiagnoseSimpleReportInfo) data_Stream_Map
								.get(only_Id);
						info.setDataStreamValue(result);
					} else {
						info = this.getDiagnoseSimpleReportInfoEntry(
								data_Stream_Id, result,converType);
	
					}
				} else if (OrderUtils.oneByteToHexString(v_reselt[2])
						.equals("0x83")) {
	
					if (flag) {
						info = (DiagnoseSimpleReportInfo) data_Stream_Map
								.get(only_Id);
						info.setDataStreamValue(String.valueOf(Float
								.parseFloat(result)));
						info.setMaxvalue(getMaxOrMinValue(1, only_Id, result));
						info.setMinvalue(getMaxOrMinValue(2, only_Id, result));
					} else {
						converType=2;
						info = this.getDiagnoseSimpleReportInfoEntry(
								data_Stream_Id, result,converType);
	
					}
				} else if (OrderUtils.oneByteToHexString(v_reselt[2])
						.equals("0x84")) {
	
					if (flag) {
						info = (DiagnoseSimpleReportInfo) data_Stream_Map
								.get(only_Id);
						info.setDataStreamValue(String.valueOf(Float
								.parseFloat(result)));
						info.setMaxvalue(getMaxOrMinValue(1, only_Id, result));
						info.setMinvalue(getMaxOrMinValue(2, only_Id, result));
					} else {
						converType=2;
						info = this.getDiagnoseSimpleReportInfoEntry(
								data_Stream_Id, result,converType);
					}
				} else if (OrderUtils.oneByteToHexString(v_reselt[2])
						.equals("0x85")) {
	
					if (flag) {
						info = (DiagnoseSimpleReportInfo) data_Stream_Map
								.get(only_Id);
						info.setDataStreamValue(result);
					} else {
						info = this.getDiagnoseSimpleReportInfoEntry(
								data_Stream_Id, result,converType);
	
					}
				}
				else{
					if (flag) {
						info = (DiagnoseSimpleReportInfo) data_Stream_Map
								.get(only_Id);
					} else {
						 info = new DiagnoseSimpleReportInfo();
					     info.setDataStreamName(data_Stream_Id);
					}
				      
				}
			}catch(Exception ex){
				info = new DiagnoseSimpleReportInfo();
				info.setDataStreamName(data_Stream_Id);
				ex.printStackTrace();
			}
			data_Stream_Map.put(only_Id, info);
		}

		carExamHandler.obtainMessage(SIMPLE_REPORT_STEP_8, programNum, 0, null)
				.sendToTarget();
	}
	// ��ʼ��DiagnoseSimpleReportInfo����
	public DiagnoseSimpleReportInfo getDiagnoseSimpleReportInfoEntry(
			String data_Stream_Id, String result,int type) {
		DiagnoseSimpleReportInfo info = new DiagnoseSimpleReportInfo();
		info.setDataStreamName(data_Stream_Id);
		if(type==1){
			info.setDataStreamValue(result);
		}else{
			info.setDataStreamValue(String.valueOf(Float.parseFloat(result)));
			info.setMaxvalue(String.valueOf(Float.parseFloat(result)));
			info.setMinvalue(String.valueOf(Float.parseFloat(result)));
		}
		
		info.setCauseResult("");
		info.setHelpAdvice("");
		return info;
	}

	// ��ȡ���ͻ�ȡ���ֵ������Сֵ
	public String getMaxOrMinValue(int BigSmalltype, int key,
			String currentvalue) {
		DiagnoseSimpleReportInfo info = (DiagnoseSimpleReportInfo) data_Stream_Map
				.get(key);
		float num;
		if (BigSmalltype == 1) {

			num = Float.parseFloat(currentvalue) > Float.parseFloat(info
					.getMaxvalue()) ? Float.parseFloat(currentvalue) : Float
					.parseFloat(info.getMaxvalue());
		} else {

			num = Float.parseFloat(currentvalue) < Float.parseFloat(info
					.getMinvalue()) ? Float.parseFloat(currentvalue) : Float
					.parseFloat(info.getMinvalue());
		}
		return String.valueOf(num);
	}

	// ����DPU_ID
	public String FindDpuId(String str) {
		String v_new = str;
		int id_size = 0;
		while (true) {
			id_size = v_new.indexOf("[DPU_ID", id_size);
			if (id_size >= 0)// �鵽��
			{
				String v_idbuf = v_new.substring(id_size + 9, id_size + 17);
				byte v_fileID = (byte) v_new.charAt(id_size + 7);
				int searchID = HexStringtoInt(v_idbuf);
				String id_text = searchIdUtils.getMessage(searchID,
						(v_fileID & 0xFF));
				id_text = DelStringLast(id_text);
				// �滻��PDU_ID
				String v_data = v_new.substring(0, id_size);
				v_data += id_text
						+ v_new.substring(id_size + 18, v_new.length());
				v_new = v_data;
			} else {
				break;
			}
		}
		return v_new;
	}

	public static int HexStringtoInt(String hexstr) {
		int x = 0, iRet = 0;
		if (hexstr == null)
			return iRet;
		int length = hexstr.length();
		for (int i = 0; i < length; i++) {
			char c = hexstr.charAt(i);
			// ��'0'->0��'a'->10
			if (c >= 'a' && c <= 'f') {
				x = c - 'a' + 10;
			} else if (c >= 'A' && c <= 'F') {
				x = c - 'A' + 10;
			} else if (c >= '0' && c <= '9') {
				x = c - '0';
			}
			iRet = (iRet << 4) | x;// n=n*4+x,��λƴ��
		}
		return iRet;
	}

	// ɾ��String�����00
	public static String DelStringLast(String v_str) {
		if (v_str == null)
			return "";
		int v_len = v_str.length();
		if (v_len < 1)
			return "";
		int i = 0;
		for (i = v_len - 1; i > 0; i--) {
			if (v_str.charAt(i) != 0x00)
				break;
		}
		v_str = v_str.substring(0, i + 1);
		return v_str;
	}
	public void GetDataFromService(byte[] databuf, int datalen) {
		//if (D)	Log.i("databuf", Arrays.toString(databuf));
		byte[] dpuPackage = OrderUtils.filterReturnDataPackage(databuf);
		//if (D)	Log.i("tag", Arrays.toString(dpuPackage));
		byte[] cmd_subcmd = OrderUtils.filterOutCommand(dpuPackage);
		byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);// �������ֽ�
		String info = "";
		byte[] v_recv_buf = new byte[datalen - 8];
		int v_recv_len = this.GetDataFromBluetooth(databuf, datalen, v_recv_buf);
		// ����ģʽ
		String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);
		if (cmd_subcmdString.equals("6109")) {
			if (param.length == 2 && param[0] != 0x06 && param[1] == 0x00) {
				this.setOrGetMode(new byte[] { 0x06 });
				next.set(true);
			} else {
				if (param.length == 2 && param[0] == 0x06 && param[1] == 0x00) {					
					//�ӳ�����
					delayTimeThread=new DelayTimeThread(DT_CMD_MODE,3000,null);
					delayTimeThread.start();
					delayTimeThread=null;
					next.set(true);
				}
			}
		}
		// ������֤
		else if (cmd_subcmdString.equals("6110")) {
			if (param.length > 0 && param[0] == 0x00) {
				this.SafeCheckEnter(1, (byte) 0, null);
				next.set(true);
			} else {
//				Log.e(TAG,"������֤ʧ�ܣ�");
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,1,0).sendToTarget();
				
			}
		}
		// ��ȫ��֤1
		else if (cmd_subcmdString.equals("6502")) {
			try{
				// ������֤
				byte[] v_key = null;
				if (param.length > 0 && param[0] == 0x01) {
					v_key = new byte[] { param[1], param[2] };
					this.SafeCheckEnter(2, param[0], v_key);
					next.set(true);
					this.clearExamData();
					this.InitialGGPInstance();
					programNum = 0;
					programNum += 10;
					carExamHandler.obtainMessage(SIMPLE_REPORT_STEP_7, programNum, 0,
							null).sendToTarget();// ˢ��UI
				} else {
	//				Log.e(TAG,"��ȫ��֤ʧ�ܣ�");
					carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,2,0).sendToTarget();
				}
				
			}catch(Exception ex){			
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,2,0).sendToTarget();
				ex.printStackTrace();
			}
		}
		// ��ȫ��֤2
		else if (cmd_subcmdString.equals("6503")) {
			try{
				if (param.length > 0 && param[1] == 0x00) {
					//��֤�Ƿ������ɹ�
					this.SendSampleReportAppointCmd(11,null);
					next.set(true);
				} else {
	//				Log.e(TAG,"��ȫ��֤ʧ�ܣ�");
					carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,2,0).sendToTarget();
				}
			}catch(Exception ex){			
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,2,0).sendToTarget();
				ex.printStackTrace();
			}
		}
		//�ȴ��Ի���
		else if (cmd_subcmdString.equals("221C")) {
			if(D) Log.i(TAG,"��ʾ���˵�");
			GetShowDialog(param, 0);
			next.set(true);
		}
		//ϵͳ��֧�ֵ����Ի���
		else if (cmd_subcmdString.equals("2210")) {
			if(D) Log.i(TAG,"��ʾ���˵�");
			carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,7,0).sendToTarget();
			next.set(true);
		}
		// ��֤�Ƿ������ɹ�
		else if (cmd_subcmdString.equals("6406")) {
			if (ReadUpdateComplete(param,v_recv_len)==0) {
				this.ReadSampleReportStep(2);
			} else {
				carExamHandler.obtainMessage(MSG_SHOW_UPDATE_DIAGLOG,0,0).sendToTarget();
			}
		}
		// һ��ɨ��ϵͳ֧�ֵ��б�
		else if (cmd_subcmdString.equals("6A00")) {
			if (param.length > 0) {
				try {
					programNum += 20;
					GetSimpleScanList(param, 0);
					this.getSimeplePID();
					int sysNum=this.getSysNum(param);
					if(sysNum>0){
						delayTimeThread=new DelayTimeThread(DT_APPOINT_DATA_LIST,300,v_sendbufInfo);
						delayTimeThread.start();
						delayTimeThread=null;
					}
					else{
						carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,8,0).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,3,0).sendToTarget();
			}
		}
		// ��ȡ֧�ֵ�ϵͳ�б�
		else if (cmd_subcmdString.equals("6A01")) {
			if (param.length > 0) {
				try {
					programNum += 20;
					GetSimpleScanList(param, 0);
					this.getSimeplePID();
					int sysNum=this.getSysNum(param);
					if(sysNum>0){
//						delayTimeThread=new DelayTimeThread(DT_APPOINT_DATA_LIST,300,v_sendbufInfo);
//						delayTimeThread.start();
//						delayTimeThread=null;
//						v_sendbufInfo =new  byte[]{(byte)0xff,(byte)0xff};
						delayTimeThread=new DelayTimeThread(DT_APPOINT_DOC_LIST ,200,v_sendbufInfo);
						delayTimeThread.start();
						delayTimeThread=null;
					}
					else{
						carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,8,0).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,3,0).sendToTarget();
			}
		}
		// ��ȡָ��ϵͳ�������б�
		else if (cmd_subcmdString.equals("6A02")) {
			if (param.length > 0) {
				try {
					programNum += 10;
					GetSimpleAppointDataList(param, 0);
					getSimepleDataStreamInfo();
					data_Stream_Map = new HashMap<Integer, DiagnoseSimpleReportInfo>();
					totalNum = 0;
					data_Stream_Map.clear();
					delayTimeThread=new DelayTimeThread(DT_APPOINT_ID_LIST ,200,v_dataStreamInfo);
					delayTimeThread.start();			
					delayTimeThread=null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
//				Log.e(TAG,"û��ָ��ϵͳ�������б�");
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,4,0).sendToTarget();
			}	

		}
		// ��ȡָ��ID������
		else if (cmd_subcmdString.equals("6A11")) {
			if (param.length > 0) {
				try {
					programNum += 3;
					GetSimpleAppointIDList(param, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}			
				totalNum += 1;
				if (totalNum < 10) {
					delayTimeThread=new DelayTimeThread(DT_APPOINT_ID_LIST ,200,v_dataStreamInfo);
					delayTimeThread.start();
					delayTimeThread=null;
				} else {
					List<Map<String, String>> v_simple_list = new ArrayList<Map<String, String>>();
					HashMap<String, String> map = null;
					Iterator iter = data_Stream_Map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						map = new HashMap<String, String>();
						DiagnoseSimpleReportInfo val = (DiagnoseSimpleReportInfo) entry
								.getValue();
						map.put("dataStreamName", val.getDataStreamName());
						map.put("dataStreamValue", val.getDataStreamValue());
						map.put("maxvalue", val.getMaxvalue());
						map.put("minvalue", val.getMinvalue());
						map.put("causeResult", val.getCauseResult());
						map.put("helpAdvice", val.getHelpAdvice());
						v_simple_list.add(map);					     
					}
					carExamHandler.obtainMessage(SIMPLE_REPORT_STEP_3, programNum,
							0, v_simple_list).sendToTarget();
					programNum = 0;
					totalNum = 0;
					v_dataStream_Num=0;
					data_Stream_Map.clear();
					delayTimeThread=new DelayTimeThread(DT_APPOINT_DOC_LIST ,200,v_sendbufInfo);
					delayTimeThread.start();
					delayTimeThread=null;
//					this.SendSampleReportCmd(4, v_sendbufInfo);	
				}
			} else {
//				Log.e(TAG,"û��ָ��ID��������");
				carExamHandler.obtainMessage(MSG_SHOW_ERROR_WINDOW,5,0).sendToTarget();
			}
		}
		// ��ȡָ��ϵͳ�������б�
		else if (cmd_subcmdString.equals("6A03")) {
			if (param.length > 0) {
				try {
					GetSimpleAppointQuestionList(param, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			else {
//				Log.e(TAG,"û��ָ��ϵͳ�������б�");
//			}
		}

	}

	// ---�������---
	// ��byteת��Ϊ�ַ���
	public static final String bytesToHexStringNoBar(int[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		int len = bArray.length;
		for (int i = 0; i < len; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append("0");
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
   //������ID�ַ���
	public String saveDocIDStr(List<HashMap<String, List<DiagnoseQuestionCategory>>> list){
		 String docIDStr="";
	     for(int i=0;i<list.size();i++){
	    	 HashMap<String, List<DiagnoseQuestionCategory>> map=list.get(i);
	    	 List<DiagnoseQuestionCategory> questionList=map.get(DiagnoseConstant.V_DOC_PIDID);
	    	 docIDStr+=questionList.get(0).getCategoryParentId()+"|";
	    	 for(int j=0;j<questionList.size();j++){
	    		 DiagnoseQuestionCategory questionCate=questionList.get(j);
	    		 if(j<questionList.size()-1){
	    		  docIDStr+=questionCate.getCategoryId()+",";
	    		 }
	    		 else{
	    		  docIDStr+=questionCate.getCategoryId()+" "; 
	    		 }
	    	 }	    	 
	    	 
	     }
		return docIDStr;
	}
	//ͨ��������ID��ѯ��ϸ��Ϣ
	public List<HashMap<String, List<DiagnoseQuestionCategory>>>  GetSimpleAppointQuestionList(String docStr){		
		if(docStr!=null&&!docStr.equals("")){
			questionCateList = new ArrayList<HashMap<String, List<DiagnoseQuestionCategory>>>();
			String[] docInfos=docStr.split(" ");
			//ͨ�������ȡϵͳId�ı�
			SharedPreferences preQuestionInfo=context.getSharedPreferences(DiagnoseConstant.PRE_QUESTION_LIST_PREFS, Context.MODE_WORLD_WRITEABLE);
			DiagnoseQuestionCategory docCateInfo=null;
			HashMap<String, List<DiagnoseQuestionCategory>> map = null;
			for(String info:docInfos){
				int index=info.indexOf("|");
				String sysId=info.substring(0, index);
				String text_info=preQuestionInfo.getString(sysId, "");
				map=new HashMap<String, List<DiagnoseQuestionCategory>>();
				List<DiagnoseQuestionCategory> categoryList = new ArrayList<DiagnoseQuestionCategory>();
				//��ȡϵͳId�ı�
				String docIdStrs=info.substring(index+1,info.length());
				String[] docIds=docIdStrs.split(","); 
				for(String docId:docIds){
					docCateInfo=new DiagnoseQuestionCategory();
					String question_text =preQuestionInfo.getString(docId, "");
					if(!question_text.equals("")){
						int questIndex=question_text.indexOf("|");
						String docNo = question_text.substring(0, questIndex);
						docCateInfo.setQuestionNum(docNo);
						String questionInfo =question_text.substring(questIndex+1).trim();					
						docCateInfo.setQuestionInfo(questionInfo);
					}
					docCateInfo.setCategoryParentId(sysId);
					docCateInfo.setCategoryParentStr(text_info);
					categoryList.add(docCateInfo);
				}
				map.put(DiagnoseConstant.V_DOC_PIDID, categoryList);
				questionCateList.add(map);
			}
		}
		return questionCateList;
	}
	//��ȡ����ID��Ϣ
//	public int GetDiagErrorID(int err_id)
//	{
//		int v_id = 0;
//		switch(err_id)
//		{
//		case 0:
//			v_id = R.string.diag_sp_error_00;
//			break;
//		case 1:
//			v_id = R.string.diag_sp_error_01;
//			break;
//		case 2:
//			v_id = R.string.diag_sp_error_02;
//			break;
//		case 3:
//			v_id = R.string.diag_sp_no_sys_list;
//			break;
//		case 4:
//			v_id = R.string.diag_sp_no_appoint_data_list;
//			break;
//		case 5:
//			v_id = R.string.diag_sp_no_appoint_id_list;
//			break;
//		case 6:
//			v_id = R.string.diag_sp_bluetooth_connection_lost;
//			break;
//		case 7:
//			v_id = R.string.diag_sp_sys_no_support;
//			break;
//		case 8:
//			v_id = R.string.diag_sp_sys_data_empty;
//			break;
//		default:
//			v_id = R.string.diag_error_00;
//			break;
//		}
//		return v_id;
//	}
	private final static int DT_CMD_MODE = 1; // ����ģʽ��ʱ
	private final static int DT_APPOINT_DATA_LIST = 2; // ��ȡָ��ϵͳ�������б��ʱ
	private final static int DT_APPOINT_ID_LIST = 3; // ��ȡָ��ID��������ʱ
	private final static int DT_APPOINT_DOC_LIST = 4; //��ȡָ��ϵͳ�������б��ʱ
	 /*
     * ��ʱ�߳�
     */
    class  DelayTimeThread extends Thread
    {
        int cmdType;
        int time=1000;
        byte[] param=null;

        public DelayTimeThread(int cmdType,int time, byte[] param)
        {
            this.cmdType = cmdType;
            this.time=time;
            this.param=param;
        }

        @Override
        public void run()
        {
            try
            {
                this.sleep(time);
            	carExamHandler.obtainMessage(cmdType, param).sendToTarget();

            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }         
           
        }
    }

	//����ϴβ�ѯ������ʼ��
	public void clearExamData(){
		// ���˷��Ͳ���
		if(v_sendbufInfo!=null){
			v_sendbufInfo=null;
		}
		// ����������
		if(v_dataStreamInfo!=null){
			v_dataStreamInfo=null;
		}
		// ϵͳID�б�
		if(v_sysIdList!=null&&v_sysIdList.size()>0){
			v_sysIdList=null;
		}
		// �������б�
		if(v_dataStream_List!=null&&v_dataStream_List.size()>0){
			v_dataStream_List=null;
		}
		// ����������
		v_dataStream_Num=0;
		// ��¼��ȡָ��ID������ѭ������
		totalNum=0;
		// ��ʼ�����̲���
		programNum=0;
		// ������ϸ��Ϣ
		if(questionCateList!=null){
			questionCateList=null;
		}
		// �������б�
		if(data_Stream_Map!=null){
			data_Stream_Map=null;
		}		
		if(v_sys_map!=null){
			v_sys_map=null;
		}
		// ϵͳID�ı�
		if(docIDText!=null){
			docIDText=null;
		}
		//������������¼
		if(docNumMap!=null){
			docNumMap=null;
		}
		if(delayTimeThread!=null){
			delayTimeThread=null;
		}
	}
	//���������������λ��ֻ��ʾ����������
		public static String DatastreamGetName(String v_str)
		{
			String v_retu = null;
			if(v_str == null || v_str.length() < 1)
				return "";
			int v_len = v_str.length();
			int v_find = 0;
			for(int i = v_len - 1; i > 0; i --)
			{
				if(v_str.charAt(i) == 0x00)
				{
					v_find ++;
					if(v_find == 2)
					{
						v_retu = v_str.substring(0,i);
						break;
					}
				}
			}
			return v_retu;
		}
		//�����������������,ֻ��ʾ��λ
		public static String DatastreamGetUnit(String v_str)
		{
			String v_retu = null;
			if(v_str == null || v_str.length() < 1)
				return "";
			int v_len = v_str.length();
			int v_find = 0;
			for(int i = v_len - 1; i > 0; i --)
			{
				if(v_str.charAt(i) == 0x00)
				{
					v_find ++;
					if(v_find == 2)
					{
						v_retu = v_str.substring(i + 1,v_len - 1);
						break;
					}
				}
			}
			return v_retu;
		}
	// ��ʼ��ggp����
		public void closeGGPInstance() {
			SearchIdUtils search = SearchIdUtils.SearchIdInstance(null);
			if(search!=null)
        	   search.CloseFile();
		}
		/**@author liachuanhai
		 * ���ֽ�����ת����16�����ַ���,���ָ���
		 * @param bArray
		 * @return
		 */
		public static final String bytesToHexStringHasBar(byte[] bArray) {
		    StringBuffer sb = new StringBuffer(bArray.length);
		    String sTemp;
		    int len = bArray.length;
		    int last = len -1;
		    for (int i = 0; i < len; i++)
		    {
			     sTemp = Integer.toHexString(0xFF & bArray[i]);
			     if (sTemp.length() < 2)
			       sb.append("0");
			     sb.append(sTemp.toUpperCase());
			     if(i!=last)
			       sb.append(",");
		    }
		    return sb.toString();
		}
	//���ϵͳ�ĸ���
	public int getSysNum(byte[] databuf){
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		return v_dtc_num;
	}
	//��ȡ�ϵ������Ƿ��������
	public int ReadUpdateComplete(byte[] para,int datalen)
	{
		//��3��DPUString����
		int v_start = 0;
		int v_len = (para[v_start]&0xFF)* 0x100 + (para[v_start+1] &0xFF) + 2;
		v_start+=v_len;
		int v_len1 = (para[v_start]&0xFF)* 0x100 + (para[v_start + 1] &0xFF) + 2;
		v_start+=v_len1;
		int v_len2 = (para[v_start]&0xFF)* 0x100 + (para[v_start + 1] &0xFF) + 2;
		v_start+=v_len2;
		if(v_len1 < datalen)
			return para[v_start]&0xFF;
		else
			return 0xFF;
	}
}

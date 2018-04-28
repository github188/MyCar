package com.cnlaunch.mycar.diagnose.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import launch.SearchIdUtils;

import android.os.Bundle;
import android.util.Log;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.bluetooth.service.BluetoothInterface;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseQuestionCategory;
import com.cnlaunch.mycar.diagnose.simplereport.model.DiagnoseSimpleReportInfo;
import com.cnlaunch.mycar.diagnose.util.OrderUtils;
import com.cnlaunch.mycar.updatecenter.ConditionVariable;

public class DiagnoseUnifiedDataStreamService implements BluetoothInterface {
	private static final String TAG = "DiagnoseUnifiedDataStreamService";
	private static final boolean D = false;
	public final int MAX_TIME = 1000; // ��ʱʱ��
	// ��ʼ����������
	private BluetoothDataService m_blue_service = null;
	// ��ʱ�߳�
	private DelayTimeThread delayTimeThread;
	// ������
	ConditionVariable next = new ConditionVariable(false);
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

	// ��������
	public final static int CMD_MAIN_INPUTPASS = 0x10; // ��֤��ȫ����ָ��
	public final static int CMD_OPEN_CONNECT = 0X02; // ��������
	public final static int CMD_SHOW_GETDIALOG = 0x1C; // ��ʾ�Ի�����Ϣ
	public final int CMD_SIMPLE_READDATESTREAM = 0X11; // ��ȡָ��ID������
	public final int CMD_SIMPLE_SCAN = 0X00; // һ��ɨ��ϵͳ֧�ֵ��б�
	public final int CMD_SIMPLE_GETLIST = 0x01; // ��ȡ֧�ֵ�ϵͳ�б�
	public final int CMD_SIMPLE_GETDATASTREAMSELECT = 0X02; // ��ȡָ��ϵͳ�������б�
	// ��������
	private ReceiveReuqestDataThread reqDataThread = null;
	private final static int DT_CMD_MODE = 1; // ����ģʽ��ʱ
	private final static int DT_APPOINT_DATA_LIST = 2; // ��ȡָ��ϵͳ�������б��ʱ
	private final static int DT_APPOINT_ID_LIST = 3; // ��ȡָ��ID��������ʱ
	//��ʱ�߳�
	private TimeOutThread timeOutThread;
	// ���˷��Ͳ���
	byte[] v_sendSystIDInfo = null;
	// ��ǰֵ
	private static String CURRENT_VALUE = "";
	// �����˷���
	private static DiagnoseUnifiedDataStreamService m_myservice = null;
	boolean flag = false;
	public synchronized static DiagnoseUnifiedDataStreamService getInstance() {
		if (m_myservice == null)
			m_myservice = new DiagnoseUnifiedDataStreamService();
		return m_myservice;
	}
	// ���췽��
	private DiagnoseUnifiedDataStreamService() {
		m_commond = new byte[2];
		m_blue_service = BluetoothDataService.getInstance();
	}
	
	// ���˷��Ͳ���
	byte[] v_sendbufInfo = null;
	// ����ϵͳID
	ArrayList<Byte> v_sysIdList = null;
	// �����������б�
	ArrayList<Byte> v_dataStream_List = null;
	//֧��ͳһ��������ID
	HashMap<String, String> unifDataStreamMap = null;
	//�ж��Ƿ���Ҫϵͳɨ��
	boolean isSysScan=true;
	//ǰ�˴��ݹ�������
	private String keyStr="";
	// ���ó�����̵�ǰֵ
	public static void setCarMileageCurrentValue(String key, String value) {

	}
	// ��ȡ������̵�ǰֵ
	public String getCarMileageCurrentValue(String key) {
		keyStr=key;//���ݲ���
		CURRENT_VALUE="";
		// ��ӹ۲���
		m_blue_service.AddObserver(this);
		if (m_blue_service != null) {
			v_sendbufInfo = new byte[6];
			v_sendbufInfo[0] = 0x00;
			v_sendbufInfo[1] = 0x01;
			v_sendbufInfo[2] = 0x00;
			v_sendbufInfo[3] = 0x00;
			if (key != null && !key.equals("")) 
			{   
				if(unifDataStreamMap!=null){
					if(unifDataStreamMap.containsKey(key)){
						isSysScan=false;
					}
				}
				//���ַ���ת��Ϊʮ������
				String str1="0x"+key.substring(0, 2);
				v_sendbufInfo[4]=(byte)HexStringtoInt(str1);
				String str2="0x"+key.substring(2,4);
				v_sendbufInfo[5]=(byte)HexStringtoInt(str2);
				int num=0;		
				//����ɨ�赱ǰ�����߳�
				reqDataThread=new ReceiveReuqestDataThread();
				reqDataThread.start();
				//��ʱ�߳�
				timeOutThread=new TimeOutThread();
				timeOutThread.start();
				//ѭ��ִ��
				while(!flag){
					num+=1;		
			    }		
			} 
			else 
			{
				CURRENT_VALUE = "-2";// ������֧��
			}

		} else {
			CURRENT_VALUE = "-1";// ͨ��ʧ��
		}
		flag=false;
		closeThread();
		DelObserver(); 
		if(CURRENT_VALUE.equals("")){
			CURRENT_VALUE="-2";
			isSysScan=true;
		}
		return CURRENT_VALUE;

	}

	// ������
	private byte[] m_commond = null;

	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"BlueConnectLost" + name);
		CURRENT_VALUE = "-1";// ͨ��ʧ��
		flag=true;
	}

	@Override
	public void BlueConnected(String name, String mac) {
		// TODO Auto-generated method stub

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
					BluetoothDataService.CMD_OneToOne, v_cmd, null, 0, 1500);
	}

	@Override
	public void GetDataFromService(byte[] databuf, int datalen) {

		// if (D) Log.i("databuf", Arrays.toString(databuf));
		byte[] dpuPackage = OrderUtils.filterReturnDataPackage(databuf);
		// if (D) Log.i("tag", Arrays.toString(dpuPackage));
		byte[] cmd_subcmd = OrderUtils.filterOutCommand(dpuPackage);
		byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);// �������ֽ�
		String v_show = BluetoothDataService.bytesToHexString(databuf, datalen);
		if (D)
			Log.i(TAG, "SHOW��" + v_show);
		String info = "";
		// if (D) Log.i("param", "param" + Arrays.toString(param));
		// ����ģʽ
		String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);
		if (cmd_subcmdString.equals("6109")) {
			if (param.length == 2 && param[0] != 0x06 && param[1] == 0x00) {
				this.setOrGetMode(new byte[] { 0x06 });
				next.set(true);
			} else {
				if (param.length == 2 && param[0] == 0x06 && param[1] == 0x00) {
					// �ӳ�����
					delayTimeThread = new DelayTimeThread(DT_CMD_MODE, 3000,
							null);
					delayTimeThread.start();
					delayTimeThread = null;
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
				//ͨ��ʧ��
				initConsTantValue("-1",true);
			}
		}
		// ��ȫ��֤1
		else if (cmd_subcmdString.equals("6502")) {
			// ������֤
			byte[] v_key = null;
			if (param.length > 0 && param[0] == 0x01) {
				v_key = new byte[] { param[1], param[2] };
				this.SafeCheckEnter(2, param[0], v_key);
				next.set(true);
//				this.InitialGGPInstance();

			} else {
				//ͨ��ʧ��
				initConsTantValue("-1",true);
			}
		}
		// ��ȫ��֤2
		else if (cmd_subcmdString.equals("6503")) {
			if (param.length > 0 && param[1] == 0x00) {
				if(isSysScan){				
					this.ReadSampleReportStep(2);
				//���Ͷ�ȡ֧�ֵ�ϵͳ�б�����
				}else{
					delayTimeThread = new DelayTimeThread(DT_APPOINT_ID_LIST,
							300, v_sendbufInfo);
					delayTimeThread.start();
					delayTimeThread = null;
					next.set(true);
				}
			} else {
				//ͨ��ʧ��
				initConsTantValue("-1",true);
			}

		} else if (cmd_subcmdString.equals("221C")) {
			if (D)
				Log.i(TAG, "��ʾ���˵�");
			GetShowDialog(param, 0);
			next.set(true);
		}
		// ��ȡ֧�ֵ�ϵͳ�б�
		else if (cmd_subcmdString.equals("6A01")) {
			if (param.length > 0) {
				try {
					GetSimpleScanList(param, 0);
					this.getSimeplePID();
					delayTimeThread=new DelayTimeThread(DT_APPOINT_DATA_LIST,300,v_sendSystIDInfo);
					delayTimeThread.start();
					delayTimeThread=null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//ͨ��ʧ��
				initConsTantValue("-1",true);
			}
		}
		// ��ȡָ��ϵͳ�������б�
		else if (cmd_subcmdString.equals("6A02")) {
			if (param.length > 0) {
				try {
					GetSimpleAppointDataList(param, 0);
					if(unifDataStreamMap.containsKey(keyStr)){
						delayTimeThread = new DelayTimeThread(DT_APPOINT_ID_LIST,
								300, v_sendbufInfo);
						delayTimeThread.start();
						delayTimeThread = null;
						isSysScan=false;
					}
					else{
						CURRENT_VALUE = "-2";// ϵͳ��֧��
						flag=true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//ͨ��ʧ��
				initConsTantValue("-1",true);
				isSysScan=false;
			}	

		}
		// ��ȡָ��ID������
		else if (cmd_subcmdString.equals("6A11")) {
			if (param.length > 0) {
				try {
					GetSimpleAppointIDList(param, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				//ͨ��ʧ��
				initConsTantValue("-1",true);
			}
		}

	}

	// ��öԻ����ı���Ϣ CMD_SHOW_GETDIALOG
	public void GetShowDialog(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "�ı��Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		int v_start = 0;
		// ����Ի�������
		int v_style = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		bundle.putInt("DIALOG_STYLE", v_style); // �Ի�������
		// ����Ի������
		v_start += 2;
		if (v_style != 5) {
			//ͨ��ʧ��
			initConsTantValue("-1",true);
		}
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"���ճ�ʱ��step=");
		CURRENT_VALUE = "-1";// ͨ��ʧ��
		flag=true;
		
	}

	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"SHOW���ر�����" );
	}

	/* �������������߳� */

	class ReceiveReuqestDataThread extends Thread {
		@Override
		public void run() {
			try {
				this.sleep(100);
				setOrGetMode(new byte[] { 0x06 });
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	/* ��ʱ�߳� */

	class TimeOutThread extends Thread {
		@Override
		public void run() {
			try {
				this.sleep(1000*30);
				if(!flag){
					flag=true;
					CURRENT_VALUE = "-1";
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	/*
	 * ��ʱ�߳�
	 */
	class DelayTimeThread extends Thread {
		int cmdType;
		int time = 1000;
		byte[] param = null;

		public DelayTimeThread(int cmdType, int time, byte[] param) {
			this.cmdType = cmdType;
			this.time = time;
			this.param = param;
		}

		@Override
		public void run() {
			try {
				this.sleep(time);
				ReadSampleReportStep(cmdType, param);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// ��ȫ��֤1: 25 02 01; 2: 25 03 01 ������
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
	// �õ�ָ��������PID����
	private void getSimeplePID() {
		if (v_sysIdList != null && v_sysIdList.size() > 0) {
			v_sendSystIDInfo = new byte[v_sysIdList.size()];
			for (int i = 0; i < v_sysIdList.size(); i++) {
				v_sendSystIDInfo[i] = v_sysIdList.get(i);
			}
		}
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

	// ��ʼ��ggp����
//	public void InitialGGPInstance() {
//		m_ggppath = Env.getAppRootDirInSdcard().getAbsolutePath()
//				+ "/VEHICLES/TOYOTA/V46.82/TOYOTA_"
//				+ Env.GetCurrentLanguage().toUpperCase() + ".GGP";
//		SearchIdUtils search = SearchIdUtils.SearchIdInstance(m_ggppath);
//		searchIdUtils = SearchIdUtils.SearchIdInstance("");
//	}

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
//			String text_id = searchIdUtils.getMessage(v_id2,
//					SearchIdUtils.ID_TEXT_LIB_FILE);
//			text_id = DelStringLast(text_id);
			v_start += 4; // ��ַ�������ۼ�
			idTextList.add(cateText);

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
//			String data_Stream_Id = searchIdUtils.getMessage(v_id,
//					SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
//			String v_name = DatastreamGetName(data_Stream_Id);
//			String v_unit = DatastreamGetUnit(data_Stream_Id);
//			data_Stream_Id = v_name;
//			if (!v_unit.equals("")) {
//				data_Stream_Id += "(" + v_unit + ")";
//			}
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
			v_start += v_id; // ��ַ�ۼ�
			if (result != null && !result.equals("")) {
				if (OrderUtils.oneByteToHexString(v_reselt[2]).equals("0x81")) {
					CURRENT_VALUE = "-2";

				} else if (OrderUtils.oneByteToHexString(v_reselt[2]).equals(
						"0x83")) {
					Float curValue1 = Float.parseFloat(result);
					CURRENT_VALUE = curValue1 > 0 ? String.valueOf(Float
							.parseFloat(result)) : "-2";
				} else if (OrderUtils.oneByteToHexString(v_reselt[2]).equals(
						"0x84")) {
					
					Float curValue2 = Float.parseFloat(result);
					CURRENT_VALUE = curValue2 > 0 ? String.valueOf(Float
							.parseFloat(result)) : "-2";
				} else if (OrderUtils.oneByteToHexString(v_reselt[2]).equals(
						"0x85")) {
					CURRENT_VALUE = "-2";
				}
			} else {
				CURRENT_VALUE = "-2";
			}
		}
		if (reqDataThread != null) {
			reqDataThread = null;
		}
		flag=true;
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
		unifDataStreamMap = new HashMap<String, String>();	
		for (int i = 0; i < v_dtc_num; i++) {
			
			// ϵͳID
			v_id = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			v_start += 4; // ��ַ�������ۼ�
			//����������
			int num = (databuf[v_start] & 0xff) * 0x100
					+ (databuf[v_start + 1] & 0xff);
			v_start += 2;
			for (int j = 0; j < num; j++) {
				// �������б�
				v_id = (databuf[v_start] & 0xff) * 0x1000000
						+ (databuf[v_start + 1] & 0xff) * 0x10000
						+ (databuf[v_start + 2] & 0xff) * 0x100
						+ (databuf[v_start + 3] & 0xff);
				byte[] streamInfos = new byte[2];
				streamInfos[0]=databuf[v_start + 2];
				streamInfos[1]=databuf[v_start + 3];
				// ֧�ֵ�ͳһ������ID
				String unifStreamInfo= OrderUtils.bytesToHexStringNoBar(streamInfos);
//				String questionList = searchIdUtils.getMessage(v_id,
//						SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
//				questionList = DelStringLast(questionList);
				unifDataStreamMap.put(unifStreamInfo, unifStreamInfo);
				v_start += 4; // ��ַ�������ۼ�

			}
		}
	}
	// ��ʼ��DiagnoseSimpleReportInfo����
	public DiagnoseSimpleReportInfo getDiagnoseSimpleReportInfoEntry(
			String data_Stream_Id, String result, int type) {
		DiagnoseSimpleReportInfo info = new DiagnoseSimpleReportInfo();
		info.setDataStreamName(data_Stream_Id);
		if (type == 1) {
			info.setDataStreamValue(result);
		} else {
			info.setDataStreamValue(String.valueOf(Float.parseFloat(result)));
			info.setMaxvalue(String.valueOf(Float.parseFloat(result)));
			info.setMinvalue(String.valueOf(Float.parseFloat(result)));
		}

		return info;
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

	// ���������������λ��ֻ��ʾ����������
	public static String DatastreamGetName(String v_str) {
		String v_retu = null;
		if (v_str == null || v_str.length() < 1)
			return "";
		int v_len = v_str.length();
		int v_find = 0;
		for (int i = v_len - 1; i > 0; i--) {
			if (v_str.charAt(i) == 0x00) {
				v_find++;
				if (v_find == 2) {
					v_retu = v_str.substring(0, i);
					break;
				}
			}
		}
		return v_retu;
	}

	// ���Ͷ�ȡָ��ID������
	public void SendSampleReportAppointCmd(int mode,byte[] para) {
		if (mode == 2) { // ��ȡָ��ϵͳ�������б�
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETDATASTREAMSELECT;
		} else if (mode == 3) {// ��ȡָ��ID������
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_READDATESTREAM;
		} else
			return;
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
				m_commond, para, para.length, MAX_TIME);
	}

	// ���ͼ��ױ���ָ������
	public void SendSampleReportCmd(int step,byte[] para) {
		byte[] paradata = GetParaByte(para);
		this.SendSampleReportAppointCmd(step,paradata);

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

	// �����������������,ֻ��ʾ��λ
	public static String DatastreamGetUnit(String v_str) {
		String v_retu = null;
		if (v_str == null || v_str.length() < 1)
			return "";
		int v_len = v_str.length();
		int v_find = 0;
		for (int i = v_len - 1; i > 0; i--) {
			if (v_str.charAt(i) == 0x00) {
				v_find++;
				if (v_find == 2) {
					v_retu = v_str.substring(i + 1, v_len - 1);
					break;
				}
			}
		}
		return v_retu;
	}

	// ���Ϳ����������
	public void ReadSampleReportStep(int step, byte[] param) {

		switch (step) {
		case DT_CMD_MODE:
			this.EnterPassword();
			break;
		case DT_APPOINT_DATA_LIST:
			this.SendSampleReportCmd(step,param);
			break;
		case DT_APPOINT_ID_LIST :
			this.SendSampleReportCmd(step,param);
			break;
		default:
			break;
		}
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

	// ɾ���۲���
	public void DelObserver() {
		m_blue_service.DelObserver(this);
	}

	// ��ȡ���ױ�����Ϣ
	public int ReadSampleReportStep(int step) {
		if (step == 1) // һ��ɨ��ϵͳ֧�ֵ��б�
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_SCAN;
		}
		else if (step == 2) // ��ȡ֧�ֵ�ϵͳ�б�
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETLIST;
		}
		return m_blue_service
				.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
						m_commond, null, 0, MAX_TIME);
	}
	public void initConsTantValue(String str,boolean bit){
		CURRENT_VALUE = str;// ͨ��ʧ��
		flag=bit;
	}
	//�رճ�ʱ�߳�
	public void closeThread(){
		if(timeOutThread!=null){
			try{			
				timeOutThread.interrupt();
			}catch(Exception e){
				e.printStackTrace();
			}
			timeOutThread=null;	
		}
		if(reqDataThread!=null){			
			reqDataThread=null;
		}
	}
}

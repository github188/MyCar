package com.cnlaunch.mycar.diagnose.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import launch.SearchIdUtils;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cnlaunch.bluetooth.service.BluetoothDataService;
import com.cnlaunch.mycar.R;

public class DiagnoseDataService {
	private static final String TAG = "DiagnoseDataService";
    private static final boolean D = true;
	//�����ʽ
	/* <��ʼ��־> + <Ŀ���ַ> + <Դ��ַ> + <������> + <������> + <������> + <����> + <��У��>
	<��ʼ��־>: 2byte   0x55,0xAA
	<Ŀ���ַ>:	1byte	0xF8
	<Դ��ַ>:	1byte	0xF0
	<������>:	2byte  <������> + <������> + <����>
	<������>��	1byte  0x00~0xFFѭ������
	<������>��  2byte  
	<����>��
	<��У��>��  1byte  <Ŀ���ַ> + <Դ��ַ> + <������> + <������> + <������> + <����>

	DPUString��ʽ
	<����> + <����> + <������־>
	<����> :	2byte  <����> + <������־>
	<����>:		
	<������־>: 1byte 0x00
	*////////////////////////////////////////////////////////////////////////////////////////
	//��������
	public final static int	CMD_MAIN = 0x21;	//���û��ȡ����������
	public final static int	CMD_SHOW = 0x22;	//��ʾ����ͨ��������
	public final static int	CMD_ERROR = 0x23;	//�쳣����
	public final static int	CMD_UPDATE = 0x24;	//�������
	public final static int	CMD_OPEN = 0x25;	//��������\��ȫУ��\�Ͽ����Ӽ���·
	public final static int	CMD_CRECORDER = 0x26;	//Crecorderģʽ
	public final static int	CMD_SMARTBOX = 0x27;	//SmartBoxģʽ
	public final static int	CMD_SET = 0x28;	//���� 
	public final static int	CMD_OBD = 0x29;	//OBDģʽ	
	public final static int CMD_SIMPLE = 0X2A;	//�����ģʽ
	
	//�ڶ��������� CMD1_MAIN
	public final static int	CMD_MAIN_SETTIME = 0x01;	//����ʱ��
	public final static int	CMD_MAIN_READTIME = 0x02;	//��ȡʱ��
	public final static int	CMD_MAIN_READHARD = 0x03;	//ȡDPU ��ͷӲ���汾��Ϣ 
	public final static int	CMD_MAIN_READSOFT = 0x04;	//ȡDPU ��ͷ���ļ��汾���������ƣ����Ͱ汾�����ԣ�
	public final static int	CMD_MAIN_READBOOT = 0x05;	//ȡDPU ��ͷ����汾��boot��download����������
	public final static int	CMD_MAIN_READERIA = 0x06;	//ȡDPU ��ͷ���۸ı�ʶ��֧���������ͣ���������
	public final static int	CMD_MAIN_WRITESN = 0x07;	//дDPU ��ͷ���к� 
	public final static int	CMD_MAIN_WRITEBLUE = 0x08;	//������������
	public final static int	CMD_MAIN_SETMODE = 0x09;	//��λ DPU ����ģʽ�������л����ģʽʱ��
	public final static int	CMD_MAIN_CLEARFLASH = 0x0A;	//��� Flash ���� 
	public final static int	CMD_MAIN_SETPASS = 0x0B;	//���û��޸İ�ȫ����ָ��
	public final static int	CMD_MAIN_RESETPASS = 0x0F;	//�ָ���ʼ���� 
	public final static int	CMD_MAIN_INPUTPASS = 0x10;	//��֤��ȫ����ָ�� 
	public final static int	CMD_MAIN_DOWNLOAD = 0x11;	//��ת�� Download ������� 
	public final static int	CMD_MAIN_WRITECONFIG = 0x12;	//д��ͷ�����ļ� 
	public final static int	CMD_MAIN_READCONFIG = 0x13;	//����ͷ�����ļ�
	public final static int	CMD_MAIN_READMODE = 0x14;	//��ȡ��ǰ״̬
	public final static int	CMD_MAIN_READLICENCE = 0x15;	//����ͷLicence
	public final static int	CMD_MAIN_READBLUEMAC = 0x16;	//��ȡ��ͷ������ַ
	
	//��ʾ������  CMD_SHOW
	public final static int	CMD_SHOW_GETMENU = 0x10;		//��ò˵�
	public final static int	CMD_SHOW_SETMENU = 0x11;		//���ز˵�
	public final static int	CMD_SHOW_GETDTC	=	0x12;			//��ù�����
	public final static int	CMD_SHOW_SETDTC = 0x13;			//���ع�����
	public final static int	CMD_SHOW_GETFRAME = 0x14;		//��ö���֡
	public final static int	CMD_SHOW_SETFRAME = 0x15;		//���ض���֡
	public final static int	CMD_SHOW_GETDATASTREAMSELECT = 0x16;	//���������ѡ��
	public final static int	CMD_SHOW_SETDATASTREAMSELECT = 0x17;	//����������ѡ��
	public final static int	CMD_SHOW_GETDATASTREAM = 0x18;		//���������
	public final static int	CMD_SHOW_SETDATASTREAM = 0x19;		//����������
	public final static int	CMD_SHOW_GETDONGZUO = 0x1A;			//��ö�������
	public final static int	CMD_SHOW_SETDONGZUO = 0x1B;			//���ض�������
	public final static int	CMD_SHOW_GETDIALOG = 0x1C;			//��öԻ���
	public final static int	CMD_SHOW_SETDIALOG = 0X1D;			//���ضԻ���
	public final static int	CMD_SHOW_GETDIALOGID = 0x1E;		//���ID�Ի���
	public final static int	CMD_SHOW_SETDIALOGID = 0x1F;		//����ID�Ի���
	public final static int	CMD_SHOW_GETINPUT_INT = 0X20;			//����ı���������Ի���
	public final static int	CMD_SHOW_SETINPUT_INT = 0X21;			//�����ı���������Ի���
	public final static int	CMD_SHOW_GETINPUT_INTID = 0X22;		//���ID��������Ի���
	public final static int	CMD_SHOW_SETINPUT_INTID = 0X23;		//����ID��������Ի���
	public final static int	CMD_SHOW_GETINPUT_STR = 0X24;			//����ı��ַ�������Ի���
	public final static int	CMD_SHOW_SETINPUT_STR = 0X25;			//�����ı��ַ�������Ի���
	public final static int	CMD_SHOW_GETINPUT_STRID = 0X26;		//���ID�ַ�������Ի���
	public final static int	CMD_SHOW_SETINPUT_STRID = 0x27;		//����ID�ַ�������Ի���
	public final static int	CMD_SHOW_GETFILE_ID = 0X28;				//���ID�ļ��Ի���
	public final static int	CMD_SHOW_SETFILE_ID = 0x29;				//����ID�ļ��Ի���
	public final static int	CMD_SHOW_GETINPUT_FLOAT = 0X2A;		//����ı�������������Ի���
	public final static int	CMD_SHOW_SETINPUT_FLOAT = 0X2B;		//�����ı�������������Ի���
	public final static int	CMD_SHOW_GETINPUT_FLOAT_ID = 0X2C;	//���ID������������Ի���
	public final static int	CMD_SHOW_SETINPUT_FLOAT_ID = 0X2D;	//����ID������������Ի���
	public final static int	CMD_SHOW_GETDATASTREAM_PAGE = 0X2E;	//�����������ҳ��ʾ
	public final static int	CMD_SHOW_SETDATASTREAM_PAGE = 0X2F;	//������������ҳ��ʾ
	public final static int	CMD_SHOW_GETDTC_ADD = 0X30;					//��ù�������չָ��
	public final static int	CMD_SHOW_SETDTC_ADD = 0X31;					//���ع�������չָ��
	public final static int CMD_SHOW_ROOT_DIAG	= 0x32;			//��Ŀ¼��ʾ���յ��������ʾĿǰ�ڸ�Ŀ¼
	//��ʾδ֪�Ի���
	public final static int CMD_SHOW_NONE = 0xFF; 				//δ֪�Ի�����ʾ
	//������ʾ ERROR
	public final static int	CMD_ERROR_CHECK = 0X01;		//У�����
	public final static int	CMD_ERROR_LONGBAG = 0X02;		//�����ȹ���

	//��������\��ȫУ��\�Ͽ����Ӽ���· OPEN
	public final static int	CMD_OPEN_LINK = 0X01;				//��·����
	public final static int	CMD_OPEN_CONNECT = 0X02;		//��������
	public final static int	CMD_OPEN_SAFECHECK = 0X03;	//��ȫУ��
	public final static int	CMD_OPEN_DISCONNECT = 0X04;	//�ر�����
	public final static int	CMD_OPEN_RESET_DPU = 0x05;	//��λ��ͷ
	public final static int	CMD_OPEN_SEARCH = 0x00;			//һ��ɨ��
	
	//������������
	public final static int	CMD_UPDATE_TITLE = 0X01;		//����׼����������
	public final static int	CMD_UPDATE_SENDNAME = 0X02;		//���������ļ����ƺͳ���
	public final static int	CMD_UPDATE_SENDDATA	= 0X03;		//���������ļ�����
	public final static int	CMD_UPDATE_SENDCHECK = 0X04;	//���������ļ�У��
	public final static int	CMD_UPDATE_SENDCOMPLETE = 0X05;	//���������������
	public final static int	CMD_UPDATE_SENDCONTINUE = 0X06;	//���Ͷϵ���������
	public final static int	CMD_UPDATE_ENABLEBOOT = 0x07;	//���ø��¹̼�����
	public final static int	CMD_UPDATE_READFILEINFO	= 0X08;	//��ȡ��ͷ�ļ���Ϣ
	

	//Crecorderģʽ CRECORDER
	public final int	CMD_CRECORDER_START = 0X01;				//����Crecorderģʽ
	public final int	CMD_CRECORDER_SEARCH = 0X02;			//��ѯ�ļ���Ϣ
	public final int	CMD_CRECORDER_READ_FILE = 0x03;		//��ȡ�ļ�
	public final int	CMD_CRECORDER_DEL_FILE = 0X04;		//ɾ���ļ�
	public final int	CMD_CRECORDER_DEL_ALLFILE = 0X05;	//ɾ�������ļ�

	//�����ģʽ SIMPLE
	public final int	CMD_SIMPLE_SCAN = 0X00;				//һ��ɨ��ϵͳ֧�ֵ��б�
	public final int	CMD_SIMPLE_GETLIST = 0x01;		//��ȡ֧�ֵ�ϵͳ�б�
	public final int	CMD_SIMPLE_GETDATASTREAMSELECT = 0X02;	//��ȡָ��ϵͳ�������б�
	public final int	CMD_SIMPLE_GETDTC = 0X03;			//��ȡָ��ϵͳ�������б�
	public final int	CMD_SIMPLE_CLEARDTC = 0X04;		//���ָ��ϵͳ������
	public final int	CMD_SIMPLE_READDATESTREAM = 0X11;	//��ȡָ��ID������
	public final int	CMD_SIMPLE_READLAST = 0X13;			//��ȡ�ϴ�����
	//��������
	public final int	ADD_DPU = 0xF0;
	public final int	ADD_PC = 0xF8;
	
	public final int 	MAX_TIME = 1000;  	//��ʱʱ��
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
	
	//˽�б���
	private Activity m_now_activity = null;		//��¼��ǰactivity����
	private Dialog m_now_dialog = null;			//��¼��ǰ�ĶԻ�������
	//���º���������
	private BluetoothDataService m_blue_service = null;
	//������
	private byte [] m_commond = null;
	//��ggp�ļ�
	SearchIdUtils m_idutils = null;
	//�����˷���
	private static DiagnoseDataService m_myservice = null;
	public synchronized static DiagnoseDataService getInstance()
	{
		if(m_myservice == null)
			m_myservice = new DiagnoseDataService();
		return m_myservice;
	}
	//���췽��
	private DiagnoseDataService()
	{
		m_commond = new byte[2];
		m_blue_service = BluetoothDataService.getInstance();
	}
	
	public void SetCurrentActivity(Activity activity)
	{
		m_now_activity = activity;
	}
	public void SetCurrentDialog(Dialog dlg)
	{
		m_now_dialog = dlg;
	}
	//��ʼ��ggp����
	public void InitialGGPInstance()
	{
		m_idutils = SearchIdUtils.SearchIdInstance("");
	}
	//-------------------------���ʵ�ַ���---------------------------------------
	//��ȡ��ϰ汾��Ϣ����һ��21 03���ڶ���21 04��
	
	public int ReadVersionStep(int step)
	{
		if(step == 1) //��Ӳ���汾��Ϣ
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READHARD;
		}
		else if(step == 2)  //������汾��Ϣ
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READSOFT;
		}	
		else if(step == 3)	//��ѯ�Ƿ����downloadģʽ
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READMODE;
		}
		else if(step == 4)   //��ȡBOOT�汾
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READBOOT;
		}
		else if(step == 5) //��ѯ�����Ƿ����
		{
			m_commond[0] = CMD_UPDATE;
			m_commond[1] = 0x06;
		}
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, null, 0, MAX_TIME);
	}
	//����dpu�������
	public int ConnectDpu()
	{
		m_commond[0] = CMD_OPEN;
		m_commond[1] = CMD_OPEN_LINK;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode, m_commond, null, 0, MAX_TIME);
	}
	//��ȡ���������ģʽGetMode=0,SMARTBOX=1,MYCAR=2,CREADER=3,CRECORDER=4��OBD=5��QuickDiag=6 
	public void SetAndReadMode(byte mode)
	{
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_SETMODE;
		byte [] paradata = new byte[1];
		paradata[0] = mode;
 		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, paradata, paradata.length, MAX_TIME);
	}
	//���Ĳ���ȫ��֤1: 25 02 01;  2: 25 03 01 ������
	public void SafeCheckEnter(int mode,byte cmd,byte[] para)
	{
		byte [] paradata = null;
		int paralen = 0;
		if(mode == 1)
		{
			m_commond[0] = CMD_OPEN;
			m_commond[1] = CMD_OPEN_CONNECT;
			paradata = new byte[1];
			paradata[0] = 0x01;
			paralen = 1;
		}
		else if(mode == 2)
		{
			m_commond[0] = CMD_OPEN;
			m_commond[1] = CMD_OPEN_SAFECHECK;
			if(cmd == 0)
			{
				paradata = new byte[]{0x00};
			}
			else if(cmd == 1)
			{
				paradata = new byte[]{0x01,para[0],para[1]};
			}
			else if(cmd == 2)
				paradata = new byte[]{0x01,para[0],para[1],para[2],para[3]};
			paralen = paradata.length;
		}
		else
			return;
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, paradata, paralen, MAX_TIME);
	}
	//��ȡ�ϵ������Ƿ��������
	public int ReadUpdateComplete(byte[] databuf,int datalen)
	{
		//��3��DPUString����
		int v_len = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		int v_len1 = (databuf[v_len + 2]&0xFF)* 0x100 + (databuf[v_len + 2 + 1] &0xFF) + 2;
		int v_len2 = (databuf[v_len + v_len1 + 2]&0xFF)* 0x100 + (databuf[v_len + v_len1 + 2 + 1] &0xFF) + 2;
		if(v_len1 < datalen)
			return databuf[v_len + v_len1 + v_len2 + 2]&0xFF;
		else
			return 0xFF;
	}
	//��������
	public void ResetPassword(byte[] password,int passlen)
	{
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_RESETPASS;
		byte[] v_dpupassword = new byte[passlen + 3];
		int v_dpulen = AddDataToDpuString(password, passlen, v_dpupassword, v_dpupassword.length);
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, v_dpupassword, v_dpulen, MAX_TIME);
	}
	//����������֤
	public void EnterPassword(byte[] password,int passlen)
	{
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_INPUTPASS;
		byte[] v_dpupassword = new byte[passlen + 3];
		int v_dpulen = AddDataToDpuString(password, passlen, v_dpupassword, v_dpupassword.length);
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, v_dpupassword, v_dpulen, MAX_TIME);
	}
	
	//���˽������ݵİ�ͷ
	public int GetDataFromBluetooth(byte[] databuf,int datalen,byte[] Recbuf)
	{
		System.arraycopy(databuf, 7, Recbuf, 0, datalen - 8);
		return datalen - 8;
	}
	//����DPU String
	public String GetDpuString(byte[] dpustr)
	{
		byte[] v_str = null;
		if(dpustr.length < 3)
			return "";
		int v_len = (dpustr[0] &0xFF) * 0x100 + (dpustr[1] & 0xFF);
		v_str = new byte[v_len - 1];
		System.arraycopy(dpustr, 2, v_str, 0, v_len - 1);
		String v_return = null;
		try {
			v_return =  new String(v_str,"GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v_return;
	}
	//��װDPUString
	public int AddDataToDpuString(byte[] source,int len,byte [] object,int objlen)
	{
		//�ṩ�Ļ���������̫��
		if(objlen < len + 3)
			return -1;
		//��ӵ�DPUString
		object[0] = (byte)(((len + 1) / 0x100) & 0xFF);
		object[1] = (byte)(((len + 1) % 0x100) & 0xFF);
		System.arraycopy(source, 0, object, 2, len);
		object[len + 2] = 0x00;
		return len + 3;
	}
	//��ȡӲ��Ψһ��ʶ�Ͳ�Ʒ���к�
	public void GetDpuIdandSerialNo(byte[] databuf,int datalen,ArrayList<String> list)
	{
		//��ȡDPU ID
		int v_id_len = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		byte[] v_id = new byte[v_id_len];
		System.arraycopy(databuf, 2, v_id, 0, v_id_len);
		list.add(GetDpuString(v_id));
		//��ȡ���к�
		int v_serial_len = (databuf[2 + v_id_len]&0xFF)* 0x100 + (databuf[2 + v_id_len + 1] &0xFF) + 2;
		byte [] v_serial = new byte[v_serial_len];
		System.arraycopy(databuf, 2 + v_id_len, v_serial, 0, v_serial_len);	
		list.add(GetDpuString(v_serial));
	}
	//��ȡ��������Ϣ
	public void GetDiagSoftwareVersion(byte[] databuf,int datalen,ArrayList<String> list)
	{
		//��ȡ�������
		int v_lang = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		byte[] v_langname = new byte[v_lang];
		System.arraycopy(databuf, 2, v_langname, 0, v_lang);
		list.add(GetDpuString(v_langname));
		//��ȡ��������
		int v_carnamelen = (databuf[2 + v_lang]&0xFF)* 0x100 + (databuf[2 + v_lang + 1] &0xFF) + 2;
		byte [] v_carname = new byte[v_carnamelen];
		System.arraycopy(databuf, 2 + v_lang, v_carname, 0, v_carnamelen);	
		list.add(GetDpuString(v_carname));
		//��ȡ���Ͱ汾
		int v_carverlen = (databuf[2 + v_lang + v_carnamelen]&0xFF)* 0x100 + (databuf[2 + v_lang + v_carnamelen + 1] &0xFF) + 2;
		byte [] v_carnver = new byte[v_carverlen];
		System.arraycopy(databuf, 2 + v_lang + v_carnamelen, v_carnver, 0, v_carverlen);	
		list.add(GetDpuString(v_carnver));
	}
	//��ȡBOOT��DOWNLOAD�汾��Ϣ
	public void GetbootanddownloadInfo(byte[] databuf,int datalen,ArrayList<String> list)
	{
		//��ȡDPU ID
		int v_id_len = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		byte[] v_id = new byte[v_id_len];
		System.arraycopy(databuf, 2, v_id, 0, v_id_len);
		list.add(GetDpuString(v_id));
		//��ȡ���к�
		int v_serial_len = (databuf[2 + v_id_len]&0xFF)* 0x100 + (databuf[2 + v_id_len + 1] &0xFF) + 2;
		byte [] v_serial = new byte[v_serial_len];
		System.arraycopy(databuf, 2 + v_id_len, v_serial, 0, v_serial_len);	
		list.add(GetDpuString(v_serial));
	}
	
	//��ȡ����ID��Ϣ
	public int GetDiagErrorID(int err_id)
	{
		int v_id = 0;
		switch(err_id)
		{
		case 0:
			v_id = R.string.diag_error_00;
			break;
		case 1:
			v_id = R.string.diag_error_01;
			break;
		case 2:
			v_id = R.string.diag_error_02;
			break;
		case 3:
			v_id = R.string.diag_error_03;
			break;
		case 4:
			v_id = R.string.diag_error_04;
			break;
		case 5:
			v_id = R.string.diag_error_05;
			break;
		case 6:
			v_id = R.string.diag_error_06;
			break;
		case 7:
			v_id = R.string.diag_error_07;
			break;
		case 8:
			v_id = R.string.diag_error_08;
			break;
		case 9:
			v_id = R.string.diag_error_09;
			break;
		case 10:
			v_id = R.string.diag_error_10;
			break;
		case 11:
			v_id = R.string.diag_error_11;
			break;
		case 12:
			v_id = R.string.diag_error_12;
			break;
		case 13:
			v_id = R.string.diag_error_13;
			break;
		case 14:
			v_id = R.string.diag_error_14;
			break;
		case 15:
			v_id = R.string.diag_error_15;
			break;
		case 16:
			v_id = R.string.diag_error_16;
			break;
		case 17:
			v_id = R.string.diag_error_17;
			break;
		case 18:
			v_id = R.string.diag_error_18;
			break;
		case 19:
			v_id = R.string.diag_error_19;
			break;
		case 20:
			v_id = R.string.diag_error_20;
			break;
		default:
			v_id = R.string.diag_error_00;
			break;
		}
		return v_id;
	}
	//��öԻ����ı���Ϣ CMD_SHOW_GETDIALOG
	//databuf <������> + <����>
	//Handler Ϊ���صĶԻ���
	public void GetShowDialog(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"�ı��Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		//����Ի�������
		int v_style = (databuf[2]&0xFF) * 0x100 + (databuf[3]&0xFF);
		bundle.putInt("DIALOG_STYLE", v_style);  //�Ի�������
		//����Ի������
		int v_title_len = (databuf[4]&0xFF) * 0x100 + (databuf[5]&0xFF) + 2; //DPUSting����
		byte [] v_title = new byte[v_title_len];
		System.arraycopy(databuf, 4, v_title, 0, v_title_len);
		String v_show = GetDpuString(v_title);
		v_show = FindDpuId(v_show); //���������DPU_ID
		v_show = DelStringLast(v_show);
		bundle.putString("DIALOG_TITLE", v_show);
		//����Ի����ı�
		int v_msg_len = (databuf[4 + v_title_len]&0xFF) * 0x100 + (databuf[4 + v_title_len + 1]&0xFF) + 2;
		byte [] v_msg = new byte[v_msg_len];
		System.arraycopy(databuf, 4 + v_title_len, v_msg, 0, v_msg_len);
		String v_text = GetDpuString(v_msg);
		v_text = FindDpuId(v_text); //���������DPU_ID
		v_text = DelStringLast(v_text);
		bundle.putString("DIALOG_BODY", v_text);
		bundle.putInt("DIALOG_CMD_RETURN", CMD_SHOW_SETDIALOG); //�Ի��򷵻�����
		handler.obtainMessage(CMD_SHOW_GETDIALOG, bundle).sendToTarget();
	}
	//��ʾID�Ի���   CMD_SHOW_GETDIALOGID
	public void GetShowDialogID(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"ID�Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		//����Ի�������
		int v_style = (databuf[2]&0xFF) * 0x100 + (databuf[3]&0xFF);
		bundle.putInt("DIALOG_STYLE", v_style);  //�Ի�������
		//����Ի������ID
		int v_start = 4;
		int v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
				+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
		String v_title = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
		v_title = DelStringLast(v_title);
		bundle.putString("DIALOG_TITLE", v_title);
		//����Ի����ı�ID
		v_start += 4;
		v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
				+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
		String v_body = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
		v_body = DelStringLast(v_body);
		bundle.putString("DIALOG_BODY", v_body);
		bundle.putInt("DIALOG_CMD_RETURN", CMD_SHOW_SETDIALOGID); //�Ի��򷵻�����
		handler.obtainMessage(CMD_SHOW_GETDIALOGID, bundle).sendToTarget();
	}
	//��ʾ�˵� CMD_SHOW_GETMENU
	public void GetShowMenuActivity(Handler handler,byte[] databuf,int datalen,boolean isroot)
	{
		if(D) Log.i(TAG,"�˵�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		//�ж��Ƿ��Ǹ�Ŀ¼
		if(isroot == true)
			bundle.putBoolean("DIALOG_ROOT", true);
		else
			bundle.putBoolean("DIALOG_ROOT", false);
		//��ȡ����ID 4 byte
		int v_start = 2;
		int v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
				+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
		String v_title = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
		if(D) Log.i(TAG,"++" + v_title +"--len=" + v_title.length());
		v_title = DelStringLast(v_title);
		if(D) Log.i(TAG,"++" + v_title +"-len=" + v_title.length());
		bundle.putString("DIALOG_TITLE", v_title);
		//��ȡ����ID 4 byte
		v_start += 4;
		//��ȡ�˵�����
		v_start += 4;
		int v_menu_num = (databuf[v_start ] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//��ȡ�˵��ı�
		v_start += 2;
		List<Map<String, String>> v_menu_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_menu_num; i ++)
		{
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_menu = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
			v_menu = DelStringLast(v_menu);
			map = new HashMap<String, String>();
			map.put("MENU", v_menu);
			v_menu_list.add(map);
			v_start += 4; //��ַ�������ۼ�
		}
		bundle.putSerializable("DIALOG_MENU", (Serializable)v_menu_list);
		handler.obtainMessage(CMD_SHOW_GETMENU, bundle).sendToTarget();
	}
	//��ʾ������ѡ��Ի���  CMD_SHOW_GETDATASTREAMSELECT
	public void GetShowDatastreamChoiceActivity(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"������ѡ��Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		//��ȡ����ID û�б���0
		bundle.putString("DIALOG_TITLE", "");
		//��ȡ����������  2 byte
		int v_start = 2;
		int v_menu_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//��ȡ�������ı�
		v_start += 2;
		int v_id = 0;
		List<String> v_menu_list = new ArrayList<String>();
		for(int i = 0; i < v_menu_num; i ++)
		{
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_menu = m_idutils.getMessage(v_id, SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
			String v_name = DatastreamGetName(v_menu);
			v_menu_list.add(v_name);
			v_start += 4; //��ַ�������ۼ�
		}
		bundle.putSerializable("DATASTREAM_NAME", (Serializable)v_menu_list);
		handler.obtainMessage(CMD_SHOW_GETDATASTREAMSELECT, bundle).sendToTarget();
	}
	//��ʾ������Ի���    CMD_SHOW_GETDTC ��Ϊ CMD_SHOW_GETDTC_ADD
	public void GetShowDTCActivityAdd(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"������Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		//��ȡ����ID û�б���0
		bundle.putString("DIALOG_TITLE", "");
		//��ȡ����������  2 byte
		int v_start = 2;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		int v_real_dtc_num = v_dtc_num; //��ס
		//��ȡ�������ı�
		v_start += 2;
		int v_id = 0;
		int v_search_dtc_id = 0;
		List<Map<String, String>> v_dtc_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_dtc_num; i ++)
		{
			map = new HashMap<String, String>();
			//����������
			v_search_dtc_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_dtc = m_idutils.getMessage(v_search_dtc_id, SearchIdUtils.ID_TROUBLE_CODE_LIB_FILE);
			v_dtc = DelStringLast(v_dtc);
			v_start += 4; //��ַ�������ۼ�
			//������״̬
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_dtc_status = m_idutils.getMessage(v_id, SearchIdUtils.ID_TROUBLE_CODE_STATUS_LIB_FILE);
			v_dtc_status = DelStringLast(v_dtc_status);
			map.put("DTC_STATUS", v_dtc_status);
			v_start += 4; //��ַ�������ۼ�
			//������ID DPUString
			v_id = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff) + 2;
			byte [] v_dtc_id = new byte[v_id];
			//��ֹϵͳ�����������
			if(v_id > databuf.length - v_start)
				v_id = databuf.length - v_start;
			System.arraycopy(databuf, v_start, v_dtc_id, 0, v_id);
			String v_dtc_id_name = GetDpuString(v_dtc_id);
			v_dtc_id_name = FindDpuId(v_dtc_id_name); //���������DPU_ID
			v_dtc_id_name = DelStringLast(v_dtc_id_name);
			if(v_dtc.length() > 6)
			{
				map.put("DTC_ID", v_dtc.substring(0,v_dtc.indexOf(0x00)));
				map.put("DTC_NAME", v_dtc.substring(v_dtc.indexOf(0x00) + 1,v_dtc.length()));
				v_dtc_list.add(map);
			}
			else
			{
				if(v_dtc_id_name.length() < 2) //��ʵ���޹�����
				{
					v_real_dtc_num --;
				}
				else
				{
					map.put("DTC_ID", v_dtc_id_name);
					map.put("DTC_NAME", "ID:" + Integer.toHexString(v_search_dtc_id));
					v_dtc_list.add(map);
				}
			}	
			v_start += v_id;
		}
		bundle.putInt("DTC_NUM", v_real_dtc_num); //��ʵ�ʹ������������ȥ
		bundle.putSerializable("DTC", (Serializable)v_dtc_list);
		handler.obtainMessage(CMD_SHOW_GETDTC_ADD, bundle).sendToTarget();
	}
	//�������Ի�����ʾ CMD_SHOW_GETDATASTREAM
	public void GetShowDatastreamActivity(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"������ѡ��Ի�����ʾ");
		Bundle bundle = new Bundle();// ���ڴ�������
		//��ȡ����ID û�б���0
		bundle.putString("DIALOG_TITLE", "");
		//��ȡ����������  2 byte
		int v_start = 2;
		int v_datastream_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//��ȡ�������ı�
		v_start += 2;
		int v_id = 0;
		int v_value_len = 0;
		List<Map<String, String>> v_datastream_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_datastream_num; i ++)
		{
			//��ȡ����������
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_data = m_idutils.getMessage(v_id, SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
			map = new HashMap<String, String>();
			String v_name = DatastreamGetName(v_data);
			String v_unit = DatastreamGetUnit(v_data);
			map.put("DATASTREAM_NAME", v_name);
			//�������������ƺ͵�λ
			//�����������λ
			map.put("DATASTREAM_UNIT", v_unit);
			v_start += 4; //��ַ�������ۼ�
			v_value_len = (databuf[v_start] & 0xff); //������ֵ����
			//������ֵ
			v_start += 1;
			byte [] v_value = new byte[v_value_len];
			//if(D) Log.i(TAG,"buflen=" + databuf.length + " v_start=" + v_start + " v_value_len=" + v_value_len);
			//��ֹϵͳ�����������
			if(v_value_len > databuf.length - v_start)
				v_value_len = databuf.length - v_start;
			System.arraycopy(databuf, v_start, v_value, 0, v_value_len);
			String v_value_str = null;
			try {
				v_value_str = new String(v_value,"GB2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			v_value_str = FindDpuId(v_value_str);
			v_value_str = DelStringLast(v_value_str);
			v_start += v_value_len; //��ַ�ۼ�
			//���������ֵ
			map.put("DATASTREAM_VALUE", v_value_str);
			v_datastream_list.add(map);
		}
		bundle.putSerializable("DATASTREAM", (Serializable)v_datastream_list);
		handler.obtainMessage(CMD_SHOW_GETDATASTREAM, bundle).sendToTarget();
	}
	//ˢ��������ͼ��
	public void GetShowDatastreamgraph(Bundle bundle,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"������ѡ��Ի�����ʾ");
		//��ȡ����ID û�б���0
		bundle.putString("DIALOG_TITLE", "");
		//��ȡ����������  2 byte
		int v_start = 2;
		int v_datastream_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//��ȡ�������ı�
		v_start += 2;
		int v_id = 0;
		int v_value_len = 0;
		List<Map<String, String>> v_datastream_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_datastream_num; i ++)
		{
			//��ȡ����������
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_data = m_idutils.getMessage(v_id, SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
			map = new HashMap<String, String>();
			String v_name = DatastreamGetName(v_data);
			String v_unit = DatastreamGetUnit(v_data);
			map.put("DATASTREAM_NAME", v_name);
			//�������������ƺ͵�λ
			//�����������λ
			map.put("DATASTREAM_UNIT", v_unit);
			v_start += 4; //��ַ�������ۼ�
			v_value_len = (databuf[v_start] & 0xff); //������ֵ����
			//������ֵ
			v_start += 1;
			byte [] v_value = new byte[v_value_len];
			System.arraycopy(databuf, v_start, v_value, 0, v_value_len);
			String v_value_str = null;
			try {
				v_value_str = new String(v_value,"GB2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			v_start += v_value_len; //��ַ�ۼ�
			//���������ֵ
			map.put("DATASTREAM_VALUE", v_value_str);
			v_datastream_list.add(map);
		}
		bundle.putSerializable("DATASTREAM", (Serializable)v_datastream_list);
	}
	//���Ͱ�ť��Ӧ����
	public int SendDialogButton(int button,int cmd_return)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = (byte)cmd_return;
		byte [] v_para = new byte[2];
		v_para[0] = (byte)0x00;
		v_para[1] = (byte)button;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME);
	}
	//����Menu�Ի���ť��Ӧ����
	public int SendMenuClickList(int position)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = CMD_SHOW_SETMENU;
		byte [] v_para = new byte[]{(byte)(position / 0x100),(byte)(position % 0x100)};
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME);
	}
	//���͵�ǰ���淵����һ���˵�����
	public int SendCurrentbackPre(int currentpage)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = (byte)currentpage;
		byte [] v_para = new byte[]{(byte) 0xff, 0x01};
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME * 5);
	}
	//���͵�ǰ���淵����һ���˵�����
	public int SendNoDtcButtonOK()
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = CMD_SHOW_SETDTC_ADD;
		byte [] v_para = new byte[]{(byte) 0xff, 0x03};
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME);
	}
	//������λ����λָ��
	public int SendDpuReset()
	{
		m_commond[0] = CMD_OPEN;
		m_commond[1] = CMD_OPEN_RESET_DPU;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToNone, m_commond, null, 0, MAX_TIME);
	}
	//����������ѡ������
	public int SendDatastreamChoice(byte[] choice)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = CMD_SHOW_SETDATASTREAMSELECT;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, choice, choice.length, MAX_TIME);
	}
	//����DPU_ID
	public String FindDpuId(String str)
	{
		String v_new = str;
		int id_size = 0;
		while(true)
		{
			id_size = v_new.indexOf("[DPU_ID",id_size);
			if(id_size >= 0)//�鵽��
			{
				String v_idbuf = v_new.substring(id_size+9,id_size+17);
				byte v_fileID = (byte)v_new.charAt(id_size + 7);
				int searchID = HexStringtoInt(v_idbuf);		
				String id_text = m_idutils.getMessage(searchID, (v_fileID & 0xFF));
				id_text = DelStringLast(id_text);
				//�滻��PDU_ID
				String v_data = v_new.substring(0,id_size);
				v_data += id_text + v_new.substring(id_size + 18, v_new.length());
				v_new = v_data;
			}
			else
			{
				break;
			}
		}
		return v_new;
	}
	public static int HexStringtoInt(String hexstr)
	{
		int x=0,iRet = 0;
		if(hexstr == null)
			return iRet;
		int length = hexstr.length();
		for(int i = 0; i < length; i ++)
		{
			char c = hexstr.charAt(i);
			//��'0'->0��'a'->10
		    if(c>='a'&&c<='f'){
		    	x=c-'a'+10;
		    }else if(c>='A'&&c<='F'){
		    	x=c-'A'+10;
		    }else if(c>='0'&&c<='9'){
		     x=c-'0';
		    }
		    iRet=(iRet<<4)| x;//n=n*4+x,��λƴ��	
		}
		return iRet;
	}
	//ɾ��String�����00
	public static String DelStringLast(String v_str)
	{
		if(v_str == null)
			return "";
		int v_len = v_str.length();
		if(v_len < 1)
			return "";
		int i = 0;
		for(i = v_len - 1; i >= 0; i --)
		{
			if(v_str.charAt(i) != 0x00)
				break;
		}
		v_str = v_str.substring(0, i + 1);
		return v_str;
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
}

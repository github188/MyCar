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
	//命令格式
	/* <起始标志> + <目标地址> + <源地址> + <包长度> + <计数器> + <命令字> + <数据> + <包校验>
	<起始标志>: 2byte   0x55,0xAA
	<目标地址>:	1byte	0xF8
	<源地址>:	1byte	0xF0
	<包长度>:	2byte  <计数器> + <命令字> + <数据>
	<计数器>：	1byte  0x00~0xFF循环计数
	<命令字>：  2byte  
	<数据>：
	<包校验>：  1byte  <目标地址> + <源地址> + <包长度> + <计数器> + <命令字> + <数据>

	DPUString格式
	<长度> + <数据> + <结束标志>
	<长度> :	2byte  <数据> + <结束标志>
	<数据>:		
	<结束标志>: 1byte 0x00
	*////////////////////////////////////////////////////////////////////////////////////////
	//主命令字
	public final static int	CMD_MAIN = 0x21;	//设置或读取设置命令字
	public final static int	CMD_SHOW = 0x22;	//显示函数通信命令字
	public final static int	CMD_ERROR = 0x23;	//异常处理
	public final static int	CMD_UPDATE = 0x24;	//软件升级
	public final static int	CMD_OPEN = 0x25;	//建立连接\安全校验\断开连接及链路
	public final static int	CMD_CRECORDER = 0x26;	//Crecorder模式
	public final static int	CMD_SMARTBOX = 0x27;	//SmartBox模式
	public final static int	CMD_SET = 0x28;	//设置 
	public final static int	CMD_OBD = 0x29;	//OBD模式	
	public final static int CMD_SIMPLE = 0X2A;	//简单诊断模式
	
	//第二个命令字 CMD1_MAIN
	public final static int	CMD_MAIN_SETTIME = 0x01;	//设置时钟
	public final static int	CMD_MAIN_READTIME = 0x02;	//读取时钟
	public final static int	CMD_MAIN_READHARD = 0x03;	//取DPU 接头硬件版本信息 
	public final static int	CMD_MAIN_READSOFT = 0x04;	//取DPU 接头库文件版本（车型名称，车型版本，语言）
	public final static int	CMD_MAIN_READBOOT = 0x05;	//取DPU 接头软件版本（boot，download，诊断软件）
	public final static int	CMD_MAIN_READERIA = 0x06;	//取DPU 接头防篡改标识（支持语言类型，销售区域）
	public final static int	CMD_MAIN_WRITESN = 0x07;	//写DPU 接头序列号 
	public final static int	CMD_MAIN_WRITEBLUE = 0x08;	//设置蓝牙名称
	public final static int	CMD_MAIN_SETMODE = 0x09;	//复位 DPU 运行模式（用于切换诊断模式时）
	public final static int	CMD_MAIN_CLEARFLASH = 0x0A;	//清除 Flash 数据 
	public final static int	CMD_MAIN_SETPASS = 0x0B;	//设置或修改安全密码指令
	public final static int	CMD_MAIN_RESETPASS = 0x0F;	//恢复初始密码 
	public final static int	CMD_MAIN_INPUTPASS = 0x10;	//验证安全密码指令 
	public final static int	CMD_MAIN_DOWNLOAD = 0x11;	//跳转至 Download 代码入口 
	public final static int	CMD_MAIN_WRITECONFIG = 0x12;	//写接头配置文件 
	public final static int	CMD_MAIN_READCONFIG = 0x13;	//读接头配置文件
	public final static int	CMD_MAIN_READMODE = 0x14;	//读取当前状态
	public final static int	CMD_MAIN_READLICENCE = 0x15;	//读接头Licence
	public final static int	CMD_MAIN_READBLUEMAC = 0x16;	//读取接头蓝牙地址
	
	//显示命令字  CMD_SHOW
	public final static int	CMD_SHOW_GETMENU = 0x10;		//获得菜单
	public final static int	CMD_SHOW_SETMENU = 0x11;		//返回菜单
	public final static int	CMD_SHOW_GETDTC	=	0x12;			//获得故障码
	public final static int	CMD_SHOW_SETDTC = 0x13;			//返回故障码
	public final static int	CMD_SHOW_GETFRAME = 0x14;		//获得冻结帧
	public final static int	CMD_SHOW_SETFRAME = 0x15;		//返回冻结帧
	public final static int	CMD_SHOW_GETDATASTREAMSELECT = 0x16;	//获得数据流选择
	public final static int	CMD_SHOW_SETDATASTREAMSELECT = 0x17;	//返回数据流选择
	public final static int	CMD_SHOW_GETDATASTREAM = 0x18;		//获得数据流
	public final static int	CMD_SHOW_SETDATASTREAM = 0x19;		//返回数据流
	public final static int	CMD_SHOW_GETDONGZUO = 0x1A;			//获得动作测试
	public final static int	CMD_SHOW_SETDONGZUO = 0x1B;			//返回动作测试
	public final static int	CMD_SHOW_GETDIALOG = 0x1C;			//获得对话框
	public final static int	CMD_SHOW_SETDIALOG = 0X1D;			//返回对话框
	public final static int	CMD_SHOW_GETDIALOGID = 0x1E;		//获得ID对话框
	public final static int	CMD_SHOW_SETDIALOGID = 0x1F;		//返回ID对话框
	public final static int	CMD_SHOW_GETINPUT_INT = 0X20;			//获得文本整数输入对话框
	public final static int	CMD_SHOW_SETINPUT_INT = 0X21;			//返回文本整数输入对话框
	public final static int	CMD_SHOW_GETINPUT_INTID = 0X22;		//获得ID整数输入对话框
	public final static int	CMD_SHOW_SETINPUT_INTID = 0X23;		//返回ID整数输入对话框
	public final static int	CMD_SHOW_GETINPUT_STR = 0X24;			//获得文本字符串输入对话框
	public final static int	CMD_SHOW_SETINPUT_STR = 0X25;			//返回文本字符串输入对话框
	public final static int	CMD_SHOW_GETINPUT_STRID = 0X26;		//获得ID字符串输入对话框
	public final static int	CMD_SHOW_SETINPUT_STRID = 0x27;		//返回ID字符串输入对话框
	public final static int	CMD_SHOW_GETFILE_ID = 0X28;				//获得ID文件对话框
	public final static int	CMD_SHOW_SETFILE_ID = 0x29;				//返回ID文件对话框
	public final static int	CMD_SHOW_GETINPUT_FLOAT = 0X2A;		//获得文本浮点数据输入对话框
	public final static int	CMD_SHOW_SETINPUT_FLOAT = 0X2B;		//返回文本浮点数据输入对话框
	public final static int	CMD_SHOW_GETINPUT_FLOAT_ID = 0X2C;	//获得ID浮点数据输入对话框
	public final static int	CMD_SHOW_SETINPUT_FLOAT_ID = 0X2D;	//返回ID浮点数据输入对话框
	public final static int	CMD_SHOW_GETDATASTREAM_PAGE = 0X2E;	//获得数据流分页显示
	public final static int	CMD_SHOW_SETDATASTREAM_PAGE = 0X2F;	//返回数据流分页显示
	public final static int	CMD_SHOW_GETDTC_ADD = 0X30;					//获得故障码扩展指令
	public final static int	CMD_SHOW_SETDTC_ADD = 0X31;					//返回故障码扩展指令
	public final static int CMD_SHOW_ROOT_DIAG	= 0x32;			//根目录提示，收到此命令表示目前在根目录
	//显示未知对话框
	public final static int CMD_SHOW_NONE = 0xFF; 				//未知对话框显示
	//错误显示 ERROR
	public final static int	CMD_ERROR_CHECK = 0X01;		//校验错误
	public final static int	CMD_ERROR_LONGBAG = 0X02;		//包长度过长

	//建立连接\安全校验\断开连接及链路 OPEN
	public final static int	CMD_OPEN_LINK = 0X01;				//链路保存
	public final static int	CMD_OPEN_CONNECT = 0X02;		//建立连接
	public final static int	CMD_OPEN_SAFECHECK = 0X03;	//安全校验
	public final static int	CMD_OPEN_DISCONNECT = 0X04;	//关闭链接
	public final static int	CMD_OPEN_RESET_DPU = 0x05;	//复位接头
	public final static int	CMD_OPEN_SEARCH = 0x00;			//一键扫描
	
	//升级中心命令
	public final static int	CMD_UPDATE_TITLE = 0X01;		//发送准备升级命令
	public final static int	CMD_UPDATE_SENDNAME = 0X02;		//发送升级文件名称和长度
	public final static int	CMD_UPDATE_SENDDATA	= 0X03;		//发送升级文件内容
	public final static int	CMD_UPDATE_SENDCHECK = 0X04;	//发送升级文件校验
	public final static int	CMD_UPDATE_SENDCOMPLETE = 0X05;	//发送升级完成命令
	public final static int	CMD_UPDATE_SENDCONTINUE = 0X06;	//发送断点续传命令
	public final static int	CMD_UPDATE_ENABLEBOOT = 0x07;	//启用更新固件命令
	public final static int	CMD_UPDATE_READFILEINFO	= 0X08;	//读取接头文件信息
	

	//Crecorder模式 CRECORDER
	public final int	CMD_CRECORDER_START = 0X01;				//启动Crecorder模式
	public final int	CMD_CRECORDER_SEARCH = 0X02;			//查询文件信息
	public final int	CMD_CRECORDER_READ_FILE = 0x03;		//读取文件
	public final int	CMD_CRECORDER_DEL_FILE = 0X04;		//删除文件
	public final int	CMD_CRECORDER_DEL_ALLFILE = 0X05;	//删除所有文件

	//简单诊断模式 SIMPLE
	public final int	CMD_SIMPLE_SCAN = 0X00;				//一键扫描系统支持的列表
	public final int	CMD_SIMPLE_GETLIST = 0x01;		//获取支持的系统列表
	public final int	CMD_SIMPLE_GETDATASTREAMSELECT = 0X02;	//读取指定系统数据流列表
	public final int	CMD_SIMPLE_GETDTC = 0X03;			//读取指定系统故障码列表
	public final int	CMD_SIMPLE_CLEARDTC = 0X04;		//清除指定系统故障码
	public final int	CMD_SIMPLE_READDATESTREAM = 0X11;	//读取指定ID数据流
	public final int	CMD_SIMPLE_READLAST = 0X13;			//读取上次数据
	//其他参数
	public final int	ADD_DPU = 0xF0;
	public final int	ADD_PC = 0xF8;
	
	public final int 	MAX_TIME = 1000;  	//超时时间
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
	
	//私有变量
	private Activity m_now_activity = null;		//记录当前activity引用
	private Dialog m_now_dialog = null;			//记录当前的对话框引用
	//出事后蓝牙服务
	private BluetoothDataService m_blue_service = null;
	//命令字
	private byte [] m_commond = null;
	//读ggp文件
	SearchIdUtils m_idutils = null;
	//单例此服务
	private static DiagnoseDataService m_myservice = null;
	public synchronized static DiagnoseDataService getInstance()
	{
		if(m_myservice == null)
			m_myservice = new DiagnoseDataService();
		return m_myservice;
	}
	//构造方法
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
	//初始化ggp方法
	public void InitialGGPInstance()
	{
		m_idutils = SearchIdUtils.SearchIdInstance("");
	}
	//-------------------------诊断实现方法---------------------------------------
	//读取诊断版本信息，第一步21 03；第二步21 04，
	
	public int ReadVersionStep(int step)
	{
		if(step == 1) //读硬件版本信息
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READHARD;
		}
		else if(step == 2)  //读软件版本信息
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READSOFT;
		}	
		else if(step == 3)	//查询是否进入download模式
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READMODE;
		}
		else if(step == 4)   //读取BOOT版本
		{
			m_commond[0] = CMD_MAIN;
			m_commond[1] = CMD_MAIN_READBOOT;
		}
		else if(step == 5) //查询升级是否完成
		{
			m_commond[0] = CMD_UPDATE;
			m_commond[1] = 0x06;
		}
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, null, 0, MAX_TIME);
	}
	//连接dpu进入诊断
	public int ConnectDpu()
	{
		m_commond[0] = CMD_OPEN;
		m_commond[1] = CMD_OPEN_LINK;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode, m_commond, null, 0, MAX_TIME);
	}
	//读取和设置诊断模式GetMode=0,SMARTBOX=1,MYCAR=2,CREADER=3,CRECORDER=4，OBD=5，QuickDiag=6 
	public void SetAndReadMode(byte mode)
	{
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_SETMODE;
		byte [] paradata = new byte[1];
		paradata[0] = mode;
 		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, paradata, paradata.length, MAX_TIME);
	}
	//第四步安全认证1: 25 02 01;  2: 25 03 01 带密码
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
	//获取断点续传是否完成命令
	public int ReadUpdateComplete(byte[] databuf,int datalen)
	{
		//在3个DPUString后面
		int v_len = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		int v_len1 = (databuf[v_len + 2]&0xFF)* 0x100 + (databuf[v_len + 2 + 1] &0xFF) + 2;
		int v_len2 = (databuf[v_len + v_len1 + 2]&0xFF)* 0x100 + (databuf[v_len + v_len1 + 2 + 1] &0xFF) + 2;
		if(v_len1 < datalen)
			return databuf[v_len + v_len1 + v_len2 + 2]&0xFF;
		else
			return 0xFF;
	}
	//重置密码
	public void ResetPassword(byte[] password,int passlen)
	{
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_RESETPASS;
		byte[] v_dpupassword = new byte[passlen + 3];
		int v_dpulen = AddDataToDpuString(password, passlen, v_dpupassword, v_dpupassword.length);
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, v_dpupassword, v_dpulen, MAX_TIME);
	}
	//输入密码认证
	public void EnterPassword(byte[] password,int passlen)
	{
		m_commond[0] = CMD_MAIN;
		m_commond[1] = CMD_MAIN_INPUTPASS;
		byte[] v_dpupassword = new byte[passlen + 3];
		int v_dpulen = AddDataToDpuString(password, passlen, v_dpupassword, v_dpupassword.length);
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToOne, m_commond, v_dpupassword, v_dpulen, MAX_TIME);
	}
	
	//过滤接收数据的包头
	public int GetDataFromBluetooth(byte[] databuf,int datalen,byte[] Recbuf)
	{
		System.arraycopy(databuf, 7, Recbuf, 0, datalen - 8);
		return datalen - 8;
	}
	//过滤DPU String
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
	//组装DPUString
	public int AddDataToDpuString(byte[] source,int len,byte [] object,int objlen)
	{
		//提供的缓冲区长度太短
		if(objlen < len + 3)
			return -1;
		//添加到DPUString
		object[0] = (byte)(((len + 1) / 0x100) & 0xFF);
		object[1] = (byte)(((len + 1) % 0x100) & 0xFF);
		System.arraycopy(source, 0, object, 2, len);
		object[len + 2] = 0x00;
		return len + 3;
	}
	//获取硬件唯一标识和产品序列号
	public void GetDpuIdandSerialNo(byte[] databuf,int datalen,ArrayList<String> list)
	{
		//获取DPU ID
		int v_id_len = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		byte[] v_id = new byte[v_id_len];
		System.arraycopy(databuf, 2, v_id, 0, v_id_len);
		list.add(GetDpuString(v_id));
		//获取序列号
		int v_serial_len = (databuf[2 + v_id_len]&0xFF)* 0x100 + (databuf[2 + v_id_len + 1] &0xFF) + 2;
		byte [] v_serial = new byte[v_serial_len];
		System.arraycopy(databuf, 2 + v_id_len, v_serial, 0, v_serial_len);	
		list.add(GetDpuString(v_serial));
	}
	//获取诊断软件信息
	public void GetDiagSoftwareVersion(byte[] databuf,int datalen,ArrayList<String> list)
	{
		//获取软件语言
		int v_lang = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		byte[] v_langname = new byte[v_lang];
		System.arraycopy(databuf, 2, v_langname, 0, v_lang);
		list.add(GetDpuString(v_langname));
		//获取车型名称
		int v_carnamelen = (databuf[2 + v_lang]&0xFF)* 0x100 + (databuf[2 + v_lang + 1] &0xFF) + 2;
		byte [] v_carname = new byte[v_carnamelen];
		System.arraycopy(databuf, 2 + v_lang, v_carname, 0, v_carnamelen);	
		list.add(GetDpuString(v_carname));
		//获取车型版本
		int v_carverlen = (databuf[2 + v_lang + v_carnamelen]&0xFF)* 0x100 + (databuf[2 + v_lang + v_carnamelen + 1] &0xFF) + 2;
		byte [] v_carnver = new byte[v_carverlen];
		System.arraycopy(databuf, 2 + v_lang + v_carnamelen, v_carnver, 0, v_carverlen);	
		list.add(GetDpuString(v_carnver));
	}
	//获取BOOT、DOWNLOAD版本信息
	public void GetbootanddownloadInfo(byte[] databuf,int datalen,ArrayList<String> list)
	{
		//获取DPU ID
		int v_id_len = (databuf[2]&0xFF)* 0x100 + (databuf[3] &0xFF) + 2;
		byte[] v_id = new byte[v_id_len];
		System.arraycopy(databuf, 2, v_id, 0, v_id_len);
		list.add(GetDpuString(v_id));
		//获取序列号
		int v_serial_len = (databuf[2 + v_id_len]&0xFF)* 0x100 + (databuf[2 + v_id_len + 1] &0xFF) + 2;
		byte [] v_serial = new byte[v_serial_len];
		System.arraycopy(databuf, 2 + v_id_len, v_serial, 0, v_serial_len);	
		list.add(GetDpuString(v_serial));
	}
	
	//获取错误ID信息
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
	//获得对话框文本信息 CMD_SHOW_GETDIALOG
	//databuf <命令字> + <数据>
	//Handler 为返回的对话框
	public void GetShowDialog(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"文本对话框显示");
		Bundle bundle = new Bundle();// 用于传送数据
		//插入对话框类型
		int v_style = (databuf[2]&0xFF) * 0x100 + (databuf[3]&0xFF);
		bundle.putInt("DIALOG_STYLE", v_style);  //对话框类型
		//插入对话框标题
		int v_title_len = (databuf[4]&0xFF) * 0x100 + (databuf[5]&0xFF) + 2; //DPUSting长度
		byte [] v_title = new byte[v_title_len];
		System.arraycopy(databuf, 4, v_title, 0, v_title_len);
		String v_show = GetDpuString(v_title);
		v_show = FindDpuId(v_show); //查找里面的DPU_ID
		v_show = DelStringLast(v_show);
		bundle.putString("DIALOG_TITLE", v_show);
		//插入对话框文本
		int v_msg_len = (databuf[4 + v_title_len]&0xFF) * 0x100 + (databuf[4 + v_title_len + 1]&0xFF) + 2;
		byte [] v_msg = new byte[v_msg_len];
		System.arraycopy(databuf, 4 + v_title_len, v_msg, 0, v_msg_len);
		String v_text = GetDpuString(v_msg);
		v_text = FindDpuId(v_text); //查找里面的DPU_ID
		v_text = DelStringLast(v_text);
		bundle.putString("DIALOG_BODY", v_text);
		bundle.putInt("DIALOG_CMD_RETURN", CMD_SHOW_SETDIALOG); //对话框返回类型
		handler.obtainMessage(CMD_SHOW_GETDIALOG, bundle).sendToTarget();
	}
	//显示ID对话框   CMD_SHOW_GETDIALOGID
	public void GetShowDialogID(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"ID对话框显示");
		Bundle bundle = new Bundle();// 用于传送数据
		//插入对话框类型
		int v_style = (databuf[2]&0xFF) * 0x100 + (databuf[3]&0xFF);
		bundle.putInt("DIALOG_STYLE", v_style);  //对话框类型
		//插入对话框标题ID
		int v_start = 4;
		int v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
				+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
		String v_title = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
		v_title = DelStringLast(v_title);
		bundle.putString("DIALOG_TITLE", v_title);
		//插入对话框文本ID
		v_start += 4;
		v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
				+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
		String v_body = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
		v_body = DelStringLast(v_body);
		bundle.putString("DIALOG_BODY", v_body);
		bundle.putInt("DIALOG_CMD_RETURN", CMD_SHOW_SETDIALOGID); //对话框返回类型
		handler.obtainMessage(CMD_SHOW_GETDIALOGID, bundle).sendToTarget();
	}
	//显示菜单 CMD_SHOW_GETMENU
	public void GetShowMenuActivity(Handler handler,byte[] databuf,int datalen,boolean isroot)
	{
		if(D) Log.i(TAG,"菜单树显示");
		Bundle bundle = new Bundle();// 用于传送数据
		//判断是否是根目录
		if(isroot == true)
			bundle.putBoolean("DIALOG_ROOT", true);
		else
			bundle.putBoolean("DIALOG_ROOT", false);
		//获取标题ID 4 byte
		int v_start = 2;
		int v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
				+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
		String v_title = m_idutils.getMessage(v_id, SearchIdUtils.ID_TEXT_LIB_FILE);
		if(D) Log.i(TAG,"++" + v_title +"--len=" + v_title.length());
		v_title = DelStringLast(v_title);
		if(D) Log.i(TAG,"++" + v_title +"-len=" + v_title.length());
		bundle.putString("DIALOG_TITLE", v_title);
		//获取帮助ID 4 byte
		v_start += 4;
		//获取菜单条数
		v_start += 4;
		int v_menu_num = (databuf[v_start ] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//获取菜单文本
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
			v_start += 4; //地址计数器累加
		}
		bundle.putSerializable("DIALOG_MENU", (Serializable)v_menu_list);
		handler.obtainMessage(CMD_SHOW_GETMENU, bundle).sendToTarget();
	}
	//显示数据流选择对话框  CMD_SHOW_GETDATASTREAMSELECT
	public void GetShowDatastreamChoiceActivity(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"数据流选择对话框显示");
		Bundle bundle = new Bundle();// 用于传送数据
		//获取标题ID 没有标题0
		bundle.putString("DIALOG_TITLE", "");
		//获取数据流条数  2 byte
		int v_start = 2;
		int v_menu_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//获取数据流文本
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
			v_start += 4; //地址计数器累加
		}
		bundle.putSerializable("DATASTREAM_NAME", (Serializable)v_menu_list);
		handler.obtainMessage(CMD_SHOW_GETDATASTREAMSELECT, bundle).sendToTarget();
	}
	//显示故障码对话框    CMD_SHOW_GETDTC 改为 CMD_SHOW_GETDTC_ADD
	public void GetShowDTCActivityAdd(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"故障码对话框显示");
		Bundle bundle = new Bundle();// 用于传送数据
		//获取标题ID 没有标题0
		bundle.putString("DIALOG_TITLE", "");
		//获取数据流条数  2 byte
		int v_start = 2;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		int v_real_dtc_num = v_dtc_num; //记住
		//获取数据流文本
		v_start += 2;
		int v_id = 0;
		int v_search_dtc_id = 0;
		List<Map<String, String>> v_dtc_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_dtc_num; i ++)
		{
			map = new HashMap<String, String>();
			//故障码描述
			v_search_dtc_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_dtc = m_idutils.getMessage(v_search_dtc_id, SearchIdUtils.ID_TROUBLE_CODE_LIB_FILE);
			v_dtc = DelStringLast(v_dtc);
			v_start += 4; //地址计数器累加
			//故障码状态
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_dtc_status = m_idutils.getMessage(v_id, SearchIdUtils.ID_TROUBLE_CODE_STATUS_LIB_FILE);
			v_dtc_status = DelStringLast(v_dtc_status);
			map.put("DTC_STATUS", v_dtc_status);
			v_start += 4; //地址计数器累加
			//故障码ID DPUString
			v_id = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff) + 2;
			byte [] v_dtc_id = new byte[v_id];
			//防止系统内容溢出处理
			if(v_id > databuf.length - v_start)
				v_id = databuf.length - v_start;
			System.arraycopy(databuf, v_start, v_dtc_id, 0, v_id);
			String v_dtc_id_name = GetDpuString(v_dtc_id);
			v_dtc_id_name = FindDpuId(v_dtc_id_name); //查找里面的DPU_ID
			v_dtc_id_name = DelStringLast(v_dtc_id_name);
			if(v_dtc.length() > 6)
			{
				map.put("DTC_ID", v_dtc.substring(0,v_dtc.indexOf(0x00)));
				map.put("DTC_NAME", v_dtc.substring(v_dtc.indexOf(0x00) + 1,v_dtc.length()));
				v_dtc_list.add(map);
			}
			else
			{
				if(v_dtc_id_name.length() < 2) //其实是无故障码
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
		bundle.putInt("DTC_NUM", v_real_dtc_num); //把实际故障码个数传回去
		bundle.putSerializable("DTC", (Serializable)v_dtc_list);
		handler.obtainMessage(CMD_SHOW_GETDTC_ADD, bundle).sendToTarget();
	}
	//数据流对话框显示 CMD_SHOW_GETDATASTREAM
	public void GetShowDatastreamActivity(Handler handler,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"数据流选择对话框显示");
		Bundle bundle = new Bundle();// 用于传送数据
		//获取标题ID 没有标题0
		bundle.putString("DIALOG_TITLE", "");
		//获取数据流条数  2 byte
		int v_start = 2;
		int v_datastream_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//获取数据流文本
		v_start += 2;
		int v_id = 0;
		int v_value_len = 0;
		List<Map<String, String>> v_datastream_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_datastream_num; i ++)
		{
			//获取数据流名称
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_data = m_idutils.getMessage(v_id, SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
			map = new HashMap<String, String>();
			String v_name = DatastreamGetName(v_data);
			String v_unit = DatastreamGetUnit(v_data);
			map.put("DATASTREAM_NAME", v_name);
			//分离数据流名称和单位
			//添加数据流单位
			map.put("DATASTREAM_UNIT", v_unit);
			v_start += 4; //地址计数器累加
			v_value_len = (databuf[v_start] & 0xff); //数据流值长度
			//数据流值
			v_start += 1;
			byte [] v_value = new byte[v_value_len];
			//if(D) Log.i(TAG,"buflen=" + databuf.length + " v_start=" + v_start + " v_value_len=" + v_value_len);
			//防止系统内容溢出处理
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
			v_start += v_value_len; //地址累加
			//添加数据流值
			map.put("DATASTREAM_VALUE", v_value_str);
			v_datastream_list.add(map);
		}
		bundle.putSerializable("DATASTREAM", (Serializable)v_datastream_list);
		handler.obtainMessage(CMD_SHOW_GETDATASTREAM, bundle).sendToTarget();
	}
	//刷新数据流图像
	public void GetShowDatastreamgraph(Bundle bundle,byte[] databuf,int datalen)
	{
		if(D) Log.i(TAG,"数据流选择对话框显示");
		//获取标题ID 没有标题0
		bundle.putString("DIALOG_TITLE", "");
		//获取数据流条数  2 byte
		int v_start = 2;
		int v_datastream_num = (databuf[v_start] & 0xff) * 0x100 + (databuf[v_start + 1] & 0xff);
		//获取数据流文本
		v_start += 2;
		int v_id = 0;
		int v_value_len = 0;
		List<Map<String, String>> v_datastream_list = new ArrayList<Map<String,String>>();
		HashMap<String, String> map = null;
		for(int i = 0; i < v_datastream_num; i ++)
		{
			//获取数据流名称
			v_id = (databuf[v_start] & 0xff) * 0x1000000 + (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100 + (databuf[v_start + 3] & 0xff);
			String v_data = m_idutils.getMessage(v_id, SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
			map = new HashMap<String, String>();
			String v_name = DatastreamGetName(v_data);
			String v_unit = DatastreamGetUnit(v_data);
			map.put("DATASTREAM_NAME", v_name);
			//分离数据流名称和单位
			//添加数据流单位
			map.put("DATASTREAM_UNIT", v_unit);
			v_start += 4; //地址计数器累加
			v_value_len = (databuf[v_start] & 0xff); //数据流值长度
			//数据流值
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
			v_start += v_value_len; //地址累加
			//添加数据流值
			map.put("DATASTREAM_VALUE", v_value_str);
			v_datastream_list.add(map);
		}
		bundle.putSerializable("DATASTREAM", (Serializable)v_datastream_list);
	}
	//发送按钮响应方法
	public int SendDialogButton(int button,int cmd_return)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = (byte)cmd_return;
		byte [] v_para = new byte[2];
		v_para[0] = (byte)0x00;
		v_para[1] = (byte)button;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME);
	}
	//发送Menu对话框按钮响应方法
	public int SendMenuClickList(int position)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = CMD_SHOW_SETMENU;
		byte [] v_para = new byte[]{(byte)(position / 0x100),(byte)(position % 0x100)};
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME);
	}
	//发送当前界面返回上一级菜单命令
	public int SendCurrentbackPre(int currentpage)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = (byte)currentpage;
		byte [] v_para = new byte[]{(byte) 0xff, 0x01};
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME * 5);
	}
	//发送当前界面返回上一级菜单命令
	public int SendNoDtcButtonOK()
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = CMD_SHOW_SETDTC_ADD;
		byte [] v_para = new byte[]{(byte) 0xff, 0x03};
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, v_para, v_para.length, MAX_TIME);
	}
	//发送下位机复位指令
	public int SendDpuReset()
	{
		m_commond[0] = CMD_OPEN;
		m_commond[1] = CMD_OPEN_RESET_DPU;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToNone, m_commond, null, 0, MAX_TIME);
	}
	//发送数据流选择命令
	public int SendDatastreamChoice(byte[] choice)
	{
		m_commond[0] = CMD_SHOW;
		m_commond[1] = CMD_SHOW_SETDATASTREAMSELECT;
		return m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_OneToMore, m_commond, choice, choice.length, MAX_TIME);
	}
	//查找DPU_ID
	public String FindDpuId(String str)
	{
		String v_new = str;
		int id_size = 0;
		while(true)
		{
			id_size = v_new.indexOf("[DPU_ID",id_size);
			if(id_size >= 0)//查到了
			{
				String v_idbuf = v_new.substring(id_size+9,id_size+17);
				byte v_fileID = (byte)v_new.charAt(id_size + 7);
				int searchID = HexStringtoInt(v_idbuf);		
				String id_text = m_idutils.getMessage(searchID, (v_fileID & 0xFF));
				id_text = DelStringLast(id_text);
				//替换掉PDU_ID
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
			//对'0'->0，'a'->10
		    if(c>='a'&&c<='f'){
		    	x=c-'a'+10;
		    }else if(c>='A'&&c<='F'){
		    	x=c-'A'+10;
		    }else if(c>='0'&&c<='9'){
		     x=c-'0';
		    }
		    iRet=(iRet<<4)| x;//n=n*4+x,移位拼接	
		}
		return iRet;
	}
	//删除String后面的00
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
	//从数据流分离出单位，只显示数据流名字
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
	//从数据流分离出名字,只显示单位
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

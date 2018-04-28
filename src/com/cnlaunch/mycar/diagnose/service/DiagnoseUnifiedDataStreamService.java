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
	public final int MAX_TIME = 1000; // 超时时间
	// 初始化蓝牙服务
	private BluetoothDataService m_blue_service = null;
	// 延时线程
	private DelayTimeThread delayTimeThread;
	// 条件锁
	ConditionVariable next = new ConditionVariable(false);
	// 命令格式
	/*
	 * <起始标志> + <目标地址> + <源地址> + <包长度> + <计数器> + <命令字> + <数据> + <包校验> <起始标志>:
	 * 2byte 0x55,0xAA <目标地址>: 1byte 0xF8 <源地址>: 1byte 0xF0 <包长度>: 2byte <计数器> +
	 * <命令字> + <数据> <计数器>： 1byte 0x00~0xFF循环计数 <命令字>： 2byte <数据>： <包校验>： 1byte
	 * <目标地址> + <源地址> + <包长度> + <计数器> + <命令字> + <数据>
	 * 
	 * DPUString格式 <长度> + <数据> + <结束标志> <长度> : 2byte <数据> + <结束标志> <数据>: <结束标志>:
	 * 1byte 0x00
	 */// /////////////////////////////////////////////////////////////////////////////////////
		// 简单诊断模式 SIMPLE
	public final static int CMD_SIMPLE = 0X2A; // 简单诊断模式
	public final static int CMD_MAIN = 0x21; // 设置或读取设置命令字
	public final static int CMD_OPEN = 0x25; // 建立连接\安全校验\断开连接及链路
	public final static int CMD_OPEN_SAFECHECK = 0X03; // 安全校验

	// 子命令字
	public final static int CMD_MAIN_INPUTPASS = 0x10; // 验证安全密码指令
	public final static int CMD_OPEN_CONNECT = 0X02; // 建立连接
	public final static int CMD_SHOW_GETDIALOG = 0x1C; // 显示对话框信息
	public final int CMD_SIMPLE_READDATESTREAM = 0X11; // 读取指定ID数据流
	public final int CMD_SIMPLE_SCAN = 0X00; // 一键扫描系统支持的列表
	public final int CMD_SIMPLE_GETLIST = 0x01; // 获取支持的系统列表
	public final int CMD_SIMPLE_GETDATASTREAMSELECT = 0X02; // 读取指定系统数据流列表
	// 接收数据
	private ReceiveReuqestDataThread reqDataThread = null;
	private final static int DT_CMD_MODE = 1; // 设置模式迟时
	private final static int DT_APPOINT_DATA_LIST = 2; // 读取指定系统数据流列表迟时
	private final static int DT_APPOINT_ID_LIST = 3; // 读取指定ID数据流迟时
	//超时线程
	private TimeOutThread timeOutThread;
	// 主端发送参数
	byte[] v_sendSystIDInfo = null;
	// 当前值
	private static String CURRENT_VALUE = "";
	// 单例此服务
	private static DiagnoseUnifiedDataStreamService m_myservice = null;
	boolean flag = false;
	public synchronized static DiagnoseUnifiedDataStreamService getInstance() {
		if (m_myservice == null)
			m_myservice = new DiagnoseUnifiedDataStreamService();
		return m_myservice;
	}
	// 构造方法
	private DiagnoseUnifiedDataStreamService() {
		m_commond = new byte[2];
		m_blue_service = BluetoothDataService.getInstance();
	}
	
	// 主端发送参数
	byte[] v_sendbufInfo = null;
	// 保存系统ID
	ArrayList<Byte> v_sysIdList = null;
	// 保存数据流列表
	ArrayList<Byte> v_dataStream_List = null;
	//支持统一的数据流ID
	HashMap<String, String> unifDataStreamMap = null;
	//判断是否需要系统扫描
	boolean isSysScan=true;
	//前端传递过来参数
	private String keyStr="";
	// 设置车辆里程当前值
	public static void setCarMileageCurrentValue(String key, String value) {

	}
	// 获取车辆里程当前值
	public String getCarMileageCurrentValue(String key) {
		keyStr=key;//传递参数
		CURRENT_VALUE="";
		// 添加观察者
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
				//把字符串转换为十六进制
				String str1="0x"+key.substring(0, 2);
				v_sendbufInfo[4]=(byte)HexStringtoInt(str1);
				String str2="0x"+key.substring(2,4);
				v_sendbufInfo[5]=(byte)HexStringtoInt(str2);
				int num=0;		
				//启动扫描当前数据线程
				reqDataThread=new ReceiveReuqestDataThread();
				reqDataThread.start();
				//超时线程
				timeOutThread=new TimeOutThread();
				timeOutThread.start();
				//循还执行
				while(!flag){
					num+=1;		
			    }		
			} 
			else 
			{
				CURRENT_VALUE = "-2";// 车辆不支持
			}

		} else {
			CURRENT_VALUE = "-1";// 通信失败
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

	// 命令字
	private byte[] m_commond = null;

	@Override
	public void BlueConnectLost(String name, String mac) {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"BlueConnectLost" + name);
		CURRENT_VALUE = "-1";// 通信失败
		flag=true;
	}

	@Override
	public void BlueConnected(String name, String mac) {
		// TODO Auto-generated method stub

	}

	/**
	 * 发送设置(得到)模式命令
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
	 * 发送复位模式命令
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
		byte[] param = OrderUtils.filterOutCmdParameters(dpuPackage);// 带长度字节
		String v_show = BluetoothDataService.bytesToHexString(databuf, datalen);
		if (D)
			Log.i(TAG, "SHOW：" + v_show);
		String info = "";
		// if (D) Log.i("param", "param" + Arrays.toString(param));
		// 设置模式
		String cmd_subcmdString = OrderUtils.bytesToHexStringNoBar(cmd_subcmd);
		if (cmd_subcmdString.equals("6109")) {
			if (param.length == 2 && param[0] != 0x06 && param[1] == 0x00) {
				this.setOrGetMode(new byte[] { 0x06 });
				next.set(true);
			} else {
				if (param.length == 2 && param[0] == 0x06 && param[1] == 0x00) {
					// 延迟三秒
					delayTimeThread = new DelayTimeThread(DT_CMD_MODE, 3000,
							null);
					delayTimeThread.start();
					delayTimeThread = null;
					next.set(true);
				}
			}
		}
		// 密码验证
		else if (cmd_subcmdString.equals("6110")) {
			if (param.length > 0 && param[0] == 0x00) {
				this.SafeCheckEnter(1, (byte) 0, null);
				next.set(true);
			} else {
				//通信失败
				initConsTantValue("-1",true);
			}
		}
		// 安全认证1
		else if (cmd_subcmdString.equals("6502")) {
			// 发送认证
			byte[] v_key = null;
			if (param.length > 0 && param[0] == 0x01) {
				v_key = new byte[] { param[1], param[2] };
				this.SafeCheckEnter(2, param[0], v_key);
				next.set(true);
//				this.InitialGGPInstance();

			} else {
				//通信失败
				initConsTantValue("-1",true);
			}
		}
		// 安全认证2
		else if (cmd_subcmdString.equals("6503")) {
			if (param.length > 0 && param[1] == 0x00) {
				if(isSysScan){				
					this.ReadSampleReportStep(2);
				//发送读取支持的系统列表命令
				}else{
					delayTimeThread = new DelayTimeThread(DT_APPOINT_ID_LIST,
							300, v_sendbufInfo);
					delayTimeThread.start();
					delayTimeThread = null;
					next.set(true);
				}
			} else {
				//通信失败
				initConsTantValue("-1",true);
			}

		} else if (cmd_subcmdString.equals("221C")) {
			if (D)
				Log.i(TAG, "显示主菜单");
			GetShowDialog(param, 0);
			next.set(true);
		}
		// 读取支持的系统列表
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
				//通信失败
				initConsTantValue("-1",true);
			}
		}
		// 读取指定系统数据流列表
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
						CURRENT_VALUE = "-2";// 系统不支持
						flag=true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//通信失败
				initConsTantValue("-1",true);
				isSysScan=false;
			}	

		}
		// 读取指定ID数据流
		else if (cmd_subcmdString.equals("6A11")) {
			if (param.length > 0) {
				try {
					GetSimpleAppointIDList(param, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				//通信失败
				initConsTantValue("-1",true);
			}
		}

	}

	// 获得对话框文本信息 CMD_SHOW_GETDIALOG
	public void GetShowDialog(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "文本对话框显示");
		Bundle bundle = new Bundle();// 用于传送数据
		int v_start = 0;
		// 插入对话框类型
		int v_style = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		bundle.putInt("DIALOG_STYLE", v_style); // 对话框类型
		// 插入对话框标题
		v_start += 2;
		if (v_style != 5) {
			//通信失败
			initConsTantValue("-1",true);
		}
	}

	@Override
	public void GetDataTimeout() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"接收超时，step=");
		CURRENT_VALUE = "-1";// 通信失败
		flag=true;
		
	}

	@Override
	public void BlueConnectClose() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG,"SHOW：关闭蓝牙" );
	}

	/* 接收请求数据线程 */

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
	/* 超时线程 */

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
	 * 迟时线程
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

	// 安全认证1: 25 02 01; 2: 25 03 01 带密码
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
	// 得到指定数据流PID数据
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
			// 对'0'->0，'a'->10
			if (c >= 'a' && c <= 'f') {
				x = c - 'a' + 10;
			} else if (c >= 'A' && c <= 'F') {
				x = c - 'A' + 10;
			} else if (c >= '0' && c <= '9') {
				x = c - '0';
			}
			iRet = (iRet << 4) | x;// n=n*4+x,移位拼接
		}
		return iRet;
	}

	// 删除String后面的00
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

	// 初始化ggp方法
//	public void InitialGGPInstance() {
//		m_ggppath = Env.getAppRootDirInSdcard().getAbsolutePath()
//				+ "/VEHICLES/TOYOTA/V46.82/TOYOTA_"
//				+ Env.GetCurrentLanguage().toUpperCase() + ".GGP";
//		SearchIdUtils search = SearchIdUtils.SearchIdInstance(m_ggppath);
//		searchIdUtils = SearchIdUtils.SearchIdInstance("");
//	}

	// 过滤DPU String
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
	// 读取支持的系统列表
	public void GetSimpleScanList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "读取一建扫描支持的系统列表");
		Bundle bundle = new Bundle();// 用于传送数据
		// 获取标题ID 没有标题0
		bundle.putString("DIALOG_TITLE", "");
		// 获取数据流条数 2
		v_sysIdList = new ArrayList<Byte>();
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		v_sysIdList.add(databuf[v_start]);
		v_sysIdList.add(databuf[v_start + 1]);
		// 获取文本
		v_start += 2;
		List<DiagnoseQuestionCategory> idTextList=new ArrayList<DiagnoseQuestionCategory>();
		DiagnoseQuestionCategory cateText=null;
		for (int i = 0; i < v_dtc_num; i++) {
			cateText=new DiagnoseQuestionCategory();
			// 系统子ID
			int v_id1 = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			cateText.setCategoryParentId(String.valueOf(v_id1));
			v_sysIdList.add(databuf[v_start]);
			v_sysIdList.add(databuf[v_start + 1]);
			v_sysIdList.add(databuf[v_start + 2]);
			v_sysIdList.add(databuf[v_start + 3]);

			v_start += 4; // 地址计数器累加
			// 文本ID
			int v_id2 = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			cateText.setCategoryParentTextID(String.valueOf(v_id2));
//			String text_id = searchIdUtils.getMessage(v_id2,
//					SearchIdUtils.ID_TEXT_LIB_FILE);
//			text_id = DelStringLast(text_id);
			v_start += 4; // 地址计数器累加
			idTextList.add(cateText);

		}
	}
	// 读取指定ID数据流
	public void GetSimpleAppointIDList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "读取指定ID数据流");
		// 获取数据流条数 2
		int v_start = 0;
		int v_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		v_start += 2;
		int v_id = 0;
		int only_Id = 0;
		for (int i = 0; i < v_num; i++) {
			// 数据流ID
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
			v_start += 4; // 地址计数器累加
			// 数据流计算结果
			v_id = (databuf[v_start] & 0xff) * 0x100
					+ (databuf[v_start + 1] & 0xff) + 2;
			byte[] v_reselt = new byte[v_id];
			// 防止系统内容溢出处理
			if (v_id > databuf.length - v_start)
				v_id = databuf.length - v_start;
			System.arraycopy(databuf, v_start, v_reselt, 0, v_id);
			String result = GetDpuStringSpecial(v_reselt);
			v_start += v_id; // 地址累加
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
	// 读取指定系统数据流列表
	public void GetSimpleAppointDataList(byte[] databuf, int datalen) {
		if (D)
			Log.i(TAG, "读取指定系统数据流列表");
		// 系统数量
		int v_start = 0;
		int v_dtc_num = (databuf[v_start] & 0xff) * 0x100
				+ (databuf[v_start + 1] & 0xff);
		// 获取数据流列表
		v_start += 2;
		int v_id = 0;
		unifDataStreamMap = new HashMap<String, String>();	
		for (int i = 0; i < v_dtc_num; i++) {
			
			// 系统ID
			v_id = (databuf[v_start] & 0xff) * 0x1000000
					+ (databuf[v_start + 1] & 0xff) * 0x10000
					+ (databuf[v_start + 2] & 0xff) * 0x100
					+ (databuf[v_start + 3] & 0xff);
			v_start += 4; // 地址计数器累加
			//数据流数量
			int num = (databuf[v_start] & 0xff) * 0x100
					+ (databuf[v_start + 1] & 0xff);
			v_start += 2;
			for (int j = 0; j < num; j++) {
				// 数据流列表
				v_id = (databuf[v_start] & 0xff) * 0x1000000
						+ (databuf[v_start + 1] & 0xff) * 0x10000
						+ (databuf[v_start + 2] & 0xff) * 0x100
						+ (databuf[v_start + 3] & 0xff);
				byte[] streamInfos = new byte[2];
				streamInfos[0]=databuf[v_start + 2];
				streamInfos[1]=databuf[v_start + 3];
				// 支持的统一数据流ID
				String unifStreamInfo= OrderUtils.bytesToHexStringNoBar(streamInfos);
//				String questionList = searchIdUtils.getMessage(v_id,
//						SearchIdUtils.ID_DATA_STREAM_LIB_FILE);
//				questionList = DelStringLast(questionList);
				unifDataStreamMap.put(unifStreamInfo, unifStreamInfo);
				v_start += 4; // 地址计数器累加

			}
		}
	}
	// 初始化DiagnoseSimpleReportInfo对象
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

	// 过滤数据流结果DPU String
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

	// 从数据流分离出单位，只显示数据流名字
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

	// 发送读取指定ID数据流
	public void SendSampleReportAppointCmd(int mode,byte[] para) {
		if (mode == 2) { // 读取指定系统数据流列表
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETDATASTREAMSELECT;
		} else if (mode == 3) {// 读取指定ID数据流
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_READDATESTREAM;
		} else
			return;
		m_blue_service.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
				m_commond, para, para.length, MAX_TIME);
	}

	// 发送简易报告指定命令
	public void SendSampleReportCmd(int step,byte[] para) {
		byte[] paradata = GetParaByte(para);
		this.SendSampleReportAppointCmd(step,paradata);

	}

	// 返回对应的字节数组
	public byte[] GetParaByte(byte[] para) {
		byte[] paradata = null;
		if (para == null) {
			paradata = new byte[] { (byte) 0xff, (byte) 0xff };
		} else {
			paradata = para;

		}
		return paradata;
	}

	// 从数据流分离出名字,只显示单位
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

	// 发送快速诊断命令
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

	// 验证密码
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

	// 组装DPUString
	public int AddDataToDpuString(byte[] source, int len, byte[] object,
			int objlen) {
		// 提供的缓冲区长度太短
		if (objlen < len + 3)
			return -1;
		// 添加到DPUString
		object[0] = (byte) (((len + 1) / 0x100) & 0xFF);
		object[1] = (byte) (((len + 1) % 0x100) & 0xFF);
		System.arraycopy(source, 0, object, 2, len);
		object[len + 2] = 0x00;
		return len + 3;
	}

	// 删除观察者
	public void DelObserver() {
		m_blue_service.DelObserver(this);
	}

	// 读取简易报告信息
	public int ReadSampleReportStep(int step) {
		if (step == 1) // 一键扫描系统支持的列表
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_SCAN;
		}
		else if (step == 2) // 获取支持的系统列表
		{
			m_commond[0] = CMD_SIMPLE;
			m_commond[1] = CMD_SIMPLE_GETLIST;
		}
		return m_blue_service
				.SendDataToBluetooth(BluetoothDataService.CMD_ReadMode,
						m_commond, null, 0, MAX_TIME);
	}
	public void initConsTantValue(String str,boolean bit){
		CURRENT_VALUE = str;// 通信失败
		flag=bit;
	}
	//关闭超时线程
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

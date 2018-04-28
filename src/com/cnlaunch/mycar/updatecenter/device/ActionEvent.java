package com.cnlaunch.mycar.updatecenter.device;
/**
 * 设备操作事件
 * @author luxingsong
 */
public class ActionEvent
{
	/**
	 * 操作码
	 */
	public final static int ACTION_CODE_START_UPDATE = 0; // 开始升级
	public final static int ACTION_CODE_CONNECT_DEVICE = 1; // 连接设备
	public final static int ACTION_CODE_CHECK_FILE_INTEGRETY = 2; // 文件完整性校验
	public final static int ACTION_CODE_CALC_FILE_MD5 = 3;   // md5 校验
	public final static int ACTION_CODE_DATA_TRANSFERING = 4;    // 数据传输
	public final static int ACTION_CODE_COMPARE_DATA_INTEGRETY = 5;// 数据完整性对比
	public final static int ACTION_CODE_PREPARE_FILE_INFO = 6;// 准备升级文件信息
	public final static int ACTION_CODE_FINISH_UPDATE = 7;// 完成升级
	public final static int ACTION_CODE_DECOMPRESS_FILES = 8;// 解压文件
	public final static int ACTION_CODE_SCANNING_FILES = 9;// 扫描文件
	public final static int ACTION_CODE_SPILT_INI_FILE = 10;// 截取 INI 文件
	
	/**
	 *  错误码
	 */
	public final static int ERROR_CONNECT_DEVICE = -10;// 连接设备出错
	public final static int ERROR_FILE_NOT_COMPLETE = -11; // 文件不完整
	public final static int ERROR_DEVICE_NO_REPLY = -12;  //设备没有回应
	public final static int ERROR_DATA_TRANSFER = -13;  // 数据传输错误
	public final static int ERROR_DATA_INTEGRETY = -14; // 数据完整性校验出错
	public final static int ERROR_DEVICE_EXCEPTION = -15; // 设备回应异常 23
	public final static int ERROR_REMOTE_OPERATION = -16;// 远程操作错误
	public final static int ERROR_FIRMWARE_BIN_FILE = -17;// 固件文件有问题
	public final static int ERROR_BOOT_VERSION_NUMBER = -18;// 固件版本有误
	public final static int ERROR_SWITCH_TO_BOOT_MODE = -19;// 切换Boot模式出错
	public final static int ERROR_FILE_INFO_FOR_DEVICE = -20;// 文件信息错误
	public final static int ERROR_FILE_POSITION_OPERATION = -21;// 文件操作位置错误
	public final static int ERROR_UPDATE_COMPLETE_INDICATION = -22;// 升级完成指示响应错误
	public final static int ERROR_UPDATE_SERIALS_NOT_SEEM = -23;//序列号不一致
	
}

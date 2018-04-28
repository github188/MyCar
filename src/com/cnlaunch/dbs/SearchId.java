package com.cnlaunch.dbs;

public class SearchId {

	/*
	 * PID参数计算接口
	 * short mitPid			输入Pid
	 * byte[] pDataBuffer 	从诊断接头接收到的数据缓冲区，需预作移位处理以指向待计算PID数据所在位置
	 * 输出：					字符串，未查到则返回NULL
	 */
	public native byte[] getResultWithCalc(short mltPid, byte[] pDataBuffer);
	
	/*
	 * 不再有效，请使用函数public native byte[] getTextFromLibReturnByte(int lineId, int iFileName);
	 * int lineId		输入文本ID
	 * int iFileName	输入文件名ID
	 * 输出： 			读取到的一行文本字符串，未查询到则输出NULL
	 */
	public native String getTextFromLib(int lineId, int iFileName);
	
	/*
	 * 根据文本ID及文件名查询字符串
	 * int lineId		输入文本ID
	 * int iFileName	输入文件名ID
	 * 可能用到的文件类型如下，如有疑问请联系，谢谢！
	 * #define ID_TEXT_LIB_FILE_NAME                1       菜单，对话框，动作测试按钮等使用
	 * #define ID_DATA_STREAMBOUNDS_LIB_FILE_NAME   2
	 * #define ID_DATA_STREAM_LIB_FILE_NAME         3       数据流选择，显示，动作测试数据流
	 * #define ID_DATA_STREAM_HELP_LIB_FILE_NAME    4       数据流帮助
	 * #define ID_DATA_STREAM_UNIT_LIB_FILE_NAME    5
	 * #define ID_TROUBLE_CODE_LIB_FILE_NAME        6       故障码
	 * #define ID_TROUBLE_CODE_STATUS_LIB_FILE_NAME 7       故障码状态
	 * #define ID_TROUBLE_CODE_HELP_LIB_FILE_NAME   8       故障码帮助
	 * #define ID_INFORMATION_FILE_NAME             9
	 * #define ID_SHOW_PROGRAM_HELP_FILE_NAME       10
	 * #define ID_LICENSE_FILE_NAME                 11
	 * #define ID_PICTURE_FILE_NAME                 12
	 * #define ID_BMP_FILE_PATH                     13
	 * 
	 * 输出：				读取到的一行文本字符串，未查询到则输出NULL
	 */
	public native byte[] getTextFromLibReturnByte(int lineId, int iFileName);
	
	
	/*
	 * 打开ggp文件，需根据文件名后缀确定语言
	 * String filename	文件名全路径
	 * 返回值：int
	 */
	public native int ggpOpen(String filename);
	
	
	/*
	 * 关闭ggp文件
	 * 返回值：	无
	 */
	public native void ggpClose();
	
}

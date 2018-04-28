package com.cnlaunch.mycar.diagnose.util;

import com.cnlaunch.mycar.diagnose.constant.DiagnoseCounter;

public class OrderUtils {
	// 0x55 0xAA 0xF0 0xF8
	// : <起始标志>+<目标地址>+<源地址>+<包长度>+<计数器>+<命令字>+<参数设置区>+<包校验>
	/*
	 * <起始标志>:0x55 0xAA <目标地址>:0xF0 <源地址>: 0xF8 <包长度>: ="<计数器>+<命令字>+<参数设置区>" :
	 * <计数器>: <命令字>: <参数设置区>: <包校验>:对“<目标地址>+<源地址>+<包长度>+<计数器>+<命令字>+<参数设置区>" :
	 */
	public static byte[] getSendOrder(byte[] parameterData, byte[] commandWord) {
		int parameterDataLength = 0;
		if (parameterData != null) {
			parameterDataLength = parameterData.length;
		}
		byte[] sendData = new byte[parameterDataLength + 10];
		sendData[0] = 0x55;
		sendData[1] = (byte) 0xaa;
		sendData[2] = (byte) 0xf0;
		sendData[3] = (byte) 0xf8;
		if (parameterData != null) {
			sendData[4] = (byte) ((parameterData.length + 3) / 256);
			sendData[5] = (byte) ((parameterData.length + 3) % 256);
		}
		sendData[6] = (byte) DiagnoseCounter.getCounter();
		if (commandWord != null && commandWord.length >= 2) {
			sendData[7] = commandWord[0];
			sendData[8] = commandWord[1];
		}
		for (int i = 0; i < parameterDataLength; i++) {
			sendData[i + 9] = parameterData[i];
		}
		// 校验码
		int s_n = 0;
		for (int i = 2; i < sendData.length - 1; i++) {
			s_n ^= sendData[i];
		}
		sendData[parameterDataLength + 9] = (byte) s_n;

		return sendData;
	}

	/**
	 * @author luxingsong
	 * @param commandWord
	 *            命令字字节数组
	 * @return 不带命令参数的数据包
	 */
	public static byte[] getSendOrderWithoutParameter(byte[] commandWord) {
		byte[] sendData = new byte[10];// 无命令参数的数据包固定长度 10字节
		sendData[0] = 0x55;
		sendData[1] = (byte) 0xaa;
		sendData[2] = (byte) 0xf0;
		sendData[3] = (byte) 0xf8;
		sendData[4] = (byte) 0x00;
		sendData[5] = (byte) 0x03;
		sendData[6] = (byte) DiagnoseCounter.getCounter();
		sendData[7] = commandWord[0];
		sendData[8] = commandWord[1];
		// 校验码
		byte xor_cs = 0;
		int from = 2;
		int to = sendData.length - 2;
		int calc_len = to - from + 1;
		for (int i = 0; i < calc_len; i++) {
			xor_cs ^= sendData[from++];
		}
		sendData[9] = (byte) (xor_cs);// 最后一个字节作为校验
		return sendData;
	}
	/**
	 * @author luxingsong
	 * @param data
	 *            数据包字节数组
	 * @param from
	 *            校验起始位置
	 * @param to
	 *            校验的结束位置
	 * @return XOR校验值
	 * */
	public static byte calcXorChechSum(byte[] data, int from, int to) {
		int chechsum = 0;
		if (from >= 0 && from < to && data != null && data.length > 0) {
			int calc_len = to - from + 1;
			for (int i = from; i < from + calc_len; i++) {
				chechsum ^= data[i];
			}
			return (byte) chechsum;
		}
		return (byte) 0xff;
	}

	public static byte[] leachReturnOrder(byte[] returnOrder) {
		if (returnOrder == null || returnOrder.length < 8) {
			return null;
		}
		int length = returnOrder.length - 8;
		byte[] order = new byte[length];
		for (int i = 0; i < length; i++) {
			order[i] = returnOrder[i + 7];
		}
		return order;
	}

	/**
	 * @author luxingsong 过滤出返回的完整数据包
	 * */
	final static byte[] NONE = {(byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff};
	public static byte[] filterReturnDataPackage(byte[] data) {
		int index = 0;
		if (data.length > 0) {
			if (isValidPackageHeader(data)) {
				// 数据包长度
				int low8 = (int) (data[5] & 0xFF);
				int high8 = (int) (data[4] & 0xFF);
				int len = ((high8 << 8) | low8);
				// System.out.println("high byte[4]"+data[4]);
				// System.out.println("low byte[5]"+data[5]);
				// System.out.println("<计数器>+<命令字>+<参数> = 长度："+len);
				int total_len = len + 7;// 总的数据包长度
				if (total_len < data.length)// 确保数组操作不会越界
				{
					byte[] Package = new byte[total_len];
					for (int i = 0; i < total_len; i++) {
						Package[i] = data[i];
					}
					return Package;
				}
			}
		}
		return data;// 放弃处理
	}

	/**
	 * 得到OBD2故障码控制字节
	 * */
	public static byte[] filterOBD2CmdParameters(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		int pkg_total_len = pkg.length;
		int param_start_pos = 10;// 参数起始位置
		int param_end_pos = pkg_total_len - 2; // 参数结束位置
		int param_total_len = param_end_pos - param_start_pos + 1; // 参数字节数
		byte[] parameters = null;
		if (param_total_len > 0) {
			parameters = new byte[param_total_len];
			for (int i = 0, index = param_start_pos; i < param_total_len;) {
				parameters[i++] = pkg[index++];
			}
		}
		// String paramStr = bytesToHexString(parameters);
		// String pkgStr = bytesToHexString(pkg);
		// // System.out.println("数据包长度:["+pkg_total_len+"字节]:"+pkgStr);
		// System.out.println("参数长度:["+param_total_len+"字节]:"+paramStr);
		return parameters;
	}

	/**
	 * @author luxingsong
	 * @param data
	 * @return 过滤出数据包中的命令字字节 包括主命令和子命令
	 * */
	public static byte[] filterOutCommand(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] command = new byte[2];
		command[0] = pkg[7];
		command[1] = pkg[8];
		// System.out.println("命令字:"+bytesToHexStringNoBar(command));
		return command;
	}

	/**
	 * @author luxingsong
	 * @param data
	 * @return 过滤出数据包中的命令字字节中的主命令
	 * */
	public static byte[] filterOutMainCommand(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] mainCmd = new byte[1];
		if (pkg.length >= 10) {// 数据包最小长度是10 字节
			mainCmd[0] = pkg[7];
		}
		// System.out.println("主命令字:"+bytesToHexStringNoBar(command));
		return mainCmd;
	}

	/**
	 * @author luxingsong
	 * @param data
	 * @return 过滤出数据包中的命令字字节中的子命令
	 * */
	public static byte[] filterOutSubCommand(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] subCmd = new byte[1];
		if (pkg.length >= 10) {// 数据包最小长度是10 字节
			subCmd[0] = pkg[8];
		}
		// System.out.println("子命令字:"+bytesToHexStringNoBar(command));
		return subCmd;
	}

	/**
	 * @author luxingsong 过滤出数据包中的命令字字节 cmd subcmd
	 * */
	public static byte[] filterOutCommandAndCommandParameters(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] command = new byte[3];
		command[0] = pkg[7];
		command[1] = pkg[8];
		command[2] = pkg[9];
		// System.out.println("命令字及名字参数:"+bytesToHexStringNoBar(command));
		return command;
	}

	/**
	 * @author luxingsong 过滤出数据包中的命令字及命令参数字节 cmd subcmd cmdparam
	 * */
	public static byte[] filterOutCommandWithParameter(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		int pkg_total_len = pkg.length;
		int param_start_pos = 7;// 主命令起始位置
		int param_end_pos = pkg_total_len - 2; // 参数结束位置
		int param_total_len = param_end_pos - param_start_pos + 1; // 参数字节数
		byte[] cmdWitchParameters = new byte[param_total_len];
		for (int i = 0, index = param_start_pos; i < param_total_len;) {
			cmdWitchParameters[i++] = pkg[index++];
		}
		// String paramStr = bytesToHexString(cmdWitchParameters);
		// String pkgStr = bytesToHexString(pkg);
		// System.out.println("数据包长度:["+pkg_total_len+"字节]:"+pkgStr);
		// System.out.println("参数长度:["+param_total_len+"字节]:"+paramStr);
		return cmdWitchParameters;
	}

	/**
	 * @author luxingsong 过滤出数据包的命令参数字节
	 * */
	public static byte[] filterOutCmdParameters(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		int pkg_total_len = pkg.length;
		int param_start_pos = 9;// 参数起始位置
		int param_end_pos = pkg_total_len - 2; // 参数结束位置
		int param_total_len = param_end_pos - param_start_pos + 1; // 参数字节数
		byte[] parameters = new byte[param_total_len];
		for (int i = 0, index = param_start_pos; i < param_total_len;) {
			parameters[i++] = pkg[index++];
		}
		// String paramStr = bytesToHexString(parameters);
		// String pkgStr = bytesToHexString(pkg);
		// System.out.println("数据包长度:["+pkg_total_len+"字节]:"+pkgStr);
		// System.out.println("参数长度:["+param_total_len+"字节]:"+paramStr);
		return parameters;
	}

	/**
	 * @author luxingsong 获取4字节校验值 --用于升级链路的校验操作
	 * @param data
	 *            数据缓冲区[蓝牙返回的数据包]
	 * */
	public static byte[] filterOut4BytesCheckSum(byte[] data) {
		// 过滤命令及命令的参数
		byte[] cmdAndCmdParam = filterOutCommandWithParameter(data);
		if (cmdAndCmdParam[0] == 0x65 && cmdAndCmdParam[1] == 0x02
				&& cmdAndCmdParam[2] == 0x02) {
			byte[] checksum = new byte[4];
			checksum[0] = cmdAndCmdParam[3];
			checksum[1] = cmdAndCmdParam[4];
			checksum[2] = cmdAndCmdParam[5];
			checksum[3] = cmdAndCmdParam[6];
			// String checkSumStr = bytesToHexString(checksum);
			// System.out.println("OrderUtil 4字节校验数据包长度:["+checksum.length+"字节]:"+checkSumStr);
			return checksum;
		}
		return null;
	}

	/**
	 * @author luxingsong 获取数据包的校验字节[最后一个字节]
	 * */
	public static byte filterOutPackageCheckSum(byte[] data) {
		if (isValidPackageHeader(data)) {
			return data[data.length - 2];
		}
		return (byte) 0xff;
	}

	/**
	 * @author luxingsong 过滤出数据包的长度
	 * */
	public static int filterOutPackageTotalLen(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		return pkg.length;
	}

	/**
	 * @author luxingsong 过滤出数据包的长度，根据协议: 长度只包括 <计数器>+<命令字>+<参数>字段
	 * */
	public static int filterOutPackageLen(byte[] data) {
		if (isValidPackageHeader(data)) {
			// 数据包长度
			int low8 = (int) (data[5] & 0xFF);
			int high8 = (int) (data[4] & 0xFF);
			int len = ((high8 << 8) | low8);
			return len;
		}
		return 0; // 无效的数据包不处理 返回0长度
	}

	/**
	 * @author luxingsong 数据包头是否有效
	 * */
	public static boolean isValidPackageHeader(byte[] data) {
		if (data[0] == 0x55 && data[1] == (byte) 0xaa) {
			return true;
		}
		return false;
	}

	/****
	 * @author luxingsong 把字节数组转换成16进制字符串
	 * @param bArray
	 *            字节数组
	 * @return String 转换成的字符串
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		int len = bArray.length;
		int last = len - 1;
		for (int i = 0; i < len; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append("0");
			sb.append(sTemp.toUpperCase());
			if (i != last)
				sb.append("-");
		}
		return sb.toString();
	}

	/**
	 * @author luxingsong 把字节数组转换成16进制字符串,不带分隔符
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexStringNoBar(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		int len = bArray.length;
		int last = len - 1;
		for (int i = 0; i < len; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append("0");
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * @author luxingsong 单字节转为十六进制字符串
	 * */
	public static final String oneByteToHexString(byte b) {
		String prefix = "0x";// 前缀
		StringBuffer sb = new StringBuffer(4);
		String sTemp;
		sTemp = Integer.toHexString(0xFF & b);
		if (sTemp.length() < 2)
			sb.append("0");
		sb.append(sTemp.toUpperCase());
		return prefix + sb.toString();
	}

	/**
	 * @author luxingsong
	 * @see 字节数组操作 System.arraycopy() 将data数组的数据附加到src数组之后
	 * */
	public static byte[] appendByteArray(byte[] src, byte[] data) {
		if (src.length > 0 && data.length > 0) {
			byte[] ret = new byte[src.length + data.length];
			System.arraycopy(src, 0, ret, 0, src.length);// copy source
			System.arraycopy(data, 0, ret, src.length, data.length);// copy data
			return ret;
		} else
			throw new IllegalArgumentException("字节数组参数错误");
	}

	public static int[] appendIntArray(int[] src, int[] data) {
		if (src.length > 0 && data.length > 0) {
			int[] ret = new int[src.length + data.length];
			System.arraycopy(src, 0, ret, 0, src.length);// copy source
			System.arraycopy(data, 0, ret, src.length, data.length);// copy data
			return ret;
		} else
			throw new IllegalArgumentException("字节数组参数错误");
	}

	public static int bytesToInt(byte[] data) {
		int len = data.length;
		int ret = 0;
		for (int i = 0; i < len; i++) {
			ret |= data[len - i] << (8 * i);
		}
		return ret;
	}
}// end of class
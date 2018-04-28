package com.cnlaunch.mycar.diagnose.util;

import com.cnlaunch.mycar.diagnose.constant.DiagnoseCounter;

public class OrderUtils {
	// 0x55 0xAA 0xF0 0xF8
	// : <��ʼ��־>+<Ŀ���ַ>+<Դ��ַ>+<������>+<������>+<������>+<����������>+<��У��>
	/*
	 * <��ʼ��־>:0x55 0xAA <Ŀ���ַ>:0xF0 <Դ��ַ>: 0xF8 <������>: ="<������>+<������>+<����������>" :
	 * <������>: <������>: <����������>: <��У��>:�ԡ�<Ŀ���ַ>+<Դ��ַ>+<������>+<������>+<������>+<����������>" :
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
		// У����
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
	 *            �������ֽ�����
	 * @return ����������������ݰ�
	 */
	public static byte[] getSendOrderWithoutParameter(byte[] commandWord) {
		byte[] sendData = new byte[10];// ��������������ݰ��̶����� 10�ֽ�
		sendData[0] = 0x55;
		sendData[1] = (byte) 0xaa;
		sendData[2] = (byte) 0xf0;
		sendData[3] = (byte) 0xf8;
		sendData[4] = (byte) 0x00;
		sendData[5] = (byte) 0x03;
		sendData[6] = (byte) DiagnoseCounter.getCounter();
		sendData[7] = commandWord[0];
		sendData[8] = commandWord[1];
		// У����
		byte xor_cs = 0;
		int from = 2;
		int to = sendData.length - 2;
		int calc_len = to - from + 1;
		for (int i = 0; i < calc_len; i++) {
			xor_cs ^= sendData[from++];
		}
		sendData[9] = (byte) (xor_cs);// ���һ���ֽ���ΪУ��
		return sendData;
	}
	/**
	 * @author luxingsong
	 * @param data
	 *            ���ݰ��ֽ�����
	 * @param from
	 *            У����ʼλ��
	 * @param to
	 *            У��Ľ���λ��
	 * @return XORУ��ֵ
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
	 * @author luxingsong ���˳����ص��������ݰ�
	 * */
	final static byte[] NONE = {(byte) 0xff, (byte) 0xff, (byte) 0xff,
			(byte) 0xff};
	public static byte[] filterReturnDataPackage(byte[] data) {
		int index = 0;
		if (data.length > 0) {
			if (isValidPackageHeader(data)) {
				// ���ݰ�����
				int low8 = (int) (data[5] & 0xFF);
				int high8 = (int) (data[4] & 0xFF);
				int len = ((high8 << 8) | low8);
				// System.out.println("high byte[4]"+data[4]);
				// System.out.println("low byte[5]"+data[5]);
				// System.out.println("<������>+<������>+<����> = ���ȣ�"+len);
				int total_len = len + 7;// �ܵ����ݰ�����
				if (total_len < data.length)// ȷ�������������Խ��
				{
					byte[] Package = new byte[total_len];
					for (int i = 0; i < total_len; i++) {
						Package[i] = data[i];
					}
					return Package;
				}
			}
		}
		return data;// ��������
	}

	/**
	 * �õ�OBD2����������ֽ�
	 * */
	public static byte[] filterOBD2CmdParameters(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		int pkg_total_len = pkg.length;
		int param_start_pos = 10;// ������ʼλ��
		int param_end_pos = pkg_total_len - 2; // ��������λ��
		int param_total_len = param_end_pos - param_start_pos + 1; // �����ֽ���
		byte[] parameters = null;
		if (param_total_len > 0) {
			parameters = new byte[param_total_len];
			for (int i = 0, index = param_start_pos; i < param_total_len;) {
				parameters[i++] = pkg[index++];
			}
		}
		// String paramStr = bytesToHexString(parameters);
		// String pkgStr = bytesToHexString(pkg);
		// // System.out.println("���ݰ�����:["+pkg_total_len+"�ֽ�]:"+pkgStr);
		// System.out.println("��������:["+param_total_len+"�ֽ�]:"+paramStr);
		return parameters;
	}

	/**
	 * @author luxingsong
	 * @param data
	 * @return ���˳����ݰ��е��������ֽ� �����������������
	 * */
	public static byte[] filterOutCommand(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] command = new byte[2];
		command[0] = pkg[7];
		command[1] = pkg[8];
		// System.out.println("������:"+bytesToHexStringNoBar(command));
		return command;
	}

	/**
	 * @author luxingsong
	 * @param data
	 * @return ���˳����ݰ��е��������ֽ��е�������
	 * */
	public static byte[] filterOutMainCommand(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] mainCmd = new byte[1];
		if (pkg.length >= 10) {// ���ݰ���С������10 �ֽ�
			mainCmd[0] = pkg[7];
		}
		// System.out.println("��������:"+bytesToHexStringNoBar(command));
		return mainCmd;
	}

	/**
	 * @author luxingsong
	 * @param data
	 * @return ���˳����ݰ��е��������ֽ��е�������
	 * */
	public static byte[] filterOutSubCommand(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] subCmd = new byte[1];
		if (pkg.length >= 10) {// ���ݰ���С������10 �ֽ�
			subCmd[0] = pkg[8];
		}
		// System.out.println("��������:"+bytesToHexStringNoBar(command));
		return subCmd;
	}

	/**
	 * @author luxingsong ���˳����ݰ��е��������ֽ� cmd subcmd
	 * */
	public static byte[] filterOutCommandAndCommandParameters(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		byte[] command = new byte[3];
		command[0] = pkg[7];
		command[1] = pkg[8];
		command[2] = pkg[9];
		// System.out.println("�����ּ����ֲ���:"+bytesToHexStringNoBar(command));
		return command;
	}

	/**
	 * @author luxingsong ���˳����ݰ��е������ּ���������ֽ� cmd subcmd cmdparam
	 * */
	public static byte[] filterOutCommandWithParameter(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		int pkg_total_len = pkg.length;
		int param_start_pos = 7;// ��������ʼλ��
		int param_end_pos = pkg_total_len - 2; // ��������λ��
		int param_total_len = param_end_pos - param_start_pos + 1; // �����ֽ���
		byte[] cmdWitchParameters = new byte[param_total_len];
		for (int i = 0, index = param_start_pos; i < param_total_len;) {
			cmdWitchParameters[i++] = pkg[index++];
		}
		// String paramStr = bytesToHexString(cmdWitchParameters);
		// String pkgStr = bytesToHexString(pkg);
		// System.out.println("���ݰ�����:["+pkg_total_len+"�ֽ�]:"+pkgStr);
		// System.out.println("��������:["+param_total_len+"�ֽ�]:"+paramStr);
		return cmdWitchParameters;
	}

	/**
	 * @author luxingsong ���˳����ݰ�����������ֽ�
	 * */
	public static byte[] filterOutCmdParameters(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		int pkg_total_len = pkg.length;
		int param_start_pos = 9;// ������ʼλ��
		int param_end_pos = pkg_total_len - 2; // ��������λ��
		int param_total_len = param_end_pos - param_start_pos + 1; // �����ֽ���
		byte[] parameters = new byte[param_total_len];
		for (int i = 0, index = param_start_pos; i < param_total_len;) {
			parameters[i++] = pkg[index++];
		}
		// String paramStr = bytesToHexString(parameters);
		// String pkgStr = bytesToHexString(pkg);
		// System.out.println("���ݰ�����:["+pkg_total_len+"�ֽ�]:"+pkgStr);
		// System.out.println("��������:["+param_total_len+"�ֽ�]:"+paramStr);
		return parameters;
	}

	/**
	 * @author luxingsong ��ȡ4�ֽ�У��ֵ --����������·��У�����
	 * @param data
	 *            ���ݻ�����[�������ص����ݰ�]
	 * */
	public static byte[] filterOut4BytesCheckSum(byte[] data) {
		// �����������Ĳ���
		byte[] cmdAndCmdParam = filterOutCommandWithParameter(data);
		if (cmdAndCmdParam[0] == 0x65 && cmdAndCmdParam[1] == 0x02
				&& cmdAndCmdParam[2] == 0x02) {
			byte[] checksum = new byte[4];
			checksum[0] = cmdAndCmdParam[3];
			checksum[1] = cmdAndCmdParam[4];
			checksum[2] = cmdAndCmdParam[5];
			checksum[3] = cmdAndCmdParam[6];
			// String checkSumStr = bytesToHexString(checksum);
			// System.out.println("OrderUtil 4�ֽ�У�����ݰ�����:["+checksum.length+"�ֽ�]:"+checkSumStr);
			return checksum;
		}
		return null;
	}

	/**
	 * @author luxingsong ��ȡ���ݰ���У���ֽ�[���һ���ֽ�]
	 * */
	public static byte filterOutPackageCheckSum(byte[] data) {
		if (isValidPackageHeader(data)) {
			return data[data.length - 2];
		}
		return (byte) 0xff;
	}

	/**
	 * @author luxingsong ���˳����ݰ��ĳ���
	 * */
	public static int filterOutPackageTotalLen(byte[] data) {
		byte[] pkg = filterReturnDataPackage(data);
		return pkg.length;
	}

	/**
	 * @author luxingsong ���˳����ݰ��ĳ��ȣ�����Э��: ����ֻ���� <������>+<������>+<����>�ֶ�
	 * */
	public static int filterOutPackageLen(byte[] data) {
		if (isValidPackageHeader(data)) {
			// ���ݰ�����
			int low8 = (int) (data[5] & 0xFF);
			int high8 = (int) (data[4] & 0xFF);
			int len = ((high8 << 8) | low8);
			return len;
		}
		return 0; // ��Ч�����ݰ������� ����0����
	}

	/**
	 * @author luxingsong ���ݰ�ͷ�Ƿ���Ч
	 * */
	public static boolean isValidPackageHeader(byte[] data) {
		if (data[0] == 0x55 && data[1] == (byte) 0xaa) {
			return true;
		}
		return false;
	}

	/****
	 * @author luxingsong ���ֽ�����ת����16�����ַ���
	 * @param bArray
	 *            �ֽ�����
	 * @return String ת���ɵ��ַ���
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
	 * @author luxingsong ���ֽ�����ת����16�����ַ���,�����ָ���
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
	 * @author luxingsong ���ֽ�תΪʮ�������ַ���
	 * */
	public static final String oneByteToHexString(byte b) {
		String prefix = "0x";// ǰ׺
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
	 * @see �ֽ�������� System.arraycopy() ��data��������ݸ��ӵ�src����֮��
	 * */
	public static byte[] appendByteArray(byte[] src, byte[] data) {
		if (src.length > 0 && data.length > 0) {
			byte[] ret = new byte[src.length + data.length];
			System.arraycopy(src, 0, ret, 0, src.length);// copy source
			System.arraycopy(data, 0, ret, src.length, data.length);// copy data
			return ret;
		} else
			throw new IllegalArgumentException("�ֽ������������");
	}

	public static int[] appendIntArray(int[] src, int[] data) {
		if (src.length > 0 && data.length > 0) {
			int[] ret = new int[src.length + data.length];
			System.arraycopy(src, 0, ret, 0, src.length);// copy source
			System.arraycopy(data, 0, ret, src.length, data.length);// copy data
			return ret;
		} else
			throw new IllegalArgumentException("�ֽ������������");
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
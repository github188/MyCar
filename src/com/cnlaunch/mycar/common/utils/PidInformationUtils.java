package com.cnlaunch.mycar.common.utils;

import launch.SearchIdUtils;
import android.content.Context;

public class PidInformationUtils {

	// �õ����������ֵ
	public static int getPidMax(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return 0;
	}

	// �õ���������Сֵ
	public static int getPidMin(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return 0;
	}

	// �õ���������λ
	public static String getPidUnit(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return null;
	}

	// �õ�����������
	public static String getPidName(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return null;
	}

	// �õ����������
	public static String getPidSimpleName(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return null;
	}

	// �õ�����������Ϣ
	public static String getPidMessage(int pid, Context context) {
		SearchIdUtils idUtils = SearchIdUtils.SearchIdInstance("");
		String pidMessage = idUtils.getMessage(0x00000000 + pid * 0x100, 3);
		return pidMessage;
	}
}

package com.cnlaunch.mycar.common.utils;

import launch.SearchIdUtils;
import android.content.Context;

public class PidInformationUtils {

	// 得到数据流最大值
	public static int getPidMax(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return 0;
	}

	// 得到数据流最小值
	public static int getPidMin(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return 0;
	}

	// 得到数据流单位
	public static String getPidUnit(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return null;
	}

	// 得到数据流名称
	public static String getPidName(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return null;
	}

	// 得到数据流简称
	public static String getPidSimpleName(int pid, Context context) {
		String pidMessage = getPidMessage(pid, context);
		return null;
	}

	// 得到数据流总信息
	public static String getPidMessage(int pid, Context context) {
		SearchIdUtils idUtils = SearchIdUtils.SearchIdInstance("");
		String pidMessage = idUtils.getMessage(0x00000000 + pid * 0x100, 3);
		return pidMessage;
	}
}

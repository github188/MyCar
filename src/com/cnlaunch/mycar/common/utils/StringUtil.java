package com.cnlaunch.mycar.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cnlaunch.mycar.R;

import android.content.Context;

public class StringUtil {
	// 过滤空格,换行,制表符
	public static String filterBlank(String str) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}
	
	public static String getCurrency(Context context){
		return context.getString(R.string.manager_currency);
	}

}

package com.cnlaunch.mycar.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * <功能简述>SharedPreferences操作公共类
 * <功能详细描述>
 * @author huangweiyong
 * @version 1.0 2012-5-14
 * @since DBS V100
 */
public class SharedPreferencesHelper {
	private SharedPreferences preferences;
	private Editor editor;
	private final static int MODE =Context.MODE_PRIVATE;

	public SharedPreferencesHelper(Context context, String Preferences_NAME) {
		super();
		preferences = context.getSharedPreferences(Preferences_NAME, MODE);
	}

	public boolean save(String key, String value) {
		editor = preferences.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	public String read(String key) {
		String str = null;
		str = preferences.getString(key, null);
		return str;
	}
}

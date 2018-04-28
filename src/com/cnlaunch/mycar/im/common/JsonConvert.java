package com.cnlaunch.mycar.im.common;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonConvert {
	private static Gson mGson = new Gson();

	public static String toJson(Object obj) {
		return mGson.toJson(obj);
	}

	public static <T> T fromJson(String jsonStr, Class<T> clazz) {
		try {
			return mGson.fromJson(jsonStr, clazz);
		} catch (IllegalStateException e) {
			Log.e("JsonConvert", clazz.getName() + " <- " + jsonStr);
			Log.e("JsonConvert", "´íÎó  -> " + e.toString());
			return null;
		} catch(JsonSyntaxException e){
			Log.e("JsonConvert", clazz.getName() + " <- " + jsonStr);
			Log.e("JsonConvert", "´íÎó  -> " + e.toString());
			return null;
		}
	}
}

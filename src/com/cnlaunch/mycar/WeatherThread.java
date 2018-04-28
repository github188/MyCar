package com.cnlaunch.mycar;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.cnlaunch.mycar.common.config.Constants;

public class WeatherThread extends Thread {
	static final int MSG_WEATHER_OK = 0;
	static final int MSG_WEATHER_ERR = 1;
	private static final String PREFERENCE_NAME = "com.cnlaunch.mycar.weather";
	private static final String PREFERENCE_ITEM_WEATHER_CITY_NAME = "city_name";

	Context mContext;

	public WeatherThread(Context context) {
		mContext = context;
	}

	public WeatherThread(Context context, String cityName) {
		mContext = context;
		setCityFromSharedPreferences(cityName);
	}

	@Override
	public void run() {
		// TODO
		try {
			String weatherData = getWeatherData();
			if (weatherData != null) {

				Intent intent = new Intent();
				intent.putExtra("weatherData", weatherData);
				intent.setAction(Constants.MAIN_TITLE_ACTION_WEATHER);
				mContext.sendBroadcast(intent);
				return;
			} else {
				// 异常情况，统一在run末尾返回，weatherData置为空字符串
			}
		} catch (ClientProtocolException e) {
			// 异常情况，统一在run末尾返回，weatherData置为空字符串
		} catch (IOException e) {
			// 异常情况，统一在run末尾返回，weatherData置为空字符串
		}
		Intent intent = new Intent();
		intent.putExtra("weatherData", "");
		intent.setAction(Constants.MAIN_TITLE_ACTION_WEATHER);
		mContext.sendBroadcast(intent);
	}

	private String getWeatherData() throws ClientProtocolException, IOException {
		String uri = Constants.SERVICE_WEATHER_URL
				+ "?MethodName=getweatherbycityname&cityname=" + GetCityName();

		//Log.e("test", "天气预报--->"+uri);
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(uri);
		HttpResponse httpResponse = defaultHttpClient.execute(httppost);
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			//Log.e("test", "天气预报--->"+strResult);
			return strResult;
		}
		//Log.e("test", "NULL");
		return null;
	}

	private String GetCityName() {
		return getCityFromSharedPreferences();
	}

	private String getCityFromSharedPreferences() {
		SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(PREFERENCE_ITEM_WEATHER_CITY_NAME, "深圳");
	}

	private void setCityFromSharedPreferences(String cityName) {
		SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(PREFERENCE_ITEM_WEATHER_CITY_NAME, cityName);
		editor.commit();
	}
}

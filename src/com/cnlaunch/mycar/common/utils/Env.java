package com.cnlaunch.mycar.common.utils;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.common.config.Constants;

public class Env {
	static PowerManager.WakeLock mWakeLock;   //用着防止屏幕黑屏

	// 检没网络是否可用
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 一般情况下会返回"/mnt/sdcard/cnlaunch/dbsCar"，
	 * 需要调用exists()检查各级文件夹是否都存在，否则需调用mkdirs()创建各级目录
	 * @return 本应用程序在sdCard上的根目录
	 */
	public static File getAppRootDirInSdcard() {
		return new File(android.os.Environment.getExternalStorageDirectory(),
				File.separator + Constants.ROOT_DIR);
	}
	

	// 检测SD卡否可用
	public static boolean isSDCardAvailable(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	// 获取屏幕宽度
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	// 获取屏幕高度
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int screenHeight = dm.heightPixels;
		return screenHeight;
	}

	// SIM序列号
	public static String getSimSerialNumber(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSimSerialNumber();
		} catch (Exception e) {
			return "";
		}
	}

	// IMEI号
	public static String getDeviceId(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getDeviceId();
		} catch (Exception e) {
			return "";
		}
	}

	public static String getNetworkOperator(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getNetworkOperator();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isGpsLocationProviderEnable(Context context) {
		String str = android.provider.Settings.Secure.getString(
				context.getContentResolver(),
				android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (str != null && str.contains(LocationManager.GPS_PROVIDER)) {
			return true;
		}
		return false;
	}

	public static String SDPATH() {
		String Sdpath = "";
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			Sdpath = sdDir.toString();
		}
		return Sdpath;
	}
	//屏蔽屏幕锁屏
	public static void acquireWakeLock(Activity activity) {
		PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
		if (mWakeLock != null) {
		   mWakeLock.release();
		   mWakeLock = null;
		  }
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
		    "com.cnlaunch.mycar.obd2.DataFlowMain");
		  mWakeLock.acquire();
		}
	//注销锁屏
	public static void releaseWakeLock()
	{
		if (mWakeLock != null) {
			   mWakeLock.release();
			   mWakeLock = null;
			  }
	}
	//获取当前系统语言
	public static String GetCurrentLanguage()
	{
		String lang = null;
		String v_lang = Locale.getDefault().getLanguage();
		if(v_lang.equals("zh"))
		{
			lang = "CN";
		}
		else
		{
			lang = "EN";
		}
		return lang;
	}
	//活动当前系统语言编码
	public static String GetCurrentEncode()
	{
		String encode = null;
		String v_code = Locale.getDefault().getLanguage();
		if(v_code.equals("zh"))
		{
			encode = "GB2312";
		}
		else
		{
			encode = "GB2312";
		}
		return encode;
	}
	

}

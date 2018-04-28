package com.cnlaunch.mycar.im.net;

import java.net.URL;
import java.util.HashMap;

import android.content.Context;

public class NetRequestThread {
	URL mRequestUrl;
	HashMap<String, String> mRequestParaMap;
	Context mContext;
	OnResult mOnResult;

	public NetRequestThread(Context context, URL requestUrl,
			HashMap<String, String> requestParaMap, OnResult onResult) {
		mContext = context;
		mRequestParaMap = requestParaMap;
		mOnResult = onResult;
		mRequestUrl = requestUrl;
	}

	public static interface OnResult {
		public void onSucc(String result);
		public void onFailed(String result);
	}
}

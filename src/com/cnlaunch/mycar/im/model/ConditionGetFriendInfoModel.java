package com.cnlaunch.mycar.im.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ConditionGetFriendInfoModel {
	private String appID = null;
	private String sign = null;
	private String sourceUserUID = null;
	private String askForUserUID = null;

	public List<NameValuePair> getNameValuePairList() {
		List<NameValuePair> list = new ArrayList<NameValuePair>();

		list.add(new BasicNameValuePair("AppID", appID));
		list.add(new BasicNameValuePair("Sign", sign));
		if (sourceUserUID != null) {
			list.add(new BasicNameValuePair("sourceUserUID", sourceUserUID));
		}
		if (askForUserUID != null) {
			list.add(new BasicNameValuePair("askForUserUID", askForUserUID));
		}

		return list;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSourceUserUID() {
		return sourceUserUID;
	}

	public void setSourceUserUID(String sourceUserUID) {
		this.sourceUserUID = sourceUserUID;
	}

	public String getAskForUserUID() {
		return askForUserUID;
	}

	public void setAskForUserUID(String askForUserUID) {
		this.askForUserUID = askForUserUID;
	}

}

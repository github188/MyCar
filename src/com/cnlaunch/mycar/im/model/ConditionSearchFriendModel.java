package com.cnlaunch.mycar.im.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ConditionSearchFriendModel {
	private String appID = null;
	private String sign = null;
	private String ccNo = null;
	private String nickName = null;
	private String ageSectMin = null;
	private String ageSectMax = null;
	private String sex = null; // ¡°0¡±ÊÇÄÐ£¬¡°1¡±ÊÇÅ®
	private String pageSize = "100";
	private String targetPage = "1";

	public final String SEX_MAN = "0";
	public final String SEX_WOMAN = "1";

	public List<NameValuePair> getNameValuePairList() {
		List<NameValuePair> list = new ArrayList<NameValuePair>();

		list.add(new BasicNameValuePair("AppID", appID));
		list.add(new BasicNameValuePair("Sign", sign));
		if (ccNo != null) {
			list.add(new BasicNameValuePair("ccNo", ccNo));
		}
		if (nickName != null) {
			list.add(new BasicNameValuePair("nickName", nickName));
		}
		if (ageSectMin != null) {
			list.add(new BasicNameValuePair("ageSectMin", ageSectMin));
		}
		if (sex != null) {
			list.add(new BasicNameValuePair("sex", sex));
		}
		if (pageSize != null) {
			list.add(new BasicNameValuePair("pageSize", pageSize));
		}
		if (targetPage != null) {
			list.add(new BasicNameValuePair("targetPage", targetPage));
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

	public String getCcNo() {
		return ccNo;
	}

	public void setCcNo(String ccNo) {
		this.ccNo = ccNo;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAgeSectMin() {
		return ageSectMin;
	}

	public void setAgeSectMin(String ageSectMin) {
		this.ageSectMin = ageSectMin;
	}

	public String getAgeSectMax() {
		return ageSectMax;
	}

	public void setAgeSectMax(String ageSectMax) {
		this.ageSectMax = ageSectMax;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

}

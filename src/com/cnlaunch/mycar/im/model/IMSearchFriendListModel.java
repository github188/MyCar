package com.cnlaunch.mycar.im.model;

public class IMSearchFriendListModel {
	private String UserUID; // 用户UID
	private String CCNo; // 用户CC号
	private String NickName; // 用户昵称
	private int Old; // 年龄
	private int Sex; // 性别
	private int FaceID; //用户头像
	
	public String getUserUID() {
		return UserUID;
	}
	public void setUserUID(String userUID) {
		UserUID = userUID;
	}
	public String getCCNo() {
		return CCNo;
	}
	public void setCCNo(String cCNo) {
		CCNo = cCNo;
	}
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public int getOld() {
		return Old;
	}
	public void setOld(int old) {
		Old = old;
	}
	public int getSex() {
		return Sex;
	}
	public void setSex(int sex) {
		Sex = sex;
	}
	public int getFaceID() {
		return FaceID;
	}
	public void setFaceID(int faceID) {
		FaceID = faceID;
	}
	
	
	
}

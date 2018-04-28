package com.cnlaunch.mycar.im.model;

public class IMMyFriendComModel {
	private String IMMyFriendRelationID;
	private String ParentFriendUID;
	private String GroupUID;
	private String UserUID;
	private String GroupName;
	private String NiceName;// NiceName
	private String UserSign; // UserSign
	private int FaceID; // FaceID
	private String TaskID;
	private String CCNo;
	private String NameRemark;
	private int OnlineStatus = 0; // 0.代表未在线

	public static final int ONLINE_STATE_ON = 1;
	public static final int ONLINE_STATE_OFF = 0;

	public String getIMMyFriendRelationID() {
		return IMMyFriendRelationID;
	}

	public void setIMMyFriendRelationID(String iMMyFriendRelationID) {
		IMMyFriendRelationID = iMMyFriendRelationID;
	}

	public String getParentFriendUID() {
		return ParentFriendUID;
	}

	public void setParentFriendUID(String parentFriendUID) {
		ParentFriendUID = parentFriendUID;
	}

	public String getGroupUID() {
		return GroupUID;
	}

	public void setGroupUID(String groupUID) {
		GroupUID = groupUID;
	}

	public String getUserUID() {
		return UserUID;
	}

	public void setUserUID(String userUID) {
		UserUID = userUID;
	}

	public String getGroupName() {
		return GroupName;
	}

	public void setGroupName(String groupName) {
		GroupName = groupName;
	}

	public String getNiceName() {
		return NiceName;
	}

	public void setNiceName(String niceName) {
		NiceName = niceName;
	}

	public String getNickName() {
		return NiceName;
	}

	public void setNickName(String nickName) {
		NiceName = nickName;
	}

	public String getUserSign() {
		return UserSign;
	}

	public void setUserSign(String userSign) {
		UserSign = userSign;
	}

	public int getFaceID() {
		return FaceID;
	}

	public void setFaceID(int faceID) {
		FaceID = faceID;
	}

	public String getTaskID() {
		return TaskID;
	}

	public void setTaskID(String taskID) {
		TaskID = taskID;
	}

	public String getCCNo() {
		return CCNo;
	}

	public void setCCNo(String cCNo) {
		CCNo = cCNo;
	}

	public String getNameRemark() {
		return NameRemark;
	}

	public void setNameRemark(String nameRemark) {
		NameRemark = nameRemark;
	}

	public int getOnlineStatus() {
		return OnlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		OnlineStatus = onlineStatus;
	}

}

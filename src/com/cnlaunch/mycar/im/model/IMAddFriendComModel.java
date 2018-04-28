package com.cnlaunch.mycar.im.model;

public class IMAddFriendComModel {
	private String TaskID;
	private IMMyFriendComModel SourceUserComModel;
	private IMMyFriendComModel DestUserComModel;
	private int AddFriendConfigType;// 0.未知 1.自动添加,2.被添加人审核 3.自动拒绝
	private int ConfirmType; // 1.同意添加, 2.拒绝添加(仅当_addfriendconfigtype=2时，使用)
	private String ConfirmContent; // 审核文本信息(仅当_addfriendconfigtype=2时，使用)
	private int Status; // 1.发出添加邀请 2.等待被添加者审核, 3.添加成功,4.添加失败
	
	public static final int STATUS_REQUEST = 1;
	public static final int STATUS_WAIT_FOR_RESPONSE = 2;
	public static final int STATUS_RESPONSE_SUCC = 3;
	public static final int STATUS_RESPONSE_FAIL = 4;
	
	
	public String getTaskID() {
		return TaskID;
	}
	public void setTaskID(String taskID) {
		TaskID = taskID;
	}
	public IMMyFriendComModel getSourceUserComModel() {
		return SourceUserComModel;
	}
	public void setSourceUserComModel(IMMyFriendComModel sourceUserComModel) {
		SourceUserComModel = sourceUserComModel;
	}
	public IMMyFriendComModel getDestUserComModel() {
		return DestUserComModel;
	}
	public void setDestUserComModel(IMMyFriendComModel destUserComModel) {
		DestUserComModel = destUserComModel;
	}
	public int getAddFriendConfigType() {
		return AddFriendConfigType;
	}
	public void setAddFriendConfigType(int addFriendConfigType) {
		AddFriendConfigType = addFriendConfigType;
	}
	public int getConfirmType() {
		return ConfirmType;
	}
	public void setConfirmType(int confirmType) {
		ConfirmType = confirmType;
	}
	public String getConfirmContent() {
		return ConfirmContent;
	}
	public void setConfirmContent(String confirmContent) {
		ConfirmContent = confirmContent;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}

	
	
}
